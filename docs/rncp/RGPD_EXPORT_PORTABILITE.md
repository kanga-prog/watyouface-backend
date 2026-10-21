# RGPD-EXPORT-04 — Export et portabilité des données utilisateur

Date : 20 octobre 2026
Version du format : `1.0`

## Périmètre et accès

L’utilisateur connecté peut télécharger une copie JSON via `GET /api/users/me/export`. L’identifiant est lu uniquement par `Authz.me()` dans le contexte Spring Security ; aucun identifiant de compte n’est accepté dans l’URL ou le corps. Le rôle ADMIN ne change pas ce comportement : un administrateur exporte uniquement son propre compte. Une session absente est refusée par Spring Security. L’anonymisation invalide le JWT existant ; les appels d’un compte effacé sont donc rejetés en 401 par le filtre JWT. Le service vérifie aussi que le compte chargé n’a pas `deletedAt`.

La réponse est un DTO dédié (`UserDataExportDTO`), lu dans une transaction en lecture seule. Aucune entité JPA n’est sérialisée directement.

## Données présentes dans le JSON

- `user` : identifiant, pseudo, e-mail, rôle, avatar, acceptation et version du contrat courant.
- `posts` : publications de l’utilisateur, contenu, URLs de médias et date.
- `comments` : commentaires écrits par l’utilisateur, date et identifiant de la publication/vidéo cible.
- `likes` : interactions de l’utilisateur et identifiant de la cible.
- `messages` : messages envoyés par l’utilisateur seulement, avec conversation, contenu, date et états édité/supprimé.
- `conversations` : conversations auxquelles l’utilisateur participe, métadonnées de conversation seulement ; aucun autre participant n’est listé.
- `listings` : annonces où l’utilisateur est vendeur ou acheteur, avec rôle, contenu Marketplace et statut.
- `transactions` : transactions où il est acheteur (`fromUser`) ou vendeur (`toUser`), montant démo, statut, annonce liée et date ; aucun identifiant ou profil du tiers.
- `wallet` : solde actuel de démonstration et indication qu’il n’a pas de valeur monétaire ; aucun historique bancaire.
- `videos` : vidéos téléversées par l’utilisateur, titre, URL et date.
- `videoShares` : partages envoyés ou reçus, ID de la vidéo, état vu/non vu et direction ; aucune identité de l’autre personne.
- `contracts` : versions de contrat acceptées/refusées et date disponible, sans données de tiers.

`exportGeneratedAt` utilise UTC ISO-8601. Les champs de date gardent le format ISO produit par Jackson. Les collections sont toujours présentes, y compris vides. `formatVersion` permet de faire évoluer le contrat sans ambiguïté.

## Données exclues et minimisation des tiers

Le DTO ne contient jamais le mot de passe ou hash, JWT, cookie, CSRF, secret, identifiants de base, en-têtes d’authentification ni credentials. Il ne contient pas les e-mails, pseudos, contenus privés reçus ou listes de participants d’autres utilisateurs. Les messages entrants sont exclus : seuls les messages dont l’utilisateur est l’expéditeur sont exportés. Les conversations et références Marketplace utilisent des identifiants techniques et n’exposent pas les données privées du tiers. Les fichiers média binaires ne sont pas intégrés ; seuls les chemins/URLs déjà enregistrés sont présents.

Le contenu et le prix des annonces sont exportés lorsqu’elles sont liées à l’utilisateur en tant que vendeur ou acheteur ; ces champs correspondent aux données métier de l’annonce Marketplace. Le wallet est explicitement un mécanisme de démonstration, pas un compte bancaire.

## Contrat JSON

```json
{
  "formatVersion": "1.0",
  "exportGeneratedAt": "2026-10-20T12:00:00Z",
  "user": {
    "id": 42,
    "username": "exemple",
    "email": "exemple@example.test",
    "role": "USER",
    "avatarUrl": "/media/avatars/example.png",
    "acceptedContract": true,
    "acceptedContractVersion": "2026-1"
  },
  "posts": [], "comments": [], "likes": [], "messages": [],
  "conversations": [], "listings": [], "transactions": [],
  "wallet": null, "videos": [], "videoShares": [], "contracts": []
}
```

## Frontend

Le Profil affiche « Télécharger mes données ». L’appel passe par le client API cookie/credentials et récupère la protection CSRF seulement pour les mutations ; cet endpoint GET est authentifié par le cookie JWT HttpOnly. Le navigateur télécharge `watyouface-data-export-YYYY-MM-DD.json`. Le bouton expose l’état de préparation et les échecs par un message `role=alert`.

## Tests et limites

Les tests d’intégration vérifient l’authentification, l’export des catégories disponibles, la minimisation des messages et données de tiers, l’absence de champs sensibles, le filtrage des posts/listings/transactions, la restriction ADMIN à son compte, un compte sans données associées et l’exclusion d’un compte anonymisé. Ils utilisent H2 et des données synthétiques uniquement.

Résultats : `./mvnw test` — **125 tests reportés, 0 échec, 0 erreur, 6 ignorés conditionnellement** (tests PostgreSQL non activés) ; tests frontend — **14 fichiers, 52 tests, 0 échec** ; build Vite réussi ; lint sans erreur avec un warning préexistant dans `Admin.jsx`.

Limites : aucun média binaire/ZIP ; certains champs historiques n’existent pas (par exemple date de création du compte, date de participation à une conversation) et ne peuvent donc pas être exportés. Ce JSON est un export de données dans un format structuré ; la portée juridique de la portabilité des différents contenus et la politique de conservation doivent être confirmées dans la gouvernance RGPD du projet.
