# Matrice exigences – tests et preuves

Date de contrôle : 09/10/2026. « Couvert » signifie qu’un test automatisé pertinent existe; cela ne signifie pas qu’un parcours E2E navigateur a été exécuté.

| Parcours / exigence | Preuve automatisée observée | Contrôle négatif pertinent | Évaluation du parcours |
|---|---|---|---|
| Auth — AUTH-001 à AUTH-006; US-AUTH-001/002/003 | `SecurityApiIntegrationTests`, `CookieAuthenticationIntegrationTests`, `AuthCookieFactoryTests`, `LoginRateLimiterTests`; `LoginForm.test.jsx`, `api.test.js` | 401 sans cookie/invalide/expiré; contrat requis; CSRF absent; limitation de débit 429 | PARTIEL — flux API/UI composant testé, pas de navigateur E2E; recette manuelle cookie à faire |
| Profil — PROFILE-001/002/003; US-PROFILE-001/002 | endpoints d’identité dans `SecurityApiIntegrationTests`; transport commun `api.js`; aucune suite dédiée au formulaire Profil recensée | accès privé non authentifié (401); règles d’accès dépendantes endpoint | PARTIEL — pas de test composant Profil ni E2E spécifique |
| Feed — POST-001/002/003, COMMENT-001, LIKE-001; US-POST-001/002/003, US-COMMENT-001, US-LIKE-001 | `SecurityApiIntegrationTests`, `PostOwnershipTests`, `LikeServiceAuthorizationTests`; `LikeButton.test.jsx`, `api.test.js` | propriétaire tiers refusé 403; auto-like refusé; payload invalide 400; endpoints Like legacy 410 | PARTIEL — API et composant Like testés; création/commentaires/édition ne sont pas un parcours UI E2E |
| Chat — CHAT-001/003/004/005; US-CHAT-001/002 | `SecurityApiIntegrationTests`, `MessageServiceAuthorizationTests`, `MessageValidationTests`, `StompAuthorizationTests`, `CookieJwtHandshakeTests`; `ChatList.test.jsx`, `ChatWindow.test.jsx`, `api.test.js` | non-membre 403/refus d’abonnement; contenu vide/trop long rejeté; destination SEND non permise refusée | PARTIEL — composants/API et interceptor/handler couverts; aucun client STOMP navigateur E2E |
| Marketplace — MARKET-001/003/004/005/006; US-MARKET-001 à 005 | `MarketplaceTransitionTests`, `MarketplaceSecurityTests`, `SecurityApiIntegrationTests`, `PostgresPaymentIntegrationTests`; `CreateListingDialog.test.jsx`, `ListingStatusBadge.test.jsx`, `api.test.js` | 404 ressource absente; 409 état/conflit; acteur non autorisé; paiement répété refusé; solde insuffisant | PARTIEL — transitions et composants testés séparément; PostgreSQL non rejoué dans cette exécution; pas de parcours navigateur complet |
| Wallet démo — WALLET-001/002; US-WALLET-001 | `SecurityApiIntegrationTests`, `MarketplaceSecurityTests`, `PostgresPaymentIntegrationTests` | USER refusé sur crédit admin; paiement répété et solde insuffisant refusés | PARTIEL — données PostgreSQL antérieures probantes, test courant conditionnel ignoré |

## Réponses négatives et limites de statut

| Réponse / cas | Preuve actuelle |
|---|---|
| 400 | validation inscription, message et listing dans `SecurityApiIntegrationTests` / `MessageValidationTests` |
| 401 | absence, invalidité et expiration du cookie/JWT dans `CookieAuthenticationIntegrationTests` et `SecurityApiIntegrationTests` |
| 403 | rôle, ownership, self-like, contrat et membership dans `SecurityApiIntegrationTests` et tests de services |
| 409 | transition/paiement invalide ou conflit métier dans `SecurityApiIntegrationTests` |
| 410 | routes Like legacy dans `SecurityApiIntegrationTests` |
| 422 | aucun endpoint/test 422 recensé; le projet utilise 400 pour les payloads invalides |
| Double paiement / concurrence | test PostgreSQL historique 5/5 (preuve existante); non rejoué cette fois, faute de service PostgreSQL local et de variables requises |

## Classification

Inventaire détaillé des classes, types et limites dans `96_STRATEGIE_TESTS_QUALITE.md`. Aucun test navigateur E2E automatisé n’a été trouvé. Les recettes manuelles et captures précédentes sont des preuves manuelles distinctes.
