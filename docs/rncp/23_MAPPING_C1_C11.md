# Mapping C1 à C11 — plan de preuves

| Compétence | État au 07/10 | Tâches restantes prioritaires | Preuves attendues | Date cible | Risque |
|---|---|---|---|---:|---|
| C1 Installer/configurer environnement | PARTIEL | variables, procédure locale, Docker | README/runbook, `.env.example`, Compose, capture lancement | 13/10 | moyen |
| C2 Développer interfaces | PARTIEL | wireframes, maquettes, UX feed/chat/marketplace, recette accessibilité | écrans, maquettes, tests composants, captures responsive | 15/10 | moyen |
| C3 Composants métier | PARTIEL | P0 contrat/Like/chat, validation feed/message, règles marketplace | services/controllers, ADR, tests 403/409 | 12/10 | critique |
| C4 Gestion projet | PARTIEL → renforcé | suivi backlog/risques/jalons/Git, décisions quotidiennes | documents 17–22, historique commits | 17/10 | faible |
| C5 Analyser besoins/maquetter | PARTIEL → analyse forte | validation besoins, wireframes/maquettes/prototype | docs 08–16, wireframes, prototype | 08/10–15/10 | moyen |
| C6 Architecture logicielle | PARTIEL | diagramme réel, ADR, flux REST/STOMP, responsabilités | architecture, ADR, diagrammes séquence | 09/10 | élevé |
| C7 Base relationnelle | PARTIEL → preuve renforcée | MCD/MLD/MPD, contraintes, migrations | V1 Flyway, PostgreSQL vide 14 tables, validate, tests 5/5 | 13/10 | moyen |
| C8 Accès données SQL/NoSQL | PARTIEL | sécuriser accès Like/chat, documenter JPA/transactions, migrations | repositories/services, tests BOLA, transactions | 12/10 | élevé |
| C9 Plans de tests | PARTIEL → renforcé | plan complet, jeu d'essai, P0/UI/E2E, rapport | cas, sorties, matrice, captures | 15/10 | critique |
| C10 Déploiement | PARTIEL → preuve renforcée | redeploy Railway, volume média, backup/rollback opérationnels | Docker backend, port/variables, Flyway base vide, runbook | 13/10 | moyen |
| C11 DevOps | PARTIEL → preuve renforcée | CI verte et observabilité | branche/PR, build Docker, migrations testées, rapports versionnés | 13/10 | élevé |

## Règle de couverture

Aucune compétence ne peut être déclarée « complète » avant de disposer d'au moins une preuve versionnée, une preuve d'exécution ou de démonstration, et une explication prête pour l'entretien. Les dates sont des objectifs de jalon, non une affirmation de réalisation.

### Mise à jour Phase 13

Preuves nouvelles : `evidence/backend/BE-DATA-03-flyway-railway-baseline.txt`, `102_DOCKER_RAILWAY_BACKEND.md`, `103_MIGRATIONS_POSTGRESQL_PRODUCTION.md`, V1 Flyway, commits `934b650` / `db23290`, PR #33. C7 est démontrée pour le schéma initial sur base vide. C10 et C11 restent **PARTIELLES** : aucun redeploy Railway réel, volume média, sauvegarde/restauration opérationnelle ni pipeline CI vert n'ont été prouvés.
