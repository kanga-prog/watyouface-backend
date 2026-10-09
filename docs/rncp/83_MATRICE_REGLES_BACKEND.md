# Matrice règles → implémentation → tests — Phase 10

| Règle / exigence | Endpoint ou flux | Composant métier / accès données | Test | Résultat observé | RNCP |
|---|---|---|---|---|---|
| Contrat actif accepté avant JWT | `POST /api/auth/login` | `AuthService`, `ContractService` | `SecurityApiIntegrationTests.loginIsForbiddenUntilTheActiveContractIsAccepted` | 403 sans token si acceptation requise | C3/C9 |
| L'identité du contrat vient du token | `POST /api/contracts/accept` | `ContractService`, `Authz` | `contractUsesAuthenticatedUserAndIgnoresClientSuppliedUserId` | User A modifié, User B inchangé | C3/C8/C9 |
| Self-like interdit + like d'autrui | `POST /api/likes/toggle` | `LikeController`, `LikeService` | `SecurityApiIntegrationTests.likeApiRejectsSelfLikeAllowsOtherUserAndDisablesLegacyRoutes` | autre USER 200; self-like 403; principal résolu par ID authentifié | C3/C9 |
| Routes Like legacy neutralisées | `/api/likes/post/{id}`, `DELETE /api/likes/{id}` | `LikeController` | même test MockMvc | routes historiques retournent 410 | C3/C9 |
| Ownership Post | `POST/PUT/DELETE /api/posts/{id}` | `PostController`, `PostService` | `SecurityApiIntegrationTests.postApiEnforcesAuthenticatedCreationAndOwnerOnlyUpdateDelete`; `PostOwnershipTests` | création et opérations owner réussies; non-owner 403 | C3/C9 |
| Lecture messages réservée aux membres | REST `/api/messages/**` | `MessageController`, `ConversationRepository` | `nonParticipantCannotReadLegacyConversationEndpoint` | 403 non-membre; lecture membre 200 | C3/C8/C9 |
| Envoi message membre + validation | REST et STOMP `/app/chat.sendMessage` | `MessageDTO`, `MessageService` | `participantCanReadAndSendMessageButBlankContentIsRejected`; service auth test | message blanc 400 REST; limite 2000 au DTO/service; principal STOMP fournit sender | C3/C9 |
| Abonnement STOMP contrôlé | `/topic/conversations/{id}` | `JwtChannelInterceptor`, `ConversationRepository` | `StompAuthorizationTests.subscriptionInterceptorAllowsMembersAndRejectsNonMembers` | membre transmis, non-membre bloqué; test composant/intercepteur, sans broker E2E | C3/C9 |
| Cycle listing | demande → acceptation → paiement → expédition → réception | `MarketplaceService`, `ListingRepository` | `MarketplaceTransitionTests` | cycle autorisé testé; actor/état interdits refusés | C3/C8/C9 |
| Paiement unique par listing | `/api/marketplace/listings/{id}/pay` | verrou `findByIdForUpdate`, garde `existsByListing_Id`, contrainte PostgreSQL `uq_transaction_listing_id` | `PostgresPaymentIntegrationTests` (4 tests) | contrainte DB, paiement/commit, rollback et concurrence testés sur PostgreSQL 16.15; H2 garde ses tests propres | C3/C7/C8/C9 |
| Ressource Marketplace absente / conflit | GET annonce / paiement | `ResourceNotFoundException`, `ApiExceptionHandler` | `missingMarketplaceResourceIs404AndInvalidPaymentStateIs409` | 404 / 409 MockMvc | C3/C9 |
| Validation entrée invalide | Auth, Listing, Message | Bean Validation + garde service | `SecurityApiIntegrationTests` | 400 MockMvc | C3/C9 |

Note : les tests MockMvc sont sur H2; les quatre tests transactionnels dédiés utilisent réellement PostgreSQL 16.15. STOMP reste couvert au niveau intercepteur/handler, sans client broker de bout en bout.
