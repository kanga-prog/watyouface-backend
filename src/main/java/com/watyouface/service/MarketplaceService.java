package com.watyouface.service;

import com.watyouface.dto.ListingDTO;
import com.watyouface.entity.Listing;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.ListingStatus;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.TransactionRepository;
import com.watyouface.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarketplaceService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    public MarketplaceService(
            ListingRepository listingRepository,
            UserRepository userRepository,
            TransactionService transactionService,
            TransactionRepository transactionRepository
    ) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    // READ
    public List<ListingDTO> findAll() {
        return listingRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ListingDTO findById(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));
        return toDTO(listing);
    }

    // CREATE => seller = current user
    public ListingDTO createAsSeller(ListingDTO dto, Long sellerId) {
        User seller = userRepository.findById(sellerId)
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

    // UPDATE sécurisé
    public ListingDTO updateSecured(Long id, ListingDTO dto, Long actorId, boolean admin) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (!admin && !listing.getSeller().getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }

        if (listing.getStatus() != ListingStatus.AVAILABLE && listing.getStatus() != ListingStatus.PENDING) {
            throw new IllegalStateException("Modification impossible dans l'état " + listing.getStatus());
        }

        listing.setTitle(dto.title);
        listing.setDescription(dto.description);
        listing.setPrice(dto.price);
        listing.setImage(dto.image);

        return toDTO(listingRepository.save(listing));
    }

    // DELETE sécurisé
    public void deleteSecured(Long id, Long actorId, boolean admin) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (!admin && !listing.getSeller().getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }

        if (listing.getStatus() == ListingStatus.PAID
                || listing.getStatus() == ListingStatus.SHIPPED
                || listing.getStatus() == ListingStatus.RECEIVED) {
            throw new IllegalStateException("Suppression impossible après paiement/expédition");
        }

        listingRepository.delete(listing);
    }

    // Buyer demande achat => PENDING + buyer
    @Transactional
    public void requestPurchase(Long listingId, Long buyerId, boolean admin) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (listing.getStatus() != ListingStatus.AVAILABLE) {
            throw new IllegalStateException("Demande impossible dans l'état " + listing.getStatus());
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Acheteur introuvable"));

        if (!admin && listing.getSeller().getId().equals(buyerId)) {
            throw new IllegalStateException("Vous ne pouvez pas acheter votre propre annonce");
        }

        listing.setBuyer(buyer);
        listing.setStatus(ListingStatus.PENDING);
        listingRepository.save(listing);
    }

    // Seller accepte => ACCEPTED
    @Transactional
    public void accept(Long listingId, Long actorId, boolean admin) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (!admin && !listing.getSeller().getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }

        if (listing.getStatus() != ListingStatus.PENDING) {
            throw new IllegalStateException("Accept impossible dans l'état " + listing.getStatus());
        }

        if (listing.getBuyer() == null) {
            throw new IllegalStateException("Aucun acheteur en attente");
        }

        listing.setStatus(ListingStatus.ACCEPTED);
        listingRepository.save(listing);
    }

    // Seller refuse => REFUSED
    @Transactional
    public void refuse(Long listingId, Long actorId, boolean admin) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (!admin && !listing.getSeller().getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }

        if (listing.getStatus() != ListingStatus.PENDING) {
            throw new IllegalStateException("Refus impossible dans l'état " + listing.getStatus());
        }

        listing.setStatus(ListingStatus.REFUSED);
        listingRepository.save(listing);
    }

    // Buyer paye => PAID (uniquement ACCEPTED)
    @Transactional
    public void paySecured(Long listingId, Long buyerId, boolean admin) {

        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (listing.getStatus() != ListingStatus.ACCEPTED) {
            throw new IllegalStateException("Paiement impossible dans l'état " + listing.getStatus());
        }

        if (listing.getBuyer() == null) {
            throw new IllegalStateException("Aucun acheteur défini");
        }

        if (!admin && !listing.getBuyer().getId().equals(buyerId)) {
            throw new SecurityException("Interdit : seul l'acheteur peut payer");
        }

        // ⚠️ IMPORTANT : à vérifier selon ton Transaction entity
        // Si Transaction a `Listing listing;` => utiliser existsByListing_Id(listingId)
        if (transactionRepository.existsByListing_Id(listingId)){
            throw new IllegalStateException("Paiement déjà effectué");
        }

        transactionService.transfer(listing.getBuyer(), listing.getSeller(), listing);

        listing.setStatus(ListingStatus.PAID);
        listingRepository.save(listing);
    }

    // Seller ship => SHIPPED (uniquement PAID)
    @Transactional
    public void ship(Long listingId, Long actorId, boolean admin) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (!admin && !listing.getSeller().getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }

        if (listing.getStatus() != ListingStatus.PAID) {
            throw new IllegalStateException("Expédition impossible dans l'état " + listing.getStatus());
        }

        listing.setStatus(ListingStatus.SHIPPED);
        listingRepository.save(listing);
    }

    // Buyer receive => RECEIVED (uniquement SHIPPED)
    @Transactional
    public void receive(Long listingId, Long buyerId, boolean admin) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        if (listing.getStatus() != ListingStatus.SHIPPED) {
            throw new IllegalStateException("Réception impossible dans l'état " + listing.getStatus());
        }

        if (listing.getBuyer() == null) {
            throw new IllegalStateException("Aucun acheteur défini");
        }

        if (!admin && !listing.getBuyer().getId().equals(buyerId)) {
            throw new SecurityException("Interdit : seul l'acheteur peut confirmer");
        }

        listing.setStatus(ListingStatus.RECEIVED);
        listingRepository.save(listing);
    }

    // MAPPER
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
