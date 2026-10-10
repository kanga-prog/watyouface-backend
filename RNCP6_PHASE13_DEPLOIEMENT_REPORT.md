# Phase 13 — Déploiement

## 1. Contexte

Objectif: préparer un déploiement reproductible sans publier l’application aveuglément. Audit mené le 10/10/2026 sur les deux dépôts. Aucun déploiement réel n’a été tenté.

## 2. État initial et traces existantes

Au début de l’audit, backend et frontend étaient sur `main`, alignés avec `origin/main`. Le backend est ensuite passé sur sa branche d’audit Marketplace puis sur `rncp6/deployment` pour cette documentation. Le frontend conserve le changement local préexistant `updateAvatars.js`, laissé intact et non inclus. Aucun Dockerfile/Compose, Nginx/Apache, systemd, PM2, Procfile ou workflow de déploiement n’est suivi. L’hébergeur cible déclaré est Hostinger; l’offre souscrite, les domaines et le serveur restent à confirmer.

Profils backend observés: `dev` (H2 mémoire par défaut, port 8081, console H2/SQL visibles, cookie non Secure et fallback Bearer permis: développement uniquement); `local` (PostgreSQL, port 8080, validation de schéma); `prod` (PostgreSQL obligatoire, `ddl-auto=validate`, erreurs sans message interne, cookie Secure, fallback Bearer désactivé, port 9090 par défaut). Aucun endpoint health spécifique de production n’a été identifié.

## 3. Architecture cible Hostinger

Utilisateur → HTTPS → Nginx (statique React/Vite + reverse proxy) → Spring Boot → PostgreSQL et répertoire `media/` persistant. Nginx route `/api`, `/media` et `/ws`; le backend reste sur loopback `9090`; PostgreSQL n’est pas exposé publiquement. WebSocket/SockJS traverse TLS et utilise l’upgrade WSS. Cookies d’auth HttpOnly/Secure; cookie CSRF séparé; allowlist CORS explicite.

Architecture conditionnelle à un VPS Hostinger. Une origine publique unique est recommandée pour réduire la complexité cookie/CSRF/CORS. Hostinger documente Java comme technologie nécessitant l’accès root/VPS et PostgreSQL parmi les technologies gérables sur VPS. Ses offres Web/Cloud Node.js listent React/Vite au frontend mais des backends JavaScript; un plan Web/Cloud seul n’est donc pas validé pour Spring Boot. Le titulaire doit confirmer l’offre dans hPanel. Références : [langages/frameworks Hostinger](https://www.hostinger.com/support/which-programming-languages-and-frameworks-are-supported-at-hostinger/), [VPS auto-géré](https://www.hostinger.com/support/8852150-what-is-a-self-managed-vps-at-hostinger/), [Web Apps Node.js](https://www.hostinger.com/support/how-to-deploy-a-nodejs-website-in-hostinger/). Diagramme : `docs/rncp/evidence/deployment/deployment-architecture.txt`.

## 4. Environnements

`LOCAL`: profil `local` avec PostgreSQL locale (ou `dev` H2 pour développement isolé), frontend Vite localhost; secrets locaux externes. `DEVELOPPEMENT`: données non réelles, H2 ou PostgreSQL dédiée, ports locaux, jamais de secret partagé. `STAGING`: à provisionner sur Hostinger après confirmation de l’offre, avec PostgreSQL isolée, HTTPS, domaine de test et secrets distincts. `PRODUCTION`: cible Hostinger à confirmer (VPS recommandé pour héberger Spring/DB); profil `prod`, PostgreSQL persistante et privée, HTTPS obligatoire, cookie Secure, origine CORS exacte, sauvegardes/restauration testées.

Les URLs staging/production et le domaine Hostinger ne sont pas connus; ce sont des placeholders, pas des endpoints existants. Variables recensées dans `docs/rncp/97_VARIABLES_ENVIRONNEMENT.md`.

## 5. Stratégie retenue

Comparaison A (services gérés séparés), B (Hostinger VPS unique + reverse proxy), C (containers). B est recommandé sous réserve d’avoir réellement un VPS Hostinger; origine unique, chemins `/media` et proxy WebSocket explicite; contrepartie: patchs OS, sauvegardes, supervision, TLS et administration. Docker/Compose n’existe pas dans le dépôt. Il s’agit d’une proposition documentée; aucun VPS n’est confirmé/réservé.

## 6. Builds

`./mvnw clean package`: PASS, JAR exécutable `target/watyouface-0.0.1-SNAPSHOT.jar`, 69 MB. 65 tests comptabilisés: 60 réussis, 0 échec/erreur, 5 PostgreSQL conditionnels ignorés. `npm ci`: preuve antérieure PASS; `npm audit --omit=dev` rejoué: 0 vulnérabilité. Build Vite 7.3.7 PASS, `dist/`, 1900 modules, JS 441.57 kB (137.78 gzip), CSS 43.10 kB (8.40 gzip), HTML 0.47 kB.

Un build de contrôle supplémentaire a fourni `VITE_API_BASE=https://api.example.invalid` et `VITE_WS_URL=https://api.example.invalid/ws`; Vite PASS et `localhost:8080` est absent du bundle. `.invalid` est une valeur réservée de test, pas une URL Hostinger déployable. Le domaine réel étant inconnu, un build production réel reste à faire avec les deux variables; aucune ne doit contenir de secret.

## 7. Sécurité de configuration

`application-prod.properties` exige DB_URL/DB_USERNAME/DB_PASSWORD, utilise `ddl-auto=validate`, définit Secure cookie à true et désactive Bearer fallback. HSTS est opt-in (`HSTS_ENABLED`) et doit être activé seulement après vérification TLS. CORS prod a une allowlist vide par défaut: la variable `CORS_ALLOWED_ORIGINS` doit être définie à l’origine HTTPS exacte. Cookie SameSite vaut Lax par défaut; le frontend/API cible same-origin. CSRF reste actif; ne pas le désactiver pour le proxy.

Les variables sont documentées sans valeurs. Secrets via fichier de service protégé/secret manager; aucun secret dans Git ni VITE_*.

## 8. PostgreSQL et migrations

Une preuve RNCP antérieure constate PostgreSQL local 16.15 et 5/5 tests de paiement; ce n’est pas une version de production décidée. Dans la vérification Phase 13, Maven a ignoré 5 tests PostgreSQL conditionnels. Aucun mécanisme Flyway/Liquibase complet n’est configuré. Le profil prod valide le schéma, ne le crée pas. La migration unique sur transaction/listing est manuelle et le SQL de référence n’est pas un baseline opérationnel complet. **Blocage à résoudre avant vrai déploiement:** établir, relire, tester et sauvegarder le schéma initial puis définir une stratégie de migration reproductible. Sauvegarde quotidienne chiffrée, copie hors serveur et exercice de restauration sont des exigences cibles, non des opérations constatées.

## 9. Médias / uploads

Stockage actuel disque local `${user.dir}/media`, servi publiquement par `/media/**`; l’application ne lit pas `UPLOAD_DIR`. Taille multipart globale: 205 MB par fichier et 255 MB par requête; règles métier observées: avatar 5 MB, listing image 10 MB, image de post 20 MB, vidéo de post 200 MB. Les types autorisés sont définis par services applicatifs. Un VPS doit donner au service un WorkingDirectory stable et un répertoire media persistant, avec permissions minimales, espace disque surveillé et sauvegarde. Les médias sont publics: aucun déploiement ne doit considérer ce stockage comme privé ou scalable; antivirus/quarantaine et stockage objet restent des évolutions.

## 10. Procédure, rollback et logs

La procédure pas-à-pas et la stratégie de rollback se trouvent dans `docs/rncp/98_PROCEDURE_DEPLOIEMENT.md`. La checklist exécutable est `docs/rncp/99_CHECKLIST_POST_DEPLOIEMENT.md`. Nginx/systemd/TLS et supervision sont décrits comme cibles, jamais comme installés. Logs Spring/Nginx protégés, rétention, absence de tokens/passwords et supervision minimale sont prescrits.

## 11. Preuves et limites

Les preuves de build et diagramme sont dans `docs/rncp/evidence/deployment/`. La checklist post-déploiement reste non exécutée; aucune capture/URL live n’est ajoutée. Les limites majeures: offre Hostinger et domaine non confirmés, pas d’infrastructure versionnée, pas de migration de schéma complète, pas de média externe, pas de smoke test HTTPS/WebSocket/cookie dans un environnement hébergé, pas de backup/restore réalisé. L’audit Marketplace est consigné dans `docs/rncp/100_RECETTE_MARKETPLACE_COMPLETE.md` et `101_MATRICE_VALIDATION_MARKETPLACE.md`: plusieurs parcours API/H2 PASS, mais PostgreSQL courant, recette UI et E2E navigateur restent absents. Aucune mise en production ne doit précéder cette validation.

## 12. Mapping RNCP

| Compétence | Preuve | Portée |
|---|---|---|
| C6 | architecture et flux dans ce rapport/procédure | architecture cible documentée, pas déployée |
| C7 | profil PostgreSQL et risques de migration | preuve de conception; schema bootstrap reste nécessaire |
| C10 | `97`, `98`, `99` | configuration/procédure/checklist reproductibles à valider sur cible |
| C11 | rollback, logs, sécurité et supervision cible | préparation DevOps; aucune chaîne CI/CD ou production prouvée |

## 13. Conclusion

**Déploiement réel: NON. Phase 13 documentaire: préparation Hostinger mise à jour, builds locaux réussis. Phase 13 clôturable: NON.** Blocages: offre Hostinger/domaine à confirmer; baseline/migrations PostgreSQL complètes absentes; Marketplace non recetté sur PostgreSQL courant ni dans un navigateur; aucune infrastructure staging, TLS, Nginx/systemd ou smoke test réel. La Phase 14 n’est pas déclarée prête avant levée de ces prérequis.
