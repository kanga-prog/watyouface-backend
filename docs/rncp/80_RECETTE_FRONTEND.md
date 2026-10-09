# Recette frontend réelle — Phase 9

## Cadre et environnement d'exécution

Cette recette concerne l'application React réelle, et non le prototype UX de phase 6. Les données de démonstration doivent être fictives et ne doivent contenir ni token, ni email personnel, ni secret dans les captures.

| Élément | Résultat au 07/10/2026 |
|---|---|
| Frontend | Vite démarré sur `127.0.0.1:5173` hors sandbox ; réponse HTTP 200 confirmée. Arrêt propre après vérification. |
| Backend | Profil `dev`, H2 mémoire et `DevMailConfig` initialisés ; Tomcat a démarré sur `8081`. Requête HTTP locale à `/` : 401 (serveur joignable, route protégée/non publique). Arrêt propre après vérification. |
| Navigateur/capture | Aucun exécutable Chromium/Chrome/Firefox/Playwright ni dépendance Playwright/Puppeteer détecté. Pas de navigateur utilisable pour recette manuelle ou capture réelle. |
| Données utilisées | Aucune donnée personnelle réelle. H2 était vierge hors contrat initial ; aucun compte fictif Alice/Bruno/Admin ni jeu de données de recette chargé. Secret JWT éphémère créé en mémoire de commande et non affiché ni enregistré. |

| ID | Écran | Précondition | Action | Attendu | Obtenu au 07/10/2026 | Verdict | Capture |
|---|---|---|---|---|---|---|---|
| REC-FEED-01 | Feed desktop | USER connecté ; API disponible | Ouvrir `/` | Fil, création de post et cartes affichés | Non exécuté : backend indisponible après initialisation | BLOCKED — backend runtime | `REAL-UI-01-feed-desktop.png` à produire |
| REC-FEED-02 | Feed mobile | Même précondition ; viewport 390×844 | Ouvrir le feed | Une colonne lisible ; navigation basse disponible | Non exécuté : absence navigateur/viewport et backend indisponible | BLOCKED — environnement | `REAL-UI-02-feed-mobile.png` à produire |
| REC-FEED-03 | Carte post | Post appartenant au USER courant | Ouvrir le menu/actions | Modifier et Supprimer visibles ; auto-like désactivé | Test composant `LikeButton` vert ; intégration non exécutée | BLOCKED — recette navigateur | À associer à REC-FEED-01 |
| REC-FEED-04 | Carte post | Post d’un autre USER | Ouvrir la carte | Actions propriétaire absentes ; like disponible | Non exécuté : backend indisponible | BLOCKED — backend runtime | À associer à REC-FEED-01 |
| REC-CHAT-01 | Chat desktop | USER connecté avec conversations | Ouvrir `/messages` | Liste à gauche, conversation active à droite | Non exécuté : backend indisponible | BLOCKED — backend runtime | `REAL-UI-03-chat-desktop.png` à produire |
| REC-CHAT-02 | Chat mobile | Même précondition ; viewport 390×844 | Sélectionner puis quitter une conversation | Liste → conversation → retour | Non exécuté : absence navigateur et backend indisponible | BLOCKED — environnement | `REAL-UI-04-chat-mobile.png` à produire |
| REC-CHAT-03 | Chat interdit | API répond 403 à l’historique | Sélectionner la conversation | Message « Accès interdit » sans détail technique | Gestion UI codée ; scénario réel indisponible | BLOCKED — backend runtime | À associer à REC-CHAT-01 |
| REC-MARKET-01 | Marketplace | USER connecté ; annonces API disponibles | Ouvrir `/marketplace` | Grille responsive, recherche, filtres, bouton création | Non exécuté : backend indisponible | BLOCKED — backend runtime | `REAL-UI-05-marketplace.png` à produire |
| REC-MARKET-02 | Marketplace | Annonce dans chaque état | Consulter les cartes | Libellés compréhensibles pour AVAILABLE à RECEIVED | Test composant `ListingStatusBadge` vert ; intégration non exécutée | BLOCKED — recette navigateur | `REAL-UI-09-marketplace-status.png` à produire |
| REC-MARKET-03 | Paiement | Acheteur ; annonce ACCEPTED ; solde démo suffisant | Choisir « Payer avec le wallet de démonstration » | Action démo explicite ; statut rafraîchi si API réussit | Logique backend non démarrée et hors périmètre Phase 9 | BLOCKED — backend runtime/P0 | À associer à REC-MARKET-01 |
| REC-PROFILE-01 | Profil | USER connecté | Ouvrir `/profile` | Email affiché dans l’espace privé ; wallet de démonstration explicite | Non exécuté : backend indisponible | BLOCKED — backend runtime | `REAL-UI-06-profile.png` à produire |
| REC-PROFILE-02 | Wallet | USER non ADMIN | Ouvrir le profil | Aucun formulaire de crédit disponible | Rendu codé ; scénario intégré non exécuté | BLOCKED — recette navigateur | `REAL-UI-06-profile.png` à produire |
| REC-AUTH-01 | Auth | Visiteur ; API et compte fictif disponibles | Tenter une connexion invalide | Erreur textuelle, aucun token stocké | Test composant Auth vert ; aucun appel navigateur exécuté | BLOCKED — recette navigateur | `REAL-UI-07-login.png` à produire |

> Cette première table et la campagne ci-dessous sont l'état historique du 07/10/2026 ; les verdicts visuels courants sont ceux du réaudit final du 08/10/2026.

## Campagne réelle — 07/10/2026

Les services ont été démarrés réellement hors sandbox pour vérifier leur disponibilité :

- Backend : `JWT_SECRET="$(openssl rand -hex 32)" SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run`. H2 mémoire initialisée, `JavaMailSender` dev résolu, Tomcat « started on port 8081 ». `curl` local sur `/` a reçu HTTP 401. Cela confirme le démarrage et la réponse HTTP, pas le succès des parcours authentifiés. Le secret temporaire n'a pas été affiché ni persisté.
- Frontend : `npm run dev -- --host 127.0.0.1`, Vite prêt sur `127.0.0.1:5173`; `curl` local a reçu HTTP 200.
- Les deux processus ont été arrêtés après vérification. Les exécutions dans le sandbox strict échouaient au bind par `listen EPERM`; l'exécution autorisée hors sandbox a permis le démarrage.
- Aucun navigateur exploitable n'est disponible (`command -v chromium chromium-browser google-chrome google-chrome-stable firefox playwright` sans résultat ; dépendances Playwright/Puppeteer absentes). Aucun utilisateur ou contenu fictif n'a été injecté.
- Sans navigateur, les routes UI, requêtes API émises par le frontend, console navigateur, responsive et parcours d'autorisation n'ont pas été observés ; les seules vérifications runtime sont les réponses HTTP directes mentionnées ci-dessus.

En conséquence, aucun des parcours d'interface AUTH, FEED, CHAT, MARKETPLACE, PROFILE, DESKTOP ou MOBILE n'a été réellement parcouru. Ils restent **BLOCKED**, même si des tests de composants ciblés sont PASS. Les scénarios `AUTH-01..04`, `FEED-01..08`, `CHAT-01..07`, `MKT-01..10` et `PROFILE-01..05` sont tous **BLOCKED — navigateur et jeu de données fictives indisponibles**. Aucun fichier `REAL-UI-*.png` n'est fourni.

## Validation automatisée associée

- `npm run lint` : **PASS** le 07/10/2026.
- `npm test -- --run` : **PASS**, 5 fichiers / 11 tests le 07/10/2026.
- `npm run build` : **PASS** le 07/10/2026.

## Limite de preuve / décision

État historique au 07/10/2026 : le blocage JavaMailSender était levé, mais la recette visuelle n'avait pas été exécutée et aucune image n'était alors déposée. Cette section est supersédée par le réaudit final ci-dessous.

## Réaudit des captures — 08/10/2026

Les neuf fichiers attendus ont été trouvés dans ce dossier, vérifiés non vides et reconnus comme PNG. Les captures ont été ouvertes et leur contenu visuel contrôlé. Elles montrent l'application réelle dans un navigateur avec outils d'émulation mobile visibles sur les vues mobile. Cette validation porte sur les écrans et états visibles, pas sur toutes les actions métier/API.

| ID | Vérification visuelle | Résultat | Preuve |
|---|---|---|---|
| REC-FEED-01 | Feed desktop, navigation, composer et carte de publication | PASS — écran visible sans coupure majeure | `REAL-UI-01-feed-desktop.png` |
| REC-FEED-02 | Feed mobile, colonne unique et navigation basse | PASS — écran visible dans viewport 393×852 | `REAL-UI-02-feed-mobile.png` |
| REC-FEED-03 | Actions propriétaire affichées sur son post | PASS — présence des commandes visible ; exécution complète du CRUD non prouvée par cette seule capture | `REAL-UI-01-feed-desktop.png` |
| REC-CHAT-01 | Chat desktop, liste et conversation avec heures valides | PASS — aucun « Invalid Date » visible | `REAL-UI-03-chat-desktop.png` |
| REC-CHAT-02 | Chat mobile, conversation et retour à la liste | PASS — aucun « Invalid Date » visible | `REAL-UI-04-chat-mobile.png` |
| REC-MARKET-01 | Marketplace desktop, filtres, annonces et statuts visibles | PASS — écran visible sans coupure majeure | `REAL-UI-05-marketplace.png` |
| REC-MARKET-02 | Marketplace mobile et carte adaptée à la largeur | PASS — écran visible dans viewport 393×852 | `REAL-UI-09-marketplace-mobile.png` |
| REC-PROFILE-01 | Profil desktop, avatar, identité et cartes | PASS — écran visible sans débordement horizontal apparent | `REAL-UI-08-profile-desktop.png` |
| REC-PROFILE-02 | Profil mobile, avatar, email, bouton, wallet et cartes | PASS — contenu visible dans viewport 393×852 sans débordement horizontal apparent | `REAL-UI-06-profile-mobile.png` |
| REC-AUTH-01 | Écran de connexion mobile et formulaire | PASS — présentation visible ; succès/échec d'authentification non démontré par la capture | `REAL-UI-07-login-mobile.png` |

Bilan des assertions strictement visuelles de ce réaudit : **PASS 10 · FAIL 0 · BLOCKED 0**. Ce décompte ne porte pas sur les scénarios métier/API élargis ; ceux-ci restent PARTIELS ou NON PROUVÉS selon la matrice ci-dessous.

### Anomalies — détection, correction et retest

- **ANOMALIE CHAT-DATE** — détectée lors de la recette ; le rendu lisait `createdAt`, absent du DTO. Correction : lecture de `sentAt`, omission d'une date absente/invalide. Retest visuel : PASS (`REAL-UI-03-chat-desktop.png`, `REAL-UI-04-chat-mobile.png`) ; tests composants correspondants PASS.
- **ANOMALIE PROFILE-MOBILE-OVERFLOW** — détectée lors de la recette ; en-tête et éléments dépassaient le viewport. Correction : structure responsive et largeur contrainte. Retest visuel : PASS (`REAL-UI-06-profile-mobile.png`) ; capture desktop complémentaire `REAL-UI-08-profile-desktop.png`.

### Confidentialité des preuves

Des captures de Profil/Connexion affichent une adresse email qui semble personnelle ; Feed/Chat exposent également des noms ou identifiants de compte. Ne pas publier ces images hors du dossier de travail sans accord explicite ou anonymisation préalable. Les originaux n'ont pas été retouchés pendant ce réaudit.

Les validations automatisées ont été relancées le 08/10/2026 : `npm run lint` PASS ; `npm test -- --run` PASS (6 fichiers, 14 tests) ; `npm run build` PASS (1901 modules transformés). Verdict des assertions visuelles : **PASS**, les images étant présentes et examinées dans ce réaudit.

## Complément — corrections UI du 08/10/2026

| ID | Écran / viewport | Précondition | Vérification manuelle attendue | Résultat de l'implémentation | Verdict visuel | Capture |
|---|---|---|---|---|---|---|
| CHAT-MOBILE | Chat, 393×852 (capture) | Conversation avec messages horodatés | Aucune mention « Invalid Date » | Aucune mention visible ; heures lisibles | PASS | `REAL-UI-04-chat-mobile.png` |
| PROFILE-MOBILE | Profil, 393×852 (capture) | Session affichant le profil | Aucun overflow horizontal ; avatar, email, bouton avatar, wallet et cartes visibles | Contenu visible, aucune coupure horizontale apparente | PASS | `REAL-UI-06-profile-mobile.png` |
| PROFILE-DESKTOP | Profil desktop | Profil affiché | En-tête horizontal et cartes non coupées | Présentation visible sans débordement apparent | PASS | `REAL-UI-08-profile-desktop.png` |

Ces corrections sont couvertes par `ChatWindow.test.jsx` (3 tests) et les validations automatisées relancées le 08/10/2026 : lint PASS, Vitest PASS (6 fichiers / 14 tests), build PASS (1901 modules transformés). Aucun scénario d'action serveur tel que paiement, autorisation 403 ou persistance CRUD n'est déclaré PASS sur la seule base d'une capture.
