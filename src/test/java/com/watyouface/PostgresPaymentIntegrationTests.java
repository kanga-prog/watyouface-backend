package com.watyouface;

import com.watyouface.entity.Listing;
import com.watyouface.entity.Transaction;
import com.watyouface.entity.User;
import com.watyouface.entity.Wallet;
import com.watyouface.entity.enums.ListingStatus;
import com.watyouface.entity.enums.Role;
import com.watyouface.entity.enums.TransactionStatus;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.TransactionRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.WalletRepository;
import com.watyouface.service.MarketplaceService;
import com.watyouface.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.Mockito;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * Real PostgreSQL tests. Enable by exporting DB_URL, DB_USERNAME, DB_PASSWORD and JWT_SECRET.
 * Uses uniquely named fixtures and removes only its own rows.
 */
@SpringBootTest
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = "jdbc:postgresql:.*")
@EnabledIfEnvironmentVariable(named = "DB_USERNAME", matches = ".+")
// An empty password is valid for the documented local PostgreSQL trust setup.
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".*")
@EnabledIfEnvironmentVariable(named = "JWT_SECRET", matches = ".{32,}")
class PostgresPaymentIntegrationTests {

    @Autowired private UserRepository users;
    @Autowired private ListingRepository listings;
    @Autowired private WalletRepository wallets;
    @Autowired private TransactionRepository transactions;
    @Autowired private MarketplaceService marketplace;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManager entityManager;
    @SpyBean private TransactionService transactionService;

    private User buyer;
    private User seller;
    private Listing listing;

    @BeforeEach
    void createIsolatedFixture() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        buyer = users.saveAndFlush(user("pg-buyer-" + suffix, "pg-buyer-" + suffix + "@example.test"));
        seller = users.saveAndFlush(user("pg-seller-" + suffix, "pg-seller-" + suffix + "@example.test"));
        listing = new Listing();
        listing.setTitle("PostgreSQL demo fixture");
        listing.setPrice(30.0);
        listing.setSeller(seller);
        listing.setBuyer(buyer);
        listing.setStatus(ListingStatus.ACCEPTED);
        listing = listings.saveAndFlush(listing);
        wallets.saveAndFlush(wallet(buyer, 100.0));
        wallets.saveAndFlush(wallet(seller, 0.0));
    }

    @AfterEach
    void deleteOnlyOwnFixture() {
        if (listing == null || buyer == null || seller == null) return;
        jdbc.update("DELETE FROM \"transaction\" WHERE listing_id = ?", listing.getId());
        jdbc.update("DELETE FROM listings WHERE id = ?", listing.getId());
        jdbc.update("DELETE FROM wallet WHERE user_id IN (?, ?)", buyer.getId(), seller.getId());
        jdbc.update("DELETE FROM users WHERE id IN (?, ?)", buyer.getId(), seller.getId());
    }

    @Test
    void paymentCommitsWalletsTransactionAndListingStateInPostgres() {
        marketplace.paySecured(listing.getId(), buyer.getId(), false);
        entityManager.clear();

        assertEquals(ListingStatus.PAID, listings.findById(listing.getId()).orElseThrow().getStatus());
        assertEquals(70.0, wallets.findByUser_Id(buyer.getId()).orElseThrow().getBalance());
        assertEquals(30.0, wallets.findByUser_Id(seller.getId()).orElseThrow().getBalance());
        assertEquals(1, transactions.existsByListing_Id(listing.getId()) ? 1 : 0);
    }

    @Test
    void paymentFailureAfterTransferRollsBackAllPostgresWrites() {
        doAnswer(invocation -> {
            Transaction result = (Transaction) invocation.callRealMethod();
            throw new IllegalStateException("forced test failure after wallet/transaction write");
        }).when(transactionService).transfer(any(User.class), any(User.class), any(Listing.class));

        assertThrows(IllegalStateException.class,
                () -> marketplace.paySecured(listing.getId(), buyer.getId(), false));
        entityManager.clear();

        assertEquals(ListingStatus.ACCEPTED, listings.findById(listing.getId()).orElseThrow().getStatus());
        assertEquals(100.0, wallets.findByUser_Id(buyer.getId()).orElseThrow().getBalance());
        assertEquals(0.0, wallets.findByUser_Id(seller.getId()).orElseThrow().getBalance());
        assertFalse(transactions.existsByListing_Id(listing.getId()));
    }

    @Test
    void insufficientBalanceLeavesPostgresPaymentUnchanged() {
        Wallet buyerWallet = wallets.findByUser_Id(buyer.getId()).orElseThrow();
        buyerWallet.setBalance(0.0);
        wallets.saveAndFlush(buyerWallet);
        assertEquals(0.0, wallets.findByUser_Id(buyer.getId()).orElseThrow().getBalance());
        assertEquals(30.0, listing.getPrice());
        assertEquals(buyer.getId(), listing.getBuyer().getId());

        assertThrows(IllegalStateException.class,
                () -> marketplace.paySecured(listing.getId(), buyer.getId(), false));
        entityManager.clear();

        assertEquals(ListingStatus.ACCEPTED, listings.findById(listing.getId()).orElseThrow().getStatus());
        assertEquals(0.0, wallets.findByUser_Id(buyer.getId()).orElseThrow().getBalance());
        assertEquals(0.0, wallets.findByUser_Id(seller.getId()).orElseThrow().getBalance());
        assertFalse(transactions.existsByListing_Id(listing.getId()));
    }

    @Test
    void postgresUniqueConstraintRejectsDuplicatePaymentRows() {
        Transaction first = transaction();
        transactions.saveAndFlush(first);
        Transaction duplicate = transaction();
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> transactions.saveAndFlush(duplicate));
    }

    @Test
    void concurrentPaymentAttemptsProduceExactlyOneCommittedPayment() throws Exception {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<Boolean>> results = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                results.add(pool.submit(() -> {
                    ready.countDown();
                    start.await();
                    try {
                        marketplace.paySecured(listing.getId(), buyer.getId(), false);
                        return true;
                    } catch (IllegalStateException | org.springframework.dao.DataIntegrityViolationException rejected) {
                        return false;
                    }
                }));
            }
            assertTrue(ready.await(10, java.util.concurrent.TimeUnit.SECONDS));
            start.countDown();
            int successful = (results.get(0).get() ? 1 : 0) + (results.get(1).get() ? 1 : 0);
            assertEquals(1, successful);
        } finally {
            pool.shutdownNow();
        }

        entityManager.clear();
        assertEquals(ListingStatus.PAID, listings.findById(listing.getId()).orElseThrow().getStatus());
        assertEquals(70.0, wallets.findByUser_Id(buyer.getId()).orElseThrow().getBalance());
        assertEquals(30.0, wallets.findByUser_Id(seller.getId()).orElseThrow().getBalance());
        assertTrue(transactions.existsByListing_Id(listing.getId()));
    }

    private Transaction transaction() {
        Transaction tx = new Transaction();
        tx.setAmount(listing.getPrice());
        tx.setFromUser(buyer);
        tx.setToUser(seller);
        tx.setListing(listing);
        tx.setStatus(TransactionStatus.COMPLETED);
        return tx;
    }

    private User user(String username, String email) {
        User user = new User(username, email, "non-login-test-hash");
        user.setRole(Role.USER);
        return user;
    }

    private Wallet wallet(User user, double balance) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(balance);
        return wallet;
    }
}
