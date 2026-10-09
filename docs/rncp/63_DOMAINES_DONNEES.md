# Domaines de données

| Domaine | Entités | Rôle | Frontière / remarque |
|---|---|---|---|
| AUTH / PROFILE | User, Contract, UserContract, Wallet | identité, rôle, contrat, avatar, solde démo | password/email privés ; Wallet sans inverse User actuel |
| FEED | Post, Comment, Like | publication et interactions | Comment/Like peuvent aussi viser Video dans le modèle actuel |
| CHAT | Conversation, ConversationUser, Message | participants, historique, messages | pas de `listing_id` MVP |
| MARKETPLACE | Listing, Transaction, Wallet | annonce, vendeur/acheteur, paiement simulé | transition et transaction atomique |
| CONTRACT | Contract, UserContract, User.acceptedContractVersion | version/acceptation | deux représentations à clarifier |
| MEDIA | Video, VideoShare, URL image/avatar/vidéo | contenu et partage | fichiers hors BDD, URLs en colonnes |
| ADMIN | aucune entité dédiée | rôle `User.role`, actions sur autres domaines | conforme au modèle actuel |

