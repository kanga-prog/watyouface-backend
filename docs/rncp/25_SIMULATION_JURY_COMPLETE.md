# WatYouFace — Simulation jury complète

> Conducteur d'entraînement pour Kouakou Brice Kanga, certification CDA RNCP
> niveau 6. Référentiel factuel : Dossiers Projet et Professionnel, support de
> présentation, entretien technique, questionnaire et entretien final.
>
> Les validations fonctionnelles production peuvent être citées comme telles ;
> une capture ou une trace non archivée reste PARTIAL. Ne jamais afficher de
> secret. WatYouFace est le projet principal ; PurpleWatch est un projet solo
> complémentaire.

## Repères factuels à garder en tête

- Backend Maven : 104 tests rapportés, 0 échec, 0 erreur ; 5 tests PostgreSQL conditionnels skipped.
- PostgreSQL dédié : 5/5 exécutés, 0 skipped, 0 échec, 0 erreur.
- Frontend : 47/47 ; build Vite PASS ; lint 0 erreur, 1 warning documenté.
- CI backend : run 39165116209, Maven + Docker, SUCCESS ; aucun CD Railway automatique.
- CI/CD frontend : run 39165084863, tests/build/rsync/smoke Hostinger, SUCCESS.
- Production : frontend Hostinger, backend et PostgreSQL Railway, volume média persistant.
- RGPD PARTIAL/GAP, RGAA PARTIAL, RGESN PARTIAL.
- Backup/restore non testé ; rollback BDD non testé ; observabilité limitée.
- Upload Feed/Marketplace : JPEG/PNG/GIF/BMP, 10 Mo, décodage réel, normalisation JPEG. Captures production des formats à ajouter.
- WebP, AVIF, HEIC/HEIF non supportés ; avatar est un flux séparé à harmoniser.

---

# A. Soutenance projet

## A1. Déroulé oral de 20 minutes

Ce conducteur reprend les 15 slides du support. Les preuves existantes doivent
être ouvertes avant la soutenance. Une preuve indiquée « à archiver » ne doit
pas être présentée comme une capture déjà disponible.

### Slide 1 — WatYouFace (0:00–0:45)

- **Message principal :** application full-stack et fil conducteur conception → sécurité → preuve → recul.
- **Ouverture :** « Je vais vous présenter WatYouFace, puis expliquer les choix qui m'ont permis de sécuriser et déployer le projet. »
- **À développer :** nom, identité, titre préparé, plan de récit.
- **Transition :** « Je commence par le besoin auquel répond le produit. »
- **Preuve montrable :** titre du support ; logo uniquement s'il est disponible.
- **Risque de question :** « Quelle part avez-vous réalisée personnellement ? » Répondre avec des actions concrètes, sans parler d'équipe fictive.

### Slide 2 — Contexte et besoin (0:45–1:35)

- **Message principal :** réunir interactions sociales, messagerie et vente.
- **Ouverture :** « Le besoin était de regrouper plusieurs usages autour d'une identité et de contrôles communs. »
- **À développer :** Feed, Chat, Marketplace, wallet simulé ; préciser que les trois piliers sont de poids produit équivalent.
- **Transition :** « Ce besoin m'a conduit à définir des objectifs applicatifs et de sécurité. »
- **Preuve montrable :** capture frontend Feed existante dans evidence/frontend.
- **Risque de question :** « Est-ce une vraie plateforme de paiement ? » Non, le wallet est une simulation métier.

### Slide 3 — Objectifs (1:35–2:30)

- **Message principal :** développer, sécuriser, tester et mettre en production un produit web.
- **Ouverture :** « J'ai visé une séparation nette entre interface, règles métier et données. »
- **À développer :** REST, temps réel, transitions Marketplace, production.
- **Transition :** « Voici la structure qui porte ces objectifs. »
- **Preuve montrable :** exigences et synthèse du dossier projet.
- **Risque de question :** « Quelles exigences ont été validées par des utilisateurs ? » Ne pas revendiquer une validation formelle non documentée.

### Slide 4 — Architecture (2:30–3:50)

- **Message principal :** le backend porte sécurité et logique métier.
- **Ouverture :** « React/Vite communique avec Spring Boot en REST et STOMP/WebSocket. »
- **À développer :** Spring Security, Controllers, Services, Repositories JPA, PostgreSQL ; expliquer la responsabilité de chaque couche.
- **Transition :** « Cette architecture s'appuie sur une stack connue et cohérente avec le projet. »
- **Preuve montrable :** schéma de architecture du support / dossier.
- **Risque de question :** « Pourquoi pas des microservices ? » Le monolithe en couches reste proportionné au périmètre et à l'exploitation actuels.

### Slide 5 — Stack (3:50–4:40)

- **Message principal :** choix de technologies soutenant les besoins.
- **Ouverture :** « J'ai conservé Java 17 pour la stabilité et PostgreSQL pour les transactions relationnelles. »
- **À développer :** React 19.1/Vite 7.3.7 ; Java 17/Spring Boot 3.3.5 ; JPA/Hibernate 6.5.3 ; Flyway 10.10.0 ; Docker/GitHub Actions.
- **Transition :** « Cette stack sert les modules fonctionnels suivants. »
- **Preuve montrable :** extrait de documentation ou fichiers de versions, sans configuration sensible.
- **Risque de question :** « Pourquoi ces versions ? » Stabilité et compatibilité retenues ; Java 21 est une évolution à étudier, pas une migration faite.

### Slide 6 — Fonctionnalités (4:40–5:40)

- **Message principal :** trois piliers fonctionnels et administration.
- **Ouverture :** « Le Feed, le Chat et la Marketplace utilisent une identité commune. »
- **À développer :** une phrase par module ; wallet de démonstration ; médias.
- **Transition :** « Le cycle Marketplace illustre particulièrement bien les règles métier. »
- **Preuve montrable :** maximum trois captures UI déjà archivées Feed/Chat/Marketplace.
- **Risque de question :** « Le chat annonce est-il lié techniquement à chaque listing ? » Ne pas inventer une relation non démontrée.

### Slide 7 — Cycle Marketplace (5:40–7:00)

- **Message principal :** les transitions sont conditionnées par état et acteur.
- **Ouverture :** « Chaque transition a un acteur et une précondition vérifiés côté backend. »
- **À développer :** AVAILABLE → PENDING → ACCEPTED → PAID → SHIPPED → RECEIVED ; PENDING → REFUSED ; insuffisance = 409.
- **Transition :** « La sécurité de ce parcours repose sur des contrôles serveur. »
- **Preuve montrable :** matrice des transitions ; les traces E2E production restent à archiver.
- **Risque de question :** double paiement/concurrence ; expliquer transaction, contraintes et tests ciblés, sans revendiquer un test de charge industriel.

### Slide 8 — Sécurité (7:00–8:20)

- **Message principal :** sécurité intégrée aux contrôles serveur et aux entrées.
- **Ouverture :** « Je ne considère pas le frontend comme une frontière de sécurité. »
- **À développer :** JWT HttpOnly, Secure en production, SameSite=Lax, CSRF, CORS explicite, RBAC, ownership/BOLA, validation média.
- **Transition :** « L'espace ADMIN illustre la séparation des privilèges. »
- **Preuve montrable :** matrice de sécurité/tests ; ne montrer aucun cookie ou token.
- **Risque de question :** « L'application est-elle parfaitement sécurisée ? » Non ; citer limites et preuves ciblées.

### Slide 9 — ADMIN et modération (8:20–9:25)

- **Message principal :** ADMIN modère et crédite le wallet, sans devenir acheteur/vendeur.
- **Ouverture :** « J'ai limité l'ADMIN à ses fonctions de modération et de gestion du wallet démo. »
- **À développer :** un seul compte ADMIN opérationnel demo ; rôle non mutable ; dernier admin protégé ; Feed ; listing engagé hidden=true.
- **Transition :** « Ces règles ont été testées à plusieurs niveaux. »
- **Preuve montrable :** BE-ADMIN-01 ; captures UI Admin à archiver.
- **Risque de question :** « Pourquoi un seul ADMIN ? » C'est le périmètre opérationnel courant et un risque de gouvernance à adresser en exploitation.

### Slide 10 — Tests (9:25–10:35)

- **Message principal :** citer les résultats exacts et leurs portées.
- **Ouverture :** « Je distingue la suite générale du run PostgreSQL réellement exécuté. »
- **À développer :** 104 rapportés, 5 PG conditionnels skipped ; PG dédié 5/5 ; frontend 47/47 ; build PASS ; lint 0 erreur/1 warning.
- **Transition :** « La CI reprend une partie de ces contrôles à chaque changement. »
- **Preuve montrable :** logs textuels ou run CI archivés.
- **Risque de question :** « Pourquoi pas 104/104 exécutés ? » Expliquer les 5 tests conditionnels ignorés.

### Slide 11 — CI/CD et production (10:35–11:50)

- **Message principal :** frontend automatisé ; backend CI automatisée et déploiement manuel contrôlé.
- **Ouverture :** « Les deux chaînes de livraison ont des niveaux d'automatisation différents. »
- **À développer :** Hostinger Actions → tests → Vite build → rsync → smoke ; backend Maven/Docker CI → Railway manuel.
- **Transition :** « Ces décisions sont nées de difficultés concrètes. »
- **Preuve montrable :** runs 39165084863 et 39165116209 ; vérifier les URLs avant présentation.
- **Risque de question :** « Pourquoi pas de CD backend ? » Choix de prudence avant soutenance ; pas de CD revendiqué.

### Slide 12 — Difficultés et arbitrages (11:50–13:20)

- **Message principal :** diagnostic, décision fondée sur preuve, résultat.
- **Ouverture :** « Quatre incidents ont fait évoluer mes décisions. »
- **À développer :** WebP/ImageIO ; Git natif Hostinger ; migrations non vides ; BOLA/IDOR.
- **Transition :** « La recette production montre ce qui a été validé et ce qui reste à archiver. »
- **Preuve montrable :** BE-DATA-03/BE-ADMIN-01, CI frontend, tests image.
- **Risque de question :** « Quelle erreur personnelle ? » WebP annoncé sans décodage fiable, puis contrat corrigé et méthode changée.

### Slide 13 — Recette E2E (13:20–14:45)

- **Message principal :** fonctionnalité validée ne signifie pas toujours capture archivée.
- **Ouverture :** « Les parcours ont été validés fonctionnellement ; je qualifie séparément la preuve visuelle. »
- **À développer :** cycle nominal/refus, 409, crédit ADMIN, modération, smoke 401/CSRF 200 ; captures restant à archiver.
- **Transition :** « Ce niveau de preuve m'amène à présenter les limites d'exploitation. »
- **Preuve montrable :** BE-E2E-01 et smoke textuel ; captures production uniquement si réellement présentes.
- **Risque de question :** « Où sont les traces ? » Dire précisément ce qui est textuel et ce qui est encore à ajouter.

### Slide 14 — Limites et pistes (14:45–16:20)

- **Message principal :** reconnaître les limites d'exploitation.
- **Ouverture :** « Le projet fonctionne en production, mais je ne revendique pas une industrialisation complète. »
- **À développer :** backup/restore, rollback réel, observabilité, révocation JWT, scan média, rate limiter mono-instance, conformité PARTIAL, Flyway/PG18.
- **Transition :** « Je termine par ce que le projet démontre sur ma démarche CDA. »
- **Preuve montrable :** 15_CONFORMITE_QUALITE.md et source de vérité.
- **Risque de question :** « N'est-ce pas bloquant ? » Expliquer périmètre soutenance et priorité future, sans minimiser l'impact.

### Slide 15 — Conclusion (16:20–20:00)

- **Message principal :** concevoir, développer, sécuriser, tester, déployer et apprendre.
- **Ouverture :** « WatYouFace m'a permis de relier le développement full-stack à la sécurité applicative. »
- **À développer :** acquis ; preuves ; évolution vers stage AppSec/cyber/SOC junior ; ouverture questions.
- **Transition :** « Je suis prêt à détailler un choix technique ou une preuve. »
- **Preuve montrable :** mapping C1-C11 et index de preuves.
- **Risque de question :** compétence la moins aboutie ; répondre observabilité/opérations et expliquer le plan.

### Vérification du timing

Les intervalles de slide totalisent 20 minutes ; garder les réponses complémentaires
pour la séquence questions, pas dans la présentation initiale.

## A2. Présentation de secours en 10 minutes

| Temps | Contenu | À dire |
|---|---|---|
| 0:00–0:45 | Besoin | Réunir Feed, Chat et Marketplace avec une identité commune. |
| 0:45–2:00 | Architecture | React/Vite → REST/STOMP → Spring Security → couches Spring → PostgreSQL. |
| 2:00–3:30 | Produit / cycle | Montrer modules et expliquer le cycle Marketplace + rôles. |
| 3:30–5:00 | Sécurité | JWT cookie, CSRF, RBAC, ownership/BOLA, upload réel. |
| 5:00–6:15 | ADMIN | Modération, wallet démo, hidden sur annonce engagée. |
| 6:15–7:30 | Tests et CI | Chiffres exacts ; frontend automatique, backend Railway manuel. |
| 7:30–8:45 | Difficulté | WebP ou Flyway base non vide avec démarche décisionnelle. |
| 8:45–10:00 | Limites/conclusion | Backup, observabilité, conformité partielle et projet professionnel. |

---

# B. Questions du jury après soutenance

## B1. Questions projet générales (30)

Pour chacune : commencer par la réponse courte, ajouter le détail si demandé,
citer la preuve indiquée, puis nommer une limite. Les preuves indiquées sont
des références de dossier ; elles ne garantissent pas qu'une capture graphique
soit archivée.

### Q1 — Quel besoin WatYouFace adresse-t-il ?

- **Courte :** réunir usages sociaux, conversation et vente dans une application avec contrôle commun.
- **Détaillée :** Feed, Chat et Marketplace sont les trois piliers ; l'ADMIN est transversal.
- **Preuve :** dossier projet, besoin et périmètre.
- **À éviter :** prétendre résoudre tous les problèmes des réseaux sociaux.

### Q2 — Pourquoi avoir choisi ce projet ?

- **Courte :** il permet de travailler un produit full-stack avec des règles métier et de sécurité concrètes.
- **Détaillée :** il combine interface, API, données, temps réel, transaction simulée et déploiement.
- **Preuve :** Dossier Projet et Dossier Professionnel.
- **À éviter :** dire qu'il constitue un système bancaire ou une plateforme commerciale industrielle.

### Q3 — Quelle est l'architecture ?

- **Courte :** React/Vite appelle Spring Boot en REST/STOMP ; les couches backend accèdent à PostgreSQL.
- **Détaillée :** Spring Security filtre, controllers transportent HTTP, services portent les règles, repositories JPA persistent.
- **Preuve :** schéma PRV-03.
- **À éviter :** oublier que le backend est l'autorité.

### Q4 — Pourquoi séparer frontend et backend ?

- **Courte :** séparer présentation et règles serveur, et permettre à l'API d'appliquer les mêmes contrôles à tout client.
- **Détaillée :** le navigateur reste modifiable ; RBAC et ownership sont contrôlés côté backend.
- **Preuve :** matrice sécurité et tests.
- **À éviter :** considérer les routes React comme des permissions.

### Q5 — Pourquoi Spring Boot ?

- **Courte :** il fournit une structure intégrée pour API, sécurité, persistance, configuration et tests.
- **Détaillée :** il permet une architecture Java en couches adaptée au périmètre.
- **Preuve :** stack et code documentés.
- **À éviter :** le présenter comme forcément meilleur que toutes les alternatives.

### Q6 — Pourquoi React/Vite ?

- **Courte :** React compose les interfaces ; Vite fournit le serveur et le build frontend.
- **Détaillée :** la séparation composants facilite les écrans Feed, Chat et Marketplace.
- **Preuve :** tests frontend et build.
- **À éviter :** confondre bundling et sécurité.

### Q7 — Quel est le rôle de PostgreSQL ?

- **Courte :** persister les relations, contraintes et transactions du produit.
- **Détaillée :** utilisé en production Railway ; les tests dédiés valident un périmètre sur PostgreSQL réel.
- **Preuve :** BE-PAY-01 ; résultats 5/5.
- **À éviter :** dire que H2 reproduit parfaitement PostgreSQL.

### Q8 — Quel est le rôle de Flyway ?

- **Courte :** appliquer et tracer les évolutions de schéma versionnées.
- **Détaillée :** V1 baseline, V2 created_at, V3 hidden ; Hibernate validate ensuite.
- **Preuve :** BE-DATA-03 et BE-ADMIN-01.
- **À éviter :** promettre un rollback automatique.

### Q9 — Pourquoi le cycle Marketplace est-il une machine à états ?

- **Courte :** chaque transition dépend de l'état et de l'acteur.
- **Détaillée :** demande acheteur, décision vendeur, paiement acheteur, expédition vendeur, réception acheteur.
- **Preuve :** matrice transitions et résultats recette.
- **À éviter :** attribuer une transition au mauvais acteur.

### Q10 — Que signifie le 409 de solde insuffisant ?

- **Courte :** l'opération est invalide au regard de l'état métier courant.
- **Détaillée :** le paiement est refusé avant validation d'une écriture incohérente.
- **Preuve :** recette de production et tests.
- **À éviter :** appeler cela une erreur serveur 500.

### Q11 — À quoi sert le wallet ?

- **Courte :** à simuler les règles de paiement Marketplace.
- **Détaillée :** le crédit manuel est réservé ADMIN ; la simulation n'est pas un produit bancaire.
- **Preuve :** BE-ADMIN-01.
- **À éviter :** parler de paiement réel ou de prestataire PSP.

### Q12 — Pourquoi l'ADMIN ne peut-il pas accepter ou payer ?

- **Courte :** il modère, mais ne remplace pas acheteur et vendeur.
- **Détaillée :** un bypass ADMIN détruirait la cohérence des rôles et transitions.
- **Preuve :** tests RBAC et Marketplace.
- **À éviter :** décrire l'ADMIN comme super-utilisateur métier.

### Q13 — Pourquoi masquer une annonce engagée ?

- **Courte :** préserver l'historique transactionnel tout en la cachant aux tiers.
- **Détaillée :** hidden=true sur transaction engagée ; suppression physique des états non engagés prévus.
- **Preuve :** tests et recette modération.
- **À éviter :** prétendre que toute annonce est supprimée physiquement.

### Q14 — Comment protégez-vous contre BOLA/IDOR ?

- **Courte :** vérifier l'acteur et l'ownership côté backend pour chaque ressource/action.
- **Détaillée :** un identifiant fourni par client ne prouve jamais l'autorisation.
- **Preuve :** security-test-matrix et tests Marketplace.
- **À éviter :** se limiter à cacher le bouton.

### Q15 — Pourquoi JWT dans un cookie HttpOnly ?

- **Courte :** limiter la lecture du jeton par JavaScript et transporter l'identité par cookie.
- **Détaillée :** Secure en production, SameSite=Lax et CSRF sur mutations ; pas de révocation centrale.
- **Preuve :** matrice sécurité et smoke auth.
- **À éviter :** dire que HttpOnly élimine tout risque XSS ou CSRF.

### Q16 — Comment protégez-vous les uploads ?

- **Courte :** taille, MIME, décodage réel, nom serveur, temporaire et nettoyage.
- **Détaillée :** 10 Mo, JPEG/PNG/GIF/BMP, sortie JPEG ; captures production restant à ajouter.
- **Preuve :** tests media et BE-API-02.
- **À éviter :** annoncer WebP/AVIF/HEIC ou antivirus.

### Q17 — Pourquoi WebP n'est-il pas accepté ?

- **Courte :** la pile de décodage ne le traitait pas fiablement.
- **Détaillée :** réduire le contrat aux formats réellement décodés évite des erreurs serveur.
- **Preuve :** tests d'upload.
- **À éviter :** présenter le refus comme une incapacité future permanente.

### Q18 — Comment traitez-vous les GIF ?

- **Courte :** première frame normalisée en JPEG.
- **Détaillée :** l'animation n'est pas conservée dans le pipeline Feed/Marketplace.
- **Preuve :** tests image réels.
- **À éviter :** promettre une animation conservée.

### Q19 — Comment testez-vous le backend ?

- **Courte :** tests Maven, tests PostgreSQL dédiés et contrôles de sécurité.
- **Détaillée :** 104 rapportés avec 5 PG conditionnels skipped ; run PG dédié 5/5 réel.
- **Preuve :** logs tests.
- **À éviter :** dire 104/104 exécutés sur PostgreSQL.

### Q20 — Comment testez-vous le frontend ?

- **Courte :** 47 tests PASS, build Vite PASS, lint sans erreur avec un warning documenté.
- **Détaillée :** ces tests ne remplacent pas une recette visuelle complète.
- **Preuve :** preuves frontend Phase 15 et CI.
- **À éviter :** annoncer zéro warning.

### Q21 — Que prouve la CI backend ?

- **Courte :** Maven tests et build Docker sur GitHub Actions.
- **Détaillée :** run 39165116209 SUCCESS ; il ne déploie pas Railway.
- **Preuve :** lien du run.
- **À éviter :** dire CD backend automatisé.

### Q22 — Que prouve la CI frontend ?

- **Courte :** tests, build Vite, rsync vers Hostinger et smoke test.
- **Détaillée :** run 39165084863 SUCCESS.
- **Preuve :** lien du run.
- **À éviter :** dire que Git natif Hostinger est actif.

### Q23 — Pourquoi déployer le backend manuellement ?

- **Courte :** garder un contrôle de mise en production avant d'automatiser davantage.
- **Détaillée :** CI Maven/Docker automatique, Railway manuel contrôlé.
- **Preuve :** workflow backend et historique déploiement.
- **À éviter :** présenter le manuel comme CD.

### Q24 — Quelles difficultés ont été formatrices ?

- **Courte :** WebP, déploiement Hostinger, migrations base non vide et accès BOLA/IDOR.
- **Détaillée :** chaque fois, diagnostic, test, décision documentée et vérification.
- **Preuve :** preuves Phase 13, BE-DATA-01 et matrice sécurité.
- **À éviter :** faire passer une décision pour une fonctionnalité supplémentaire.

### Q25 — Le projet est-il sécurisé ?

- **Courte :** des protections et tests ciblés sont en place ; je ne revendique pas une sécurité absolue.
- **Détaillée :** cookies, CSRF, CORS, RBAC, ownership, validations ; limites révocation JWT, rate limiting, AV et observabilité.
- **Preuve :** security-test-matrix.
- **À éviter :** dire « c'est sécurisé » sans préciser le périmètre.

### Q26 — Êtes-vous conforme RGPD/RGAA ?

- **Courte :** non, conformité complète non revendiquée ; état PARTIAL/GAP.
- **Détaillée :** mesures ciblées, mais politique, rétention, droits, audit formel et certification à compléter.
- **Preuve :** 15_CONFORMITE_QUALITE.md.
- **À éviter :** confondre sécurité technique et conformité juridique.

### Q27 — Que manque-t-il en exploitation ?

- **Courte :** backup/restore testé, observabilité, rollback BDD et automatisation backend.
- **Détaillée :** ajouter aussi antivirus/quarantaine, révocation JWT centrale et audit accessibilité complet.
- **Preuve :** source de vérité, plan de preuves.
- **À éviter :** dire que ces éléments existent déjà.

### Q28 — Comment abordez-vous le travail en équipe ?

- **Courte :** WatYouFace est un projet personnel ; ma méthode Git/documentation/revue est transposable en équipe.
- **Détaillée :** tickets, branches, commits, communication technique et demande de revue.
- **Preuve :** historique Git et documents.
- **À éviter :** inventer une équipe projet.

### Q29 — Quel est votre projet professionnel ?

- **Courte :** stage en cybersécurité, AppSec, développement sécurisé ou SOC junior.
- **Détaillée :** consolider l'expérience, puis développer un profil hybride.
- **Preuve :** Dossier Professionnel.
- **À éviter :** présenter plusieurs métiers comme une expertise déjà acquise.

### Q30 — Qu'avez-vous appris de votre démarche RNCP ?

- **Courte :** une décision doit être testée et sa portée prouvée.
- **Détaillée :** distinguer comportement fonctionnel, preuve archivée et limite a amélioré mes arbitrages.
- **Preuve :** sources de vérité, preuves tests et veille.
- **À éviter :** confondre documentation abondante et validation.

---

# C. Questions techniques difficiles (20)

Pour s'entraîner : répondre en 60 à 90 secondes. La réponse doit distinguer
mécanisme actuel, preuve et évolution, sans prétendre que l'évolution existe déjà.

### D1 — Pourquoi JWT en cookie plutôt qu'en localStorage ?

- **Réponse :** HttpOnly limite la lecture du token par JavaScript ; localStorage serait accessible à un script injecté.
- **Détail :** un cookie implique CSRF, donc le projet ajoute un token anti-CSRF et des attributs Secure/SameSite.
- **Preuve :** matrice sécurité.
- **Piège :** dire que cookie élimine XSS ou CSRF.

### D2 — Pourquoi CSRF existe-t-il encore avec JWT ?

- **Réponse :** le navigateur joint automatiquement le cookie ; la signature du JWT ne prouve pas l'intention de l'utilisateur.
- **Détail :** le token CSRF protège les mutations, indépendamment de l'intégrité JWT.
- **Preuve :** GET /api/auth/csrf 200 observé.
- **Piège :** confondre authentification et intention de requête.

### D3 — Pourquoi CORS ne protège-t-il pas curl/Postman ?

- **Réponse :** CORS est une règle exécutée par les navigateurs.
- **Détail :** les clients non navigateur peuvent appeler l'API ; auth, RBAC et ownership restent obligatoires.
- **Preuve :** règles serveur et tests 401/403.
- **Piège :** présenter CORS comme firewall.

### D4 — Comment empêcher une double transaction Marketplace ?

- **Réponse :** vérifier acteur/état côté backend, encadrer les écritures en transaction et s'appuyer sur contraintes d'intégrité.
- **Détail :** les tests PostgreSQL dédiés couvrent un périmètre de paiement, unicité et rollback ; pas de test de charge industriel.
- **Preuve :** PostgresPaymentIntegrationTests 5/5.
- **Piège :** garantir l'absence absolue de course sans détailler le verrouillage testé.

### D5 — Que se passe-t-il si deux acheteurs paient en même temps ?

- **Réponse :** l'application doit sérialiser ou rejeter l'opération en conflit afin qu'une seule transition cohérente soit validée.
- **Détail :** transactions et contraintes protègent le périmètre testé ; je ne prétends pas avoir démontré la tenue à toute concurrence de production.
- **Preuve :** tests PostgreSQL dédiés.
- **Piège :** affirmer « impossible » sans mesure de charge.

### D6 — Comment garantir l'atomicité du wallet ?

- **Réponse :** regrouper les lectures/validations et écritures concernées dans une transaction, refuser le montant non positif et laisser le rollback annuler l'ensemble en cas d'échec.
- **Détail :** crédit manuel réservé à l'ADMIN ; paiement Marketplace est une opération métier distincte.
- **Preuve :** tests wallet et PostgreSQL.
- **Piège :** croire que la validation UI garantit l'atomicité.

### D7 — Que se passe-t-il si Flyway échoue en production ?

- **Réponse :** le démarrage échoue plutôt que de lancer l'application avec un schéma non validé.
- **Détail :** examiner logs et version échouée, préserver les données, préparer une migration corrective forward ; ne pas supprimer l'historique arbitrairement.
- **Preuve :** stratégie Flyway documentée.
- **Piège :** relancer aveuglément ou basculer ddl-auto update.

### D8 — Comment restaurer la base ?

- **Réponse :** la restauration réelle n'est pas encore testée, donc je ne prétends pas avoir un runbook validé.
- **Détail :** procédure à éprouver : pg_dump, restauration isolée via pg_restore/psql, vérification applicative et sauvegarde média cohérente.
- **Preuve :** GAP déclaré dans conformité.
- **Piège :** confondre possibilité technique et preuve de restauration.

### D9 — Que faire si Railway perd le volume média ?

- **Réponse :** le volume persistant limite la perte liée au redémarrage, mais sa sauvegarde indépendante n'est pas démontrée.
- **Détail :** inventorier les médias, restaurer une sauvegarde éprouvée et vérifier références DB/fichiers ; cette capacité reste à mettre en place.
- **Preuve :** BE-DEP-03 prouve persistance après redeploy, pas backup.
- **Piège :** dire que « volume persistant = sauvegarde ».

### D10 — Comment scaler le rate limiter ?

- **Réponse :** le limiter in-memory est valable pour une instance ; en horizontal, utiliser un état partagé ou limiteur distribué.
- **Détail :** Redis est une piste à évaluer avec politiques de clés, fenêtres, expiration et tolérance panne.
- **Preuve :** audit qualité et veille.
- **Piège :** dire Redis déjà en place.

### D11 — Comment révoquer immédiatement un JWT ?

- **Réponse :** ce n'est pas possible centralement dans l'état actuel.
- **Détail :** options : blacklist avec TTL, version de token par utilisateur ou session opaque ; choisir après analyse disponibilité/coût.
- **Preuve :** limitation source de vérité.
- **Piège :** prétendre que supprimer le cookie révoque une copie volée.

### D12 — Comment sécuriser STOMP ?

- **Réponse :** authentifier la connexion et contrôler l'appartenance/autorisation des destinations et messages côté serveur.
- **Détail :** le projet contrôle l'appartenance dans le périmètre audité, mais ne possède pas de couverture E2E STOMP exhaustive.
- **Preuve :** BE-STOMP-01 et matrice sécurité.
- **Piège :** dire que la connexion WebSocket rend automatiquement les destinations sûres.

### D13 — Comment détecter un fichier malveillant avec MIME correct ?

- **Réponse :** le MIME ne suffit pas ; le contenu est décodé réellement et limité en taille, mais il n'y a pas d'antivirus.
- **Détail :** une évolution serait quarantaine et scan asynchrone ; ce n'est pas présent.
- **Preuve :** tests upload et BE-API-02.
- **Piège :** prétendre que le décodage garantit l'absence de malware.

### D14 — Pourquoi hidden=true plutôt que suppression ?

- **Réponse :** pour préserver une transaction engagée tout en retirant l'annonce du catalogue public.
- **Détail :** les tiers ne voient plus le listing ; vendeur, acheteur et ADMIN gardent l'accès nécessaire.
- **Preuve :** tests et recette de modération.
- **Piège :** décrire la suppression physique d'un paiement historique.

### D15 — Comment améliorer l'observabilité ?

- **Réponse :** aujourd'hui logs applicatifs/plateforme et CI ; métriques et alertes centralisées ne sont pas démontrées.
- **Détail :** ajouter health checks, Actuator/Micrometer, collecte de métriques, dashboards et alertes sur disponibilité/erreurs.
- **Preuve :** audit conformité.
- **Piège :** prétendre que les logs seuls constituent une supervision complète.

### D16 — Comment rollbacker une migration ?

- **Réponse :** pas de rollback automatique prouvé ; privilégier correction forward et backup vérifié avant migration critique.
- **Détail :** restaurer la base uniquement selon procédure testée, avec compatibilité applicative étudiée.
- **Preuve :** GAP rollback et backup.
- **Piège :** supprimer une ligne Flyway ou rejouer un script sans analyse.

### D17 — Pourquoi PostgreSQL plutôt que MySQL ?

- **Réponse :** PostgreSQL correspond au choix relationnel et à l'environnement déjà utilisé ; l'application et ses migrations sont validées sur lui.
- **Détail :** je ne prétends pas qu'il soit toujours supérieur ; changer de moteur demanderait tests de types, SQL, contraintes et migrations.
- **Preuve :** tests PostgreSQL.
- **Piège :** critiquer MySQL sans besoin mesuré.

### D18 — Pourquoi Docker ?

- **Réponse :** figer un environnement reproductible et séparer compilation Maven du runtime JRE.
- **Détail :** l'image exécute Java en non-root ; Docker ne remplace ni sécurité applicative ni sauvegarde.
- **Preuve :** run CI backend, BE-DEP-03.
- **Piège :** présenter container et VM comme identiques.

### D19 — Pourquoi Spring Boot ?

- **Réponse :** intégration cohérente pour REST, Spring Security, JPA, configuration et tests Java.
- **Détail :** le choix réduit le câblage manuel et reste adapté au monolithe en couches.
- **Preuve :** architecture et CI.
- **Piège :** dire que le framework sécurise automatiquement tout code métier.

### D20 — Pourquoi React/Vite ?

- **Réponse :** React structure l'interface en composants et Vite fournit un cycle de développement/build rapide.
- **Détail :** les tests et le bundle sont mesurés ; les optimisations et audits d'accessibilité restent partiels.
- **Preuve :** suite frontend et build.
- **Piège :** confondre rapidité de build avec performance complète de l'application.

---

# D. Questionnaire rapide (30 questions)

Répondre en 20 à 30 secondes chacune, sans lire. Mélange HTTP, Java, SQL,
sécurité, Git, Docker, CI/CD, architecture, RGPD et RGAA.

1. **Q :** Différence entre 401 et 403 ?
   **Réponse attendue :** 401 sans identité valide ; 403 identité authentifiée sans droit.

2. **Q :** Quel code pour une création réussie ?
   **Réponse attendue :** 201 Created, si le contrat indique une ressource créée.

3. **Q :** Qu'est-ce qu'une exception Java ?
   **Réponse attendue :** Un signal d'erreur ou situation particulière remonté puis traité par le programme.

4. **Q :** Qu'est-ce qu'une clé étrangère ?
   **Réponse attendue :** Une contrainte reliant une ligne à une clé d'une autre table.

5. **Q :** BCrypt chiffre-t-il un mot de passe ?
   **Réponse attendue :** Non, il produit un hash lent et non réversible destiné à la vérification.

6. **Q :** Qu'est-ce qu'une transaction ACID ?
   **Réponse attendue :** Unité de travail visant atomicité, cohérence, isolation et durabilité.

7. **Q :** Différence entre commit et push ?
   **Réponse attendue :** Commit enregistre localement ; push transmet les commits au dépôt distant.

8. **Q :** Que fait git fetch ?
   **Réponse attendue :** Récupère les références distantes sans intégrer automatiquement leurs changements dans la branche courante.

9. **Q :** Qu'est-ce qu'une image Docker ?
   **Réponse attendue :** Un modèle construit à partir duquel un ou plusieurs containers peuvent être lancés.

10. **Q :** À quoi sert un volume Docker ?
    **Réponse attendue :** À conserver des données au-delà du cycle de vie du container, selon son montage et sa plateforme.

11. **Q :** CI versus CD ?
    **Réponse attendue :** CI automatise intégration/tests/build ; CD automatise livraison ou déploiement.

12. **Q :** Quel est le niveau d'automatisation du backend WatYouFace ?
    **Réponse attendue :** CI Maven/Docker automatique ; déploiement Railway manuel contrôlé.

13. **Q :** Qu'est-ce qu'un DTO ?
    **Réponse attendue :** Un objet contrat d'échange qui limite et structure les données API.

14. **Q :** À quoi sert un Repository ?
    **Réponse attendue :** À encapsuler l'accès persistant aux données.

15. **Q :** CORS protège-t-il une API contre curl ?
    **Réponse attendue :** Non, CORS est appliqué par les navigateurs.

16. **Q :** Pourquoi CSRF avec cookie HttpOnly ?
    **Réponse attendue :** Le navigateur envoie le cookie automatiquement ; le token CSRF protège les mutations.

17. **Q :** Définis BOLA.
    **Réponse attendue :** Défaut d'autorisation au niveau d'un objet API, permettant accès/action non autorisé.

18. **Q :** Qui peut créditer manuellement un wallet WatYouFace ?
    **Réponse attendue :** L'ADMIN via l'endpoint protégé ; montant positif.

19. **Q :** Pourquoi le listing engagé est-il masqué ?
    **Réponse attendue :** Pour modérer sa visibilité sans casser son historique transactionnel.

20. **Q :** Quels formats Feed/Marketplace ?
    **Réponse attendue :** JPEG, PNG, GIF et BMP, maximum 10 Mo, décodage réel.

21. **Q :** Quelle est la sortie du traitement GIF ?
    **Réponse attendue :** JPEG fixe de la première frame, animation non conservée.

22. **Q :** Qu'est-ce que Flyway ?
    **Réponse attendue :** Outil de migrations versionnées et appliquées dans l'ordre.

23. **Q :** PostgreSQL est-il interchangeable avec H2 ?
    **Réponse attendue :** Non ; comportements et dialectes peuvent différer.

24. **Q :** Quel statut pour solde insuffisant ?
    **Réponse attendue :** 409 Conflict dans la recette WatYouFace.

25. **Q :** Que mesure un test E2E ?
    **Réponse attendue :** Un parcours de bout en bout à travers les composants du système.

26. **Q :** Quel attribut donne un nom à un champ de formulaire ?
    **Réponse attendue :** Un label associé ; aria-label peut compléter quand aucun label visible ne convient.

27. **Q :** Qu'est-ce que la minimisation RGPD ?
    **Réponse attendue :** Limiter collecte et exposition aux données nécessaires à la finalité.

28. **Q :** Quel est le statut d'audit RGAA ?
    **Réponse attendue :** PARTIAL ; des corrections ciblées, pas d'audit complet.

29. **Q :** Pourquoi utiliser un compte Docker non-root ?
    **Réponse attendue :** Réduire les privilèges et l'impact potentiel d'une compromission.

30. **Q :** Backup/restore a-t-il été testé en production ?
    **Réponse attendue :** Non ; c'est un GAP à traiter, pas une preuve existante.

---

# E. Entretien final (20 questions)

Pour chaque réponse, utiliser le dossier entretien final ; les points ci-dessous
rappellent l'angle attendu et le piège.

| # | Question | Réponse attendue | Piège |
|---:|---|---|---|
| 1 | Parlez-moi de votre parcours. | Technicien forestier, reconversion numérique, Holberton full-stack, spécialisation cybersécurité, CDA. | Inventer une étape ou expérience. |
| 2 | Pourquoi cette reconversion ? | Intérêt concret pour résolution de problèmes, développement et progression vers sécurité. | « Le numérique recrute » seul. |
| 3 | Pourquoi le titre CDA ? | Démontrer conception, développement, sécurité, tests, déploiement et documentation. | Le réduire à un diplôme. |
| 4 | Comment décrivez-vous WatYouFace ? | Application sociale avec Feed, Chat, Marketplace, ADMIN, React/Spring/PostgreSQL en production. | Paiement réel ou conformité complète. |
| 5 | Qu'avez-vous fait personnellement ? | Conception, code, sécurité, tests, CI, déploiement, recette, documentation/veille. | Confondre rôle personnel et équipe. |
| 6 | Quelle difficulté vous a le plus appris ? | WebP : capacité réelle non vérifiée, contrat réduit après tests. | Prétendre support WebP. |
| 7 | Racontez une erreur. | Contrat WebP initial trop large, correction après diagnostic, ajout d'une vérification de capacité réelle. | Blâmer uniquement une bibliothèque. |
| 8 | Comment travaillez-vous seul ? | Décomposer, reproduire, logs, documentation officielle, test, documenter, vérifier. | Dire « je travaille bien seul » sans méthode. |
| 9 | Comment vous intégrez-vous à une équipe ? | Projet solo, méthode compatible : Git, tickets, revue, documentation, communication. | Inventer une collaboration passée. |
| 10 | Quelle qualité illustrez-vous ? | Rigueur, avec exemple du rapport exact des tests PostgreSQL. | Qualité sans preuve. |
| 11 | Quel axe devez-vous renforcer ? | Observabilité et backup/restore opérationnels. | « Perfectionnisme ». |
| 12 | Comment gérez-vous la critique ? | Comprendre l'exemple, évaluer le risque, ajuster ou justifier avec preuves. | Réagir défensivement. |
| 13 | Comment choisissez-vous vos priorités ? | Sécurité/intégrité d'abord, valeur et risque, effort ensuite. | Tout appeler urgent. |
| 14 | Pourquoi la cybersécurité ? | Continuité du développement : protéger utilisateurs, données et règles métier. | Réponse de mode. |
| 15 | Pourquoi AppSec ? | Intégrer sécurité dès conception et développement. | Dire que sécurité est seulement un audit final. |
| 16 | Pourquoi SOC junior ? | Intérêt pour détection/analyse et expérience complémentaire PurpleWatch solo. | Prétendre expérience SOC salariée. |
| 17 | Quel projet professionnel ? | Stage cyber/AppSec/dev sécurisé/SOC junior, progression vers profil hybride. | Promettre un poste senior. |
| 18 | Quelle limite de WatYouFace assumez-vous ? | Backup/restore non testé, observabilité limitée, CD backend manuel, conformité PARTIAL. | Minimiser le risque. |
| 19 | Pourquoi vous recruter ? | Double approche dev/cyber, pratique full-stack, sécurité, déploiement et apprentissage. | « Je sais tout ». |
| 20 | Que feriez-vous dans trois mois ? | Tester backup/restore, observabilité, audit accessibilité et scan média, par priorité. | Ajouter des features sans réduire le risque. |

---

# F. Scénarios de pression (10)

Posture commune : écouter sans interrompre, reconnaître les faits de la question,
répondre avec une preuve et une limite, proposer une suite réaliste.

### P1 — « Vous dites que l'application est sécurisée. Prouvez-le. »

- **Posture :** reformuler le périmètre, pas revendiquer une sécurité absolue.
- **Réponse :** « J'ai mis en place JWT HttpOnly, CSRF, CORS, RBAC, ownership et validations upload. J'ai des tests ciblés sur les refus et le paiement. La révocation JWT, l'antivirus et la couverture exhaustive restent des limites. »
- **Preuve :** matrice security-test-matrix, BE-ADMIN-01.
- **Piège :** dire « 100 % sécurisée ».

### P2 — « Votre RGPD est incomplet. Pourquoi valider le projet ? »

- **Posture :** accepter le constat et préciser l'évaluation technique.
- **Réponse :** « Je ne revendique pas une conformité juridique complète. Le projet démontre une démarche technique de protection et identifie les chantiers de finalité, rétention, droits et effacement. »
- **Preuve :** 15_CONFORMITE_QUALITE.md et BE-QUAL-01.
- **Piège :** confondre réussite technique et conformité réglementaire.

### P3 — « Pourquoi ne pas avoir automatisé le backend ? »

- **Posture :** expliquer l'arbitrage de risque.
- **Réponse :** « La CI Maven/Docker est automatisée. Le déploiement Railway est resté manuel et contrôlé, car ajouter tardivement un CD insuffisamment validé aurait augmenté le risque. »
- **Preuve :** run 39165116209 et documentation déploiement.
- **Piège :** appeler cela un CD partiel sans expliquer ce qui est automatique.

### P4 — « Votre backup n'est pas testé. Ce n'est pas professionnel ? »

- **Posture :** reconnaître la gravité opérationnelle.
- **Réponse :** « C'est une lacune réelle : une persistance n'est pas une sauvegarde. Je l'ai classée GAP et la prochaine étape est un dump puis une restauration isolée avec les médias. »
- **Preuve :** audit conformité / plan de preuves.
- **Piège :** prétendre que Railway effectue une sauvegarde restaurable démontrée.

### P5 — « Pourquoi garder un seul ADMIN ? »

- **Posture :** distinguer périmètre actuel et gouvernance future.
- **Réponse :** « Le compte demo est l'unique ADMIN opérationnel du périmètre. Le rôle n'est pas librement mutable et le dernier ADMIN est protégé. En exploitation réelle, je formaliserais accès de secours et gouvernance. »
- **Preuve :** tests RBAC.
- **Piège :** présenter un compte unique comme meilleure pratique universelle.

### P6 — « Pourquoi JWT si vous ne savez pas le révoquer ? »

- **Posture :** reconnaître le compromis.
- **Réponse :** « Le JWT simplifie l'API stateless, mais la révocation centrale manque. J'ai limité certains risques par expiration et cookie sécurisé ; une blacklist TTL ou une version de token serait à évaluer. »
- **Preuve :** source de vérité.
- **Piège :** dire que logout révoque un token déjà copié.

### P7 — « Flyway ne supporte pas officiellement PostgreSQL 18. Pourquoi avoir déployé ? »

- **Posture :** séparer compatibilité testée et garantie éditeur.
- **Réponse :** « Le warning Flyway 10.10.0 est conservé. Les migrations ont réussi en production et V2/V3 ont été testées sur PostgreSQL 16 non vide ; je surveille le warning et testerais toute mise à jour avant changement. »
- **Preuve :** BE-DATA-03 et BE-ADMIN-01.
- **Piège :** affirmer que toutes versions sont officiellement certifiées compatibles.

### P8 — « Votre application est-elle scalable ? »

- **Posture :** décrire l'état et limite d'architecture.
- **Réponse :** « Elle est structurée en couches et déployée, mais je ne revendique pas une scalabilité horizontale démontrée. Le rate limiter est in-memory et l'observabilité/charge restent à travailler. »
- **Preuve :** audit qualité.
- **Piège :** confondre Docker avec scalabilité.

### P9 — « Pourquoi WebP n'est-il pas supporté ? »

- **Posture :** expliquer le contrat fondé sur capacité réelle.
- **Réponse :** « La pile de décodage ne le traitait pas fiablement. J'ai préféré refuser WebP proprement et accepter JPEG/PNG/GIF/BMP validés, plutôt que produire des erreurs serveur. »
- **Preuve :** tests upload.
- **Piège :** promettre qu'une extension MIME suffit.

### P10 — « Que faites-vous si la production tombe pendant votre soutenance ? »

- **Posture :** garder le calme et basculer sur preuves archivées.
- **Réponse :** « Je signale l'indisponibilité, ne modifie pas la production à chaud, puis présente captures, logs anonymisés, résultats CI et tests locaux préparés. Le live complète les preuves ; il n'est pas leur unique source. »
- **Preuve :** dossier local avec captures réelles, CI, tests, architecture ; vérifier avant jour J.
- **Piège :** improviser une mutation de production sous pression.

---

# G. Démonstration live

## G1. Parcours de 5 à 7 minutes

Préparer une session et des données autorisées avant l'épreuve. Ne pas effectuer
de suppression ou de paiement sur des données de production non dédiées à la
démo.

| Temps | Action | Phrase |
|---|---|---|
| 0:00–0:30 | Page d'accueil | « Voici le produit et sa navigation principale. » |
| 0:30–1:00 | Connexion avec compte préparé | Ne saisir aucun secret face caméra si évitable ; utiliser une session préparée selon les règles du jury. |
| 1:00–1:40 | Feed | « Les posts et commentaires sont soumis aux droits côté serveur. » |
| 1:40–2:20 | Chat | « Le temps réel repose sur WebSocket/STOMP ; je ne prétends pas démontrer une couverture E2E exhaustive ici. » |
| 2:20–3:00 | Marketplace | Montrer un listing de démonstration et ses états. |
| 3:00–4:00 | Transaction | Expliquer la transition et l'acteur ; ne payer que sur données dédiées autorisées. |
| 4:00–5:00 | Admin | Montrer modération/wallet seulement si un jeu de démonstration sûr est prêt. |
| 5:00–6:00 | Modération | Montrer hidden ou suppression sur données préparées, sans toucher aux données réelles d'utilisateurs. |
| 6:00–7:00 | CI/déploiement | Montrer les runs et expliquer frontend automatisé / backend manuel contrôlé. |

### Vérifications avant le live

- Session et réseau vérifiés ; navigateur sans extensions intrusives.
- Comptes et données préparés avec autorisation explicite.
- Aucun secret dans presse-papiers, terminal, logs ou écran partagé.
- Aucun achat, crédit ou delete sur données utilisateur réelles.
- Captures et logs disponibles en secours.
- Si une mutation n'est pas sûre, expliquer le flux sur schéma/tests plutôt que la provoquer.

## G2. Version de démonstration en 3 minutes

1. **0:00–0:30 :** accueil et architecture produit.
2. **0:30–1:10 :** Feed ou Marketplace en lecture.
3. **1:10–1:50 :** cycle transactionnel sur capture/matrice ; pas de mutation risquée.
4. **1:50–2:20 :** Admin et hidden sur preuve préparée.
5. **2:20–3:00 :** CI frontend/backend et distinction de déploiement.

---

# H. Plan B si la production tombe

1. Annoncer factuellement l'indisponibilité et ne pas tenter de modifier la production en urgence.
2. Montrer les captures déjà archivées dans evidence/frontend ; ne pas inventer les captures production E2E/uploads qui restent à ajouter.
3. Présenter le support d'architecture et la machine d'état Marketplace.
4. Ouvrir les rapports textuels de tests : Maven, PostgreSQL dédié, frontend.
5. Montrer les runs GitHub Actions connus : 39165084863 et 39165116209.
6. Montrer la preuve Railway du volume et du runtime si elle est disponible hors secret.
7. Lancer une démonstration locale seulement si elle a été préparée et testée avant le jury.
8. Dire clairement ce que le fallback prouve : le code/tests/CI ; il ne remplace pas la disponibilité production.
9. Garder une copie locale des documents et preuves, sans données sensibles.
10. Ne pas improviser une vidéo ou une capture qui n'a jamais été réalisée.

---

# I. Checklist de preuves à ouvrir avant le jury

| Élément | Préparation | Statut/précaution |
|---|---|---|
| watyouface.com | Ouvrir l'accueil et vérifier disponibilité | Production réelle ; ne pas dépendre du live seul. |
| api.watyouface.com | Préparer le smoke documenté | 401 /api/users/me et CSRF 200 rapportés ; traces à archiver. |
| GitHub Actions frontend | Préouvrir run 39165084863 | Tests, build, rsync, smoke SUCCESS. |
| GitHub Actions backend | Préouvrir run 39165116209 | Maven + Docker SUCCESS, pas de deploy Railway. |
| Railway logs | Préparer vue anonymisée si disponible | Ne pas exposer variables, headers sensibles ou tokens. |
| Tests | Avoir logs exacts ouverts | 104 rapportés avec 5 skipped ; PG dédié 5/5 ; frontend 47/47. |
| Flyway | Préparer preuve migration | V1-V3 ; warning PostgreSQL 18 à reconnaître. |
| Dashboard Admin | Capture seulement si réellement archivée | Sinon annoncer preuve fonctionnelle et capture à compléter. |
| Marketplace | Captures UI existantes + matrice | Captures/traces du cycle E2E à compléter. |
| E2E uploads | Préparer checklist JPEG/PNG/GIF/BMP | Fonctionnel validé, preuve visuelle à ajouter. |
| Matrice RNCP | Ouvrir 23_MAPPING_C1_C11 et plan de preuves | Distinguer PASS/PARTIAL/GAP. |
| Secrets écran | Fermer env, console et pages credentials | Ne jamais partager secrets, cookies, tokens ou credentials. |

---

# J. Grille d'évaluation sur 100

Pour chaque catégorie, attribuer un niveau unique selon le descripteur atteint.
Le score est un outil d'entraînement, pas une prédiction de résultat jury.

| Catégorie | Points | Insuffisant | Correct | Bon | Excellent |
|---|---:|---|---|---|---|
| Présentation | 15 | Besoin et contribution confus, dépassement important | Produit et objectifs compréhensibles | Fil clair, timing maîtrisé, rôle personnel précis | Récit concis, adapté au jury, preuves bien choisies |
| Architecture | 10 | Ne sait pas expliquer les couches | Décrit frontend/backend et base | Explique responsabilités et flux | Justifie arbitrages et limites sans jargon inutile |
| Développement | 15 | Fonctionnalités ou rôle personnel imprécis | Explique les modules principaux | Détaille règles et exemples corrects | Relie conception, code, tests et impacts |
| Sécurité | 15 | Confond UI et contrôle serveur | Cite protections principales | Explique CSRF, RBAC, ownership et menaces | Relie menace, mesure, preuve et risque résiduel |
| Données | 10 | Confond ORM et base | Explique tables et migrations simplement | Défend contraintes, transaction, Flyway | Analyse concurrence, non-vide, rollback et limites |
| Tests | 10 | Annonce chiffres inexacts | Donne résultats principaux | Distingue unit/integration/E2E et skipped | Explique portée, limites, régression et preuve |
| DevOps/déploiement | 10 | Confond CI/CD ou hébergeurs | Décrit production | Distingue Hostinger automatisé et Railway manuel | Explique artefacts, non-root, volume, smoke et rollback |
| Recul/limites | 5 | Nie les gaps | Cite une limite | Priorise les risques | Propose mesures réalistes et mesurables |
| Communication | 5 | Réponses longues ou incompréhensibles | Compréhensible | Structurée et concise | Adapte le détail à la relance, explique le vocabulaire |
| Posture professionnelle | 5 | Invente ou se défend agressivement | Reste respectueux et factuel | Assume erreurs et contribution | Écoute, reconnaît incertitude, propose vérification pertinente |

## J2. Seuils d'auto-entraînement

- **0–49 :** reprendre les notions fondamentales et le pitch.
- **50–69 :** entraînement ciblé sur preuves et questions difficiles.
- **70–84 :** préparation correcte ; travailler concision et relances.
- **85–100 :** bonne maîtrise d'entraînement ; continuer les simulations orales.
- Un score élevé ne certifie pas l'obtention du titre.

---

# K. Fiche d'auto-évaluation après simulation

Attribuer 0, 1 ou 2 à chaque critère : 0 = absent/incorrect, 1 = partiel,
2 = clair et juste. Score maximal : 16.

| Critère | 0 | 1 | 2 | Score |
|---|---|---|---|---:|
| Exactitude | erreur ou affirmation inventée | nuance nécessaire | fait exact et preuve cohérente | |
| Structure | réponse dispersée | idée générale présente | réponse courte puis détail logique | |
| Preuve | aucune ou mauvaise | preuve citée sans portée | bonne preuve et portée précisée | |
| Limite | cachée ou niée | évoquée vaguement | limite qualifiée PARTIAL/GAP | |
| Temps | dépasse fortement | petit dépassement | durée respectée | |
| Vocabulaire | jargon non expliqué | quelques termes clarifiés | termes expliqués simplement | |
| Assurance | évitement ou agressivité | hésitation gérable | calme, honnête, écoute active | |
| Concision | digressions | quelques détails inutiles | répond à la question puis s'arrête | |

**Bilan après chaque répétition :** meilleure réponse ; erreur à corriger ;
preuve à ouvrir ; thème à retravailler ; action avant prochaine session.

---

# L. Erreurs à éviter le jour J

- Répondre trop longtemps ou lire les slides.
- Affirmer « c'est sécurisé » sans décrire menace, mesure et preuve.
- Confondre 401/403, hash/chiffrement, CI/CD.
- Dire que le backend Railway est déployé automatiquement.
- Déclarer conformité RGPD/RGAA/RGESN complète.
- Cacher backup, rollback, observabilité ou révocation JWT absents.
- Montrer un secret, cookie, token, email ou écran de credentials.
- Inventer un résultat ou une capture de production.
- Présenter PurpleWatch comme travail d'équipe : il a été réalisé seul.
- Critiquer une technologie sans critère mesurable.
- Se perdre dans un détail avant d'avoir répondu à la question.
- Déclencher une mutation de production dangereuse pendant une démo.

## Réponses de secours

- « Je n'ai pas validé ce point dans le projet, donc je préfère ne pas l'affirmer. Mon approche serait de… »
- « Ce comportement n'est pas encore industrialisé. La mesure actuelle est…, et l'amélioration serait… »
- « Je n'ai pas la valeur exacte en mémoire ; la preuve correspondante est dans… »
- « Je peux distinguer ce que le test démontre et ce qu'il ne couvre pas. »
- « Je ne suis pas certain de ce détail ; je peux expliquer ce que je vérifierais dans la documentation et par un test ciblé. »

---

# M. Simulation chronométrée complète

## Format 75 minutes

| Temps | Séquence | Déroulé |
|---|---|---|
| 0–20 min | Présentation projet | 15 slides ; utiliser le conducteur A1. |
| 20–35 min | Questions projet | Sélectionner 10 questions de B, relances incluses. |
| 35–50 min | Questions techniques | Choisir 10 questions D, demander preuve et limite pour chacune. |
| 50–60 min | Questionnaire rapide | 20 questions de la section D, 30 secondes chacune. |
| 60–75 min | Entretien final | 10 questions de E, avec une réponse STAR et projet professionnel. |

## Format raccourci 45 minutes

| Temps | Séquence | Déroulé |
|---|---|---|
| 0–10 min | Présentation condensée | Version A2 en 10 minutes. |
| 10–20 min | Questions projet | 6 questions B, dont Marketplace, sécurité, limites. |
| 20–30 min | Questions techniques | 6 questions D, dont JWT, transaction, Flyway et backup. |
| 30–37 min | Questionnaire rapide | 14 questions variées, 30 secondes chacune. |
| 37–45 min | Entretien final | 5 questions sur parcours, autonomie, erreur et projet pro. |

**Règle de simulation.** L'interlocuteur peut demander « quelle preuve ? » après
chaque affirmation. Le candidat doit répondre par une référence ou qualifier le
point de PARTIAL/GAP.

---

# N. Fiche Jour J — une page

## Pitch 30 secondes

Je suis Kouakou Brice Kanga, en reconversion du métier de technicien forestier
vers le développement full-stack et la cybersécurité. À Holberton School Paris,
je prépare le titre CDA niveau 6. WatYouFace m'a permis de pratiquer conception,
sécurité applicative, tests et déploiement. Je recherche un stage AppSec,
développement sécurisé ou SOC junior.

## Architecture

Navigateur → React/Vite → REST + STOMP → Spring Security → Controllers →
Services → Repositories JPA → PostgreSQL.

## Marketplace

AVAILABLE → PENDING → ACCEPTED → PAID → SHIPPED → RECEIVED ; alternative
PENDING → REFUSED. L'acheteur et le vendeur agissent selon leur rôle ; solde
insuffisant = 409.

## Sécurité

JWT cookie HttpOnly, Secure en production, SameSite=Lax ; CSRF ; CORS explicite ;
RBAC ; ownership/BOLA ; uploads MIME + décodage réel. Le frontend n'est pas la
frontière de sécurité.

## Tests et livraison

Maven 104 rapportés, 0 fail/error, 5 PG conditionnels skipped ; PG dédié 5/5 ;
frontend 47/47 ; build PASS ; lint 0 erreur/1 warning. Frontend CI/CD Hostinger ;
backend CI Maven/Docker et deploy Railway manuel contrôlé.

## Cinq preuves fortes

1. PostgreSQL dédié 5/5.
2. Backend CI 39165116209.
3. Frontend CI/CD 39165084863.
4. Flyway V2/V3 sur PostgreSQL 16 avec 37 listings, 0 perte/0 NULL.
5. BE-DEP-03 : Java non-root et persistance volume validée après redeploy.

## Cinq limites

1. Backup/restore réel non testé.
2. Observabilité complète absente.
3. Révocation JWT centrale absente.
4. Pas d'antivirus/quarantaine média.
5. CD backend automatique et conformité complète non revendiqués.

## Projet professionnel

Stage cybersécurité, AppSec, développement sécurisé ou SOC junior ; évoluer
vers un profil hybride développement et cybersécurité.

## Conclusion

« Je sais expliquer ce que j'ai conçu et testé, montrer les preuves disponibles
et nommer précisément ce qui reste à industrialiser. »
