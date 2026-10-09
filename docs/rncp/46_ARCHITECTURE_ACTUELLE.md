# Architecture actuelle observée

## Vue d’ensemble

WatYouFace est répartie entre deux dépôts : un client **React 19 / Vite 7** et une API **Java 17 / Spring Boot 3.3.5**. Le navigateur appelle l’API HTTP et ouvre un canal SockJS/STOMP pour le chat. Le backend utilise Spring Data JPA vers PostgreSQL (H2 est aussi présent en dépendance de test/runtime) et un dossier local `media/` pour les fichiers servis sous `/media/**`.

## Frontend réellement observé

| Élément | Preuve | Observation |
|---|---|---|
| Routage | `watyouface-frontend/src/App.jsx` | React Router : `/`, `/marketplace`, `/messages`, `/profile`, `/admin`, `/contract`, `/login`, `/register` |
| Pages/UI | `src/pages`, `src/components` | Feed, posts/commentaires/like, chat, marketplace, profil, admin |
| API | `src/utils/api.js` | `fetch`, `VITE_API_BASE`, entête `Authorization: Bearer` |
| État | composants/hooks + `localStorage` | pas de store global observé ; token, username, avatar stockés localement |
| Temps réel | `src/utils/chatApi.js`, `ChatWindow.jsx` | SockJS + `@stomp/stompjs`, JWT au `CONNECT` |
| Médias | formulaires post/listing/avatar | `FormData` et URL retournée par l’API |

## Backend réellement observé

| Couche | Preuve | Rôle actuel |
|---|---|---|
| Entrée HTTP/STOMP | `controller/*Controller.java`, `ChatController.java` | routes REST et destination STOMP |
| Métier | `service/*Service.java` | posts, conversation, marketplace, wallet, transactions, contrat |
| Données | `repository/*Repository.java` | JPA, requêtes et verrou de listing |
| Persistance | `entity/*`, `entity/enums/*` | JPA : User, Post, Comment, Like, Conversation, Message, Listing, Wallet, Transaction… |
| DTO | `dto/*DTO.java`, requêtes validées | contrats API partiels : Login/Register/Listing/Conversation/Message… |
| Sécurité | `SecurityConfig`, `JwtAuthenticationFilter`, `JwtUtil`, `Authz` | JWT stateless, BCrypt, rôles, contrôles ownership selon flux |
| Erreurs | `ApiExceptionHandler` | 400/403/409 et validation centralisés partiellement |
| Médias | `media/MediaStorageService`, `VideoService`, `WebConfig` | stockage disque local et exposition `/media/**` |

## Données et limites observées

- PostgreSQL est la cible configurée ; les relations JPA portent les FK, mais les migrations versionnées restent à produire (issue #5/#10).
- `Conversation` ↔ `ConversationUser` porte l’appartenance ; `Message` référence conversation, émetteur et destinataire.
- `Listing` référence vendeur/acheteur et `Transaction` référence listing, débiteur et créditeur.
- Le client conserve le JWT dans `localStorage` : observé, acceptable à documenter comme compromis à revoir contre le risque XSS ; ce n’est pas une stratégie finale parfaite.
- Les médias sont publics sous `/media/**` dans la configuration actuelle ; confidentialité fine et politiques de type/taille restent partielles ou non prouvées.
