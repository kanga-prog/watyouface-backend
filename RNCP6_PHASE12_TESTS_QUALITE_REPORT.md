# Phase 12 — Tests et qualité

Date : 09/10/2026

## 1. Objectif

Consolider les preuves de non-régression, relier les tests présents aux exigences RNCP, et déclarer explicitement les limites d’intégration et de mesure.

## 2. État Git

Backend : branche `rncp6/tests-quality`, créée depuis `main` à jour après fusion de la PR #28. Frontend : `main` à jour après fusion de la PR #5; `updateAvatars.js` reste local et n’est pas inclus. Aucun changement applicatif frontend n’a été fait dans cette phase.

## 3. Inventaire et classification

Voir `docs/rncp/96_STRATEGIE_TESTS_QUALITE.md` : 15 classes backend (service/unitaire, intégration/API, sécurité, PostgreSQL conditionnel) et 7 fichiers frontend (composants/transport). Classification exhaustive par classe de test et limites incluse dans ce document.

## 4. Parcours critiques

AUTH, FEED, CHAT, MARKETPLACE et PROFIL sont PARTIELS comme parcours complets : les comportements critiques ont des tests API, service ou composants ciblés, mais aucun scénario E2E navigateur ne relie ces écrans et services. Le Profil ne possède pas de test composant dédié. Détail dans `95_MATRICE_EXIGENCES_TESTS.md`.

## 5. Contrôles négatifs

Tests présents pour 400 (validation), 401 (auth absente/invalide/expirée), 403 (rôle/ownership/membership/self-like), 409 (conflit métier/état), 410 (routes Like legacy), CSRF absent, message invalide, paiement répété et concurrence PostgreSQL. Aucun endpoint/test 422 n’a été recensé; les entrées invalides utilisent 400.

## 6. Backend

Commande : `./mvnw clean test` — BUILD SUCCESS; 61 tests comptabilisés, 56 réussis, 0 échec, 0 erreur, 5 ignorés. Les cinq tests ignorés sont ceux de `PostgresPaymentIntegrationTests`, qui nécessitent PostgreSQL et ses variables de connexion. Voir `evidence/tests/backend-test-summary.txt`.

## 7. PostgreSQL

État courant : NON REJOUÉ. `pg_isready` rapporte qu’aucun serveur local ne répond; `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` et `JWT_SECRET` ne sont pas définis. Dernier résultat réel connu : 5/5 PASS sur PostgreSQL 16.15, le 08/10/2026, selon `evidence/backend/BE-PAY-01-postgres-integration.txt`. Ce résultat historique n’est pas présenté comme une exécution Phase 12.

## 8. Frontend

Lint : exit 0, zéro erreur et un avertissement Hook dans `Admin.jsx`. Vitest : 7 fichiers, 16 tests PASS. Build Vite : PASS. `npm audit --omit=dev` : 0 vulnérabilité runtime. Les commandes et sorties sont résumées dans `evidence/tests/`.

## 9. Couverture chiffrée

NON MESURÉE. Aucun JaCoCo/Vitest coverage/Istanbul/c8 n’est configuré. Recommandation future : JaCoCo backend et Vitest coverage frontend, après définition d’une baseline/seuils.

## 10. E2E et recette manuelle

Aucun E2E navigateur automatisé recensé. Les tests MockMvc et composants ne sont pas qualifiés de E2E. Les captures réelles UI de Phase 9 restent des preuves manuelles antérieures; la recette Auth Cookie DevTools (`94_RECETTE_MANUELLE_AUTH_COOKIE.md`) reste À FAIRE MANUELLEMENT.

## 11. Qualité et outils

Utilisés : Maven/Surefire/JUnit 5, Spring Boot Test/MockMvc, Mockito, tests PostgreSQL conditionnels; ESLint, Vitest/jsdom, Vite build et npm audit. Aucun SonarQube ni outil non présent n’est déclaré.

## 12. Mapping RNCP

- C3 : scénarios métier ciblés et contrôles d’accès.
- C7/C8 : preuve PostgreSQL historique et tests dédiés, mais non rejoués lors de cette passe.
- C9 : suites exécutées, résultats, cas négatifs et traçabilité documentés.
- C10 : stratégie, matrices et preuves reproductibles.
- C11 : hygiène de qualité partielle; couverture chiffrée, E2E et contrôle continu qualité restent des améliorations.

## 13. Conclusion et limites

Les suites backend/frontend courantes sont vertes. L’exécution PostgreSQL réelle et la recette navigateur ne sont pas rejouées; la couverture chiffrée et les E2E sont absents. Aucun de ces éléments n’est transformé en PASS par extrapolation. La Phase 12 est clôturable pour le périmètre de tests automatisés exécutables ici, avec ces limites explicites. Branche poussée `rncp6/tests-quality`; PR #29 vers `main`, ouverte et non fusionnée.

Étape suivante : PHASE 13 — DÉPLOIEMENT.
