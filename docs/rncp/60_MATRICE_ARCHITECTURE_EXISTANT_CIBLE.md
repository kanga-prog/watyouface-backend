# Matrice architecture existant / cible

| Sujet | Actuel | Cible | État | Écart prioritaire |
|---|---|---|---|---|
| séparation frontend/backend | deux dépôts React/Spring | conserver | CONFORME | documenter déploiement |
| couches backend | controllers/services/repositories/entities présents | responsabilités homogènes | PARTIEL | réduire variantes legacy / DTO |
| REST / DTO | REST riche, DTO partiels, Maps/variantes historiques | contrats stabilisés/OpenAPI | PARTIEL | issue #21 + contrats |
| JWT/env | JWT secret env + fail fast | rotation + politique token | PARTIEL | issue #4, localStorage |
| autorisation ownership | mécanismes `Authz`/services sur flux | chaque ressource sensible couverte | PARTIEL | Like/chat/legacy |
| WebSocket | JWT CONNECT + subscription participant | tests et envoi/lecture cohérents | PARTIEL | issue #3/#13 |
| PostgreSQL/JPA | entités/repositories, pas migrations preuve | migrations, contraintes/index | PARTIEL | issue #5/#10 |
| paiement | transaction + lock observés | unique BDD + tests concurrence | PARTIEL | migration/tests |
| médias | local `/media/**`, anti-traversal | validation/privé si nécessaire | PARTIEL | politique médias |
| erreurs/logs | 400/403/409 et no message framework | 401/404/500 normalisés | PARTIEL | handler/contrats |
| performance/éco | quelques requêtes pageable possibles | pagination, payload/médias optimisés | NON PROUVÉ | mesures futures |
| déploiement/CI | non produit à ce stade | reproductible et automatisé | NON PROUVÉ | C10/C11 |

## Mapping RNCP

| Décision / preuve | Compétence |
|---|---|
| diagramme, couches, ADR, flux | C6 |
| services/ownership/transitions/transactions | C3 |
| PostgreSQL/JPA/relations/migrations planifiées | C7/C8 |
| menaces, contrats, recette future | C9 |
| média/configuration/déploiement à produire | C10 |
| CI/observabilité à produire | C11 |
