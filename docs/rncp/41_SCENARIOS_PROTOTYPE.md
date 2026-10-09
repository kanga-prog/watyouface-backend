# Scénarios de démonstration du prototype

| ID | Acteur | Point de départ | Étapes | Résultat attendu | Maquettes | User stories | RNCP |
|---|---|---|---|---|---|---|---|
| DEMO-01 AUTH | Visiteur | Connexion | Inscription → Contrat → Accepter → Connexion → Feed ; simuler email invalide / mauvais login / contrat refusé | navigation et retours visibles, aucun mot de passe réel | UI-01 à UI-04 | US-AUTH-001/002 | C2/C5 |
| DEMO-02 FEED | Alice Demo (USER) | Feed | Créer → Publier → Modifier son post → Enregistrer → Supprimer → Confirmer ; liker/commenter un post de Bruno | état success ; actions owner visibles ; auto-like indisponible | UI-04 à UI-08 | US-POST-001/003, US-LIKE-001, US-COMMENT-001 | C2/C3/C5 |
| DEMO-03 CHAT | Alice Demo (USER) | Liste conversations | Ouvrir Bruno → Envoyer un message → Retour mobile ; depuis annonce, Contacter le vendeur | message affiché seulement dans la simulation ; intention annonce-chat annotée | UI-09 à UI-11, UI-13 | US-CHAT-001/002/003 | C2/C5 |
| DEMO-04 MARKETPLACE | Alice Demo puis vendeur simulé | Liste annonces | Détail → Demander → PENDING → Accepter → ACCEPTED → Payer 30 crédits démo → PAID → Expédier → SHIPPED → Réception → RECEIVED | badges et action contextuelle par état ; wallet passe 100 → 70 crédits démo | UI-12 à UI-17 | US-MARKET-001 à 004 | C2/C3/C5 |

## Conduite de présentation

Ouvrir `prototype/index.html`, annoncer « Prototype UX — non production », puis sélectionner **Desktop** ou **Mobile**. Les boutons d’états `Chargement`, `Vide`, `Erreur`, `Interdit` existent uniquement pour soutenir la recette. Les données Alice Demo, Bruno Demo et Charlie Demo sont fictives.

