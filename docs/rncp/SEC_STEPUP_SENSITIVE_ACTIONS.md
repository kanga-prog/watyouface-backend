# SEC-STEPUP-01 — Vérification renforcée des actions sensibles

Date : 20 octobre 2026
Périmètre : changement de l’adresse e-mail et du mot de passe. Ce mécanisme réduit le risque d’utilisation d’une session active avec le seul mot de passe courant ; il ne constitue pas une déclaration de conformité ni une authentification multifacteur indépendante.

## Menace et workflow

Une session déjà ouverte ne suffit plus à finaliser ces opérations. Le parcours est : utilisateur authentifié + mot de passe courant → demande de code → vérification du code reçu par e-mail → preuve courte à usage unique → PATCH de l’opération sensible. L’identité et le compte visé proviennent exclusivement de `Authz.me()` ; aucun `userId` n’est accepté dans les routes de step-up.

Pour `EMAIL_CHANGE`, le code est envoyé à l’adresse actuellement enregistrée, donc à l’ancienne adresse avant la modification. Pour `PASSWORD_CHANGE`, il est envoyé à l’adresse enregistrée au moment de la demande. Les routes sont protégées par l’authentification JWT cookie et CSRF existants.

Endpoints :

- `POST /api/users/me/step-up/request` avec `action` (`EMAIL_CHANGE` ou `PASSWORD_CHANGE`) et `currentPassword` ; réponse `202` avec expiration, sans code.
- `POST /api/users/me/step-up/verify` avec l’action et le code à six chiffres ; réponse `200` avec une preuve temporaire opaque et son expiration.
- `PATCH /api/users/me/email` et `PATCH /api/users/me/password` exigent cette preuve en plus des validations existantes. La preuve est liée au compte et à l’action, et consommée au plus une fois.

## Stockage, durée et limitation

Les challenges sont en mémoire dans `StepUpChallengeService`, par instance. Le code OTP est généré avec `SecureRandom`, puis stocké uniquement sous forme BCrypt ; il n’est ni retourné par l’API, ni écrit dans les journaux. La preuve retournée est aléatoire ; seul son SHA-256 est conservé en mémoire. Les challenges et preuves expirent après 300 secondes par défaut. La configuration permet de régler le TTL de 1 à 600 secondes (`STEP_UP_CHALLENGE_TTL_SECONDS`).

Une demande exige le mot de passe courant et un compte actif. Le plafond est de 5 vérifications par challenge et 3 demandes par utilisateur sur 900 secondes par défaut ; le dépassement de demandes répond `429` avec `Retry-After`. Paramètres : `STEP_UP_MAX_CODE_ATTEMPTS`, `STEP_UP_REQUEST_LIMIT` et `STEP_UP_REQUEST_WINDOW_SECONDS`. Les limites sont en mémoire, non distribuées et contournables entre instances ou après redémarrage. Redis n’a pas été ajouté.

## Envoi e-mail et configuration

L’implémentation réutilise `MailService` et la configuration JavaMail existante ; aucune dépendance supplémentaire n’a été ajoutée. `MAIL_ENABLED` reste désactivé par défaut, y compris en développement. Si le mail n’est pas activé ou l’envoi échoue, l’opération échoue fermée (`503`) et aucun OTP n’est divulgué dans la réponse. Pour tester localement, utiliser exclusivement un SMTP sink local isolé (par exemple Mailpit sur `localhost:1025`) avec `MAIL_ENABLED=true`, sans adresse ni infrastructure de production. Les tests automatisés remplacent le sender par un mock.

Les paramètres SMTP d’authentification, TLS et timeouts restent configurables ; les identifiants SMTP doivent être injectés hors dépôt. Ne pas activer l’envoi réel avant configuration et validation opérationnelle.

## Intégration des changements de compte

`AccountProfileService` consomme la preuve correspondant à l’action après les validations de mot de passe, email et politique existantes. Le changement d’e-mail conserve normalisation/unicité et n’a lieu qu’après vérification du code reçu sur l’ancienne adresse. Le changement du mot de passe conserve BCrypt, expire le cookie courant et ferme la session navigateur.

Les JWT déjà copiés ne sont pas révoqués centralement et peuvent rester utilisables jusqu’à leur expiration (24 h maximum selon la configuration actuelle). Cette phase ne crée ni blacklist, ni rotation centralisée, ni stockage de session.

## Nature de la vérification et limites

Le code envoyé par e-mail est un facteur de contrôle supplémentaire, mais il repose sur la boîte e-mail qui peut elle-même être compromise. Il s’agit d’un step-up par canal e-mail, pas d’une vraie 2FA indépendante. La disponibilité et la sécurité de la boîte et du SMTP sont des dépendances. Le stockage mémoire signifie aussi que challenge, preuve et compteurs ne sont ni partagés entre instances ni persistants après redémarrage.

Évolutions possibles, hors périmètre actuel : TOTP, WebAuthn/passkeys, stockage partagé anti-bruteforce et révocation JWT centralisée.

## Preuves et tests

- `StepUpChallengeServiceTests` couvre génération/destination, mot de passe incorrect, code incorrect et limite de tentatives, expiration, usage unique, liaison à l’action et à l’utilisateur, limite de demandes et mode mail désactivé.
- `SecurityApiIntegrationTests` couvre le parcours email/password avec code vérifié, la réauthentification, la portée `/me`, les protections d’authentification et CSRF existantes, et la réponse sans credentials.
- `Profile.test.jsx` couvre les étapes email et mot de passe, l’envoi/vérification simulés, l’état UI, l’absence de préremplissage des mots de passe et la redirection après changement du mot de passe.

Les tests n’envoient aucun courriel réel : le sender est mocké. Validation locale de cette phase : `./mvnw -q test` — 139 tests reportés, 0 échec, 0 erreur, 6 conditionnels ignorés ; les classes ciblées `SecurityApiIntegrationTests` (46) et `StepUpChallengeServiceTests` (7) passent. Frontend : 14 fichiers / 58 tests Vitest réussis, build Vite réussi, lint avec 0 erreur et 1 avertissement préexistant dans `src/pages/Admin.jsx`.
