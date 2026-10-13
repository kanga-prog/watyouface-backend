# Contraintes d’intégrité

| Règle | État | Preuve / action |
|---|---|---|
| username/email unique | APPLIQUÉE V1 | contraintes `uq_users_username` / `uq_users_email`, base PostgreSQL vide validée |
| wallet par user | APPLIQUÉE V1 | `uq_wallet_user`, FK et NN validés par V1 |
| titre/prix/status listing NN | APPLIQUÉE V1 | colonnes NN + checks de prix/statut dans V1 |
| transaction amount/listing/users NN | APPLIQUÉE V1 | colonnes NN, FK, check montant et statut |
| message conversation/sender/receiver NN | APPLIQUÉE V1 | colonnes NN et FK dans V1 |
| `like(user,post)` unique | APPLIQUÉE V1 | `uq_likes_user_post` ; test fonctionnel spécifique à compléter |
| `like(user,video)` unique | APPLIQUÉE V1 | `uq_likes_user_video` ; test fonctionnel spécifique à compléter |
| exactement une cible Like/Comment (post XOR video) | APPLIQUÉE V1 | checks `ck_likes_target` / `ck_comment_target` sur base vide |
| participant conversation unique | APPLIQUÉE V1 | `uq_conversation_users_participant` |
| acceptation contrat unique user/version | APPLIQUÉE V1 | `uq_user_contract_user_contract` |
| transaction unique par listing | APPLIQUÉE V1 / TESTÉE | `uq_transaction_listing_id`; `PostgresPaymentIntegrationTests` 5/5 sur PostgreSQL 16.15 temporaire |
| solde/prix/montant positifs | APPLIQUÉE V1 | checks `ck_wallet_balance`, `ck_listings_price`, `ck_transaction_amount` |
| status enum valide | APPLIQUÉE V1 | checks rôles, `listings.status` et `transaction.status` |

## Risque

Les validations Java protègent le flux normal mais une contrainte DB est la dernière barrière face à concurrence, scripts ou endpoint legacy. La V1 a été appliquée et validée sur une base PostgreSQL vide ; toute évolution exige une nouvelle migration testée, sans réécrire V1. Limite : le redeploy Railway réel reste à effectuer.
