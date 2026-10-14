# Plan de collecte des preuves RNCP

| ID | Preuve à collecter | Source / condition | Date cible | Usage dossier | Usage slide | Usage oral |
|---|---|---|---:|---|---|---|
| EV-01 | Historique Git, branches et commits atomiques | `git log`, PR/auto-revue | continu | gestion projet | timeline | méthode C4 |
| EV-02 | Wireframes/maquettes des trois piliers | outil de conception à choisir | 08–15/10 | interfaces | parcours | arbitrages UX |
| EV-03 | Captures feed : création, owner/tier refusé, commentaire/like | jeu d'essai fictif | 15/10 | fonctionnalités | feed | C2/C3 |
| EV-04 | Captures chat : conversation, message, refus participant tiers | jeu d'essai + trace STOMP | 15/10 | sécurité/chat | temps réel | BOLA chat |
| EV-05 | Captures marketplace cycle complet | annonce fictive avec états | 15/10 | règles métier | machine états | C3/C7/C8 |
| EV-06 | MCD/MLD/MPD et migrations | V1 Flyway sur PostgreSQL 16.15 vide : 14 tables, validate, tests 5/5 | 13/10 | données | modèle + schéma | C7, intégrité et reproductibilité |
| EV-07 | Tests backend/frontend et plan de tests | sorties commandes, rapports | 12–17/10 | qualité | tableau résultats | C9 |
| EV-08 | Avant/après sécurité | gaps + correctifs + tests | 12/10 | sécurité | risque/correction | raisonnement AppSec |
| EV-09 | Docker, lancement backend et volume media | image Java 17, entrypoint root limité, Java `spring` UID 999, volume Railway `spring:spring`, fichier media conservé après redeploy, smoke HTTP prod 401 + en-têtes | 13–14/10 | déploiement | schéma env. + preuves `BE-DATA-03` / `BE-DEP-03` | C10, C11 |
| EV-10 | Pipeline CI backend verte | Run GitHub Actions [#38550729055](https://github.com/kanga-prog/watyouface-backend/actions/runs/38550729055) : checkout, Java 17, Maven tests et Docker build PASS | 14/10 | DevOps | workflow + preuve `BE-DEVOPS-01` | C11 |
| EV-11 | Procédure déploiement, backup/rollback | runbook validé | 14/10 | exploitation | étapes | C10 |
| EV-12 | RGPD/RGAA/éco-conception | checklist/revue et limites | 15/10 | conformité | checklist | responsabilité |
| EV-13 | Dossier/DP/slides et scénario | version finalisée | 16–17/10 | certification | support | soutenance |

## Règles de collecte

- Utiliser uniquement comptes et données fictifs.
- Conserver la date, commande, version/commit et verdict de chaque preuve.
- Masquer tokens, mots de passe, emails réels, chemins sensibles et informations d'infrastructure.
- Une capture ne remplace jamais un test versionné ; elle illustre le résultat pour le jury.
- Mettre à jour `RNCP6_SOURCE_DE_VERITE.md` quand une preuve change d'état.

## État Phase 13

Les preuves versionnées `evidence/backend/BE-DATA-03-flyway-railway-baseline.txt`, `evidence/backend/BE-DEP-03-railway-media-volume-permissions.txt` et `evidence/backend/BE-DEVOPS-01-github-actions-backend-ci.txt` conservent uniquement des résultats réels : build Docker PASS, suite Maven 79 tests/0 échec/5 ignorés, Flyway V1 appliquée, 14 tables, Hibernate validate, 5 tests PostgreSQL PASS, volume Railway `spring:spring`, Java UID 999, persistance media après redeploy, smoke HTTP prod 401 avec en-têtes de sécurité et CI GitHub Actions backend verte. Elles ne prouvent pas le CD Railway automatique, l'observabilité, le rollback ni la restauration de sauvegarde.
