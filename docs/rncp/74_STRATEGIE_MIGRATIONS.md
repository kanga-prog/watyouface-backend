# Stratégie de migrations

## État réel — Phase 13 (2026-10-13)

| Élément | État vérifié |
|---|---|
| dev | H2 mémoire, `spring.jpa.hibernate.ddl-auto=update` |
| local / prod | PostgreSQL, `spring.jpa.hibernate.ddl-auto=validate` |
| Moteur de migration | Flyway 10.10.0 dans `pom.xml` |
| Migration active | `src/main/resources/db/migration/V1__baseline_schema.sql` |
| Base vide PostgreSQL 16.15 | PASS : V1, 14 tables, historique `1:true`, Hibernate validate et backend démarré |
| Données démo | absentes de V1 et jamais exécutées automatiquement en production |

## Décision

Flyway est retenu : le SQL versionné est lisible, l'ordre `V1__...sql` est déterministe et Spring Boot l'exécute avant Hibernate. Liquibase n'apporterait pas d'avantage démontré pour ce projet déjà orienté SQL/JPA.

`ddl-auto=validate` est conservé en production. Hibernate ne crée ni ne modifie le schéma ; il échoue si une migration ne correspond plus aux entités. `create` et `update` ne sont pas une stratégie de production.

## Baseline et exploitation

V1 crée les 14 tables des entités actuelles avec PK, FK, index et contraintes métier, notamment `uq_transaction_listing_id` et les checks de statuts Marketplace. Elle est prévue pour une base neuve et vide. La base de démonstration `docs/rncp/sql/002_seed_demo.sql` reste réservée au dev/test.

Pour une base existante sans historique Flyway, ne pas activer `baseline-on-migrate` aveuglément : sauvegarder, comparer le schéma puis créer une migration d'adoption revue. Après application partagée, une migration est immuable ; l'évolution passe par `V2__...` et suivantes.

## Difficulté, solution et limites

La première V1 quotait `transaction`, ce qui créait sous H2 une table sensible à la casse distincte de celle résolue par Hibernate. Le test d'unicité ne voyait donc pas la contrainte. La baseline crée désormais `transaction` sans guillemets ; `./mvnw clean test` repasse à 79 tests, 0 échec, 0 erreur, 5 ignorés et la suite PostgreSQL dédiée passe 5/5.

Le rollback est logique : migration corrective forward ou restauration d'une sauvegarde validée. Le redeploy Railway est désormais prouvé pour le backend et le volume media par `evidence/backend/BE-DEP-03-railway-media-volume-permissions.txt`; cette preuve ne remplace pas une procédure de sauvegarde/restauration ni une migration d'adoption de base non vide. La preuve Flyway de base vide reste `evidence/backend/BE-DATA-03-flyway-railway-baseline.txt`. Commits : `934b650`, `db23290`, `ba705c4`; PR backend #33 ouverte.

