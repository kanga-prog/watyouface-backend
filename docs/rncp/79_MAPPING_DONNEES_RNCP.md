# Mapping données ↔ RNCP

| Preuve | Compétence | Usage jury |
|---|---|---|
| inventaire, domaines, MCD/MLD/MPD | C7 | expliquer modèle relationnel et cardinalités |
| SQL de référence, contraintes, index, migrations | C7/C8 | démontrer intégrité et accès SQL/JPA |
| repositories/JPA/N+1/DTO | C8 | justifier persistance et performances |
| transaction/double paiement | C3/C7/C8/C9 | défendre intégrité métier |
| seed/recette future | C9 | données de test reproductibles |
| confidentialité/sécurité BDD | C6/C10 | expliquer protection et exploitation |

## État

La Phase 13 fournit une preuve technique supplémentaire : Flyway V1 a créé les 14 tables sur PostgreSQL 16.15 vide, Hibernate `validate` a démarré et les cinq tests PostgreSQL dédiés sont PASS. Cela renforce C7/C8 pour le périmètre schéma/versionnement ; l'adoption d'une base non vide et le redeploy Railway réel restent hors de cette preuve.

