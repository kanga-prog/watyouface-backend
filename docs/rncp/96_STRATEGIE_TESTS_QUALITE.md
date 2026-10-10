# Stratégie de tests et qualité — Phase 12

Date : 09/10/2026. Objectif : relier exigences, niveaux de test, résultats réels et limites. Aucun pourcentage de couverture n’est déduit du nombre de tests.

## Inventaire backend

| Classe de test | Classification | Domaine / portée observée |
|---|---|---|
| `AuthCookieFactoryTests` | unitaire, sécurité | attributs cookie locaux/prod, expiration et contrainte Secure/SameSite |
| `CookieAuthenticationIntegrationTests` | intégration, API, sécurité | login, cookie, routes protégées, logout, CSRF, CORS, persistance logique multi-requête |
| `CookieJwtHandshakeTests` | intégration composant, sécurité | handshake WebSocket à partir du cookie et identité authentifiée |
| `LikeServiceAuthorizationTests` | unitaire/service, sécurité | auto-like interdit |
| `LoginRateLimiterTests` | unitaire, sécurité | seuil, fenêtre expirée via horloge contrôlée, isolation client |
| `MarketplaceSecurityTests` | unitaire/service, sécurité | acteur, paiement répété et état invalide |
| `MarketplaceTransitionTests` | unitaire/service | cycle d’état, acteur vendeur et transition invalide |
| `MediaUploadSecurityTests` | service/intégration, sécurité | MIME refusé avant persistance/écriture |
| `MessageServiceAuthorizationTests` | unitaire/service, sécurité | non-participant interdit |
| `MessageValidationTests` | unitaire/service | contenu vide/longueur et garde partagée REST/STOMP |
| `PostOwnershipTests` | unitaire/service, sécurité | propriétaire/admin et droits de modification/suppression |
| `PostgresPaymentIntegrationTests` | intégration, BDD réelle | commit, rollback, solde insuffisant, unicité et concurrence; 5 tests ignorés lors de l’exécution présente faute de configuration PostgreSQL |
| `SecurityApiIntegrationTests` | intégration, API, sécurité (MockMvc) | auth/contrat, 401/403/400/404/409/410, posts, likes, chat, admin, CORS/headers |
| `StompAuthorizationTests` | intégration composant, sécurité | autorisation SUBSCRIBE/SEND et garde de contenu via interceptor/handler/service, sans client broker navigateur E2E |
| `VideoServiceAuthorizationTests` | unitaire/service, sécurité | ownership/admin sur modification/suppression vidéo |

`./mvnw clean test` : 61 tests comptabilisés, 56 réussis, 0 échec, 0 erreur, 5 ignorés. Les 5 ignorés appartiennent à `PostgresPaymentIntegrationTests`, conditionnels à une configuration PostgreSQL réelle.

## Inventaire frontend

| Fichier | Classification | Couverture observée |
|---|---|---|
| `src/components/LoginForm.test.jsx` | frontend/composant | login cookie, erreur API, absence de stockage JWT |
| `src/utils/api.test.js` | frontend/unitaire/transport | credentials cookie et header CSRF sur lecture/mutation, absence Bearer |
| `src/components/post/LikeButton.test.jsx` | frontend/composant | auto-like non proposé, interaction admissible |
| `src/components/chat/ChatList.test.jsx` | frontend/composant | état vide, sélection clavier/action via bouton |
| `src/components/chat/ChatWindow.test.jsx` | frontend/composant | affichage date valide, date absente/invalide sans `Invalid Date` |
| `src/components/marketplace/CreateListingDialog.test.jsx` | frontend/composant | validation, soumission, erreur API |
| `src/components/marketplace/ListingStatusBadge.test.jsx` | frontend/composant | libellés de statut et statut inconnu explicite |

Résultat : Vitest 7 fichiers, 16 tests PASS. Aucun test automatisé spécifique Profil n’a été recensé. Il n’existe pas de parcours E2E navigateur automatisé dans l’inventaire courant.

## Exécution et outils réellement présents

- Backend : Maven Wrapper, Surefire, JUnit 5, Spring Boot Test/MockMvc, Mockito; H2 pour le contexte de tests courant; tests PostgreSQL dédiés conditionnels.
- Frontend : ESLint, Vitest avec jsdom, Vite build, `npm audit --omit=dev`.
- Aucun JaCoCo, Vitest coverage, Istanbul/c8 configuré; aucune couverture chiffrée mesurée.
- Aucun SonarQube ou scanner qualité équivalent présent dans ce périmètre.

## Niveaux et limites

| Niveau | État | Limite |
|---|---|---|
| Unitaire/service | présent | couverture métier ciblée, pas de couverture chiffrée |
| Intégration/API | présent via Spring Boot Test et MockMvc | serveur HTTP externe réel non utilisé par MockMvc |
| Sécurité | présent pour les contrôles cités dans la matrice | pas d’audit exhaustif/intrusion |
| BDD réelle PostgreSQL | preuve historique 5/5 du 08/10/2026; NON REJOUÉ dans la Phase 12 | service local sans réponse et variables DB/JWT absentes |
| Frontend | 16 tests composants/transport PASS | pas de navigateur réel dans la suite automatisée |
| E2E | non couvert | aucun Playwright/Cypress/browser E2E recensé |
| Manuel | captures d’interface réelles Phase 9 disponibles; recette cookie DevTools toujours À FAIRE | pas de navigateur graphique dans l’environnement actuel |

## Couverture future

Recommandation, sans l’introduire dans cette phase : ajouter JaCoCo pour Java et le fournisseur de couverture Vitest (v8 ou Istanbul) avec seuils décidés après une baseline. Distinguer lignes/instructions et branches; ne pas prendre un pourcentage global comme substitut à la traçabilité exigences/scénarios.

## Qualité et suites exécutées

Les sorties exactes et sans secrets sont conservées dans `evidence/tests/`. Le lint a un avertissement Hook préexistant dans `Admin.jsx` (dépendance `query` manquante), mais aucun échec. `npm audit --omit=dev` a retourné 0 vulnérabilité runtime.
