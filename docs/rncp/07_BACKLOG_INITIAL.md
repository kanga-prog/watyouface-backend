# Backlog initial de haut niveau

**Convention :** « À affiner » signifie que l'item nécessite analyse, critères d'acceptation et estimation avant développement. Aucun item de ce fichier n'autorise à lui seul une modification du code.

| EPIC | FEATURE | User story / tâche | Priorité | RNCP | Statut |
|---|---|---|---|---|---|
| EPIC-01 Auth | parcours accès | En tant que visiteur, je peux accepter le contrat, créer un compte et me connecter avec des erreurs claires. | MUST | C2, C3, C5, C9 | à affiner |
| EPIC-01 Auth | session | Définir expiration, logout, gestion d'erreur et stratégie de stockage du jeton. | P1 | C3, C9 | à analyser |
| EPIC-SOCIAL | feed | En tant que USER, je peux consulter le fil et créer une publication rattachée à mon identité. | MUST | C2, C3, C8, C9 | à affiner |
| EPIC-SOCIAL | permissions post | En tant que propriétaire, je peux modifier/supprimer mon post ; un autre USER est refusé sauf règle ADMIN définie. | MUST | C3, C8, C9 | exigence cible à vérifier |
| EPIC-SOCIAL | interaction | En tant que USER, je peux commenter et liker selon les règles de visibilité et d'autorisation ; l'auto-like est à vérifier. | MUST | C2, C3, C8, C9 | à analyser |
| EPIC-MESSAGING | conversation | En tant que participant, je peux créer/ouvrir une conversation et envoyer/recevoir un message seulement dans mes conversations. | MUST | C2, C3, C8, C9 | à affiner |
| EPIC-MESSAGING | négociation | En tant qu'acheteur ou vendeur, je peux discuter autour d'une annonce selon des règles à définir. | SHOULD | C2, C3, C5, C8 | à analyser |
| EPIC-MESSAGING | preuve WebSocket | Définir et exécuter le test E2E de souscription non autorisée. | P1 | C3, C9 | à planifier |
| EPIC-MARKETPLACE | annonce | En tant que vendeur, je peux créer une annonce valide et en suivre l'état. | MUST | C2, C3, C7, C8, C9 | à affiner |
| EPIC-MARKETPLACE | cycle vente | En tant qu'acheteur/vendeur, je ne peux exécuter que les transitions qui me concernent. | MUST | C3, C8, C9 | partiellement testé |
| EPIC-MARKETPLACE | paiement démo | En tant qu'acheteur, je peux déclencher une fois le paiement de démonstration d'une annonce acceptée. | MUST | C3, C7, C8, C9 | partiellement testé |
| EPIC-MARKETPLACE | données monétaires | Décider et documenter le type monétaire, l'intégrité et les limites du wallet démo. | P1 | C3, C7, C8 | à analyser |
| EPIC-05 Administration | rôle ADMIN | En tant qu'ADMIN, je peux accéder uniquement aux actions d'administration prévues. | SHOULD | C3, C9 | partiellement prouvé |
| EPIC-06 Security | secrets | Effectuer rotation des secrets historiques et préparer les variables par environnement. | P0 | C1, C3, C10 | à réaliser hors code métier |
| EPIC-06 Security | défense web | Définir CSP, rate limiting, stratégie session et sécurité upload. | P1 | C3, C9, C10 | à analyser |
| EPIC-06 Security | conformité | Produire inventaire des données, confidentialité, rétention et droits. | P1 | C4, C5 | à produire |
| EPIC-07 Testing | plan | Concevoir le plan de tests avec données, préconditions, attendus et verdicts. | P0 | C9 | à produire |
| EPIC-07 Testing | couverture | Ajouter tests services/API/UI du MVP et rapport d'exécution. | P0 | C3, C8, C9 | à planifier |
| EPIC-07 Testing | accessibilité/performance | Définir contrôles manuels et limites de performance proportionnées. | P2 | C2, C9 | à produire |
| EPIC-08 Deployment | conteneurs | Créer et valider Docker/Compose local. | P1 | C10 | hors phase |
| EPIC-08 Deployment | exploitation | Écrire déploiement, variables, migration, backup et rollback. | P1 | C10 | hors phase |
| EPIC-09 DevOps | pipeline | Mettre en place CI lint/build/tests et traces de résultats. | P1 | C11 | hors phase |
| EPIC-09 DevOps | qualité | Ajouter analyse dépendances et règles de branche/revue proportionnées. | P2 | C4, C11 | à planifier |
| EPIC-10 Documentation/RNCP | conception | Produire besoins, use cases, maquettes, architecture et données. | P0 | C4, C5, C6, C7 | en cours |
| EPIC-10 Documentation/RNCP | soutenance | Constituer dossier, sélection de preuves et scénario oral marketplace. | P0 | C1-C11 | à planifier |

## Ordre de traitement recommandé

1. EPIC-10 : analyse détaillée des besoins et critères d'acceptation des trois piliers.
2. EPIC-07 : plan de tests et jeu d'essai du MVP en parallèle.
3. EPIC-01/EPIC-SOCIAL/EPIC-MESSAGING/EPIC-MARKETPLACE : stabilisation strictement nécessaire des parcours MUST.
4. EPIC-06 : risques P0/P1 de sécurité et conformité.
5. EPIC-08/09 : déploiement et DevOps, une fois le MVP stable.

## Traçabilité RNCP

| Compétence | Apport |
|---|---|
| C4 | priorisation, risques, suivi d'un projet |
| C5 | traduction besoins en fonctionnalités |
| C3/C8/C9 | règles, données et tests à construire |
| C10/C11 | éléments de mise en production explicitement planifiés |
