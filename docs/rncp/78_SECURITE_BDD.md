# Sécurité BDD

| Sujet | Cible / vigilance | État |
|---|---|---|
| compte BDD | utilisateur applicatif dédié, pas superuser, privilèges minimaux | À mettre en place/documenter |
| secrets | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` par environnement, hors Git | configuration env observée ; rotation/historique à traiter |
| réseau/TLS | TLS pour BDD distante, pare-feu, accès privé | NON PROUVÉ |
| migrations | compte migration contrôlé, review et backup avant évolution | cible Flyway |
| sauvegarde | sauvegarde chiffrée, test restauration, rétention définie | NON PROUVÉ |
| logs | pas de mot de passe/secret/PII excessive | à vérifier exploitation |
| test | H2/dev ou PostgreSQL isolé ; seed fictif | H2 dev observé ; seed documentaire créé |
| accès data | RLS non retenu à ce stade ; contrôle applicatif serveur obligatoire | décision à réévaluer |

Cette section est une cible d’exploitation sécurisée, non une déclaration que l’infrastructure existe déjà.

