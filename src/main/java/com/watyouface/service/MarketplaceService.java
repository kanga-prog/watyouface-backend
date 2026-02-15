package com.watyouface.service;

import com.watyouface.dto.ListingDTO;
import com.watyouface.entity.Listing;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.ListingStatus;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarketplaceService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public MarketplaceService(ListingRepository listingRepository, UserRepository userRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    // ---------- READ ----------
    public List<ListingDTO> findAll() {
        return listingRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ListingDTO findById(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        return toDTO(listing);
    }

    // ---------- CREATE sécurisé ----------
    public ListingDTO createAs(ListingDTO dto, Long currentUserId) {

        User seller = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Vendeur introuvable"));

        Listing listing = new Listing();
        listing.setTitle(dto.title);
        listing.setDescription(dto.description);
        listing.setPrice(dto.price);
        listing.setImage(dto.image);
        listing.setSeller(seller);
        listing.setStatus(ListingStatus.AVAILABLE);

        return toDTO(listingRepository.save(listing));
    }

    // ---------- UPDATE sécurisé ----------
    public ListingDTO updateAs(Long id, ListingDTO dto, Long currentUserId, boolean isAdmin) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));

        Long sellerId = listing.getSeller() != null ? listing.getSeller().getId() : null;

        if (!isAdmin) {
            if (sellerId == null || !sellerId.equals(currentUserId)) {
                throw new AccessDeniedException("Interdit : vous ne pouvez modifier que vos annonces.");
            }
        }

        if (listing.getStatus() != null && listing.getStatus().isFinal()) {
            throw new IllegalStateException("Annonce dans un état final");
        }

        listing.setTitle(dto.title);
        listing.setDescription(dto.description);
        listing.setPrice(dto.price);
        listing.setImage(dto.image);

        return toDTO(listingRepository.save(listing));
    }

    // ---------- DELETE sécurisé ----------
    public void deleteAs(Long id, Long currentUserId, boolean isAdmin) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));

        Long sellerId = listing.getSeller() != null ? listing.getSeller().getId() : null;

        if (!isAdmin) {
            if (sellerId == null || !sellerId.equals(currentUserId)) {
                throw new AccessDeniedException("Interdit : vous ne pouvez supprimer que vos annonces.");
            }
        }

        listingRepository.deleteById(id);
    }

    // ---------- PAY sécurisé ----------
    @Transactional
    public void payAs(Long listingId, Long currentUserId, boolean isAdmin) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));

        if (!listing.getStatus().canBePaid()) {
            throw new IllegalStateException("Paiement impossible dans l'état actuel");
        }

        // USER ne peut pas payer sa propre annonce
        Long sellerId = listing.getSeller() != null ? listing.getSeller().getId() : null;
        if (!isAdmin && sellerId != null && sellerId.equals(currentUserId)) {
            throw new AccessDeniedException("Interdit : vous ne pouvez pas payer votre propre annonce.");
        }

        User buyer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Acheteur introuvable"));

        listing.setBuyer(buyer);
        listing.setStatus(ListingStatus.PAID);

        listingRepository.save(listing);
    }

    // ---------- MAPPER ----------
    private ListingDTO toDTO(Listing listing) {
        ListingDTO dto = new ListingDTO();
        dto.id = listing.getId();
        dto.title = listing.getTitle();
        dto.description = listing.getDescription();
        dto.price = listing.getPrice();
        dto.image = listing.getImage();
        dto.status = listing.getStatus().name();
        dto.sellerId = listing.getSeller().getId();
        dto.buyerId = listing.getBuyer() != null ? listing.getBuyer().getId() : null;
        return dto;
    }
}
