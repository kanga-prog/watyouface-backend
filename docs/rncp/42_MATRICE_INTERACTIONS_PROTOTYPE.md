# Matrice des interactions du prototype

| Écran | Élément | Action | Destination / résultat | État | Règle | User story | RNCP |
|---|---|---|---|---|---|---|---|
| UI-01 | Créer un compte | clic | UI-02 | default | AUTH-001 | US-AUTH-001 | C2/C5 |
| UI-02 | Continuer vers contrat | submit | UI-03 | validation simulée | AUTH-002 | US-AUTH-001 | C2/C5 |
| UI-03 | Accepter | clic | UI-01 puis connexion simulée | success | AUTH-002 | US-AUTH-001 | C2/C5 |
| UI-01 | Simuler erreur / contrat refusé | clic | message associé | error | AUTH-003/004 | US-AUTH-002 | C2/C5 |
| UI-04 | Créer une publication | clic | UI-06 | default | BR-POST-001 | US-POST-001 | C2/C5 |
| UI-06 | Publier | submit | UI-04, post visible | success | BR-POST-001 | US-POST-001 | C2/C3/C5 |
| UI-04 | Modifier / Supprimer sur son post | clic | UI-07 / confirmation | default | BR-POST-002/003 | US-POST-003 | C2/C3/C5 |
| UI-08 | Like post d’autrui | clic | compteur fictif évolue | active | BR-LIKE-001 | US-LIKE-001 | C2/C3 |
| UI-08 | Like son propre post | disabled | aucune destination | disabled | BR-LIKE-002 | US-LIKE-001 | C2/C3 |
| UI-09 | Conversation | clic | UI-10/11 | default | BR-CHAT-001 | US-CHAT-001 | C2/C5 |
| UI-10/11 | Envoyer | submit | bulle ajoutée localement | success | BR-CHAT-002 | US-CHAT-002 | C2/C5 |
| UI-13 | Contacter le vendeur | clic | UI-10/11 | default | besoin CHAT-006 | US-CHAT-003 | C2/C5 |
| UI-13 | Demander achat | clic | UI-15 / PENDING | success | BR-MARKET-002 | US-MARKET-002 | C2/C3/C5 |
| UI-16 | Accepter / Refuser | clic | ACCEPTED / REFUSED | success | BR-MARKET-003 | US-MARKET-003 | C2/C3 |
| UI-17 | Payer / expédier / réception | clic | PAID / SHIPPED / RECEIVED | success | BR-WALLET-001, BR-MARKET | US-MARKET-004 | C2/C3/C5 |
| Tous | États de démonstration | clic | loading, empty, error, forbidden | démonstration | NFR UX/SEC | — | C2/C5/C9 |

**Limite :** les destinations et règles ci-dessus sont une simulation d’interface. Aucun endpoint, WebSocket, rôle serveur ou transaction réelle n’est exécuté.

