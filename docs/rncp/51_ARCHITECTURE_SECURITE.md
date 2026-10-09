# Architecture sécurité et autorisation

## Authentification : qui es-tu ?

1. `POST /api/auth/login` reçoit les credentials validés.
2. Spring vérifie le mot de passe hashé BCrypt.
3. `JwtUtil` signe un JWT HS256 contenant id, username, rôle, avec expiration observée à 24 h.
4. `JwtAuthenticationFilter` lit le Bearer et peuple le contexte Spring Security.
5. Frontend : JWT observé dans `localStorage`, puis entête `Authorization: Bearer` dans `src/utils/api.js`.

Le secret JWT provient de `JWT_SECRET` / `app.jwt.secret` et échoue au démarrage s’il est absent ou fait moins de 32 octets. La rotation historique reste à réaliser (issue #4). Stocker un token dans `localStorage` augmente l’exposition en cas de XSS : compromis actuel, **PARTIEL**, à traiter par prévention XSS et décision de session future.

## Autorisation : as-tu le droit ?

| Type | Décision serveur attendue |
|---|---|
| Rôle | USER ou ADMIN (`ROLE_ADMIN`) |
| Ownership | auteur post/commentaire/vidéo ou vendeur listing, sinon ADMIN explicitement autorisé |
| Conversation | participant `ConversationUser` pour lire, envoyer et s’abonner |
| Marketplace | acheteur/vendeur selon état (`AVAILABLE` à `RECEIVED`) |
| Wallet | crédit démo ADMIN ; paiement acheteur autorisé seulement à l’état ACCEPTED |

`Authz` centralise déjà identité courante et contrôle owner/admin pour certains flux. Toute visibilité UI est ergonomique ; elle ne remplace jamais une vérification service/controller.

## Gestion erreur cible

| Situation | Réponse cible |
|---|---|
| entrée/validation invalide | 400 générique et exploitable |
| pas de JWT / JWT invalide | 401 |
| authentifié mais interdit | 403 |
| ressource absente | 404 |
| conflit de transition/double paiement | 409 |
| incident interne | 500 générique, log sans secret |

`ApiExceptionHandler` couvre 400/403/409 et validation ; la couverture 404/500 homogène est **PARTIELLE**. `server.error.include-message=never` réduit l’exposition de messages framework.
