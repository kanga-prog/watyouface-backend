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
import com.watyouface.service.AccountErasureService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Intentionally restricted to an explicitly enabled local PostgreSQL URL.
 * It can never run against the Railway production hostname by annotation.
 */
@SpringBootTest(properties = "app.media.base-dir=/tmp/watyouface-pg-erasure-tests")
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "RUN_POSTGRES_TESTS", matches = "true")
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = "jdbc:postgresql://(localhost|127\\.0\\.0\\.1)(:.*)?/.*")
@EnabledIfEnvironmentVariable(named = "DB_USERNAME", matches = ".+")
@EnabledIfEnvironmentVariable(named = "JWT_SECRET", matches = ".{32,}")
class PostgresAccountErasureIntegrationTests {

    @Autowired private AccountErasureService erasure;
    @Autowired private UserRepository users;
    @Autowired private ListingRepository listings;
    @Autowired private TransactionRepository transactions;
    @Autowired private WalletRepository wallets;
    @Autowired private JdbcTemplate jdbc;

    private User buyer;
    private User seller;
    private Listing listing;

    @BeforeEach
    void createFixture() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        buyer = users.saveAndFlush(user("erase-buyer-" + suffix, "erase-buyer-" + suffix + "@example.test"));
        seller = users.saveAndFlush(user("erase-seller-" + suffix, "erase-seller-" + suffix + "@example.test"));
        listing = new Listing();
        listing.setSeller(seller);
        listing.setBuyer(buyer);
        listing.setTitle("personal transaction listing");
        listing.setDescription("personal transaction description");
        listing.setPrice(20.0);
        listing.setStatus(ListingStatus.PAID);
        listing = listings.saveAndFlush(listing);
        Transaction tx = new Transaction();
        tx.setFromUser(buyer);
        tx.setToUser(seller);
        tx.setListing(listing);
        tx.setAmount(20.0);
        tx.setStatus(TransactionStatus.COMPLETED);
        transactions.saveAndFlush(tx);
        wallets.saveAndFlush(wallet(buyer, 80.0));
        wallets.saveAndFlush(wallet(seller, 20.0));
    }

    @AfterEach
    void deleteOnlyFixture() {
        if (listing == null || buyer == null || seller == null) return;
        jdbc.update("DELETE FROM \"transaction\" WHERE listing_id = ?", listing.getId());
        jdbc.update("DELETE FROM listings WHERE id = ?", listing.getId());
        jdbc.update("DELETE FROM wallet WHERE user_id IN (?, ?)", buyer.getId(), seller.getId());
        jdbc.update("DELETE FROM users WHERE id IN (?, ?)", buyer.getId(), seller.getId());
    }

    @Test
    void erasureKeepsRealPostgresForeignKeysAndCompletedTransactionIntact() {
        erasure.erase(seller.getId());

        User erased = users.findById(seller.getId()).orElseThrow();
        assertTrue(erased.isDeleted());
        assertTrue(erased.getEmail().endsWith("@deleted.invalid"));
        assertFalse(transactions.findByListing_Id(listing.getId()).isEmpty());
        Listing retained = listings.findById(listing.getId()).orElseThrow();
        assertEquals(ListingStatus.PAID, retained.getStatus());
        assertTrue(retained.isHidden());
        assertEquals("Annonce supprimée", retained.getTitle());
        assertNull(retained.getDescription());
        assertFalse(wallets.findByUser_Id(buyer.getId()).isEmpty());
        assertTrue(wallets.findByUser_Id(seller.getId()).isEmpty());
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
