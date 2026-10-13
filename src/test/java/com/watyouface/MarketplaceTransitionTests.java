package com.watyouface;

import com.watyouface.entity.Listing;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.ListingStatus;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.TransactionRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.service.MarketplaceService;
import com.watyouface.service.TransactionService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarketplaceTransitionTests {

    @Test
    void authorizedActorsCanCompleteTheDefinedListingLifecycle() {
        ListingRepository listings = mock(ListingRepository.class);
        UserRepository users = mock(UserRepository.class);
        TransactionRepository transactions = mock(TransactionRepository.class);
        TransactionService transfer = mock(TransactionService.class);

        User seller = new User(); seller.setId(1L);
        User buyer = new User(); buyer.setId(2L);
        Listing listing = new Listing();
        listing.setSeller(seller);
        listing.setPrice(25.0);
        listing.setStatus(ListingStatus.AVAILABLE);
        when(listings.findByIdForUpdate(7L)).thenReturn(Optional.of(listing));
        when(users.findById(2L)).thenReturn(Optional.of(buyer));
        when(transactions.existsByListing_Id(7L)).thenReturn(false);

        MarketplaceService service = new MarketplaceService(listings, users, transfer, transactions);

        service.requestPurchase(7L, 2L, false);
        assertEquals(ListingStatus.PENDING, listing.getStatus());
        service.accept(7L, 1L, false);
        assertEquals(ListingStatus.ACCEPTED, listing.getStatus());
        service.paySecured(7L, 2L, false);
        assertEquals(ListingStatus.PAID, listing.getStatus());
        service.ship(7L, 1L, false);
        assertEquals(ListingStatus.SHIPPED, listing.getStatus());
        service.receive(7L, 2L, false);
        assertEquals(ListingStatus.RECEIVED, listing.getStatus());
        verify(transfer).transfer(buyer, seller, listing);
    }

    @Test
    void nonSellerCannotAcceptAndInvalidTransitionIsRejected() {
        ListingRepository listings = mock(ListingRepository.class);
        Listing listing = new Listing();
        User seller = new User(); seller.setId(1L);
        User buyer = new User(); buyer.setId(2L);
        listing.setSeller(seller);
        listing.setBuyer(buyer);
        listing.setStatus(ListingStatus.PENDING);
        when(listings.findByIdForUpdate(7L)).thenReturn(Optional.of(listing));
        MarketplaceService service = new MarketplaceService(
                listings, mock(UserRepository.class), mock(TransactionService.class), mock(TransactionRepository.class));

        assertThrows(SecurityException.class, () -> service.accept(7L, 2L, false));
        assertEquals(ListingStatus.PENDING, listing.getStatus());
        verify(listings).findByIdForUpdate(7L);
        assertThrows(IllegalStateException.class, () -> service.ship(7L, 1L, false));
    }
}
