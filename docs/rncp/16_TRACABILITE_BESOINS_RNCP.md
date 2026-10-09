# Matrice de traçabilité besoins → RNCP

| Besoin | User story | Use case | Règle métier | Critère | Test futur | Compétence RNCP |
|---|---|---|---|---|---|---|
| Accéder de manière contrôlée | US-AUTH-001/002 | UC-01/02 | BR-AUTH-001 à 003 | AC-AUTH-001 à 003 | TEST-AUTH-001 inscription, -002 contrat, -003 login | C2,C3,C5,C9 |
| Gérer son identité | US-PROFILE-001/002 | UC-01/02 | BR-PROFILE-001/002 | AC-PROFILE-001/002 | TEST-PROFILE-001 ownership, -002 avatar | C2,C3,C7,C8,C9 |
| Partager dans le feed | US-POST-001/002/003 | UC-03/04 | BR-POST-001 à 004 | AC-POST-001 à 004 | TEST-POST-001 création, -002 owner, -003 tiers 403 | C2,C3,C5,C8,C9 |
| Interagir de façon contrôlée | US-COMMENT-001, US-LIKE-001 | UC-05 | BR-COMMENT-001/002, BR-LIKE-001/002 | AC-COMMENT-001/002, AC-LIKE-001/002 | TEST-COMMENT-001, TEST-LIKE-001/002 | C2,C3,C8,C9 |
| Communiquer entre membres | US-CHAT-001/002 | UC-06 | BR-CHAT-001 à 003 | AC-CHAT-001 à 003 | TEST-CHAT-001 lecture tiers, -002 envoi tiers, -003 STOMP | C2,C3,C5,C6,C8,C9 |
| Négocier autour d'une annonce | US-CHAT-003 | UC-06/08 | BR-CHAT-004 | AC-CHAT-004 | TEST-CHAT-MARKET-001 à définir après décision | C2,C3,C5,C6,C7,C8 |
| Proposer/acquérir un produit | US-MARKET-001 à 005 | UC-07 à 12 | BR-MARKET-001 à 007 | AC-MARKET-001 à 007 | TEST-MARKET-001 à 007 | C2,C3,C5,C7,C8,C9 |
| Simuler le paiement et éviter le double débit | US-MARKET-004, US-WALLET-001 | UC-10 | BR-MARKET-005, BR-WALLET-001 à 003 | AC-MARKET-005, AC-WALLET-001 à 003 | TEST-WALLET-001 USER 403, -002 ADMIN, TEST-MARKET-005 double paiement/solde | C3,C7,C8,C9 |
| Administrer dans un périmètre limité | US-ADMIN-001 | UC-13 | BR-ADMIN-001/002 | AC-ADMIN-001/002 | TEST-ADMIN-001 USER 403, -002 ADMIN | C3,C4,C9 |
| Produire une application exploitable | backlog EPIC-08/09 | hors phase | NFR-PORT-001 | procédure reproductible | recette Docker/CI future | C1,C10,C11 |

## Alimentation des supports RNCP

| Support | Contenu source prêt | Documents à utiliser |
|---|---|---|
| Dossier projet | besoin fondateur, trois piliers, acteurs, stories, règles, UC, écarts | `08` à `16` + cadrage `01` à `07` |
| Dossier professionnel | analyse du besoin, arbitrages, contraintes sécurité, comparaison code | `10`, `13`, `14`, `15` |
| Slides | problème, trois piliers, matrice acteurs, parcours marketplace, règle BOLA ou double paiement | `01`, `04`, `11`, `12`, `14` |
| Entretien | raisonnement sur exigences cibles vs code, priorisation P0, machine à états | `10`, `14`, `15` |

## Frontière de preuve

Cette matrice prépare C10/C11 mais ne les démontre pas : Docker, CI/CD, déploiement, sauvegarde et monitoring restent des livrables futurs. Elle ne remplace pas le modèle de données définitif (C7) ni les diagrammes d'architecture (C6), prévus dans des phases ultérieures.
