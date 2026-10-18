# WatYouFace — Dossier Projet final (source)

Version source : 18/10/2026. Ce document est la source Markdown du futur
Dossier Projet DOCX/PDF. Il s'appuie sur `RNCP6_SOURCE_DE_VERITE.md`, qui
prévaut en cas de divergence. Les captures sont référencées uniquement lorsqu'elles
existent; les emplacements à compléter ne constituent pas une preuve.

---

## 1. Page de garde

**Titre :** WatYouFace — conception, réalisation et mise en production d'une
application sociale sécurisée avec Marketplace.
**Certification visée :** RNCP CDA niveau 6.
**Candidat :** à renseigner.
**Date de remise :** à renseigner.
**Version du contenu :** état consolidé au 18/10/2026.

> Emplacement visuel DP-01 : titre, identité du candidat et contexte de la
> certification. Aucune capture applicative n'est requise ici.

## 2. Sommaire

Ce document suit les sections 3 à 40 ci-dessous. Dans le DOCX final, générer un
sommaire automatique, les numéros de page et une liste des figures/annexes.

## 3. Présentation du candidat

À compléter par le candidat : parcours, rôle exercé dans le projet, contexte de
réalisation et contributions personnelles. Les éléments techniques présentés
dans ce dossier sont tracés dans Git, les tests et les preuves RNCP; ils ne
doivent pas être attribués à une personne non impliquée.

## 4. Contexte du projet

WatYouFace est une application web réunissant dans un même produit un feed
social, une messagerie privée et une marketplace. Le projet permet d'aborder
interfaces utilisateur, API sécurisée, base relationnelle, transactions métier,
médias persistants et déploiement d'une application moderne.

Le périmètre reste celui d'un MVP : il ne revendique pas un paiement réel, une
conformité réglementaire certifiée, ni une exploitation industrielle complète.

## 5. Présentation de WatYouFace

Les trois piliers fonctionnels sont le Feed, le Chat et la Marketplace. Le Feed
porte les contenus et interactions sociales; le Chat permet des conversations
privées; la Marketplace matérialise un cycle d'achat/vente et un paiement de
démonstration. Un espace ADMIN apporte la modération et la gestion contrôlée des
crédits de démonstration. La sécurité reste centralisée au backend.

> Figure DP-02 à prévoir : `evidence/frontend/REAL-UI-01-feed-desktop.png`.

## 6. Objectifs et problématique

La problématique est de centraliser des usages sociaux, conversationnels et de
vente tout en protégeant identités, données et règles métier. Les objectifs sont
de séparer frontend/backend, contrôler les droits, garantir les transitions
Marketplace, persister les données relationnelles et documenter le déploiement.

La qualité repose sur des tests, une gestion explicite des erreurs et la
documentation des limites plutôt que sur des promesses non démontrées.

## 7. Analyse du besoin

Les besoins couvrent les utilisateurs qui publient et communiquent, les vendeurs
et acheteurs qui suivent une transaction, et un modérateur. Aucun client ne doit
pouvoir imposer un auteur, un rôle, un montant de wallet ou une transition.
Les exigences et parcours détaillés sont tracés dans les documents 08 à 16.

## 8. Utilisateurs et rôles

| Acteur | Capacités | Restrictions importantes |
|---|---|---|
| Visiteur | inscription, connexion, lecture autorisée | pas d'action protégée |
| USER | Feed, Chat membre, annonces, achat/vente, consultation wallet | ne crédite pas un wallet; pas d'administration |
| Vendeur | accepte/refuse et expédie selon l'état | ne paie pas à la place de l'acheteur |
| Acheteur | demande, paie et réceptionne selon l'état | ne vend pas à la place du vendeur |
| ADMIN `demo` | modération et crédit wallet | pas de bypass transactionnel; rôle non mutable |

Un seul ADMIN est actuellement utilisé : `demo`. Aucun second ADMIN n'a été
créé pour la recette ou la démonstration.

## 9. Fonctionnalités

| Domaine | Fonctionnalités réalisées | Preuve principale |
|---|---|---|
| Authentification | inscription USER, session cookie, CSRF, logout | Phase 11C / tests sécurité |
| Feed | posts, commentaires, likes et suppression contrôlée | tests + recette E2E PARTIAL |
| Chat | conversations privées, messages REST/STOMP | tests composant; E2E STOMP à compléter |
| Marketplace | annonces, transitions, paiement démo, refus, modération | `BE-E2E-01-production-recipe.txt` |
| Wallet | crédit ADMIN transactionnel | `BE-ADMIN-01-rbac-wallet-moderation-media.txt` |
| ADMIN | utilisateurs, wallets, Feed, commentaires et annonces | tests backend; captures à ajouter |

## 10. Gestion de projet

La démarche est incrémentale : cadrage, besoins, UX/architecture, développement
par lots, tests, mise en production, conformité/veille et consolidation des
preuves. Git/GitHub servent à versionner les branches, commits et contrôles CI.
Les jalons historiques restent consultables; l'état actuel est centralisé dans
`18_PREUVES_RNCP_FINALES.md`.

## 11. UX / UI / prototypage

Les wireframes, maquettes et prototype ont préparé les écrans des trois piliers.
L'interface implémentée dispose de captures versionnées Feed, Chat, Marketplace,
Profil et Login. Des améliorations d'accessibilité ciblées ont été apportées :
labels, noms accessibles, messages d'erreur, alternatives et tableaux ADMIN.

**Statut : PARTIAL.** Ces travaux ne constituent pas un audit RGAA complet.

> Figures DP-03 à DP-06 : captures existantes `REAL-UI-01` à `REAL-UI-09`.

## 12. Architecture générale

```text
Navigateur → React / Vite → REST + STOMP/WebSocket → Spring Boot
           → services métier → repositories JPA → PostgreSQL
```

Le frontend porte l'expérience et les validations de confort. Le backend porte
les autorisations, l'ownership, les transactions et l'intégrité métier.

## 13. Stack technique

| Couche | Technologies |
|---|---|
| Frontend | React 19.1, Vite 7.3.7, Vitest, composants UI |
| Backend | Java 17, Spring Boot 3.3.5, Spring Security, Maven |
| Persistance | JPA, Hibernate 6.5.3, PostgreSQL, Flyway 10.10.0 |
| Temps réel | WebSocket/STOMP |
| DevOps | Docker, GitHub Actions, Railway, Hostinger |

## 14. Architecture frontend

Le frontend React/Vite propose Feed, Chat, Marketplace, Profil et Dashboard
ADMIN. Les requêtes utilisent les cookies de session et le jeton CSRF pour les
mutations. Les validations d'upload côté interface sont alignées sur le contrat
backend, mais le backend reste l'autorité finale.

## 15. Architecture backend

Les controllers exposent les contrats HTTP, les services appliquent les règles
et transactions, et les repositories accèdent aux données JPA. Les DTO évitent
l'exposition inutile de données sensibles. En production, Flyway applique les
migrations avant la validation Hibernate; `ddl-auto=validate` ne crée pas le
schéma.

## 16. Modèle de données

Le modèle comprend utilisateurs, posts, commentaires, likes, conversations,
messages, annonces, transactions et wallets. Flyway V1 crée le schéma; V2 ajoute
`listings.created_at`; V3 ajoute `listings.hidden`.

V2/V3 ont été validées sur PostgreSQL 16 avec 37 listings existants, sans perte
de données et sans `NULL` résiduel sur les colonnes ajoutées. PostgreSQL 18.6 est
déclaré sur Railway; le warning Flyway/PostgreSQL reste une limite surveillée.

## 17. Authentification / autorisation

```text
register → rôle USER imposé
login → cookie JWT HttpOnly
mutation → XSRF-TOKEN / CSRF
logout → expiration du cookie
```

Le cookie est `Secure` en production, `HttpOnly` et `SameSite=Lax`. CORS utilise
des origines explicites. RBAC, ownership et contrôles BOLA/IDOR sont appliqués
au backend; une protection d'interface ne suffit jamais.

## 18. Sécurité applicative

| Risque | Mesure actuelle | Preuve |
|---|---|---|
| Accès ADMIN | routes ADMIN, RBAC et CSRF | `BE-ADMIN-01`, matrice sécurité |
| Élévation de rôle | mutation supprimée; DTO/inscription bornés | tests ADMIN |
| Perte dernier ADMIN | auto-suppression/rétrogradation refusées | tests ADMIN |
| BOLA/IDOR | ownership, participant et acteur transactionnel vérifiés | tests Marketplace/Chat |
| Upload hostile | MIME, décodage réel, UUID, temporaire/nettoyage, 10 Mo | tests média |
| Secrets | variables externalisées, aucun secret versionné | CI/runbooks |

Le rate limiter est in-memory mono-instance; JWT n'a pas de révocation centrale;
un antivirus/quarantaine média n'est pas présent.

## 19. Feed

Le Feed gère la publication, la consultation, les commentaires et les likes.
Les actions d'édition/suppression sont bornées par l'ownership; l'ADMIN modère
par suppression sans modifier arbitrairement le texte d'un autre utilisateur.

## 20. Chat / WebSocket

Les conversations sont privées et l'appartenance est contrôlée côté REST et
STOMP. Le chat est volontairement générique : il ne prétend pas avoir une
relation technique `Listing`–`Conversation`. Les tests ciblés existent, mais un
E2E STOMP multi-utilisateur reste à réaliser.

> Figure DP-07 à prévoir : `evidence/frontend/REAL-UI-03-chat-desktop.png`.

## 21. Marketplace

```text
AVAILABLE → PENDING → ACCEPTED → PAID → SHIPPED → RECEIVED
                    └→ REFUSED
```

Le vendeur et l'acheteur sont les seuls acteurs transactionnels. Le solde
insuffisant est rejeté en HTTP 409. Les tests PostgreSQL dédiés couvrent le
paiement, l'unicité et le rollback dans le périmètre de démonstration.

> Figure DP-08 à prévoir : `evidence/frontend/REAL-UI-05-marketplace.png`.

## 22. Wallet de démonstration

Le wallet sert uniquement au paiement simulé Marketplace. Le seul crédit manuel
est `POST /api/admin/users/{userId}/wallet/credit`, réservé à l'ADMIN, positif
et transactionnel. Un USER ne peut pas créer arbitrairement, créditer ou fixer
un solde via l'API.

## 23. Administration / modération

L'ADMIN `demo` peut consulter utilisateurs, wallets, posts, commentaires et
annonces. Le rôle ADMIN n'est plus modifiable depuis l'UI ou l'API et le dernier
ADMIN ne peut être supprimé/rétrogradé.

Pour préserver l'intégrité : une annonce `AVAILABLE` ou `REFUSED` est supprimable
physiquement; une annonce engagée de `PENDING` à `RECEIVED` est masquée avec
`hidden=true`. Le tiers ne la voit plus, mais vendeur, acheteur et ADMIN gardent
l'accès nécessaire. L'ADMIN ne contourne pas le cycle vendeur/acheteur.

> Figures DP-09/10 à prévoir : dashboard ADMIN et modération sur données de test.

## 24. Gestion des médias

Feed et Marketplace acceptent JPEG/JPG, PNG, GIF et BMP, jusqu'à 10 Mo. Le MIME
est filtré puis le contenu est réellement décodé. Les formats GIF/BMP sont
normalisés en JPEG (première image pour GIF); l'écriture passe par un temporaire,
un déplacement après succès et un nettoyage à l'échec.

WebP, AVIF et HEIC/HEIF sont volontairement non supportés. JPEG/PNG/GIF/BMP sont
fonctionnels validés en production, mais les captures visuelles restent à
archiver. L'avatar est un flux distinct à harmoniser.

## 25. Tests

| Exécution | Résultat exact | Interprétation |
|---|---|---|
| Maven global | 104 rapportés, 0 failure, 0 error, 5 PostgreSQL conditionnels skipped | ne pas écrire 104/104 exécutés |
| PostgreSQL dédié | 5/5, 0 skipped/failure/error | paiement et contraintes sur moteur réel local |
| Frontend | 47/47 PASS | composants et comportements ciblés |
| Build Vite | PASS | build production généré |
| Lint | 0 erreur, 1 warning documenté | warning non masqué |

Les captures/traces E2E ne remplacent jamais les tests versionnés; inversement,
un test ne remplace pas une démonstration visuelle lorsqu'elle est attendue.

## 26. CI/CD

```text
Frontend : tests → build Vite → rsync SSH → Hostinger → smoke
Backend  : Maven tests → Docker build → déploiement Railway manuel contrôlé
```

Le run frontend 39165084863, associé au commit `6b50af2`, est SUCCESS. Le run
backend 39165116209, associé au commit `089f141`, est SUCCESS pour Maven et
Docker. Le backend ne dispose pas de CD Railway automatique dans ce dépôt.

## 27. Déploiement

Le frontend est disponible sur `https://watyouface.com` et hébergé sur Hostinger.
Le déploiement provient de GitHub Actions et de `rsync` SSH : aucun Git natif
Hostinger n'est utilisé. Le backend est disponible sur `https://api.watyouface.com`,
sur Railway, dans une image Docker Java 17.

Le volume persistant `/app/media` est initialisé de manière ciblée puis Java
tourne en utilisateur non-root `spring`. La recette a observé le volume persistant
et le smoke `/api/users/me` sans session à 401; `/api/auth/csrf` répond 200 avec
le cookie XSRF-TOKEN.

## 28. Base de données / migrations

PostgreSQL Railway est le moteur de production. Flyway V1→V3 est appliqué et
Hibernate valide le schéma. V1 a été validée sur base PostgreSQL 16 vide avec
14 tables; V2/V3 sur 37 listings existants, sans suppression et sans NULL pour
`created_at`/`hidden`.

La version PostgreSQL 18.6 déclarée sur Railway provoque un warning de
compatibilité avec Flyway 10.10.0 : le projet ne l'ignore pas. Toute évolution
doit être testée avant production et précédée d'une sauvegarde validée.

## 29. Conformité RGPD / RGAA / RGESN

Des mesures de conformité ont été intégrées, mais une conformité complète n'est
pas revendiquée.

| Domaine | État factuel |
|---|---|
| RGPD | PARTIAL/GAP : minimisation et sécurité techniques analysées; politique, registre, rétention, export et effacement complet à formaliser. |
| RGAA | PARTIAL : labels, alternatives, alertes et tableaux améliorés; audit formel complet absent. |
| RGESN | PARTIAL : médias normalisés, pagination ciblée et bundle mesuré; stratégie cache, Lighthouse et optimisation complète absents. |

## 30. Veille technologique

La veille documente Java/Spring, PostgreSQL/Flyway, React/Vite, OWASP API,
uploads, observabilité, backup et déploiement. Les décisions sont de conserver
Java 17 avant soutenance, ne pas adopter WebP sans décodeur fiable, garder le
déploiement Railway contrôlé et tester chaque évolution Flyway/PostgreSQL.

## 31. Recette E2E production

La recette fonctionnelle a validé : démarrage Railway, migrations V2/V3, smoke
401/CSRF 200, connexion ADMIN, wallet, modération Feed, cycle Marketplace
nominal jusqu'à RECEIVED, refus, solde insuffisant à 409 et masquage d'annonce
engagée. Les détails sont dans `BE-E2E-01-production-recipe.txt` et la matrice
Phase 18.

**Statut : PASS fonctionnel / PARTIAL documentaire.** Les captures et traces HTTP
ne sont pas encore archivées; elles ne sont pas inventées dans ce dossier.

## 32. Résultats

Les résultats exploitables sont : application disponible sur Hostinger/Railway,
migrations Flyway, volume média persistant non-root, tests backend/frontend,
tests PostgreSQL dédiés, CI backend, CI/CD frontend et recette fonctionnelle
production. Les uploads JPEG/PNG/GIF/BMP sont fonctionnels validés, avec preuves
visuelles encore à ajouter.

## 33. Difficultés rencontrées

| Difficulté | Impact |
|---|---|
| WebP/ImageIO | le MIME annoncé ne garantissait pas un décodage réel; erreurs 500 possibles |
| Hostinger Git natif | le source Vite était susceptible d'être déployé au lieu de `dist` |
| Flyway sur base non vide | risque d'échec ou de perte lors de l'ajout de colonnes |
| BOLA/IDOR | risque qu'un tiers agisse sur un listing/transaction non possédé |
| ADMIN transactionnel | risque de contourner les règles vendeur/acheteur |

## 34. Solutions apportées

| Problème | Décision et résultat |
|---|---|
| WebP | formats réellement décodés : JPEG/PNG/GIF/BMP; WebP/AVIF/HEIC refusés; ImageIO, temporaire et 400 contrôlé |
| Hostinger | GitHub Actions → build Vite → rsync SSH → smoke; Git natif Hostinger non réactivé |
| Flyway | migration additive V2/V3, test sur 37 listings, 0 perte/NULL |
| BOLA/IDOR | ownership et acteurs transactionnels contrôlés côté backend |
| ADMIN | modérateur/gestionnaire wallet, sans bypass Marketplace; `hidden=true` préserve les transactions |

## 35. Limites connues

- backup/restauration réelle non testée;
- rollback production non exécuté;
- observabilité limitée, sans métriques ni alertes prouvées;
- rate limiter mono-instance et absence de révocation JWT centrale;
- pas d'antivirus/quarantaine média;
- avatar sur flux distinct à harmoniser;
- pas de CD backend automatique;
- warning Flyway/PostgreSQL 18;
- conformité RGPD/RGAA/RGESN non certifiée.

## 36. Pistes d'amélioration

Priorités post-soutenance : test de restauration DB/média, observabilité
Actuator/Micrometer avec accès maîtrisé, audit RGAA, formalisation RGPD,
harmonisation avatar, antivirus/quarantaine, stockage objet si nécessaire,
limiteur distribué et étude Java 21/Flyway avant mise à jour.

## 37. Mapping RNCP C1–C11

| Compétence | Contribution WatYouFace | Niveau | Justification |
|---|---|---|---|
| C1 | environnement Java/Docker/profils/CI | PARTIAL | restauration non testée |
| C2 | interfaces Feed/Chat/Marketplace/ADMIN | PARTIAL | audit RGAA/captures E2E incomplets |
| C3 | règles métier, wallet, modération, RBAC | PARTIAL | E2E STOMP et traces à compléter |
| C4 | cadrage, backlog, Git et preuves | PARTIAL | livrables jury à finaliser |
| C5 | besoins, personas, maquettes/prototype | PARTIAL | validation formelle utilisateurs absente |
| C6 | architecture multicouche documentée | PASS documentaire | flux et responsabilités exploitables |
| C7 | Flyway/PostgreSQL/migrations | PARTIAL | restore et warning PG18 |
| C8 | accès données, transactions, BOLA/IDOR | PARTIAL | charge/STOMP E2E incomplets |
| C9 | tests, CI et recette | PARTIAL | traces/captures E2E manquantes |
| C10 | Docker, volume, Railway/Hostinger | PARTIAL | rollback/restore non prouvés |
| C11 | CI backend et CI/CD frontend | PARTIAL | CD backend, métriques et rollback absents |

Le détail et les preuves associées figurent dans `23_MAPPING_C1_C11.md` et
`18_PREUVES_RNCP_FINALES.md`.

## 38. Conclusion

WatYouFace démontre un MVP déployé, testé et documenté autour de trois domaines
métier cohérents. Les décisions de sécurité, de données et d'exploitation ont
été prises à partir de problèmes rencontrés et non seulement de choix théoriques.
Le dossier distingue les résultats validés des limites restantes afin de fournir
au jury une démonstration vérifiable et honnête.

## 39. Liste des preuves sélectionnées

| Domaine | Preuve principale | Preuves secondaires |
|---|---|---|
| Architecture | `17_DOCUMENTATION_FINALE.md` | documents 46–61 |
| Sécurité / ADMIN | `BE-ADMIN-01-rbac-wallet-moderation-media.txt` | matrice sécurité |
| Données | `BE-DATA-03-flyway-railway-baseline.txt` | tests PostgreSQL 5/5 |
| Déploiement | `BE-DEP-03-railway-media-volume-permissions.txt` | docs 102/103 |
| CI | runs 39165116209 et 39165084863 | workflows versionnés |
| E2E | `BE-E2E-01-production-recipe.txt` | matrice Phase 18 |
| Qualité/conformité | `BE-QUAL-01-conformite-qualite.txt` | Phase 15 |
| Veille | `BE-VEILLE-01-veille-technologique.txt` | Phase 16 |

## 40. Annexes à prévoir

1. Captures UI existantes : Feed, Chat, Marketplace, Profil et Login.
2. Captures à produire : dashboard ADMIN, wallet, modération, cycle Marketplace,
   traces smoke et uploads JPEG/PNG/GIF/BMP.
3. Extraits anonymisés de résultats Maven, PostgreSQL, Vitest, lint et CI.
4. Diagrammes d'architecture, MCD/MLD/MPD et migrations Flyway.
5. Référentiel détaillé : `18_PREUVES_RNCP_FINALES.md`.

Les annexes futures doivent être datées, anonymisées et dépourvues de secret.
