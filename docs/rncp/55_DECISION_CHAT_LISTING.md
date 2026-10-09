# Décision d’architecture — Conversation ↔ Listing

## Contexte

Le chat sert à la fois aux échanges sociaux et à la négociation marketplace. L’interface peut proposer « Contacter le vendeur », mais aucune relation `Listing–Conversation` n’est actuellement démontrée dans les entités observées.

| Critère | Option A — chat générique | Option B — conversation liée à Listing |
|---|---|---|
| Simplicité | élevée, pas de schéma supplémentaire | moyenne, FK nullable ou table liaison |
| UX | contexte à rappeler par le client | contexte annonce explicite |
| Traçabilité | faible : négociation difficile à identifier | forte : conversation rattachée à une annonce |
| Sécurité | contrôle participant identique | contrôle participant + cohérence vendeur/acheteur/listing |
| Couplage | faible | plus fort entre Chat et Marketplace |
| Évolution | discussion multi-annonces possible | une conversation peut être dédiée à une annonce |

## Recommandation : B ciblée, pas immédiate

Pour le **MVP actuel**, conserver l’option A évite de modifier données/API juste pour la démonstration. Pour une fonctionnalité de négociation réellement livrée, adopter **Option B** : `Conversation.listing_id` nullable (ou liaison explicite) et créer/réutiliser une conversation seulement si les participants sont le vendeur et le demandeur associé. Les services doivent vérifier ces deux appartenances côté serveur.

## Décision

**ADR-006 :** la liaison technique est différée ; le prototype l’annonce comme intention UX. Toute mise en œuvre future requiert migration, contrat API, tests BOLA/participants et mise à jour du modèle de données.
