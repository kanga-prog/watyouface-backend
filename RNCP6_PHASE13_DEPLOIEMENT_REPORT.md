# Phase 13 — Déploiement

## 1. Contexte

Objectif: préparer un déploiement reproductible sans publier l’application aveuglément. Audit mené le 10/10/2026 sur les deux dépôts. Aucun déploiement réel n’a été tenté.

## 2. État initial et traces existantes

Backend et frontend étaient sur `main`, à jour avec `origin/main`. Backend worktree propre. Frontend contient le changement local préexistant `updateAvatars.js`, laissé intact et non inclus. Aucun Dockerfile/Compose, Nginx/Apache, systemd, PM2, Procfile, manifeste Render/Railway/Vercel/Netlify ou workflow GitHub Actions de déploiement n’est suivi. Aucun fournisseur, domaine ou serveur hébergé n’est documenté.

Profils backend observés: `dev` (H2 mémoire par défaut, port 8081, console H2/SQL visibles, cookie non Secure et fallback Bearer permis: développement uniquement); `local` (PostgreSQL, port 8080, validation de schéma); `prod` (PostgreSQL obligatoire, `ddl-auto=validate`, erreurs sans message interne, cookie Secure, fallback Bearer désactivé, port 9090 par défaut). Aucun endpoint health spécifique de production n’a été identifié.

## 3. Architecture cible

Utilisateur → HTTPS → Nginx (statique React/Vite + reverse proxy) → Spring Boot → PostgreSQL et répertoire `media/` persistant. Nginx route `/api`, `/media` et `/ws`; le backend reste sur loopback `9090`; PostgreSQL n’est pas exposé publiquement. WebSocket/SockJS traverse TLS et utilise l’upgrade WSS. Cookies d’auth HttpOnly/Secure; cookie CSRF séparé; allowlist CORS explicite.

Les noms de domaine restent à choisir. Une origine publique unique est recommandée pour réduire la complexité cookie/CSRF/CORS. Diagramme détaillé: `docs/rncp/evidence/deployment/deployment-architecture.txt`.

## 4. Environnements

`LOCAL`: profil `local` avec PostgreSQL local (ou `dev` H2 pour développement isolé), frontend Vite localhost; secrets locaux externes. `DEVELOPPEMENT`: données non réelles, H2 ou PostgreSQL dédiée, ports locaux, jamais de secret partagé. `STAGING`: environnement à provisionner avec PostgreSQL isolée, HTTPS, domaines de test et secrets distincts. `PRODUCTION`: profil `prod`, PostgreSQL persistante et privée, HTTPS obligatoire, cookie Secure, origine CORS exacte, sauvegardes/restauration testées.

Les URLs staging/production ne sont pas connues; elles sont des placeholders, pas des endpoints existants. Variables recensées dans `docs/rncp/97_VARIABLES_ENVIRONNEMENT.md`.

## 5. Stratégie retenue

Comparaison A (services gérés séparés), B (VPS unique + reverse proxy), C (containers). La cible choisie pour le MVP est B: origine unique, contrôle des chemins `/media` actuels et proxy WebSocket explicite; contrepartie: patchs OS, sauvegardes, supervision, TLS et accès au VPS à administrer. Docker/Compose n’existe pas dans le dépôt et n’est pas introduit ici. Ce choix est une proposition documentée; aucun VPS ou fournisseur n’est réservé.

## 6. Builds

`./mvnw clean package`: PASS, JAR exécutable `target/watyouface-0.0.1-SNAPSHOT.jar`, environ 69 MB. 65 tests rapportés: 60 passés, 0 failure/error, 5 tests PostgreSQL ignorés car l’environnement DB n’était pas configuré pour cette commande. `npm ci`: PASS; 373 packages installés, audit de l’installation sans vulnérabilité rapportée. `npm run build`: PASS; Vite 7.3.7, dist produit, JS 441.52 kB (137.75 gzip), CSS 43.10 kB (8.40 gzip), HTML 0.47 kB. Détails: `docs/rncp/evidence/deployment/*-build-summary.txt`.

Le build frontend de preuve a utilisé les valeurs locales par défaut (`localhost:8080`), et **n’est pas** un bundle de production. Un build de déploiement doit recevoir `VITE_API_BASE` et `VITE_WS_URL` avant compilation; aucune de ces variables ne doit contenir de secret.

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

Les preuves de build et diagramme sont dans `docs/rncp/evidence/deployment/`. La checklist post-déploiement reste non exécutée; aucune capture/URL live n’est ajoutée. Les limites majeures: domaine/hébergeur non choisis, pas d’infrastructure versionnée, pas de migration de schéma complète, pas de média externe, pas de smoke test HTTPS/WebSocket/cookie dans un environnement hébergé, pas de backup/restore réalisé.

## 12. Mapping RNCP

| Compétence | Preuve | Portée |
|---|---|---|
| C6 | architecture et flux dans ce rapport/procédure | architecture cible documentée, pas déployée |
| C7 | profil PostgreSQL et risques de migration | preuve de conception; schema bootstrap reste nécessaire |
| C10 | `97`, `98`, `99` | configuration/procédure/checklist reproductibles à valider sur cible |
| C11 | rollback, logs, sécurité et supervision cible | préparation DevOps; aucune chaîne CI/CD ou production prouvée |

## 13. Conclusion

**Déploiement réel: NON. Phase 13 documentaire: livrables créés et builds locaux réussis. Phase 13 entièrement clôturable: NON**, car le schéma initial/migrations restent un prérequis de mise en service et la procédure n’a pas été répétée sur une infrastructure staging. La prochaine phase proposée est Phase 14 DevOps/CI-CD, en gardant les prérequis de base et de déploiement identifiés.
