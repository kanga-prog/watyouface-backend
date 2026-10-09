# Vision et objectifs WatYouFace

## Vision

La vision produit retenue est celle formulée dans `01_CADRAGE_WATYOUFACE.md` : une application communautaire sécurisée qui centralise un **feed social**, un **chat** et une **marketplace** autour d'une identité et de contrôles d'accès serveur. L'objectif est une démonstration RNCP cohérente, pas la mise sur le marché d'un réseau social ni d'un service de paiement.

## Objectifs mesurables

| ID | Catégorie | Objectif | Indicateur de réussite | Priorité |
|---|---|---|---|---|
| OBJ-M-01 | Métier | Permettre un échange marketplace entre deux membres | scénario annonce → demande → acceptation → paiement démo → expédition → réception démontré | MUST |
| OBJ-M-02 | Métier | Permettre le partage et l'interaction communautaires | création d'un post et interaction démontrées | SHOULD |
| OBJ-M-03 | Métier | Permettre une conversation sociale ou liée à une annonce | échange réservé aux participants démontré | MUST |
| OBJ-U-01 | Utilisateur | Donner un accès simple à l'espace membre | inscription, contrat et connexion réalisables avec retours d'erreur compréhensibles | MUST |
| OBJ-U-02 | Utilisateur | Permettre une communication privée et une négociation autour d'une annonce | un membre peut ouvrir une conversation autorisée et envoyer/recevoir un message | MUST |
| OBJ-T-01 | Technique | Conserver une architecture répartie multicouche | frontend React, API Spring, services, repositories et PostgreSQL documentés | MUST |
| OBJ-T-02 | Technique | Rendre l'exécution reproductible | procédure locale, variables d'environnement et versions documentées ; vérification à venir | SHOULD |
| OBJ-S-01 | Sécurité | Authentifier et autoriser côté serveur | appels sans jeton 401, droits ADMIN/USER et ownership couverts par tests | MUST |
| OBJ-S-02 | Sécurité | Protéger l'intégrité de la vente | les transitions invalides, double paiement et acteur non concerné sont refusés | MUST |
| OBJ-Q-01 | Qualité | Disposer d'un socle de non-régression | backend `mvn test`, frontend tests/lint/build verts au jalon | MUST |
| OBJ-Q-02 | Qualité | Préparer une interface accessible et responsive | audit manuel ciblé clavier, labels, erreurs et mobile à produire | SHOULD |
| OBJ-R-01 | RNCP | Prouver C2 à C11 par des artefacts | matrice de preuves reliée à chaque compétence avant jury | MUST |

## Indicateurs de cadrage à suivre

| Indicateur | Cible avant jury | État au 07/10/2026 |
|---|---|---|
| Parcours marketplace démontrable | 1 parcours nominal + cas refusés | partiellement codé et testé côté backend |
| Tests automatisés verts | 100 % des tests versionnés exécutés | 14 backend + 5 frontend PASS au dernier jalon |
| Décisions de sécurité tracées | risque, décision, test et limite documentés | démarré dans le rapport de sécurisation |
| Documentation de conception | besoins, maquettes, architecture, données, tests, déploiement | cadrage démarré ; autres éléments à produire |
| Mise en production démontrable | CI, image/conteneur, procédure et preuve d'environnement | non commencé, hors phase de cadrage |

## Critères de qualité à ne pas sacrifier

1. La démonstration ne doit pas contourner les contrôles serveur.
2. Une fonctionnalité non testée ou non documentée est présentée avec son niveau réel de maturité.
3. Le périmètre doit rester compatible avec l'échéance : stabiliser les trois piliers prévaut sur l'ajout de modules secondaires.
4. Les données utilisées dans les démonstrations doivent être fictives et reproductibles.

## Traçabilité RNCP

| Compétence | Preuve soutenue | Utilisation |
|---|---|---|
| C2 | objectifs d'interfaces des trois piliers | dossier et démonstration |
| C3 | objectifs de règles métier feed/chat/marketplace et sécurité | entretien technique |
| C4 | objectifs priorisés et indicateurs | dossier projet |
| C5 | vision, valeur, besoins et parcours des trois piliers | slides / dossier |
| C6-C8 | objectifs d'architecture, données et accès associés | dossier technique ultérieur |
| C9 | objectifs de tests de règles métier et permissions | plan de tests ultérieur |
| C10/C11 | critères de reproductibilité, à réaliser | backlog futur |
