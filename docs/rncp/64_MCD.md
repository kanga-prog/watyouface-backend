# MCD — Modèle conceptuel des données

```mermaid
erDiagram
  USER ||--o{ POST : auteur
  USER ||--o{ COMMENT : ecrit
  POST ||--o{ COMMENT : recoit
  USER ||--o{ LIKE : effectue
  POST o|--o{ LIKE : recoit
  VIDEO o|--o{ COMMENT : recoit
  VIDEO o|--o{ LIKE : recoit
  USER ||--o{ CONVERSATION_USER : participe
  CONVERSATION ||--|{ CONVERSATION_USER : contient
  CONVERSATION ||--o{ MESSAGE : contient
  USER ||--o{ MESSAGE : envoie
  USER ||--o{ MESSAGE : recoit
  USER ||--o{ LISTING : vend
  USER o|--o{ LISTING : achete
  USER ||--o| WALLET : possede
  LISTING ||--o{ TRANSACTION : concerne
  USER ||--o{ TRANSACTION : debite
  USER ||--o{ TRANSACTION : credite
  CONTRACT ||--o{ USER_CONTRACT : versionne
  USER ||--o{ USER_CONTRACT : accepte
  CONTRACT ||--o{ USER : version_acceptee
  USER ||--o{ VIDEO : depose
  VIDEO ||--o{ VIDEO_SHARE : partage
  USER ||--o{ VIDEO_SHARE : envoie_recoit
```

## Cardinalités métier retenues

Un post a un auteur ; un commentaire/like cible actuellement **un post ou une vidéo** (exclusivité cible à imposer par contrôle/contrainte). Une conversation a au moins des participants dans le métier, mais l’entité seule ne l’impose pas. Un listing a un vendeur et éventuellement un acheteur. Un wallet appartient à un seul user. Le MCD MVP ne relie pas Conversation à Listing, conformément à ADR-006.

