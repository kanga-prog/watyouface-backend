# Frontières fonctionnelles

| Domaine | Responsabilités | Données principales | Dépendances / frontière |
|---|---|---|---|
| Auth / Profile | compte, contrat, identité, avatar | User, Contract, UserContract | fournit l’identité courante aux autres domaines |
| Feed | publications, commentaires, likes, ownership | Post, Comment, Like, User | dépend de User ; aucune règle critique seulement UI |
| Chat | conversations privées/groupes, participants, messages, temps réel | Conversation, ConversationUser, Message | dépend de User et de l’autorisation de participant |
| Marketplace | annonce, acheteur/vendeur, transitions de vente | Listing, ListingStatus, User | dépend de Wallet/Transaction et de l’identité |
| Wallet / Transaction | crédit démo admin, transfert, traçabilité paiement simulé | Wallet, Transaction | appelé par Marketplace dans transaction atomique |
| Admin | rôles et modération limitée | User, Post, Comment, Listing | rôle technique ADMIN, contrôlé serveur |
| Media | avatar, image annonce, vidéo | fichiers disque + références entité | service transverse ; accès public actuel à durcir selon besoin |

## Règles de couplage

- Feed ne dépend pas de Marketplace.
- Chat reste générique ; une relation Conversation–Listing est une décision future documentée dans `55_DECISION_CHAT_LISTING.md`.
- Marketplace orchestre Wallet/Transaction, mais Wallet n’expose pas de crédit libre USER.
- USER/ADMIN sont des rôles techniques ; acheteur/vendeur sont des contextes dérivés du listing.
