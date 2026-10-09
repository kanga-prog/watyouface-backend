# Matrice UX cible ↔ interface existante

**Méthode :** lecture du frontend existant le 07/10/2026, sans exécution ni modification. Un état `CONFORME` concerne la présence structurelle d'une zone ; il ne garantit pas la conformité métier/serveur. Les écarts alimentent les issues existantes ou de futures issues UI.

| Wireframe | Cible | Interface actuelle observée | État | Preuve | Écart / suite |
|---|---|---|---|---|---|
| WF-01 | login avec erreurs états | page Login + LoginForm existent ; tests succès/erreur ajoutés phase sécurité. | PARTIEL | `pages/Login.jsx:1-9`; `components/LoginForm.jsx` | contrat non accepté et états UX à harmoniser avec GAP-AUTH-001. |
| WF-02 | inscription validée | page/RegisterForm existent ; parcours stocke temporairement les données. | PARTIEL | `pages/Register.jsx:1-9`; `pages/Contract.jsx:29-58` | messages champ et confidentialité temporaire à contrôler. |
| WF-03 | contrat dans parcours | écran contrat, accept/refuse et retour connexion présents. | PARTIEL | `pages/Contract.jsx:12-64,90-123` | backend login ne bloque pas actuellement contrat non accepté : GAP-AUTH-001. |
| WF-04 | feed desktop distinct et navigation piliers | Home affiche feed central, marketplace gauche et chat droite. | PARTIEL | `pages/Home.jsx:89-157` | cible sépare les piliers via navigation globale ; UI actuelle surcharge Home. |
| WF-05 | feed mobile structurel | classes responsive très limitées dans Home trois colonnes fixes. | NON CONFORME | `pages/Home.jsx:90-156` | concevoir navigation bas et mono-colonne ; issue frontend #1. |
| WF-06 | création post avec états/limites | CreatePostForm texte/fichier, aperçu, chargement et alertes. | PARTIEL | `CreatePostForm.jsx:7-145` | limites/type affichés insuffisants ; serveur à renforcer (SOCIAL-003). |
| WF-07 | owner actions et confirmation | owner/admin conditionnel, édition et `confirm` suppression. | PARTIEL | `PostCard.jsx:26-30,61-119` | libellés icônes, erreurs et Forbidden à améliorer. |
| WF-08 | commentaires/like avec auto-like indisponible | commentaires et LikeButton présents ; aucun traitement UI auto-like. | NON CONFORME | `PostCard.jsx:145-170`; `LikeButton.jsx:6-38` | dépend GAP-LIKE-001/002 et issue backend #2. |
| WF-09 | liste conversations états explicites | ChatList intégré ; absence de conversation pas clairement structurée dans Home. | PARTIEL | `Home.jsx:128-156`; `ChatList.jsx` | créer Empty/Error/New conversation UX. |
| WF-10 | chat desktop avec erreur/forbidden | ChatWindow charges/messages/empty ; sélection dans colonne Home. | PARTIEL | `Home.jsx:128-156`; `ChatWindow.jsx` | Forbidden/erreur métier à rendre explicite ; issue #3/#13 backend. |
| WF-11 | chat mobile liste→conversation | aucune vue mobile dédiée prouvée. | NON CONFORME | `Home.jsx:128-156` | concevoir puis implémenter dans issue UX future. |
| WF-12 | marketplace liste pleine page responsive | Marketplace page délègue à une sidebar et contenu vide ; filtres/empty existent dans sidebar. | PARTIEL | `pages/Marketplace.jsx:28-40`; `MarketplaceSidebar.jsx:118-207` | détail/listing central et responsive à concevoir. |
| WF-13 | détail annonce + entrée contact vendeur annotée | cartes/actions marketplace existent, mais détail cible et lien Listing–Conversation non prouvés. | NON PROUVÉ | `MarketplaceSidebar.jsx:95-116` | décision CHAT-004 nécessaire. |
| WF-14 | création annonce avec validation/aperçu | dialogue création, labels, erreur, aperçu image présents. | PARTIEL | `CreateListingDialog.jsx:15-212` | accessibilité labels `htmlFor`, état/limites/texte paiement simulé à renforcer. |
| WF-15 | demande achat explicite | action probablement portée par ListingCard, non inspectée complètement ; confirmation dédiée non prouvée. | NON PROUVÉ | `MarketplaceSidebar.jsx:188-194` | analyser/maquetter confirmation. |
| WF-16 | gestion vendeur par états | actions ListingCard probables, mais écran vendeur cible non identifié. | NON PROUVÉ | `MarketplaceSidebar.jsx:188-194` | concevoir écran/zone « Mes ventes ». |
| WF-17 | paiement simulé/reception explicites | wallet/profil et callbacks PAID existent ; wording et parcours réception non établis. | PARTIEL | `Profile.jsx:130-149`; `MarketplaceSidebar.jsx:95-116` | rendre simulation et étapes visibles. |
| WF-18 | profil privé minimal | avatar, username, email, wallet, contrat ; wallet recharge affichée sans condition ADMIN UI. | PARTIEL | `Profile.jsx:106-193` | masquer/adapter crédit USER ; compte suppression non présent. |
| WF-19 | admin minimal conditionnel | route/page Admin, rôle UI et gestion utilisateurs/modération par ID. | PARTIEL | `App.jsx:21-33`; `Navbar.jsx:42-46`; `Admin.jsx:28-218` | confirmer actions, erreurs et traçabilité. |

## Synthèse des écarts UX

1. Navigation responsive cible absente : Feed/Chat/Marketplace doivent cesser d'être trois panneaux fixes sur Home mobile.
2. Auto-like et endpoints legacy Like doivent être corrigés côté serveur avant de rendre l'action indisponible côté UI.
3. Chat doit disposer d'états Forbidden/Erreur explicites et d'une preuve d'appartenance.
4. Marketplace nécessite détail, gestion vendeur et libellage « paiement simulé » cohérents.
5. Profil expose un contrôle de recharge dont la règle ADMIN doit être alignée avec le backend.
