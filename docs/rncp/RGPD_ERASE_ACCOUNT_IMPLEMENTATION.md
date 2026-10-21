# RGPD-ERASE-01 — Effacement d'un compte actif

Date : 20 octobre 2026
Périmètre : backend local et tests. Aucune suppression de compte, média ou donnée n'a été exécutée en production.

> Cette note décrit une mesure technique. Elle ne remplace pas la validation métier/juridique des durées de conservation ni l'information des personnes concernées.

## Décision de conception

La suppression SQL directe de `users` n'était pas viable : les clés étrangères de `wallet`, `listings`, `transaction`, `conversation_users`, `video_share` et `user_contract` ne possèdent pas de règle SQL `ON DELETE`, tandis que des cascades JPA historiques sont hétérogènes. Une suppression directe pouvait donc échouer ou détruire des éléments utiles à l'intégrité du Marketplace.

La solution est une **anonymisation irréversible transactionnelle** du compte : la ligne `users` est conservée comme tombstone technique afin de préserver les FK, mais son nom, email, mot de passe, avatar, rôle et acceptation courante de contrat sont remplacés ou retirés. `deleted_at` marque l'effacement. L'utilisateur ne peut plus être chargé par les flux d'authentification ni ajouté aux conversations/partages.

La migration ajoutée est `V4__add_users_deleted_at_for_account_erasure.sql`. Elle ne modifie pas V1, V2 ou V3.

## Stratégie par entité

| Entité | Stratégie lors de l'effacement | Justification |
|---|---|---|
| `User` | anonymisation, `deleted_at`, compte rendu inutilisable | maintient les FK historiques sans identité active |
| `Wallet` | suppression physique | le solde démo n'est plus nécessaire après anonymisation ; aucune FK transactionnelle vers wallet |
| `Post` | conservation anonymisée : contenu remplacé, URLs média retirées | évite une cascade implicite et conserve la cohérence des discussions ; plus de contenu personnel |
| `Comment` | suppression physique si écrit par le compte | interaction sans nécessité d'historique Marketplace |
| `Like` | suppression physique | interaction sans valeur de conservation identifiée |
| `ConversationUser` | suppression physique de la participation | le compte n'est plus participant ; évite de garder une association à un destinataire effacé |
| `Conversation` | suppression physique seulement si elle devient vide, avec ses messages | aucune conversation sans participant n'est conservée |
| `Message` | messages sortants anonymisés et marqués supprimés ; messages entrants conservés | préserve le fil restant sans conserver le contenu envoyé par la personne effacée |
| `Listing` | sans transaction : suppression physique ; avec transaction : conservation, `hidden=true`, texte/image neutralisés | les transactions gardent leur unicité et leur preuve technique sans contenu personnel affichable |
| `Transaction` | conservation | intégrité du scénario Marketplace, montants et liens à l'annonce ; les parties sont des tombstones anonymes si effacées |
| `Video` | conservation anonymisée : titre remplacé, URL retirée | évite une FK rompue vers commentaires/likes tout en retirant le média |
| `VideoShare` | suppression physique si émetteur ou destinataire effacé | partage devenu sans objet et FK retirée |
| `Contract` | conservation inchangée | contenu/version de contrat non personnel en soi |
| `UserContract` | conservation liée au tombstone | la preuve d'acceptation n'identifie plus directement une personne après anonymisation ; sa nécessité et durée restent à valider juridiquement |

## Atomicité et médias

`AccountErasureService.erase` est annoté `@Transactional`. Une exception avant commit annule les mutations de base de données.

Les candidats média (avatar, post, vidéo, annonce) sont traités **après commit** uniquement par `MediaReferenceCleanupService`. Avant effacement physique, celui-ci vérifie qu'aucun `User`, `Post`, `Listing` ou `Video` ne référence encore l'URL. Seules les URLs applicatives sous `/media/avatars`, `/media/posts`, `/media/marketplace` et `/media/videos` sont admises ; les URLs externes, défauts avatar et tentatives de traversal sont ignorés. Un échec de suppression fichier ne remet pas en cause l'intégrité de la transaction DB et devra être traité par la future réconciliation RGPD-MEDIA-02.

## Marketplace, messages et session

- Une annonce sans `Transaction` est supprimée avec son image candidate au nettoyage.
- Une annonce engagée est masquée et neutralisée ; statut, transaction, acheteur/vendeur et montant restent intègres.
- Le wallet du compte effacé est supprimé, sans toucher au wallet de l'autre partie.
- La participation de conversation est retirée. Les messages envoyés deviennent `Message supprimé par son auteur`; une conversation vide est supprimée avec ses messages.
- Le cookie est effacé lorsque l'utilisateur supprime son propre compte. Les recherches utilisées par les filtres JWT, WebSocket, vidéos et conversations écartent les comptes `deleted_at != null`, ce qui invalide l'usage normal d'un JWT encore présent.

## Tests ajoutés et résultats

`AccountErasureIntegrationTests` (H2 + Flyway V1 à V4) couvre :

1. compte simple, wallet, anonymisation et rejet de rechargement de session ;
2. posts/comments/likes, vidéo/partage, annonce disponible, conversation/messages et suppression de médias non référencés ;
3. vendeur impliqué dans une transaction `PAID`, intégrité de l'annonce, transaction et wallet de l'acheteur ;
4. conversation devenue vide, sans violation de FK.

Résultat local : `./mvnw test` — **110 tests, 0 échec, 0 erreur, 6 ignorés**. Les ignorés sont des tests PostgreSQL explicitement conditionnels.

`PostgresAccountErasureIntegrationTests` ajoute une preuve pour les FK PostgreSQL réelles et une transaction `PAID`. Il ne s'exécute que si `RUN_POSTGRES_TESTS=true`, si `DB_URL` cible strictement `localhost` ou `127.0.0.1`, et si les identifiants locaux nécessaires sont présents. Ces garde-fous empêchent toute exécution contre Railway. Il n'a pas été exécuté ici, faute d'instance PostgreSQL locale explicitement fournie.

## Limites résiduelles

- Aucun inventaire ni purge des médias historiques/non associés n'est fait : RGPD-MEDIA-02 reste nécessaire.
- Les choix de durée et de conservation de `Transaction` et `UserContract` nécessitent validation métier/juridique.
- L'interface frontend ne propose pas encore le parcours et l'information d'effacement.
- Le token JWT ne possède pas de révocation centralisée ; l'accès applicatif est toutefois rejeté car l'utilisateur effacé n'est plus chargé.
- La validation sur PostgreSQL local réel est à effectuer avant déclaration de preuve complète inter-SGBD.
