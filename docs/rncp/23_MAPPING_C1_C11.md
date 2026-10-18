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

### Lot ADMIN, wallet, modération et médias — état de preuve (18/10/2026)

| Compétence | Preuve nouvelle, réellement obtenue | État |
|---|---|---|
| C2 | Dashboard ADMIN : pagination/recherche, consultation utilisateurs/wallets/posts/commentaires/annonces, actions de modération et crédit wallet ; validation frontend des formats JPEG/PNG/GIF/BMP et de 10 Mo. | renforcée, PARTIEL |
| C3 | RBAC serveur, ADMIN limité à modération/wallet, cycle Marketplace réservé aux deux parties, protections dernier ADMIN et règles de masquage transactionnel. | renforcée, PARTIEL |
| C7 | Flyway V2 (`created_at`) et V3 (`hidden`) validées sur PostgreSQL 16 avec 37 annonces non vides, sans suppression et sans NULL résiduel. | renforcée, PARTIEL |
| C8 | Endpoint de crédit unique sous `/api/admin`, transactionnalité wallet, DTO sans secret/rôle mutable et accès annonces masquées borné aux acteurs légitimes. | renforcée, PARTIEL |
| C9 | Suite Maven : 104 rapportés avec 5 conditionnels ignorés ; preuve séparée `PostgresPaymentIntegrationTests` : 5/5 exécutés, 0 failure/error/skip ; tests image réels JPEG/PNG/GIF/BMP et entrées invalides. | renforcée, PARTIEL |
| C10 | Politique d'upload documentée (décodage réel, temporaire/nettoyage, persistance media) et migrations compatibles avec une base Railway non vide. | renforcée, PARTIEL |
| C11 | CI backend et frontend exécutées avec succès sur les commits du lot ; déploiement frontend Hostinger automatisé, backend Railway contrôlé/manuellement déployé. | renforcée, PARTIEL |

WebP, AVIF et HEIC/HEIF ne sont pas déclarés supportés : aucun décodeur Java correspondant n'a été ajouté. GIF est converti en JPEG sur sa première image et BMP en JPEG. Les preuves C10/C11 demeurent partielles tant que CD Railway automatique, observabilité, sauvegarde/restauration et rollback testés ne sont pas démontrés.

### Recette production consolidée — impact C1 à C11 (18/10/2026)

| Compétence | Apport réellement validé | État de preuve |
|---|---|---|
| C1 | Déploiement exécuté sur les environnements Hostinger/Railway et smoke API/CSRF validés. | renforcée, PARTIEL |
| C2 | Interfaces Admin, Feed et Marketplace utilisées en production ; validations UI de format/taille cohérentes avec le backend. | PASS fonctionnel, PARTIAL captures |
| C3 | Cycle Marketplace nominal et refusé, modération, wallet de démonstration et messages d'erreur validés fonctionnellement. | PASS fonctionnel, PARTIAL traces |
| C4 | Lot traçable par commits, CI, plan et preuve de recette. | renforcée, PARTIEL |
| C5 | Parcours administrateur, vendeur et acheteur confrontés à la recette réelle. | renforcée, PARTIEL |
| C6 | Architecture multicouche et séparation frontend/REST/Spring/PostgreSQL confirmées par les parcours déployés. | renforcée, PARTIEL |
| C7 | Flyway V2/V3 appliquées et PostgreSQL opérationnel en production ; validation non vide locale déjà archivée. | renforcée, PARTIEL |
| C8 | Repositories/services transactionnels, ownership, RBAC et protection BOLA/IDOR vérifiés par tests et scénarios de masquage. | renforcée, PARTIEL |
| C9 | Tests automatisés + recette fonctionnelle production ; captures et traces HTTP restent à compléter. | renforcée, PARTIEL |
| C10 | Docker/Railway, migrations, smoke tests et déploiement Hostinger prouvés fonctionnellement. | renforcée, PARTIEL |
| C11 | GitHub Actions : backend tests/build Docker ; frontend tests/build/déploiement Hostinger/smoke test. Railway est contrôlé/manuellement déployé, pas de CD backend automatique revendiqué. | renforcée, PARTIEL |

Les uploads JPEG, PNG, GIF et BMP sont **fonctionnels validés** en production ; leurs captures visuelles restent une preuve à ajouter. Aucune compétence n'est déclarée complète sur la seule base d'une recette manuelle sans l'ensemble des livrables de certification.
