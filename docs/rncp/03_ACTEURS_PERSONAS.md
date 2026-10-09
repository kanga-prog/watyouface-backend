# Acteurs et personas initiaux

## Rôles techniques et contextuels

| Type | Rôle | Description et droits | Restrictions |
|---|---|---|---|
| Technique | Visiteur | non authentifié ; peut consulter les écrans publics d'accès et le contrat actif | ne publie pas, ne converse pas, ne consulte pas les ressources privées |
| Technique | USER | membre authentifié ; gère son profil, publie, interagit, participe aux conversations et aux annonces auxquelles il est lié | ne gère pas les ressources d'autrui, sauf autorisation explicite |
| Technique | ADMIN | membre doté d'une administration serveur ; modère et accède aux opérations prévues par l'API | droits limités aux endpoints administratifs documentés ; toute action doit être tracée à terme |
| Contextuel | Vendeur | USER propriétaire d'une annonce | gère uniquement ses annonces et les étapes vendeur du cycle |
| Contextuel | Acheteur | USER demandeur ou acheteur d'une annonce | ne paie et ne confirme que les annonces où il est acheteur |

Un même USER peut être acheteur dans une transaction et vendeur dans une autre. Acheteur et vendeur ne doivent donc pas devenir des rôles techniques globaux.

## Fiches acteurs

### Visiteur

**Objectif :** créer un compte ou comprendre les conditions d'accès.  
**Actions :** consulter le contrat actif, s'inscrire, se connecter.  
**Droits :** accès public explicitement autorisé par l'API.  
**Restriction :** aucun accès aux données utilisateur, au feed privé, aux messages, annonces personnelles ou wallet.

### Utilisateur (USER)

**Objectif :** participer à la communauté et effectuer un échange encadré.  
**Actions :** gérer le profil, poster/commenter/liker, converser, créer une annonce, demander ou acheter une annonce.  
**Droits :** accès authentifié ; propriétaire de ses ressources.  
**Restriction :** refus des actions ADMIN et des ressources d'un autre USER (BOLA/IDOR).

### Administrateur (ADMIN)

**Objectif :** administrer ou modérer les éléments prévus.  
**Actions :** opérations `/api/admin/**`, modération et crédit wallet de démonstration.  
**Droits :** contrôle de rôle serveur.  
**Restriction :** les permissions précises, la journalisation et la délégation d'administration doivent être spécifiées ultérieurement.

## Personas de conception (hypothèses à valider)

### Léa Martin — membre qui partage

**Profil :** membre fictive utilisant une application web sur mobile et ordinateur.  
**Besoin :** publier une information et suivre les réactions de sa communauté.  
**Frustration :** ne pas savoir si une action a été enregistrée ou pourquoi elle est refusée.  
**Objectif :** publier puis interagir sans quitter l'application.  
**Fonctionnalités utiles :** inscription/connexion, profil/avatar, feed, posts, modification/suppression de ses contenus, commentaires, likes autorisés et messages d'erreur clairs.

### Karim Bensaïd — vendeur occasionnel

**Profil :** membre fictif souhaitant proposer un objet à la communauté.  
**Besoin :** publier une annonce puis suivre une demande d'achat.  
**Frustration :** qu'un tiers puisse agir sur son annonce ou que l'état de vente soit ambigu.  
**Objectif :** passer d'une annonce disponible à une expédition selon les règles prévues.  
**Fonctionnalités utiles :** création d'annonce, statut, acceptation/refus, conversation sociale ou de négociation et historique transactionnel.

### Chloé Dubois — acheteuse prudente

**Profil :** membre fictive qui consulte une annonce et souhaite l'acquérir.  
**Besoin :** comprendre le statut de l'annonce et obtenir une confirmation après chaque étape.  
**Frustration :** payer deux fois, accéder à une annonce déjà indisponible, ou exposer ses données.  
**Objectif :** demander, payer dans le contexte de démonstration et confirmer la réception.  
**Fonctionnalités utiles :** consultation annonce, demande, statut, chat de négociation, wallet de démonstration, transaction et contrôles d'accès.

### Nadia Leroy — administratrice

**Profil :** administratrice fictive chargée de la modération de démonstration.  
**Besoin :** intervenir sur le contenu sans ouvrir de privilèges aux membres.  
**Frustration :** dépendre uniquement de contrôles côté interface.  
**Objectif :** démontrer une distinction de rôle appliquée par l'API.  
**Fonctionnalités utiles :** accès administration, rôle serveur, modération des contenus des trois piliers et crédit de démonstration limité.

## Traçabilité RNCP

| Compétence | Preuve | Usage |
|---|---|---|
| C4 | acteurs et responsabilités | dossier projet |
| C5 | personas et besoins à analyser | maquettes et cas d'utilisation futurs |
| C3 | droits et restrictions liés aux règles métier | entretien |
| C2 | besoins d'interface et retours utilisateur | conception UI ultérieure |
