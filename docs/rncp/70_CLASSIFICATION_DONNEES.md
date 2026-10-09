# Classification des données

| Classification | Données | Accès cible |
|---|---|---|
| PUBLIC / membre | username, avatar, post/annonce selon visibilité décidée | visiteur ou USER selon produit |
| AUTHENTIFIÉ | feed complet, commentaires, annonces actives | USER connecté |
| PRIVÉ | email, contrat accepté, profil complet, wallet | propriétaire ; ADMIN si nécessaire justifiée |
| PARTICIPANTS | conversation et messages | seuls `ConversationUser` participants |
| ADMIN | rôle, modération, gestion crédit démo | ADMIN serveur |
| SENSIBLE MÉTIER | password hash, JWT/secret, solde, transaction, logs | système/administration technique minimale |

La classification doit se traduire en DTO, autorisation backend, routes média et tests 401/403. `@JsonIgnore` seul ne suffit pas à faire respecter cette politique.

