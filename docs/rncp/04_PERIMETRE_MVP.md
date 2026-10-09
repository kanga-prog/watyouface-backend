# Périmètre officiel et MVP RNCP

## Périmètre fonctionnel

### IN SCOPE — version RNCP

| Domaine | Décision de périmètre | Justification |
|---|---|---|
| Accès | inscription, contrat, connexion/déconnexion | point d'entrée et preuve d'authentification |
| Profil | lecture/mise à jour limitée du profil et avatar | rattache les données au membre authentifié |
| Feed social | consulter le fil, créer/modifier/supprimer son post, commenter et liker selon les autorisations | premier pilier : preuve d'interface, ownership et règles d'interaction |
| Chat | conversation entre utilisateurs, envoi/réception, discussion sociale ou liée à une annonce | deuxième pilier : preuve WebSocket, contrôle d'appartenance et lien inter-domaines |
| Marketplace | annonce, demande, décision vendeur, négociation via chat, paiement démo, expédition, réception | troisième pilier : parcours métier riche et règles d'état |
| Wallet | consultation et crédit de démonstration ADMIN seulement | soutient le scénario sans simuler un paiement réel |
| Sécurité / tests | autorisation, validation, erreurs, tests des scénarios critiques | exigence transversale de la certification |

### SECONDARY

Vidéos et partage de vidéos, administration détaillée, personnalisation avancée du profil, modération étendue, consultation de l'historique complet des transactions. Ces modules pourront être présentés comme preuves complémentaires si leur scénario est stabilisé et testé, mais ne conditionnent pas le MVP.

### OUT OF SCOPE

Paiement réel/PSP, livraison réelle et suivi transporteur, notification mobile/email complète, recommandation algorithmique, recherche avancée, multi-langue, MFA, SSO, application mobile native, monétisation, analytics produit, modération automatisée, haute disponibilité et montée en charge. Leur absence est volontaire et ne doit pas être masquée.

## MVP priorisé — MoSCoW

| ID | Fonctionnalité | Acteur | Priorité | Justification | RNCP |
|---|---|---|---|---|---|
| MVP-01 | Inscription avec acceptation du contrat puis connexion | Visiteur/USER | MUST | condition d'accès au reste de l'application | C2, C3, C5, C9 |
| MVP-02 | Profil associé au compte authentifié | USER | MUST | relation identité/données et contrôle d'accès | C2, C3, C8 |
| MVP-03 | Feed : consulter, créer, modifier/supprimer son post | USER | MUST | premier pilier, interface et ownership visibles | C2, C3, C8, C9 |
| MVP-04 | Feed : commenter et liker avec règles d'autorisation | USER | MUST | interactions sociales et permissions à vérifier | C2, C3, C8, C9 |
| MVP-05 | Chat : conversation, envoi/réception et appartenance | USER | MUST | deuxième pilier et sécurité WebSocket | C2, C3, C8, C9 |
| MVP-06 | Marketplace : créer une annonce et suivre ses états | Vendeur/Acheteur | MUST | troisième pilier et scénario technique riche | C2, C3, C7, C8, C9 |
| MVP-07 | Marketplace : paiement de démonstration unique et transaction | Acheteur/Vendeur | MUST | intégrité, transaction, refus double paiement | C3, C7, C8, C9 |
| MVP-08 | Autorisations USER/ADMIN/propriétaire | USER/ADMIN | MUST | sécurité transverse aux trois piliers | C3, C8, C9 |
| MVP-09 | Chat lié à une annonce | Acheteur/Vendeur | SHOULD | négociation sans inventer encore ses règles détaillées | C2, C3, C8, C9 |
| MVP-10 | Administration/modération ciblée | ADMIN | SHOULD | preuve de rôle technique | C3, C9 |
| MVP-11 | Vidéos | USER | COULD | utile mais non nécessaire aux trois piliers | C2, C3 |
| MVP-12 | Déploiement conteneurisé et pipeline | Équipe | MUST pour le dossier final, hors développement fonctionnel immédiat | requis par C10/C11 | C10, C11 |
| MVP-13 | Paiement réel ou application mobile | — | WON'T | hors objet et risqué dans le délai | — |

## Parcours métier de référence

### Parcours A — accès membre

**Déclencheur :** un visiteur souhaite rejoindre l'espace.  
**Étapes :** consulter/accepter le contrat → renseigner identifiants valides → inscription → connexion → réception d'un jeton → accès à l'espace authentifié.  
**Résultat :** un USER accède uniquement à ses ressources autorisées.  
**Données :** User, Contract/UserContract, JWT.  
**Règles :** données validées ; mot de passe haché ; identité déterminée serveur.  
**Risques :** mot de passe faible, fuite jeton, erreur de validation.  
**RNCP :** C2, C3, C7, C8, C9.

### Parcours B — publication et interaction

**Déclencheur :** un USER veut partager du contenu.  
**Étapes :** saisir une publication → validation → création associée au USER → affichage → commentaire ou like → contrôle du propriétaire lors d'une modification/suppression.  
**Résultat :** contenu et interaction rattachés à leurs auteurs.  
**Données :** Post, Comment, Like, médias éventuels.  
**Règles cibles :** `POST-R01` un USER authentifié crée une publication ; `POST-R02` et `POST-R03` seul le propriétaire modifie/supprime sa publication ; `POST-R04` un autre USER est refusé sauf règle ADMIN définie ; `LIKE-R01` un USER peut liker une publication d'un autre utilisateur ; `LIKE-R02` un USER ne doit pas liker sa propre publication ; `COMMENT-R01` un USER authentifié commente selon la visibilité retenue. Ces exigences doivent être comparées au code réel en phase 2 ; `LIKE-R02` n'est pas déclarée implémentée.  
**Risques :** upload, XSS, BOLA, modération.  
**RNCP :** C2, C3, C8, C9.

### Parcours C — messagerie

**Déclencheur :** deux membres membres d'une conversation veulent échanger.  
**Étapes :** ouverture d'une conversation sociale ou liée à une annonce → connexion STOMP authentifiée → abonnement vérifié → envoi/réception → persistance/diffusion.  
**Résultat :** seuls les participants accèdent aux messages.  
**Données :** Conversation, ConversationUser, Message.  
**Risques :** abonnement à une autre conversation, fuite de messages.  
**RNCP :** C2, C3, C8, C9.

### Parcours D — marketplace (scénario technique principal)

**Déclencheur :** un vendeur propose une annonce et un acheteur souhaite l'acquérir.  
**Étapes :** vendeur crée une annonce AVAILABLE → acheteur demande (PENDING) → vendeur accepte/refuse → acheteur paie une fois (PAID, crédit/débit démo et Transaction) → vendeur expédie (SHIPPED) → acheteur confirme (RECEIVED).  
**Résultat :** transaction interne cohérente et annonce terminée selon le cycle.  
**Données :** Listing, Wallet, Transaction, User.  
**Règles :** seuls acteur concerné/ADMIN selon règle peuvent agir ; transition valide obligatoire ; double paiement refusé ; transaction atomique.  
**Risques :** BOLA, concurrence/double paiement, montant imprécis, confusion avec paiement réel.  
**RNCP :** C2, C3, C6, C7, C8, C9 et, plus tard, C10/C11.

**Décision :** feed social, chat et marketplace sont les trois parcours/piliers centraux. Le parcours D est le scénario technique le plus riche pour certaines preuves RNCP : il matérialise données, logique métier, rôles, sécurité et tests. Il ne doit ni masquer les parcours A à C, ni réduire feed/chat à un simple contexte.

## Traçabilité RNCP

| Compétence | Preuve attendue | Usage |
|---|---|---|
| C2/C3 | interfaces et règles des trois piliers | démo / dossier |
| C5 | périmètre, MoSCoW et parcours des trois piliers | dossier projet |
| C6-C8 | architecture, données et accès des trois domaines | diagrammes futurs |
| C9 | cas nominaux/refusés et permissions feed/chat/marketplace | plan de tests |
| C10-C11 | MVP lançable et automatisé à préparer | dernières phases |
