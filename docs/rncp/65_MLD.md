# MLD — Modèle logique relationnel

```text
USERS(id PK, username UQ NN, email UQ NN, password NN, accepted_contract NN,
      avatar_url, role NN, contract_id FK → CONTRACT.id)
CONTRACT(id PK, title, content, version, active, created_at)
USER_CONTRACT(id PK, user_id FK → USERS.id, contract_id FK → CONTRACT.id,
              accepted, accepted_at) [cible UQ(user_id, contract_id)]

POST(id PK, content, image_url, video_url, created_at, author_id FK → USERS.id)
COMMENT(id PK, content NN, created_at, author_id FK → USERS.id NN,
        post_id FK → POST.id nullable, video_id FK → VIDEO.id nullable)
LIKES(id PK, user_id FK → USERS.id, post_id FK → POST.id nullable,
      video_id FK → VIDEO.id nullable) [cible UQ(user_id,post_id), UQ(user_id,video_id)]

CONVERSATIONS(id PK, is_group, title, created_at)
CONVERSATION_USERS(id PK, conversation_id FK → CONVERSATIONS.id NN,
                   user_id FK → USERS.id NN) [cible UQ(conversation_id,user_id)]
MESSAGES(id PK, conversation_id FK → CONVERSATIONS.id NN,
         sender_id FK → USERS.id NN, receiver_id FK → USERS.id NN,
         content, sent_at, edited, deleted)

LISTINGS(id PK, title NN, description, price NN, image, status NN,
         seller_id FK → USERS.id NN, buyer_id FK → USERS.id nullable)
WALLET(id PK, balance NN, user_id FK → USERS.id NN UQ)
TRANSACTION(id PK, amount NN, status NN, from_user_id FK → USERS.id NN,
            to_user_id FK → USERS.id NN, listing_id FK → LISTINGS.id NN,
            created_at NN) [cible UQ(listing_id)]

VIDEO(id PK, title, url, uploaded_at, uploader_id FK → USERS.id)
VIDEO_SHARE(id PK, video_id FK → VIDEO.id, sender_id FK → USERS.id,
            receiver_id FK → USERS.id, seen)
```

Les crochets `cible` désignent des contraintes nécessaires mais non prouvées par annotations/migrations actuelles.

