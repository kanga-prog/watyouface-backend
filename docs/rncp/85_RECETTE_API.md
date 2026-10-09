# Recette API — Phase 10B

Date d'exécution : 08/10/2026. Les comptes, messages et annonces de test sont fictifs (`example.test`). Aucun secret n'est inclus.

## Environnements et niveaux de preuve

- Suite générale : `./mvnw clean test`, profil `dev`, H2 mémoire, MockMvc sans socket HTTP. Résultat : 34 cas, 30 réussis, 4 tests PostgreSQL ignorés par absence des variables dans cette exécution.
- PostgreSQL réel : PostgreSQL 16.15, `watyouface_db`, profil `local`; classe dédiée `PostgresPaymentIntegrationTests`. Résultat : 5/5 PASS. La connexion locale est configurée en `trust`; le mot de passe vide est volontairement accepté par le garde JUnit. Fixtures aléatoires isolées puis supprimées.
- STOMP : tests unitaires dirigés vers l'intercepteur, le contrôleur STOMP et la garde de service réels; pas de client/broker E2E.
- Les assertions de service ne sont pas présentées comme des réponses HTTP; les résultats PostgreSQL ne sont pas confondus avec H2.

## Scénarios

| ID | Précondition | Niveau testé | Attendu | Observé | Verdict | Preuve |
|---|---|---|---|---|---|---|
| AUTH-01 | Compte fictif et contrat accepté | API MockMvc/H2 | login valide, token émis | HTTP 200 | PASS | `SecurityApiIntegrationTests.registrationThenLoginAcceptsValidCredentialsAndRejectsInvalidCredentials` |
| AUTH-02 | Compte fictif | API MockMvc/H2 | mauvais mot de passe refusé | HTTP 401 | PASS | même test |
| AUTH-03 | Contrat actif non accepté | API MockMvc/H2 | aucun accès/token | HTTP 403 métier | PASS | `loginIsForbiddenUntilTheActiveContractIsAccepted` |
| POST-API-01 | USER authentifié | API MockMvc/H2 | créer avec auteur authentifié | HTTP 200; auteur issu du principal | PASS | `postApiEnforcesAuthenticatedCreationAndOwnerOnlyUpdateDelete` |
| POST-API-02 | Propriétaire | API MockMvc/H2 | modifier son post | succès | PASS | même test |
| POST-API-03 | Autre USER | API MockMvc/H2 | modifier le post d'autrui | HTTP 403 | PASS | même test |
| POST-API-04 | Propriétaire | API MockMvc/H2 | supprimer son post | succès | PASS | même test |
| POST-API-05 | Autre USER | API MockMvc/H2 | supprimer le post d'autrui | HTTP 403 | PASS | même test |
| LIKE-API-01 | A distinct de l'auteur B | API MockMvc/H2 | A like le post de B | HTTP 200 | PASS | `likeApiRejectsSelfLikeAllowsOtherUserAndDisablesLegacyRoutes` |
| LIKE-API-02 | Auteur du post | API MockMvc/H2 | auto-like refusé | HTTP 403 | PASS | même test |
| LIKE-API-03 | Route Like historique | API MockMvc/H2 | route neutralisée | HTTP 410 | PASS | même test |
| CHAT-01 | Participant/non-participant | API MockMvc/H2 | lire seulement si membre | membre 200, non-membre 403 | PASS | `participantCanReadAndSendMessageButBlankContentIsRejected`, `nonParticipantCannotReadLegacyConversationEndpoint` |
| CHAT-02 | Participant | API MockMvc/H2 | envoyer message valide | succès, message persisté | PASS | `participantCanReadAndSendMessageButBlankContentIsRejected` |
| CHAT-03 | Message vide ou > 2000 caractères | DTO/service + API MockMvc | rejet | vide REST 400; garde service/STOMP lève `IllegalArgumentException`; longueur testée au service/handler | PASS aux niveaux indiqués | `MessageValidationTests`, `StompAuthorizationTests` |
| STOMP-01 | SUBSCRIBE membre | Intercepteur réel, test unitaire | autoriser | message transmis | PASS (unitaire) | `subscriptionInterceptorAllowsMembersAndRejectsNonMembers` |
| STOMP-02 | SUBSCRIBE non-membre | Intercepteur réel, test unitaire | bloquer | `preSend` retourne null | PASS (unitaire) | même test |
| STOMP-03 | Principal authentifié ID 7, payload senderId 999 | Contrôleur STOMP réel, service mocké | identité serveur utilisée | service appelé avec ID 7, jamais 999 | PASS (handler unitaire) | `stompHandlerUsesAuthenticatedPrincipalRatherThanPayloadSenderId` |
| STOMP-04 | Contenu vide | Contrôleur/service STOMP | rejeter | exception de validation métier | PASS (handler/service) | `stompHandlerRejectsBlankAndOverlongMessageThroughSharedServiceGuard` |
| STOMP-05 | Contenu > 2000 | Contrôleur/service STOMP | rejeter | exception de validation métier | PASS (handler/service) | même test |
| MKT-01 | Listing AVAILABLE, acheteur distinct | Service | demander achat puis acceptation | PENDING → ACCEPTED | PASS service | `MarketplaceTransitionTests` |
| MKT-02 | Mauvais acteur/état | Service | transition interdite refusée | état protégé | PASS service | `MarketplaceTransitionTests.nonSellerCannotAcceptAndInvalidTransitionIsRejected` |
| PAY-01 | Listing ACCEPTED, soldes démo | PostgreSQL 16.15 | paiement atomique | état PAID; acheteur 100→70; vendeur 0→30; transaction créée | PASS PostgreSQL | `PostgresPaymentIntegrationTests.paymentCommitsWalletsTransactionAndListingStateInPostgres` |
| PAY-02 | Écriture transaction déjà présente | PostgreSQL 16.15 | deuxième transaction refusée par DB | SQLSTATE 23505, contrainte `uq_transaction_listing_id`; fixture nettoyée | PASS PostgreSQL | `postgresUniqueConstraintRejectsDuplicatePaymentRows`; `BE-DATA-02-postgres-unique-payment.txt` |
| PAY-03 | Échec injecté après transfert | PostgreSQL 16.15 | rollback total | listing ACCEPTED; soldes inchangés; aucune transaction | PASS PostgreSQL | `paymentFailureAfterTransferRollsBackAllPostgresWrites` |
| PAY-04 | Deux appels quasi simultanés | PostgreSQL 16.15 | exactement un paiement | 1 succès; solde débité une fois; 1 transaction; état PAID | PASS PostgreSQL | `concurrentPaymentAttemptsProduceExactlyOneCommittedPayment` |
| PAY-05 | Wallet acheteur à zéro | PostgreSQL 16.15 | paiement refusé sans écritures partielles | rejet; listing ACCEPTED; soldes inchangés; aucune transaction | PASS PostgreSQL | `insufficientBalanceLeavesPostgresPaymentUnchanged` |
| ERROR-01 | Entrée DTO invalide | API MockMvc/H2 | 400 | HTTP 400 | PASS | `SecurityApiIntegrationTests` |
| ERROR-02 | USER non autorisé/non-membre | API MockMvc/H2 | 403 | HTTP 403 | PASS | tests Chat/Like/Marketplace |
| ERROR-03 | Listing absent | API MockMvc/H2 | 404 | HTTP 404 | PASS | `missingMarketplaceResourceIs404AndInvalidPaymentStateIs409` |
| ERROR-04 | Paiement en état PENDING | API MockMvc/H2 | conflit métier | HTTP 409 | PASS | même test |
| ERROR-05 | Erreur inattendue | Handler | 500 générique sans détail | handler présent; assertion dédiée non présente | PARTIEL | `ApiExceptionHandler` |

## Résultats et limites

La contrainte a été appliquée sur la base locale après requête préalable confirmant 11 transactions, 11 listings distincts et zéro doublon. Le test PostgreSQL n'est pas H2 : il démarre le contexte `local` et utilise la vraie base. Les 5 scénarios PostgreSQL passent indépendamment; dans la suite générale, ils sont conditionnellement ignorés afin que les tests H2 ne prennent pas les variables de la base locale.

Les cas STOMP valident les composants serveur réels aux frontières interceptor/handler/service, mais ne constituent pas une recette de transport WebSocket bout-en-bout. Les transitions marketplace sont testées au niveau service; le cycle complet de paiement est couvert par PostgreSQL, non par MockMvc.
