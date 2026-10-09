# Indexation — existant et cible

| Table | Colonne(s) | Requête / justification | Statut |
|---|---|---|---|
| users | email, username | connexion/recherche user ; UQ crée généralement un index | EXISTANT via UQ annotation |
| post | created_at | feed trié desc (`findAllByOrderByCreatedAtDesc`) | À AJOUTER cible |
| comment | post_id, created_at | commentaires d’un post | À AJOUTER cible |
| likes | post_id, user_id ; video_id, user_id | compteur/vérification unicité | À AJOUTER cible, UQ composites |
| conversation_users | user_id ; conversation_id,user_id | liste et appartenance | PARTIEL : requêtes, index/UQ non prouvés |
| messages | conversation_id,sent_at | historique/pagination | À AJOUTER cible |
| listings | status ; seller_id ; buyer_id | catalogue/mes annonces/actions | À AJOUTER cible |
| transaction | listing_id UQ ; from_user_id ; to_user_id | double paiement/historique | À AJOUTER cible |
| video_share | receiver_id ; sender_id | listes partagées | À AJOUTER cible |

Les index seront créés seulement dans migration après mesure/explain ; trop d’index ralentit les écritures.

