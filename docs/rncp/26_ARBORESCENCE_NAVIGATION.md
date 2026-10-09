# Arborescence de navigation — WatYouFace

## Principes

Les trois piliers doivent rester accessibles en un geste depuis tout écran authentifié : **Feed**, **Chat**, **Marketplace**. Les routes ne constituent pas seules une autorisation : le serveur reste autoritaire. Les données privées (profil, conversations, wallet) ne deviennent jamais publiques du fait de la navigation.

```text
VISITEUR
│
├── Connexion (WF-01)
├── Inscription (WF-02)
│   └── Contrat / acceptation (WF-03)
└── Retour connexion

UTILISATEUR CONNECTÉ
│
├── Feed / Accueil (WF-04 desktop, WF-05 mobile)
│   ├── Créer publication (WF-06)
│   ├── Modifier sa publication (WF-07)
│   ├── Commentaires (WF-08)
│   └── Profil auteur / actions selon droits
├── Chat (WF-09 à WF-11)
│   ├── Liste des conversations
│   ├── Conversation sociale
│   └── Entrée « Contacter le vendeur » depuis une annonce
│       └── LIEN TECHNIQUE Listing–Conversation À CONCEVOIR ULTÉRIEUREMENT
├── Marketplace (WF-12 à WF-17)
│   ├── Liste / filtres
│   ├── Détail annonce
│   ├── Créer annonce
│   ├── Actions acheteur
│   └── Gestion vendeur
├── Profil (WF-18)
│   ├── Avatar / username
│   ├── Email privé
│   ├── Wallet de démonstration
│   └── Suppression compte si règle confirmée
└── Déconnexion

ADMIN
│
├── Toutes les fonctions USER
└── Administration minimale (WF-19)
    ├── Utilisateurs / rôles
    └── Modération ciblée
```

## Navigation globale cible

| Contexte | Desktop | Mobile | Règle UX |
|---|---|---|---|
| Utilisateur connecté | barre supérieure : logo + Feed, Chat, Marketplace ; droite : Profil, Admin conditionnel, Déconnexion | barre inférieure persistante : Feed, Chat, Marketplace, Profil ; menu secondaire pour déconnexion/Admin | élément actif nommé et visuellement identifiable, sans reposer sur la couleur seule |
| Formulaire/modal | bouton explicite Annuler/Retour ; retour à l'écran source | fermeture et retour à l'écran source sans perte silencieuse | focus initial dans le dialogue puis retour au déclencheur |
| Non authentifié | accès Login/Register/Contrat ; redirection depuis zone protégée | même logique | message compréhensible, puis action de connexion |
| Erreur 403 | conserver le contexte sécurisé | écran/zone d'erreur avec Retour | ne pas masquer le refus ou afficher une ressource privée |

## États globaux

Chaque écran conçoit au minimum : `NORMAL`, `LOADING`, `EMPTY`, `SUCCESS`, `ERROR`, `FORBIDDEN`. Les libellés sont textuels, les actions de reprise explicites (« Réessayer », « Retour au feed », « Se connecter ») et l'annonce d'état doit être perceptible au lecteur d'écran dans l'implémentation future.

## Traçabilité RNCP

C2 : navigation et interfaces ; C5 : parcours et choix de conception ; C6 : frontières navigation/API ; C9 : états et erreurs à tester.
