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

Sur PostgreSQL 16.15 Docker vide : Flyway V1 PASS, 14/14 tables, historique `1:true`, Hibernate validate PASS, backend prod démarré et tests PostgreSQL 5/5 PASS. Le redeploy Railway avec volume et persistance media est prouvé. La CI backend est également prouvée par le run GitHub Actions 38943060289 (Maven tests et build Docker SUCCESS). La restauration de sauvegarde, le rollback, l'adoption d'une base non vide, le CD Railway automatique et l'observabilité restent à produire. C7 est fortement prouvée pour le schéma neuf ; C10/C11 restent partielles.

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
preuve d'exploitation réelle; C11 reste partielle sans preuve de CD Railway
automatique, d'observabilité, de sauvegarde/restauration ou de rollback.

### CI et limite du CD

La CI backend est versionnée dans GitHub Actions : à chaque push ou pull
request, elle prépare Java 17 avec cache Maven, lance `./mvnw clean test` puis
construit l'image Docker. Le run
[#38943060289](https://github.com/kanga-prog/watyouface-backend/actions/runs/38943060289)
est SUCCESS pour ces deux contrôles sur le correctif Marketplace désormais sur `main`. Le token GitHub est limité à la lecture et
aucun secret applicatif ou Railway n'est injecté dans le workflow.

Le CD Railway n'est pas automatisé dans ce dépôt : aucun job de déploiement,
token Railway ou credential de production n'est versionné. Après validation et
merge sur la branche contrôlée `main`, le déploiement doit être déclenché par
l'intégration Railway si elle est configurée, ou manuellement depuis son tableau
de bord. C11 est renforcée par la CI exécutée, mais reste partielle sans preuve
de CD automatique, d'observabilité et de rollback.

### Robustesse de l'upload Marketplace — validée en production

L'incident d'upload venait d'un écart entre le contrat et le runtime :
`image/webp` était accepté par le MIME mais aucun lecteur ImageIO WebP n'était
présent. Thumbnailator échouait alors à lire certaines images et l'exception
arrivait au handler 500. La décision est de limiter le produit à JPEG/PNG,
sans nouvelle dépendance. L'upload conserve la limite de 10 Mo et le contrôle
anti-path traversal, convertit les images lisibles en JPG dans
`media/marketplace/` et ne publie le fichier final qu'après conversion réussie.
Les erreurs de décodage deviennent HTTP 400 JSON avec un message utilisateur.

Sept scénarios HTTP ont été validés localement (JPEG, PNG, WebP, MIME arbitraire,
vide, taille excessive, contenu PNG illisible). Le correctif `e892ae8` est commité
sur `main`, poussé, et la CI GitHub Actions [run 38943060289](https://github.com/kanga-prog/watyouface-backend/actions/runs/38943060289)
confirme Maven tests et build Docker SUCCESS. Après redéploiement Railway,
`https://api.watyouface.com` répond ; les uploads JPEG et PNG sont validés et WebP
reste refusé proprement en HTTP 400. Cette preuve renforce C3, C8, C9, C10 et C11,
sans déclarer C10/C11 complètes : CD Railway automatique, observabilité,
sauvegarde/restauration et rollback restent à démontrer.

Le frontend applique le même contrat avant envoi (JPEG/PNG, 10 Mo, WebP refusé),
avec 35 tests et build Vite PASS. Son pipeline GitHub Actions vers Hostinger a été
déclenché et l'utilisateur confirme le fonctionnement des uploads en production.
