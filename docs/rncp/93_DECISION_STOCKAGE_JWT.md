# Décision sécurité — migration du JWT vers un cookie HttpOnly

Date : 09/10/2026  
Statut : implémentation vérifiée par tests automatisés; contrôle navigateur réel restant.

## Situation avant

Le frontend conservait l'access token JWT dans `localStorage`, le lisait en JavaScript et l'envoyait comme `Authorization: Bearer`. Une XSS pouvait donc extraire le JWT et l'utiliser jusqu'à son expiration (24 heures).

## Décision et implémentation

Le JWT d'authentification est désormais remis dans `WATYOUFACE_AUTH`, un cookie `HttpOnly`, `Path=/`, dont `Max-Age` est aligné sur l'expiration JWT. Le backend donne priorité à ce cookie. Le JSON de login ne contient plus le JWT. Le frontend n'accède plus au JWT, n'écrit plus de Bearer et configure `credentials: include` pour ses appels API.

- `Secure` : false dans les profils locaux HTTP; true par défaut en production HTTPS (`AUTH_COOKIE_SECURE` permet la configuration).
- `SameSite` : `Lax` par défaut, configurable par `AUTH_COOKIE_SAME_SITE`; `None` est refusé sans `Secure`.
- Déconnexion : `POST /api/auth/logout` émet le même cookie avec valeur vide et `Max-Age=0`.
- Compatibilité transitoire : le Bearer reste accepté uniquement lorsque `app.security.jwt.allow-bearer-fallback=true` (profils dev/local); le profil production le désactive. Le frontend ne s'en sert pas.

## CSRF et CORS

Le cookie d'authentification étant envoyé automatiquement par le navigateur, CSRF est maintenant activé avec `CookieCsrfTokenRepository`. Le cookie distinct `XSRF-TOKEN` est lisible par le frontend; les mutations envoient sa valeur dans `X-XSRF-TOKEN`. Le token JWT reste HttpOnly. Les appels cross-origin sont limités à une allowlist explicite avec `allowCredentials=true`; le wildcard d'origine est rejeté. Les origines locales sont configurées pour le développement.

Les chemins `/ws/**` ne passent pas par le contrôle CSRF HTTP : le handshake utilise l'auth cookie, une liste d'origines autorisées, puis les autorisations d'appartenance aux conversations sur les messages STOMP. Cette protection repose sur ces contrôles dédiés, et non sur un prétendu effet du cookie SameSite seul.

## Résultats et limites

Tests MockMvc couvrent Set-Cookie/HttpOnly/Max-Age/SameSite, authentification cookie, absence/invalidité/expiration, logout, CORS credentials et refus/acceptation CSRF. Un test de handshake vérifie que l'identité STOMP vient de l'utilisateur trouvé en base depuis le JWT du cookie. Recherche statique frontend : aucun stockage/lecture JWT localStorage ni construction `Authorization: Bearer` dans `src`.

Le 09/10/2026, les suites ciblées backend (35 tests) et PostgreSQL (5 tests) sont PASS; frontend lint (0 erreur, 1 avertissement Hook existant), 16 tests et build sont PASS. Aucun navigateur n'étant disponible dans l'environnement, la présence visuelle des cookies DevTools, le rechargement réel de session, la navigation réelle des piliers et la suppression visible au logout restent à confirmer manuellement.

## Impact XSS et risque résiduel

L'impact du vol du JWT via XSS est fortement réduit : `HttpOnly` empêche le JavaScript de lire le cookie et le frontend ne détient plus le JWT. Cela ne signifie pas que « XSS est corrigé » : une XSS pourrait encore agir dans la session de la victime, lire le token CSRF ou exfiltrer d'autres données accessibles.

Risques résiduels : compatibilité Bearer temporaire dev/local; validation navigateur réelle restante; politique multi-origines à vérifier au déploiement; pas de refresh token ni révocation immédiate. Réévaluer SameSite et CSRF si les origines frontend/backend deviennent réellement cross-site ou si les flux changent.

Preuves : `evidence/security/phase11c-cookie-auth-tests.txt`, `phase11c-postgres-tests.txt`, `phase11c-frontend-tests.txt`, `phase11c-frontend-token-search.txt`.
