# Matrice données existant / cible

| Élément | Actuel | Cible | État | Impact / priorité | Issue |
|---|---|---|---|---|---|
| entités domaines MVP | 14 entités JPA | conserver/clarifier | CONFORME | faible | #5 |
| PK identity | Long/IDENTITY | conserver | CONFORME | faible | #5 |
| user email/username UQ | annotations | migration confirme | PARTIEL | moyen | #5/#10 |
| Like unique / auto-like | repository lookup, pas UQ | UQ + règle service | NON CONFORME | P0 | #2/#5 |
| ConversationUser unique | non observé | UQ composite | NON CONFORME | P1 | #5 |
| Transaction listing unique | garde service, pas UQ | UQ listing_id | PARTIEL | P0 | #5/#15 |
| migrations | Hibernate update/validate | Flyway versionné | NON CONFORME | P0 | #5/#10 |
| indexation | UQ seulement prouvés | index requêtes clés | PARTIEL | P1 | #5 |
| relation Chat–Listing | absente | absente MVP, évolution séparée | CONFORME | faible | ADR-006 |
| médias confidentialité | `/media/**` public | politique/validation | PARTIEL | P1 | #16 |
| rétention/RGPD | non prouvée | politique documentée | NON PROUVÉ | P1 | #20 |

Les gaps sont déjà couverts par #2, #5, #10, #15, #16, #20 ; aucune nouvelle issue n’est créée pour éviter les doublons.

