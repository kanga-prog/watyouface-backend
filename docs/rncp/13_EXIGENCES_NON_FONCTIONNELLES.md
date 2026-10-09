# Exigences non fonctionnelles affinées

| ID | Catégorie | Exigence | Critère mesurable | Vérification | Priorité |
|---|---|---|---|---|---|
| NFR-SEC-001 | SECURITY | Une ressource d'autrui est inaccessible sauf ownership/ADMIN explicite. | 403 pour post, commentaire, annonce, wallet, vidéo et conversation non autorisés. | tests API/service | MUST |
| NFR-SEC-002 | SECURITY | Toute identité métier provient du contexte authentifié serveur. | aucun `userId` client ne modifie contrat, propriétaire, vendeur ou expéditeur. | revue + tests BOLA | MUST |
| NFR-SEC-003 | SECURITY | Le chat exige appartenance REST et STOMP. | non-membre : 403 ou abonnement refusé ; aucun message reçu. | test service + E2E STOMP | MUST |
| NFR-SEC-004 | SECURITY | Les entrées critiques sont validées. | email/champ requis/prix négatif → 400 ; limites documentées. | MockMvc + tests UI | MUST |
| NFR-SEC-005 | SECURITY | Les secrets sont externes au code et ne sont pas loggés. | aucun secret dans fichiers versionnés courants ; démarrage refuse JWT absent/faible. | revue config | MUST |
| NFR-SEC-006 | SECURITY | Erreurs client sans stacktrace ni détail sensible. | 400/401/403/404/409 contrôlés ; réponses 5xx génériques. | tests de réponses | SHOULD |
| NFR-PRIV-001 | PRIVACY | Les données personnelles sont minimisées et leur finalité documentée. | registre email, avatar, messages, contenus, transactions ; finalité/accès/rétention. | revue RGPD | MUST |
| NFR-PRIV-002 | PRIVACY | Centraliser les usages ne diminue pas automatiquement le niveau de protection requis. | risque privacy documenté et mesures de contrôle d'accès/rétention prévues. | registre risques | MUST |
| NFR-ACCESS-001 | ACCESSIBILITY | Les fonctions MUST sont utilisables au clavier et ont des libellés. | parcours login, post, chat, annonce testé clavier ; labels/erreurs présents. | recette manuelle RGAA ciblée | SHOULD |
| NFR-ACCESS-002 | ACCESSIBILITY | Les retours d'erreur sont perceptibles et compréhensibles. | erreur associée au champ/action, sans couleur seule. | revue UI manuelle | SHOULD |
| NFR-PERF-001 | PERFORMANCE | Les listes principales évitent un chargement non borné. | pagination ou limite documentée pour feed, annonces, conversations/messages. | revue API + test de charge léger | SHOULD |
| NFR-MAINT-001 | MAINTAINABILITY | Contrôleurs, services et repositories ont des responsabilités séparées. | règles critiques testées en service/API ; contrat API documenté. | revue architecture | MUST |
| NFR-REL-001 | RELIABILITY | Une annonce ne peut pas être payée deux fois. | second appel refusé, une seule Transaction ; test vert. | test intégration transactionnel | MUST |
| NFR-REL-002 | RELIABILITY | Débit et crédit wallet sont atomiques dans la simulation. | aucune écriture partielle après échec ; test solde insuffisant. | test intégration | MUST |
| NFR-PORT-001 | PORTABILITY | Le projet est lançable par une procédure versionnée. | commande et variables documentées, validation reproductible. | recette environnement | SHOULD |
| NFR-USAB-001 | USABILITY | Le statut d'une annonce est visible et les actions non disponibles ne sont pas ambiguës. | scénario marketplace sans connaissance du code ; messages d'état vérifiés. | recette utilisateur fictive | SHOULD |

## Limites assumées à communiquer

- Le wallet sert uniquement à la démonstration : ni banque, ni PSP, ni preuve d'encaissement réel.
- Les objectifs de performance sont à chiffrer après définition du jeu d'essai et de l'environnement cible.
- Le déploiement, la supervision, la sauvegarde et la CI restent hors de la présente phase ; ils ne peuvent pas encore être déclarés conformes.
