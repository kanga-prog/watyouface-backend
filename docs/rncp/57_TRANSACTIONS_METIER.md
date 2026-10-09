# Transactions métier

## Paiement simulé marketplace

L’opération doit être atomique :

1. charger le listing avec verrou ;
2. vérifier `ACCEPTED` ;
3. vérifier acheteur associé et identité courante ;
4. vérifier qu’aucune transaction du listing n’existe ;
5. vérifier solde, débiter acheteur, créditer vendeur ;
6. créer `Transaction` ;
7. positionner listing à `PAID` ;
8. committer tout ou annuler tout.

`MarketplaceService.paySecured` est annoté `@Transactional`, utilise `ListingRepository.findByIdForUpdate` (`PESSIMISTIC_WRITE`) et délègue à `TransactionService.transfer`, également transactionnel. `TransactionRepository.existsByListing_Id` bloque un second paiement au niveau applicatif.

## Cible d’intégrité

- ajouter une contrainte unique BDD sur `transaction.listing_id` lors des migrations ;
- conserver un test double paiement et un test solde insuffisant ;
- mapper les conflits métier en 409 ;
- documenter isolation/concurrence lors de la recette PostgreSQL.

Autres opérations atomiques à considérer : création compte + contrat, création conversation + participants, suppression média + métadonnée selon la politique retenue.
