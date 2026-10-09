# Matrice de tests de sécurité — WatYouFace

| ID | Précondition | Requête / action | Résultat attendu | Résultat obtenu | Verdict | Test automatisé |
|---|---|---|---|---|---|---|
| SEC-T01 | aucun jeton | `GET /api/wallet/me` | 401 | 401 | PASS | `SecurityApiIntegrationTests.protectedEndpointWithoutTokenIsUnauthorized` |
| SEC-T02 | USER authentifié | `POST /api/wallet/me/credit` | 403 | 403 | PASS | `walletCreditIsDeniedForUserAndAllowedForAdmin` |
| SEC-T03 | utilisateur hors ressource | payer une annonce dont il n'est pas l'acheteur | exception d'accès | exception d'accès | PASS | `MarketplaceSecurityTests.unrelatedUserCannotPay` |
| SEC-T04 | A et B authentifiés | A envoie un `userId` de B à l'acceptation contrat | seul A est modifié | seul A est modifié (champ ignoré) | PASS | `contractUsesAuthenticatedUserAndIgnoresClientSuppliedUserId` |
| SEC-T05 | ADMIN authentifié | `POST /api/wallet/me/credit` | 200, crédit démo explicite | 200 | PASS | `walletCreditIsDeniedForUserAndAllowedForAdmin` |
| SEC-T06 | vidéo de A, utilisateur B | B modifie ou supprime | `SecurityException`/403 API | refus service | PASS | `VideoServiceAuthorizationTests` (5 cas owner/autre/admin) |
| SEC-T07 | payload invalide | inscription email invalide ou annonce prix négatif | 400 | 400 | PASS | `invalidRegistrationAndNegativeListingAreRejected` |
| SEC-T08 | annonce déjà payée | second paiement | conflit | `IllegalStateException` | PASS | `MarketplaceSecurityTests.paymentCannotBeRepeated` |

## Test manuel défensif restant : WebSocket

1. Créer une conversation dont seul A est membre.
2. Connecter B avec son JWT STOMP puis demander un abonnement à `/topic/conversations/{id}`.
3. Vérifier que le serveur interrompt/refuse la trame et que B ne reçoit aucun message.

Le contrôle est implanté dans `JwtChannelInterceptor` via `ConversationRepository.existsByIdAndParticipants_User_Id`. Le même contrôle côté émission est couvert par `MessageServiceAuthorizationTests.nonParticipantCannotSendMessageToConversation`; un test STOMP de bout en bout reste à ajouter ultérieurement.
