# 98 — Procédure de déploiement Hostinger (préparation)

> Procédure préparatoire, non exécutée sur une infrastructure réelle. L’hébergeur cible est Hostinger, mais l’offre réellement souscrite dans le compte (VPS ou Web/Cloud), le domaine et le serveur disponibles ne sont pas confirmés. Les valeurs entre chevrons sont des paramètres à choisir; ne pas les copier comme secrets.

## Architecture retenue

Pour une première démonstration hébergée entièrement chez Hostinger, cible conditionnelle **Hostinger VPS Linux** : Nginx termine HTTPS et sert le build React; il reverse-proxy `/api`, `/media` et `/ws` vers Spring Boot sur `127.0.0.1:9090`; PostgreSQL est accessible uniquement par loopback/réseau privé. Le frontend et l’API partagent une origine HTTPS, ce qui simplifie cookies, CSRF et CORS. Le répertoire persistant des médias est `${user.dir}/media` du processus Java. Cela ne confirme pas qu’un abonnement VPS est disponible dans le compte.

Hostinger documente Java (qui requiert le contrôle root) comme technologie VPS; PostgreSQL et les reverse proxies sont aussi présentés pour VPS auto-géré. Ses offres Web/Cloud Node.js prennent en charge React/Vite pour frontend mais listent des frameworks backend JavaScript, pas Spring Boot/Java. Si le compte ne possède qu’une offre Web/Cloud, héberger l’application entière sur cette seule offre n’est pas validé : confirmer un VPS Hostinger ou prévoir un backend Java hébergé ailleurs.

Références officielles consultées le 10/10/2026 : [langages/frameworks Hostinger](https://www.hostinger.com/support/which-programming-languages-and-frameworks-are-supported-at-hostinger/), [VPS Hostinger auto-géré](https://www.hostinger.com/support/8852150-what-is-a-self-managed-vps-at-hostinger/), [Web Apps Node.js Hostinger](https://www.hostinger.com/support/how-to-deploy-a-nodejs-website-in-hostinger/).

| Option | Simplicité | Coût | Maintenance | Sécurité | Reproductibilité |
|---|---|---|---|---|---|
| A — frontend, backend et PostgreSQL gérés séparément | Déploiement applicatif simple | variable, dépend des offres | faible à moyenne | bonne si réseau privé, domaines same-site et secrets gérés | bonne; volumes médias à traiter |
| **B — Hostinger VPS unique + reverse proxy (recommandée, offre à confirmer)** | architecture directe, origine unique | VPS + domaine + sauvegardes à confirmer | plus forte, OS/patchs à gérer soi-même | dépend du durcissement; DB non exposée, pare-feu et TLS impératifs | bonne avec artefacts versionnés et procédure |
| C — conteneurs | actuellement non prête | variable | orchestration à maintenir | bonne si images/config durcies | potentiellement forte |

La cible B est retenue comme recommandation pour le MVP/jury car l’application a backend Java, PostgreSQL, WebSocket et stockage média local; elle correspond aux possibilités VPS décrites par Hostinger. L’offre active, ressources, domaine, certificat et budget restent à confirmer dans le compte Hostinger. Docker/Compose, Nginx et systemd ne sont pas présents comme configurations projet.

## Pré-requis et décision de blocage DB

1. Dans le compte Hostinger, confirmer l’offre réelle. Pour héberger Spring Boot + PostgreSQL + WebSocket sur Hostinger, confirmer un VPS avec ressources suffisantes; relever région, accès SSH/root, stockage/sauvegardes disponibles, responsable d’exploitation et budget. Confirmer/réserver domaine et DNS.
2. Préparer une machine Linux supportée, accès SSH par clé, comptes nominatifs, mises à jour de sécurité, pare-feu entrant limité à SSH administré et ports 80/443.
3. Installer une version PostgreSQL supportée; la dernière preuve de test locale RNCP indique PostgreSQL 16.15, pas la version d’un futur serveur.
4. Créer DB et rôle d’application non superuser; limiter les connexions à localhost/réseau privé; n’utiliser TLS si connexion distante.
5. **Bloquant avant mise en service** : aucune chaîne de migrations de schéma complète Flyway/Liquibase n’est actuellement configurée. `ddl-auto=validate` en prod ne crée pas les tables. Le script `docs/rncp/sql/001_schema_reference.sql` est documentaire et la migration `V1__unique_transaction_listing.sql` est manuelle, pas un baseline complet. Il faut établir/revoir le schéma existant et une procédure de migration/sauvegarde avant le premier déploiement; ne pas lancer Hibernate `update` sur la base prod.
6. Créer un répertoire de service dédié, par exemple `/srv/watyouface`, propriétaire du compte applicatif, permissions minimales; `media/` doit être persistant et sauvegardé.
7. Configurer TLS Nginx avec certificat valide et renouvellement automatique. Le proxy doit supporter `Upgrade`/`Connection` pour WebSocket et les requêtes upload jusqu’à la taille prévue.

## Variables et configuration

Charger les variables backend par un fichier protégé hors dépôt ou un secret manager, lisible seulement par le service. Référence: [97_VARIABLES_ENVIRONNEMENT.md](97_VARIABLES_ENVIRONNEMENT.md). Profil obligatoire `prod`; configurer `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (>=32 octets), `CORS_ALLOWED_ORIGINS`, `AUTH_COOKIE_SECURE=true`, `AUTH_COOKIE_SAME_SITE=Lax`, `HSTS_ENABLED=true` uniquement après validation HTTPS et `SERVER_PORT=9090` ou autre port interne.

Dans le proxy same-origin, configurer `VITE_API_BASE=https://<domaine>` et `VITE_WS_URL=https://<domaine>/ws` avant le build frontend. Aucune variable `VITE_*` ne doit être secrète.

Activer mail uniquement après configuration des `MAIL_*` via secrets. Le profil peut fonctionner sans SMTP lorsque l’envoi n’est pas requis.

## Base de données / sauvegarde

- sauvegarde chiffrée quotidienne de la base et des médias, hors du VPS si possible; rétention définie par le porteur.
- tester une restauration sur une base isolée avant toute mise en service et après évolution majeure.
- réserver les droits DDL au compte migration; rôle applicatif limité aux opérations nécessaires.
- conserver journaux et sauvegardes hors des chemins servis publiquement.
- ne pas consigner de credentials dans shell history, journaux ou rapport.

## Build reproductible

### Backend

Depuis le dépôt backend et un JDK 17 :

```sh
./mvnw clean package
```

Artefact observé le 10/10/2026 : `target/watyouface-0.0.1-SNAPSHOT.jar`, environ 69 MB; package réussi. Le run inclut 65 tests rapportés, 60 réussis, 0 échec/erreur et 5 tests PostgreSQL conditionnels ignorés faute d’environnement de test configuré. Ce build ne valide donc pas PostgreSQL courant ni un serveur déployé.

### Frontend

Avec Node/npm compatibles avec le lockfile :

```sh
npm ci
VITE_API_BASE=https://<domaine> VITE_WS_URL=https://<domaine>/ws npm run build
```

Artefact observé le 10/10/2026 : `dist/`, build Vite PASS, JS 441.52 kB (137.75 kB gzip), CSS 43.10 kB (8.40 kB gzip), HTML 0.47 kB. La configuration API/WS est publique et figée au build.

## Mise en service (cible)

1. Transférer le JAR versionné et le dossier `dist/` par canal SSH contrôlé; vérifier checksum et provenance du commit.
2. Déployer le frontend dans le répertoire statique Nginx, conserver l’ancienne release pour rollback.
3. Avant démarrage Java, préparer/restaurer la base et vérifier que le schéma complet existe; appliquer les changements DB uniquement via procédure relue et sauvegardée.
4. Démarrer Spring sous un utilisateur sans privilèges, profil `prod`, port loopback `9090`, environnement protégé, `WorkingDirectory` dédié. Le service systemd est à créer et tester; aucun fichier systemd projet n’existe.
5. Configurer Nginx: static SPA fallback, proxy `/api/` et `/media/`; proxy `/ws` et chemins SockJS avec les headers HTTP Upgrade; taille upload au moins cohérente avec 255 MB request max ou limite produit ajustée avant déploiement.
6. DNS puis TLS; activer HSTS après vérification de l’ensemble du domaine/sous-domaines. CORS exact, cookie auth Secure/HttpOnly/SameSite, CSRF cookie séparé; aucun wildcard.
7. Vérifier service, endpoint de santé lorsqu’il sera défini, routes publiques/privées, authentification, médias, chat et métriques système avant ouverture utilisateur.

## HTTPS, cookies, CORS et WebSocket

- HTTPS public obligatoire; HTTP public redirigé vers HTTPS.
- Profil prod donne `Secure=true`, `HttpOnly` sur cookie d’auth et `SameSite=Lax` par défaut. Le cookie CSRF est distinct et lisible par JS pour renvoyer le header CSRF; ne pas retirer CSRF.
- Origine autorisée exacte; aucune origine `*` avec credentials. Même origine reverse-proxy recommandée.
- navigateur utilise `/ws` en HTTPS; l’upgrade transport doit devenir `wss://`; Nginx doit transmettre `Upgrade` et `Connection`. Origine handshake est allowlistée et membership STOMP contrôlé côté serveur.
- Ne pas déclarer la politique correctement déployée avant test dans le navigateur et vérification des headers TLS.

## Logs et supervision minimale

Service : logs Spring stdout/journald avec rotation/rétention; collecter démarrages, erreurs d’auth (sans identifiants sensibles), erreurs DB et erreurs métier sans payload secret. Nginx access/error logs avec politique de rétention; masquer query strings sensibles si ajout futur. Interdiction de logger password, JWT, cookie ou secrets.

Superviser au minimum espace disque (médias/logs), mémoire, CPU, disponibilité HTTP, erreurs 5xx, état PostgreSQL, expiration TLS et dernière sauvegarde; alerte humaine documentée. Aucun outil de monitoring/health-check dédié n’est actuellement configuré dans les dépôts.

## Rollback

1. arrêter le trafic ou revenir au build frontend précédent via lien de release Nginx;
2. redéployer le JAR précédent et redémarrer le service;
3. vérifier logs puis smoke tests sans modifier les données;
4. si migration DB incompatible, restaurer snapshot/sauvegarde validée sur une instance isolée ou appliquer un rollback DB spécifiquement conçu; ne pas supposer qu’un SQL inverse existe;
5. restaurer également médias depuis sauvegarde si nécessaire et vérifier quelques URLs;
6. remettre le trafic seulement après validation de login, API, cookie/CSRF et état de données.

## État réel

Déploiement réel : **NON**. Hostinger est l’hébergeur cible déclaré, mais l’offre souscrite n’est pas confirmée; aucun accès compte, DNS, VPS, certificat HTTPS, proxy, service, base production, pipeline de release ou endpoint health n’a été constaté. Cette procédure décrit une cible conditionnée à un VPS Hostinger; la base sans baseline/migrations et les médias publics locaux sont des limites à traiter avant une ouverture réelle.
