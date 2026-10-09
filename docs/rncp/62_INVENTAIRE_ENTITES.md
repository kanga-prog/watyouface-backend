# Inventaire des entités JPA observées

**Convention :** colonnes implicites = nom déduit par Hibernate/Spring (souvent snake_case), donc **à vérifier par migration/schéma réel**. Toutes les PK observées sont `Long` + `GenerationType.IDENTITY`.

| Entité / table | Colonnes métier observées | Relations / annotations significatives | État |
|---|---|---|---|
| `User` / `users` | username NN UQ, email NN UQ, password NN, acceptedContract NN défaut false, avatarUrl nullable, role enum NN USER | 1,N Post/Message/Comment/Like cascade ALL ; N,1 Contract via `contract_id` nullable | observé |
| `Contract` / `contract` | title, content(5000), version, active, createdAt | 1,N User via acceptedContractVersion | observé |
| `UserContract` / `user_contract` | accepted, acceptedAt | N,1 User ; N,1 Contract ; join columns implicites, nullabilité non déclarée | observé |
| `Post` / `post` | content, imageUrl, videoUrl, createdAt | N,1 User author (`author_id` nullable annotation absente) ; 1,N Comment/Like cascade ALL + orphanRemoval | observé |
| `Comment` / `comment` | content NN, createdAt | N,1 author NN ; N,1 Post nullable ; N,1 Video nullable | observé |
| `Like` / `likes` | id seul | N,1 User/Post/Video, tous join columns nullable dans annotations | observé |
| `Conversation` / `conversations` | isGroup défaut false, title nullable, createdAt | 1,N ConversationUser cascade ALL + orphanRemoval | observé |
| `ConversationUser` / `conversation_users` | id seul | N,1 Conversation NN ; N,1 User NN | observé |
| `Message` / `messages` | content length 2000 nullable annotation, sentAt, edited false, deleted false | N,1 Conversation/Sender/Receiver obligatoires (`optional=false`) | observé |
| `Listing` / `listings` | title NN, description length 2000, price NN, image, status enum NN AVAILABLE | N,1 seller NN LAZY ; N,1 buyer nullable LAZY | observé |
| `Wallet` / `wallet` | balance NN défaut 0 | 1,1 User (`user_id` NN UQ) | observé |
| `Transaction` / `transaction` | amount NN, status enum NN CREATED, createdAt NN `@PrePersist` | N,1 fromUser, toUser, Listing tous NN | observé |
| `Video` / `video` | title, url, uploadedAt | N,1 uploader (join implicite) ; 1,N Comment/Like cascade ALL | observé |
| `VideoShare` / `video_share` | seen défaut false | N,1 Video/Sender/Receiver (join implicites) | observé |

## Enums observés

`Role {USER, ADMIN}` ; `ListingStatus {AVAILABLE, PENDING, ACCEPTED, PAID, SHIPPED, RECEIVED, REFUSED}` ; `TransactionStatus {CREATED, SUCCESS, FAILED, COMPLETED}`. `WalletOperationType` existe mais aucune entité d’opération de wallet n’est observée.

## Points à ne pas surinterpréter

`@JsonIgnore` règle la sérialisation JSON, pas la confidentialité BDD. `cascade=ALL` ORM n’implique pas nécessairement une cascade SQL. Aucun index/UNIQUE non annoté ne doit être déclaré existant sans schéma/migration vérifiable.

