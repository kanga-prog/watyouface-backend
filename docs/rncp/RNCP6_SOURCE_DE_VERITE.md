# WatYouFace — Source de vérité RNCP 6 CDA

Ce fichier indexe les décisions et preuves RNCP. En cas de divergence, le code versionné prouve l'implémentation ; les documents RNCP expliquent l'intention, le périmètre et les limites. Toute divergence doit être relevée comme une question ouverte, jamais masquée.

## Références

- Audit initial : `/home/kanga_prog_unix/RNCP6_WATYOUFACE_AUDIT_INITIAL.md`
- Sécurisation : `/home/kanga_prog_unix/RNCP6_SECURITY_HARDENING_REPORT.md`
- Matrice sécurité : `security-test-matrix.md`
- Cadrage : `01_CADRAGE_WATYOUFACE.md` à `07_BACKLOG_INITIAL.md`

## PHASE 1 — CADRAGE

| Champ | Valeur |
|---|---|
| Statut | terminé ; cadrage corrigé et approuvé comme base de la phase 2 |
| Date | 07/10/2026 |
| Documents | `01` cadrage, `02` vision, `03` acteurs, `04` périmètre/MVP, `05` contraintes/risques, `06` livrables, `07` backlog |
| Décisions | **Vision fonctionnelle : feed social, chat et marketplace sont les trois piliers** ; marketplace = scénario technique le plus riche, pas unique cœur ; wallet = démonstration ADMIN ; USER/ADMIN = rôles techniques ; vidéos/admin = secondaires |
| Problématique fondatrice | réduire la dispersion des usages sociaux, conversationnels et marketplace en les réunissant dans un environnement unique avec identité centralisée et contrôles serveur, sans prétendre supprimer les risques de données personnelles |
| Périmètre | accès, profil, feed social, chat autorisé, marketplace, wallet démo, sécurité/tests |
| MVP | trois piliers : feed (post, permissions, interactions), chat (conversation, appartenance, messages) et marketplace (annonce, cycle, paiement simulé) |
| Risques | sur-périmètre, rotation secrets, documentation/tests, déploiement/CI, conformité et responsabilité accrue sur les données centralisées |
| Livrables | voir `06_LIVRABLES.md` |
| Étape suivante | PHASE 2 — analyse détaillée des besoins, après validation |

### DECISION — VISION FONCTIONNELLE

Les trois piliers fonctionnels de WatYouFace sont, à poids produit équivalent :

1. **Feed social** : consulter le fil, publier, modifier/supprimer son contenu, commenter et liker selon les règles retenues ;
2. **Chat** : conversation entre utilisateurs, envoi/réception de messages et discussion sociale ou relative à une annonce ;
3. **Marketplace** : publier/consulter une annonce, demander un achat, accepter/refuser, négocier via le chat, exécuter un paiement simulé, expédier et confirmer la réception.

La marketplace reste le scénario technique le plus riche pour certaines preuves RNCP en raison de ses règles d'état, transactions et contrôles, mais elle n'est pas le seul cœur fonctionnel. Cette vision répond au problème fondateur : réduire la dispersion des usages et centraliser identité et contrôles dans un même environnement, sans prétendre réduire automatiquement les risques sur les données personnelles.

## Table de traçabilité documentaire

| Document | Compétence RNCP | Preuve | Utilisation dossier | Utilisation slide | Utilisation entretien |
|---|---|---|---|---|---|
| `01_CADRAGE_WATYOUFACE.md` | C4, C5, C6 | problème, décisions, limites | contexte / besoins | vision | pourquoi ce périmètre |
| `02_VISION_OBJECTIFS.md` | C3, C4, C5, C9 | objectifs mesurables | objectifs / critères | objectifs | indicateurs |
| `03_ACTEURS_PERSONAS.md` | C2, C3, C4, C5 | rôles, droits, besoins | utilisateurs / use cases | personas | choix de droits |
| `04_PERIMETRE_MVP.md` | C2-C11 | MoSCoW et parcours des trois piliers | spécifications | MVP / fil rouge | arbitrages |
| `05_CONTRAINTES_RISQUES.md` | C4-C6, C9-C11 | NFR et registre | contraintes / risques | risques | décisions techniques |
| `06_LIVRABLES.md` | C1-C11 | checklist de préparation | plan du dossier | roadmap | état de préparation |
| `07_BACKLOG_INITIAL.md` | C4, C5, C9-C11 | backlog priorisé | gestion projet | backlog | méthode de suivi |
| `security-test-matrix.md` | C3, C8, C9 | tests sécurité exécutés | plan/rapport tests | résultats | défense des choix |
| `RNCP6_SECURITY_HARDENING_REPORT.md` | C1, C3, C8-C10 | avant/après, tests | sécurité | correction critique | AppSec |
| `17_BACKLOG_CONSOLIDE.md` à `24_PLAN_PREUVES.md` | C1-C11 | exécution, planning, risques, Git et preuves | gestion projet | roadmap | pilotage |

## Correspondance RNCP mise à jour

| Compétence | Cadrage des trois piliers |
|---|---|
| C2 | interfaces du feed, du chat et de la marketplace |
| C3 | règles métier, permissions et transitions des trois domaines |
| C4 | périmètre, risques, livrables et backlog |
| C5 | besoins, acteurs, personas et parcours des trois piliers |
| C6 | architecture reliant frontend, API, données et temps réel des trois domaines |
| C7/C8 | modèles et accès aux données social, messagerie et marketplace à produire/analyser |
| C9 | tests des règles métier, de l'appartenance et des permissions |
| C10/C11 | planifiés seulement : déploiement et DevOps non encore réalisés |

## États de preuve actuels

Les documents de cadrage prouvent une démarche de projet, non le résultat des phases suivantes. Docker, CI/CD, déploiement, modèle de données définitif, maquettes et dossier de certification restent **NON PRODUITS** au 07/10/2026.

## PHASE 2 — ANALYSE DÉTAILLÉE DES BESOINS

| Champ | Valeur |
|---|---|
| Statut | terminée, en attente de validation avant phase 3 |
| Date | 07/10/2026 |
| Documents | `08_EXIGENCES_FONCTIONNELLES.md` à `16_TRACABILITE_BESOINS_RNCP.md` |
| User stories | 20 stories réparties sur Auth, Profile, Social, Messaging, Marketplace, Wallet et Administration |
| Use cases | UC-01 à UC-13 : inscription, connexion, feed, chat, marketplace, wallet simulé et administration limitée |
| Règles métier | 29 règles/ensembles de règles, dont ownership social, appartenance conversation et machine à états marketplace |
| Écarts | 17 gaps consignés ; P0 : acceptation contrat au login, auto-like/endpoints legacy Like, lecture de messages diagnostic sans appartenance |
| Décisions | exigences produit conservées indépendamment du code ; chat marketplace = besoin, lien technique annonce-conversation à décider ; wallet = simulation |
| Questions ouvertes | visibilité/pagination, politique groupes, négociation annonce, RGPD/rétention, règles de modification annonce PENDING |
| Étape suivante | PHASE 3 — gestion de projet détaillée, après validation |

Les documents de phase 2 ne modifient aucun code. Ils constituent la référence des corrections et tests futurs : toute évolution doit mettre à jour l'exigence, le critère, le test et la matrice exigence/code associés.

## PHASE 3 — GESTION DE PROJET DÉTAILLÉE

| Champ | Valeur |
|---|---|
| Statut | terminée, en attente de validation avant phase 4 |
| Date | 07/10/2026 |
| Documents | `17_BACKLOG_CONSOLIDE.md` à `24_PLAN_PREUVES.md` |
| Priorités | P0 avant P1 sauf dépendance documentée ; P2/P3 ne retardent pas le MVP |
| P0 | contrat/login, Like, lecture chat, rotation secrets, MCD/migrations, tests, Docker, CI, preuves/dossiers/jury |
| Planning | 07/10 à 17/10, jalons M1 à M11 | 
| Décisions | gel MVP trois piliers ; marketplace scénario technique riche ; petits lots testés ; preuve obligatoire à chaque jalon |
| Risques | retard, régression, secrets, BOLA, données, déploiement/CI, dossier et démonstration |
| Étape suivante | PHASE 4 — UX / WIREFRAMES, après validation |

La phase 3 est une planification. Les dates cibles, efforts et états `TODO` ne constituent pas une preuve de réalisation technique ; ils organisent la production des preuves futures.

## Gestion GitHub

### Méthode officielle

`Besoin → User Story → Backlog → GitHub Issue → Branche → Commit → Test → Pull Request → Merge → Preuve RNCP`

| Indicateur | Valeur au 07/10/2026 |
|---|---:|
| Issues créées | 31 |
| P0 | 18 |
| P1 | 13 |
| Backend — `kanga-prog/watyouface-backend` | 27 |
| Frontend — `kanga-prog/watyouface-frontend` | 4 |
| Mapping | `25_GITHUB_ISSUES_MAPPING.md` |

Les issues sont assignées à `kanga-prog`, ouvertes et non démarrées. Les issues P2/P3 n'ont pas été créées. L'issue UX wireframes est P0 conformément à l'instruction de matérialisation, malgré sa priorité initiale P1 ; cette décision est explicitée dans le mapping.

## PHASE 4 — UX / WIREFRAMES

| Champ | Valeur |
|---|---|
| Statut | terminée, en attente de validation avant phase 5 |
| Date | 07/10/2026 |
| Issue | frontend [#1](https://github.com/kanga-prog/watyouface-frontend/issues/1), ouverte et assignée à `kanga-prog` |
| Branche | `rncp6/ux-wireframes` créée localement sur le frontend, sans commit ni push |
| Documents | `26_ARBORESCENCE_NAVIGATION.md` à `32_WIREFRAMES_BASSE_FIDELITE.md` |
| Parcours | Auth, Feed, Chat, Marketplace, Profil et Administration minimale |
| Wireframes | WF-01 à WF-19, desktop/mobile pour Feed, Chat et Marketplace |
| Décisions | navigation persistante, ownership visible mais serveur autoritaire, auto-like indisponible, paiement explicitement simulé, chat annonce = lien technique à décider |
| Écarts UX | mobile Home, auto-like, états Forbidden chat, détail/gestion marketplace, crédit wallet UI |
| Preuves | wireframes ASCII, Mermaid, matrices de traçabilité et interface cible/existante |
| Étape suivante | PHASE 5 — maquettes UI, après validation |

La phase 4 ne modifie aucun JSX, CSS, backend, test, Docker ou CI. Les wireframes sont des décisions basse fidélité à transformer en maquettes puis en lots GitHub tracés.

## PHASE 5 — MAQUETTES UI

| Champ | Valeur |
|---|---|
| Statut | terminée, en attente de validation avant phase 6 |
| Date | 07/10/2026 |
| Issue | frontend [#3](https://github.com/kanga-prog/watyouface-frontend/issues/3), ouverte, assignée à `kanga-prog` |
| Branche documentaire | `rncp6/ui-mockups` créée localement sur le backend, sans commit ni push ; branche frontend existante conservée |
| Documents | `33_DIRECTION_VISUELLE.md` à `40_CHECKLIST_MAQUETTES.md` |
| Asset | `mockups/WATYOUFACE_UI_MOCKUPS.svg` : UI-01 à UI-19, exportable |
| Direction | sobre, lisible, unifiée ; navigation commune, cartes et statuts textuels |
| Responsive | Feed une colonne mobile ; Chat liste → conversation ; Marketplace cartes pleine largeur |
| Sécurité UX | ownership visible, auto-like indisponible dans la cible mais contrôle serveur obligatoire ; wallet explicitement simulé ; données profil privées |
| Accessibilité | contraste cible, focus visible, labels permanents, erreurs textuelles, cibles tactiles ; conformité RGAA non déclarée |
| Écarts | mobile actuel, auto-like, chat forbidden, détail/vente marketplace et wallet restent à implémenter/contrôler |
| Étape suivante | PHASE 6 — prototype interactif, après validation |

La phase 5 ne modifie aucun JSX, CSS applicatif, Java, test, Docker ou CI. Les SVG et documents sont des preuves de conception ; ils ne prouvent ni un prototype réel ni une conformité accessibilité complète.

## PHASE 6 — PROTOTYPE INTERACTIF

| Champ | Valeur |
|---|---|
| Statut | terminée, en attente de validation avant phase 7 |
| Date | 07/10/2026 |
| Issue | frontend [#3](https://github.com/kanga-prog/watyouface-frontend/issues/3), ouverte et assignée à `kanga-prog` |
| Branche documentaire | `rncp6/ui-prototype` créée localement sur le backend, sans commit ni push |
| Format | HTML/CSS/JS statique isolé dans `docs/rncp/prototype/` ; bannière « PROTOTYPE UX — NON PRODUCTION » |
| Scénarios | DEMO-01 Auth, DEMO-02 Feed, DEMO-03 Chat, DEMO-04 Marketplace |
| États | default, loading, empty, success, error, forbidden, disabled ; tous simulés localement |
| Données | Alice Demo, Bruno Demo, Charlie Demo, produits et emails `example.com` fictifs |
| Captures | `screenshots/PROTO-01…06` en SVG |
| Documents | `41_SCENARIOS_PROTOTYPE.md` à `45_PROTO_VS_APPLICATION.md` |
| Limites | aucune API, BDD, authentification, WebSocket, autorisation serveur, paiement ou persistance réels |
| Étape suivante | PHASE 7 — architecture logicielle, après validation |

Le prototype est une simulation UX. Il soutient C2 et C5, prépare C3/C9, mais ne peut pas être présenté comme preuve de C7, C8, C10 ou C11 ni comme une application réellement sécurisée.

## PHASE 7 — ARCHITECTURE LOGICIELLE

| Champ | Valeur |
|---|---|
| Statut | terminée, en attente de validation avant phase 8 |
| Date | 07/10/2026 |
| Issue | backend [#21](https://github.com/kanga-prog/watyouface-backend/issues/21), ouverte et assignée à `kanga-prog` |
| Branche documentaire | `rncp6/architecture` créée localement sur le backend, sans commit ni push |
| Documents | `46_ARCHITECTURE_ACTUELLE.md` à `60_MATRICE_ARCHITECTURE_EXISTANT_CIBLE.md` |
| Actuel | React/Vite → REST/SockJS-STOMP → Spring Boot en couches → JPA/PostgreSQL, médias disque local |
| Cible | règles/transactions/autorisation au backend ; DTO/validation/erreurs/configuration/logs transverses |
| Flux | REST Auth/Post/Marketplace, WebSocket conversation, séquences Mermaid |
| Sécurité | JWT environnement, BCrypt, CORS, ownership/rôle/participant, modèle de menaces ; localStorage = compromis documenté |
| Décisions | chat générique MVP ; liaison Listing–Conversation différée ; paiement démo atomique avec verrou/transaction |
| Écarts | auth contrat, Like legacy/auto-like, lecture chat legacy, migrations/contraintes, médias, erreurs, CI/déploiement |
| Justification choix technologiques | **FAIT** — `61_JUSTIFICATION_CHOIX_TECHNOLOGIQUES.md` (React/Vite, Java/Spring, PostgreSQL/JPA, REST, STOMP/WebSocket, JWT/BCrypt, médias) |
| Architecture | **VALIDÉE** comme cible documentaire ; les éléments partiels restent explicitement dans la matrice existant/cible |
| C6 | preuve renforcée : diagrammes, flux, ADR et justification comparative |
| Étape suivante | PHASE 8 — conception des données, après validation |

La phase 7 ne modifie aucun controller, service, repository, entity, JSX/CSS applicatif, migration ou CI. Elle établit C6 et prépare C3/C7/C8/C9/C10/C11 ; seule l’implémentation/test ultérieur confirmera les éléments marqués partiels.

## PHASE 8 — CONCEPTION DES DONNÉES

| Champ | Valeur |
|---|---|
| Statut | terminée, en attente de validation avant phase 9 |
| Date | 07/10/2026 |
| Issue | backend [#5](https://github.com/kanga-prog/watyouface-backend/issues/5), ouverte et assignée à `kanga-prog` |
| Branche documentaire | `rncp6/data-design` créée localement sur le backend, sans commit ni push |
| Inventaire | 14 entités JPA : Auth/Profile, Feed, Chat, Marketplace, Contract, Media |
| Modèles | MCD/MLD/MPD Mermaid/Markdown et script SQL cible de référence |
| Intégrité | UQ Like/participant/transaction, CHECK et index identifiés comme migrations cibles sauf annotations déjà présentes |
| Migrations | H2 `update` dev, PostgreSQL `validate` local/prod ; aucune migration détectée ; Flyway recommandé |
| Seed | Alice/Bruno/Charlie/Admin Demo, données `example.com`, uniquement documentaire dev/test |
| Confidentialité | cartographie, classification, rétention et sécurité BDD documentées ; conformité RGPD non déclarée |
| Gaps | migrations, contraintes DB, index, UQ double paiement, rétention, politique média ; couverts par issues #2/#5/#10/#15/#16/#20 |
| Mapping | C7/C8 renforcées ; C3/C6/C9/C10 préparées |
| Étape suivante | PHASE 9 — développement frontend / conformité UI, après validation |

La phase 8 ne modifie aucune Entity, Repository, migration active, table, base réelle ou donnée réelle. Les scripts SQL sont des références à transformer en migrations testées, jamais à appliquer aveuglément.

## PHASE 9 — DÉVELOPPEMENT FRONTEND / CONFORMITÉ UI

| Champ | Valeur |
|---|---|
| Statut initial (07/10) | lot UI implémenté, clôture en attente des captures et recette visuelle ; statut historique réévalué le 08/10 |
| Date | 07/10/2026 |
| Issue | frontend [#2](https://github.com/kanga-prog/watyouface-frontend/issues/2), ouverte ; lot qualité, tests et accessibilité |
| Branche | `rncp6/responsive-accessibility` ; aucun commit, push ou PR : worktree local préexistant à revoir et recette intégrée bloquée |
| Navigation | Feed, Chat et Marketplace sont désormais des écrans dédiés ; navigation desktop et mobile explicite |
| Feed | états loading/empty/error ; actions ownership visibles uniquement au propriétaire ; auto-like désactivé dans l’UI, contrôle serveur toujours requis |
| Chat | liste et conversation séparées sur mobile ; boutons clavier réels ; états empty/error/403 gérés côté présentation |
| Marketplace | grille responsive, statuts métier libellés, actualisation explicite sans polling périodique ; actions contextualisées |
| Profil/wallet | email dans l’espace privé ; « Wallet de démonstration » ; crédit UI réservé à ADMIN |
| Accessibilité | focus global visible, labels, boutons sémantiques, messages textuels ; conformité RGAA complète non déclarée |
| Validation | `npm run lint` PASS ; `npm test -- --run` PASS (5 fichiers / 11 tests) ; `npm run build` PASS ; Vite HTTP 200 local |
| Recette initiale | blocage JavaMailSender corrigé depuis par la Phase 10 ; aucune recette navigateur n'avait été faite à cette date |
| Finalisation runtime du 07/10 | Backend `dev` + H2 démarré hors sandbox, Tomcat 8081 ; HTTP `/` = 401 ; Vite 5173 = HTTP 200. Les deux processus ont été arrêtés après vérification. |
| Validation frontend | `npm run lint` PASS ; `npm test -- --run` PASS (5 fichiers/11 tests) ; `npm run build` PASS le 07/10/2026 |
| Recette initiale | AUTH/FEED/CHAT/MARKETPLACE/PROFILE bloqués le 07/10, avant disponibilité des captures ; historique conservé dans `80_RECETTE_FRONTEND.md` |
| Documents | `80_RECETTE_FRONTEND.md`, `81_MATRICE_MAQUETTE_IMPLEMENTATION.md`, `82_MAPPING_FRONTEND_RNCP.md`, `evidence/frontend/README.md`, rapport Phase 9 |
| Captures | 9 PNG présents, non vides, vérifiés et ouverts le 08/10/2026 : Feed/Chat desktop-mobile, Marketplace desktop-mobile, Profil desktop-mobile, Connexion mobile |
| Recette finale | Captures PASS pour les écrans/états visibles ; erreurs, persistance CRUD, auth fonctionnelle, autorisations et transitions marketplace non réputées prouvées par image seule |
| Anomalies UI | Chat `createdAt` → `sentAt` corrigé et vu sans `Invalid Date` ; Profil responsive corrigé et vu sans overflow horizontal apparent |
| C2/C5/C9 | Captures réelles et recette visuelle renforcent C2/C5 ; lint PASS, Vitest 6 fichiers/14 tests PASS, build PASS renforcent C9 |
| Confidentialité | Certaines captures montrent des données personnelles apparentes ; anonymiser ou vérifier l'accord avant diffusion externe |
| Git / C4 | Worktree frontend mélangé ; aucun commit/PR. Écart de traçabilité C4 documenté, non bloquant pour C2/C5/C9 |
| Corrections UI rapides du 08/10 | `ChatWindow` utilise le champ API `sentAt` (confirmé dans `MessageDTO`) et omet une date absente/invalide ; profil empilé et contraint en largeur sur mobile. Lint PASS, Vitest 6 fichiers/14 tests PASS, build PASS. |
| Recette des corrections | Captures Chat/Profil après correction examinées ; heures valides sans `Invalid Date`, Profil mobile sans overflow horizontal apparent |
| État captures | 9 fichiers `REAL-UI-*.png` présents, non vides, ouverts et associés aux rôles dans le README |
| Clôture | **Fonctionnelle/RNCP : OUI pour C2/C5/C9**, limites résiduelles conservées ; **Git/PR : NON**, worktree mélangé |
| Étape suivante | RETOUR PHASE 10 — finalisation backend |

La Phase 10 a corrigé/testé contrat actif au login, auto-like et lecture REST legacy des messages. Les règles métier frontend ne remplacent pas les contrôles serveur ; les écarts backend encore ouverts (migrations/contraintes, concurrence/solde, STOMP E2E et médias) restent suivis séparément.

## PHASE 10 — BACKEND / MÉTIER / SÉCURITÉ

| Champ | Valeur |
|---|---|
| Branche | `rncp6/fix-dev-start` |
| Correctifs principaux | JavaMailSender dev, contrat/login, contrôle Like et routes legacy, membership Chat REST/STOMP, ownership Post, validation message, statuts Marketplace |
| Baseline Phase 10B | `./mvnw clean test`: 23 tests PASS |
| Suite générale finale | H2/MockMvc + unités : 35 tests, 30 PASS, 0 FAIL/ERROR, 5 tests PostgreSQL conditionnels ignorés; `BUILD SUCCESS` |
| Suite PostgreSQL dédiée | PostgreSQL 16.15 réel : 5/5 PASS (commit/état/soldes, rollback, contrainte, concurrence, solde insuffisant) |
| Migration | `src/main/resources/db/manual/V1__unique_transaction_listing.sql` appliquée après 11 transactions distinctes et zéro doublon; `uq_transaction_listing_id` vérifiée dans le catalogue et par insertion rejetée SQLSTATE 23505 |
| API Feed/Like | MockMvc couvre création, owner/non-owner CRUD; like d'autrui 200, self-like 403, routes legacy 410 |
| Chat | REST membre/non-membre et validation couverts MockMvc; STOMP couvert au niveau vrai interceptor/handler/service (3 tests), sans client-broker E2E |
| Marketplace | transitions métier service; paiement réel DB atomique et concurrence couverts par cinq tests PostgreSQL |
| Erreurs | 400/401/403/404/409 présents dans les tests ciblés; test dédié du corps générique 500 et harmonisation historique restent à faire |
| Preuves | `83_MATRICE_REGLES_BACKEND.md`, `84_MATRICE_SECURITE_BACKEND.md`, `85_RECETTE_API.md`, `evidence/backend/BE-API-01-mockmvc-results.txt`, `BE-STOMP-01-component-tests.txt`, `BE-PAY-01-postgres-integration.txt`, `BE-DATA-02-postgres-unique-payment.txt`, `test-results.txt` |
| C3/C6/C7/C8/C9 | renforcées par règles serveur, architecture transactionnelle, contrainte PostgreSQL réelle, accès JPA et tests/recette gradués |
| Clôture fonctionnelle / RNCP | **OUI**, limites non bloquantes listées au rapport (STOMP broker E2E, migration manuelle, erreur générique 500, anciens contrôleurs) |
| Clôture Git / PR | **NON** : worktree préexistant mélangé; aucun commit/PR créé, écart C4 |
| Étape suivante | Phase 10 fonctionnelle évaluée; attendre validation avant Phase 11 |

La migration est un script manuel versionné, pas une migration Flyway/Liquibase. Les cinq tests PostgreSQL s'exécutent séparément pour préserver H2 dans la suite générale. Aucun secret ni JWT n'est consigné.

## PHASE 11 — APPROFONDISSEMENT SÉCURITÉ

| Champ | Valeur vérifiée au 08/10/2026 |
|---|---|
| Statut avant 11B | Contrôles ciblés renforcés; clôture fonctionnelle/RNCP en attente de la remédiation runtime et du rate limiting |
| Fichiers | `90_MATRICE_SECURITE.md`, `91_THREAT_MODEL.md`, `92_OWASP_CHECKLIST.md`, `evidence/security/`, `RNCP6_PHASE11_APPROFONDISSEMENT_SECURITE_REPORT.md` |
| Correctifs | rôle d'autorisation rechargé de la base; destinations STOMP restreintes; headers/CORS et CSRF justifiés/testés; upload refus MIME et chemins contrôlés; logs stack traces éliminés dans les chemins audités |
| Backend | `./mvnw clean test`: 41 comptabilisés, 36 exécutés PASS, 0 FAIL/ERROR, 5 PG ignorés dans la suite générale; suite PostgreSQL réelle dédiée 5/5 PASS |
| Frontend | lint PASS; Vitest 14/14 PASS; build PASS; `npm audit --omit=dev`: 3 findings runtime (2 high, 1 critical) |
| Limites | rate limiting absent; token localStorage; HSTS opt-in et CSP absente; médias publics; STOMP non testé E2E; scan Maven absent; secrets historiques à faire tourner si encore actifs |
| C1/C3/C6/C8/C9 | preuves renforcées par profils/config, autorisations, contrôles transverses, accès JPA/transactions, tests H2 et PostgreSQL |
| C10/C11 | préparées uniquement; déploiement HTTPS, CI/scanner et monitoring non validés |
| Git / C4 | worktrees préexistants mélangés; aucun commit/PR créé |
| Étape suivante | Phase 11 à clôturer après correction/revue des advisories runtime et arbitrage des risques critiques; ne pas démarrer Phase 12 avant validation |

La preuve est matérielle mais bornée : les tests d'intercepteur STOMP ne valent pas test client/broker de bout en bout; les advisory runtime initiaux étaient un bloqueur avant la remédiation Phase 11B.

### PHASE 11B — RÉMÉDIATION DES RISQUES SÉCURITÉ PRIORITAIRES

| Champ | Résultat vérifié au 08/10/2026 |
|---|---|
| Frontend runtime | Avant: 3 advisories runtime (2 HIGH, 1 CRITICAL). Après mises à jour ciblées react-router-dom/react-router 7.18.4 et websocket-driver 0.7.5: `npm audit --omit=dev` rapporte 0 vulnérabilité. Preuves avant/après dans le dépôt frontend sous `docs/rncp/evidence/security/`. |
| Dépendances complètes | Audit complet initial après fixes runtime: 14 findings (1 low, 3 moderate, 10 high). `npm audit fix` sans `--force` et sans changement majeur a mis à jour 38 paquets; audits complets et runtime finaux: zéro vulnérabilité connue. |
| Rate limiting login | Limiteur mémoire/thread-safe, 5 échecs par IP sur 60 s, réponse 429 + `Retry-After`; 6 tests ciblés API/unitaires PASS, fenêtre simulée sans temporisation réelle. Preuve `evidence/security/rate-limit-tests.txt`. |
| Audit Maven | `dependency-check-maven:13.0.0` a été invoqué mais est BLOCKED avant analyse: API NVD rejette l'absence de clé, puis `NoDataException`. Aucun CVE/nombre Maven conclu; preuve `evidence/security/maven-dependency-check.txt`. |
| JWT localStorage | Risque résiduel formalisé dans `93_DECISION_STOCKAGE_JWT.md`; aucune migration effectuée. |
| Secrets historiques | Configuration actuelle externalisée; ancien `.env` repéré historiquement. Rotation des valeurs encore actives: inconnue/à confirmer. Pas de purge ou réécriture Git. |
| Validations | `./mvnw clean test`: 47 comptabilisés, 42 PASS, 5 tests PostgreSQL conditionnels ignorés; PostgreSQL 16 dédié: 5/5 PASS; limiteur ciblé: 21/21 PASS (6 cas couvrent le débit); frontend lint PASS, Vitest 14/14 PASS, build PASS; npm audit complet et runtime 0 findings. |
| C1/C3/C6/C8/C9 | Renforcées par tests du contrôle de débit, autorisation backend et preuve de remédiation des dépendances; C10/C11 restent à planifier (scan SCA opérationnel/CI, secrets historiques). |
| Git / C4 | Worktrees antérieurement mélangés; pas de commit/PR créé. |
| Statut fonctionnel/RNCP | **OUI**: risque runtime critique/élevé corrigé, rate limit testé, suites backend/PostgreSQL/frontend vertes; le scan Maven est clairement BLOCKED faute de clé API NVD et reste un risque d'information/documentation, non un résultat sain inventé. |
| Statut Git / PR | **NON**: worktrees préexistants mélangés; aucun commit ni PR créé, écart C4 documenté. |
| Étape suivante | PHASE 12 — TESTS ET QUALITÉ, après validation du porteur de projet. |

### PHASE 11C — MIGRATION JWT HTTPONLY

| Champ | Résultat vérifié au 09/10/2026 |
|---|---|
| Authentification | Login émet `WATYOUFACE_AUTH` HttpOnly; JWT retiré du JSON; cookie Path `/`, SameSite=Lax par défaut, Max-Age aligné JWT; Secure false en local HTTP et true par défaut en production. Bearer transitoire limité dev/local, refusé en production. |
| Frontend | `credentials: include` central; CSRF token distinct transmis par `X-XSRF-TOKEN`; aucun JWT localStorage ou construction Bearer trouvé dans `src`. username/avatar non-auth peuvent rester en localStorage. |
| Logout | `/api/auth/logout` expire le cookie aux mêmes attributs. API MockMvc validée. |
| CSRF / CORS | `CookieCsrfTokenRepository`, mutations refusées sans CSRF; CORS allowlist + credentials, wildcard interdit. `/ws/**` utilise handshake cookie + allowlist Origin + membership STOMP. |
| Backend ciblé | 35 tests ciblés PASS: 10 cookie auth, 2 factory, 1 handshake, 18 Security API, 4 STOMP. |
| Backend suite générale | `./mvnw clean test`: 60 comptabilisés, 0 échec/erreur, 5 tests PostgreSQL conditionnels ignorés; BUILD SUCCESS. |
| PostgreSQL | 5/5 tests dédiés exécutés sur `watyouface_db` PASS (les tests PostgreSQL métier ne dépendent pas de l'auth cookie). |
| Frontend | lint exit 0 (0 erreur, un avertissement Hook existant); Vitest 16/16 PASS; Vite build PASS. |
| Risque XSS | Impact du vol JWT via XSS fortement réduit par HttpOnly; XSS n'est pas « corrigé » et peut toujours agir dans la session. |
| Limite de recette | Aucun navigateur installé dans l'environnement. Inspection DevTools, refresh réel, navigation après login et logout navigateur non testés. |
| Statut clôture Phase 11C | **NON** à ce stade: manque le test manuel navigateur imposé par le critère de succès. |
| Statut clôture Phase 11 globale | **NON**: Phase 11C non clôturée; Phase 12 ne commence pas. |
| Git / C4 | Worktrees frontend/backend déjà mélangés; pas de commit, PR ou mise à jour d'issue dans ce lot. |
| Preuves | `evidence/security/phase11c-cookie-auth-tests.txt`, `phase11c-postgres-tests.txt`, `phase11c-backend-suite.txt`, `phase11c-frontend-tests.txt`, `phase11c-frontend-token-search.txt`; décision `93_DECISION_STOCKAGE_JWT.md`. |

## État Git et livrables — réaudit du 09/10/2026

| Dépôt | Branche | PR | Worktree local |
|---|---|---|---|
| Backend / RNCP | `rncp6/fix-dev-start` | [#28 — Backend, sécurité et preuves projet](https://github.com/kanga-prog/watyouface-backend/pull/28) | Propre au dernier contrôle |
| Frontend | `rncp6/responsive-accessibility` | [#5 — Frontend, responsive et auth sécurisée](https://github.com/kanga-prog/watyouface-frontend/pull/5) | `updateAvatars.js` laissé local, non committé |

Les PR #28 et #5 ainsi que l'état poussé sont ceux fournis dans le contexte de réaudit. La vérification directe avec GitHub CLI n'a pas été possible pendant cette passe, l'authentification locale étant invalide; aucune modification distante n'a été faite.

### Commits de consolidation

Backend : `041868f` code; `9145871` tests; `eb1e697` ignore local; `4f5870d` documentation/preuves; `5aa4280` livrables; `87af7cd` source de vérité. Frontend : `f1703f3` interfaces; `1bc16f1` auth cookie; `fc1306e` tests; `fcf40de` dépendances/audits; `e0fb23e` retrait `.env`.

### Livrables V0.2

Les trois fichiers fournis depuis le dossier Windows sont versionnés dans `docs/rncp/livrables/` (3/3, V0.2); leurs SHA-256 source/destination concordaient au contrôle.

### Configuration locale et secrets historiques

Dans le frontend, `.env` est désormais non suivi depuis `e0fb23e`; son contenu historique n'a pas été lu. `.gitignore` exclut `.env` et `.env.*`, avec exception `.env.example`, qui ne contient qu'une URL locale fictive. Les secrets runtime backend sont externalisés par variables d'environnement. Les valeurs historiques éventuelles restent potentiellement compromises; la checklist `evidence/security/secret-rotation-checklist.md` maintient JWT, DB password et SMTP « À CONFIRMER ». Aucun historique n'a été réécrit.

### Validations réexécutées — audit final Phase 11C

Backend : `./mvnw clean test` PASS, 61 tests comptabilisés, 0 échec/erreur, 5 tests PostgreSQL conditionnels ignorés (56 réussis). Le test ajouté vérifie la réutilisation du cookie émis au login sur plusieurs requêtes puis le rejet après logout. PostgreSQL réel : dernier résultat connu PASS 5/5; rejeu actuel NON REJOUÉ, car `pg_isready` ne trouve aucun serveur local et les variables de connexion ne sont pas définies. Frontend : lint PASS (0 erreur, 1 avertissement Hook dans `Admin.jsx`), tests PASS 16/16, build PASS, audit runtime npm 0 vulnérabilité. Aucun navigateur n'est installé : recette DevTools, refresh de session et logout visuel restent MANUEL À FAIRE.

### Phase 11C — résultat

JWT d'auth dans localStorage/sessionStorage : ABSENT; appels API `credentials: include`; cookie HttpOnly; CSRF actif via token distinct; CORS credentials avec origines explicites; logout expire le cookie. L'impact du vol de JWT via XSS est fortement réduit, sans déclarer les XSS corrigées. Clôture technique Phase 11C : OUI; preuve navigateur réelle : MANUEL À FAIRE (`94_RECETTE_MANUELLE_AUTH_COOKIE.md`). PR backend : #28; PR frontend : #5; livrables V0.2 : versionnés; `updateAvatars.js` reste local; rotation historique : À CONFIRMER.

## PHASE 12 — TESTS ET QUALITÉ

| Champ | Résultat vérifié au 09/10/2026 |
|---|---|
| Branche | `rncp6/tests-quality`, créée depuis `main` à jour après fusion PR #28; PR de Phase 12 à créer |
| Backend | `./mvnw clean test`: BUILD SUCCESS, 61 comptabilisés, 56 réussis, 0 échec, 0 erreur, 5 ignorés (tests PostgreSQL conditionnels) |
| PostgreSQL | NON REJOUÉ: `pg_isready` sans serveur local et variables DB/JWT absentes; dernier résultat réel connu 5/5 PASS sur PostgreSQL 16.15 le 08/10/2026 (`evidence/backend/BE-PAY-01-postgres-integration.txt`) |
| Frontend | ESLint PASS (0 erreur, avertissement Hook `Admin.jsx`); Vitest 7 fichiers/16 tests PASS; Vite build PASS; `npm audit --omit=dev`: 0 vulnérabilité runtime |
| Couverture chiffrée | NON MESURÉE; JaCoCo/Vitest coverage non configurés |
| E2E / manuel | aucun E2E navigateur automatisé recensé; captures UI Phase 9 antérieures; Auth Cookie DevTools reste À FAIRE MANUELLEMENT |
| Documents / preuves | `95_MATRICE_EXIGENCES_TESTS.md`, `96_STRATEGIE_TESTS_QUALITE.md`, `RNCP6_PHASE12_TESTS_QUALITE_REPORT.md`, `evidence/tests/*-summary.txt` |
| Mapping | C9 renforcée par suites exécutées et traçabilité; C3/C7/C8 appuyées par tests métier/BDD (BDD courant non rejoué); C10 documentation; C11 qualité partielle |
| Limites | parcours E2E et couverture chiffrée absents; PostgreSQL et recette navigateur non rejoués dans cette passe |
| Étape suivante | PHASE 13 — déploiement après revue/validation de la PR Phase 12 |
