# Audit technique RGPD — WatYouFace

Date : 20 octobre 2026
Périmètre : backend Spring Boot, frontend React/Vite, migrations Flyway, configuration versionnée, workflow de production et documentation RNCP.
Méthode : lecture du code et des configurations uniquement. Aucune donnée de production, aucun secret, aucune migration et aucune suppression n'ont été exécutés dans le cadre de cet audit. `updateAvatars.js`, les DOCX et les PPTX ne font pas partie du périmètre.

> Cet audit est technique : il décrit des mécanismes effectivement observés et les preuves disponibles. Il ne constitue pas un avis juridique, un registre des traitements complet, ni une déclaration de conformité RGPD.

## Conclusion courte

WatYouFace met en œuvre des protections techniques substantielles (hachage BCrypt, JWT en cookie `HttpOnly`, CSRF, CORS à liste d'origines, RBAC et contrôles d'appartenance). Depuis RGPD-ERASE-01, l'effacement d'un compte actif est orchestré transactionnellement, avec anonymisation irréversible de l'identité, maintien de références Marketplace et nettoyage post-commit des médias gérés non référencés. RGPD-INFO-03 rend les CGU et la politique de confidentialité accessibles depuis l’inscription et le footer, sans prétendre compléter les éléments non validés. Un export JSON utilisateur est disponible et RGPD-RETENTION-06 formalise désormais les catégories, événements et actions de fin de conservation ; les durées métier/juridiques restent à valider et aucune purge de rétention n'est active. Les preuves automatisées sont locales (H2) ; les scénarios PostgreSQL réels restent à exécuter sur une instance locale dédiée. Une procédure de violation documentée et une sauvegarde/restauration démontrée restent absentes.

Le statut global est donc **PARTIEL / GAP**, et non « conforme RGPD ».

## 1. Cartographie des données personnelles observées

Les quatorze entités JPA réelles sont : `User`, `Post`, `Comment`, `Like`, `Conversation`, `ConversationUser`, `Message`, `Listing`, `Transaction`, `Wallet`, `Video`, `VideoShare`, `Contract` et `UserContract`.

| Entité / table | Champs et identifiants observés | Finalité technique/fonctionnelle | Caractère / lien avec `User` | Effet observé ou risque à la suppression d'un `User` |
|---|---|---|---|---|
| `User` / `users` | `id`, `username`, `email`, `password`, `acceptedContract`, `avatarUrl`, `role`, `contract_id`, `deleted_at` | compte, authentification, identité affichée, acceptation de contrat ; tombstone d'effacement | username/email/password obligatoires ; avatar optionnel ; rôle attribué côté serveur ; relation au contrat courant | anonymisation et traitement relationnel via `AccountErasureService`; la durée de conservation du tombstone reste à valider |
| `Post` / `post` | `id`, `content`, `imageUrl`, `videoUrl`, `createdAt`, `author_id` | publication sociale et média associé | contenu/médias optionnels selon le flux ; N:1 auteur | `User.posts` a `cascade=ALL`; les fichiers référencés ne sont pas supprimés par `PostService` |
| `Comment` / `comment` | `id`, `content`, `createdAt`, `author_id`, `post_id`, `video_id` | interaction sociale sur post ou vidéo | contenu obligatoire ; auteur obligatoire ; cible post ou vidéo contrainte par SQL | cascade depuis `User.comments`, `Post.comments` et `Video.comments`; aucun besoin de média propre |
| `Like` / `likes` | `id`, `user_id`, `post_id`, `video_id` | interaction sociale | lien N:1 avec l'utilisateur et une seule cible post/vidéo | cascade depuis `User.likes`, `Post.likes` et `Video.likes`; FK SQL sans `ON DELETE` explicite |
| `Conversation` / `conversations` | `id`, `is_group`, `title`, `createdAt` | messagerie privée/de groupe | titre potentiellement personnel ; participants via table de jointure | participants en `cascade=ALL, orphanRemoval`; les messages ne sont pas mappés dans la collection et restent soumis à FK SQL |
| `ConversationUser` / `conversation_users` | `id`, `conversation_id`, `user_id` | appartenance à une conversation | association obligatoire conversation/utilisateur | FK SQL sans règle `ON DELETE`; bloque potentiellement la suppression directe du compte |
| `Message` / `messages` | `id`, `conversation_id`, `sender_id`, `receiver_id`, `content`, `sentAt`, `edited`, `deleted` | messagerie privée, traçabilité d'édition/suppression | expéditeur, destinataire et contenu personnel ; relations obligatoires | `User.sentMessages` et `receivedMessages` ont `cascade=ALL`, mais politique métier de conservation/anonymisation inexistante |
| `Listing` / `listings` | `id`, `title`, `description`, `price`, `image`, `createdAt`, `hidden`, `status`, `seller_id`, `buyer_id` | annonce Marketplace et parcours d'achat | vendeur obligatoire, acheteur éventuel ; contenu/image potentiellement personnel | aucune relation inverse/cascade depuis `User`; FK vendeur/acheteur sans `ON DELETE`; annonce engagée conservée avec `hidden=true` |
| `Transaction` / `transaction` | `id`, `amount`, `status`, `from_user_id`, `to_user_id`, `listing_id`, `createdAt` | paiement simulé, intégrité du wallet et historique Marketplace | acheteur, vendeur, annonce et montant ; tous obligatoires | aucune cascade depuis `User`; FK sans `ON DELETE`; suppression/anonymisation à décider avant toute effacement |
| `Wallet` / `wallet` | `id`, `balance`, `user_id` | solde de démonstration Marketplace | relation 1:1 obligatoire et unique avec `User` | aucune cascade inverse ; une ligne wallet suffit à bloquer une suppression SQL du compte si elle est présente |
| `Video` / `video` | `id`, `title`, `url`, `uploadedAt`, `uploader_id` | publication vidéo | uploader et URL de média ; champs métier optionnels dans le modèle | aucune cascade depuis `User`; commentaires/likes en cascade depuis la vidéo, mais `VideoShare` n'est pas mappé ni purgé |
| `VideoShare` / `video_share` | `id`, `video_id`, `sender_id`, `receiver_id`, `seen` | partage de vidéo et état de consultation | émetteur, destinataire, vidéo et statut de lecture | aucune cascade depuis `User` ou `Video`; FK sans `ON DELETE`, risque de blocage à l'effacement |
| `Contract` / `contract` | `id`, `title`, `content`, `version`, `active`, `createdAt` | texte/version du contrat | pas personnel seul ; devient lié à une personne via `User` et `UserContract` | référencé par `users.contract_id` et `user_contract.contract_id`; suppression non définie |
| `UserContract` / `user_contract` | `id`, `user_id`, `contract_id`, `accepted`, `acceptedAt` | preuve d'acceptation/refus d'une version | donnée associée à un utilisateur ; date d'acceptation ; unicité `(user_id, contract_id)` | aucune cascade depuis `User`; FK sans `ON DELETE`; conservation et sort à l'effacement non définis |

Autres données techniques observées :

- L'adresse IP distante est utilisée temporairement comme clé du limiteur de tentatives de connexion en mémoire (`LoginRateLimiter`) ; elle n'est pas conservée dans une entité et sa fenêtre est de 60 secondes par défaut.
- Le JWT contient `userId`, `username`, `role`, une date d'émission et une expiration d'un jour. Il est transmis en cookie `WATYOUFACE_AUTH`, pas stocké par l'application frontend.
- Les fichiers d'avatar, d'images Feed/Marketplace et de vidéos sont conservés sur le volume persistant `/app/media` et servis sous `/media/**`.

## 2. Minimisation et collecte

L'inscription collecte exactement `username` (3–50 caractères), `email` (format email, 255 caractères maximum), `password` (12–128 caractères) et `acceptTerms` (`RegisterRequest`, `AuthController`, `AuthService`). Le rôle n'est jamais fourni par le client à l'inscription : il est fixé à `USER` côté serveur. Aucun nom civil, numéro de téléphone, adresse postale, moyen de paiement bancaire ou donnée de catégorie particulière n'a été trouvé dans les quatorze entités.

Constats :

- username, email et mot de passe sont nécessaires aux mécanismes actuels de compte/connexion ; l'avatar est facultatif ; les contenus, médias, messages et annonces sont créés seulement dans les fonctionnalités correspondantes ;
- le profil permet de modifier le username, l'avatar, l'e-mail et le mot de passe ; les changements d'e-mail/mot de passe réauthentifient par le mot de passe courant ;
- `acceptedContract` + `acceptedContractVersion` représentent l'état courant, alors que `UserContract` vise un historique. Cette séparation peut être justifiée, mais `acceptContractForUser` insère systématiquement un `UserContract` malgré l'unicité `(user_id, contract_id)` : le modèle « état courant / preuve historique » doit être clarifié et testé ;
- la page `/legal/privacy` expose désormais, de façon informative, les finalités, bases à valider, catégories de destinataires, hébergement et droits. Elle signale explicitement les éléments qui ne sont pas encore formalisés : identité/contact du responsable, durées, bases validées et sous-traitants exhaustifs.

## 3. Sécurité des identifiants et de l'authentification

| Mécanisme observé | Preuve | État technique |
|---|---|---|
| Mot de passe non stocké en clair | `AuthService` appelle `PasswordEncoder.encode`; `SecurityConfig` crée `BCryptPasswordEncoder`; champ `User.password` est `@JsonIgnore` | OK |
| Réponses API sans hash | login renvoie seulement username/avatar/role ; `/api/users/me` construit une réponse explicite ; DTO admin dédié sans password | OK |
| JWT cookie | `AuthCookieFactory` crée `WATYOUFACE_AUTH`, `HttpOnly`, `Path=/`, durée égale à l'expiration JWT (1 jour) | OK |
| Configuration production du cookie | `application-prod.properties` configure `Secure=true`, `SameSite=Lax`; production confirmée dans la configuration déployée | OK sous réserve du maintien des variables de production |
| CSRF | `CookieCsrfTokenRepository` : cookie `XSRF-TOKEN`, header `X-XSRF-TOKEN`; le client récupère `/api/auth/csrf` avant chaque mutation | OK |
| CORS | liste d'origines issue de `CORS_ALLOWED_ORIGINS`, wildcard refusé, credentials autorisés ; contrôle production observé pour `watyouface.com` et `www.watyouface.com`, localhost refusé | OK |
| Logout | `/api/auth/logout` efface le cookie et le `SecurityContext` | PARTIEL : pas de révocation centrale ; un JWT copié reste valide jusqu'à expiration |
| Limitation login | 5 échecs/IP/60 s par défaut, en mémoire, sans conserver les identifiants | PARTIEL : mono-instance, réinitialisable au redémarrage, non distribué |

Le frontend ne stocke pas de JWT en `localStorage` ou `sessionStorage`; il y conserve uniquement username et URL d'avatar pour l'UX. Les tests frontend vérifient l'absence de clé `token` et l'absence d'en-tête `Authorization` sur les flux cookie/CSRF.

## 4. Droits observables dans l'application

### Accès

L'accès est **PARTIEL**. L'utilisateur connecté peut récupérer :

- son profil, email, contrat courant, avatar et solde via `GET /api/users/me` ;
- son wallet via `GET /api/wallet/me` ;
- ses transactions (ou celles auxquelles il participe) via `GET /api/transactions` ;
- ses conversations et, s'il est participant, leurs messages via `/api/conversations` et `/api/messages/conversations/{id}` ;
- les contenus accessibles de Feed, Marketplace et vidéos, ainsi que les partages vidéo dont il est émetteur/destinataire.

Un export structuré JSON est maintenant disponible via `GET /api/users/me/export` et rassemble les catégories listées dans `RGPD_EXPORT_PORTABILITE.md`. Il reste partiel : les fichiers binaires ne sont pas inclus, les messages entrants et les données privées des tiers sont volontairement exclus, et les champs absents du modèle ne peuvent pas être fournis. Le téléchargement du PDF du contrat actif seul ne constitue pas un export de données personnelles.

### Rectification

L'utilisateur authentifié peut modifier son username (`PUT /api/users/update`), son avatar (`POST /api/users/avatar`), son e-mail (`PATCH /api/users/me/email`) et son mot de passe (`PATCH /api/users/me/password`), ainsi que ses contenus dans les limites des règles métier. Les routes `/me` utilisent exclusivement `Authz.me()`, exigent le mot de passe courant et une preuve step-up courte, à usage unique et liée à l'action. Le code d'e-mail est adressé à l'ancienne adresse lors d'un changement d'e-mail, et à l'adresse courante pour un changement de mot de passe. Le mot de passe reste haché BCrypt et aucune réponse ne l'expose. Les challenges et limites sont stockés en mémoire par instance ; le mail est désactivé par défaut, et une configuration SMTP est nécessaire pour rendre le parcours disponible. Ce contrôle par e-mail n'est pas une 2FA indépendante. La nouvelle adresse n'est pas elle-même confirmée ; les JWT déjà copiés ne sont pas révoqués centralement. La mise à jour username historique n'emploie pas de DTO ni de gestion explicite du conflit d'unicité.

### Effacement

`DELETE /api/users/{id}` existe et applique `ownerOrAdmin`, avec deux protections pour les administrateurs (pas d'auto-suppression d'ADMIN ; pas de suppression du dernier ADMIN). Depuis RGPD-ERASE-01, `UserService` délègue à `AccountErasureService.erase`, sous `@Transactional` : le compte devient un tombstone anonymisé (`deleted_at`, identifiants et mot de passe remplacés), les contenus et messages sortants sont purgés ou rendus non identifiants, et les références Marketplace nécessaires sont maintenues. Un auto-effacement efface également le cookie d'authentification. Le profil frontend expose désormais ce parcours après une confirmation forte par saisie de `SUPPRIMER`, puis redirige vers la connexion. Le détail et les limites sont consignés dans `RGPD_ERASE_ACCOUNT_IMPLEMENTATION.md` et `RGPD_INFORMATION_UTILISATEUR.md`.

## 5. Suppression relationnelle, FK et médias

### Base relationnelle

La migration `V1__baseline_schema.sql` définit les FK mais **aucune** clause `ON DELETE CASCADE`, `SET NULL` ou politique équivalente. Les associations JPA et leurs effets ne sont donc pas une politique homogène :

- `User` a `cascade=ALL` vers posts, messages envoyés/reçus, commentaires et likes ;
- `Post` a `cascade=ALL, orphanRemoval=true` vers commentaires et likes ;
- `Conversation` a `cascade=ALL, orphanRemoval=true` vers participants ;
- `Video` a `cascade=ALL` vers commentaires et likes ;
- aucune relation inverse/cascade n'existe dans `User` pour wallet, listings vendeur/acheteur, transactions, vidéos, partages vidéo, participants de conversation ou `UserContract`.

La migration V4 n'ajoute volontairement pas de cascade SQL : `AccountErasureService` applique une stratégie explicite et transactionnelle, décrite dans `RGPD_ERASE_ACCOUNT_IMPLEMENTATION.md`. Elle évite à la fois le blocage des FK et la suppression arbitraire des transactions. Un test PostgreSQL local à activation explicite est présent, mais son résultat n'est pas encore disponible ; il serait donc inexact de présenter la compatibilité PostgreSQL réelle comme validée.

### Médias physiques

| Objet | Ce qui est effectivement supprimé | Écart constaté |
|---|---|---|
| Avatar effacé | candidat au nettoyage post-commit, uniquement s'il n'est plus référencé | pas de réconciliation des fichiers historiques |
| Post anonymisé | URL image/vidéo retirée, candidat au nettoyage post-commit non référencé | les fichiers jamais liés à une ligne ne sont pas couverts |
| Listing supprimé/neutralisé | annonce sans transaction supprimée ; annonce engagée cachée et image retirée, puis vérification de référence | pas d'inventaire des uploads abandonnés |
| Video anonymisée | URL retirée et partage lié supprimé ; média candidat au nettoyage post-commit non référencé | les autres cycles de suppression vidéo restent hors de RGPD-ERASE-01 |
| Échec d'écriture image Feed/Marketplace | fichier temporaire et sortie sont nettoyés dans `ImageUploadService` | nettoyage correct seulement pour l'échec d'encodage, pas pour le cycle de vie ultérieur |

Les médias sont servis publiquement par `WebConfig` et `SecurityConfig` (`/media/**` est `permitAll`). C'est cohérent seulement pour des contenus explicitement publics ; aucune visibilité fine, autorisation de téléchargement, rétention ou purge d'orphelins n'est implémentée.

## 6. Contrôles d'accès et administration

Les contrôles backend ne reposent pas sur le masquage frontend :

- `/api/admin/**` exige `ROLE_ADMIN` dans `SecurityConfig`, et `AdminController` vérifie à nouveau `Authz.isAdmin()` ;
- wallet : propriétaire ou ADMIN ; transactions : seulement participant, sauf ADMIN ;
- messages : appartenance à la conversation contrôlée avant lecture et envoi ; l'intercepteur STOMP contrôle aussi l'abonnement à `/topic/conversations/{id}` ;
- Marketplace : vendeur/acheteur déterminés côté serveur ; les méthodes `paySecured`, `accept`, `ship`, `receive` n'accordent pas de contournement métier automatique à ADMIN ;
- posts, commentaires et vidéos vérifient le propriétaire (avec modération ADMIN quand prévue).

L'administration peut consulter, via DTO dédiés, les utilisateurs (id, username, email, rôle, acceptation de contrat, solde), les posts (texte/médias), commentaires (texte/extrait) et annonces (contenu, vendeur, prix, état, image). Un ADMIN peut aussi consulter toutes les transactions par l'endpoint général. Aucun DTO ADMIN observé n'expose password/hash, JWT ou secret ; aucun endpoint ADMIN de lecture des messages n'a été trouvé.

Le besoin fonctionnel d'accès à l'email et au solde pour modération/gestion doit être documenté, et les actions administratives sensibles ne produisent pas de journal métier dédié. C'est un sujet de minimisation et de responsabilité, pas une preuve de fuite actuelle.

## 7. Logs, secrets, sauvegardes, rétention et information

### Logs

Les logs applicatifs ciblés observés consignent principalement une classe d'exception, un identifiant de post ou l'état d'initialisation du média. Aucun log applicatif lu n'écrit email, password, JWT, cookie ou en-tête `Authorization`; `spring.jpa.show-sql=false` en production et `server.error.include-message=never`. L'intercepteur WebSocket indique explicitement ne pas logger les credentials.

Cet état est **PARTIEL** : la durée, l'accès, la redaction centralisée des logs Railway/Spring, les adresses IP éventuelles des plateformes et la purge ne sont pas formalisés. Côté frontend, `chatApi.js` journalise le corps d'un message lorsque son parsing échoue et garde le debug STOMP actif ; aucune collecte par un tiers n'est prouvée, mais ces appels doivent être redacted/désactivés en production. Il n'y a pas de test de non-divulgation exhaustif des journaux. Voir le détail et les inconnues opérateur dans `RGPD_RETENTION_POLICY.md`.

### Secrets et configuration

Le dépôt exclut `.env` et fichiers associés ; les secrets applicatifs sont référencés par variables d'environnement (`JWT_SECRET`, DB, mail) et non écrits dans les fichiers de configuration. Le workflow backend ne référence que le secret GitHub `RAILWAY_TOKEN` sans imprimer sa valeur. Aucun fichier de clé, `.env` ou secret n'est suivi par Git selon l'inventaire des fichiers versionnés réalisé pour cet audit.

Cela prouve l'absence de valeur secrète dans les fichiers versionnés inspectés, pas les droits d'accès, la rotation ou l'inventaire effectif des secrets dans les interfaces GitHub, Railway et Hostinger. La source de vérité RNCP conserve en outre la limite historique de valeurs éventuellement présentes dans l'ancien historique Git : leur rotation doit rester confirmée hors dépôt.

### Conservation, sauvegarde et incidents

- aucune durée métier/juridique ni purge automatisée pour comptes, messages, contenus, transactions, médias ou logs n'a été trouvée ; les durées techniques sont précisées dans `RGPD_RETENTION_POLICY.md` ;
- le volume `/app/media` est persistant, mais aucune sauvegarde/restauration PostgreSQL ou média, ni exercice de restauration, n'est démontré ; la disponibilité éventuelle de fonctions Railway ne peut pas être déduite du dépôt ;
- aucune procédure versionnée de gestion de violation de données (détection, confinement, analyse, décision de notification, communication, journalisation) n'a été trouvée ;
- les routes frontend `/legal/terms` et `/legal/privacy`, ainsi que le footer et les liens depuis l'inscription, rendent les documents accessibles. Le contrat actif sert de CGU versionnées ; la politique de confidentialité ne remplace pas les éléments encore à valider (responsable/contact, durées, bases et sous-traitants).

## 8. Matrice d'audit

| ID | Sujet | État actuel | Preuve code/config | Risque | Action | Priorité |
|---|---|---|---|---|---|---|
| RGPD-01 | Inventaire technique | PARTIEL | 14 entités JPA et V1 Flyway recensées dans ce document | finalités, catégories, destinataires et bases légales non formalisés | valider un registre de traitements avec responsable juridique | P1 |
| RGPD-02 | Minimisation à l'inscription | PARTIEL | `RegisterRequest`, `AuthService.register` : username, email, password, acceptTerms uniquement ; rôle serveur | information et nécessité de chaque traitement non exposées | documenter finalité/base légale et revoir l'état/historique de contrat | P1 |
| RGPD-03 | Mot de passe et non-exposition | OK | BCrypt (`SecurityConfig`/`AuthService`), `@JsonIgnore`, réponses/DTO sans password | maintien à vérifier lors des évolutions | conserver tests de non-exposition | P2 |
| RGPD-04 | JWT, cookie, CSRF, CORS | OK | `AuthCookieFactory`, `SecurityConfig`, `JwtAuthenticationFilter`, client `src/utils/api.js` | révocation JWT non centrale | documenter la limite et définir une stratégie si le besoin apparaît | P2 |
| RGPD-05 | Rate limiting login | PARTIEL | `LoginRateLimiter`, 5/IP/60 s en mémoire | contournement multi-instance/redémarrage | déplacer au proxy/WAF ou stockage partagé si montée en charge | P2 |
| RGPD-06 | Droit d'accès | PARTIEL | `/api/users/me`, endpoints métier et `GET /api/users/me/export` | export structuré disponible, mais médias binaires et données privées de tiers non inclus ; champs absents du modèle | poursuivre la revue des catégories et proposer un export média seulement si nécessaire et validé | P1 |
| RGPD-07 | Rectification | PARTIEL | mêmes routes et services ; `POST /api/users/me/step-up/request` et `/verify`; `StepUpChallengeServiceTests`, `SecurityApiIntegrationTests`, `Profile.jsx`, `SEC_STEPUP_SENSITIVE_ACTIONS.md` | step-up e-mail non indépendant, stockage/limites mono-instance ; nouvelle adresse non confirmée ; copies JWT non révoquées centralement ; conflit username historique non géré explicitement | configurer et valider un SMTP réel avant activation, formaliser confirmation de nouvelle adresse et révocation si nécessaire | P1 |
| RGPD-08 | Portabilité / export | PARTIEL | `GET /api/users/me/export`, `UserDataExportService`, `UserDataExportDTO`, Profil frontend, tests d’intégration et `RGPD_EXPORT_PORTABILITE.md` | export JSON structuré, sans médias binaires ; validation complète des catégories et portée juridique à poursuivre | conserver la version du format, compléter selon les besoins validés et tester les évolutions | P1 |
| RGPD-09 | Effacement de compte | PARTIEL | `AccountErasureService` transactionnel, `V4__add_users_deleted_at_for_account_erasure.sql`, `AccountErasureIntegrationTests` (4 scénarios, PASS), `Profile.jsx` (confirmation forte) | le scénario PostgreSQL local est prêt mais non exécuté dans cet environnement ; validation des durées résiduelles à finaliser | exécuter le test PostgreSQL local contrôlé et finaliser l'information validée | P1 |
| RGPD-10 | FK, cascades et preuves contractuelles | PARTIEL | stratégie explicite par entité dans `RGPD_ERASE_ACCOUNT_IMPLEMENTATION.md`; transactions/listings engagés conservés avec utilisateur anonymisé | V1 conserve ses FK sans `ON DELETE`; la stratégie doit être revue avec le besoin juridique de conservation des contrats | valider les durées/légitimité de conservation et exécuter la preuve PostgreSQL locale | P1 |
| RGPD-11 | Cycle de vie médias / orphelins | PARTIEL | `MediaReconciliationService`/`MediaReconciliationCommand`, dry-run par défaut, purge à triple confirmation, normalisation de chemins et tests ; `MediaReferenceCleanupService` post-commit ; Maven 120/0/0 | inventaire local H2 non autoritatif pour les fichiers historiques ; uploads Marketplace non attachés et legacy `media/images/` à traiter ; aucune purge production | appliquer la procédure RGPD-MEDIA-02 sur snapshot sauvegardé, après validation explicite | P1 |
| RGPD-12 | Sécurité des uploads et accès média | PARTIEL | taille/MIME/décodage ImageUploadService, chemins bornés ; `/media/**` public | contenu privé accessible par URL ; pas d'AV/quarantaine ; avatar distinct | décider visibilité, protéger les médias non publics, harmoniser/tester avatar | P1 |
| RGPD-13 | RBAC, ownership, BOLA | PARTIEL | `SecurityConfig`, `Authz`, services Marketplace/messages, intercepteur STOMP | couverture E2E et revue continue nécessaires | conserver/étendre les tests par rôle et ressource | P1 |
| RGPD-14 | Exposition ADMIN | PARTIEL | `AdminUserDTO`, `AdminPostDTO`, `AdminCommentDTO`, `AdminListingDTO` | email/solde/contenus accessibles sans justification/documentation de besoin ; pas de journal métier | documenter besoin, limiter les colonnes/recherches et tracer les actions sensibles | P1 |
| RGPD-15 | Logs et redaction | PARTIEL | configuration prod SQL désactivé, niveaux `INFO`, erreurs HTTP masquées ; inventaire logs dans `RGPD_RETENTION_POLICY.md` | durée/rotation/accès Railway/Hostinger inconnus ; debug STOMP et corps WS en erreur de parsing visibles dans console navigateur | obtenir les règles des prestataires, désactiver/redacter les logs frontend sensibles, définir purge/accès et tests | P1 |
| RGPD-16 | Secrets | PARTIEL | `.gitignore`, variables d'environnement, secret GitHub référencé sans valeur | rotation, scopes et secrets historiques non prouvés depuis le dépôt | vérifier dans les consoles, appliquer/consigner rotation et moindre privilège | P1 |
| RGPD-17 | Politique de conservation | PARTIEL | `RGPD_RETENTION_POLICY.md` : 23 catégories, finalités, événements de départ, durées techniques prouvées, stratégies et limites de purge ; `JwtUtil` (24 h), `LoginRateLimiter` (60 s), `VideoService` (timeout 120 s / raw supprimé en finally), `MediaReconciliationService` (dry-run par défaut) | durées métier/juridiques non validées ; rétention/rotation/accessibilité des logs Railway/Hostinger inconnues ; pas de job général ; log frontend WS peut inclure un message en erreur de parsing | faire valider durées et bases, obtenir la rétention des prestataires, corriger/redacter les logs contenant des contenus, puis concevoir dry-run et tests avant toute purge | P1 |
| RGPD-18 | Sauvegarde / restauration | GAP | volume persistant prouvé ; aucune sauvegarde ni restauration DB/média prouvée | perte de données et incapacité à tenir les droits après incident | runbook chiffré, sauvegarde DB+volume cohérente, restauration isolée testée | P1 |
| RGPD-19 | Violation de données | GAP | aucune procédure trouvée | réponse tardive/non maîtrisée à un incident | procédure opérationnelle, rôles, critères, modèle de journal et communication | P1 |
| RGPD-20 | Information utilisateur | PARTIEL | `/legal/terms`, `/legal/privacy`, footer, information à l’inscription et `RGPD_INFORMATION_UTILISATEUR.md` ; frontend tests exécutés pendant RGPD-INFO-03 | responsable/contact, durées, bases et sous-traitants à valider ; procédure de demande de droits à formaliser | compléter les éléments validés avant ouverture publique et maintenir la page à jour | P1 |
| RGPD-21 | Preuves de non-régression | PARTIEL | `UserDataExportIntegrationTests` (5 tests), `AccountErasureIntegrationTests`, `CookieAuthenticationIntegrationTests` ; suite Maven : 125 tests, 0 échec, 0 erreur, 6 conditionnels ignorés ; frontend : 14 fichiers, 52 tests, 0 échec | pas encore de résultat PostgreSQL réel ni de tests export/restauration | exécuter PostgreSQL local et étendre au futur export/à la réconciliation média | P1 |
| RGPD-22 | Cohérence documentaire production | PARTIEL | `RNCP6_SOURCE_DE_VERITE.md` du 18/10 décrit encore un déploiement Railway manuel, alors que le workflow actuel `backend-ci.yml` déploie depuis `main` après CI | documentation opérationnelle obsolète, sans effet direct sur les données | mettre à jour la source de vérité lors de la prochaine phase documentaire | P2 |

## 9. Backlog recommandé, ordonné

1. **RGPD-ERASE-01 — Effacement de compte (P1 résiduel).** Implémenté et testé localement : finaliser la validation métier/juridique de la conservation, puis exécuter le test PostgreSQL local contrôlé documenté dans `RGPD_ERASE_ACCOUNT_IMPLEMENTATION.md`.
2. **RGPD-MEDIA-02 — Réconciliation et purge contrôlée des médias (P1 résiduel).** Outillage et prévention locale implémentés ; réaliser uniquement la procédure de production documentée dans `RGPD_MEDIA_RECONCILIATION.md` avec sauvegarde, dry-run et validation explicite.
3. **RGPD-INFO-03 — Information utilisateur (P1 résiduel).** Pages, footer et parcours inscription/effacement implémentés ; finaliser les informations validées (responsable, contact, durées, bases, sous-traitants) avant ouverture publique.
4. **RGPD-EXPORT-04 — Export et droit d'accès (P1 résiduel).** Export JSON authentifié implémenté avec DTO et minimisation des tiers ; poursuivre la revue du périmètre et vérifier les besoins validés sans élargir aux données de tiers.
5. **RGPD-RECTIFY-05 / SEC-STEPUP-01 — Rectification et vérification renforcée (P1).** Implémentées localement : changement d'e-mail/mot de passe avec réauthentification, OTP e-mail à usage unique et preuve courte. Restent la configuration SMTP opérationnelle, la confirmation de la nouvelle adresse, l'historique métier minimisé et l'évaluation de la révocation des JWT copiés ; voir `RGPD_RECTIFICATION.md` et `SEC_STEPUP_SENSITIVE_ACTIONS.md`.
6. **RGPD-RETENTION-06 — Politique de conservation (P1).** Matrice et politique technique formalisées dans `RGPD_RETENTION_POLICY.md`; obtenir validation des durées/finalités, clarifier logs et sauvegardes, puis seulement préparer un mécanisme configurable et dry-run.
7. **RGPD-GOV-07 — Gouvernance et validation des durées (P1).** Faire valider la matrice par responsable/métiers et prestataires ; décider notamment `Transaction`, `UserContract`, tombstone, médias et rétention des logs avant d'activer une purge.
8. **RGPD-INCIDENT-08 — Procédure de violation (P1).** Écrire et faire valider la procédure de détection, confinement, investigation, décision de notification et conservation de preuve.
9. **RGPD-ADMIN-09 — Minimisation et traçabilité administrative (P1).** Justifier les données visibles, réduire les colonnes/recherches au nécessaire et créer une trace d'actions de modération/solde avec rétention définie.
10. **RGPD-OPS-10 — Secrets et preuve de sécurité opérationnelle (P1).** Vérifier hors dépôt les droits GitHub/Railway/Hostinger, la rotation des valeurs historiques et les accès aux logs/sauvegardes ; ne jamais verser les valeurs dans Git.
11. **RGPD-QUALITY-11 — Limites résiduelles (P2).** Renforcer l'anti-bruteforce distribué, clarifier l'historique `UserContract`, documenter la révocation JWT éventuelle et actualiser la source de vérité DevOps.

## Verdict

**GO pour la suite RGPD**, avec priorité à RGPD-EXPORT-04, à la formalisation des durées/contact et à la validation PostgreSQL locale. RGPD-MEDIA-02 reste volontairement bloqué en production par sa procédure de sauvegarde, dry-run et validation explicite.
**NO-GO pour déclarer WatYouFace conforme RGPD** tant que les informations validées de RGPD-INFO-03, les exports, la rétention, la sauvegarde/restauration, la procédure d'incident et la preuve PostgreSQL locale ne sont pas réalisés et validés au-delà du seul audit technique.
