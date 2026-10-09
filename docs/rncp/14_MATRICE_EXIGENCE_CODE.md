# Matrice exigence cible ↔ code actuel

**Méthode :** lecture statique de la branche `rncp6/security-hardening` le 07/10/2026. `CONFORME` signifie qu'un comportement ciblé est directement observé ; `PARTIEL` qu'une partie manque, est contournable ou non testée ; `NON CONFORME` qu'une contradiction est observée ; `NON PROUVÉ` qu'aucune preuve suffisante n'a été trouvée. Cette matrice ne remplace pas les tests d'exécution futurs.

| ID exigence | Exigence cible | Implémentation actuelle observée | État | Preuve fichier:ligne | Correction / décision nécessaire | Priorité |
|---|---|---|---|---|---|---|
| AUTH-001 | Inscription validée et unique | DTO valide username/email/password ; service vérifie email/username et BCrypt. | CONFORME | `RegisterRequest.java:8-17`; `AuthService.java:52-77` | tests conflits 409 et erreurs homogènes à compléter. | P1 |
| AUTH-002 | Contrat accepté avant accès membre | l'inscription enregistre `acceptTerms`, mais le `AuthController` de login authentifie/émet un JWT sans vérifier `acceptedContract`; méthode service non utilisée contient ce contrôle. | NON CONFORME | `AuthController.java:62-90`; `AuthService.java:40-42` | faire appliquer le contrôle dans le flux login réel + test. | P0 |
| AUTH-003 | Login générique et validé | `@Valid`; `AuthenticationException` retourne 401 « Identifiants invalides ». | CONFORME | `LoginRequest.java:8-13`; `AuthController.java:63-72` | test contrat refusé requis après correction AUTH-002. | P1 |
| AUTH-004 | Logout fonctionnel | suppression locale du token attendue ; aucune révocation serveur ni preuve complète recensée. | PARTIEL | `watyouface-frontend/src/utils/api.js` (stockage token, audit) | définir logout client et stratégie de révocation/expiration. | P2 |
| AUTH-005 | API protégée sans JWT | `anyRequest().authenticated()` et entry point 401 ; test existant. | CONFORME | `SecurityConfig.java:49-63`; `SecurityApiIntegrationTests` | conserver test. | P1 |
| AUTH-006 | Administration réservée ADMIN | `requireAdmin()` vérifie `authz.isAdmin()`. | CONFORME | `AdminController.java:37-83` | tests API USER/ADMIN à compléter. | P1 |
| PROFILE-001 | Consulter son profil privé | `/me` dérive l'identité de `authz.me()`. | CONFORME | `UserController.java:33-55` | documenter données exposées. | P1 |
| PROFILE-002 | Username validé et unique à la modification | endpoint Map vérifie seulement non vide ; aucune validation taille/unicité applicative ni réponse conflit maîtrisée. | PARTIEL | `UserController.java:57-83`; `User.java:16-17` | DTO `@Valid`, unicité et 409. | P1 |
| PROFILE-003 | Avatar contrôlé | service dédié appelé, mais les critères précis ne sont pas revérifiés dans cette phase. | PARTIEL | `UserController.java:85-104` | auditer type/taille/contenu et tests. | P2 |
| PROFILE-004 | Suppression de compte avec politique | owner/admin autorisé mais effets sur données, rétention et RGPD non documentés. | PARTIEL | `UserController.java:122-129` | décider suppression/anonymisation et cascades. | P1 |
| SOCIAL-001 | Feed lisible et borné | liste triée décroissante ; aucune pagination/visibilité définie. | PARTIEL | `PostController.java:33-64`; `PostService.java:112-115` | décider visibilité/pagination et tests. | P2 |
| POST-001 | USER crée un post lié à son identité | auteur vient de `authz.me()` ; média facultatif. | PARTIEL | `PostController.java:119-160` | valider contenu, taille/type média et réponses d'erreur. | P1 |
| POST-002 | Propriétaire modifie son post | service owner/admin. | PARTIEL | `PostService.java:145-157` | DTO/validation contenu et test API 403. | P1 |
| POST-003 | Propriétaire supprime son post | service owner/admin. | CONFORME | `PostService.java:126-138` | test non-régression à ajouter. | P1 |
| POST-004 | Tiers refusé, ADMIN explicite | `AccessDeniedException` tiers ; ADMIN autorisé. | CONFORME | `PostService.java:129-135,148-153` | journalisation ADMIN à décider. | P2 |
| COMMENT-001 | USER commente un post visible | identité et post existent ; contenu non vide ; visibilité non définie, longueur non bornée. | PARTIEL | `CommentController.java:45-76` | DTO/limite/visibilité et tests. | P1 |
| COMMENT-002 | Owner/ADMIN modifie/supprime commentaire | contrôle central owner/admin. | CONFORME | `CommentService.java:36-68` | tests API à ajouter. | P1 |
| LIKE-001 | Like unique sur post tiers | toggle recherché par post/user mais aucun contrôle d'unicité DB observé ; anciens endpoints créent/suppriment un Like sans ownership. | PARTIEL | `LikeService.java:25-49,56-62`; `LikeController.java:69-86` | supprimer/fermer anciens endpoints, contrainte unique, tests. | P0 |
| LIKE-002 | Auto-like interdit | aucun contrôle auteur=liker. | NON CONFORME | `LikeService.java:25-49`; `LikeController.java:37-67` | contrôle serveur + test 403/409. | P0 |
| CHAT-001 | Lecture réservée aux participants | endpoints principaux vérifient l'appartenance, mais `/api/messages/{id}/all` retourne sans contrôle. | NON CONFORME | `MessageController.java:50-60,95-112` | supprimer/protéger endpoint diagnostic + test BOLA lecture. | P0 |
| CHAT-002 | Envoi réservé aux participants | REST et service vérifient ; WebSocket appelle le service. Contenu pas explicitement validé. | PARTIEL | `MessageController.java:63-91`; `MessageService.java:35-82`; `ChatController.java:25-45` | DTO validation longueur/non-vide et tests STOMP. | P1 |
| CHAT-003 | Abonnement STOMP réservé aux participants | interceptor JWT et contrôle `existsBy...`. E2E non exécuté. | PARTIEL | `JwtChannelInterceptor.java:45-96` | test STOMP défensif. | P1 |
| CHAT-004 | Chat de négociation lié à une annonce | conversation n'a pas de référence Listing ; usage métier possible seulement de manière libre. | NON PROUVÉ | `Conversation.java:13-41`; `Listing.java:8-36` | décider lien explicite ou documenter chat générique. | P2 |
| CHAT-005 | Conversation groupe inclut un créateur et participants contrôlés | le contrôleur transmet seulement les IDs body ; aucune injection du créateur ni validation liste/titre. | PARTIEL | `ConversationController.java:38-51`; `ConversationService.java:61-76` | règle de création groupe et validation. | P2 |
| MARKET-001 | Création annonce : vendeur JWT, AVAILABLE, validation | vendeur serveur, état AVAILABLE, `@Valid` titre/prix. | CONFORME | `MarketplaceController.java:43-47`; `MarketplaceService.java:47-60`; `ListingDTO.java:11-18` | tests upload/image et création API. | P1 |
| MARKET-002 | Consulter annonces avec règles de visibilité/bornage | `findAll`/`findById` sans pagination ni règle de visibilité formalisée. | PARTIEL | `MarketplaceController.java:33-41`; `MarketplaceService.java:35-44` | décider pagination/visibilité. | P2 |
| MARKET-003 | Demande tiers AVAILABLE vers PENDING | contrôle état et auto-achat ; buyer défini. | CONFORME | `MarketplaceService.java:101-121` | tests 409 état et auto-achat. | P1 |
| MARKET-004 | Vendeur accepte/refuse PENDING | contrôles vendeur/admin + état PENDING. | CONFORME | `MarketplaceService.java:123-161` | tests accept/refuse. | P1 |
| MARKET-005 | Paiement simulé unique, atomique | verrou pessimiste, état ACCEPTED, acheteur, existence Transaction, transfert transactionnel. | CONFORME | `MarketplaceService.java:163-192`; `TransactionService.java:25-40`; `ListingRepository.java:14-16` | test solde insuffisant et type monétaire à traiter. | P1 |
| MARKET-006 | Expédition vendeur après PAID | rôle et état contrôlés. | CONFORME | `MarketplaceService.java:194-210` | test non-régression. | P1 |
| MARKET-007 | Réception acheteur après SHIPPED | rôle et état contrôlés. | CONFORME | `MarketplaceService.java:212-232` | test non-régression. | P1 |
| WALLET-001 | USER ne se crédite pas | crédit refuse sans ADMIN ; test existant. | CONFORME | `WalletController.java:43-50`; `SecurityApiIntegrationTests` | conserver test. | P1 |
| WALLET-002 | Crédit démo ADMIN, montant positif | rôle ADMIN et DTO validé positif. | CONFORME | `WalletController.java:39-50`; `WalletCreditRequest.java` | documenter procédure de jeu d'essai. | P2 |
| WALLET-003 | Pas de PSP réel | transfert interne wallet + Transaction ; aucun PSP observé. | CONFORME | `WalletService.java:37-55`; `TransactionService.java:25-40` | rendre le wording UI explicite. | P1 |
| ADMIN-001 | Endpoints admin réservés | méthode `requireAdmin`. | CONFORME | `AdminController.java:37-83` | tests et journalisation actions. | P1 |
| NFR-SEC-001 | Erreurs cohérentes sans fuite | conseil central existe, mais contrôleurs gardent `printStackTrace`/500 directs. | PARTIEL | `ApiExceptionHandler.java`; `PostController.java:66-69,155-160` | logger structuré et harmonisation. | P2 |
| NFR-PRIV-001 | Données personnelles finalité/rétention | entités contiennent données ; aucune politique/versionnée observée. | NON PROUVÉ | `User.java:16-61`; audit initial | produire registre, mentions et rétention. | P1 |
| NFR-PERF-001 | Listes bornées | messages paginés sur un endpoint ; feed et annonces non paginés. | PARTIEL | `MessageController.java:43-60`; `PostController.java:33-64`; `MarketplaceController.java:33-36` | pagination/limites. | P2 |

## Synthèse de cette matrice

**CONFORME : 18** — **PARTIEL : 15** — **NON CONFORME : 3** — **NON PROUVÉ : 2**. Ces nombres portent sur les 38 lignes de la matrice, et non sur la maturité globale de l'application.
