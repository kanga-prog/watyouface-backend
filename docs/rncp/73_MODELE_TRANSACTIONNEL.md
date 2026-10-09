# Modèle transactionnel — paiement simulé

```mermaid
flowchart TD
  B[BEGIN] --> L[charger Listing FOR UPDATE]
  L --> V{status ACCEPTED et buyer courant ?}
  V -- non --> R[rollback / 403 ou 409]
  V -- oui --> U{transaction listing déjà présente ?}
  U -- oui --> R
  U -- non --> S{solde acheteur suffisant ?}
  S -- non --> R
  S -- oui --> D[débiter Wallet acheteur]
  D --> C[créditer Wallet vendeur]
  C --> T[insérer Transaction]
  T --> P[Listing = PAID]
  P --> E[COMMIT]
```

## Données touchées

`listings` (verrou/statut), `wallet` acheteur, `wallet` vendeur, `transaction`. Le service actuel possède `@Transactional`, verrou pessimiste sur listing et garde `existsByListing_Id`; la contrainte unique DB sur listing est une cible indispensable pour compléter la défense contre concurrence.

