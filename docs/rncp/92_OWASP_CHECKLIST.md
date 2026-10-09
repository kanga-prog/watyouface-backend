# Phase 11 — Checklist OWASP par risque

Évaluation au 08/10/2026. Cette checklist associe mesures et gaps aux familles OWASP; elle ne déclare pas l'application globalement conforme OWASP.

| Famille pertinente | Application WatYouFace | État vérifié | Action / preuve |
|---|---|---|---|
| Broken Access Control | Ownership, wallet, membership conversation, rôle JWT | Contrôles serveur et tests ciblés présents | Continuer couverture endpoint par endpoint et STOMP E2E |
| Cryptographic Failures / Session Management | JWT HS256, BCrypt, secrets externalisés; JWT placé dans cookie `HttpOnly` | Signature/expiration, TTL 24 h, cookie `WATYOUFACE_AUTH` HttpOnly, Path `/`, SameSite=Lax par défaut, Secure actif par défaut en production; aucun JWT dans le JSON de login ni localStorage frontend | Vérification DevTools réelle; rotation secrets historiques encore actifs; pas de révocation/refresh token |
| Injection | JPA/JPQL, DTO validation | Requêtes inspectées paramétrées; validation partielle | Revoir requêtes et limites de champs à chaque évolution |
| Insecure Design | Marketplace/double paiement, login | Transaction, contrainte, rollback et concurrence vérifiés PostgreSQL; limitation du login testée | Limiteur non distribué; audit trail métier absent |
| Security Misconfiguration | CORS, CSRF, headers, profils | Allowlist explicite, credentials, wildcard origine rejeté; cookie CSRF distinct; CSRF contrôlé sur requêtes HTTP state-changing | Confirmer les origines de production, HTTPS/HSTS et politique CSP; couper H2 console hors dev |
| Vulnerable and Outdated Components | npm/Maven | Après mise à jour ciblée, `npm audit --omit=dev` rapporte zéro vulnérabilité runtime connue | Frontend `docs/rncp/evidence/security/npm-audit-after.txt`; scan Maven invoqué mais bloqué avant analyse par la base NVD indisponible faute de clé API, état Maven inconnu |
| Identification and Authentication Failures | Login et cookie d'authentification | Cookie HttpOnly émis, lu côté backend, expiré au logout; JWT absent/invalide/expiré refusé; fallback Bearer désactivé en production | Tests cookie/CSRF; inspection manuelle du cookie, refresh réel et logout navigateur encore à effectuer; limiteur mémoire mono-instance; aucune révocation immédiate |
| Software and Data Integrity Failures | dépendances et upload | Contrôles serveur de type/taille | MIME client falsifiable; pipeline signature/CI à venir |
| Security Logging and Monitoring Failures | erreurs/logs | 500 générique; quelques logs neutralisés | Pas de monitoring/alerting ni audit log complet |
| SSRF | URLs externes | Aucun chemin SSRF ciblé repéré dans le périmètre inspecté; pas d'import arbitraire d'URL démontré | Réauditer si récupération d'URL externe ou proxy URL est ajoutée |
| XSS / client injection | Rendu React et session navigateur | Aucun dangerouslySetInnerHTML trouvé; le JWT HttpOnly n'est pas lisible par JavaScript, ce qui réduit l'impact de son exfiltration | Une XSS peut toujours agir avec la session et accéder au token CSRF; continuer les contrôles XSS et revue des URL/dépendances |
| CSRF | Cookie auth envoyé automatiquement | `CookieCsrfTokenRepository`, token distinct lisible par JS + header `X-XSRF-TOKEN`; CORS credentials limité aux origines configurées; test 403 sans token | Vérifier les scénarios cross-origin réels et le cookie dans DevTools; `/ws/**` dépend de l'allowlist Origin et des contrôles membership dédiés |
| WebSocket | CONNECT/SUBSCRIBE/SEND | JWT CONNECT; membership; destinations limitées | Tests interceptor; E2E navigateur/broker restant |

## Base de vérification

- Backend : tests ciblés cookie/CSRF/CORS et intercepteur STOMP (35/35 PASS le 09/10/2026); suite complète à enregistrer dans le rapport Phase 11C.
- PostgreSQL : cinq tests isolés exécutés séparément sur la base locale.
- Frontend : lint, tests, build et `npm audit --omit=dev` après mise à jour ciblée (0 advisory runtime au scan du 08/10/2026).
- Maven Dependency-Check : tentative exécutée; bloquée par l'absence de clé NVD/API et de base locale exploitable; aucune conclusion de vulnérabilité Maven.
- Les limites et les risques résiduels restent dans le rapport de Phase 11.
