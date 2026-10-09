# Diagramme d’architecture globale

```mermaid
flowchart TB
  U[Utilisateur / navigateur] --> R[React 19 + Vite\nRoutes, composants, état UI]
  R -->|HTTPS REST + Bearer JWT| S[Spring Boot 3.3.5]
  R -->|SockJS / STOMP + JWT CONNECT| W[Endpoint /ws]
  W --> SI[JwtChannelInterceptor\ncontrôle participant topic]
  S --> SEC[SecurityConfig + JWT filter\nCORS, BCrypt, rôles]
  S --> C[Controllers + DTO + Validation]
  C --> M[Services métier\nownership, états, transactions]
  M --> REP[Spring Data JPA repositories]
  REP --> DB[(PostgreSQL)]
  M --> MEDIA[MediaStorageService\nrépertoire local media/]
  MEDIA --> PUB[/media/**\nressource actuellement publique/]
  S --> E[ApiExceptionHandler\n400 / 403 / 409 partiels]
```

## Flux REST représentatifs

### FLOW-REST-01 — Création de post

`USER → React FormData → POST /api/posts + Bearer → JwtAuthenticationFilter → PostController → validation → PostService (identité/ownership) → PostRepository → PostgreSQL → réponse → React`.

### FLOW-REST-02 — Paiement simulé marketplace

`Acheteur → POST /api/marketplace/listings/{id}/pay → JWT → MarketplaceController → MarketplaceService.paySecured → verrou listing + état + buyer + unicité transaction → TransactionService.transfer → WalletService + TransactionRepository + ListingRepository → réponse`.

### FLOW-REST-03 — Connexion

`Visiteur → POST /api/auth/login → AuthController/AuthService → vérification BCrypt → JwtUtil (JWT 24h) → réponse token → frontend localStorage observé → Bearer aux appels suivants`.

Ces flux décrivent l’existant/cible immédiate ; le contrôle contrat réel dans le login reste GAP-AUTH-001.
