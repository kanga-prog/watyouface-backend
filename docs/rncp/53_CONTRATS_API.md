# Contrats API représentatifs

**Convention :** « Actuel » est vérifié dans les controllers. « Cible » indique explicitement les contrats à stabiliser, jamais une route déjà livrée sans preuve.

| Domaine | Method / path actuel | Acteur | Input / output | Auth / règle | Erreurs cibles |
|---|---|---|---|---|---|
| Auth | `POST /api/auth/register` | Visiteur | `RegisterRequest` → compte/DTO | public, validation | 400, 409 cible |
| Auth | `POST /api/auth/login` | Visiteur | `LoginRequest` → JWT/rôle | public, BCrypt ; contrat à contrôler | 400/401/403 cible |
| Post | `GET /api/posts`, `POST /api/posts` multipart | USER | liste / contenu+média → post | JWT, auteur issu serveur | 400/401 |
| Post | `PUT/DELETE /api/posts/{id}` | owner/ADMIN | mise à jour / vide | ownership | 403/404 |
| Like | `POST /api/likes/toggle` | USER | payload actuel → like | **legacy à sécuriser**, auto-like interdit cible | 403/409 |
| Comment | `GET /api/comments/post/{postId}`, `POST /api/comments` | USER | postId+content | JWT, visibilité/owner | 400/403 |
| Conversation | `GET /api/conversations`, `POST /api/conversations/with/{userId}` | USER | liste / conversation | participant | 401/403 |
| Message | `GET/POST /api/messages/conversations/{id}…` | participant | message / DTO | appartenance obligatoire | 401/403 |
| Listing | `GET/POST /api/marketplace/listings` | USER | `ListingDTO` | vendeur courant création | 400/401 |
| Listing | `POST /{id}/request|accept|refuse|pay|ship|receive` | buyer/seller/ADMIN | listing DTO ou vide | machine à états | 403/409 |
| Wallet | `GET /api/wallet/me`, `POST /api/wallet/me/credit` | USER/ADMIN | wallet / crédit démo | crédit ADMIN uniquement | 403/400 |

Les détails exhaustifs restent à normaliser en OpenAPI ultérieurement. Les endpoints `/{conversationId}/all`, `POST /api/likes/post/{postId}` et autres variantes doivent être inventoriés puis supprimés/sécurisés s’ils contournent le contrat cible.
