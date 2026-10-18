# Dossier Professionnel — Kouakou Brice Kanga

Version source : 18/10/2026. Cette source est destinée au futur Dossier
Professionnel CDA RNCP niveau 6. Elle s'appuie principalement sur WatYouFace et
sur les preuves versionnées de ce dépôt. Elle ne remplace pas le Dossier Projet :
elle explicite ma contribution, mes choix, mes méthodes et mon recul.

## 1. Identification du candidat

**Nom :** Kouakou Brice Kanga
**Titre préparé :** Concepteur Développeur d'Applications — RNCP niveau 6
**Projet principal :** WatYouFace
**Orientation :** développement full-stack sécurisé, AppSec et SOC junior.

Les dates administratives, coordonnées et éléments réglementaires seront ajoutés
dans le DOCX final par le candidat; ils ne sont pas versionnés dans le dépôt.

## 2. Titre professionnel préparé

Je prépare le titre de Concepteur Développeur d'Applications. Dans ce cadre,
je démontre ma capacité à développer des interfaces, concevoir une application
organisée en couches, sécuriser les accès et préparer son déploiement. Je relie
chaque situation présentée à une preuve, en conservant les limites observées.

## 3. Présentation du parcours

Je suis en reconversion professionnelle après une expérience de technicien
forestier. J'ai suivi une formation de développement full-stack à Holberton
School Paris et je me suis spécialisé en cybersécurité. Mon orientation vise les
métiers AppSec, développement sécurisé et SOC junior.

Ce parcours m'a conduit à privilégier l'autonomie, la rigueur, l'analyse de
problèmes et la documentation. Je n'aborde pas la sécurité comme une étape
finale : je l'intègre dans les droits, validations, tests, migrations et
déploiement.

## 4. Synthèse des compétences

| Axe | Ce que j'ai mis en pratique sur WatYouFace | Niveau de preuve |
|---|---|---|
| Interfaces | React/Vite, Feed, Chat, Marketplace, ADMIN, validations et erreurs | PARTIAL : tests/captures, audit RGAA incomplet |
| Métier sécurisé | cycle Marketplace, wallet démo, RBAC, ownership, modération | PARTIAL : tests et recette, traces E2E à archiver |
| Architecture/données | React → Spring Boot → JPA → PostgreSQL, Flyway et transactions | PASS documentaire / PARTIAL exploitation |
| Tests/qualité | Maven, PostgreSQL dédié, Vitest, lint, CI | PASS pour les exécutions citées |
| Déploiement | Docker, Railway, Hostinger, GitHub Actions, volume média | PARTIAL : restore/rollback/CD backend absents |

## 5. Activité type 1 — Développer une application sécurisée

J'ai développé les interfaces React/Vite du Feed, du Chat, du Marketplace et du
Dashboard ADMIN. J'ai aligné les formulaires Marketplace et Feed sur le contrat
backend : JPEG/PNG/GIF/BMP, limite de 10 Mo et affichage d'erreurs exploitables.
J'ai renforcé l'accessibilité de manière ciblée par labels, alternatives, noms de
boutons, alertes et tableaux ADMIN.

J'ai contribué à la gestion d'erreurs, aux appels API avec cookies/CSRF et aux
tests frontend. La suite frontend compte 47 tests PASS; le build Vite est PASS
et le lint ne contient aucune erreur, avec un avertissement documenté. Je ne
présente pas ces résultats comme un audit RGAA complet.

**Preuves :** EV-07, EV-14, EV-15, EV-20; `18_PREUVES_RNCP_FINALES.md`.

## 6. Activité type 2 — Concevoir et développer une application sécurisée organisée en couches

J'ai travaillé sur une architecture où React/Vite communique avec Spring Boot par
REST et STOMP/WebSocket. Côté backend, j'ai distingué controllers, services et
repositories JPA. Les services portent les règles métier, les contrôles d'accès
et les transactions; les DTO réduisent l'exposition de données non nécessaires.

J'ai contribué à sécuriser le cycle Marketplace, les transactions wallet de
démonstration, le RBAC ADMIN, l'ownership et les risques BOLA/IDOR. J'ai adopté
Flyway pour la reproductibilité de la base et conservé Hibernate en `validate`
en production. Le STOMP E2E et les tests de charge restent des limites.

**Preuves :** EV-06, EV-14 à EV-17; `BE-ADMIN-01…`, `BE-DATA-03…` et matrice
de sécurité.

## 7. Activité type 3 — Préparer le déploiement d'une application sécurisée

J'ai préparé le backend Docker Java 17 avec un runtime non-root, Flyway et des
variables externalisées. J'ai vérifié le volume média Railway, l'exécution Java
sous `spring` et les smoke tests. J'ai mis en place une CI backend qui exécute
Maven puis le build Docker.

J'ai aussi utilisé un pipeline frontend automatisé : tests, build Vite, `rsync`
SSH vers Hostinger et smoke test. Le déploiement Railway backend reste manuel et
contrôlé; je ne le présente pas comme du CD automatique.

**Preuves :** EV-09, EV-10, EV-22; runs 39165116209 et 39165084863.

## 8. Situations professionnelles significatives

### Situation 1 — Sécurisation Marketplace, ownership et transactions

**Contexte.** Le cycle Marketplace porte des actions réservées à l'acheteur ou
au vendeur et un paiement de démonstration.
**Problème.** Une action par un tiers ou une transition invalide créerait un
risque BOLA/IDOR et d'incohérence métier.
**Mon action.** J'ai renforcé les contrôles d'ownership et d'état côté backend,
testé les refus et conservé la transaction de paiement dans le périmètre
PostgreSQL dédié.
**Résultat.** Cycle nominal/refusé validé fonctionnellement; solde insuffisant
en 409; PostgreSQL dédié 5/5 PASS.
**Preuve.** EV-16, EV-17, `BE-PAY-01…`.
**Recul.** Les traces HTTP de recette restent à archiver.

### Situation 2 — Déploiement frontend Hostinger

**Contexte.** Le frontend Vite doit publier le répertoire compilé, non les
sources.
**Problème.** Le Git natif Hostinger pouvait déployer le source Vite au lieu de
`dist`.
**Mon action.** J'ai retenu GitHub Actions : tests, build Vite, rsync SSH puis
smoke test, sans réactiver le Git natif Hostinger.
**Résultat.** Run 39165084863 SUCCESS et site production déployé.
**Preuve.** EV-22.
**Recul.** Cette automatisation concerne le frontend, pas Railway backend.

### Situation 3 — Migration PostgreSQL/Flyway sur base non vide

**Contexte.** Une base production peut déjà contenir des listings.
**Problème.** Ajouter `created_at` et `hidden` pouvait échouer ou perdre des
données.
**Mon action.** J'ai utilisé des migrations additives, validées sur PostgreSQL
16 avec 37 listings.
**Résultat.** Aucune suppression et 0 NULL après V2/V3.
**Preuve.** EV-16, `BE-ADMIN-01…`.
**Recul.** Backup/restore reste à tester; le warning PostgreSQL 18/Flyway reste
surveillé.

### Situation 4 — ADMIN, wallet et modération

**Contexte.** L'ADMIN devait modérer sans contourner le Marketplace.
**Problème.** Un ADMIN acteur transactionnel et un crédit wallet libre auraient
fragilisé le modèle métier.
**Mon action.** J'ai retiré la mutation de rôle, protégé le dernier ADMIN,
réservé le crédit manuel à l'endpoint ADMIN et séparé suppression/masquage.
**Résultat.** Les annonces engagées sont masquées par `hidden=true`; les tiers
sont exclus, sans casser la transaction.
**Preuve.** EV-14, EV-17, `BE-ADMIN-01…`.
**Recul.** Les captures d'administration restent à ajouter.

### Situation 5 — Upload média sécurisé

**Contexte.** ImageIO/Thumbnailator ne décodait pas WebP de manière fiable.
**Problème.** Un MIME WebP annoncé pouvait produire une erreur 500.
**Mon action.** J'ai limité la politique aux formats réellement décodés :
JPEG/PNG/GIF/BMP, 10 Mo, MIME plus décodage réel, temporaire et nettoyage.
**Résultat.** Une image illisible est une erreur client; GIF/BMP sont normalisés
en JPEG.
**Preuve.** EV-15 et tests média.
**Recul.** Les captures production des quatre formats sont à archiver.

## 9. Projets complémentaires

WatYouFace est mon projet principal pour la démonstration CDA. Je peux le
compléter oralement par deux expériences, sans les présenter comme des preuves
supplémentaires versionnées dans ce dépôt.

- **PurpleWatch, réalisé seul :** projet cybersécurité mobilisant Caldera,
  Wazuh, auditd, ATT&CK, détection, corrélation, journalisation et mesure de
  latence. Il illustre mon intérêt pour la détection et l'analyse sécurité.
- **Bagage Voyage :** expérience de développement avec Next.js, sécurité de
  checkout, IDOR/BOLA, paiement, capacité, tests et CI/CD. Elle complète mes
  réflexions sur les règles métier et les accès, sans remplacer WatYouFace.

Les détails, responsabilités et résultats de ces projets doivent être confirmés
par leurs propres supports avant d'être ajoutés comme annexes formelles.

## 10. Sécurité et qualité

J'ai appliqué une logique de sécurité dès la conception : cookie JWT HttpOnly,
CSRF, CORS explicite, RBAC, ownership, contrôles BOLA/IDOR, validation des
uploads et secrets externalisés. J'ai aussi documenté les limites : pas de
révocation JWT centrale, rate limiter mono-instance, pas d'antivirus média.

Pour la qualité, j'ai utilisé tests backend/frontend, PostgreSQL dédié, build,
lint, CI et `git diff --check`. Les résultats exacts sont conservés dans la
consolidation Phase 18; je distingue un test exécuté d'une capture E2E absente.

## 11. Travail autonome / gestion de projet

J'ai organisé le travail par phases, documents, branches, commits et contrôles.
J'ai relu l'état réel avant de déclarer un résultat, puis documenté les décisions
et limites. Cette méthode m'a permis de corriger des écarts concrets : support
WebP annoncé sans décodeur, migration de schéma, permissions de volume et
confusion possible entre rôle ADMIN et rôle transactionnel.

## 12. Veille technologique

J'ai formalisé une méthode : source officielle, analyse de l'impact WatYouFace,
priorité, test isolé, décision et documentation. Les sujets suivis couvrent
Java/Spring, PostgreSQL/Flyway, React/Vite, OWASP API, uploads, observabilité,
sauvegarde et déploiement.

Cette veille m'a conduit à conserver Java 17 avant soutenance, à ne pas ajouter
WebP sans décodeur testable et à ne pas ignorer le warning Flyway/PostgreSQL 18.

## 13. Compétences transversales

| Compétence | Exemple concret |
|---|---|
| Autonomie | audit de l'état réel, reprise des preuves et décisions documentées |
| Recherche de solution | diagnostic WebP/ImageIO, Flyway et permissions volume |
| Documentation | source de vérité, matrices RNCP, runbooks et dossiers finaux |
| Communication technique | explication des statuts PASS/PARTIAL/GAP et des limites |
| Gestion de changements | branches, commits, CI et validation avant déploiement |
| Sécurité by design | droits serveur, tests négatifs, validation upload et secrets externalisés |

## 14. Évolution professionnelle

Je souhaite poursuivre dans un environnement où je peux combiner développement
full-stack et sécurité : AppSec, développement sécurisé ou SOC junior. Les
prochaines étapes que j'identifie sont l'observabilité, les tests de restauration,
les audits accessibilité et la sécurité des médias.

## 15. Conclusion

WatYouFace m'a permis de mobiliser conception, développement, sécurité, données,
tests et déploiement autour d'un produit réel. Je présente les preuves disponibles
mais aussi les limites restantes, afin d'adopter une posture professionnelle
fondée sur des faits vérifiables plutôt que sur une conformité non démontrée.

## 16. Références vers les preuves

| Sujet | Référence courte |
|---|---|
| État de référence | `RNCP6_SOURCE_DE_VERITE.md` |
| Preuves finales | `18_PREUVES_RNCP_FINALES.md`, `BE-RNCP-01-final-evidence-index.txt` |
| Mapping | `23_MAPPING_C1_C11.md` |
| Plan | `24_PLAN_PREUVES.md` |
| Projet principal | `19_DOSSIER_PROJET_FINAL.md` |
| Sécurité/ADMIN | `BE-ADMIN-01-rbac-wallet-moderation-media.txt` |
| Déploiement | `BE-DEP-03-railway-media-volume-permissions.txt` |
| Recette | `BE-E2E-01-production-recipe.txt` |
| Qualité/veille | `BE-QUAL-01-conformite-qualite.txt`, `BE-VEILLE-01-veille-technologique.txt` |

Ces références permettent au jury d'accéder aux détails sans recopier le
Dossier Projet ni multiplier les annexes dans le corps professionnel.
