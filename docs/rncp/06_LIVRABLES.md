# Livrables officiels WatYouFace

| ID | Livrable | Phase | État au 07/10/2026 | Preuve/source | Échéance cible |
|---|---|---|---|---|---|
| LIV-01 | Expression des besoins / cahier des charges | cadrage/analyse | à produire | cadrage initial | avant phase conception |
| LIV-02 | Vision, objectifs, acteurs, personas | cadrage | produit | `01` à `03` | 07/10/2026 |
| LIV-03 | Périmètre MVP et backlog | cadrage | produit | `04`, `07` | 07/10/2026 |
| LIV-04 | Planning et jalons | gestion projet | à produire | backlog initial | immédiat après cadrage |
| LIV-05 | Wireframes et maquettes | conception UI | à produire | personas/périmètre | phase 3 |
| LIV-06 | Prototype navigable | conception UI | à produire | maquettes | phase 3 |
| LIV-07 | Architecture logicielle et ADR | conception technique | à produire | audit initial | phase architecture |
| LIV-08 | Cas d'utilisation et diagrammes de séquence | analyse/conception | à produire | parcours MVP | phase analyse |
| LIV-09 | MCD, MLD, MPD | données | à produire | entités JPA existantes, à confirmer | phase données |
| LIV-10 | Migrations et jeu d'essai | données | à produire | aucune migration versionnée | phase données |
| LIV-11 | Frontend et backend versionnés | développement | existant, à stabiliser | deux dépôts Git | continu |
| LIV-12 | Documentation sécurité | sécurité | partiel/produit | rapport hardening + matrice sécurité | compléter avant jury |
| LIV-13 | Plan de tests, cas, jeux et rapport | tests | partiel | 14 tests backend, 5 frontend, matrice sécurité | phase tests |
| LIV-14 | Docker/Compose | déploiement | absent | audit initial | phase déploiement |
| LIV-15 | CI/CD et qualité automatisée | DevOps | absent | audit initial | phase DevOps |
| LIV-16 | Procédure de déploiement, rollback, sauvegarde | déploiement | absent | audit initial | phase déploiement |
| LIV-17 | Veille technique et sécurité | transverse | à produire | risques / choix futurs | avant jury |
| LIV-18 | Dossier projet RNCP | certification | à produire | compilation des livrables | avant 17/10/2026 |
| LIV-19 | Dossier professionnel | certification | à produire | expériences/preuves sélectionnées | avant 17/10/2026 |
| LIV-20 | Slides et scénario oral | certification | à produire | parcours MVP et preuves | avant 17/10/2026 |

**Règle de cohérence :** les livrables de conception, données, tests et démonstration doivent couvrir les trois piliers — feed social, chat et marketplace — même si le scénario marketplace apporte davantage de preuves transactionnelles.

## Critères de succès projet

- [ ] Le MVP est limité, stable et démontrable sans contournement manuel.
- [ ] Le parcours marketplace nominal et ses refus importants sont testés.
- [ ] Les protections critiques (authentification, rôle, ownership, validation, secrets) sont vérifiées.
- [ ] L'architecture réelle et les décisions sont documentées.
- [ ] Le modèle de données, les migrations et le jeu d'essai sont reproductibles.
- [ ] Les tests versionnés sont verts et leur rapport est conservé.
- [ ] L'interface MVP est documentée par maquettes et vérifiée pour les critères d'accessibilité retenus.
- [ ] L'application est construite et lancée via une procédure reproductible.
- [ ] Le déploiement, les variables, la sauvegarde, le rollback et la supervision minimale sont documentés et testés.
- [ ] Une CI exécute au minimum lint, build et tests ; sa preuve est conservée.
- [ ] Dossier projet, dossier professionnel et support oral pointent vers les mêmes preuves.

## Traçabilité RNCP

| Compétence | Livrables clés |
|---|---|
| C1 | procédure environnement, variables |
| C2 | maquettes, interface, prototype |
| C3/C8 | code, règles, accès données, sécurité |
| C4/C5 | cadrage, backlog, planning, besoins |
| C6/C7 | architecture, diagrammes, modèle données, migrations |
| C9 | plan, jeux, rapports de tests |
| C10/C11 | Docker, procédure, pipeline, preuve de mise en production |
