package com.watyouface.service;

import com.watyouface.dto.ListingDTO;
import com.watyouface.entity.Listing;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.ListingStatus;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.TransactionRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.exception.ResourceNotFoundException;
import com.watyouface.media.MediaReferenceCleanupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarketplaceService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private MediaReferenceCleanupService mediaCleanup;

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
    public List<ListingDTO> findVisibleTo(Long actorId, boolean actorIsAdmin) {
        return listingRepository.findAll().stream()
                .filter(listing -> isVisibleTo(listing, actorId, actorIsAdmin))
                .map(this::toDTO).toList();
    }

    public ListingDTO findVisibleTo(Long id, Long actorId, boolean actorIsAdmin) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));
        if (!isVisibleTo(listing, actorId, actorIsAdmin)) {
            throw new ResourceNotFoundException("Annonce introuvable");
        }
        return toDTO(listing);
    }

    // CREATE => seller = current user
    public ListingDTO createAsSeller(ListingDTO dto, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendeur introuvable"));

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
    @Transactional
    public ListingDTO updateSecured(Long id, ListingDTO dto, Long actorId) {
        Listing listing = listingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));

        if (!listing.getSeller().getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }

        if (listing.getStatus() != ListingStatus.AVAILABLE && listing.getStatus() != ListingStatus.PENDING) {
            throw new IllegalStateException("Modification impossible dans l'état " + listing.getStatus());
        }

        String previousImage = listing.getImage();
        listing.setTitle(dto.title);
        listing.setDescription(dto.description);
        listing.setPrice(dto.price);
        listing.setImage(dto.image);

        ListingDTO updated = toDTO(listingRepository.save(listing));
        scheduleMediaCleanup(previousImage);
        return updated;
    }

    // DELETE sécurisé
    @Transactional
    public void deleteSecured(Long id, Long actorId) {
        Listing listing = listingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));

        if (!listing.getSeller().getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }

        if (listing.getStatus() == ListingStatus.PAID
                || listing.getStatus() == ListingStatus.SHIPPED
                || listing.getStatus() == ListingStatus.RECEIVED) {
            throw new IllegalStateException("Suppression impossible après paiement/expédition");
        }

        String previousImage = listing.getImage();
        listingRepository.delete(listing);
        scheduleMediaCleanup(previousImage);
    }

    /**
     * La modération détruit seulement les annonces sans transaction active.
     * Une annonce engagée est masquée des tiers, sans casser son cycle
     * vendeur/acheteur ni les écritures de paiement existantes.
     */
    @Transactional
    public void deleteForModeration(Long id) {
        Listing listing = listingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));
        if (listing.getStatus() == ListingStatus.AVAILABLE || listing.getStatus() == ListingStatus.REFUSED) {
            String previousImage = listing.getImage();
            listingRepository.delete(listing);
            scheduleMediaCleanup(previousImage);
            return;
        }
        listing.setHidden(true);
        listingRepository.save(listing);
    }

    // Buyer demande achat => PENDING + buyer
    @Transactional
    public void requestPurchase(Long listingId, Long buyerId) {
        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));

        if (listing.getStatus() != ListingStatus.AVAILABLE) {
            throw new IllegalStateException("Demande impossible dans l'état " + listing.getStatus());
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Acheteur introuvable"));

        if (listing.getSeller().getId().equals(buyerId)) {
            throw new IllegalStateException("Vous ne pouvez pas acheter votre propre annonce");
        }

        listing.setBuyer(buyer);
        listing.setStatus(ListingStatus.PENDING);
        listingRepository.save(listing);
    }

    @Deprecated(forRemoval = false)
    @Transactional
    public void requestPurchase(Long listingId, Long buyerId, boolean ignoredAdmin) {
        requestPurchase(listingId, buyerId);
    }

    // Seller accepte => ACCEPTED
    @Transactional
    public void accept(Long listingId, Long actorId) {
        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));

        if (!listing.getSeller().getId().equals(actorId)) {
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

    @Deprecated(forRemoval = false)
    @Transactional
    public void accept(Long listingId, Long actorId, boolean ignoredAdmin) {
        accept(listingId, actorId);
    }

    // Seller refuse => REFUSED
    @Transactional
    public void refuse(Long listingId, Long actorId) {
        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));

        if (!listing.getSeller().getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }

        if (listing.getStatus() != ListingStatus.PENDING) {
            throw new IllegalStateException("Refus impossible dans l'état " + listing.getStatus());
        }

        listing.setStatus(ListingStatus.REFUSED);
        listingRepository.save(listing);
    }

    @Deprecated(forRemoval = false)
    @Transactional
    public void refuse(Long listingId, Long actorId, boolean ignoredAdmin) {
        refuse(listingId, actorId);
    }

    // Buyer paye => PAID (uniquement ACCEPTED)
    @Transactional
    public void paySecured(Long listingId, Long buyerId) {

        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));

        if (listing.getStatus() != ListingStatus.ACCEPTED) {
            throw new IllegalStateException("Paiement impossible dans l'état " + listing.getStatus());
        }

        if (listing.getBuyer() == null) {
            throw new IllegalStateException("Aucun acheteur défini");
        }

        if (!listing.getBuyer().getId().equals(buyerId)) {
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

    @Deprecated(forRemoval = false)
    @Transactional
    public void paySecured(Long listingId, Long buyerId, boolean ignoredAdmin) {
        paySecured(listingId, buyerId);
    }

    // Seller ship => SHIPPED (uniquement PAID)
    @Transactional
    public void ship(Long listingId, Long actorId) {
        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));

        if (!listing.getSeller().getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }

        if (listing.getStatus() != ListingStatus.PAID) {
            throw new IllegalStateException("Expédition impossible dans l'état " + listing.getStatus());
        }

        listing.setStatus(ListingStatus.SHIPPED);
        listingRepository.save(listing);
    }

    @Deprecated(forRemoval = false)
    @Transactional
    public void ship(Long listingId, Long actorId, boolean ignoredAdmin) {
        ship(listingId, actorId);
    }

    // Buyer receive => RECEIVED (uniquement SHIPPED)
    @Transactional
    public void receive(Long listingId, Long buyerId) {
        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable"));

        if (listing.getStatus() != ListingStatus.SHIPPED) {
            throw new IllegalStateException("Réception impossible dans l'état " + listing.getStatus());
        }

        if (listing.getBuyer() == null) {
            throw new IllegalStateException("Aucun acheteur défini");
        }

        if (!listing.getBuyer().getId().equals(buyerId)) {
            throw new SecurityException("Interdit : seul l'acheteur peut confirmer");
        }

        listing.setStatus(ListingStatus.RECEIVED);
        listingRepository.save(listing);
    }

    @Deprecated(forRemoval = false)
    @Transactional
    public void receive(Long listingId, Long buyerId, boolean ignoredAdmin) {
        receive(listingId, buyerId);
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

    private boolean isVisibleTo(Listing listing, Long actorId, boolean actorIsAdmin) {
        if (!listing.isHidden() || actorIsAdmin) return true;
        if (actorId == null) return false;
        return listing.getSeller().getId().equals(actorId)
                || (listing.getBuyer() != null && listing.getBuyer().getId().equals(actorId));
    }

    private void scheduleMediaCleanup(String publicUrl) {
        if (mediaCleanup != null && publicUrl != null && !publicUrl.isBlank()) {
            mediaCleanup.deleteIfUnreferencedAfterCommit(List.of(publicUrl));
        }
    }
}
