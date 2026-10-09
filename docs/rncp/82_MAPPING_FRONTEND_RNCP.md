# Mapping RNCP — développement frontend Phase 9

| Compétence | Contribution Phase 9 | Preuves | Limites connues |
|---|---|---|---|
| C2 — Développer des interfaces utilisateur | Pages distinctes Feed, Chat et Marketplace ; navigation responsive ; composants de statut, actions et formulaires accessibles | Captures réelles `REAL-UI-01` à `REAL-UI-09` ; `Home.jsx`, `Messages.jsx`, `Marketplace.jsx`, `Navbar.jsx`, `ListingStatusBadge.jsx`, `input.css` | Certaines interactions, erreurs et états ne sont pas démontrés par capture ; données personnelles apparentes à protéger avant diffusion |
| C3 — Développer des composants métier | UI conditionnelle selon ownership, état d’annonce et rôle admin ; wording wallet démo | `PostCard.jsx`, `LikeButton.jsx`, `ListingCard.jsx`, `Profile.jsx` | Les règles critiques doivent rester et être testées au backend |
| C4 — Contribuer à la gestion de projet | Lot relié à l’issue frontend #2 et branche `rncp6/responsive-accessibility` | Historique Git local ; `80`/`81`/rapport Phase 9 | Worktree mélangé : aucun commit/PR isolé ; issue à mettre à jour quand les changements seront séparables |
| C5 — Analyser les besoins et maquetter | Implémentation confrontée aux wireframes et maquettes UI-01 à UI-19 | `81_MATRICE_MAQUETTE_IMPLEMENTATION.md`, captures réelles desktop/mobile | Capture login desktop et vues détail/états d'erreur manquants ; conformité limitée aux écrans visibles |
| C9 — Préparer et exécuter des tests | Tests ciblés auto-like, chat dates/empty, statut marketplace, auth et création d’annonce ; recette visuelle documentée | Vitest : 6 fichiers, 14 tests PASS ; `80_RECETTE_FRONTEND.md` | Tests API/E2E, actions métier réelles et audit accessibilité complète à compléter |

## Règle de sécurité

Le frontend sert l’UX, n’est pas l’autorité de sécurité. Masquer ou désactiver une action ne remplace jamais l’authentification, l’autorisation, la validation, ni les contrôles d’intégrité côté Spring Boot/PostgreSQL.
