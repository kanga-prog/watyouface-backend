# 97 — Variables d’environnement

## Règles

Les secrets sont fournis au processus par l’environnement ou un gestionnaire de secrets; ils ne sont ni stockés dans Git, ni intégrés au bundle Vite. `VITE_*` est public et visible par tout utilisateur du navigateur. Ne jamais y placer un secret.

## Backend Spring Boot

| Variable | Usage | Requise / valeur cible | Profil / remarque |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | choix du profil | `prod` en production | Ne pas démarrer la production avec `dev`. |
| `DB_URL` | JDBC PostgreSQL | requise en `prod` | URL privée, TLS selon hébergeur; jamais imprimée avec paramètres sensibles. |
| `DB_USERNAME` | compte applicatif PostgreSQL | requis | Privilèges minimaux; distinct du compte d’administration/migration. |
| `DB_PASSWORD` | authentification DB | secret requis | Gestionnaire de secrets, rotation et aucun log. |
| `JWT_SECRET` | signature JWT | secret requis, au moins 32 octets | `application.properties` échoue si absent/trop court. |
| `CORS_ALLOWED_ORIGINS` | origines autorisées | requise; liste exacte | Avec le reverse proxy same-origin, valeur égale à l’origine HTTPS publique; jamais `*`. |
| `AUTH_COOKIE_SECURE` | attribut Secure du cookie d’auth | `true` en production HTTPS | Profil prod le met à `true` par défaut. |
| `AUTH_COOKIE_SAME_SITE` | politique SameSite | `Lax` cible initiale | À revalider si les domaines/origines deviennent cross-site. |
| `HSTS_ENABLED` | HSTS Spring | `true` uniquement après HTTPS fiable | Le proxy TLS doit être correctement configuré; ne pas activer sur HTTP local. |
| `SERVER_PORT` | port interne Spring | `9090` par défaut en prod | Le reverse proxy termine TLS; n’exposer publiquement que 80/443. |
| `MAIL_ENABLED` | activation du mail | optionnelle | Les paramètres `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH`, `MAIL_SMTP_STARTTLS`, `MAIL_SMTP_STARTTLS_REQUIRED`, `MAIL_CONNECTION_TIMEOUT`, `MAIL_TIMEOUT`, `MAIL_WRITE_TIMEOUT` ne sont nécessaires que si le mail est activé. Identifiants = secrets. |
| `LOGIN_RATE_LIMIT_MAX_ATTEMPTS` | seuil de limitation login | défaut code: 5 | Limiteur mémoire mono-instance; non distribué. |
| `LOGIN_RATE_LIMIT_WINDOW_SECONDS` | fenêtre login | défaut code: 60 | À partager/centraliser si plusieurs instances. |

`UPLOAD_DIR` n’est pas une variable implémentée actuellement. Les médias sont écrits sous `${user.dir}/media`; le `WorkingDirectory` du service doit donc désigner le répertoire persistant voulu. Les limites multipart configurées sont 205 MB par fichier et 255 MB par requête, avec des limites métier plus basses par type.

## Frontend Vite

| Variable | Usage | Valeur cible |
|---|---|---|
| `VITE_API_BASE` | base publique utilisée par REST et URLs média | origine HTTPS publique de l’application si le proxy route `/api` et `/media`; sinon URL HTTPS de l’API. |
| `VITE_WS_URL` | URL SockJS `/ws` | origine HTTPS publique suivie de `/ws`; SockJS négocie ensuite une connexion WebSocket sécurisée (`wss`). |

Ces deux valeurs sont incorporées lors du build. Changer la configuration après `vite build` ne change pas le bundle déjà produit.

## Profils présents

- `dev`: H2 mémoire par défaut, port 8081, console H2 active, SQL affiché, cookie non Secure et fallback Bearer permis. Développement seulement.
- `local`: PostgreSQL externe, port 8080, cookie non Secure par défaut, origine localhost; `ddl-auto=validate`.
- `prod`: PostgreSQL externe, port `SERVER_PORT` (9090 par défaut), `ddl-auto=validate`, erreurs sans message interne, cookie Secure, fallback Bearer interdit. L’allowlist CORS doit être fournie.

Les valeurs des fichiers de configuration sont des défauts de développement; les secrets restent externes.

## Matrice des environnements

| Environnement | Frontend / API | Base | Profil / variables | Sécurité et usage |
|---|---|---|---|---|
| LOCAL | Vite `http://localhost:5173`; API `http://localhost:8080` | PostgreSQL locale selon `DB_URL` | `local`; variables DB/JWT dans l’environnement local | cookie Secure false pour HTTP; CORS localhost explicite; données fictives. |
| DÉVELOPPEMENT | Vite localhost; backend `http://localhost:8081` | H2 mémoire par défaut ou DB de développement isolée | `dev`; JWT de test; aucune vraie donnée | profil permissif de développement uniquement: console H2/SQL et fallback Bearer présents. |
| STAGING | placeholders `https://<staging-domain>`; mêmes URLs publiques ou origine API dédiée | PostgreSQL isolée, sans données de production | `prod` ou configuration staging dédiée durcie; secrets distincts | HTTPS, cookie Secure, CORS exacte, essais de migration/sauvegarde et recette avant promotion. Domaine non choisi. |
| PRODUCTION | placeholder `https://<domain>`; cible VPS same-origin `/api`, `/media`, `/ws` | PostgreSQL privée/persistante | `prod`; secrets externes; VITE_API_BASE/VITE_WS_URL au build | HTTPS obligatoire, cookie HttpOnly/Secure, CSRF actif, CORS exacte, sauvegardes/restauration supervisées. Aucun environnement réel actuellement configuré. |

Les URL staging/production sont des modèles et ne sont pas des domaines existants. Si le déploiement adopte des sous-domaines, ils doivent rester same-site pour le cookie Lax, ou la politique cookie/CSRF/CORS doit être réévaluée avant lancement.
