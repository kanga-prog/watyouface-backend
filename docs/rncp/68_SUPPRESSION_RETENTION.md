# Suppression, cascades et rétention

| Événement | ORM observé | Risque / décision cible |
|---|---|---|
| suppression User | cascade ALL vers posts, messages envoyés/reçus, commentaires, likes ; pas de wallet/listing/transaction explicitement mappés | suppression massive/incohérente possible ; préférer politique RGPD documentée : anonymisation ou suppression contrôlée après analyse légale |
| suppression Post | cascade ALL + orphanRemoval vers Comment/Like | cohérent pour contenu associé, mais médias fichier doivent être traités séparément |
| suppression Conversation | cascade ALL + orphanRemoval participants | `Message` n’est pas mappé en collection : comportement DB à vérifier ; cible FK/retention explicite |
| suppression Listing | pas de cascade ORM vers Transaction | suppression bloquée métier après PAID ; FK SQL doit empêcher une perte d’historique |
| suppression Video | cascade ALL vers Comment/Like ; VideoShare non mappé | orphelins/violations possibles à vérifier |

## Rétention

La durée actuelle n’est **NON PROUVÉE**. Cible à définir avec RGPD : justification métier, délai, purge/anonymisation, sauvegardes, droit d’accès/effacement et maintien minimal des traces transactionnelles. Aucun comportement n’est modifié dans cette phase.

