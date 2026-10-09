# Phase 10 — Backend / métier / sécurité

Date de réévaluation : 08/10/2026. Périmètre : backend WatYouFace; aucun code frontend modifié. Les changements de cette phase restent mêlés aux modifications préexistantes; aucun commit/PR n'a été créé.

## 1. Objectif et baseline

La phase vise à rendre les contrôles métier vérifiables et à lever les derniers gaps paiement/PostgreSQL/STOMP/API. Baseline avant cette passe : 23 tests réussis. Aucun changement utilisateur préexistant n'a été supprimé.

## 2. Architecture backend

Spring Boot 3.3.5 / Java 17, couches Controller → Service → Repository/JPA → PostgreSQL. Les contrôles d'identité, de rôle, d'ownership et de membership relèvent du backend. Le profil MockMvc reste sur H2; les scénarios transactionnels dédiés activent un contexte `local` contre PostgreSQL réel.

## 3. Services métier et contrôles d'accès

- Auth : contrat actif requis avant émission du JWT; MockMvc couvre login valide, mauvais mot de passe et contrat non accepté.
- Contrat : identité issue de l'authentification, non du `userId` de requête.
- Posts : création avec auteur authentifié; modification/suppression propriétaire ou règle ADMIN; endpoints API testés pour propriétaire/non-propriétaire.
- Likes : la vérification API a mis au jour que le principal JWT est un ID alors que le service cherchait un username. `LikeController`/`LikeService` utilisent maintenant l'ID authentifié. Like d'un autre USER = 200; self-like = 403; routes legacy = 410.
- Chat REST : lecture/envoi réservés aux participants; non-membre reçoit 403. STOMP : le test direct de l'intercepteur permet SUBSCRIBE membre et bloque non-membre. Le handler ignore l'identifiant sender du payload et utilise le principal.

## 4. Validation et erreurs

`MessageDTO` contrôle le contenu non blanc et la longueur maximale 2000; le service applique une garde commune, REST utilise validation DTO et le handler STOMP valide également les entrées. MockMvc vérifie 400/401/403/404/409 sur les scénarios existants. Un test dédié du corps générique 500 et l'harmonisation des réponses de tous les anciens contrôleurs restent limités/non couverts.

## 5. Marketplace et paiement

Les transitions valides et refus d'acteur/état invalide sont couverts au niveau service. Paiement reste simulé. Le paiement conserve `@Transactional`, verrou pessimiste du listing et garde applicative `existsByListing_Id`. Une contrainte DB `uq_transaction_listing_id` a été ajoutée à l'entité et appliquée par le script SQL manuel versionné.

Avant migration, PostgreSQL contenait 11 transactions pour 11 listings distincts et aucun doublon. Après migration, le catalogue a confirmé l'unicité; une insertion de doublon a été rejetée avec SQLSTATE 23505 et sans ligne persistée. Les tests PostgreSQL réels prouvent : commit cohérent (100→70 acheteur, 0→30 vendeur, une transaction, listing PAID), rollback total après une exception injectée après le transfert, unicité, une seule réussite sur deux tentatives simultanées et refus/absence d'écriture avec solde nul.

## 6. Tests et résultats

- Baseline : `./mvnw clean test` — 23/23 PASS.
- Suite H2/MockMvc + unités : `./mvnw clean test` sans variables PostgreSQL — 35 tests, 30 PASS, 0 échec/erreur, 5 tests PostgreSQL conditionnels ignorés; BUILD SUCCESS, 50.961 s.
- PostgreSQL : `PostgresPaymentIntegrationTests` dédié contre PostgreSQL 16.15 — 5/5 PASS, 0 ignoré. Il couvre commit, rollback, contrainte, concurrence et solde insuffisant.
- H2 et PostgreSQL sont donc rapportés distinctement; la suite générale ne force pas les tests locaux PG afin que les tests MockMvc utilisent H2.
- STOMP : 3 tests unitaires sur les véritables interceptor/handler/gardes; aucune connexion client-broker WebSocket end-to-end.

## 7. PostgreSQL et migration

Base locale `watyouface_db`, utilisateur `watuser`, PostgreSQL 16.15. Script exécuté : `src/main/resources/db/manual/V1__unique_transaction_listing.sql`, après précontrôle sans doublons. La contrainte est présente et le test d'insertion incompatible la prouve directement. Le mécanisme reste un script manuel (pas Flyway/Liquibase); sa procédure doit être rejouée/documentée pour un nouvel environnement. Les fixtures d'intégration ont des identifiants uniques et sont supprimées après chaque cas.

## 8. Fichiers de preuve et recette

- `docs/rncp/85_RECETTE_API.md` : résultats par scénario et niveau testé.
- `docs/rncp/evidence/backend/BE-PAY-01-postgres-integration.txt`
- `docs/rncp/evidence/backend/BE-DATA-02-postgres-unique-payment.txt`
- `docs/rncp/evidence/backend/BE-API-01-mockmvc-results.txt`
- `docs/rncp/evidence/backend/BE-STOMP-01-component-tests.txt`
- `docs/rncp/evidence/backend/test-results.txt`

## 9. Écarts résiduels

1. Pas de test avec client STOMP/broker de bout en bout (membership/validation sont testés au niveau composant réel).
2. Transitions Marketplace complètes testées au niveau service, mais pas toutes via MockMvc; le paiement HTTP existant est aussi couvert pour conflit d'état.
3. Gestion centralisée 500 générique présente, mais assertion dédiée de non-divulgation non ajoutée; anciens contrôleurs à harmoniser.
4. Migration SQL versionnée manuellement, sans orchestration Flyway/Liquibase ni essai de reconstruction vierge.
5. Worktree préexistant très mélangé : aucun commit/PR isolable sans tri humain (écart C4).

## 10. Correspondance RNCP

| Compétence | Preuve | Appréciation |
|---|---|---|
| C3 | contrôles auth, ownership, Like, chat, transitions; MockMvc et services | renforcée, règles métier sécurisées testées |
| C6 | séparation contrôleurs/services/repositories, verrou et transactions existants | renforcée |
| C7 | modèle transactionnel, migration et contrainte PostgreSQL vérifiée | renforcée par preuve réelle |
| C8 | JPA/repository, contrainte, commit/rollback/concurrence sur PostgreSQL | renforcée |
| C9 | 30 tests suite générale + 4 intégrations PostgreSQL + recette API traçable | renforcée |
| AppSec | auth, ownership, validation, membership, anti-double-paiement, erreurs | améliorée; STOMP transport E2E et contrôleurs anciens restent à traiter |

## 11. Conclusion et décision

Les écarts P0 liés à la persistance PostgreSQL, la contrainte anti-double-paiement, le commit/rollback/concurrence, le flux Post/Like et les contrôles STOMP au niveau interceptor/handler sont désormais étayés par des tests. **Clôture fonctionnelle / RNCP Phase 10 : OUI**, avec les limites non bloquantes ci-dessus explicitement conservées. Cela n'affirme pas une recette STOMP end-to-end ni une uniformité complète de toutes les erreurs historiques.

**Clôture Git / PR : NON.** La branche est `rncp6/fix-dev-start`; le worktree contient de nombreux changements applicatifs et documentaires antérieurs non isolables sans tri. Aucun commit/push/PR n'a été créé. Ne pas lancer la Phase 11 avant décision de l'utilisateur sur les suites, conformément au cadrage Phase 10B.
