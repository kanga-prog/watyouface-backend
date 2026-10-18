# Dossier projet

> Mise à jour Phase 19 — 18/10/2026 : la source complète du futur Dossier Projet
> est désormais `../19_DOSSIER_PROJET_FINAL.md`. Ce répertoire conserve les
> contenus Phase 13 utiles en annexe historique. Aucun livrable DOCX binaire
> n'est modifié dans cette phase.

Emplacement de travail pour les éléments sources du dossier projet RNCP6. Le livrable final DOCX V0.2 sera déposé dans `../livrables/` lorsqu'il sera fourni et vérifié.

## Contenu source à intégrer — Phase 13

### Architecture et déploiement

Le backend est conteneurisé avec une image multi-stage Java 17 : construction Maven dans un JDK, runtime JRE, utilisateur non-root `spring`, port Railway `${PORT:8080}` et secrets injectés par variables. PostgreSQL reste un service externe ; les médias locaux exigent un volume `/app/media` pour survivre au redeploy.

### Base de données et décision

Le profil production conserve `ddl-auto=validate`. Flyway V1 crée le schéma relationnel initial avant la validation Hibernate : 14 tables, PK/FK/index, checks Marketplace et contrainte `uq_transaction_listing_id`. Cette décision rend une base Railway neuve reproductible sans utiliser `create` ou `update`.

### Difficulté et résolution

Une première baseline créait `transaction` avec des guillemets : H2 la distinguait de la table attendue par Hibernate et le test d'unicité ne déclenchait pas. La V1 utilise désormais le nom non quoté, validé par les tests H2 et PostgreSQL.

### Résultats et limites

Sur PostgreSQL 16.15 Docker vide : Flyway V1 PASS, 14/14 tables, historique `1:true`, Hibernate validate PASS, backend prod démarré et tests PostgreSQL 5/5 PASS. V2/V3 ont aussi été validées sur une base PostgreSQL 16 contenant 37 listings : aucune donnée supprimée et 0 NULL pour `created_at`/`hidden`. Le redeploy Railway avec volume et persistance media est prouvé. La CI backend actuelle est prouvée par le run GitHub Actions 39165116209 (Maven tests et build Docker SUCCESS). La restauration de sauvegarde, le rollback et l'observabilité restent à produire; le CD Railway n'est pas automatique. C7 est renforcée; C10/C11 restent partielles.

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

### Robustesse de l'upload Marketplace — évolution historisée et état actuel

L'incident d'upload venait d'un écart entre le contrat et le runtime :
`image/webp` était accepté par le MIME mais aucun lecteur ImageIO WebP n'était
présent. Thumbnailator échouait alors à lire certaines images et l'exception
arrivait au handler 500. Le premier correctif a limité le contrat à JPEG/PNG.
La politique actuelle, validée ensuite sur Feed et Marketplace, accepte
JPEG/PNG/GIF/BMP à 10 Mo, refuse WebP/AVIF/HEIC et conserve le contrôle
anti-path traversal. Elle convertit les images lisibles en JPG dans
`media/marketplace/` et ne publie le fichier final qu'après conversion réussie.
Les erreurs de décodage deviennent HTTP 400 JSON avec un message utilisateur.

Les tests HTTP historiques couvrent JPEG, PNG, WebP, MIME arbitraire, vide,
taille excessive et contenu illisible. Les tests ultérieurs couvrent aussi de
vrais GIF/BMP et les erreurs Feed/Marketplace. La CI backend actuelle
[#39165116209](https://github.com/kanga-prog/watyouface-backend/actions/runs/39165116209)
confirme Maven et Docker SUCCESS. Les quatre formats autorisés sont fonctionnels
validés en production; les captures visuelles restent à ajouter. C10/C11 restent
partielles : CD Railway automatique, observabilité, sauvegarde/restauration et
rollback restent à démontrer.

Le frontend applique le contrat actuel avant envoi (JPEG/PNG/GIF/BMP, 10 Mo;
WebP refusé), avec 47 tests et build Vite PASS. Son pipeline GitHub Actions vers
Hostinger est prouvé par le run 39165084863 SUCCESS.
