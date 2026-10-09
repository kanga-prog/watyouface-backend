# Wireframes basse fidélité — WatYouFace

**Convention :** `[ ]` bouton/action, `( )` champ, `---` séparation. Ces schémas définissent hiérarchie, zones et états ; aucune couleur, police, taille définitive ni comportement React n'est imposé.

## Authentification

### WF-01 — Connexion

```text
+------------------------------------------------+
| WatYouFace                                    |
| Connexion                                     |
| Email        (____________________________)   |
| Mot de passe (____________________________)   |
| [ Se connecter ]                              |
|                                                |
| Erreur : identifiants invalides (zone texte)  |
| Pas de compte ? [ Créer un compte ]           |
+------------------------------------------------+
```

États : bouton désactivé pendant envoi ; erreur email/champ ; 401 générique ; « contrat à accepter » avec retour sécurisé. Focus : titre → email → mot de passe → bouton → lien.

### WF-02 — Inscription

```text
+------------------------------------------------+
| Créer un compte                               |
| Pseudo       (____________________________)   |
| Email        (____________________________)   |
| Mot de passe (____________________________)   |
| Aide : 12 caractères minimum                  |
| [ Continuer vers le contrat ]                 |
| Déjà membre ? [ Connexion ]                   |
+------------------------------------------------+
```

États : erreurs associées à chaque champ ; données conservées temporairement uniquement pour le parcours contrat ; aucun mot de passe affiché.

### WF-03 — Contrat

```text
+------------------------------------------------+
| < Retour   Contrat WatYouFace — version n     |
|------------------------------------------------|
| [ Zone de lecture du contrat défilante ]      |
|                                                |
| [ Refuser et revenir à l'inscription ]        |
| [ Accepter et créer mon compte ]              |
| Erreur contrat indisponible : [ Réessayer ]   |
+------------------------------------------------+
```

## Feed social

### WF-04 — Feed desktop

```text
+--------------------------------------------------------------------------------+
| Logo | Feed* | Chat | Marketplace                      Profil | Déconnexion |
+----------------+---------------------------------------+---------------------+
| Raccourcis      | Fil d'actualité                       | (optionnel)         |
| Mon profil      | [ Créer une publication ]            | Suggestions /       |
| Mes annonces    |---------------------------------------| actions rapides     |
|                 | PostCard auteur/date                  |                     |
|                 | contenu / média (alt)                 |                     |
|                 | [Like n] [Commentaires n]             |                     |
|                 | PostCard ...                          |                     |
+----------------+---------------------------------------+---------------------+
```

États : Loading liste ; Empty « Aucune publication — Publier » ; Error + Réessayer ; Forbidden retourne login. Les actions owner sont dans la carte, non dans une zone publique.

### WF-05 — Feed mobile

```text
+-------------------------------+
| WatYouFace      [Profil/menu] |
| Feed                         |
| [ Créer une publication ]     |
|-------------------------------|
| PostCard pleine largeur       |
| auteur / date                 |
| contenu / média               |
| [ J'aime n ] [ Commenter n ]  |
|-------------------------------|
| Feed* | Chat | Market | Profil|
+-------------------------------+
```

Adaptation : une seule colonne ; navigation bas de page ; actions secondaires dans menu de carte ; aucun panneau chat/market concurrent.

### WF-06 — Créer une publication

```text
+------------------------------------------------+
| < Annuler        Nouvelle publication          |
| Que souhaitez-vous partager ?                  |
| ( zone de texte                               )|
| [ Ajouter une image/vidéo ]  aperçu + [Retirer]|
| Aide formats/limites à définir                 |
| Erreur associée au champ                       |
|                           [ Publier ]          |
+------------------------------------------------+
```

Succès : confirmation « Publication créée », retour feed actualisé. Erreur upload/validation : saisie conservée lorsque possible.

### WF-07 — Modifier/supprimer son post

```text
+------------------------------------------------+
| Post de Moi                         [Actions v]|
|  ├─ Modifier                                  |
|  └─ Supprimer                                 |
|------------------------------------------------|
| ( contenu modifiable                          )|
| [ Annuler ]                         [ Enregistrer ]
+------------------------------------------------+

Confirmation suppression : « Supprimer définitivement cette publication ? »
[ Annuler ] [ Supprimer ]
```

Non-propriétaire : ni menu Modifier/Supprimer, ni fuite d'action ; si refus serveur, message Forbidden et recharge du feed.

### WF-08 — Commentaires et like

```text
+------------------------------------------------+
| [ J'aime n ] [ Commentaires n ]                |
|------------------------------------------------|
| Commentaires                                   |
| Avatar Nom : texte                              |
| Avatar Nom : texte                              |
| ( Écrire un commentaire... ) [ Envoyer ]       |
| Erreur de commentaire / chargement / vide      |
+------------------------------------------------+
```

Sur son propre post : `J'aime` indisponible avec aide « Vous ne pouvez pas aimer votre propre publication » (cible UX ; contrôle serveur obligatoire). Sur post tiers : état « J'aime déjà » permet retrait selon règle toggle.

## Chat

### WF-09 — Liste des conversations

```text
+------------------------------------------------+
| Chat                                           |
| [ Nouvelle conversation ]                      |
| Rechercher (______________________________)    |
|------------------------------------------------|
| Avatar  Léa          Dernier message    heure  |
| Avatar  Groupe projet Dernier message    heure  |
|------------------------------------------------|
| Empty : « Aucune conversation » [ Contacter ]  |
+------------------------------------------------+
```

États : loading, empty, erreur + Réessayer. La liste ne contient que les conversations auxquelles le USER appartient.

### WF-10 — Chat desktop

```text
+--------------------------------------------------------------------------------+
| Nav : Feed | Chat* | Marketplace | Profil                                    |
+---------------------------+----------------------------------------------------+
| Conversations              | < Retour  Avatar Nom / groupe                    |
| [Nouvelle] [Recherche]     |----------------------------------------------------|
| > Léa                      | message reçu                           10:12      |
|   Groupe                   |                         mon message    10:13      |
| Empty/Loading/Erreur       |                                                    |
|                            | ( Écrire un message... ) [ Envoyer ]             |
+---------------------------+----------------------------------------------------+
```

Forbidden : remplacer la zone messages par « Accès refusé à cette conversation » + `[Retour aux conversations]`, sans contenu historique.

### WF-11 — Chat mobile

```text
LISTE                             CONVERSATION
+-------------------------+       +---------------------------+
| Chat                     |       | < Conversations  Léa      |
| [Nouvelle]               |  -->  |---------------------------|
| Avatar Léa               |       | bulles messages           |
| Avatar Groupe            |       |                           |
| Feed | Chat* | Market... |       | (Message...) [Envoyer]    |
+-------------------------+       +---------------------------+
```

Adaptation : une vue à la fois, bouton retour explicite et conservation de la sélection ; réception temps réel annoncée sans voler le focus pendant la saisie.

## Marketplace

### WF-12 — Liste des annonces

```text
+--------------------------------------------------------------------------------+
| Nav : Feed | Chat | Marketplace* | Profil                                     |
| [ + Créer une annonce ]  Rechercher (______) [Filtres]                         |
|-------------------------------------------------------------------------|
| [image alt] Titre       Prix WUF     Badge AVAILABLE   [Voir]           |
| [image alt] Titre       Prix WUF     Badge PENDING     [Voir]           |
| Empty : Aucune annonce disponible  [Créer une annonce]                         |
+--------------------------------------------------------------------------------+
```

Mobile : cartes une colonne ; filtres dans panneau repliable ; titre/prix/état restent visibles sans dépendre de la couleur.

### WF-13 — Détail annonce

```text
+------------------------------------------------+
| < Retour Marketplace                           |
| [ Image — texte alternatif ]                   |
| Titre annonce                  [AVAILABLE]     |
| Prix : 00 WUF (simulation)                     |
| Vendeur : Pseudo                               |
| Description                                     |
|------------------------------------------------|
| Acheteur tiers : [ Demander l'achat ]           |
|                 [ Contacter le vendeur ]*       |
| Vendeur : [ Modifier / gérer mon annonce ]      |
| * Lien technique à concevoir ultérieurement     |
+------------------------------------------------+
```

Actions reflètent rôle/état ; l'acheteur ne voit pas une action de paiement avant `ACCEPTED`.

### WF-14 — Créer une annonce

```text
+------------------------------------------------+
| < Annuler          Nouvelle annonce            |
| Titre       (_______________________________)  |
| Description (_______________________________)  |
| Prix WUF*   (________)  *Paiement simulé       |
| Image       [ Choisir une image ] [ aperçu ]   |
| Erreurs par champ                              |
|                         [ Créer l'annonce ]    |
+------------------------------------------------+
```

Le vendeur est implicite (jamais un champ éditable). États : validation, chargement, succès (`AVAILABLE`) et erreur.

### WF-15 — Demande d'achat

```text
+------------------------------------------------+
| Annonce : Titre / prix / vendeur                |
| Vous allez demander l'achat de cette annonce.   |
| Aucun paiement ne sera effectué à cette étape.  |
| [ Annuler ] [ Confirmer la demande ]            |
|------------------------------------------------|
| Succès : demande envoyée — état PENDING         |
| Erreur : annonce indisponible / action interdite|
+------------------------------------------------+
```

### WF-16 — Gestion vente vendeur

```text
+------------------------------------------------+
| Mes ventes > Titre                             |
| État : [ PENDING ]   Acheteur : Pseudo          |
| [ Refuser ] [ Accepter ]                        |
|------------------------------------------------|
| État : [ PAID ]                                 |
| [ Marquer comme expédié ]                       |
|------------------------------------------------|
| État : [ SHIPPED ] — attente de confirmation    |
+------------------------------------------------+
```

Une seule action principale contextuelle ; confirmation pour action irréversible si la règle est retenue.

### WF-17 — Paiement simulé et réception

```text
+------------------------------------------------+
| Achat : Titre          État [ ACCEPTED ]        |
| Prix : 00 WUF — paiement simulé                 |
| Solde démonstration : 00 WUF                    |
| [ Payer avec mon wallet de démonstration ]      |
|------------------------------------------------|
| PAID : « Paiement simulé confirmé »             |
| SHIPPED : [ Confirmer la réception ]            |
| RECEIVED : « Échange terminé »                  |
| Erreur : solde insuffisant / état invalide      |
+------------------------------------------------+
```

## Profil et administration

### WF-18 — Profil

```text
+------------------------------------------------+
| Profil                                          |
| [Avatar, alternative pseudo]  Pseudo [Modifier] |
| Email : adresse privée (non publique)           |
| [ Modifier avatar ]                             |
|------------------------------------------------|
| Wallet de démonstration : 00 WUF                |
| Crédit : réservé ADMIN / ou masqué USER         |
|------------------------------------------------|
| Documents : [ Contrat ]                         |
| Zone sensible : [ Supprimer mon compte ]*       |
| * seulement si politique confirmée              |
+------------------------------------------------+
```

### WF-19 — Administration minimale

```text
+------------------------------------------------+
| Administration (ADMIN)                          |
| Utilisateurs : recherche / rôle USER|ADMIN       |
| Modération : [Post ID] [Comment ID] [Annonce ID]|
| [ Action de modération ]                        |
| Confirmation + résultat / erreur                 |
+------------------------------------------------+
```

Écran conditionnel ADMIN, vérification serveur obligatoire ; l'interface ne constitue pas une autorisation.

## Accessibilité prévue dans tous les wireframes

- un `h1` par écran et titres hiérarchisés ;
- labels visibles associés aux champs, aides et erreurs textuelles ;
- ordre de tabulation logique, focus visible, boutons libellés ;
- médias avec alternative textuelle utile ;
- état et statut exprimés par texte en plus de toute couleur/icone ;
- zones tactiles et actions destructives distinctes ;
- dialogues : focus piégé durant l'ouverture, retour au déclencheur à la fermeture.
