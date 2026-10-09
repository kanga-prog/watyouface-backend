# Prototype UX vs application réelle

| Fonction | Prototype | Application actuelle | Écart / référence |
|---|---|---|---|
| Connexion / contrat | navigation et erreurs simulées | React + Spring Boot, contrôle contrat réel à compléter | GAP-AUTH-001 |
| Création / édition post | tableau JavaScript local, ownership visuel | composants/API existants, contrôle serveur à vérifier par tests | matrice exigence/code, POST-… |
| Like | auto-like indisponible dans la cible | risque auto-like / endpoints legacy identifié | GAP-LIKE-001/002, issue backend #2 |
| Chat | bulles locales, entrée vendeur simulée | WebSocket/API réels ; appartenance à renforcer | GAP-CHAT-001, issue backend #3 |
| Marketplace | machine à états locale | logique backend réelle à sécuriser/tester | MARKET-…, issue backend #15 |
| Wallet | 100→70 crédits démo, aucune transaction | wallet démonstration serveur, crédit USER déjà ciblé par hardening | WALLET-001/002 |
| Autorisation | messages UX `forbidden` | Spring Security et contrôles serveur nécessaires | C3/C8/C9 |
| Données | Alice/Bruno/Charlie Demo, mémoire navigateur | PostgreSQL et médias réels | C7/C8 |

## Limites non négociables

- aucune vraie authentification, session, token ou mot de passe ;
- aucune API, base PostgreSQL, persistance, upload ou média réel ;
- aucun WebSocket ni contrôle d’appartenance réel ;
- aucun paiement, transaction, banque, PSP ou argent réel ;
- aucune autorisation serveur ;
- aucune preuve de sécurité backend, de déploiement ou de CI/CD.

Le prototype doit donc être présenté au jury comme une **simulation UX interactive**, complémentaire mais distincte de l’application React/Spring Boot.

