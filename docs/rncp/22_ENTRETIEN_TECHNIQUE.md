# WatYouFace — Préparation entretien technique CDA

> Préparation orale de Kouakou Brice Kanga pour l'entretien technique RNCP CDA
> niveau 6. Référence : 18/10/2026. Sources : RNCP6_SOURCE_DE_VERITE.md,
> 18_PREUVES_RNCP_FINALES.md, 19_DOSSIER_PROJET_FINAL.md,
> 20_DOSSIER_PROFESSIONNEL_FINAL.md et 21_SUPPORT_PRESENTATION_FINAL.md.
>
> Règle : annoncer ce qui est prouvé ; distinguer PARTIAL et GAP ; ne jamais
> exposer de secret, jeton, cookie ou variable sensible.

## Utilisation

- Commencer par la réponse courte, puis développer seulement sur relance.
- Donner un exemple WatYouFace plutôt qu'une définition abstraite.
- Conclure les sujets sensibles par la limite et l'évolution possible.
- Ne pas réciter : comprendre le raisonnement.

---

## 1. Architecture

### Décris l'architecture globale.

**Réponse courte.** WatYouFace est une application en couches : le navigateur
utilise React/Vite, qui communique en REST et STOMP/WebSocket avec Spring Boot.
Spring Security protège les accès ; les controllers délèguent aux services
métier ; les repositories JPA accèdent à PostgreSQL.

**Schéma à dire.**

~~~
Navigateur → React/Vite → REST + STOMP → Spring Security
           → Controllers → Services → Repositories JPA → PostgreSQL
~~~

**Exemple projet.** Le frontend peut afficher une action Marketplace, mais le
backend vérifie l'état du listing, l'acteur attendu et l'ownership avant toute
transition.

**Relance. Où est la sécurité ?** Dans Spring Security et les règles de
service. Le frontend améliore l'expérience mais n'est jamais une frontière de
sécurité.

### Pourquoi séparer frontend et backend ?

**Réponse courte.** Cette séparation isole l'interface de la logique métier, des
données et de la sécurité. Le backend devient l'autorité commune pour tous les
clients, y compris un client qui contournerait React.

### Pourquoi une architecture en couches ?

**Réponse courte.** Le controller porte le transport HTTP, le service les règles
métier et transactions, le repository la persistance. Cette séparation rend le
code plus testable et limite le couplage.

### Quel est le rôle de chaque couche ?

| Élément | Réponse courte |
|---|---|
| Controller | Reçoit la requête, valide le contrat HTTP, récupère l'identité et délègue. |
| Service | Applique métier, ownership et transactions ; décide une transition Marketplace. |
| Repository | Encapsule l'accès JPA aux données ; ne décide pas les droits d'un utilisateur. |
| DTO | Contrat API qui évite d'exposer des entités ou champs internes inutiles. |
| PostgreSQL | Persistance relationnelle, contraintes et transactions pour utilisateurs, listings et wallet démo. |
| Flyway | Migrations SQL versionnées, puis Hibernate validate contrôle le schéma. |
| Tomcat | Serveur HTTP embarqué Spring Boot. |
| Docker | Runtime Java 17 reproductible ; processus final non-root sur Railway. |

### Pourquoi React/Vite et Spring Boot ?

**Réponse courte.** React/Vite répond au besoin d'interface moderne et de build
rapide. Spring Boot structure REST, sécurité, JPA et tests. C'est un choix
cohérent pour WatYouFace, pas une solution universelle.

**Piège à éviter.** Ne pas dire que React sécurise l'application ou que Docker
remplace la sécurité du code.

---

## 2. Authentification et autorisation

### Authentification ou autorisation ?

**Réponse courte.** L'authentification répond à qui êtes-vous ; l'autorisation
répond à avez-vous le droit. Un USER connecté est authentifié mais reçoit 403
sur une route ADMIN.

### Pourquoi JWT ?

**Réponse courte.** Le JWT porte une identité signée et permet une API stateless
sans session applicative classique. Limite : il n'existe pas encore de révocation
centrale WatYouFace.

### Pourquoi un cookie HttpOnly, Secure et SameSite=Lax ?

**Réponse courte.** HttpOnly limite la lecture par JavaScript ; Secure impose
HTTPS en production ; SameSite=Lax réduit certains envois cross-site. Ces mesures
se complètent et ne remplacent ni CSRF ni les contrôles serveur.

### Pourquoi CSRF si le JWT est signé ?

**Réponse courte.** La signature protège l'intégrité du JWT, mais le navigateur
peut envoyer un cookie automatiquement à une requête cross-site. Le token CSRF
prouve que la mutation vient du client attendu.

### 401 ou 403 ?

**Réponse courte.** 401 signifie absence ou invalidité d'authentification ; 403
signifie identité connue mais droit insuffisant. Sans session, GET /api/users/me
répond 401 ; un USER sur /api/admin répond 403.

### RBAC, ownership et BOLA/IDOR ?

**Réponse courte.** RBAC contrôle l'accès par rôle. Ownership vérifie que l'objet
appartient à l'acteur attendu. BOLA/IDOR désigne un accès à l'objet d'autrui par
identifiant sans contrôle suffisant.

**Exemple projet.** Un tiers ne peut pas faire une transition sur la transaction
d'un listing qu'il ne possède pas. L'ADMIN modère, mais ne devient pas un
acheteur ou vendeur universel.

### Rôle de Spring Security et SecurityFilterChain

**Réponse courte.** Spring Security applique authentification, règles d'accès,
CSRF et CORS. SecurityFilterChain définit l'ordre des filtres et les protections
des routes.

**Piège à éviter.** Masquer un bouton React n'est pas une autorisation.

---

## 3. JWT en profondeur

| Question | Réponse courte | Réponse détaillée / relance |
|---|---|---|
| Que contient un JWT ? | Des claims : identité, rôles ou expiration utiles. | Ne pas placer de secret ou de donnée sensible non nécessaire dans les claims. |
| Est-il chiffré ? | Non : signé ne signifie pas chiffré. | Le contenu peut être lu ; une modification doit invalider la signature. |
| Peut-on lui faire confiance ? | Après vérification serveur de signature et expiration. | On ne fait jamais confiance à un claim fourni sans validation de signature. |
| Comment est-il signé ? | Par le serveur avec une clé secrète non exposée. | La clé ne doit jamais être versionnée ni affichée. |
| Que signifie exp ? | La date d'expiration du token. | Après expiration, le backend doit le refuser. |
| Si le token est volé ? | Il peut être utilisé jusqu'à expiration. | HttpOnly, HTTPS et Secure réduisent l'exposition ; pas de révocation centrale aujourd'hui. |
| Pourquoi pas localStorage ? | Le token serait lisible par JavaScript en cas de XSS. | HttpOnly limite cette lecture directe, sans annuler le besoin de prévenir XSS. |
| Comment fonctionne logout ? | Suppression du cookie navigateur. | Un token déjà copié ne peut pas être révoqué centralement dans l'état actuel. |

---

## 4. CSRF, CORS et API

### CSRF

**Réponse courte.** Une attaque CSRF utilise les cookies envoyés automatiquement
par le navigateur pour provoquer une mutation au nom d'une victime. WatYouFace
obtient un token via /api/auth/csrf et l'envoie sur les mutations ; la recette a
observé HTTP 200 avec XSRF-TOKEN.

### CORS

**Réponse courte.** CORS contrôle quels origins navigateur peuvent appeler l'API.
WatYouFace utilise une allowlist explicite et n'autorise les credentials que pour
des origins connus.

### Différence à formuler

**Réponse courte.** CORS est une politique navigateur sur les appels JavaScript ;
CSRF protège une mutation transportée par cookie. CORS ne protège ni Postman ni
curl : l'API doit toujours vérifier identité, rôle et ownership.

---

## 5. Marketplace et transactions SQL

### Cycle Marketplace

~~~
AVAILABLE → PENDING → ACCEPTED → PAID → SHIPPED → RECEIVED
                  └→ REFUSED
~~~

| Transition | Acteur autorisé |
|---|---|
| AVAILABLE vers PENDING | acheteur |
| PENDING vers ACCEPTED ou REFUSED | vendeur |
| ACCEPTED vers PAID | acheteur |
| PAID vers SHIPPED | vendeur |
| SHIPPED vers RECEIVED | acheteur |

### Pourquoi le backend contrôle-t-il les transitions ?

**Réponse courte.** Le frontend est contournable. Le backend vérifie l'état,
l'acteur et les préconditions pour empêcher une transition illégale.

### Qu'est-ce qu'une transaction SQL ?

**Réponse courte.** C'est un groupe atomique d'opérations : soit toutes
réussissent, soit aucune n'est validée.

### Exemple paiement WatYouFace

**Réponse courte.** Les contrôles, l'écriture de paiement, les mises à jour de
solde et d'état restent cohérents grâce à la logique transactionnelle. Sans
transaction, un wallet pourrait être débité sans paiement enregistré.

### Solde insuffisant : pourquoi HTTP 409 ?

**Réponse courte.** Le paiement est compris mais incompatible avec l'état métier
actuel, donc il est refusé sans écriture partielle avec HTTP 409.

### Quel est le rôle du wallet ?

**Réponse courte.** C'est un wallet de démonstration Marketplace, pas un système
bancaire. Le crédit manuel est réservé à l'ADMIN avec montant positif et
traitement transactionnel.

---

## 6. PostgreSQL, JPA et Hibernate

| Question | Réponse courte | Précision défendable |
|---|---|---|
| JPA ou Hibernate ? | JPA est une spécification ; Hibernate est l'ORM utilisé. | Hibernate réalise le mapping et les accès selon JPA. |
| Entity ? | Modèle persistant mappé à une table. | Elle n'est pas forcément le contrat API ; les DTO limitent l'exposition. |
| Pourquoi PostgreSQL ? | Relations, contraintes et transactions adaptées au métier. | C'est aussi le moteur de production. |
| Pourquoi pas H2 seul ? | H2 peut différer de PostgreSQL. | SQL, types et transactions réels motivent les tests PostgreSQL dédiés. |
| Résultat PostgreSQL ? | PostgresPaymentIntegrationTests : 5/5 réels. | 0 skipped, 0 failure, 0 error. |
| Relations et contraintes ? | Elles structurent et protègent les invariants. | Elles complètent les contrôles métier, elles ne les remplacent pas. |
| Lazy loading / open-in-view ? | Lazy charge une relation au besoin ; open-in-view peut garder le contexte plus longtemps. | C'est un sujet qualité à surveiller, pas une optimisation automatique. |

---

## 7. Flyway

### Quel est son rôle ?

**Réponse courte.** Flyway applique les migrations SQL versionnées dans l'ordre
et conserve leur état dans la table flyway_schema_history. Hibernate validate
contrôle ensuite la cohérence sans créer le schéma en production.

### V1, V2, V3

| Version | Rôle et preuve |
|---|---|
| V1 | Baseline relationnel versionné. |
| V2 | Ajout created_at aux listings. |
| V3 | Ajout du masquage hidden. |
| Validation V2/V3 | PostgreSQL 16, 37 listings, aucune suppression, 0 NULL. |

### Pourquoi tester une base non vide ?

**Réponse courte.** Une migration valide sur une base vide peut échouer lorsqu'il
existe déjà des lignes. Le test sur 37 listings vérifie l'ajout et le backfill.

### Warning PostgreSQL 18

**Réponse courte.** Production PostgreSQL 18.6, Flyway 10.10.0 avertit que la
version n'est pas officiellement testée. Les migrations ont réussi, mais le
warning reste à surveiller avant toute évolution.

### Rollback

**Réponse courte.** Il n'est pas automatique ici. Préférer une migration
corrective forward et une sauvegarde avant migration critique. Backup/restore
réel reste un GAP.

---

## 8. Uploads Feed et Marketplace

### Politique réelle

- Formats : JPEG, PNG, GIF, BMP.
- Limite : 10 Mo.
- Validation : MIME autorisé puis décodage réel du contenu.
- Stockage : nom UUID généré côté serveur, protection de chemin.
- Fiabilité : temporaire, conversion et déplacement final après succès, nettoyage sur erreur.
- Sortie : JPEG ; GIF devient image fixe de première frame.
- Refus volontaires : WebP, AVIF, HEIC/HEIF.

### Questions probables

| Question | Réponse courte |
|---|---|
| Pourquoi ne pas faire confiance à l'extension ? | Elle peut être falsifiée ; MIME et décodage réel complètent le contrôle. |
| Pourquoi un fichier temporaire ? | Aucun fichier final ne persiste si conversion ou lecture échoue. |
| Pourquoi un UUID ? | Réduit collisions et risques liés au nom fourni par l'utilisateur. |
| Pourquoi WebP refusé ? | ImageIO/Thumbnailator ne le décodait pas fiablement ; mieux vaut un 400 qu'un 500. |
| Quel risque demeure ? | Absence d'antivirus/quarantaine ; l'upload avatar est un flux séparé à harmoniser. |

---

## 9. ADMIN, RBAC et modération

### Quel est le rôle de l'ADMIN ?

**Réponse courte.** L'ADMIN modère les contenus et crédite le wallet de
démonstration. Il ne contourne pas les actions normales vendeur/acheteur.

### Pourquoi un seul ADMIN ?

**Réponse courte.** Le périmètre actuel utilise le compte demo ; la mutation de
rôle est retirée afin d'éviter une promotion libre. En exploitation réelle, une
gouvernance des comptes admins serait à formaliser.

### Protection du dernier ADMIN

**Réponse courte.** Auto-suppression, suppression ou rétrogradation du dernier
ADMIN sont refusées par le backend avec une erreur métier explicite.

### Suppression physique ou hidden=true ?

**Réponse courte.** AVAILABLE et REFUSED sont supprimables. Une transaction
engagée est masquée par hidden=true pour préserver l'historique et l'intégrité ;
les tiers ne la voient plus, les acteurs nécessaires conservent l'accès.

---

## 10. WebSocket et STOMP

### HTTP ou WebSocket ?

**Réponse courte.** HTTP est surtout requête/réponse. WebSocket maintient une
connexion bidirectionnelle persistante, adaptée aux messages de Chat.

### Quel est le rôle de STOMP ?

**Réponse courte.** STOMP structure les messages au-dessus de WebSocket avec des
destinations et abonnements.

### Limite actuelle

**Réponse courte.** Le Chat existe et le périmètre de sécurité est testé, mais
les E2E STOMP complets et les tests de charge ne sont pas démontrés de façon
exhaustive. Il faut dire PARTIAL.

---

## 11. Tests, CI et déploiement

### Chiffres exacts

| Élément | Résultat à annoncer |
|---|---|
| Backend Maven | 104 tests rapportés ; 0 failure ; 0 error ; 5 tests PostgreSQL conditionnels skipped |
| PostgreSQL dédié | 5/5 exécutés ; 0 skipped ; 0 failure ; 0 error |
| Frontend | 47/47 PASS |
| Build Vite | PASS |
| Lint | 0 erreur ; 1 warning documenté |
| CI backend | run 39165116209 : Maven et build Docker SUCCESS |
| CI/CD frontend | run 39165084863 : tests, build, rsync Hostinger, smoke SUCCESS |

### Pourquoi ne pas dire 104 sur 104 exécutés ?

**Réponse courte.** Parce que cinq tests PostgreSQL conditionnels sont skipped
dans la suite globale sans profil dédié. Le run PostgreSQL séparé prouve 5/5
réels : c'est la formulation exacte.

### Unit, intégration, E2E

**Réponse courte.** Unitaire : logique isolée ; intégration : composants réels
ensemble, comme PostgreSQL ; E2E : parcours utilisateur. La recette E2E est
fonctionnellement validée, mais plusieurs traces/captures restent PARTIAL.

### CI ou CD ?

**Réponse courte.** CI signifie tests/build automatisés. CD signifie déploiement
automatisé. Le frontend a un CI/CD Hostinger prouvé ; le backend a une CI et un
déploiement Railway manuel contrôlé, donc pas de CD backend revendiqué.

### Hostinger et Railway

**Réponse courte.** Hostinger héberge le frontend statique compilé par Vite,
déployé par rsync SSH. Railway exécute le backend Docker, PostgreSQL et le
volume persistant /app/media. Les secrets sont externalisés.

---

## 12. Sécurité résiduelle, conformité et exploitation

Utiliser le format : risque → mesure actuelle → limite → amélioration future.

| Sujet | Réponse défendable |
|---|---|
| Rate limiting | In-memory utile sur une instance ; non partagé horizontalement ; évolution Redis ou limiteur distribué. |
| Révocation JWT | Expiration, HttpOnly et HTTPS ; pas de blacklist centrale ; évolution par TTL, version de token ou session stockée. |
| Médias | MIME, décodage, UUID, taille, temporaire ; pas d'AV/quarantaine ; évolution scan asynchrone. |
| Observabilité | Logs et CI disponibles ; métriques/alertes non prouvées ; évolution Actuator/Micrometer/alerting. |
| Backup/restore | Procédure envisageable ; restauration réelle non testée ; priorité pg_dump/pg_restore et volume média en environnement isolé. |
| Rollback | Applicatif documenté ; base non automatique ; backup avant migration et migration corrective forward. |
| Flyway/PG18 | Migrations réussies ; warning conservé ; vérifier compatibilité et tester avant évolution. |
| RGPD | Sécurité/minimisation partielles ; rétention, droits et suppression complète à formaliser ; pas de conformité juridique déclarée. |
| RGAA | Labels, alternatives et focus corrigés ; audit formel clavier/zoom/contraste/lecteur incomplet. |
| RGESN | Bundle et médias considérés ; pistes pagination/optimisation ; évaluation complète absente. |

---

## 13. Questions pièges — 25 réponses prêtes

### 1. Pourquoi JWT plutôt qu'une session serveur ?

**Courte.** API stateless et identité signée.
**Développée.** Contrepartie : sans révocation centrale, un token volé reste
utilisable jusqu'à expiration. HttpOnly, HTTPS et Secure limitent l'exposition.

### 2. Si JWT est HttpOnly, pourquoi CSRF ?

**Courte.** Le navigateur envoie le cookie automatiquement.
**Développée.** La signature JWT ne distingue pas une mutation légitime d'une
requête cross-site ; le token CSRF le fait.

### 3. CORS protège-t-il Postman ?

**Courte.** Non, CORS est appliqué par le navigateur.
**Développée.** RBAC et ownership serveur restent indispensables.

### 4. Peut-on modifier un JWT ?

**Courte.** Oui textuellement, mais la signature devient invalide.
**Développée.** Signé ne veut pas dire chiffré : ne pas mettre de secret dans les claims.

### 5. Pourquoi 403 et non 401 ?

**Courte.** Le USER est connecté mais n'a pas le droit.
**Développée.** 401 indique absence ou invalidité de l'identité.

### 6. Que se passe-t-il si PostgreSQL tombe pendant un paiement ?

**Courte.** La transaction ne doit pas se valider partiellement.
**Développée.** Les pannes complètes et observabilité avancée restent à approfondir.

### 7. Deux acheteurs paient simultanément ?

**Courte.** Règles transactionnelles, contraintes et états visent la cohérence.
**Développée.** Tests PostgreSQL ciblés de concurrence/rollback ; pas de test de charge industriel déclaré.

### 8. Pourquoi hidden=true ?

**Courte.** Masquer une transaction engagée sans casser l'historique.
**Développée.** AVAILABLE/REFUSED peut être supprimé ; les états engagés sont modérés logiquement.

### 9. Pourquoi WebP refusé ?

**Courte.** Décodage Java non fiable dans la stack actuelle.
**Développée.** Le contrat privilégie JPEG/PNG/GIF/BMP testés plutôt qu'un support fragile provoquant des 500.

### 10. ADMIN unique : risque ?

**Courte.** Point de dépendance opérationnel.
**Développée.** Le dernier ADMIN est protégé et la promotion libre retirée ; une gouvernance formalisée serait nécessaire en exploitation.

### 11. Comment restaurer la base ?

**Courte.** Ce n'est pas prouvé : GAP.
**Développée.** Procédure visée : pg_dump, restauration isolée pg_restore ou psql, sauvegarde volume média et contrôle de cohérence.

### 12. Comment scaler le rate limiter ?

**Courte.** Redis ou limiteur distribué.
**Développée.** Le limiter in-memory actuel n'est pas partagé entre instances.

### 13. Comment révoquer un JWT ?

**Courte.** Ce n'est pas implémenté centralement.
**Développée.** Blacklist TTL, version de token ou session stockée sont des options à évaluer.

### 14. Comment surveiller les 500 ?

**Courte.** Logs Railway/applicatifs aujourd'hui ; pas d'alerting prouvé.
**Développée.** Évolution : Actuator, Micrometer, métriques de latence/erreurs et alertes.

### 15. Comment rollbacker Flyway ?

**Courte.** Pas de rollback automatique déclaré.
**Développée.** Migration corrective forward et sauvegarde avant changement critique.

### 16. Pourquoi pas Hibernate update en production ?

**Courte.** Trop peu traçable et risqué.
**Développée.** Flyway versionne les changements ; Hibernate validate contrôle le résultat.

### 17. Pourquoi PostgreSQL réel si H2 passe ?

**Courte.** H2 n'est pas PostgreSQL.
**Développée.** Types, SQL et transactions peuvent différer ; les 5 tests dédiés réduisent ce risque.

### 18. Le frontend empêche-t-il un IDOR ?

**Courte.** Non.
**Développée.** Le frontend guide ; le backend contrôle l'ownership même pour une requête construite hors UI.

### 19. Pourquoi normaliser en JPEG ?

**Courte.** Format final maîtrisé et stockage uniforme.
**Développée.** GIF devient première frame fixe ; ne jamais annoncer conservation de l'animation.

### 20. Comment éviter un fichier partiel ?

**Courte.** Temporaire, déplacement final après succès, nettoyage sur erreur.
**Développée.** UUID et protection de chemin complètent ce mécanisme.

### 21. Pourquoi ne pas supprimer une annonce PAID ?

**Courte.** Pour préserver historique et intégrité transactionnelle.
**Développée.** hidden=true exclut les tiers tout en conservant l'accès vendeur/acheteur/ADMIN nécessaire.

### 22. Quel statut RGPD/RGAA ?

**Courte.** RGPD PARTIAL/GAP, RGAA PARTIAL, RGESN PARTIAL.
**Développée.** Des mesures existent mais aucun audit complet, ni conformité certifiée, n'est revendiqué.

### 23. Pourquoi Docker sur Railway ?

**Courte.** Runtime reproductible, Java 17 et processus non-root.
**Développée.** Le volume média est initialisé pour spring ; les secrets restent variables d'environnement.

### 24. Votre backend a-t-il du CD automatique ?

**Courte.** Non.
**Développée.** CI Maven/Docker prouvée ; Railway manuel contrôlé. Le CD prouvé concerne le frontend Hostinger.

### 25. Production validée sans captures : est-ce une preuve complète ?

**Courte.** Non : fonctionnel validé, preuve visuelle PARTIAL.
**Développée.** C'est le statut retenu pour plusieurs E2E et uploads afin de ne pas transformer une validation manuelle en capture archivée.

---

## 14. Mini fiches mémo

### A. Sécurité

- JWT signé en cookie HttpOnly, Secure en production, SameSite=Lax.
- CSRF pour mutations ; CORS explicite pour le navigateur.
- RBAC ADMIN, ownership et protection BOLA/IDOR côté backend.
- Upload : MIME, décodage réel, 10 Mo, UUID, temporaire et nettoyage.
- Limites : révocation JWT, AV média, rate limiter distribué absents.

### B. Base de données

- PostgreSQL Railway ; JPA spécification, Hibernate ORM.
- Flyway V1 à V3 ; Hibernate validate.
- V2/V3 : test PostgreSQL 16 avec 37 listings, 0 perte, 0 NULL.
- Tests PostgreSQL dédiés : 5/5 réels.
- Limites : backup/restore non testés ; warning Flyway/PostgreSQL 18.

### C. Architecture

- React/Vite → REST/STOMP → Spring Security → Controllers → Services → JPA → PostgreSQL.
- Controller HTTP ; Service métier/sécurité/transaction ; Repository persistance.
- DTO protège le contrat API et limite l'exposition.
- Frontend utile pour UX, jamais frontière de sécurité.

### D. CI/CD

- Backend : CI Maven/Docker, run 39165116209 SUCCESS.
- Frontend : tests/build/rsync/smoke Hostinger, run 39165084863 SUCCESS.
- Railway backend : manuel contrôlé.
- Ne jamais dire CD backend automatique.

### E. Marketplace

- AVAILABLE → PENDING → ACCEPTED → PAID → SHIPPED → RECEIVED.
- Variante PENDING → REFUSED.
- Acheteur demande/paye/reçoit ; vendeur accepte/refuse/expédie.
- Solde insuffisant : 409 ; ADMIN ne bypass pas les transitions.
- Listing engagé modéré : hidden=true.

### F. Tests

- Maven : 104 rapportés, 0 failure/error, 5 PG conditionnels skipped.
- PostgreSQL dédié : 5/5, 0 skip/failure/error.
- Frontend : 47/47 PASS ; build PASS ; lint 0 erreur, 1 warning.
- E2E fonctionnel validé, plusieurs traces/captures PARTIAL.

### G. Conformité

- RGPD : sécurité/minimisation partielles ; rétention/droits à formaliser.
- RGAA : corrections ciblées ; audit formel incomplet.
- RGESN : mesures et pistes ; évaluation complète absente.
- Ne jamais dire 100 % conforme.

---

## 15. Simulation courte — 20 questions rapides

| # | Q | R attendue | Relance possible |
|---:|---|---|---|
| 1 | Où est la frontière sécurité ? | Backend Spring Security et services. | Exemple BOLA ? |
| 2 | 401 ou 403 ? | 401 sans identité, 403 sans droit. | Route ADMIN ? |
| 3 | JWT chiffré ? | Non, signé. | Pourquoi pas de secret dans claims ? |
| 4 | Pourquoi CSRF ? | Cookie envoyé automatiquement. | CORS suffit-il ? |
| 5 | Pourquoi HttpOnly ? | Limite lecture JavaScript. | Limite restante ? |
| 6 | Qui paie ? | Acheteur après ACCEPTED. | Rôle vendeur ? |
| 7 | Solde insuffisant ? | 409, sans écriture partielle. | Pourquoi transaction ? |
| 8 | Pourquoi hidden=true ? | Masque sans casser historique. | Qui conserve accès ? |
| 9 | Pourquoi Flyway ? | Schéma versionné, Hibernate valide. | V1/V2/V3 ? |
| 10 | Pourquoi PostgreSQL réel ? | H2 peut différer. | Pourquoi 5/5 ? |
| 11 | Pourquoi WebP refusé ? | Codec non fiable. | Formats supportés ? |
| 12 | Fichier upload résiduel ? | Temporaire puis nettoyage. | Taille max ? |
| 13 | CI backend ? | Maven + Docker, run 39165116209. | CD Railway ? |
| 14 | CI/CD frontend ? | tests/build/rsync/smoke, run 39165084863. | Pourquoi rsync ? |
| 15 | Docker ? | Runtime reproductible non-root. | Secrets où ? |
| 16 | WebSocket ? | Chat bidirectionnel persistant. | STOMP ? |
| 17 | Limiteur actuel ? | In-memory mono-instance. | Scaling ? |
| 18 | Backup restauré ? | Non, GAP. | Plan ? |
| 19 | RGPD/RGAA ? | PARTIAL, pas certifié. | Gaps ? |
| 20 | Principal recul ? | Prouver limites : backup, observabilité, CD, traces E2E. | Priorité suivante ? |

## 16. Checklist avant entretien

- Dire l'architecture en moins de 45 secondes.
- Connaître les chiffres, y compris les 5 tests PostgreSQL conditionnels skipped.
- Expliquer une transition Marketplace et un refus BOLA/IDOR.
- Distinguer CI backend, CI/CD frontend et Railway manuel.
- Ne pas promettre backup/restore, rollback, conformité ou CD backend.
- Citer les runs 39165116209 et 39165084863 seulement pour leur périmètre réel.
- Formuler honnêtement : uploads/E2E fonctionnels validés, preuves visuelles PARTIAL.
