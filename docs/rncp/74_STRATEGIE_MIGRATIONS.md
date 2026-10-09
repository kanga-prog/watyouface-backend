# Stratégie de migrations

## Existant

| Environnement | Observé |
|---|---|
| dev | H2 mémoire, `spring.jpa.hibernate.ddl-auto=update` |
| local | PostgreSQL, `ddl-auto=validate` |
| prod | PostgreSQL, `ddl-auto=validate` |
| Flyway / Liquibase | non détecté dans `pom.xml` ni resources |
| schema.sql / data.sql | non détecté |

## Cible : Flyway

Flyway est recommandé car Spring Boot/PostgreSQL, versionnement SQL lisible, ordre `V1__...sql`, validation au démarrage et preuve RNCP facilement rejouable. Liquibase est une alternative valable mais apporterait changelogs XML/YAML sans avantage évident pour ce projet déjà orienté SQL/JPA.

1. sauvegarde de la base existante et baseline contrôlée ;
2. `V1__baseline_schema.sql` issu du schéma validé, jamais le script documentaire directement sans revue ;
3. `V2__constraints_indexes.sql` : uniques/CHECK/index ;
4. `V3__seed_demo.sql` réservé dev/test ;
5. `ddl-auto=validate` hors dev, pas `update` en prod ;
6. rollback logique : nouvelle migration corrective + sauvegarde/restauration testée, pas suppression aveugle.

La mise en œuvre Flyway relève des issues #5/#10 et n’est pas effectuée ici.

