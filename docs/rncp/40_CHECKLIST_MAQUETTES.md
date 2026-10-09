# Checklist de validation des maquettes

| Vérification | État | Preuve |
|---|---|---|
| Trois piliers présents | OK | UI-04 à UI-17 |
| Authentification et profil couverts | OK | UI-01 à UI-03, UI-18 |
| Vue desktop et adaptation mobile | OK | UI-04/05, UI-10/11, UI-12 mobile spécifié |
| Hiérarchie et navigation cohérentes | OK | 33, 34 et maquettes SVG |
| Ownership post visible | OK | UI-07, UI-08 |
| Auto-like non proposé à l’auteur | OK, contrôle serveur restant obligatoire | UI-08, UI-D004 |
| États erreur/chargement/vide/interdit prévus | OK | 36_ETATS_UI.md |
| Wallet explicitement simulé | OK | UI-17, UI-D003 |
| Données privées non publiques | OK | UI-18, UI-D008 |
| Confirmation action dangereuse | OK | UI-07, UI-D009 |
| Contraste, focus, labels pris en compte | OK, à vérifier à l’implémentation | 33, 34, 36 |
| Relation annonce-conversation inventée | NON | UI-13/UI-D006 l’annotent comme décision future |

## Validation de phase

Les maquettes répondent au besoin de conception UI et alimentent le dossier projet/DP/slides. Elles ne valident ni le code React, ni le responsive réel, ni la conformité RGAA : ces éléments sont réservés à l’implémentation, aux tests et à la phase prototype.

