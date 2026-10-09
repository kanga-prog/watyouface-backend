# Décision sécurité — migration du JWT vers un cookie HttpOnly

Date : 09/10/2026  
Statut : migration vérifiée par code et tests automatisés; contrôle navigateur réel à faire manuellement (navigateur indisponible dans l'environnement d'audit).

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

## Audit final Phase 11C — 09/10/2026

| Contrôle | Résultat vérifié |
|---|---|
| JWT d'authentification dans `localStorage` / `sessionStorage` | Absent; les usages restants concernent username/avatar. Le module de décodage JWT n'a pas d'appelant actif dans `src`. |
| Login / réponse | Le JWT est posé dans `WATYOUFACE_AUTH` HttpOnly; il n'est pas retourné dans le JSON de login. |
| Attributs cookie | `Path=/`, `Max-Age=86400` aligné sur l'expiration JWT, `SameSite=Lax`; local HTTP sans Secure, production Secure par défaut. `SameSite=None` exige Secure. |
| Validation backend | Cookie prioritaire; route protégée refusée sans cookie ou avec cookie invalide/expiré; le mode strict rejette Bearer. |
| Fallback Bearer | Transitionnel dev/local seulement; désactivé en production et inutilisé par le frontend. L'exception CSRF liée au Bearer ne s'applique que si ce fallback local est activé. |
| Frontend / API | Wrapper central avec `credentials: include`; méthodes mutantes transmettent le token CSRF distinct dans `X-XSRF-TOKEN`. |
| CORS / CSRF | `allowCredentials=true`, origines explicites, wildcard rejeté; preflight testé. CSRF actif par `XSRF-TOKEN`; login sans token refusé et avec token accepté. |
| Logout | Le cookie auth est expiré avec `Max-Age=0`, `Path=/`, HttpOnly et SameSite; l'interface efface l'état local et redirige. |
| Tests backend | `./mvnw clean test`: 60 comptabilisés, 0 échec/erreur, 5 tests PostgreSQL conditionnels ignorés. |
| Tests PostgreSQL | 5/5 PASS sur PostgreSQL réel. |
| Tests frontend | lint PASS (0 erreur, 1 avertissement Hook préexistant), Vitest 16/16, build PASS, `npm audit --omit=dev`: 0 vulnérabilité. |
| Recette navigateur | MANUEL À FAIRE : aucun navigateur disponible pour inspecter DevTools, recharger une session réelle et confirmer le logout visuel. Les tests API ne sont pas présentés comme preuve DevTools. |

### Recette manuelle à exécuter hors environnement sans navigateur

1. Se connecter avec un compte fictif; vérifier dans DevTools → Application → Local Storage qu'aucun JWT n'est présent.
2. Dans Application → Cookies, vérifier `WATYOUFACE_AUTH` avec HttpOnly, Path `/`, SameSite `Lax` et expiration conforme; Secure doit être actif en production HTTPS.
3. Recharger la page et confirmer que `/api/users/me` conserve la session.
4. Tester Feed, Chat, Marketplace et Profil avec l'API authentifiée.
5. Se déconnecter; confirmer l'expiration du cookie et le refus d'une route protégée.

Clôture code/tests Phase 11C : OUI. La recette DevTools/navigation réelle reste MANUELLE et ne reçoit pas de verdict PASS tant qu'elle n'a pas été effectuée. L'impact du vol JWT via XSS est fortement réduit, sans prétendre corriger les XSS elles-mêmes.
