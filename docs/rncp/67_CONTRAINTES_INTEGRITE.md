# Contraintes d’intégrité

| Règle | État | Preuve / action |
|---|---|---|
| username/email unique | EXISTANTE | `User @Column(unique=true)` |
| wallet par user | EXISTANTE | `Wallet.user_id unique=true NN` |
| titre/prix/status listing NN | EXISTANTE | annotations `Listing` |
| transaction amount/listing/users NN | EXISTANTE | annotations `Transaction` |
| message conversation/sender/receiver NN | EXISTANTE | `optional=false` |
| `like(user,post)` unique | À AJOUTER | règle métier + no UQ observée ; issue #5/#2 |
| `like(user,video)` unique | À AJOUTER | no UQ observée |
| exactement une cible Like/Comment (post XOR video) | À AJOUTER / À VÉRIFIER | CHECK cible ; modèle actuel nullable |
| participant conversation unique | À AJOUTER | `ConversationUser` sans UQ composée |
| acceptation contrat unique user/version | À AJOUTER | repository vérifie, contrainte DB absente |
| transaction unique par listing | CIBLE DOCUMENTÉE / NON APPLIQUÉE | garde service `existsByListing_Id`, contrainte déclarée dans l'entité et migration manuelle `src/main/resources/db/manual/V1__unique_transaction_listing.sql`; PostgreSQL non joignable pendant cette phase |
| solde/prix/montant positifs | PARTIEL | service wallet ; CHECK DB cible |
| status enum valide | PARTIEL | `@Enumerated(STRING)` ; CHECK/enum SQL cible |

## Risque

Les validations Java protègent le flux normal mais une contrainte DB est la dernière barrière face à concurrence, scripts ou endpoint legacy. Toute contrainte nouvelle nécessite migration testée sur base vierge.
