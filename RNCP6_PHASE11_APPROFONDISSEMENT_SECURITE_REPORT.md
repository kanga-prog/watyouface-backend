# Phase 11 — Approfondissement sécurité

## 1. Objectif
Réévaluer les contrôles applicatifs backend/frontend, corriger les faiblesses vérifiées sans refonte, et produire des preuves traçables.

## 2. Périmètre
Les worktrees backend et frontend étaient déjà fortement modifiés par les phases précédentes. Aucun commit/PR n'a été créé afin de ne pas mélanger des lots non isolables.

## 3. Authentification
Le backend crée un JWT après authentification; le filtre vérifie signature et expiration, puis recharge l'utilisateur. Les mots de passe sont encodés avec BCrypt. Les routes protégées refusent un token absent. Durée observée: 24 h. Aucun refresh token ni révocation immédiate n'est implémenté.

## 4. Autorisation
Ownership/membership contrôlés pour les ressources auditées (Post, Video, Wallet, Chat, Marketplace). Correction Phase 11: les authorities viennent du rôle courant en base, et non du claim JWT. Un ancien token ADMIN perd donc ses droits après rétrogradation. Couverture API exhaustive par ressource non démontrée.

## 5. JWT
Secret externalisé, seuil minimal 32 octets, HS256 fixe, signature et expiration validées. Tests API pour token altéré, expiré, absent et rôle obsolète. Le frontend stocke le token dans localStorage: une XSS peut le voler jusqu'à expiration. Pas de refresh token.

## 6. CORS / CSRF
CORS utilise des origins configurables par profil; le wildcard est rejeté; méthodes et headers explicites. Tests preflight autorisé en local et origine tierce refusée. Production doit définir l'allowlist. CSRF est désactivé car l'API est stateless et le bearer est porté explicitement par Authorization, pas envoyé automatiquement par cookie. Réévaluer si cookie auth est introduit.

## 7. Headers
Tests MockMvc vérifient X-Content-Type-Options nosniff, X-Frame-Options DENY et Referrer-Policy. HSTS est opt-in via HSTS_ENABLED pour ne pas l'activer en HTTP local; l'activer seulement derrière HTTPS fiable. CSP non déployée.

## 8. Validation
DTO auth/message/listing et entrées pertinentes utilisent Bean Validation; tests message existants. Upload image/vidéo limité par types et taille. Test ajouté: MIME non autorisé refusé avant persistance/écriture.

## 9. XSS
Aucun dangerouslySetInnerHTML détecté dans le frontend inspecté; React échappe le texte interpolé. Cela ne neutralise pas toutes les URL à risque, les XSS de dépendances ou le risque localStorage. CSP à évaluer.

## 10. SQL Injection
Repositories inspectés utilisent JPA/JPQL paramétré; aucune concaténation de requête native avec entrée utilisateur repérée dans le périmètre. Réauditer chaque nouvelle requête custom.

## 11. Upload
Services image/vidéo: limites 20 MiB image et 200 MiB vidéo, allowlists MIME, image réellement décodée, ffmpeg limité à 120 s, noms serveur et résolution de chemins sous media. Le chemin /media/** reste public; stockage local sans antivirus/quarantaine. Le MIME annoncé par le client ne prouve pas le format.

## 12. STOMP
CONNECT exige un Bearer et un utilisateur existant. SUBSCRIBE est limité au motif exact /topic/conversations/{id} avec appartenance vérifiée; SEND est limité à /app/chat.sendMessage pour un principal authentifié. Tests au niveau intercepteur; pas de test client/broker bout-en-bout.

## 13. Erreurs
ApiExceptionHandler renvoie un message générique pour erreur inattendue et gère validation, accès refusé, absence et conflit. Des anciens contrôleurs ont encore des réponses locales; uniformisation totale et test dédié du corps HTTP 500 restent à compléter.

## 14. Logs
Impressions System.out et stack traces retirées des chemins audités (initialisation, runner migration, PDF); journaux média sans payload. /test-upload est dev-only et ne retourne plus la liste des noms. spring.jpa.show-sql reste actif en dev/local: le désactiver lors d'un partage de logs.

## 15. Dépendances
Audit initial `npm audit --omit=dev`: 3 packages runtime signalés (2 high, 1 critical): `react-router-dom`/`react-router` 7.9.5 et `websocket-driver` 0.7.4 transitif via SockJS. Après mises à jour ciblées compatibles dans les mêmes majors vers `react-router-dom`/`react-router` 7.18.4 et `websocket-driver` 0.7.5, `npm audit --omit=dev` rapporte 0 vulnérabilité runtime. L'audit complet a ensuite identifié 14 findings dans l'arbre total (1 low, 3 moderate, 10 high, principalement toolchain/dev); le dry-run indiquait des résolutions semver sans saut majeur. `npm audit fix` sans `--force` a mis à jour 38 paquets (Vite 7.2.2 → 7.3.7 inclus), et les audits complet/runtime finaux rapportent tous deux zéro vulnérabilité.

Un audit ponctuel `./mvnw org.owasp:dependency-check-maven:check` a été lancé sans modifier `pom.xml`. Il est **BLOCKED avant analyse**: le service NVD a rejeté l'absence de clé API (`Invalid API Key`, puis `NoDataException: No documents exist`). Aucun nombre/CVE Maven n'est donc établi. Voir `docs/rncp/evidence/security/maven-dependency-check.txt`; le scanner doit être relancé avec une clé fournie hors dépôt.

## 16. Secrets
Secrets runtime externalisés et aucune valeur affichée. L'historique Git contient des traces de fichiers/configurations potentiellement sensibles, dont un ancien .env frontend; aucun secret actif n'a été validé ici. Faire tourner toute valeur historique encore valide. Aucune réécriture Git n'a été lancée.

## 17. Tests sécurité
- ./mvnw clean test: BUILD SUCCESS; 41 tests comptabilisés, 0 échec, 0 erreur, 5 tests PostgreSQL conditionnels ignorés dans cette exécution générale.
- ./mvnw -Dtest=PostgresPaymentIntegrationTests test: PostgreSQL réel, 5/5 PASS (commit, unicité, rollback, solde insuffisant, concurrence).
- MediaUploadSecurityTests: 2 PASS inclus dans le total général.
- SecurityApiIntegrationTests: 15 PASS, JWT, rôle actuel, CORS et headers.
- StompAuthorizationTests: 4 PASS au niveau intercepteur.
- Frontend: lint PASS, Vitest 14/14 PASS (6 fichiers), build PASS.
- npm audit --omit=dev: FAIL sécurité, trois avis runtime dont un critique.

## 18. Matrice risques
docs/rncp/90_MATRICE_SECURITE.md répertorie actifs, menaces, impacts, preuves et risques résiduels.

## 19. Threat model
docs/rncp/91_THREAT_MODEL.md présente STRIDE et les frontières de confiance.

## 20. OWASP
docs/rncp/92_OWASP_CHECKLIST.md relie les familles applicables aux mesures et gaps sans déclaration globale de conformité.

## 21. Risques résiduels
1. Le rate limiting du login est mémoire et mono-instance; en multi-instance, utiliser un datastore partagé; derrière un reverse proxy, la stratégie de confiance pour l'adresse source doit être définie.
2. JWT conservé dans localStorage; risque documenté dans `docs/rncp/93_DECISION_STOCKAGE_JWT.md`, cible cookie HttpOnly/Secure/SameSite avec réévaluation CSRF.
3. Le scan Maven Dependency-Check est bloqué par l'absence de clé NVD; l'exposition Maven reste inconnue, pas déclarée saine.
4. La rotation de secrets potentiellement présents dans l'historique n'est pas confirmée; aucun historique Git n'a été réécrit.
5. Médias servis publiquement; antivirus/quarantaine absents.
6. STOMP sans E2E broker; couverture au niveau intercepteur seulement.
7. Aucune vulnérabilité npm connue ne reste dans l'audit complet après le correctif semver. Maintenir une revue régulière des advisories et lockfile.
8. Couverture API BOLA exhaustive non démontrée; CSP et monitoring restent à traiter.

## 22. Mapping RNCP
- C1: configuration externe, profils et exécution reproductible; preuve partielle.
- C3: ownership/membership, rôle courant, paiement et validation, tests ciblés.
- C6: filtres, sécurité, services et frontières de confiance documentés.
- C8: JPA paramétré, transactions et contrainte DB.
- C9: suite générale, PostgreSQL réel et tests ciblés.
- C10: configuration de déploiement CORS/HSTS préparée; HTTPS et procédure à valider.
- C11: analyse dépendances/DevSecOps préparée; CI, scanner et alerting absents.

## Remédiation Phase 11B

| Risque | Avant | Action | Test / preuve | Après | Risque résiduel |
|---|---|---|---|---|---|
| Dépendances npm | Runtime: 2 HIGH + 1 CRITICAL; audit complet ensuite 1 LOW + 3 MODERATE + 10 HIGH | Mises à jour ciblées runtime puis `npm audit fix` standard, sans `--force` ni saut de major (38 paquets) | npm audit complet et runtime finaux; lint/tests/build | 0 vulnérabilité connue dans les deux audits finaux | Refaire périodiquement l'audit; aucun correctif ne garantit l'absence future d'advisories |
| Brute force login | Aucun limiteur | Limiteur mémoire thread-safe, 5 échecs/60 s par IP, 429 + Retry-After, nettoyage périodique | 6 tests ciblés (unitaire/API), incluant fenêtre expirée sans attente | Contrôle testé; aucun secret/password dans la réponse | Mono-instance; reverse proxy et stockage distribué à concevoir avant scalabilité |
| Dépendances Maven | Scanner absent | Tentative ponctuelle Dependency-Check Maven sans changement `pom.xml` | `evidence/security/maven-dependency-check.txt` | BLOCKED: clé API NVD absente, aucun résultat d'analyse | Vulnérabilités Maven inconnues; relancer avec accès NVD configuré |
| JWT localStorage | Risque résiduel peu détaillé | Décision dédiée et mesures compensatoires documentées | `93_DECISION_STOCKAGE_JWT.md`, audit du rendu frontend | Risque reconnu, non déclaré corrigé | Vol possible en cas de XSS; cible cookie HttpOnly avec CSRF |
| Secrets historiques | Rotation non confirmée | Checklist de statut documentée sans valeur de secret | configuration/runtime et historique déjà audité, aucune valeur conservée | Configuration actuelle externalisée; rotation historique inconnue | rotation des éventuelles valeurs encore actives à confirmer; pas de purge Git |

## 23. Conclusion
Les dépendances npm runtime et l'arbre complet sont sans vulnérabilité connue après corrections compatibles; lint, tests et build frontend passent. La protection du login répond avec 429 et est couverte aux niveaux unitaire et API. La tentative Maven est bloquée par l'accès NVD/API: ce résultat est explicitement inconnu, jamais présenté comme un scan sain. Le localStorage JWT, la rotation des secrets historiques, le rate limit mono-instance et le scan Maven restent documentés comme risques/gaps résiduels. Tests finaux: suite Maven 47 comptabilisés, 42 PASS et 5 tests PostgreSQL conditionnels ignorés; PostgreSQL dédié 5/5 PASS; frontend 14/14 PASS. Clôture fonctionnelle/RNCP: OUI sous réserve des risques résiduels explicités et du blocage technique du scanner Maven; clôture Git/PR: NON, worktrees mélangés.

## Phase 11C — Migration JWT HttpOnly

### Avant

Le JWT était stocké dans `localStorage` puis envoyé par JavaScript en `Authorization: Bearer`. Une XSS pouvait extraire ce jeton.

### Décision et changements

Le login émet maintenant `WATYOUFACE_AUTH` avec `HttpOnly`, `Path=/`, durée alignée sur l'expiration JWT et `SameSite=Lax` par défaut. Le cookie est non-Secure en HTTP local, Secure par défaut dans le profil production; `SameSite=None` est interdit sans Secure. Le JWT n'est plus présent dans le JSON de réponse.

Le logout `POST /api/auth/logout` expire le même cookie. Le filtre JWT lit d'abord le cookie; une compatibilité Bearer transitoire existe seulement dans les profils dev/local et est désactivée en production. Le frontend ne lit ni n'écrit le JWT, n'ajoute pas de Bearer et envoie les credentials cookie; le Chat utilise aussi le cookie du handshake WebSocket au lieu d'un token JavaScript.

### CSRF / CORS

CSRF est activé avec `CookieCsrfTokenRepository`: le cookie distinct `XSRF-TOKEN` peut être lu par le frontend et doit être renvoyé dans `X-XSRF-TOKEN`. Les réponses CORS autorisent credentials uniquement pour les origines configurées; wildcard rejeté. Les chemins WebSocket sont exemptés de CSRF HTTP car le handshake utilise le cookie authentifié, une allowlist Origin et les contrôles membership/destination STOMP; aucun test broker/navigateur E2E n'est revendiqué.

### Vérifications du 09/10/2026

| Vérification | Résultat | Preuve |
|---|---|---|
| Tests ciblés cookie, CSRF, CORS, handshake et régressions sécurité | 35/35 PASS | `docs/rncp/evidence/security/phase11c-cookie-auth-tests.txt` |
| PostgreSQL réel (paiement, contrainte, rollback, concurrence) | 5/5 PASS | `docs/rncp/evidence/security/phase11c-postgres-tests.txt` |
| Frontend lint | PASS, 0 erreur; 1 avertissement Hook préexistant | `docs/rncp/evidence/security/phase11c-frontend-tests.txt` |
| Frontend tests | 16/16 PASS | même preuve |
| Frontend build | PASS | même preuve |
| Recherche JWT localStorage/Bearer dans le code frontend | aucune correspondance | `docs/rncp/evidence/security/phase11c-frontend-token-search.txt` |
| Suite backend complète | 60 comptabilisés, 0 échec/erreur, 5 tests PostgreSQL conditionnels ignorés; `BUILD SUCCESS` | `docs/rncp/evidence/security/phase11c-backend-suite.txt` |

Les cinq tests PostgreSQL ignorés dans la suite générale ont été lancés séparément sur la base locale et passent 5/5.

### Résultat sécurité et limite

L'impact du vol du JWT via XSS est fortement réduit, et non « corrigé » : une XSS peut encore effectuer des actions via la session ou lire le token CSRF. L'intégration cookie/CSRF/CORS est couverte par MockMvc et le frontend par les tests automatisés. Aucun navigateur n'est installé/disponible dans l'environnement le 09/10/2026 : inspection DevTools, refresh réel, navigation des piliers et logout visible restent **MANUEL À FAIRE**, sans verdict PASS inventé. La clôture concerne l'implémentation et les preuves automatisées; cette limite demeure dans la checklist de recette.

### Réaudit final — 09/10/2026

Le code confirme : aucun JWT d'authentification écrit/lu/supprimé dans `localStorage` ou `sessionStorage`, aucun Bearer construit par le frontend; les seules valeurs locales observées sont username/avatar. Tous les appels API passent par le wrapper `credentials: include`; les mutations joignent le CSRF token distinct. Le JSON login ne contient pas de JWT; les tests couvrent cookie HttpOnly/Path/SameSite/Max-Age, routes protégées (absent/invalide/expiré), logout, preflight CORS credentials, CSRF absent/refusé et token valide/accepté. Secure est false en local HTTP et configuré true par défaut en profil prod; toute surcharge d'environnement doit rester conforme à HTTPS.

Résultats relancés : lint frontend PASS (0 erreur, 1 avertissement Hook existant), tests frontend 16/16 PASS, build PASS, audit runtime npm 0 vulnérabilité; `./mvnw clean test` PASS (60 comptabilisés, 5 tests PostgreSQL conditionnels ignorés); PostgreSQL réel 5/5 PASS. JWT localStorage : **ABSENT**. Cookie/CSRF/CORS : **PASS en tests automatisés**. Recette navigateur : **MANUEL À FAIRE**.
