# Dockerisation backend Spring Boot pour Railway

## Objectif

Ce document décrit le conteneur de production du backend WatYouFace pour un
déploiement Railway. Il couvre la préparation au déploiement (RNCP C10) et les
pratiques DevOps de mise en production (RNCP C11), sans changer la logique
métier ni intégrer de secret au dépôt.

## Architecture cible

```text
GitHub
  ↓
Docker build
  ↓
Railway
  ↓
Spring Boot
  ↓
PostgreSQL
```

Le conteneur expose l'application Spring Boot et Railway fournit le port
public. PostgreSQL est un service externe : il n'est pas inclus dans l'image.
Le backend conserve WebSocket/STOMP (`/ws`, préfixe applicatif `/app`, broker
`/topic` et `/queue`) ; le proxy Railway doit donc accepter les WebSockets.

## Image et build

Le `Dockerfile` est multi-stage :

1. l'étape `build` utilise Eclipse Temurin JDK 17 et le Maven Wrapper présent
   dans le dépôt ;
2. les dépendances Maven sont résolues dans une couche dédiée, réutilisable par
   Docker lorsque `pom.xml` ne change pas ;
3. l'étape finale utilise Eclipse Temurin JRE 17 et contient uniquement le JAR
   exécutable `watyouface-0.0.1-SNAPSHOT.jar` ; son entrypoint initialise le
   volume media puis exécute Java avec l'utilisateur non-root `spring`.

Le contexte Docker exclut notamment Git, `target`, données media locales,
logs, fichiers `.env`, secrets et documentation. Le wrapper (`mvnw` et
`.mvn/`), le `pom.xml` et `src/` restent dans le contexte de build.

Construire l'image localement :

```bash
docker build -t watyouface-backend:rncp6 .
```

Exécuter d'abord la suite Maven hors conteneur :

```bash
./mvnw clean test
```

## Port et profils Spring

Le profil `local` garde le port `8080`. En production,
`application-prod.properties` utilise `server.port=${PORT:8080}` :

- en local, sans variable `PORT`, l'application écoute sur `8080` ;
- sur Railway, la variable `PORT` fournie par la plateforme est utilisée.

L'image définit `SPRING_PROFILES_ACTIVE=prod` par défaut. Cette valeur peut
être surchargée par une variable Railway ou une option `docker run -e`.

## Variables Railway

Configurer ces variables dans le tableau de bord Railway, jamais dans un
fichier commité ou dans le `Dockerfile` :

| Variable | Requise | Usage |
|---|---:|---|
| `DB_URL` | Oui | URL JDBC PostgreSQL fournie par le service de base de données. |
| `DB_USERNAME` | Oui | Compte de connexion PostgreSQL. |
| `DB_PASSWORD` | Oui | Mot de passe PostgreSQL. |
| `JWT_SECRET` | Oui | Secret JWT d'au moins 32 caractères ; l'application échoue rapidement s'il manque. |
| `SPRING_PROFILES_ACTIVE` | Oui | Utiliser `prod` en production. |
| `PORT` | Railway | Port injecté par Railway ; ne pas le fixer manuellement. |
| `CORS_ALLOWED_ORIGINS` | Recommandée | Origine HTTPS exacte du frontend déployé. |
| `AUTH_COOKIE_SECURE` | Recommandée | Laisser à `true` en HTTPS de production. |

Les variables de messagerie (`MAIL_*`) restent facultatives selon le besoin
fonctionnel. Aucune valeur sensible n'est reproduite dans ce document.

Exemple de lancement local : les valeurs sont lues depuis l'environnement
local et ne sont pas écrites dans la commande ni l'image.

```bash
docker run --rm \
  --add-host=host.docker.internal:host-gateway \
  -p 8080:8080 \
  -e DB_URL \
  -e DB_USERNAME \
  -e DB_PASSWORD \
  -e JWT_SECRET \
  -e SPRING_PROFILES_ACTIVE=local \
  watyouface-backend:rncp6
```

Avant cette commande, exporter des valeurs de test dans le shell local. Sous
macOS ou Windows, l'option `--add-host` n'est généralement pas nécessaire ;
sous Linux/WSL, elle rend `host.docker.internal` disponible. Le test
PostgreSQL n'est valide que si la base autorise réellement cette connexion.

## Stockage media

Le backend écrit actuellement les médias sous le chemin relatif `media/` et
les sert via `/media/**`. Dans l'image, le répertoire de travail est `/app` :
le chemin runtime correspondant est donc `/app/media`.

Railway fournit un système de fichiers éphémère par défaut : sans volume, les
avatars et uploads seront perdus lors d'un redéploiement ou remplacement de
conteneur. Pour les conserver, attacher un volume Railway monté sur
`/app/media`.

### Initialisation des permissions du volume

Un volume Railway peut masquer le répertoire créé à l'image et arriver monté
avec une propriété root. L'incident constaté était
`AccessDeniedException: /app/media/avatars` alors que les logs indiquaient un
processus démarré par `spring`. Définir `RAILWAY_RUN_UID=0` seul ne corrige pas
un `USER spring` présent dans l'image : le processus ne peut alors pas
réattribuer le montage.

`docker-entrypoint.sh` est donc l'unique étape lancée initialement comme root.
Il vérifie ce prérequis, crée si besoin `/app/media`, puis applique
`chown -R spring:spring /app/media`. Il exécute ensuite
`setpriv --reuid=spring --regid=spring --init-groups` avec la commande Java.
`setpriv` est fourni par `util-linux` dans l'image Temurin Ubuntu et sa
présence est vérifiée pendant le build. Il n'y a ni `chmod 777`, ni exécution
persistante de Spring Boot comme root, ni changement de propriétaire hors du
point de montage media.

Sur un volume qui interdit `chown`, le démarrage échoue explicitement plutôt
que d'élargir les permissions. La validation Railway production a confirmé que
le volume existant accepte cette initialisation : `/app/media` est
`spring:spring`, Java s'exécute sous `spring` UID 999 et le fichier de contrôle
media est encore présent après redeploy. Cette preuve est conservée dans
`evidence/backend/BE-DEP-03-railway-media-volume-permissions.txt`.

Le chemin applicatif n'est pas rendu configurable dans cette phase, car il est
actuellement codé dans les composants media ; le modifier exigerait une
décision d'architecture et des tests dédiés. Une évolution ultérieure pourra
introduire une variable de chemin avec migration contrôlée vers un stockage
objet ou un volume persistant.

## Vérification de déploiement

Après le build et le lancement, vérifier au minimum :

1. les logs Spring Boot indiquent le profil et le port attendus, sans afficher
   de secrets ;
2. un endpoint HTTP public répond via l'URL Railway ;
3. la connexion PostgreSQL fonctionne avec les variables Railway ;
4. le handshake WebSocket `/ws` fonctionne derrière le proxy ;
5. un upload survit à un redéploiement lorsque le volume `/app/media` est
   attaché.

## CI GitHub Actions et CD Railway

Le workflow versionné `.github/workflows/backend-ci.yml` s'exécute à chaque
événement `push` et `pull_request`. Il utilise `actions/checkout@v4` sans
persistance de credential, Java Temurin 17 avec cache Maven, puis exécute
`./mvnw clean test` et `docker build -t watyouface-backend:ci .`. Une erreur de
test ou de build fait échouer le job. Ses permissions sont limitées à
`contents: read`; aucun secret GitHub, JWT ni accès PostgreSQL n'est requis.

La première preuve GitHub Actions est le run pull request
[#38550729055](https://github.com/kanga-prog/watyouface-backend/actions/runs/38550729055),
exécuté le 14/10/2026 pour le commit `01a8919` : checkout, Java 17, tests Maven
et build Docker sont `success`.

Le workflow ne contient volontairement aucun job de déploiement Railway, aucun
token Railway et aucun secret de production. Le CD est donc **MANUEL / PARTIEL**
dans l'état prouvé : après validation et merge vers une branche de production
contrôlée (cible recommandée : `main`), un déploiement Railway doit être lancé
par la configuration Railway de ce service ou son tableau de bord. La présence,
la branche surveillée et l'automatisation de cette intégration Railway ne sont
pas déductibles du dépôt et ne sont pas déclarées actives sans preuve dédiée.

## Sécurité et limites

- L'image runtime utilise un JRE plutôt qu'un JDK. L'entrypoint utilise root
  seulement pour initialiser `/app/media`; le processus Java est ensuite
  explicitement exécuté sous `spring` (principe du moindre privilège).
- Les secrets sont exclusivement injectés par l'environnement de déploiement.
- `.dockerignore` réduit le risque d'envoyer des données locales au démon
  Docker.
- Le Dockerfile compile le JAR avec `-DskipTests` ; la suite
  `./mvnw clean test` doit être exécutée explicitement avant de publier une
  image.
- Le correctif de propriété et la persistance media après redeploy sont validés
  sur le volume Railway réel. La configuration CORS/HTTPS/WebSocket, la
  sauvegarde/restauration, le rollback et l'observabilité restent à compléter.

## Couverture RNCP

- **C10 — PARTIEL renforcé :** Dockerfile reproductible, variables, port,
  migration, stockage et procédure d'initialisation du volume sont documentés.
  Le volume Railway réel, le Java non-root et la persistance media après
  redeploy sont prouvés. La sauvegarde/restauration et le rollback restent à
  prouver.
- **C11 — PARTIEL renforcé :** build multi-stage, baisse contrôlée de
  privilèges, configuration externalisée, déploiement Railway et CI GitHub
  Actions verte sont prouvés. Le CD Railway automatique, l'observabilité et
  les procédures de rollback ne sont pas encore des preuves disponibles.
