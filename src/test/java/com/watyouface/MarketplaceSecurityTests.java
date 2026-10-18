package com.watyouface;

import com.watyouface.entity.Listing;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.ListingStatus;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.TransactionRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.service.MarketplaceService;
import com.watyouface.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MarketplaceSecurityTests {
    private ListingRepository listings;
    private UserRepository users;
    private TransactionRepository transactions;
    private TransactionService transactionService;
    private Listing listing;
    private MarketplaceService service;

    @BeforeEach
    void setUp() {
        listings = mock(ListingRepository.class);
        users = mock(UserRepository.class);
        transactions = mock(TransactionRepository.class);
        transactionService = mock(TransactionService.class);
        User seller = new User(); seller.setId(1L);
        User buyer = new User(); buyer.setId(2L);
        listing = new Listing(); listing.setSeller(seller); listing.setBuyer(buyer); listing.setPrice(10.0);
        service = new MarketplaceService(listings, users, transactionService, transactions);
    }

    @Test void unrelatedUserCannotPay() {
        listing.setStatus(ListingStatus.ACCEPTED);
        when(listings.findByIdForUpdate(12L)).thenReturn(Optional.of(listing));
        assertThrows(SecurityException.class, () -> service.paySecured(12L, 3L, false));
    }

    @Test void adminFlagDoesNotBypassMarketplaceActors() {
        listing.setStatus(ListingStatus.PENDING);
        when(listings.findByIdForUpdate(12L)).thenReturn(Optional.of(listing));

        // The legacy boolean is deliberately ignored: a moderator cannot act
        // as seller or buyer merely because it has the ADMIN role.
        assertThrows(SecurityException.class, () -> service.accept(12L, 2L, true));
        assertThrows(SecurityException.class, () -> service.refuse(12L, 2L, true));

        listing.setStatus(ListingStatus.ACCEPTED);
        assertThrows(SecurityException.class, () -> service.paySecured(12L, 3L, true));

        listing.setStatus(ListingStatus.PAID);
        assertThrows(SecurityException.class, () -> service.ship(12L, 2L, true));

        listing.setStatus(ListingStatus.SHIPPED);
        assertThrows(SecurityException.class, () -> service.receive(12L, 1L, true));

        listing.setStatus(ListingStatus.AVAILABLE);
        when(users.findById(1L)).thenReturn(Optional.of(listing.getSeller()));
        assertThrows(IllegalStateException.class, () -> service.requestPurchase(12L, 1L, true));
    }

    @Test void paymentCannotBeRepeated() {
        listing.setStatus(ListingStatus.ACCEPTED);
        when(listings.findByIdForUpdate(12L)).thenReturn(Optional.of(listing));
        when(transactions.existsByListing_Id(12L)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.paySecured(12L, 2L, false));
        verifyNoInteractions(transactionService);
    }

    @Test void invalidStateCannotBePaid() {
        listing.setStatus(ListingStatus.PENDING);
        when(listings.findByIdForUpdate(12L)).thenReturn(Optional.of(listing));
        assertThrows(IllegalStateException.class, () -> service.paySecured(12L, 2L, false));
    }
}
