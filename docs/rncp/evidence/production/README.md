# Preuves de production — WatYouFace

Ce répertoire est réservé aux pièces réutilisables de recette de production :
captures anonymisées, exports HTTP sans cookie/token, et journaux ne contenant
aucun secret. À la date du 18/10/2026, **aucune capture n'est déposée ici**.

La recette fonctionnelle est décrite dans
`../backend/BE-E2E-01-production-recipe.txt`. Une fonctionnalité validée sans
artefact archivé reste **PARTIAL** pour la preuve documentaire.

## Nommage et contenu attendu

| ID attendu | Pièce à archiver | Statut actuel |
|---|---|---|
| AUTH-INFRA-401 | réponse anonymisée `GET /api/users/me` sans session = 401 | À AJOUTER |
| AUTH-CSRF-200 | réponse anonymisée `GET /api/auth/csrf` = 200 et XSRF-TOKEN masqué | À AJOUTER |
| ADMIN-DASHBOARD | écran ADMIN sans e-mail réel ni donnée sensible | À AJOUTER |
| ADMIN-WALLET-CREDIT | avant/après crédit de démonstration, données fictives | À AJOUTER |
| MKT-CYCLE-NOMINAL | captures ou traces de chaque transition jusqu'à RECEIVED | À AJOUTER |
| MKT-CYCLE-REFUSED | trace PENDING → REFUSED | À AJOUTER |
| MKT-INSUFFICIENT-409 | réponse 409 anonymisée | À AJOUTER |
| MKT-HIDDEN | preuve tiers exclu + vue ADMIN, données fictives | À AJOUTER |
| UPLOAD-JPEG-CAPTURE | upload production JPEG et résultat affichable | À AJOUTER |
| UPLOAD-PNG-CAPTURE | upload production PNG et résultat affichable | À AJOUTER |
| UPLOAD-GIF-CAPTURE | upload production GIF, première image JPEG visible | À AJOUTER |
| UPLOAD-BMP-CAPTURE | upload production BMP, JPEG visible | À AJOUTER |

Ne jamais archiver de mot de passe, cookie d'authentification, JWT, clé SSH,
identifiant Railway/Hostinger ou donnée personnelle réelle.
