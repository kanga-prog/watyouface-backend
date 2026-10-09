# Dictionnaire de données synthétique

| Table | Colonne | Type cible | Nullable | Unique / FK | Description | Sensibilité | Exemple fictif |
|---|---|---|---|---|---|---|---|
| users | email | varchar(254) | non | UQ | identifiant connexion | privée | alice@example.com |
| users | password | varchar(255) | non | — | hash BCrypt, jamais mot de passe | critique | `$2a$…` |
| users | role | varchar(16) | non | CHECK | USER/ADMIN | admin | USER |
| post | content | text | oui actuel | author_id FK | contenu publication | membre/public | Bonjour démo |
| comment | content | text | non | author_id FK | contenu commentaire | membre | Disponible ? |
| likes | user_id/post_id | bigint | cible non | FK + UQ cible | interaction | membre | 1 / 2 |
| messages | content | varchar(2000) | oui actuel | conversation/sender/receiver FK | message privé | élevée | Bonjour Bruno |
| listings | price | numeric(12,2) | non | CHECK >0 | prix démo | métier | 30.00 |
| wallet | balance | numeric(12,2) | non | user_id UQ FK | crédits démo | élevée métier | 100.00 |
| transaction | listing_id | bigint | non | FK + UQ cible | paiement démo | élevée métier | 42 |
| video | url | varchar(2048) | oui | — | référence média | variable | /media/videos/x.mp4 |

Le MPD et le script de référence contiennent la liste détaillée ; les colonnes non annotées NN dans ce dictionnaire sont proposées comme cible et non affirmées existantes.

