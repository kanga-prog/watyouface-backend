# Plan de collecte des preuves RNCP

| ID | Preuve à collecter | Source / condition | Date cible | Usage dossier | Usage slide | Usage oral |
|---|---|---|---:|---|---|---|
| EV-01 | Historique Git, branches et commits atomiques | `git log`, PR/auto-revue | continu | gestion projet | timeline | méthode C4 |
| EV-02 | Wireframes/maquettes des trois piliers | outil de conception à choisir | 08–15/10 | interfaces | parcours | arbitrages UX |
| EV-03 | Captures feed : création, owner/tier refusé, commentaire/like | jeu d'essai fictif | 15/10 | fonctionnalités | feed | C2/C3 |
| EV-04 | Captures chat : conversation, message, refus participant tiers | jeu d'essai + trace STOMP | 15/10 | sécurité/chat | temps réel | BOLA chat |
| EV-05 | Captures marketplace cycle complet | annonce fictive avec états | 15/10 | règles métier | machine états | C3/C7/C8 |
| EV-06 | MCD/MLD/MPD et migrations | outils/SQL versionnés | 12/10 | données | modèle | conception C7 |
| EV-07 | Tests backend/frontend et plan de tests | sorties commandes, rapports | 12–17/10 | qualité | tableau résultats | C9 |
| EV-08 | Avant/après sécurité | gaps + correctifs + tests | 12/10 | sécurité | risque/correction | raisonnement AppSec |
| EV-09 | Docker/Compose et lancement | terminal propre, sans secrets | 13/10 | déploiement | schéma env. | C10 |
| EV-10 | Pipeline CI verte | logs/capture workflow | 14/10 | DevOps | pipeline | C11 |
| EV-11 | Procédure déploiement, backup/rollback | runbook validé | 14/10 | exploitation | étapes | C10 |
| EV-12 | RGPD/RGAA/éco-conception | checklist/revue et limites | 15/10 | conformité | checklist | responsabilité |
| EV-13 | Dossier/DP/slides et scénario | version finalisée | 16–17/10 | certification | support | soutenance |

## Règles de collecte

- Utiliser uniquement comptes et données fictifs.
- Conserver la date, commande, version/commit et verdict de chaque preuve.
- Masquer tokens, mots de passe, emails réels, chemins sensibles et informations d'infrastructure.
- Une capture ne remplace jamais un test versionné ; elle illustre le résultat pour le jury.
- Mettre à jour `RNCP6_SOURCE_DE_VERITE.md` quand une preuve change d'état.
