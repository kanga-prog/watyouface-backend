package com.watyouface.service;

import com.watyouface.dto.ListingDTO;
import com.watyouface.entity.Listing;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.ListingStatus;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarketplaceService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public MarketplaceService(
            ListingRepository listingRepository,
            UserRepository userRepository
    ) {
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
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));
        return toDTO(listing);
    }

    // ---------- CREATE ----------
    public ListingDTO create(ListingDTO dto) {
        User seller = userRepository.findById(dto.sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Vendeur introuvable"));

        Listing listing = new Listing();
        listing.setTitle(dto.title);
        listing.setDescription(dto.description);
        listing.setPrice(dto.price);
        listing.setImage(dto.image);
        listing.setSeller(seller);
        listing.setStatus(ListingStatus.AVAILABLE);

        return toDTO(listingRepository.save(listing));
    }

    // ---------- UPDATE ----------
    public ListingDTO update(Long id, ListingDTO dto) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (listing.getStatus().isFinal()) {
            throw new IllegalStateException("Annonce dans un état final");
        }

        listing.setTitle(dto.title);
        listing.setDescription(dto.description);
        listing.setPrice(dto.price);
        listing.setImage(dto.image);

        return toDTO(listingRepository.save(listing));
    }

    // ---------- DELETE ----------
    public void delete(Long id) {
        if (!listingRepository.existsById(id)) {
            throw new IllegalArgumentException("Annonce introuvable");
        }
        listingRepository.deleteById(id);
    }

    // ---------- PAY ----------
    @Transactional
    public void pay(Long listingId, Long buyerId) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (!listing.getStatus().canBePaid()) {
            throw new IllegalStateException("Paiement impossible dans l'état actuel");
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Acheteur introuvable"));

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
