# Plan de collecte des preuves RNCP — référence consolidée

> **État courant au 18/10/2026.** Les lignes suivantes sont le plan exploitable
> pour le jury. Les tableaux historiques conservés ensuite servent uniquement à
> tracer la préparation initiale et ne remplacent pas les résultats ci-dessous.

| ID | Catégorie | Preuve disponible | Verdict documentaire | Reste à ajouter |
|---|---|---|---|---|
| EV-01 | Pilotage | documents de cadrage, backlog, risques, Git et commits atomiques | PARTIAL | livrables jury finalisés |
| EV-06 | Données | V1–V3 Flyway; PostgreSQL 16, 37 listings existants, 0 NULL après V2/V3; tests paiement 5/5 | PASS pour le périmètre testé | restore/backup non testés |
| EV-07 | Tests applicatifs | Maven : 104 rapportés, 0 failure/error, 5 PostgreSQL conditionnels skipped; frontend : 47/47 | PASS | ne pas confondre avec le run PostgreSQL dédié |
| EV-09 | Déploiement backend | Docker Java 17, Java non-root, volume `/app/media` persistant et smoke Railway | PASS fonctionnel | rollback et restauration |
| EV-10 | CI backend | GitHub Actions run **39165116209** : Maven + Docker SUCCESS | PASS | pas de déploiement Railway dans le workflow |
| EV-14 | ADMIN/RBAC/wallet | `BE-ADMIN-01-rbac-wallet-moderation-media.txt` : 401/403, seul ADMIN, wallet transactionnel, modération | PASS tests | captures UI complémentaires |
| EV-15 | Uploads | JPEG/PNG/GIF/BMP : 10 Mo, MIME+décodage, sortie JPEG, temporaires nettoyés; formats modernes refusés | PASS tests / fonctionnel | preuve visuelle production |
| EV-16 | PostgreSQL dédié | `PostgresPaymentIntegrationTests` 5/5, 0 skipped/fail/error | PASS | pas une preuve de restauration |
| EV-17 | E2E production | `BE-E2E-01-production-recipe.txt` : admin, feed, cycles Marketplace, modération, smoke | PASS fonctionnel / PARTIAL documentaire | captures et traces HTTP archivables |
| EV-18 | Captures uploads | JPEG/PNG/GIF/BMP validés manuellement | **À AJOUTER** | captures de production sans données sensibles |
| EV-19 | Conformité/qualité | audit RGPD/RGAA/RGESN, risques, backup/rollback/observabilité | PARTIAL | audit formel, politique/registre, restore réel |
| EV-20 | Contrôles Phase 15 | 47/47 frontend, build; Maven et lint 0 erreur / 1 warning documenté | PASS | aucune certification RGAA déduite |
| EV-21 | Veille | veille datée, sources officielles et décisions documentées | PASS documentaire | aucune mise à niveau/scan implicite |
| EV-22 | CI/CD frontend | GitHub Actions run **39165084863** : tests, build, rsync Hostinger et smoke SUCCESS | PASS | mécanisme limité au frontend |
| EV-23 | Documentation finale | `17_DOCUMENTATION_FINALE.md` et les sources Dossier Projet/Professionnel/Présentation synchronisées. | PASS documentaire | régénérer les livrables DOCX/PPTX sur instruction dédiée |
| EV-24 | Index final des preuves | `18_PREUVES_RNCP_FINALES.md` et `evidence/backend/BE-RNCP-01-final-evidence-index.txt` : inventaire, matrice E2E, sécurité, DB, CI et gaps. | PASS documentaire | consolider les captures/traces listées dans `evidence/production/README.md` |
| EV-25 | Source Dossier Projet | `19_DOSSIER_PROJET_FINAL.md` : 40 sections, preuves sélectionnées, emplacements de captures et limites explicites. | PASS documentaire | générer DOCX/PDF sur instruction dédiée et compléter annexes visuelles |

## Règles d'utilisation des preuves

- Une capture complète un test ou une recette; elle ne les remplace pas.
- Une recette fonctionnelle sans trace/capture archivée est **PARTIAL** sur le plan documentaire.
- Les secrets, mots de passe, cookies, JWT, clés SSH, e-mails réels et identifiants d'infrastructure ne sont jamais intégrés aux preuves.
- Aucun résultat CI ne prouve un CD Railway automatique en l'absence de job de déploiement correspondant.
- `evidence/production/` est une structure d'accueil : aucun fichier visuel n'y
  est déclaré présent tant qu'il n'est pas versionné et vérifié.

## Historique de collecte et jalons initiaux

| ID | Preuve à collecter | Source / condition | Date cible | Usage dossier | Usage slide | Usage oral |
|---|---|---|---:|---|---|---|
| EV-01 | Historique Git, branches et commits atomiques | `git log`, PR/auto-revue | continu | gestion projet | timeline | méthode C4 |
| EV-02 | Wireframes/maquettes des trois piliers | outil de conception à choisir | 08–15/10 | interfaces | parcours | arbitrages UX |
| EV-03 | Captures feed : création, owner/tier refusé, commentaire/like | jeu d'essai fictif | 15/10 | fonctionnalités | feed | C2/C3 |
| EV-04 | Captures chat : conversation, message, refus participant tiers | jeu d'essai + trace STOMP | 15/10 | sécurité/chat | temps réel | BOLA chat |
| EV-05 | Captures marketplace cycle complet | annonce fictive avec états | 15/10 | règles métier | machine états | C3/C7/C8 |
| EV-06 | MCD/MLD/MPD et migrations | V1 Flyway sur PostgreSQL 16.15 vide : 14 tables, validate, tests 5/5 | 13/10 | données | modèle + schéma | C7, intégrité et reproductibilité |
| EV-07 | Tests backend/frontend et plan de tests | `BE-API-02` : JPEG/PNG, WebP/MIME/vide/taille/illisible ; correctif `e892ae8` sur `main`, CI backend SUCCESS et validation Railway réelle ; frontend aligné, 35 tests + build Vite PASS et pipeline Hostinger déclenché | 12–17/10 | qualité | tableau résultats + `BE-API-02` | C2/C3/C8/C9 |
| EV-08 | Avant/après sécurité | gaps + correctifs + tests | 12/10 | sécurité | risque/correction | raisonnement AppSec |
| EV-09 | Docker, lancement backend et volume media | image Java 17, entrypoint root limité, Java `spring` UID 999, volume Railway `spring:spring`, fichier media conservé après redeploy, smoke HTTP prod 401 + en-têtes | 13–14/10 | déploiement | schéma env. + preuves `BE-DATA-03` / `BE-DEP-03` | C10, C11 |
| EV-10 | Pipeline CI backend verte | Run GitHub Actions [#38943060289](https://github.com/kanga-prog/watyouface-backend/actions/runs/38943060289) : checkout, Java 17, Maven tests et Docker build SUCCESS pour le correctif Marketplace | 17/10 | DevOps | workflow + `BE-DEVOPS-01` + `BE-API-02` | C10/C11 |
| EV-11 | Procédure déploiement, backup/rollback | runbook validé | 14/10 | exploitation | étapes | C10 |
| EV-12 | RGPD/RGAA/éco-conception | checklist/revue et limites | 15/10 | conformité | checklist | responsabilité |
| EV-13 | Dossier/DP/slides et scénario | version finalisée | 16–17/10 | certification | support | soutenance |

## Règles de collecte

- Utiliser uniquement comptes et données fictifs.
- Conserver la date, commande, version/commit et verdict de chaque preuve.
- Masquer tokens, mots de passe, emails réels, chemins sensibles et informations d'infrastructure.
- Une capture ne remplace jamais un test versionné ; elle illustre le résultat pour le jury.
- Mettre à jour `RNCP6_SOURCE_DE_VERITE.md` quand une preuve change d'état.

## État Phase 13

Les preuves versionnées `evidence/backend/BE-DATA-03-flyway-railway-baseline.txt`, `evidence/backend/BE-DEP-03-railway-media-volume-permissions.txt` et `evidence/backend/BE-DEVOPS-01-github-actions-backend-ci.txt` conservent uniquement des résultats réels : build Docker PASS, suite Maven 79 tests/0 échec/5 ignorés, Flyway V1 appliquée, 14 tables, Hibernate validate, 5 tests PostgreSQL PASS, volume Railway `spring:spring`, Java UID 999, persistance media après redeploy, smoke HTTP prod 401 avec en-têtes de sécurité et CI GitHub Actions backend verte. Elles ne prouvent pas le CD Railway automatique, l'observabilité, le rollback ni la restauration de sauvegarde.

La preuve `evidence/backend/BE-API-02-marketplace-image-upload.txt` couvre 7 cas HTTP Marketplace : JPEG, PNG, WebP, MIME arbitraire, fichier vide, taille supérieure à 10 Mo et PNG illisible. Le correctif est commité sur `main` (`e892ae8`), poussé, validé par le run GitHub Actions [#38943060289](https://github.com/kanga-prog/watyouface-backend/actions/runs/38943060289) (Maven tests + Docker build SUCCESS), puis redéployé sur Railway. La validation de production constate JPEG/PNG PASS et WebP refusé proprement. Cela ne prouve pas le CD Railway automatique, l'observabilité, le rollback ni la restauration de sauvegarde.

## Ajout de preuve — lot ADMIN, wallet, modération et médias (18/10/2026)

| ID | Preuve réelle | Résultat / limites | Compétences |
|---|---|---|---|
| EV-14 | `evidence/backend/BE-ADMIN-01-rbac-wallet-moderation-media.txt` | Routes ADMIN : 401 sans session, 403 USER ; suppression du changement de rôle ; protection du seul ADMIN ; crédit wallet transactionnel exclusivement ADMIN ; modération Marketplace sans bypass transactionnel. | C3, C8, C9 |
| EV-15 | Tests image Feed + Marketplace avec fichiers réels | JPEG/PNG/GIF/BMP acceptés à 10 Mo ; MIME falsifié, vide, taille excessive et contenu corrompu refusés ; sortie JPEG, temporaire nettoyé. WebP/AVIF/HEIC non supportés volontairement. | C2, C3, C8, C9 |
| EV-16 | Validation PostgreSQL locale dédiée | `PostgresPaymentIntegrationTests` : 5/5, 0 failure/error/skip ; V2/V3 Flyway sur PostgreSQL 16 et 37 listings, aucune suppression, 0 NULL après migration. La suite globale garde 5 tests conditionnels ignorés hors profil PostgreSQL. | C7, C8, C9, C10 |

Ces preuves ne constituent pas une preuve de CD Railway automatique. La recette E2E navigateur a depuis été validée fonctionnellement ; ses captures et traces HTTP restent à centraliser dans la preuve EV-17.

## Preuve de recette production consolidée (18/10/2026)

| ID | Preuve réelle | Résultat / limites | Compétences |
|---|---|---|---|
| EV-17 | `evidence/backend/BE-E2E-01-production-recipe.txt` | Parcours ADMIN, Feed, Marketplace nominal/refusé, modération et smoke API validés fonctionnellement en production. Résultats sans capture/trace archivée = PARTIAL documentaire. | C2, C3, C8, C9, C10, C11 |
| EV-18 | Captures uploads production JPEG/PNG/GIF/BMP | **À AJOUTER** : les quatre formats sont fonctionnels validés manuellement ; aucune capture n'est inventée ni jointe à ce stade. | C2, C3, C8, C9, C10 |

Le run backend [39143755010](https://github.com/kanga-prog/watyouface-backend/actions/runs/39143755010) est SUCCESS (Maven tests, Docker build). Le run frontend [39143768559](https://github.com/kanga-prog/watyouface-frontend/actions/runs/39143768559) est SUCCESS (tests, build, déploiement Hostinger et smoke test). Ces résultats ne prouvent pas un CD Railway automatique.

## Phase 15 — conformité et qualité

| ID | Preuve | Résultat / limite | Compétences |
|---|---|---|---|
| EV-19 | `15_CONFORMITE_QUALITE.md` et `evidence/backend/BE-QUAL-01-conformite-qualite.txt` | Inventaire RGPD, accessibilité ciblée, éco-conception, qualité, risques, sauvegarde/rollback/observabilité. Audit non juridique et non certifiant ; restore/rollback non exécutés. | C2, C3, C4, C5, C6, C7, C8, C9, C10, C11 |
| EV-20 | Tests après corrections Phase 15 | Maven : 104 rapportés, 0 erreur/échec, 5 conditionnels PostgreSQL ignorés ; frontend : 47/47 et build PASS ; ESLint 0 erreur, 1 avertissement documenté. | C2, C3, C9 |

La prochaine preuve à produire avant une déclaration de conformité complète est un registre/politique RGPD validé, un audit RGAA formel sur échantillon et un test isolé de sauvegarde/restauration.

## Phase 16 — veille technologique

| ID | Preuve | Résultat / limite | Compétences |
|---|---|---|---|
| EV-21 | `16_VEILLE_TECHNOLOGIQUE.md` et `evidence/backend/BE-VEILLE-01-veille-technologique.txt` | Relevé daté des versions, sources officielles, décisions Java/Flyway/uploads/rate-limit/observabilité et méthode de veille. Aucun outil de scan, upgrade ou migration déclenché. | C4, C5, C6, C7, C8, C9, C10, C11 |

La Phase 16 est une preuve de démarche de veille et de décision, non une preuve de conformité, d'upgrade ou de correction de vulnérabilité.
