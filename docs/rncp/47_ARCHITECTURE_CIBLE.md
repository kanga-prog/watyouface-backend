# Architecture cible réaliste

## Principe

L’architecture cible est une **architecture répartie multicouche sécurisée**, à condition que les contrôles d’autorisation, validations, tests, migrations et déploiement planifiés soient finalisés. Elle ne déplace aucune règle critique uniquement dans React.

| Couche | Responsabilité cible | Règles |
|---|---|---|
| React/Vite | présentation, navigation, état UI, appels API/STOMP | ne décide jamais seul d’un droit ni d’une transition métier |
| REST/WebSocket | contrats, transport, réponses HTTP/STOMP | JWT transmis, entrée non fiable |
| Security | authentifier, peupler identité, filtrer rôles | 401/403 sans divulgation |
| Controller | mapper/valider, déléguer, retourner DTO/statut | pas de règle métier complexe |
| Service | ownership, transitions, orchestration, transaction | point d’autorité des règles métier |
| Repository | accès JPA, requêtes, verrouillage ciblé | pas de décision d’UI |
| PostgreSQL | persistance, PK/FK/contraintes/index | migrations reproductibles à ajouter |
| Media storage | fichiers validés et noms sûrs | accès/politique de conservation à définir |

## Transverses

Validation Bean Validation, gestion homogène des erreurs, logs sans secrets, configuration par variables d’environnement, CORS configuré, tests automatisés et observations d’exploitation sont transverses à toutes les couches.

## Découpage de livraison

La cible privilégie des évolutions petites : fermeture des gaps P0, migrations/contraintes, contrats API explicites, tests, Docker/CI/déploiement. Elle ne requiert pas une migration de stack ni une réécriture des trois piliers.
