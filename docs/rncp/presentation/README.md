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
