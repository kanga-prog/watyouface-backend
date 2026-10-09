# Matrice acteurs / droits

**Légende :** OUI = autorisé ; NON = interdit ; CONDITIONNEL = précondition, ownership, état ou politique à satisfaire. `Propriétaire` et `Non propriétaire` qualifient un USER ; `Acheteur` et `Vendeur` sont contextuels, pas des rôles techniques.

| Fonctionnalité / action | Visiteur | USER | ADMIN | Propriétaire | Non propriétaire | Acheteur | Vendeur | Justification |
|---|---|---|---|---|---|---|---|---|
| Consulter contrat actif | OUI | OUI | OUI | — | — | — | — | endpoint public constaté. |
| S'inscrire / se connecter | OUI | NON | NON | — | — | — | — | accès initial public. |
| Accéder à une page/API protégée | NON | OUI | OUI | — | — | — | — | JWT requis. |
| Consulter son profil | NON | OUI | OUI | OUI | NON | — | — | identité du jeton. |
| Modifier son profil/avatar | NON | CONDITIONNEL | CONDITIONNEL | OUI | NON | — | — | owner ; ADMIN selon opération/politique. |
| Consulter feed authentifié | NON* | OUI | OUI | — | — | — | — | *comportement actuel ; visibilité produit à confirmer. |
| Créer post | NON | OUI | OUI | — | — | — | — | auteur issu du contexte. |
| Modifier/supprimer post | NON | CONDITIONNEL | OUI | OUI | NON | — | — | owner/admin constaté. |
| Commenter | NON | OUI | OUI | — | — | — | — | post visible ; détail visibilité à définir. |
| Modifier/supprimer commentaire | NON | CONDITIONNEL | OUI | OUI | NON | — | — | owner/admin constaté. |
| Liker post tiers | NON | CONDITIONNEL | CONDITIONNEL | NON (cible propre) | OUI (cible tierce) | — | — | exigence cible ; auto-like non conforme au code actuel. |
| Créer/ouvrir conversation privée | NON | OUI | OUI | — | — | — | — | participants valides ; conversation groupe à encadrer. |
| Lire messages / s'abonner STOMP | NON | CONDITIONNEL | CONDITIONNEL | Participant | NON membre | — | — | appartenance obligatoire. |
| Envoyer message | NON | CONDITIONNEL | CONDITIONNEL | Participant | NON membre | — | — | appartenance obligatoire. |
| Créer annonce | NON | OUI | OUI | Vendeur | — | — | OUI | vendeur fixé serveur. |
| Modifier/supprimer annonce | NON | CONDITIONNEL | OUI | Vendeur | NON | — | OUI | règles d'état à préciser. |
| Demander achat | NON | CONDITIONNEL | OUI | NON si vendeur | OUI si annonce AVAILABLE | OUI | NON | interdit d'acheter sa propre annonce. |
| Accepter/refuser | NON | CONDITIONNEL | OUI | Vendeur | NON | NON | OUI | annonce PENDING. |
| Payer simulé / confirmer réception | NON | CONDITIONNEL | OUI | Acheteur | NON | OUI | NON | état requis ACCEPTED/SHIPPED. |
| Expédier | NON | CONDITIONNEL | OUI | Vendeur | NON | NON | OUI | état PAID requis. |
| Consulter wallet ciblé | NON | CONDITIONNEL | OUI | OUI | NON | — | — | owner/admin. |
| Créditer wallet démonstration | NON | NON | OUI | — | — | — | — | ADMIN uniquement. |
| Gérer utilisateurs/rôles/modération | NON | NON | OUI | — | — | — | — | API `/api/admin/**`. |

## Décisions de droits à confirmer en phase 2

1. La lecture du feed et des annonces reste-t-elle réservée aux membres, comme dans le comportement actuel ?
2. Dans quelles limites ADMIN peut-il modifier, supprimer ou consulter les ressources d'autrui et comment ces actions sont-elles journalisées ?
3. Une conversation de groupe doit-elle inclure automatiquement son créateur, et qui peut ajouter des participants ?
4. Quelle règle de visibilité lie une conversation à une annonce sans divulguer son contenu à des tiers ?

## Traçabilité RNCP

La matrice soutient C3/C8 (autorisations côté métier et données), C5 (acteurs), C6 (séparation des responsabilités) et C9 (cas 401/403 à tester).
