# Modèle de menaces architectural

| Actif | Menace / vecteur | Impact | Contrôle observé ou prévu | Preuve future |
|---|---|---|---|---|
| Ressources user | BOLA/IDOR par id client | lecture/modification indue | identité SecurityContext, ownership/participant, tests 403 | tests API ownership |
| Feed | auto-like / endpoints Like legacy | règle contournée | contrôle service cible, issue #2 | test auto-like + unicité |
| JWT | vol XSS/localStorage | usurpation | CSP/XSS à renforcer, expiration 24h, logout client | revue XSS, décision stockage |
| Secret | secret committé/config | signature JWT/BDD compromise | variables env, longueur min JWT ; rotation à faire | audit historique/rotation issue #4 |
| Upload | MIME/taille/nom/chemin malveillants | RCE, saturation, fuite | `resolvePath` anti-traversal ; validation stricte à compléter | tests upload + politique |
| WebSocket | abonnement conversation non membre | fuite messages | interceptor participant observé | test STOMP / service |
| Marketplace | double paiement/concurrence | perte intégrité solde | `@Transactional`, verrou pessimiste, existence transaction | tests concurrence/double paiement |
| Rôles | élévation privilège | modération/crédit indu | contrôle ADMIN serveur | tests USER vs ADMIN |
| Erreurs | stacktrace/message | fuite technique | messages erreur désactivés ; handler partiel | test 500/404 |
| XSS/CSRF | contenu affiché / Bearer | vol token/action | React échappement standard ; CSRF désactivé car JWT Bearer, à justifier/contrôler | audit frontend/CSP |

## Frontières de confiance

```text
Navigateur / entrée utilisateur (NON FIABLE)
        │ HTTPS REST + STOMP JWT
        ▼
Spring Security + validation + services (frontière d’autorisation)
        │ JPA/transactions
        ▼
PostgreSQL (données persistantes)

Upload externe ──► validation/type/taille/nom ──► media/ filesystem
WebSocket client ──► CONNECT JWT + participant SUBSCRIBE ──► conversation
```
