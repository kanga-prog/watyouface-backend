# WatYouFace — Documentation technique et projet finale

Date de consolidation : 18/10/2026. Cette synthèse destinée au jury RNCP CDA
niveau 6 décrit uniquement l'état démontré. Elle ne remplace ni le code
versionné ni les preuves d'exécution.

## 1. Présentation de WatYouFace

WatYouFace réunit un feed social, une messagerie privée et une marketplace.
Le projet démontre une application multicouche, des règles métier
transactionnelles, des contrôles d'accès et une mise en production documentée.
Le wallet est une simulation Marketplace, jamais un service bancaire.

## 2. Objectif produit

Réunir des interactions sociales, conversationnelles et de vente dans une même
application, tout en conservant le backend comme autorité pour l'identité, les
droits, les transitions Marketplace et la persistance des données.

## 3. Architecture générale

```text
Navigateur → HTTPS → Frontend React / Vite → REST / WebSocket
          → Backend Spring Boot → Services métier → Repositories JPA → PostgreSQL
```

```text
GitHub → GitHub Actions → tests/build Vite → rsync SSH → Hostinger
Backend : dépôt/local → déploiement contrôlé manuel → Railway → Docker
         → Spring Boot → PostgreSQL Railway
```

Le backend Railway n'est pas livré par CD automatique : aucun job GitHub Actions
de déploiement backend n'est revendiqué.

## 4. Stack technique

| Couche | Technologies et responsabilité |
|---|---|
| Frontend | React, Vite, REST avec cookies, WebSocket/STOMP; écrans Feed, Chat, Marketplace et ADMIN. |
| Backend | Java 17, Spring Boot 3.3.5, Spring Security, JPA/Hibernate, Maven; controllers HTTP, services métier/transactions, repositories JPA. |
| Données | PostgreSQL Railway, Flyway V1–V3, contraintes relationnelles et transaction de paiement simulé. |
| Média | Volume Railway persistant `/app/media`, nom UUID et protections de chemin. |
| DevOps | Docker multi-stage, Java non-root `spring`, GitHub Actions, Hostinger par rsync SSH et Railway contrôlé. |

## 5. Architecture backend

Les controllers portent le contrat HTTP, les services portent les règles,
autorisations et transactions, et les repositories isolent JPA/PostgreSQL.
Flyway précède `Hibernate validate` en production. Le média est écrit sous le
volume persistant et le runtime Java reste non-root.

## 6. Architecture frontend

React/Vite fournit les écrans et validations de confort; il appelle le backend
avec cookies et CSRF. Les actions sensibles restent validées côté serveur. La
navigation couvre Feed, Chat, Marketplace, Profil et Dashboard ADMIN.

| Environnement | Usage démontré |
|---|---|
| Développement | Backend local H2 ou PostgreSQL selon profil; frontend Vite local. |
| Test | Maven, Vitest, build Vite, PostgreSQL dédié et CI GitHub Actions. |
| Production | [watyouface.com](https://watyouface.com) sur Hostinger; [api.watyouface.com](https://api.watyouface.com) sur Railway; PostgreSQL Railway et volume média. |

Les mots de passe, `JWT_SECRET`, identifiants PostgreSQL, clés SSH et secrets
GitHub/Railway sont externalisés et absents de cette documentation.

## 7. Modèle de données

Le modèle couvre utilisateurs, posts/commentaires/likes, conversations/messages,
annonces/transactions, contrats, vidéos et wallets. Flyway V1 crée le schéma;
V2 ajoute `listings.created_at` et V3 `listings.hidden`.

V2/V3 sont validées sur PostgreSQL 16 avec 37 annonces existantes : aucune donnée
supprimée et 0 `NULL` pour ces colonnes. Une migration partagée est immuable et
une correction passe par une migration suivante. PostgreSQL 18.6 est déclaré sur
Railway; l'avertissement observé avec Flyway 10.10.0 impose une revue/test avant
toute mise à niveau, sans être ignoré.

## 8. Authentification et autorisation

```text
register → rôle USER imposé
login    → cookie JWT HttpOnly
csrf     → XSRF-TOKEN + en-tête sur mutation
logout   → expiration du cookie
```

- En production, le cookie JWT est `Secure`, `HttpOnly` et `SameSite=Lax`.
- CORS a des origines explicites; les mutations REST sont protégées par CSRF.
- Le backend reste l'autorité : RBAC, ownership et BOLA/IDOR ne reposent pas sur
  la seule visibilité frontend.
- `/api/admin/**` exige ADMIN : 401 sans session, 403 USER, accès ADMIN; les
  mutations restent protégées par CSRF.

## 9. Feed

Le Feed permet contenus, commentaires et likes selon les règles d'ownership.
L'ADMIN peut supprimer un post ou commentaire de modération, sans modifier le
texte d'autrui.

## 10. Chat et WebSocket

Le chat combine REST et STOMP; l'appartenance est contrôlée côté serveur. Il
reste générique : aucune relation technique `Listing`–`Conversation` n'est
prétendue. Un E2E STOMP multi-utilisateur complet reste à réaliser.

## 11. Marketplace

```text
AVAILABLE → PENDING → ACCEPTED → PAID → SHIPPED → RECEIVED
                    └→ REFUSED
```

Le paiement est une simulation transactionnelle acheteur/vendeur; un solde
insuffisant retourne 409. L'ADMIN ne peut pas faire `request`, `accept`,
`refuse`, `pay`, `ship` ou `receive` à la place des deux parties.

## 12. Wallet de démonstration

Le seul crédit manuel est `POST /api/admin/users/{userId}/wallet/credit` : montant
strictement positif, transactionnel et réservé ADMIN. Aucun USER ne peut créditer
un wallet ou imposer un solde.

## 13. ADMIN et modération

Un seul ADMIN est utilisé : `demo`; changement de rôle retiré et dernier ADMIN
protégé. La modération conserve les rôles transactionnels des utilisateurs.

| Ressource | Politique ADMIN |
|---|---|
| Utilisateurs/wallets | Consulter, rechercher/paginer, visualiser solde et créditer le wallet démo; aucune donnée sensible exposée. |
| Feed | Consulter et supprimer contenu malveillant. |
| Listing `AVAILABLE`/`REFUSED` | Suppression physique. |
| Listing engagé (`PENDING` à `RECEIVED`) | `hidden=true`; tiers exclu, vendeur/acheteur/ADMIN conservent l'accès nécessaire. |

## 14. Uploads médias

| Règle | Politique réelle |
|---|---|
| Acceptés | JPEG/JPG, PNG, GIF et BMP; 10 Mo maximum. |
| Refusés | WebP, AVIF et HEIC/HEIF, faute de décodeur Java fiable embarqué. |
| Validation | MIME puis décodage ImageIO réel; vide, falsifié ou corrompu = HTTP 400, pas 500 générique. |
| Sortie | JPEG UUID; GIF sur première image; BMP normalisé JPEG. |
| Écriture | Temporaire, déplacement final après succès, nettoyage à l'échec; protection `MediaStorageService`. |

JPEG/PNG/GIF/BMP sont **fonctionnels validés en production**; leurs captures
visuelles réutilisables pour le jury sont **à ajouter**. L'avatar est un flux
distinct qui reste à harmoniser avec cette politique.

## 15. Tests

| Sujet | Résultat réellement disponible |
|---|---|
| Backend | 104 tests rapportés, 0 failure, 0 error; 5 PostgreSQL conditionnels skipped hors profil. |
| PostgreSQL dédié | `PostgresPaymentIntegrationTests` : 5/5, 0 skipped/failure/error. |
| Frontend | 47/47 PASS; build Vite PASS; lint 0 erreur, 1 warning documenté. |

## 16. CI/CD

Le run backend 39165116209 a validé Maven et Docker. Le run frontend 39165084863
a validé tests, build, rsync Hostinger et smoke. Le CD est donc automatisé pour
le frontend seulement.

## 17. Déploiement

Railway/Flyway V2/V3 sont opérationnels; la recette indique `/api/users/me` sans
session à 401 et `/api/auth/csrf` à 200 avec XSRF-TOKEN. Le backend est déployé
manuellement de façon contrôlée; le frontend est déployé sur Hostinger par CI.

## 18. Sécurité

Les mesures comprennent JWT HttpOnly, CSRF, CORS explicite, RBAC, ownership/BOLA,
validation de contenu média, noms serveurs et Docker non-root. Le backend reste
l'autorité sur les permissions et les transitions métier.

## 19. Conformité

| Domaine | État |
|---|---|
| RGPD | PARTIAL/GAP : inventaire et minimisation documentés; registre/politique, rétention, effacement relationnel et médias orphelins à finaliser. |
| RGAA | PARTIAL : corrections ciblées labels, noms accessibles, alertes, alternatives et tableaux; aucun audit formel. |
| RGESN | PARTIAL : pagination/normalisation média et bundle mesuré; pas de Lighthouse/WebPageTest ni stratégie cache complète. |
| Backup/restore | GAP : procédure `pg_dump`/archive média documentée, aucune restauration testée. |
| Rollback | PARTIAL : redéploiement possible d'un commit connu; migrations Flyway non réversibles automatiquement; aucun rollback réel. |
| Observabilité | PARTIAL : logs Spring/Railway/CI et smoke; pas de métriques, alertes ou centralisation prouvées. |

## 20. Veille

La veille couvre Java/Spring, PostgreSQL/Flyway, React/Vite, OWASP API, uploads,
observabilité, sauvegarde et déploiement. Les décisions actuelles sont : garder
Java 17 avant soutenance, ne pas ajouter WebP sans décodeur testé, conserver
Railway manuel et tester toute évolution Flyway/PostgreSQL avant production.

## 21. Limites connues

Rate limiter mono-instance, JWT sans révocation centrale, absence d'AV/quarantaine
média, avatar distinct, formats WebP/AVIF/HEIC non supportés, CD backend manuel,
backup/restore non testés, observabilité limitée, conformité RGPD/RGAA/RGESN non
certifiée et warning Flyway/PostgreSQL 18 restent explicitement ouverts.

## 22. Pistes d'amélioration

Pistes post-soutenance : harmoniser avatar, stockage objet/antivirus, limiteur
distribué, révocation JWT, Actuator/Micrometer/alerting, E2E STOMP, audit RGAA,
politique RGPD et test de restauration/rollback contrôlé.

## 23. Preuves associées

- `RNCP6_SOURCE_DE_VERITE.md` : état courant et limites.
- `100_RECETTE_MARKETPLACE_COMPLETE.md` et `BE-E2E-01-production-recipe.txt` : recette E2E.
- `102_DOCKER_RAILWAY_BACKEND.md`, `103_MIGRATIONS_POSTGRESQL_PRODUCTION.md` : déploiement et migrations.
- `BE-ADMIN-01-rbac-wallet-moderation-media.txt` : RBAC, wallet, modération, médias.
- `15_CONFORMITE_QUALITE.md` / `BE-QUAL-01-conformite-qualite.txt` : qualité/conformité.
- `16_VEILLE_TECHNOLOGIQUE.md` / `BE-VEILLE-01-veille-technologique.txt` : veille.
- `23_MAPPING_C1_C11.md` et `24_PLAN_PREUVES.md` : compétences et preuves.
