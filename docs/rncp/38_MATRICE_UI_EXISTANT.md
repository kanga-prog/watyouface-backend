# Matrice maquettes cible / frontend existant

| Maquette cible | État | Preuve interface existante | Écart futur |
|---|---|---|---|
| UI-01 Connexion | PARTIEL | `pages/Login.jsx`, `LoginForm.jsx` | harmoniser erreur/contrat et états visuels |
| UI-02 Inscription | PARTIEL | `RegisterForm.jsx`, `Contract.jsx` | validation et confidentialité parcours à consolider |
| UI-03 Contrat | PARTIEL | `Contract.jsx` | règle serveur de connexion : GAP-AUTH-001 |
| UI-04 Feed desktop | PARTIEL | `Home.jsx` trois colonnes | séparer contextes via navigation cible |
| UI-05 Feed mobile | NON CONFORME | structure Home fixe | navigation basse, une colonne, responsive |
| UI-06/07 Publication | PARTIEL | `CreatePostForm.jsx`, `PostCard.jsx` | limites, erreurs, libellés accessibles |
| UI-08 Interactions | NON CONFORME | `LikeButton.jsx` | auto-like et legacy Like : issue backend #2 |
| UI-09/10 Chat desktop | PARTIEL | `ChatList`, `ChatWindow` | empty/error/forbidden cohérents |
| UI-11 Chat mobile | NON CONFORME | aucune vue dédiée prouvée | liste → conversation + retour |
| UI-12 Liste marketplace | PARTIEL | `MarketplaceSidebar.jsx` | page principale et cartes responsives |
| UI-13 Détail / contact | NON PROUVÉ | détail et lien annonce-chat non établis | décision architecture CHAT-004 |
| UI-14 Création annonce | PARTIEL | `CreateListingDialog.jsx` | labels, aides et états normalisés |
| UI-15/16 Achat et vendeur | NON PROUVÉ | écrans dédiés non identifiés | actions par état/rôle |
| UI-17 Wallet/paiement | PARTIEL | `Profile.jsx` / callbacks marketplace | wording simulé, crédit ADMIN seulement |
| UI-18 Profil | PARTIEL | `Profile.jsx` | confidentialité et zone dangereuse |
| UI-19 Admin | PARTIEL | `Admin.jsx` | limiter, confirmer et tracer les actions |

**Synthèse :** 0 conforme, 11 partiels, 3 non conformes, 3 non prouvés. Cette matrice crée des écarts d’implémentation ; elle ne modifie aucun composant dans cette phase.

