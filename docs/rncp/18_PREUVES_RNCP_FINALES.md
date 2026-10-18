# WatYouFace — Consolidation finale des preuves RNCP

Date : 18/10/2026. Cet index sert de dossier de preuves exploitable au jury.

- **PASS** : une pièce versionnée, un test exécuté ou une CI identifiable existe
  pour le périmètre formulé.
- **PARTIAL** : le comportement est validé, mais la pièce documentaire attendue
  (capture, trace, audit ou test complémentaire) manque encore.
- **GAP** : aucune démonstration suffisante n'est disponible.

Les preuves sont lues avec `RNCP6_SOURCE_DE_VERITE.md`, qui prévaut en cas de
divergence avec un jalon historique.

## 1. Inventaire unique des preuves

| ID | Compétences | Type | Fichier / preuve | Statut | Ce que cela prouve | Ce que cela ne prouve pas |
|---|---|---|---|---|---|---|
| PRV-01 | C4/C5 | architecture/projet | documents 01–22 et historique Git | PARTIAL | cadrage, besoins, risques et décisions | validation formelle par tous les utilisateurs |
| PRV-02 | C2/C5 | capture/UI | `evidence/frontend/REAL-UI-01` à `09` | PASS | écrans Feed/Chat/Marketplace/Profil/Auth rendus | E2E métier, sécurité ou accessibilité complète |
| PRV-03 | C6 | architecture | documents 46–61, doc 17 | PASS documentaire | couches et flux REST/STOMP/JPA | montée en charge/observabilité |
| PRV-04 | C7/C8 | données/test | `BE-DATA-03`, migrations V1–V3 | PASS | schéma V1, Flyway et validate sur PostgreSQL vide | adoption de base non vide seule |
| PRV-05 | C7/C8/C9/C10 | données/test | `BE-ADMIN-01`, EV-16 | PASS | V2/V3 sur PostgreSQL 16 avec 37 listings, 0 perte et 0 NULL | sauvegarde/restauration réelle |
| PRV-06 | C3/C8/C9 | test | `BE-PAY-01`, tests PostgreSQL dédiés | PASS | paiement, unicité, rollback et concurrence dans le périmètre testé | paiement réel/PSP |
| PRV-07 | C3/C8/C9 | sécurité/test | `BE-ADMIN-01`, `security-test-matrix.md` | PASS | RBAC ADMIN, wallet, dernier ADMIN, ownership et masquage | E2E navigateur complet de chaque refus |
| PRV-08 | C2/C3/C8/C9 | test | tests images Feed/Marketplace, `BE-ADMIN-01` | PASS | JPEG/PNG/GIF/BMP, 10 Mo, décodage, nettoyage, formats refusés | capture production de chaque format |
| PRV-09 | C2/C3/C9/C10 | E2E | `BE-E2E-01-production-recipe.txt` | PARTIAL | recette fonctionnelle production Admin/Feed/Marketplace | captures/traces HTTP réutilisables |
| PRV-10 | C10/C11 | déploiement | `BE-DEP-03` | PASS | Docker non-root, volume média et smoke Railway | backup, rollback, CD backend |
| PRV-11 | C10/C11 | CI | run backend 39165116209, commit 089f141 | PASS | Maven et build Docker sur CI | déploiement Railway automatique |
| PRV-12 | C10/C11 | CI/déploiement | run frontend 39165084863, commit 6b50af2 | PASS | tests/build/rsync Hostinger/smoke | CD backend |
| PRV-13 | C3/C8/C9 | sécurité | `security-test-matrix.md`, Phase 11C | PASS | JWT HttpOnly, CSRF, CORS, BOLA/IDOR et contrôles ciblés | révocation JWT centrale ou E2E STOMP complet |
| PRV-14 | C2/C9 | test | frontend 47/47, build et lint Phase 15 | PASS | tests frontend, build et lint connu | audit RGAA complet |
| PRV-15 | C2–C11 | conformité | `15_CONFORMITE_QUALITE.md`, `BE-QUAL-01` | PARTIAL | audit technique RGPD/RGAA/RGESN et limites | conformité juridique/certification |
| PRV-16 | C4–C11 | veille | `16_VEILLE_TECHNOLOGIQUE.md`, `BE-VEILLE-01` | PASS documentaire | méthode, sources et décisions datées | upgrade, scanner ou correction de CVE |
| PRV-17 | C10 | documentation | `102_DOCKER_RAILWAY_BACKEND.md`, `103_MIGRATIONS_POSTGRESQL_PRODUCTION.md` | PASS documentaire | runbook Docker, Flyway et variables sans secrets | restauration testée |
| PRV-18 | C1–C11 | documentation | `17_DOCUMENTATION_FINALE.md`, source de vérité | PASS documentaire | synthèse cohérente et limites | livraison des DOCX/PPTX régénérés |
| PRV-19 | C2/C3/C8/C9/C10 | capture production | `evidence/production/` | PARTIAL | structure et checklist d'archivage | aucune capture présente à ce jour |
| PRV-20 | C10/C11 | exploitation | smoke production 401/200 consignés | PARTIAL | comportement fonctionnel déclaré | logs Railway/traces HTTP archivables |

L'inventaire comporte **20 unités de preuve consolidées** : 15 PASS (dont
3 documentaires) et 5 PARTIAL. Les GAP transverses sont listés en section 7.

## 2. Matrice de recette E2E production

| ID | Scénario | Acteur | Précondition | Action | Attendu / observé | HTTP connu | Preuve disponible | Statut | RNCP |
|---|---|---|---|---|---|---|---|---|---|
| E2E-INF-01 | Smoke API | visiteur | aucune session | `GET /api/users/me` | refus / 401 | 401 | recette textuelle, trace à archiver | PARTIAL | C10/C11 |
| E2E-INF-02 | CSRF | visiteur | aucune session | `GET /api/auth/csrf` | XSRF-TOKEN / 200 | 200 | recette textuelle, trace à archiver | PARTIAL | C8/C10 |
| E2E-ADM-01 | Connexion et dashboard | ADMIN `demo` | compte existant | login puis `/admin` | dashboard accessible | non consigné | recette textuelle | PARTIAL | C2/C3 |
| E2E-ADM-02 | Utilisateurs/wallet | ADMIN `demo` | dashboard | consulter | liste et soldes visibles | non consigné | recette textuelle | PARTIAL | C2/C3 |
| E2E-ADM-03 | Crédit wallet | ADMIN `demo` | wallet cible | crédit positif | solde augmenté | non consigné | recette + tests backend | PARTIAL | C3/C8/C9 |
| E2E-FEED-01 | Modérer post | ADMIN `demo` | post de test | supprimer | post supprimé | non consigné | recette textuelle | PARTIAL | C2/C3 |
| E2E-FEED-02 | Modérer commentaire | ADMIN `demo` | commentaire test | supprimer | commentaire supprimé | non consigné | recette textuelle | PARTIAL | C2/C3 |
| E2E-MKT-01 | Demande achat | acheteur USER | listing AVAILABLE | request | PENDING | non consigné | recette textuelle | PARTIAL | C3/C8/C9 |
| E2E-MKT-02 | Acceptation | vendeur USER | PENDING | accept | ACCEPTED | non consigné | recette textuelle | PARTIAL | C3/C8/C9 |
| E2E-MKT-03 | Paiement | acheteur USER | ACCEPTED et solde | pay | PAID | non consigné | recette + tests PG | PARTIAL | C3/C7/C8/C9 |
| E2E-MKT-04 | Expédition | vendeur USER | PAID | ship | SHIPPED | non consigné | recette textuelle | PARTIAL | C3/C8 |
| E2E-MKT-05 | Réception | acheteur USER | SHIPPED | receive | RECEIVED | non consigné | recette textuelle | PARTIAL | C3/C8 |
| E2E-MKT-06 | Refus | vendeur USER | PENDING | refuse | REFUSED | non consigné | recette textuelle | PARTIAL | C3/C8 |
| E2E-MKT-07 | Solde insuffisant | acheteur USER | ACCEPTED sans solde | pay | refus | 409 | recette textuelle, trace à archiver | PARTIAL | C3/C8/C9 |
| E2E-MOD-01 | Supprimer REFUSED | ADMIN `demo` | listing REFUSED | supprimer | suppression physique | non consigné | recette textuelle | PARTIAL | C3/C8 |
| E2E-MOD-02 | Masquer listing engagé | ADMIN/tiers | listing engagé visible | modérer puis consulter tiers | `hidden=true`, absent tiers | non consigné | recette + tests backend | PARTIAL | C3/C8/C9 |

## 3. Preuves de tests et qualité

| Élément | Résultat exact | Statut | Portée / limite |
|---|---|---|---|
| Maven global | 104 tests rapportés, 0 failure, 0 error, **5 PostgreSQL conditionnels skipped** | PASS | Ne jamais écrire 104/104 exécutés. |
| PostgreSQL dédié | `PostgresPaymentIntegrationTests` : 5/5 exécutés, 0 skipped/failure/error | PASS | Valide le périmètre des tests dédiés, pas un restore. |
| Frontend | 47/47 PASS | PASS | Ne remplace pas une recette visuelle exhaustive. |
| Build frontend | Vite build PASS | PASS | Mesure de bundle disponible dans Phase 15. |
| Lint frontend | 0 erreur, 1 warning documenté | PASS | Warning non masqué, audit accessibilité incomplet. |
| Diff Git | `git diff --check` PASS lors des contrôles Phase 15/17 | PASS | Contrôle de whitespace, pas un test fonctionnel. |

## 4. Preuves CI/CD

| Dépôt | Commit / run | Chaîne effectivement prouvée | Statut | Non prouvé |
|---|---|---|---|---|
| Frontend | `6b50af2`, run 39165084863 | tests → build Vite → rsync SSH → Hostinger → smoke | PASS | ne déploie pas Railway |
| Backend | `089f141`, run 39165116209 | Maven tests → Docker build | PASS | aucun CD Railway automatique |
| Historique métier | backend `3966483`, frontend `c71bc7d`, runs 39143755010 / 39143768559 | CI des correctifs ADMIN/médias et déploiement frontend | PASS historique | ne remplace pas les runs de checkpoint actuels |

Le frontend possède donc une CI/CD automatisée vers Hostinger. Le backend possède
une CI et un déploiement Railway contrôlé/manuellement déclenché : il ne faut pas
le présenter comme du CD automatique.

## 5. Preuves sécurité

| Contrôle | Menace | Mesure | Test / preuve | Résultat | Limite |
|---|---|---|---|---|---|
| RBAC ADMIN | accès administratif USER | `/api/admin/**`, rôles serveur, CSRF | `BE-ADMIN-01`, tests sécurité | 401/403/autorisé | captures UI à archiver |
| Dernier ADMIN | perte de contrôle / élévation | auto-suppression, suppression/rétrogradation du dernier refusées; rôle retiré | `BE-ADMIN-01` | PASS tests | un seul ADMIN est un choix opérationnel |
| Wallet | IDOR/crédit libre | endpoint admin unique, montant positif, transaction | `BE-ADMIN-01`, matrice sécurité | PASS tests | wallet non bancaire |
| JWT/cookies | vol de jeton client | JWT HttpOnly, Secure prod, SameSite Lax, logout | Phase 11C, décisions sécurité | PASS ciblé | pas de révocation centrale |
| CSRF/CORS | mutation cross-site | token CSRF, CORS explicite | tests sécurité, recette 200 CSRF | PASS ciblé | traces production à archiver |
| Ownership/BOLA | action sur ressource d'autrui | contrôles service/controller/participant | matrice sécurité, tests Marketplace/Chat | PASS ciblé | STOMP E2E complet absent |
| Upload | binaire hostile/fichier résiduel | MIME + décodage réel, UUID, temporaire/nettoyage, 10 Mo | tests Feed/Marketplace | PASS tests | pas d'AV/quarantaine |
| Listing masqué | exposition transaction modérée | `hidden=true`, lecture tiers filtrée | `BE-ADMIN-01`, tests et recette | PASS tests / PARTIAL E2E | capture tiers/ADMIN à archiver |
| Paiement | double débit ou état invalide | transaction, contrainte unique, verrou/règles d'état | PostgreSQL 5/5 | PASS périmètre testé | pas de PSP réel |

## 6. Preuves base de données et déploiement

| Sujet | Preuve | Résultat | Statut | Limite |
|---|---|---|---|---|
| Flyway V1 | `BE-DATA-03` | 14 tables, historique `1:true`, Hibernate validate, PostgreSQL 16 vide | PASS | preuve initiale sur base vide |
| Flyway V2/V3 | `BE-ADMIN-01`, EV-16 | PostgreSQL 16, 37 listings, 0 perte, 0 NULL | PASS | restore non testé |
| Production DB | recette E2E / source de vérité | PostgreSQL Railway 18.6 déclaré; V1→V3 appliqué | PASS fonctionnel | warning Flyway 10.10.0/PG18 conservé |
| Docker/volume | `BE-DEP-03` | Java UID 999, `/app/media` `spring:spring`, persistance après redeploy | PASS production | pas de backup volume |
| Frontend production | run 39165084863 | Hostinger via rsync SSH et smoke | PASS | détails secrets exclus |
| Backend production | recette E2E | Railway démarré, smoke 401 et CSRF 200 | PARTIAL documentaire | journaux/traces à centraliser |

## 7. Gaps à conserver

| GAP | Conséquence | Statut |
|---|---|---|
| Backup/restauration réelle | reprise après incident non démontrée | GAP |
| Rollback production réel | procédure non éprouvée | GAP |
| Observabilité complète | pas de métriques, alertes ou centralisation prouvées | GAP |
| Conformité RGPD certifiée | politique/registre/rétention/effacement complet non démontrés | GAP |
| Audit RGAA complet | contraste, clavier, zoom et lecteur d'écran non audités formellement | GAP |
| CD backend automatique | déploiement Railway manuel contrôlé | GAP |
| Révocation JWT centrale | invalidation distribuée indisponible | GAP |
| Antivirus/quarantaine média | aucun scan de fichier téléversé | GAP |
| Avatar harmonisé | politique avatar distincte des uploads Feed/Marketplace | GAP |
| Flyway/PostgreSQL 18 | warning de compatibilité à revalider avant évolution | GAP |

## 8. Mapping C1 à C11 orienté preuves

| Compétence | Preuves principales | Preuves secondaires | Niveau | Justification |
|---|---|---|---|---|
| C1 | Docker, profils, CI, environnements documentés | doc 17, source de vérité | PARTIAL | configuration démontrée; restauration non testée |
| C2 | 47 tests frontend, captures UI existantes, ADMIN/Feed/Marketplace | recette E2E fonctionnelle | PARTIAL | captures E2E/Admin et audit RGAA incomplets |
| C3 | Marketplace, wallet, RBAC et modération testés | recette production | PARTIAL | STOMP E2E et traces E2E à compléter |
| C4 | cadrage, backlog, risques, Git et index de preuves | phases 15–18 | PARTIAL | livrables jury définitifs à générer |
| C5 | besoins, personas, wireframes, prototype et parcours | recette par rôles | PARTIAL | validation formelle utilisateurs absente |
| C6 | architecture multicouche et flux documentés | Docker/Railway et CI | PASS documentaire | preuve d'architecture exploitable; optimisation future hors périmètre |
| C7 | Flyway V1–V3, PostgreSQL 16 non vide, 5/5 dédié | modèle de données | PARTIAL | backup/restore et warning PG18 |
| C8 | services/repositories transactionnels, BOLA, hidden, wallet | tests PostgreSQL | PARTIAL | charge et STOMP E2E incomplets |
| C9 | suites backend/frontend, PostgreSQL, CI, matrice E2E | `git diff --check` | PARTIAL | captures et traces E2E manquantes |
| C10 | Docker non-root, volume, Flyway, Railway/Hostinger, smoke | runbooks | PARTIAL | rollback/restore non prouvés |
| C11 | CI backend; CI/CD frontend et déploiement manuel contrôlé | veille DevOps | PARTIAL | pas de CD backend, métriques ni rollback testé |

## 9. Checklist courte des preuves à ajouter

### Priorité 1

- `UPLOAD-JPEG-CAPTURE`, `UPLOAD-PNG-CAPTURE`, `UPLOAD-GIF-CAPTURE`, `UPLOAD-BMP-CAPTURE` : captures production anonymisées, dans `evidence/production/`.
- Traces/captures E2E : smoke 401/CSRF 200, cycle nominal, refus, 409 insuffisance, modération `hidden`, dashboard et crédit wallet.

### Priorité 2

- Sauvegarde PostgreSQL + média puis restauration isolée et compte rendu horodaté.
- Health/observabilité seulement si elle est effectivement ajoutée et testée.

### Priorité 3

- Audit accessibilité complémentaire : contraste, clavier, zoom, lecteur d'écran et synthèse RGAA.

## 10. Références de consultation

- `24_PLAN_PREUVES.md` : plan de collecte et pièces prioritaires.
- `23_MAPPING_C1_C11.md` : mapping RNCP consolidé.
- `RNCP6_SOURCE_DE_VERITE.md` : état courant faisant foi.
- `17_DOCUMENTATION_FINALE.md` : synthèse jury.
- `evidence/production/README.md` : emplacement et règles d'archivage des futurs artefacts.
