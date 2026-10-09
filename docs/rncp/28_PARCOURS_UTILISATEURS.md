# Parcours utilisateurs

## FLOW-AUTH — inscription, contrat et connexion

```mermaid
flowchart TD
  A[Visiteur : connexion] --> B{Compte existant ?}
  B -- Non --> C[Inscription]
  C --> D{Champs valides et disponibles ?}
  D -- Non --> E[Erreur de champ / revenir au formulaire]
  D -- Oui --> F[Contrat actif]
  F --> G{Accepter ?}
  G -- Non --> C
  G -- Oui --> H[Compte créé / confirmation]
  B -- Oui --> I[Saisir email et mot de passe]
  H --> I
  I --> J{Identifiants et contrat acceptés ?}
  J -- Non --> K[Erreur générique ou contrat à accepter]
  K --> I
  J -- Oui --> L[Home / Feed]
```

**Départ :** visiteur. **Fin :** Home authentifié ou retour au formulaire. **Décisions :** validité, unicité, contrat, authentification. **Erreurs :** email invalide, compte existant, mot de passe invalide, contrat absent/non accepté, réseau.

## FLOW-FEED — publier et interagir

```mermaid
flowchart TD
  A[Feed] --> B{Chargement terminé ?}
  B -- Non --> C[État Loading]
  B -- Oui, vide --> D[État Empty + Créer un post]
  B -- Oui, posts --> E[Liste PostCard]
  E --> F[Créer post]
  F --> G{Entrée valide ?}
  G -- Non --> H[Erreur de formulaire]
  G -- Oui --> I[Publication réussie / Feed actualisé]
  E --> J{Post du USER ?}
  J -- Oui --> K[Modifier ou supprimer]
  J -- Non --> L[Liker / commenter]
  K --> M{Action confirmée et autorisée ?}
  M -- Non --> N[Annuler ou Forbidden]
  M -- Oui --> I
  L --> O{Auto-like ?}
  O -- Oui --> P[Action interdite]
  O -- Non --> Q[Like/commentaire réussi ou erreur]
```

**Départ :** Feed. **Fin :** feed actualisé ou état d'erreur. **Décisions :** ownership, auto-like, validation, visibilité. **Règles :** BR-POST-001 à 004, BR-LIKE-001/002, BR-COMMENT-001.

## FLOW-CHAT — conversation sociale ou négociation

```mermaid
flowchart TD
  A[Ouvrir Chat] --> B[Liste conversations]
  B --> C{Conversation disponible ?}
  C -- Non --> D[État Empty + démarrer conversation]
  C -- Oui --> E[Sélectionner conversation]
  D --> E
  E --> F{Participant ?}
  F -- Non --> G[Forbidden / retour liste]
  F -- Oui --> H[Chargement historique]
  H --> I[Conversation + messages]
  I --> J[Saisir message]
  J --> K{Message valide ?}
  K -- Non --> L[Erreur de saisie]
  K -- Oui --> M[Envoi REST/STOMP]
  M --> N[Confirmation / réception temps réel]
  O[Annonce : Contacter vendeur] -. besoin UX .-> B
```

**Note :** l'entrée « Contacter le vendeur » est une intention UX. Le lien technique Listing–Conversation n'est pas conçu ni présumé dans ce flux. **Erreurs :** aucun chat, chargement, non membre, échec réseau, message invalide.

## FLOW-MARKETPLACE — cycle de vente simulé

```mermaid
stateDiagram-v2
  [*] --> AVAILABLE: vendeur crée annonce
  AVAILABLE --> PENDING: acheteur tiers demande
  PENDING --> ACCEPTED: vendeur accepte
  PENDING --> REFUSED: vendeur refuse
  ACCEPTED --> PAID: acheteur paie (simulation)
  PAID --> SHIPPED: vendeur expédie
  SHIPPED --> RECEIVED: acheteur confirme
  REFUSED --> [*]
  RECEIVED --> [*]
```

| Étape | Acteur / décision | Erreur / retour | Fin locale |
|---|---|---|---|
| Liste | USER consulte/filtres, ou Empty | erreur de chargement → Réessayer | sélection détail ou création |
| Détail AVAILABLE | acheteur tiers : demander ou contacter vendeur | vendeur sur sa propre annonce : action indisponible | PENDING après confirmation |
| PENDING | vendeur : accepter/refuser ; acheteur : attente/contacter | tiers : Forbidden | ACCEPTED ou REFUSED |
| ACCEPTED | acheteur : paiement **simulé** | solde insuffisant/état invalide : erreur sans transition | PAID |
| PAID | vendeur : expédier | rôle/état invalide : Forbidden/erreur | SHIPPED |
| SHIPPED | acheteur : confirmer réception | rôle/état invalide : Forbidden/erreur | RECEIVED |

## États interface communs

| État | Décision UX future |
|---|---|
| Loading | squelette/texte « Chargement… », éviter écran blanc |
| Empty | expliquer l'absence et proposer une action autorisée |
| Success | confirmation courte, non intrusive, reliée à l'action |
| Error | message clair, action Réessayer/Retour, sans erreur technique |
| Forbidden | « Vous n'avez pas l'autorisation », retour sûr, aucune donnée révélée |
