# Présentation RNCP6

Emplacement de travail pour les éléments sources du support de présentation. Le livrable final PPTX V0.2 sera déposé dans `../livrables/` lorsqu'il sera fourni et vérifié.

## Éléments Phase 13 à intégrer au support oral

| Angle | Élément vérifiable | Formulation jury |
|---|---|---|
| Architecture | Spring Boot Docker Java 17 → Flyway → PostgreSQL → Hibernate validate | « Le conteneur ne fabrique pas le schéma en production : Flyway le versionne, Hibernate le contrôle. » |
| Choix technique | Flyway V1, `ddl-auto=validate`, image multi-stage non-root | « J'ai séparé la migration versionnée du contrôle ORM pour rendre le premier démarrage traçable. » |
| Difficulté | table `transaction` initialement quotée, incompatibilité de casse H2/Hibernate | « Un test d'unicité a révélé une divergence de nommage ; j'ai corrigé la migration plutôt que de masquer le test. » |
| Résolution | nom non quoté, V1 testée sur PostgreSQL 16.15 vide | « La solution est validée à la fois par la suite Maven et par PostgreSQL réel éphémère. » |
| Preuve | Flyway V1, 14 tables, `1:true`, validate, backend prod, 5/5 PostgreSQL | « Je peux montrer la preuve textuelle versionnée et la PR #33, sans exposer de secret. » |
| Limite | pas de CI/CD, restauration, rollback ni adoption de base non vide | « Je distingue le redeploy et le volume désormais prouvés des opérations DevOps restantes. » |

Compétences à citer avec nuance : C7 (schéma relationnel versionné et validé), C10 (déploiement Docker/migration/volume Railway prouvés), C11 (démarche reproductible et moindre privilège sur PR). C10 et C11 restent partielles faute de preuve de CI/CD, observabilité, restauration et rollback.

## Éléments Phase 13 — permissions du volume

| Angle | Élément vérifiable | Formulation jury |
|---|---|---|
| Difficulté | Volume `/app/media` attaché mais `AccessDeniedException` sur `avatars` ; Java déjà démarré sous `spring` | « Le volume persistant était bien présent, mais son propriétaire empêchait l'upload : j'ai traité la propriété plutôt que d'ouvrir les droits. » |
| Choix sécurité | entrypoint root limité à `/app/media`, puis `setpriv` vers `spring` | « Root n'est utilisé qu'une fois pour initialiser le point de montage ; l'application Java conserve le moindre privilège. » |
| Preuve production | `/app/media` `spring:spring`, Java PID 1 sous UID 999, fichier media présent après redeploy, `GET /api/users/me` = HTTP/2 401 avec en-têtes de sécurité | « Je peux montrer que le volume persiste après redeploy et que Java n'est jamais resté root. » |
| Limite | CI/CD, observabilité, sauvegarde/restauration et rollback non encore prouvés | « La mise en production du volume est validée ; je ne déclare pas C11 complète sans pipeline ni exploitation mesurée. » |
