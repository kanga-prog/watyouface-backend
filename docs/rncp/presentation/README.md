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
| Limite | pas de redeploy Railway réel, CI, restauration ou adoption de base non vide | « Je distingue la reproductibilité validée sur base neuve de l'exploitation Railway restante. » |

Compétences à citer avec nuance : C7 (schéma relationnel versionné et validé), C10 (préparation Docker/migration), C11 (démarche reproductible sur PR). C10 et C11 restent partielles faute de preuve de CI et de redeploy Railway réel.

## Éléments Phase 13 — permissions du volume

| Angle | Élément vérifiable | Formulation jury |
|---|---|---|
| Difficulté | Volume `/app/media` attaché mais `AccessDeniedException` sur `avatars` ; Java déjà démarré sous `spring` | « Le volume persistant était bien présent, mais son propriétaire empêchait l'upload : j'ai traité la propriété plutôt que d'ouvrir les droits. » |
| Choix sécurité | entrypoint root limité à `/app/media`, puis `setpriv` vers `spring` | « Root n'est utilisé qu'une fois pour initialiser le point de montage ; l'application Java conserve le moindre privilège. » |
| Preuve locale | montage temporaire écrivable après `chown`, processus Java UID 999, démarrage Spring Boot et HTTP 401 attendu | « Je peux montrer le processus Java non-root et l'écriture media, sans prétendre que le redeploy Railway a déjà été réalisé. » |
| Limite | volume Railway réel et persistance d'un upload après redeploy non encore vérifiés | « Le correctif est testé localement ; la preuve d'exploitation Railway reste l'étape suivante. » |
