# Matrice maquettes / application réelle — Phase 9

| UI cible | Composant ou page réelle | Statut | Écart restant | Issue | Test / preuve |
|---|---|---|---|---|---|
| UI-01 Connexion | `Login`, `LoginForm` | PARTIEL | Capture mobile confirme l'écran ; pas de capture desktop ni de succès/échec d'authentification dans cette image | #2 | `REAL-UI-07-login-mobile.png`, `LoginForm.test.jsx` |
| UI-02 Inscription | `Register`, `RegisterForm` | PARTIEL | Alignement détaillé aux maquettes hors lot | #3 | Build PASS |
| UI-03 Contrat | `Contract` | PARTIEL | Contrôle serveur du contrat corrigé/testé en Phase 10 ; recette visuelle login/contrat et capture non exécutées | backend #1 / frontend #2 | Test backend Phase 10 ; navigateur requis |
| UI-04/05 Feed desktop/mobile | `Home`, `PostCard`, `CreatePostForm` | CONFORME | Écrans desktop/mobile, navigation, composer et cartes visibles ; les parcours complets de publication restent à tester séparément | #2 | `REAL-UI-01-feed-desktop.png`, `REAL-UI-02-feed-mobile.png` |
| UI-06/07 Création/modification post | `CreatePostForm`, édition `PostCard` | PARTIEL | Validation API et recette complète à faire | #2 | Lint/build PASS |
| UI-08 Commentaires | `PostCard`, `CommentForm` | PARTIEL | Test composant/API complémentaire à ajouter | #2 | Correction callback optionnel, lint PASS |
| UI-09/10/11 Chat | `Messages`, `ChatList`, `ChatWindow`, `MessageForm` | CONFORME | Présentation desktop/mobile et affichage d'heures valides prouvés ; autorisation 403, STOMP et envoi réel restent hors preuve visuelle | #2 + backend #3 | `REAL-UI-03-chat-desktop.png`, `REAL-UI-04-chat-mobile.png` ; `ChatWindow.test.jsx` |
| UI-12 Liste annonces | `Marketplace`, `MarketplaceSidebar`, `ListingCard` | CONFORME | Liste desktop/mobile, cartes, filtres visibles ; parcours actions transactionnelles non couvert par l'image | #2 | `REAL-UI-05-marketplace.png`, `REAL-UI-09-marketplace-mobile.png` |
| UI-13 Détail annonce | `ListingCard` / affichage détail actuel | PARTIEL | Aucun écran de détail distinct démontré | #2 | Non prouvé par les captures présentes |
| UI-14 Création annonce | `CreateListingDialog` | PARTIEL | Capture et test de validation serveur à relier | #2 | `CreateListingDialog.test.jsx` (3 PASS) |
| UI-15/16/17 Cycle marketplace | `ListingCard`, `ListingStatusBadge` | PARTIEL | Transitions/paiement restent à prouver dans l'API et à parcourir visuellement | #2 + backend marketplace/tests | `ListingStatusBadge.test.jsx` (2 PASS), recette navigateur absente |
| UI-18 Profil | `Profile` | CONFORME | Présentation desktop/mobile, avatar, identité, email et cartes/wallet visibles sans overflow ; actions d'édition/avatar non prouvées | #2 | `REAL-UI-06-profile-mobile.png`, `REAL-UI-08-profile-desktop.png` |
| UI-19 Administration minimale | `Admin`, `Profile` | PARTIEL | Écran admin non réaligné intégralement ; crédit UI limité à ADMIN | #2 | Code UI ; sécurité backend requise |

## Lecture des statuts

- **PARTIEL** : le comportement UI ciblé est implémenté ou amélioré, mais la recette intégrée, les captures réelles ou une dépendance backend restent à produire.
- Aucun statut **CONFORME** n’est attribué sans recette sur application réelle et preuve associée.
- Le frontend améliore l’expérience et reflète les droits ; il ne constitue jamais l’autorité de sécurité.

## Finalisation du 07/10/2026

Vite a répondu HTTP 200 sur `127.0.0.1:5173`. Le backend a démarré en profil `dev` avec H2 et `JavaMailSender` dev, puis a répondu HTTP 401 sur `/` (route non publique/protégée) ; le blocage JavaMailSender de la Phase 9 est donc obsolète et levé. L'environnement courant ne dispose toutefois d'aucun navigateur ni moteur de capture, et aucun jeu de comptes/données fictives n'a été chargé. Les parcours visuels et intégrés restent **BLOCKED**. Aucun statut PARTIEL n'est promu à CONFORME sans test d'écran réel et capture associée.

## Réaudit des captures — 08/10/2026

Les neuf PNG ont été vérifiés et ouverts. Les lignes CONFORME signifient ici conformité de la présentation des écrans/captures ; elles ne certifient pas toutes les règles métier ou l'API. Le CRUD post est limité à la présence des actions propriétaire sur la capture Feed ; l'exécution et persistance de chaque mutation ne sont pas démontrées par cette image. Connexion est PARTIEL car seule la vue mobile est capturée et aucun résultat d'authentification n'est visible. Les états marketplace transactionnels restent PARTIELS.

**Confidentialité à traiter avant diffusion externe :** les captures Profil/Connexion contiennent une adresse email apparente et les captures Feed/Chat des noms/identifiants. Conserver l'accès privé aux originaux ; anonymiser ou obtenir accord avant inclusion publique dans dossier/PR.
