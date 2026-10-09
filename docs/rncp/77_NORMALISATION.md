# Normalisation

## Analyse

- **1NF :** tables avec valeurs atomiques ; les collections deviennent tables relationnelles (`conversation_users`, likes, comments). Les URLs média sont des scalaires, pas des listes.
- **2NF :** PK techniques simples : pas de dépendance partielle. Les UQ composites cibles renforcent les associations sans remplacer les PK.
- **3NF :** rôle est dans User, statut dans Listing/Transaction ; vendeur/acheteur sont des FK et non dupliqués. Transaction conserve montant/acteurs/listing comme trace métier volontaire d’un transfert.

## Points de vigilance

`User.acceptedContract` + `acceptedContractVersion` + `UserContract` recouvrent partiellement l’acceptation : à clarifier (état courant dans User, historique dans UserContract) et contraindre par `UQ(user_id,contract_id)`. La conservation de `receiver_id` dans Message est un choix de modèle qui doit rester cohérent avec conversations de groupe. Aucun `listing_id` Conversation n’est ajouté au MVP afin d’éviter un couplage prématuré.

