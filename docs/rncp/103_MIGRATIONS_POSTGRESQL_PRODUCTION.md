# Migrations PostgreSQL de production

## Objectif

Flyway initialise et fait évoluer le schéma PostgreSQL de WatYouFace de façon reproductible.

```text
Spring Boot
  ↓
Flyway : V1__baseline_schema.sql
  ↓
schéma PostgreSQL versionné
  ↓
Hibernate ddl-auto=validate
  ↓
application
```

## Choix de production

Le profil `prod` conserve `spring.jpa.hibernate.ddl-auto=validate`.

`validate` ne crée ni ne modifie les tables et fait échouer le démarrage si la base ne correspond plus aux entités JPA.

Les valeurs `create` et `update` ne sont pas utilisées en production : elles risquent respectivement de perdre ou de dériver les données.

Flyway s'exécute avant l'initialisation JPA. Sur une base vide, il crée `flyway_schema_history`, applique V1, puis Hibernate valide le résultat.

## Baseline V1

`src/main/resources/db/migration/V1__baseline_schema.sql` est la source exécutable du schéma initial.

Elle est dérivée des 14 entités JPA actuelles et de `docs/rncp/sql/001_schema_reference.sql`.

- `comment`, `contract`, `conversation_users`, `conversations` ;
- `likes`, `listings`, `messages`, `post` ;
- `transaction`, `user_contract`, `users`, `video`, `video_share`, `wallet`.

Elle contient les PK, FK, index, contraintes d'unicité dont `uq_transaction_listing_id`, et checks sur statuts Marketplace, rôles, montants et cibles commentaire/like.

Elle ne contient aucun compte, mot de passe, wallet ou jeu de données de démonstration.

Le script RNCP `docs/rncp/sql/002_seed_demo.sql` reste hors du chemin Flyway et n'est jamais exécuté en production.

Le script `src/main/resources/db/manual/V1__unique_transaction_listing.sql` est une trace manuelle historique hors de `db/migration` ; Flyway ne l'exécute pas. Son invariant est intégré à V1.

## Déploiement Railway

Configurer les variables Railway sans les committer :

| Variable | Rôle |
|---|---|
| `DB_URL` | URL JDBC PostgreSQL Railway. |
| `DB_USERNAME` | Utilisateur PostgreSQL. |
| `DB_PASSWORD` | Mot de passe PostgreSQL. |
| `JWT_SECRET` | Secret JWT requis par l'application. |
| `SPRING_PROFILES_ACTIVE` | Valeur `prod`. |
| `PORT` | Port injecté par Railway. |

Pour une base Railway neuve et vide, aucun script manuel n'est nécessaire : le premier déploiement applique V1.

Pour une base déjà peuplée sans historique Flyway, ne pas activer `baseline-on-migrate` sans revue : comparer le schéma, sauvegarder la base, puis définir une migration d'adoption spécifique.

## Évolutions et rollback

Après application dans un environnement partagé, un fichier de migration ne doit jamais être modifié : Flyway contrôle son checksum.

Chaque évolution se fait dans une nouvelle migration versionnée (`V2__...`, `V3__...`).

Flyway Community n'exécute pas de migration undo automatique.

Le rollback de production repose sur une sauvegarde PostgreSQL vérifiée avant déploiement et, lorsque possible, sur une migration corrective *forward*.

Une migration destructive doit être découpée et revue avant exécution.

## Vérifications reproductibles

Les validations suivantes ont été réalisées sur PostgreSQL 16.15 lancé dans un conteneur Docker éphémère, sans volume et avec une base vide :

1. démarrage du backend Docker avec `SPRING_PROFILES_ACTIVE=prod` ;
2. Flyway applique V1 avec succès ;
3. les 14 tables attendues sont présentes dans `public` ;
4. l'historique Flyway contient `1:true` ;
5. Hibernate `validate` passe et l'application démarre ;
6. `PostgresPaymentIntegrationTests` passe : 5 tests, 0 échec, 0 erreur, 0 ignoré.

Commandes de contrôle locales, avec valeurs de test injectées uniquement dans l'environnement du shell :

```bash
./mvnw clean test

DB_URL=jdbc:postgresql://localhost:5432/watyouface_migration_test \
DB_USERNAME=... DB_PASSWORD=... JWT_SECRET=... \
./mvnw -Dtest=PostgresPaymentIntegrationTests test

docker build -t watyouface-backend:rncp6 .
```

La base `watyouface_db` existante n'est pas utilisée pour cette validation.

## Couverture RNCP

- **C7 — base de données relationnelle :** schéma versionné, relations, contraintes, index et invariants Marketplace validés sur PostgreSQL.
- **C10 — préparation au déploiement :** démarrage Railway reproductible sur une base vide et variables documentées.
- **C11 — démarche DevOps :** migration automatisée, validation au démarrage, tests de base vide et procédure d'évolution/retour arrière documentées.

## Preuve et statut Phase 13

La preuve d'exécution est `evidence/backend/BE-DATA-03-flyway-railway-baseline.txt`. Elle consigne exclusivement les résultats réels : build Docker, V1 sur PostgreSQL 16.15 vide, 14 tables, historique Flyway, Hibernate validate, démarrage et 5/5 tests PostgreSQL.

Commits concernés : `934b650` et `db23290`, sur la PR backend #33 ouverte. Statut : **PASS** pour une base neuve reproductible ; **PARTIEL** pour Railway en exploitation, car le redeploy réel, la sauvegarde/restauration et l'adoption d'une base non vide ne sont pas exécutés dans cette phase.
