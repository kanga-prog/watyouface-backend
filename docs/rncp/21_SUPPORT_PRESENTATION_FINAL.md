# WatYouFace — Support de présentation final (source)

> Source Markdown du futur PPTX de soutenance CDA niveau 6. État de référence :
> 18/10/2026. Durée cible : environ 20 minutes. Les faits et niveaux de preuve
> sont alignés sur \`RNCP6_SOURCE_DE_VERITE.md\`, \`18_PREUVES_RNCP_FINALES.md\`,
> \`19_DOSSIER_PROJET_FINAL.md\` et \`20_DOSSIER_PROFESSIONNEL_FINAL.md\`.

## Mode d'emploi du futur PPTX

- Une idée principale par slide, avec trois à cinq éléments visibles maximum.
- Préférer un schéma, une capture lisible ou un résultat chiffré au texte dense.
- Les notes ci-dessous sont orales : elles ne doivent pas être projetées.
- Anonymiser les captures ; ne jamais afficher de secret, cookie, jeton ou mot de passe.

## Timing global

| Séquence | Slides | Durée cible |
|---|---:|---:|
| Introduction, besoin, objectifs | 1 à 3 | 2 min 30 |
| Conception et fonctionnalités | 4 à 7 | 5 min 30 |
| Sécurité et administration | 8 à 9 | 3 min |
| Tests, CI et déploiement | 10 à 11 | 3 min |
| Décisions et recette | 12 à 13 | 3 min 30 |
| Recul et conclusion | 14 à 15 | 2 min 30 |
| **Total** | **15 slides** | **20 min** |

---

## Slide 1 — WatYouFace

**Objectif.** Poser le cadre et le fil conducteur.

**Visible.**

- WatYouFace
- Kouakou Brice Kanga
- Concepteur développeur d'applications — RNCP niveau 6
- Besoin → conception → sécurisation → preuves → recul

**Notes orateur.** Je présente une application web full-stack réunissant Feed,
Chat, Marketplace et modération. Le fil conducteur est de partir du besoin,
concevoir une architecture en couches, sécuriser les règles métier, tester,
déployer, puis présenter les preuves et les limites réelles.

**Transition.** Je commence par le besoin produit.

**Visuel conseillé.** Logo ou écran d'accueil anonymisé, s'il est disponible.

---

## Slide 2 — Contexte et besoin

**Objectif.** Expliquer la valeur du produit sans détailler tous les écrans.

**Visible.**

- Usages sociaux, conversationnels et vente réunis
- Une identité et des contrôles communs
- Feed · Chat · Marketplace · Administration
- Wallet : simulation métier, pas service bancaire

**Notes orateur.** WatYouFace centralise publier, communiquer et suivre une
transaction simulée. La Marketplace est le scénario métier le plus riche, mais
les trois piliers sont le Feed, le Chat et la Marketplace. Le wallet démontre
des règles de droits et de transaction sans prétendre être un moyen de paiement.

**Transition.** Ce besoin a conduit à des objectifs techniques et de sécurité.

**Preuve à montrer.** Une capture existante Feed ou Marketplace de
\`evidence/frontend/\`, sans la qualifier de recette E2E.

---

## Slide 3 — Objectifs du projet

**Objectif.** Relier le besoin aux attendus CDA.

**Visible.**

- Application web full-stack sécurisée
- Frontend, API et données séparés
- Temps réel avec WebSocket/STOMP
- Transactions Marketplace contrôlées
- Production réelle et preuves traçables

**Notes orateur.** Le backend est l'autorité sur les droits et les règles
métier. PostgreSQL porte les données transactionnelles. La démarche inclut le
déploiement reproductible et une documentation qui distingue les faits prouvés,
les validations partielles et les limites.

**Transition.** L'architecture rend cette séparation concrète.

---

## Slide 4 — Architecture générale

**Objectif.** Montrer les couches et le rôle central du backend.

**Visible.**

\`\`\`text
Navigateur
    ↓ HTTPS
React / Vite
    ↓ REST + WebSocket / STOMP
Spring Boot + Spring Security
    ↓
Services métier
    ↓
Repositories JPA
    ↓
PostgreSQL
\`\`\`

**Notes orateur.** React/Vite rend l'interface. REST et STOMP arrivent au
backend Spring Boot ; Spring Security filtre les accès. Les services portent les
règles et les contrôles d'ownership, puis les repositories JPA accèdent à
PostgreSQL. Une décision de sécurité ne dépend donc pas du seul frontend.

**Transition.** La stack choisie soutient cette architecture sans complexité
inutile.

**Preuve.** PRV-03 et \`17_DOCUMENTATION_FINALE.md\`.

---

## Slide 5 — Stack technique

**Objectif.** Donner les choix techniques significatifs.

**Visible.**

| Domaine | Technologies |
|---|---|
| Frontend | React 19.1, Vite 7.3.7 |
| Backend | Java 17, Spring Boot 3.3.5, Spring Security |
| Données | PostgreSQL, JPA/Hibernate 6.5.3, Flyway 10.10.0 |
| DevOps | Maven, Docker, GitHub Actions, Railway, Hostinger |
| Temps réel | WebSocket / STOMP |

**Notes orateur.** Java 17 est retenu pour sa stabilité LTS. Spring Boot et
Spring Security structurent l'API et les contrôles. PostgreSQL répond aux
contraintes relationnelles et transactionnelles. Docker rend le backend
reproductible ; Hostinger héberge le frontend et Railway l'API, la base et le
volume média.

**Transition.** Ces choix servent cinq blocs fonctionnels cohérents.

---

## Slide 6 — Fonctionnalités principales

**Objectif.** Présenter le périmètre en un regard.

**Visible.**

- **Feed** — publier, commenter, liker, modérer
- **Chat** — conversations privées par STOMP/WebSocket
- **Marketplace** — annonce, achat/vente, paiement simulé
- **ADMIN** — modération et crédit wallet de démonstration
- **Médias** — upload contrôlé, stockage persistant

**Notes orateur.** Le Feed porte les interactions sociales, le Chat les
conversations et la Marketplace le cycle transactionnel. L'ADMIN modère et gère
le crédit de démonstration, sans devenir acheteur ou vendeur universel. Ces
modules partagent une identité et des décisions de sécurité serveur.

**Transition.** Le cycle Marketplace rend particulièrement visible la gestion
des règles métier.

**Visuel conseillé.** Trois captures maximum : Feed, Chat et Marketplace déjà
archivées dans \`evidence/frontend/\`.

---

## Slide 7 — Marketplace : transitions contrôlées

**Objectif.** Expliquer les rôles et les états.

**Visible.**

\`\`\`text
AVAILABLE
    ↓ demande acheteur
PENDING
    ├── refus vendeur → REFUSED
    └── acceptation vendeur → ACCEPTED
                                ↓ paiement acheteur
                              PAID
                                ↓ expédition vendeur
                             SHIPPED
                                ↓ confirmation acheteur
                            RECEIVED
\`\`\`

- Solde insuffisant : **HTTP 409**
- Transition et acteur contrôlés par le backend

**Notes orateur.** L'acheteur demande, paie et confirme la réception ; le
vendeur accepte, refuse et expédie. Une transition invalide ou un solde
insuffisant est refusé côté backend. Le parcours nominal et REFUSED ont été
validés fonctionnellement en production, mais les traces et captures E2E
réutilisables restent à archiver.

**Transition.** Les états métier sont complétés par des contrôles de sécurité.

**Preuve.** PRV-06 et PRV-09 ; matrice \`E2E-MKT-01\` à \`E2E-MKT-07\`, PARTIAL
pour les traces E2E.

---

## Slide 8 — Sécurité intégrée aux flux

**Objectif.** Montrer une sécurité appliquée côté serveur et testée.

**Visible.**

- JWT cookie **HttpOnly**, \`Secure\` en production, \`SameSite=Lax\`
- CSRF sur mutations et CORS explicite
- RBAC ADMIN et contrôles d'ownership
- Protection BOLA/IDOR sur ressources et transactions
- Upload : MIME + décodage réel + 10 Mo + temporaire
- Secrets externalisés

**Notes orateur.** HttpOnly limite l'accès au JWT depuis JavaScript. Comme le
navigateur transporte le cookie, les mutations restent protégées par CSRF. Le
backend vérifie rôle et propriété afin qu'un appel direct ne permette pas d'agir
sur la ressource d'autrui. Pour les médias, le MIME annoncé ne suffit pas : le
contenu est décodé avant l'écriture finale.

**Transition.** L'ADMIN applique cette sécurité sans contourner le métier.

**Preuve.** PRV-07, PRV-08 et PRV-13 ; certains refus UI nécessitent encore des
captures.

---

## Slide 9 — Administration, modération et wallet démo

**Objectif.** Distinguer modération et rôle transactionnel.

**Visible.**

- Un seul ADMIN opérationnel : \`demo\`
- Rôle non mutable et dernier ADMIN protégé
- Crédit manuel : endpoint ADMIN uniquement
- Feed : suppression de modération
- Listing engagé : \`hidden=true\`, transaction préservée

**Notes orateur.** Il n'est pas possible de promouvoir ou rétrograder librement
un compte, et le dernier ADMIN est protégé. Le crédit wallet exige l'ADMIN, un
montant positif et une écriture transactionnelle. L'ADMIN ne contourne pas le
cycle Marketplace : AVAILABLE/REFUSED peut être supprimé ; une annonce engagée
est masquée pour les tiers par \`hidden=true\` sans casser la transaction.

**Transition.** Les règles ont été vérifiées dans les suites de tests et la CI.

**Preuve/visuel.** PRV-07 et \`BE-ADMIN-01\`. Les captures Admin/wallet restent
à archiver avant insertion dans le PPTX.

---

## Slide 10 — Tests : résultats exacts

**Objectif.** Donner les chiffres sans surdéclaration.

**Visible.**

- Backend Maven : **104 tests rapportés**, 0 failure, 0 error
- PostgreSQL dédié : **5/5 exécutés**, 0 skipped/failure/error
- Frontend : **47/47 PASS**
- Build Vite : **PASS**
- Lint : **0 erreur**, 1 warning documenté

**Notes orateur.** Les cinq tests PostgreSQL conditionnels de la suite Maven
globale sont ignorés sans profil dédié : je ne présente donc pas les 104 comme
104 tests PostgreSQL exécutés. \`PostgresPaymentIntegrationTests\` a été rejoué
sur PostgreSQL local : 5/5 réels. Le frontend compte 47 tests PASS et son build
Vite est validé.

**Transition.** Ces contrôles sont intégrés aux chaînes de livraison.

**Preuve.** PRV-06 et PRV-14. Montrer un résultat CI lisible plutôt qu'un
terminal dense.

---

## Slide 11 — CI/CD et production

**Objectif.** Distinguer l'automatisation frontend du backend.

**Visible.**

\`\`\`text
Frontend : GitHub → Actions → tests → build Vite → rsync SSH → Hostinger → smoke

Backend : GitHub → Actions → Maven + build Docker
           déploiement Railway manuel contrôlé → PostgreSQL Railway
\`\`\`

- Frontend CI/CD : run **39165084863** SUCCESS
- Backend CI : run **39165116209** SUCCESS

**Notes orateur.** Le frontend est déployé automatiquement vers Hostinger après
tests, build Vite, rsync SSH et smoke test. Le backend possède une CI Maven et
Docker ; son déploiement Railway est contrôlé et manuel. Je ne le présente pas
comme du CD automatique. L'API est sur Railway avec PostgreSQL et volume média
persistant ; le frontend est sur Hostinger.

**Transition.** Plusieurs incidents concrets ont guidé les décisions finales.

**Preuve.** PRV-10 à PRV-12. Une capture de workflow et une d'hébergement
maximum, seulement si archivées et anonymisées.

---

## Slide 12 — Difficultés et décisions techniques

**Objectif.** Montrer la démarche d'analyse et de résolution.

**Visible.**

| Difficulté | Décision / résultat |
|---|---|
| WebP non décodable fiablement | Formats réellement testés : JPEG/PNG/GIF/BMP |
| Git Hostinger inadapté à Vite | GitHub Actions + build + rsync SSH |
| Flyway sur base non vide | Migrations additives validées sur 37 listings |
| BOLA/IDOR Marketplace | Ownership vérifié côté backend |

**Notes orateur.** Je n'ai pas prétendu supporter WebP quand la stack Java ne le
décodait pas fiablement : le contrat a été réduit aux formats maîtrisés.
Hostinger déployait les sources Vite plutôt que \`dist\` ; le pipeline
Actions/build/rsync a remplacé ce fonctionnement. Les migrations V2/V3 ont été
validées sur PostgreSQL 16 avec 37 annonces, sans suppression ni NULL. Les
contrôles BOLA/IDOR sont serveur, donc indépendants du client.

**Transition.** Ces choix se retrouvent dans la recette de production.

**Preuve.** PRV-05, PRV-07, PRV-08 et PRV-12.

---

## Slide 13 — Recette E2E : résultat et preuve

**Objectif.** Montrer les validations sans confondre fonctionnement et capture.

**Visible.**

- Marketplace nominal + REFUSED : **fonctionnel validé**
- Solde insuffisant : **409 observé**
- Crédit ADMIN et modération Feed : **fonctionnels validés**
- Smoke : \`/api/users/me\` **401** ; CSRF **200** + \`XSRF-TOKEN\`
- Captures E2E et uploads : **à archiver**

**Notes orateur.** La recette fonctionnelle couvre le cycle nominal, le refus,
le paiement insuffisant, le crédit ADMIN et la modération. Les smoke tests
confirment une API sans session en 401 et le token CSRF en 200. Les traces E2E
réutilisables et les captures production des uploads JPEG/PNG/GIF/BMP restent
PARTIAL : elles ne sont pas présentées comme des preuves complètes.

**Transition.** Cette distinction conduit aux limites et améliorations.

**Preuve.** PRV-09, PRV-19 et PRV-20. Ne montrer qu'une capture réellement
archivée ; aucune illustration fictive.

---

## Slide 14 — Limites et améliorations

**Objectif.** Présenter un recul professionnel, non une conformité fictive.

**Visible.**

- Backup/restauration et rollback production non testés
- Observabilité limitée : pas de métriques/alertes centralisées prouvées
- JWT sans révocation centrale ; rate limiting mono-instance
- Pas d'antivirus/quarantaine média ; avatar à harmoniser
- RGPD/RGAA/RGESN : mesures engagées, conformité non certifiée
- Pas de CD backend ; warning Flyway/PostgreSQL 18 à surveiller

**Notes orateur.** Le projet est fonctionnel et déployé, mais je ne revendique
pas une exploitation industrielle complète. Les priorités suivantes sont une
sauvegarde/restauration testée, la santé et les métriques, la révocation JWT et
un scan de fichiers. Les sujets RGPD, RGAA et RGESN restent PARTIAL : des
mesures existent, mais aucun audit ni certification complète n'est revendiqué.

**Transition.** Les acquis sont à présenter avec leur niveau de preuve.

---

## Slide 15 — Conclusion

**Objectif.** Clore sur les acquis démontrés et les perspectives.

**Visible.**

- Concevoir : architecture et données relationnelles versionnées
- Développer : interfaces, API, métier et temps réel
- Sécuriser : RBAC, ownership, BOLA/IDOR, CSRF, uploads
- Tester et déployer : suites, CI, Docker, Railway, Hostinger
- Prochaine étape : fiabiliser l'exploitation avant d'automatiser davantage

**Notes orateur.** WatYouFace démontre un projet full-stack déployé avec une
architecture en couches, des règles Marketplace sécurisées, des tests et une
chaîne CI. Les niveaux PASS, PARTIAL et GAP restent visibles dans le dossier :
la démarche consiste autant à prouver ce qui est réalisé qu'à identifier ce qui
reste nécessaire pour une exploitation plus mature.

**Conclusion orale.** Je suis prêt à détailler un choix technique ou à montrer
une preuve archivée selon les questions du jury.

---

## Synthèse RNCP à mobiliser à l'oral

Cette synthèse est prévue dans les notes, pas comme une slide de onze lignes.

| Axe | Démonstration WatYouFace | Niveau à annoncer |
|---|---|---|
| Concevoir | besoins, architecture multicouche, PostgreSQL/Flyway | C6 PASS documentaire ; C5/C7 PARTIAL |
| Développer | React, Spring Boot, Feed/Chat/Marketplace/Admin | C2/C3/C8 PARTIAL, preuves ciblées disponibles |
| Sécuriser | JWT, CSRF, RBAC, ownership, BOLA/IDOR, uploads | tests PASS ciblés, couverture globale PARTIAL |
| Tester | Maven, PostgreSQL dédié, frontend, CI | C9 PARTIAL : traces E2E à compléter |
| Déployer | Docker, Railway, Hostinger, migrations, CI | C10/C11 PARTIAL : pas de CD backend/rollback/observabilité |

## Preuves visuelles à préparer pour le PPTX

| Priorité | Élément | Situation et règle d'usage |
|---:|---|---|
| 1 | Feed, Chat, Marketplace | Captures existantes : \`evidence/frontend/REAL-UI-01\` à \`09\`. Trois maximum dans le support. |
| 1 | Architecture et cycle | Schémas issus des slides 4 et 7 ; représentations, pas captures production. |
| 1 | Tests et CI | Captures anonymisées d'EV-10/EV-22 uniquement si réellement archivées. |
| 2 | Admin, wallet, modération, \`hidden=true\` | Fonctionnels validés ; captures E2E/production à archiver avant insertion. |
| 2 | Cycle complet, refus, 409 | Recette textuelle existante ; traces/captures HTTP à archiver. |
| 2 | Upload JPEG/PNG/GIF/BMP | Fonctionnels validés ; \`UPLOAD-*-CAPTURE\` à ajouter, sans visuel fictif. |
| 3 | Railway, Flyway, Hostinger | Captures anonymisées réelles seulement, sans secrets. |

## Annexe — questions jury anticipées

| Question | Réponse courte préparée |
|---|---|
| Pourquoi JWT ? | L'API reste stateless. Le JWT est en cookie HttpOnly pour limiter son accès par JavaScript. |
| Pourquoi HttpOnly et CSRF ? | Le cookie est envoyé automatiquement : HttpOnly protège la lecture, CSRF protège les mutations cross-site. |
| 401 ou 403 ? | 401 : pas d'authentification valide. 403 : authentifié mais non autorisé. |
| RBAC ? | Contrôle d'accès par rôle ; les routes ADMIN sont protégées côté backend. |
| BOLA/IDOR ? | Accès ou action sur la ressource d'autrui via un identifiant ; l'ownership est contrôlé côté serveur. |
| Flyway ? | Il versionne les migrations ; Hibernate \`validate\` contrôle le schéma sans le créer en production. |
| Pourquoi PostgreSQL ? | Relations, contraintes et transactions conviennent au cycle Marketplace ; périmètre dédié testé sur PostgreSQL réel. |
| Rôle de Tomcat ? | Serveur web embarqué Spring Boot qui reçoit les requêtes HTTP et exécute l'application Java. |
| Pourquoi Docker ? | Runtime Java 17 reproductible, build séparé et processus final non-root. |
| Pourquoi pas de CD backend ? | Railway reste manuel contrôlé : une automatisation tardive non éprouvée aurait accru le risque. |
| Pourquoi WebP refusé ? | ImageIO/Thumbnailator ne le décodait pas fiablement ; le contrat garde les formats réellement testés. |
| CI ou CD ? | CI : tests/build automatiques. CD : déploiement automatique ; prouvé pour Hostinger, pas Railway. |
| Transaction SQL ? | Préconditions d'état, ownership, transaction et contraintes évitent les incohérences du périmètre testé. |
| Rollback et sauvegarde ? | Applicatif documenté ; base non automatique. Sauvegarde/restauration testée reste une priorité, pas une preuve actuelle. |
| RGPD/RGAA ? | Mesures ciblées, mais ni conformité complète ni audit/certification ne sont revendiqués. |

## Répétition recommandée

1. Répéter une première fois en 18 minutes pour garder une marge de questions.
2. Vérifier que toute capture est lisible, anonymisée et réellement archivée.
3. Prévoir une démonstration de secours sans mutation de données de production.
4. Répondre avec le niveau de preuve réel : PASS, PARTIAL ou GAP.
