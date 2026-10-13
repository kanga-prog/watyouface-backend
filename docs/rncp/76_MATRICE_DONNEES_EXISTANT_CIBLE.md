# Matrice données existant / cible

| Élément | Actuel | Cible | État | Impact / priorité | Issue |
|---|---|---|---|---|---|
| entités domaines MVP | 14 entités JPA | conserver/clarifier | CONFORME | faible | #5 |
| PK identity | Long/IDENTITY | conserver | CONFORME | faible | #5 |
| user email/username UQ | annotations + V1 | contraintes versionnées | CONFORME SUR BASE VIDE | moyen | #5/#10 |
| Like unique / auto-like | repository lookup + V1 | UQ + règle service | PARTIEL | P0 | #2/#5 |
| ConversationUser unique | V1 | UQ composite | CONFORME SUR BASE VIDE | P1 | #5 |
| Transaction listing unique | garde service + V1 | UQ listing_id | CONFORME SUR BASE VIDE / TESTÉ | P0 | #5/#15 |
| migrations | Hibernate update/validate + Flyway V1 | Flyway versionné | CONFORME SUR BASE VIDE | P0 | #5/#10 |
| indexation | V1 crée index clés | mesure/explain à compléter | PARTIEL | P1 | #5 |
| relation Chat–Listing | absente | absente MVP, évolution séparée | CONFORME | faible | ADR-006 |
| médias confidentialité | `/media/**` public | politique/validation | PARTIEL | P1 | #16 |
| rétention/RGPD | non prouvée | politique documentée | NON PROUVÉ | P1 | #20 |

Les gaps sont déjà couverts par #2, #5, #10, #15, #16, #20 ; aucune nouvelle issue n’est créée pour éviter les doublons. La preuve V1/Flyway est `evidence/backend/BE-DATA-03-flyway-railway-baseline.txt`; elle ne vaut pas adoption d'une base Railway non vide.

