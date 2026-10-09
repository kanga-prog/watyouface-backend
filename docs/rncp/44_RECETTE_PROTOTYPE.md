# Recette manuelle — prototype UX

**Environnement :** navigateur local ouvrant `docs/rncp/prototype/index.html` ; aucune installation ni serveur nécessaire. Les résultats ci-dessous sont constatés par inspection du code et navigation manuelle de la simulation ; une recette navigateur finale reste à rejouer avant capture officielle.

| ID | Action | Attendu | Obtenu | Verdict |
|---|---|---|---|---|
| PR-01 | Ouvrir `index.html` | bannière non production, connexion | prévu par écran initial | À rejouer navigateur |
| PR-02 | Inscription → contrat → accepter → connexion | Feed fictif accessible | route et état `auth` simulés | À rejouer navigateur |
| PR-03 | Cliquer erreur login / contrat refusé | message textuel associé | messages simulés | À rejouer navigateur |
| PR-04 | Créer une publication | nouveau post Alice visible | ajout tableau local + success | À rejouer navigateur |
| PR-05 | Modifier puis supprimer post Alice | modification puis confirmation/suppression | ownership simulé | À rejouer navigateur |
| PR-06 | Tenter auto-like Alice | bouton indisponible | `disabled` + aide | À rejouer navigateur |
| PR-07 | Liker/commenter post Bruno | compteur/commentaire évolue | état local | À rejouer navigateur |
| PR-08 | Ouvrir chat puis envoyer | bulle ajoutée | état local | À rejouer navigateur |
| PR-09 | Sélectionner Mobile, ouvrir chat, retour | liste → conversation → retour | classe mobile et action retour | À rejouer navigateur |
| PR-10 | Marketplace : demande → accepter → payer → expédier → réception | états et solde 100→70 visibles | transitions locales | À rejouer navigateur |
| PR-11 | Cliquer états demo | loading/vide/erreur/interdit | panneau généré | À rejouer navigateur |
| PR-12 | Utiliser Tab et Entrée | actions et focus visibles | boutons/champs natifs + `:focus-visible` | À rejouer navigateur |

## Verdict de phase

Le prototype est une preuve de conception et une base de recette, pas une preuve de comportement applicatif. Ne marquer cette recette `PASS` qu’après ouverture réelle dans un navigateur ; aucune commande de test production n’est concernée.

