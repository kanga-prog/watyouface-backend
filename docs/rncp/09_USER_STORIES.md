# User stories — WatYouFace

Les critères `AC-*` sont détaillés dans `10_REGLES_METIER.md` et `11_CAS_UTILISATION.md`. Les priorités s'appliquent au MVP RNCP, pas à une roadmap commerciale.

| ID | Epic | Acteur | User story | Priorité | Préconditions / dépendances | Règles / sécurité | RNCP |
|---|---|---|---|---|---|---|---|
| US-AUTH-001 | Auth | Visiteur | En tant que visiteur, je veux créer un compte afin d'accéder à WatYouFace. | MUST | contrat actif ; données valides | AUTH-001, AUTH-002, BR-AUTH-001/002 | C2,C3,C5,C9 |
| US-AUTH-002 | Auth | USER | En tant qu'utilisateur, je veux me connecter afin d'accéder à mon espace. | MUST | compte existant et contrat accepté | AUTH-003/005, BR-AUTH-003 | C2,C3,C9 |
| US-AUTH-003 | Auth | USER | En tant qu'utilisateur, je veux me déconnecter afin de ne plus laisser ma session locale active. | SHOULD | session active | AUTH-004 | C2,C3,C9 |
| US-PROFILE-001 | Profile | USER | En tant qu'utilisateur, je veux consulter et modifier mon profil afin de gérer mon identité. | MUST | authentification | PROFILE-001/002, BR-PROFILE-001 | C2,C3,C8,C9 |
| US-PROFILE-002 | Profile | USER | En tant qu'utilisateur, je veux remplacer mon avatar afin de personnaliser mon profil. | SHOULD | authentification | PROFILE-003, BR-PROFILE-002 | C2,C3,C8,C9 |
| US-POST-001 | Social | USER | En tant qu'utilisateur, je veux consulter le feed afin de suivre les publications de la communauté. | MUST | authentification et visibilité admise | SOCIAL-001 | C2,C5,C8,C9 |
| US-POST-002 | Social | USER | En tant qu'utilisateur, je veux créer un post afin de partager un contenu. | MUST | authentification | POST-001, BR-POST-001 | C2,C3,C8,C9 |
| US-POST-003 | Social | Propriétaire | En tant que propriétaire, je veux modifier ou supprimer mon post afin de garder mon contenu à jour. | MUST | post existant et propriétaire | POST-002/003, BR-POST-002 à 004 | C2,C3,C8,C9 |
| US-COMMENT-001 | Social | USER | En tant qu'utilisateur, je veux commenter une publication visible afin d'interagir. | MUST | authentification ; post visible | COMMENT-001, BR-COMMENT-001 | C2,C3,C8,C9 |
| US-LIKE-001 | Social | USER | En tant qu'utilisateur, je veux liker le post d'un autre membre afin de signaler mon intérêt. | MUST | authentification ; post d'autrui | LIKE-001, BR-LIKE-001/002 | C2,C3,C8,C9 |
| US-CHAT-001 | Messaging | USER | En tant qu'utilisateur, je veux démarrer ou ouvrir une conversation afin d'échanger avec un membre. | MUST | authentification ; autre USER existant | CHAT-001/003, BR-CHAT-001 | C2,C3,C5,C8,C9 |
| US-CHAT-002 | Messaging | Participant | En tant que participant, je veux envoyer et recevoir des messages afin de poursuivre une discussion. | MUST | appartenance conversation | CHAT-004/005, BR-CHAT-002/003 | C2,C3,C8,C9 |
| US-CHAT-003 | Messaging | Acheteur/Vendeur | En tant qu'acheteur ou vendeur, je veux discuter autour d'une annonce afin de négocier avant les étapes de vente. | SHOULD | annonce et conversation ; règles à définir | CHAT-006 | C2,C3,C5,C8 |
| US-MARKET-001 | Marketplace | Vendeur | En tant que vendeur, je veux créer une annonce afin de proposer un produit à la communauté. | MUST | authentification | MARKET-001, BR-MARKET-001 | C2,C3,C7,C8,C9 |
| US-MARKET-002 | Marketplace | Acheteur | En tant qu'acheteur, je veux demander une annonce disponible afin de manifester mon intérêt. | MUST | annonce AVAILABLE, non propriétaire | MARKET-003, BR-MARKET-002 | C2,C3,C7,C8,C9 |
| US-MARKET-003 | Marketplace | Vendeur | En tant que vendeur, je veux accepter ou refuser une demande afin de contrôler mon cycle de vente. | MUST | annonce PENDING ; vendeur | MARKET-004, BR-MARKET-003/004 | C2,C3,C7,C8,C9 |
| US-MARKET-004 | Marketplace | Acheteur | En tant qu'acheteur, je veux effectuer un paiement simulé unique afin de poursuivre une vente acceptée. | MUST | annonce ACCEPTED ; solde suffisant | MARKET-005, BR-MARKET-005/BR-WALLET-003 | C2,C3,C7,C8,C9 |
| US-MARKET-005 | Marketplace | Vendeur/Acheteur | En tant que vendeur puis acheteur, je veux indiquer expédition et réception afin de terminer l'échange. | MUST | états PAID puis SHIPPED | MARKET-006, BR-MARKET-006/007 | C2,C3,C7,C8,C9 |
| US-WALLET-001 | Wallet | ADMIN | En tant qu'administrateur, je veux créditer un wallet de démonstration afin de préparer un jeu d'essai. | MUST | ADMIN ; montant positif | WALLET-002, BR-WALLET-001/002 | C3,C8,C9 |
| US-ADMIN-001 | Administration | ADMIN | En tant qu'administrateur, je veux modérer les ressources prévues afin d'assurer le fonctionnement de démonstration. | SHOULD | rôle ADMIN | ADMIN-001 à 003 | C3,C4,C9 |
