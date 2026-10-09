# Décisions UI — Phase 5

| ID | Question | Décision | Justification | Impact | RNCP |
|---|---|---|---|---|---|
| UI-D001 | Comment unifier les piliers ? | socle navigation/carte/bouton commun ; contexte actif par onglet | produit unique sans mélanger les tâches | AppHeader et navigation responsive | C2/C5 |
| UI-D002 | Comment représenter une vente ? | `ListingStatusBadge` textuel et action contextuelle unique | évite les transitions ambiguës | cartes, détail, gestion vendeur | C2/C3/C5 |
| UI-D003 | Comment éviter l’ambiguïté bancaire ? | libellé constant « Wallet de démonstration » ; aucun crédit USER | paiement est simulé | profil et paiement | C2/C3/C5 |
| UI-D004 | Comment traiter l’auto-like ? | action indisponible sur son post avec texte explicatif | prévention UX ; serveur toujours autoritaire | LikeButton | C2/C3 |
| UI-D005 | Comment afficher un refus ? | écran/message `Accès refusé`, sans détail interne, avec retour utile | confidentialité et compréhension | chat, post, admin | C2/C3 |
| UI-D006 | Comment ouvrir une discussion vendeur ? | point d’entrée visuel « Contacter le vendeur » annoté à concevoir techniquement | ne pas inventer Listing–Conversation | détail annonce | C2/C5/C6 |
| UI-D007 | Comment adapter mobile ? | navigation basse ; chat à une vue ; cartes pleine largeur | structure adaptée et cible tactile | trois piliers | C2/C5 |
| UI-D008 | Comment protéger les données profil ? | email/wallet seulement dans l’espace personnel | minimisation visuelle | ProfileHeader, cartes publiques | C2/C5 |
| UI-D009 | Comment rendre les actions risquées sûres ? | confirmation explicite pour suppression et actions irréversibles | prévenir erreur utilisateur | Modal/Alert | C2/C3 |

