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
| C11 DevOps | PARTIEL → preuve renforcée | CD Railway contrôlé, observabilité et rollback | branche/PR, build Docker, migrations testées, run GitHub Actions vert, rapports versionnés | 14/10 | élevé |

## Règle de couverture

Aucune compétence ne peut être déclarée « complète » avant de disposer d'au moins une preuve versionnée, une preuve d'exécution ou de démonstration, et une explication prête pour l'entretien. Les dates sont des objectifs de jalon, non une affirmation de réalisation.

### Mise à jour Phase 13

Preuves nouvelles : `evidence/backend/BE-DATA-03-flyway-railway-baseline.txt`, `evidence/backend/BE-DEP-03-railway-media-volume-permissions.txt`, `evidence/backend/BE-DEVOPS-01-github-actions-backend-ci.txt`, `102_DOCKER_RAILWAY_BACKEND.md`, `103_MIGRATIONS_POSTGRESQL_PRODUCTION.md`, V1 Flyway, commits `934b650` / `db23290` / `ba705c4` / `01a8919`, PR #33. C7 est démontrée pour le schéma initial sur base vide. C10 est renforcée par la preuve Railway du volume, de l'UID Java `spring` et de la persistance après redeploy. C11 est renforcée par la CI GitHub Actions verte, le déploiement reproductible et le moindre privilège, mais reste **PARTIELLE** sans CD Railway automatique, observabilité, sauvegarde/restauration et rollback prouvés.

### Correctif upload Marketplace — preuve de production (17/10)

Le correctif `e892ae8` est sur `main`, poussé et validé par la CI GitHub Actions [run 38943060289](https://github.com/kanga-prog/watyouface-backend/actions/runs/38943060289) : Maven tests et build Docker **SUCCESS**. `evidence/backend/BE-API-02-marketplace-image-upload.txt` consigne les 7 scénarios HTTP (JPEG, PNG, WebP, MIME arbitraire, vide, taille, image illisible) et les garanties de stockage : fichier temporaire dans `media/marketplace/`, déplacement seulement après conversion JPG réussie, nettoyage en cas d'échec et protection de chemin `MediaStorageService` conservée. Le redéploiement Railway et la validation de production confirment JPEG/PNG PASS et WebP volontairement refusé en HTTP 400. Le frontend est aligné (JPEG/PNG, 10 Mo, WebP refusé avant envoi), avec 35 tests et build Vite PASS, puis pipeline Hostinger déclenché et fonctionnement production confirmé par l'utilisateur. Cette preuve renforce C2 (validation UI), C3 (validation sécurisée d'un composant métier), C8 (stockage borné), C9 (plan de tests), C10 (déploiement documenté et vérifié) et C11 (CI exécutée et mise en production contrôlée). C10 et C11 restent **PARTIELLES** : ni CD Railway automatique, ni observabilité, sauvegarde/restauration ou rollback testé ne sont prouvés.
