# Présentation RNCP6

> Mise à jour Phase 17 — 18/10/2026 : les éléments ci-dessous sont des sources
> de contenu pour une présentation à régénérer, pas une modification du PPTX
> binaire. L'état courant est résumé dans `../17_DOCUMENTATION_FINALE.md`.

Emplacement de travail pour les éléments sources du support de présentation. Le livrable final PPTX V0.2 sera déposé dans `../livrables/` lorsqu'il sera fourni et vérifié.

## Éléments Phase 13 à intégrer au support oral

| Angle | Élément vérifiable | Formulation jury |
|---|---|---|
| Architecture | Spring Boot Docker Java 17 → Flyway → PostgreSQL → Hibernate validate | « Le conteneur ne fabrique pas le schéma en production : Flyway le versionne, Hibernate le contrôle. » |
| Choix technique | Flyway V1, `ddl-auto=validate`, image multi-stage non-root | « J'ai séparé la migration versionnée du contrôle ORM pour rendre le premier démarrage traçable. » |
| Difficulté | table `transaction` initialement quotée, incompatibilité de casse H2/Hibernate | « Un test d'unicité a révélé une divergence de nommage ; j'ai corrigé la migration plutôt que de masquer le test. » |
| Résolution | nom non quoté, V1 testée sur PostgreSQL 16.15 vide | « La solution est validée à la fois par la suite Maven et par PostgreSQL réel éphémère. » |
| Preuve | Flyway V1, 14 tables, `1:true`, validate, backend prod, 5/5 PostgreSQL | « Je peux montrer la preuve textuelle versionnée et la PR #33, sans exposer de secret. » |
| Limite | CI prouvée, mais pas de CD Railway automatique, de restauration, rollback ni observabilité; l'adoption V2/V3 d'une base non vide est validée localement | « Je distingue la CI et le redeploy prouvés des opérations DevOps qui restent à démontrer. » |

Compétences à citer avec nuance : C7 (schéma relationnel versionné et validé), C10 (déploiement Docker/migration/volume Railway prouvés), C11 (CI exécutée, démarche reproductible et moindre privilège). C10 et C11 restent partielles faute de preuve de CD Railway automatique, observabilité, restauration et rollback.

## Éléments Phase 13 — permissions du volume

| Angle | Élément vérifiable | Formulation jury |
|---|---|---|
| Difficulté | Volume `/app/media` attaché mais `AccessDeniedException` sur `avatars` ; Java déjà démarré sous `spring` | « Le volume persistant était bien présent, mais son propriétaire empêchait l'upload : j'ai traité la propriété plutôt que d'ouvrir les droits. » |
| Choix sécurité | entrypoint root limité à `/app/media`, puis `setpriv` vers `spring` | « Root n'est utilisé qu'une fois pour initialiser le point de montage ; l'application Java conserve le moindre privilège. » |
| Preuve production | `/app/media` `spring:spring`, Java PID 1 sous UID 999, fichier media présent après redeploy, `GET /api/users/me` = HTTP/2 401 avec en-têtes de sécurité | « Je peux montrer que le volume persiste après redeploy et que Java n'est jamais resté root. » |
| Limite | CI prouvée, mais CD Railway automatique, observabilité, sauvegarde/restauration et rollback non encore prouvés | « La mise en production du volume et la CI sont validées ; je ne déclare pas C11 complète sans exploitation mesurée. » |

## Éléments Phase 13 — CI/CD

| Angle | Élément vérifiable | Formulation jury |
|---|---|---|
| CI | Workflow GitHub Actions sur `push` et `pull_request` : Java 17, cache Maven, tests et build Docker | « Chaque modification est testée et l'image est reconstruite avant intégration. » |
| Preuve | Run [#38943060289](https://github.com/kanga-prog/watyouface-backend/actions/runs/38943060289) SUCCESS : Maven tests et Docker build du correctif Marketplace sur `main` | « Je conserve l'URL du run, les étapes et le commit contrôlé comme preuve rejouable. » |
| Sécurité | `contents: read`, credentials de checkout non persistés, aucun secret dans le YAML | « La CI n'a pas besoin d'un secret de production : elle applique le moindre privilège. » |
| CD | aucun job Railway dans le workflow ; merge validé vers `main`, puis intégration Railway ou tableau de bord | « Je distingue la CI prouvée du CD qui reste manuel tant que la connexion Railway et sa branche ne sont pas prouvées. » |

## Éléments Marketplace — robustesse image (production validée)

| Angle | Élément vérifiable | Formulation jury |
|---|---|---|
| Difficulté | WebP annoncé par MIME mais aucun lecteur ImageIO WebP, donc échec de décodage et HTTP 500 | « J'ai distingué le type déclaré de la capacité réelle du runtime à décoder l'image. » |
| Décision | JPEG/PNG/GIF/BMP, 10 Mo; pas de dépendance WebP | « J'ai retenu les formats réellement décodés et testés, plutôt que de promettre un format non exploitable. » |
| Résolution | fichier temporaire, déplacement après conversion JPG, `IllegalArgumentException` pour image illisible | « Une image corrompue devient un 400 explicite et ne publie pas de fichier partiel. » |
| Preuve | Tests réels JPEG/PNG/GIF/BMP et invalides; CI backend [#39165116209](https://github.com/kanga-prog/watyouface-backend/actions/runs/39165116209) SUCCESS; frontend 47 tests/build et CI/CD Hostinger [#39165084863](https://github.com/kanga-prog/watyouface-frontend/actions/runs/39165084863) SUCCESS; formats autorisés fonctionnels en production, captures à ajouter | « La règle est automatisée, versionnée, passée en CI et vérifiée en production; je distingue la validation fonctionnelle des captures encore attendues. » |
