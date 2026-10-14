# Dossier projet

Emplacement de travail pour les éléments sources du dossier projet RNCP6. Le livrable final DOCX V0.2 sera déposé dans `../livrables/` lorsqu'il sera fourni et vérifié.

## Contenu source à intégrer — Phase 13

### Architecture et déploiement

Le backend est conteneurisé avec une image multi-stage Java 17 : construction Maven dans un JDK, runtime JRE, utilisateur non-root `spring`, port Railway `${PORT:8080}` et secrets injectés par variables. PostgreSQL reste un service externe ; les médias locaux exigent un volume `/app/media` pour survivre au redeploy.

### Base de données et décision

Le profil production conserve `ddl-auto=validate`. Flyway V1 crée le schéma relationnel initial avant la validation Hibernate : 14 tables, PK/FK/index, checks Marketplace et contrainte `uq_transaction_listing_id`. Cette décision rend une base Railway neuve reproductible sans utiliser `create` ou `update`.

### Difficulté et résolution

Une première baseline créait `transaction` avec des guillemets : H2 la distinguait de la table attendue par Hibernate et le test d'unicité ne déclenchait pas. La V1 utilise désormais le nom non quoté, validé par les tests H2 et PostgreSQL.

### Résultats et limites

Sur PostgreSQL 16.15 Docker vide : Flyway V1 PASS, 14/14 tables, historique `1:true`, Hibernate validate PASS, backend prod démarré et tests PostgreSQL 5/5 PASS. La suite Maven est à 79 tests, 0 échec, 0 erreur, 5 ignorés conditionnels. Le redeploy Railway avec volume et persistance media est maintenant prouvé ; la restauration de sauvegarde, le rollback, l'adoption d'une base non vide, la CI/CD et l'observabilité restent à produire. C7 est fortement prouvée pour le schéma neuf ; C10/C11 restent partielles.

Sources : `102_DOCKER_RAILWAY_BACKEND.md`, `103_MIGRATIONS_POSTGRESQL_PRODUCTION.md`, `evidence/backend/BE-DATA-03-flyway-railway-baseline.txt`, `evidence/backend/BE-DEP-03-railway-media-volume-permissions.txt`, PR #33.

### Incident de volume et principe du moindre privilège

Le volume Railway attaché à `/app/media` provoquait `AccessDeniedException` sur
`avatars`. La cause était structurelle : l'application tournait déjà sous
`spring`, donc ne pouvait pas réattribuer un montage présenté comme root. Le
correctif conserve Java non-root : un entrypoint root limité crée et attribue
uniquement `/app/media`, puis `setpriv` lance Java sous `spring`. Aucun secret
ni permission `777` n'est ajouté. La validation Railway de production montre
`/app/media` en `spring:spring`, Java PID 1 sous `spring` UID 999 et le fichier
`railway-persistence-test.txt` toujours présent après redeploy. Le smoke
`GET /api/users/me` retourne HTTP/2 401 attendu avec les en-têtes observés
`no-cache, no-store`, `nosniff` et `DENY`. C10 est ainsi renforcée par une
preuve d'exploitation réelle; C11 reste partielle sans preuve CI/CD,
observabilité, sauvegarde/restauration ou rollback.
