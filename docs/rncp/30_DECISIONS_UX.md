# Décisions UX — basse fidélité

| ID | Question | Décision | Justification | User story | Impact | RNCP |
|---|---|---|---|---|---|---|
| UX-001 | Comment accéder aux trois piliers ? | Navigation globale persistante : barre supérieure desktop, barre inférieure mobile. | réduire les actions Feed↔Chat↔Marketplace et rendre le contexte actif visible. | US-POST-001, US-CHAT-001, US-MARKET-001 | future navigation et responsive | C2,C5 |
| UX-002 | Comment montrer les actions post ? | Afficher Modifier/Supprimer seulement au propriétaire ou ADMIN ; le backend reste décisionnaire. | éviter d'encourager une action interdite tout en gardant la sécurité côté serveur. | US-POST-003 | owner/non-owner explicite | C2,C3,C5 |
| UX-003 | Comment gérer le like de son propre post ? | Bouton désactivé/indisponible avec texte explicatif ; réponse serveur reste obligatoire. | règle BR-LIKE-002, prévention avant erreur. | US-LIKE-001 | dépend de GAP-LIKE-001 | C2,C3,C5 |
| UX-004 | Comment positionner le chat marketplace ? | Entrée « Contacter le vendeur » dans le détail annonce, annotée « lien technique à concevoir ». | matérialise le besoin sans inventer la relation de données. | US-CHAT-003 | décision architecture future | C2,C5,C6 |
| UX-005 | Comment présenter le paiement ? | Utiliser « Paiement simulé » et afficher état/solde ; jamais les termes banque/PSP. | correspond au périmètre et évite une représentation trompeuse. | US-MARKET-004 | libellés et démonstration | C2,C3,C5 |
| UX-006 | Comment rendre le cycle lisible ? | Badge textuel d'état + action unique contextuelle par acteur. | réduit les actions invalides et rend AVAILABLE→RECEIVED compréhensible. | US-MARKET-002 à 005 | actions marketplace | C2,C3,C5 |
| UX-007 | Comment protéger la confidentialité ? | Email et wallet visibles uniquement sur le profil personnel ; cartes publiques utilisent username/avatar minimal. | minimisation et séparation public/privé. | US-PROFILE-001 | RGPD/UI à vérifier | C2,C5 |
| UX-008 | Comment gérer les erreurs ? | Message textuel proche de l'action, action de reprise, pas d'alerte technique seule. | accessibilité et compréhension. | toutes MUST | composants transverses | C2,C9 |
| UX-009 | Comment gérer mobile ? | Feed mono-colonne ; chat bascule liste→conversation avec retour ; marketplace cartes en une colonne et filtres repliables. | adaptation structurelle, pas simple réduction. | US-POST-001, US-CHAT-002, US-MARKET-001 | wireframes mobile | C2,C5 |
| UX-010 | Comment traiter une action dangereuse ? | Confirmation explicite pour suppression post/compte et actions vendeur irréversibles selon décision future. | prévenir les erreurs ; ne remplace pas l'autorisation serveur. | US-POST-003, PROFILE-004 | dialogues de confirmation | C2,C3 |

## Limite de phase

Ces décisions sont des choix de conception UX. Elles n'annoncent pas une conformité RGAA complète, ni une implémentation actuelle ; toute modification fonctionnelle future doit être reliée à une issue, un test et la matrice exigence/code.
