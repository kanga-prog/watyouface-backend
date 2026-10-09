# Catalogue des états UI

| Composant critique | Default | Hover / Focus | Active | Disabled | Loading | Success | Error | Forbidden |
|---|---|---|---|---|---|---|---|---|
| Bouton | libellé clair | hover discret / contour focus jaune | pressé | contraste conservé | libellé « Envoi… » | confirmation liée | message proche | action masquée ou refus explicite |
| Champ auth/annonce | label + aide | contour focus | saisie | non applicable | soumission bloque doublon | validation visible | texte et association champ | non applicable |
| Feed | cartes lisibles | action focusable | like/commentaire choisi | auto-like indisponible | squelette | publication créée | réessayer | retour connexion / message |
| PostActions | actions propriétaire | menu clavier | édition | non-propriétaire non disponible | sauvegarde | enregistré | erreur conservant saisie | « Action non autorisée » |
| Chat | liste + fil | conversation focusable | conversation sélectionnée | envoi sans contenu | chargement/historique | message affiché | réessayer | accès refusé sans contenu |
| Marketplace | annonce + badge | carte/action focusable | action état | transition non valide | chargement | étape confirmée | erreur métier claire | action absente/refus explicite |
| Wallet démo | solde + mention simulation | lien/infos focusables | — | crédit non admin absent | paiement en cours | paiement simulé confirmé | solde insuffisant | crédit réservé ADMIN |

Règles : `hover` n’est jamais indispensable ; `focus` est visible ; une couleur n’est jamais le seul canal d’information ; les erreurs ne révèlent ni détails techniques ni secrets.

