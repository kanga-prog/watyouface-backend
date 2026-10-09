# Justification des choix technologiques — WatYouFace

## Positionnement

Les technologies décrites sont celles réellement présentes dans les dépôts au 07/10/2026, sauf indication « cible ». Les alternatives ne sont pas rejetées comme mauvaises : elles sont comparées au périmètre RNCP, à l’équipe, au délai et à une application web communautaire avec données relationnelles, règles métier et temps réel.

## React 19

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | SPA interactive : Feed, Chat, Marketplace, formulaires et états d’interface. |
| Pourquoi ce choix | composants réutilisables (`PostCard`, `ChatWindow`, `ListingCard`), état UI local et vaste écosystème. Adapté à des écrans qui changent fréquemment sans rechargement complet. |
| Avantages | composition, réutilisation, routage et tests composants déjà présents. |
| Limites / vigilance | état distribué pouvant devenir complexe ; XSS si rendu dangereux ; dépendance aux packages et aux mises à jour. Les règles métier critiques restent serveur. |
| Alternatives | Vue, Angular, HTML/JS classique. Vue est plus progressif, Angular plus structurant, HTML/JS plus léger. |
| Pourquoi non retenues | React est déjà la base fonctionnelle et répond au besoin sans migration ; le projet n’exige ni cadre Angular complet ni une UI statique. |
| RNCP | C2, C5, C6, C9 |

## Vite

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | environnement de développement et build frontend (`vite`, HMR, variables `VITE_*`). |
| Pourquoi ce choix | démarrage rapide, configuration légère, build moderne cohérent avec React 19. |
| Avantages | boucle de développement courte, scripts `dev/build/test/lint` lisibles. |
| Limites / vigilance | configuration production et variables publiques doivent être maîtrisées ; les `VITE_*` ne contiennent jamais de secret. |
| Alternatives | Webpack, Create React App. |
| Pourquoi non retenues | Webpack serait plus configuré que nécessaire ; CRA est moins adapté à une base moderne et n’apporte pas de gain au MVP. |
| RNCP | C1, C2, C6, C11 |

## Java 17

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | backend robuste, typé et organisé pour règles métier, sécurité et transactions. |
| Pourquoi ce choix | typage statique, maturité, écosystème entreprise et stabilité LTS adaptés à une application multicouche. |
| Avantages | refactoring sûr, écosystème sécurité/JPA/tests, code explicite pour les règles marketplace. |
| Limites / vigilance | verbosité, mémoire/démarrage, apprentissage des frameworks. |
| Alternatives | Node.js, Python, C#. |
| Pourquoi non retenues | elles peuvent convenir ; Java/Spring est déjà le socle et couvre les besoins transactionnels/sécurité sans réécriture. |
| RNCP | C3, C6, C8, C9 |

## Spring Boot 3.3.5

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | API, injection de dépendances, validation, JPA, transactions et WebSocket/STOMP. |
| Pourquoi ce choix | Spring MVC/Security/Data JPA/WebSocket apportent des briques intégrées à l’architecture Controller → Service → Repository. |
| Avantages | conventions solides, gestion transactionnelle, tests Spring, intégration PostgreSQL et sécurité. |
| Limites / vigilance | configuration riche et parfois opaque ; consommation mémoire et courbe d’apprentissage. Une mauvaise configuration Security reste possible. |
| Alternatives | Express/NestJS, Django/FastAPI, ASP.NET Core. |
| Pourquoi non retenues | chacune est viable ; Spring est cohérent avec Java et les modules déjà utilisés, notamment Security/JPA/STOMP. |
| RNCP | C3, C6, C8, C9, C10 |

## Spring Security

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | authentification stateless, filtre JWT, rôles, CORS et protection globale des routes. |
| Pourquoi ce choix | centralise l’accès avant les contrôleurs et s’intègre à BCrypt/JWT/SecurityContext. |
| Avantages | chaîne de filtres, règles déclaratives, contexte d’identité utilisable par `Authz` et les services. |
| Limites / vigilance | configuration complexe ; une exception de route ou un mauvais matcher peut exposer un endpoint. |
| Alternatives | filtre maison, middleware d’un autre framework. |
| Pourquoi non retenues | un filtre maison seul dupliquerait des mécanismes sensibles ; Spring Security est déjà intégré au backend. |
| RNCP | C3, C6, C9, C10 |

## JWT

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | identité stateless entre React et API/CONNECT STOMP. |
| Pourquoi ce choix | portable entre requêtes, compatible REST et WebSocket ; pas de session serveur applicative à maintenir. |
| Avantages | simple pour client séparé, expiration observée de 24 h, rôle/id portés dans le token. |
| Limites / vigilance | révocation difficile, vol de token, durée/rotation à gérer. Le stockage actuel `localStorage` est exposé au XSS : choix actuel, pas stratégie finale idéale. |
| Alternatives | sessions serveur, cookies HttpOnly/SameSite, OAuth2/OIDC. |
| Pourquoi non retenues | les sessions/cookies impliquent une stratégie CSRF et déploiement à cadrer ; OAuth/OIDC n’est pas requis au MVP. Ces options restent recevables pour une cible renforcée. |
| RNCP | C3, C6, C9, C10 |

## BCrypt

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | hashage des mots de passe via `BCryptPasswordEncoder`. |
| Pourquoi ce choix | algorithme lent et salé, standard mature adapté au stockage de mots de passe. |
| Avantages | le mot de passe n’est pas stocké en clair ; résistance accrue aux attaques hors ligne par rapport à un hash rapide. |
| Limites / vigilance | facteur de coût à calibrer, mot de passe jamais loggé, procédure reset à concevoir. |
| Alternatives | Argon2, PBKDF2. |
| Pourquoi non retenues | Argon2 est une alternative robuste ; BCrypt est intégré, éprouvé et déjà employé ici. |
| RNCP | C3, C6, C9 |

## PostgreSQL

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | persistance relationnelle des users, posts, conversations, annonces, wallets et transactions. |
| Pourquoi ce choix | transactions ACID, relations/FK, contraintes, index et verrouillage correspondent au cycle marketplace et au chat. |
| Avantages | intégrité, SQL riche, transaction et concurrence maîtrisables. |
| Limites / vigilance | schéma/migrations/index à administrer ; sauvegarde/restauration et accès DB doivent être documentés. |
| Alternatives | MySQL, MongoDB. |
| Pourquoi non retenues | MySQL est également adapté ; MongoDB est utile pour d’autres modèles mais moins naturel pour les relations et transactions métier centrales de ce MVP. |
| RNCP | C6, C7, C8, C9, C10 |

## JPA / Hibernate

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | mapping `Entity` ↔ tables, repositories Spring Data et relations JPA. |
| Pourquoi ce choix | réduit le SQL répétitif et intègre transactions/repositories au modèle Java. |
| Avantages | productivité CRUD, relation objet-relationnel, requêtes et verrou `PESSIMISTIC_WRITE` possibles. |
| Limites / vigilance | N+1, chargements implicites, sérialisation d’entités : comprendre SQL reste indispensable. |
| Alternatives | JDBC, jOOQ, MyBatis. |
| Pourquoi non retenues | elles offrent plus de contrôle SQL mais augmentent le code dans ce MVP ; JPA est déjà cohérent avec Spring Data. |
| RNCP | C6, C7, C8, C9 |

## REST

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | échanges React/backend pour CRUD, Auth, Feed, Profile et transitions Marketplace. |
| Pourquoi ce choix | HTTP standard, stateless, compréhensible, testable avec statuts 400/401/403/404/409. |
| Avantages | contrats lisibles, outillage courant, suffisamment expressif pour le MVP. |
| Limites / vigilance | variantes legacy à réduire, contrats DTO/OpenAPI à stabiliser, risques de sur/sous-fetching. |
| Alternatives | GraphQL, RPC. |
| Pourquoi non retenues | GraphQL est intéressant pour agrégation fine ; RPC pour des opérations ciblées. REST couvre les ressources/opérations actuelles sans ajouter une couche. |
| RNCP | C2, C3, C6, C8, C9 |

## WebSocket + STOMP et SockJS

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | chat temps réel. Le frontend utilise `sockjs-client` et `@stomp/stompjs`; backend expose `/ws`, `/app`, `/topic`. |
| Pourquoi ce choix | WebSocket autorise une communication bidirectionnelle ; STOMP structure destinations et topics ; SockJS fournit le mécanisme de connexion utilisé par le code actuel. |
| Avantages | évite le polling permanent, message/abonnement structurés, contrôle serveur possible au CONNECT et SUBSCRIBE. |
| Limites / vigilance | reconnexion, ordre, autorisation de destination et tests sont plus complexes qu’un REST simple. SockJS ajoute une couche et n’élimine pas le besoin de sécurité serveur. |
| Alternatives | polling, long polling, SSE. |
| Pourquoi non retenues | polling est plus simple mais coûteux/moins réactif ; SSE est unidirectionnel ; WebSocket correspond au dialogue bidirectionnel du chat. |
| RNCP | C2, C3, C6, C9 |

## Stockage médias local

| Élément | Justification |
|---|---|
| Besoin couvert / rôle | dossier `media/` local, URLs `/media/**`, avatars/images/vidéos de démonstration. |
| Pourquoi ce choix | simplicité pour MVP/local, aucune infrastructure externe obligatoire, mise au point rapide. |
| Avantages | facile à comprendre, à tester et adapté à une démonstration mono-instance. |
| Limites / vigilance | sauvegarde, confidentialité, volume, disponibilité et multi-instance ; MIME/taille/accès doivent être renforcés. |
| Alternatives | S3/object storage, Cloudinary. |
| Pourquoi non retenues | ces services améliorent scalabilité et distribution mais ajoutent comptes, coûts, secrets et configuration non nécessaires à ce stade RNCP. |
| Conclusion | acceptable pour MVP/démonstration, non présenté comme stockage de production scalable. |
| RNCP | C6, C10 |

## Architecture en couches et monolithe modulaire

Le backend est un **monolithe modulaire organisé en couches**, pas une architecture microservices : un seul Spring Boot contient plusieurs domaines internes. Cette décision simplifie déploiement et cohérence transactionnelle du MVP. Le coût futur est un couplage plus fort et une scalabilité indépendante par domaine plus limitée.

| Couche | Pourquoi | Vigilance |
|---|---|---|
| Controller | HTTP, DTO, validation et statut | pas de métier complexe |
| Service | règles, ownership, orchestration, `@Transactional` | ne pas contourner par endpoints legacy |
| Repository | JPA et accès données | requêtes explicites, N+1, verrouillage |
| Entity | modèle persistant | ne pas exposer directement l’entité comme contrat public |

Avantages : séparation des responsabilités, testabilité, maintenabilité et sécurité métier centralisée. Limites : davantage de fichiers et risque de surarchitecture si les frontières de domaine deviennent artificielles.

## Tableau synthèse

| Technologie | Rôle | Besoin | Avantage principal | Limite principale | Alternative |
|---|---|---|---|---|---|
| React 19 | interface SPA | interactivité | composants | état/XSS | Vue |
| Vite | dev/build | productivité | HMR rapide | config production | Webpack |
| Java 17 | backend typé | robustesse | maturité | verbosité | Node.js |
| Spring Boot | API/couches | services sécurisés | intégration | complexité | NestJS |
| Spring Security | authz/authn | protection routes | centralisation | configuration | middleware maison |
| JWT | identité stateless | client séparé | portable | révocation/XSS | session/cookie |
| BCrypt | mots de passe | confidentialité | hash lent salé | coût à régler | Argon2 |
| PostgreSQL | relationnel | intégrité | ACID | exploitation DB | MySQL |
| JPA/Hibernate | ORM | persistance | productivité | N+1 | jOOQ |
| REST | API | CRUD/métier | standard HTTP | contrats à stabiliser | GraphQL |
| WebSocket/STOMP | temps réel | chat | bidirectionnel | sécurité/reconnexion | SSE |
| Stockage local | médias MVP | simplicité | sans service tiers | non scalable | object storage |
