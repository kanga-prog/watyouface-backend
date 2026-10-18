# WatYouFace — Préparation entretien final CDA

> Guide oral pour Kouakou Brice Kanga, titre CDA niveau 6. Fondé sur
> WatYouFace comme projet principal et sur les sources RNCP consolidées.
> PurpleWatch et Bagage Voyage ne sont cités qu'à titre complémentaire ;
> PurpleWatch est présenté comme projet solo.

## 1. Présentation du candidat

### Version 30 secondes

Je suis Kouakou Brice Kanga. Après une première expérience comme technicien
forestier, j'ai choisi une reconversion vers le numérique. Je me forme au
développement full-stack à Holberton School Paris, avec une spécialisation
cybersécurité, et je prépare le titre CDA niveau 6. Mon objectif est de
combiner développement sécurisé, AppSec et, à terme, une expérience SOC junior.

### Version 1 minute

Je suis Kouakou Brice Kanga. Mon parcours a commencé comme technicien forestier,
où j'ai développé rigueur, autonomie et attention aux contraintes de terrain.
J'ai ensuite choisi une reconversion vers le numérique afin de travailler sur
des problèmes techniques et de construire des solutions concrètes. À Holberton
School Paris, j'ai développé des compétences full-stack puis un intérêt marqué
pour la cybersécurité. WatYouFace est mon projet principal : il m'a permis de
travailler sur React, Spring Boot, PostgreSQL, sécurité applicative, tests, CI
et déploiement. Je cherche maintenant un stage en cybersécurité, AppSec,
développement sécurisé ou SOC junior pour consolider cette pratique dans un
environnement professionnel.

### Version 2 minutes

Je suis Kouakou Brice Kanga. J'ai d'abord exercé comme technicien forestier.
Cette première expérience m'a appris à travailler avec rigueur, à observer une
situation concrète, à respecter des procédures et à être autonome. J'ai choisi
une reconversion vers le numérique par intérêt pour la résolution de problèmes
et la construction de produits utiles. J'ai suivi une formation full-stack à
Holberton School Paris, puis j'ai orienté progressivement mon travail vers la
cybersécurité et le développement sécurisé.

Dans WatYouFace, j'ai conçu et fait évoluer une application web sociale avec
Feed, Chat temps réel, Marketplace, administration et wallet de démonstration.
Le projet m'a permis de pratiquer React/Vite, Spring Boot, PostgreSQL, Flyway,
Docker, GitHub Actions, Railway et Hostinger. J'ai surtout travaillé la
sécurité applicative : JWT en cookie HttpOnly, CSRF, RBAC, ownership, protection
BOLA/IDOR, validation des uploads et secrets externalisés. J'ai également
appris à ne pas surdéclarer : le projet est en production, mais backup/restore,
observabilité, révocation JWT et conformité complète restent des axes à
industrialiser.

Mon projet professionnel est de démarrer par un stage cybersécurité, AppSec,
développement sécurisé ou SOC junior. À moyen terme, je veux renforcer une
double compétence développement et cybersécurité, notamment sur l'automatisation
des contrôles et la détection.

## 2. Pourquoi cette reconversion ?

**Réponse courte.** Je voulais évoluer vers un domaine technique où je peux
analyser un problème, construire une solution et apprendre en continu. Le
développement m'a conduit naturellement à la cybersécurité, car une
fonctionnalité utile doit aussi protéger les utilisateurs, les données et les
règles métier.

**Points à couvrir.** Transition volontaire ; résolution de problèmes ;
formation structurée ; continuité développement-sécurité ; projet professionnel
réaliste.

**À éviter.** Dire seulement que le numérique recrute ou que la cybersécurité
est à la mode.

## 3. Pourquoi le titre CDA ?

**Réponse courte.** Le titre CDA me permet de démontrer le cycle complet d'une
application : analyser le besoin, concevoir, développer frontend et backend,
sécuriser, tester, déployer et documenter. WatYouFace est le support concret de
cette professionnalisation.

## 4. Présenter WatYouFace

### Version 30 secondes

WatYouFace est une application web sociale qui rassemble un Feed, un Chat temps
réel, une Marketplace et un espace ADMIN. Le frontend React/Vite communique
avec un backend Spring Boot et PostgreSQL. Le projet est déployé : Hostinger
pour le frontend, Railway pour le backend, la base et le volume média.

### Version 1 minute

WatYouFace réunit trois piliers : Feed social, Chat temps réel et Marketplace.
La Marketplace possède un cycle contrôlé de AVAILABLE à RECEIVED, avec une
variante REFUSED et un wallet de démonstration. L'ADMIN modère les contenus et
crédite le wallet, sans contourner les rôles acheteur/vendeur. La sécurité est
centralisée côté backend : JWT HttpOnly, CSRF, CORS, RBAC, ownership et
validation des uploads. Le frontend est déployé automatiquement vers Hostinger,
tandis que le backend Docker est déployé manuellement de façon contrôlée sur
Railway avec PostgreSQL.

## 5. Contribution personnelle

**Réponse.** J'ai personnellement travaillé sur la conception et la
documentation du périmètre, le développement frontend et backend, la
sécurisation des règles Marketplace, les tests, la CI, le déploiement et la
recette. Concrètement, j'ai renforcé ownership et BOLA/IDOR, clarifié le rôle
ADMIN, mis en place les migrations Flyway, préparé Docker/Railway, corrigé les
uploads et construit les preuves RNCP. Je distingue ce que j'ai validé de ce
qui reste à industrialiser.

**Preuves à citer.** Tests PostgreSQL 5/5 ; frontend 47/47 ; CI backend
39165116209 ; CI/CD frontend 39165084863 ; production Hostinger/Railway.

## 6. Compétences acquises

| Catégorie | Compétences à formuler | Preuve ou exemple |
|---|---|---|
| Technique | React/Vite, Spring Boot, SQL, JPA, PostgreSQL, Flyway, Docker | Feed, Marketplace, migrations, déploiement |
| Sécurité | JWT, CSRF, CORS, RBAC, ownership, BOLA/IDOR, upload | Marketplace et validation média |
| Qualité | tests unitaires/intégration, CI, gestion erreurs | Maven, PostgreSQL dédié, frontend |
| Méthode | analyse, lots, documentation, veille | source de vérité, plan de preuves |
| Professionnelle | autonomie, rigueur, priorisation, apprentissage | WebP, Flyway, Hostinger |

## 7. Difficultés rencontrées

| Difficulté | Analyse et décision | Résultat et apprentissage |
|---|---|---|
| WebP/ImageIO | Décodage non fiable vérifié ; contrat réduit aux formats réellement testés | JPEG/PNG/GIF/BMP, erreurs propres ; ne pas annoncer une capacité non prouvée |
| Hostinger Git natif | Les sources Vite pouvaient être publiées au lieu de dist | Actions, build, rsync et smoke ; vérifier l'artefact réel |
| Flyway base non vide | Risque sur listings existants | Migration additive validée sur 37 listings, 0 suppression, 0 NULL |
| BOLA/IDOR | Un identifiant ne doit pas donner accès à l'objet d'autrui | Ownership serveur et tests ; frontend non frontière sécurité |
| ADMIN/transitions | Risque de bypass métier | ADMIN modérateur, hidden sur transaction engagée |

## 8. Une erreur ou un échec

**Réponse préparée.** Une erreur initiale a été de laisser le contrat annoncer
WebP alors que la stack ImageIO/Thumbnailator ne le décodait pas de façon
fiable. Le risque était une erreur 500 lors d'un upload. J'ai analysé le
comportement réel, refusé WebP plutôt que d'ajouter un support non maîtrisé, et
aligné le contrat sur JPEG/PNG/GIF/BMP. J'ai changé ma méthode : avant d'annoncer
un format ou une intégration, je vérifie la capacité réelle par test et preuve.

## 9. Autonomie et travail en équipe

### Comment travaillez-vous seul ?

Je décompose le problème, reproduis le comportement, lis les logs, consulte la
documentation officielle, compare les solutions, teste l'hypothèse, documente
la décision et vérifie avant déploiement. Flyway sur base non vide et WebP sont
des exemples de cette méthode.

### Comment parlez-vous du travail en équipe ?

WatYouFace est un projet personnel : je ne le présente pas comme un travail
d'équipe. En revanche, ma méthode est compatible avec une équipe : branches et
commits cohérents, GitHub, tickets, revue de code, documentation et communication
factuelle des risques. Je sais demander un avis lorsque l'impact dépasse mon
périmètre.

## 10. Qualités et axes d'amélioration

| Élément | Formulation avec preuve |
|---|---|
| Rigueur | Je distingue les 104 tests Maven rapportés des 5 tests PostgreSQL réellement exécutés séparément. |
| Autonomie | Je transforme une incertitude en reproduction, test, décision et documentation. |
| Persévérance | Les incidents WebP, volume Railway et Flyway ont été analysés jusqu'à une solution vérifiée. |
| Sécurité | J'intègre droits, ownership et entrées invalides dès la logique métier. |
| Axe : observabilité | Ajouter santé, métriques et alertes après stabilisation. |
| Axe : backup/restore | Tester réellement sauvegarde PostgreSQL et volume média. |
| Axe : accessibilité | Compléter l'audit RGAA clavier, contraste, zoom et lecteur d'écran. |

## 11. Veille technologique

**Méthode.** Documentation officielle, release notes, GitHub Security Advisories,
CVE, OWASP, CNIL et sources de fournisseurs ; veille légère hebdomadaire, revue
des dépendances mensuelle et analyse immédiate d'une CVE critique.

**Chaîne de décision.** Source → analyse → impact WatYouFace → priorité → test
→ décision → documentation.

**Exemples.** Le warning PostgreSQL 18/Flyway conduit à surveiller la
compatibilité ; Java 17 est conservé pour stabilité avant étude Java 21 ; WebP
a été retiré du contrat faute de décodeur fiable ; le limiter in-memory est
acceptable sur une instance mais Redis serait étudié en cas de scaling.

## 12. Projet professionnel

| Horizon | Réponse préparée |
|---|---|
| Court terme | Rechercher un stage en cybersécurité, AppSec, développement sécurisé ou SOC junior. |
| Moyen terme | Consolider une expérience professionnelle, renforcer sécurité applicative, automatisation et détection. |
| Long terme | Développer un profil hybride développement + cybersécurité, capable de concevoir et sécuriser des applications. |

## 13. Pourquoi vous ? — réponse 45 secondes

Je peux apporter une double approche développement et cybersécurité. WatYouFace
m'a donné une pratique concrète full-stack, depuis les interfaces jusqu'au
déploiement, avec des contrôles de sécurité appliqués à des cas métier réels.
Je suis autonome, méthodique et j'ai l'habitude de documenter les décisions et
les limites plutôt que de les masquer. Je cherche maintenant un cadre
professionnel où consolider ces compétences, apprendre au contact d'une équipe
et contribuer à des produits plus sûrs.

## 14. Limites du projet

**Réponse structurée.** WatYouFace est fonctionnel et déployé, mais il manque
des éléments d'industrialisation : backup/restore réel testé, observabilité
complète, révocation JWT centrale, antivirus/quarantaine média, CD backend et
conformité RGPD/RGAA complète. Je distingue volontairement ce qui est
opérationnel de ce qui reste à industrialiser.

## 15. Réponse générale sur la sécurité

J'ai intégré la sécurité dès les règles métier : authentification JWT en cookie
HttpOnly, CSRF pour les mutations, CORS explicite, RBAC ADMIN, ownership et
protection BOLA/IDOR. Les uploads vérifient le MIME et le contenu réel, les
secrets sont externalisés, et les tests négatifs vérifient les refus. Cette
démarche réduit des risques, mais ne permet pas de dire que l'application est
parfaitement sécurisée.

## 16. Réponse sur le déploiement

Oui, l'application est déployée. Le frontend est sur Hostinger, via GitHub
Actions qui teste, build Vite, transfère par rsync SSH et fait un smoke test.
Le backend Docker est sur Railway avec PostgreSQL Railway et volume média
persistant. La CI backend exécute Maven et le build Docker ; le déploiement
Railway reste manuel et contrôlé, donc ce n'est pas du CD automatique.

## 17. Lien RNCP

**Compétences développées.** Conception et architecture, interfaces frontend,
règles métier backend, base PostgreSQL, sécurité, tests, Docker, CI et
déploiement. C6 possède une preuve documentaire forte ; les autres compétences
sont présentées selon leurs preuves PASS, PARTIAL ou GAP.

**Meilleure démonstration.** La sécurité et l'intégrité Marketplace : rôles,
ownership, transactions, Flyway et tests PostgreSQL.

**À renforcer.** Exploitation : backup/restore, observabilité, accessibilité et
automatisation backend.

## 18. Questions comportementales — 30

### B1 — Parlez-moi de vous.

**Réponse courte.** Je suis Kouakou Brice Kanga, en reconversion du métier de technicien forestier vers le développement full-stack et la cybersécurité.

**Points à couvrir.** Parcours, Holberton School Paris, titre CDA, orientation AppSec/SOC junior.

**Piège à éviter.** Réciter tout le CV ou prétendre une expérience non vécue.

### B2 — Pourquoi cette reconversion ?

**Réponse courte.** Je voulais évoluer vers un domaine technique où je peux analyser, construire et résoudre des problèmes concrets.

**Points à couvrir.** Découverte développement, progression vers sécurité, démarche active d'apprentissage.

**Piège à éviter.** Réponse vague du type j'aime l'informatique.

### B3 — Pourquoi le titre CDA ?

**Réponse courte.** Il structure la maîtrise d'une application de la conception au déploiement, avec sécurité, tests et documentation.

**Points à couvrir.** Full-stack, architecture, données, mise en production, professionnalisation.

**Piège à éviter.** Le réduire à une ligne de CV.

### B4 — Quel est votre principal succès ?

**Réponse courte.** Avoir conduit WatYouFace jusqu'à une production réelle en distinguant les preuves des limites.

**Points à couvrir.** Architecture, tests, CI, Railway/Hostinger, documentation RNCP.

**Piège à éviter.** Dire que le projet est parfait.

### B5 — Quelle difficulté vous a le plus appris ?

**Réponse courte.** Le support WebP annoncé mais non décodable fiablement m'a appris à vérifier la capacité réelle de la stack.

**Points à couvrir.** Diagnostic, tests, contrat réduit JPEG/PNG/GIF/BMP, erreurs 400.

**Piège à éviter.** Présenter WebP comme supporté.

### B6 — Comment gérez-vous un bug urgent ?

**Réponse courte.** Je sécurise l'impact, je reproduis, j'examine les logs, j'isole la cause, je corrige et je vérifie par tests.

**Points à couvrir.** Priorité, rollback/mitigation si nécessaire, communication factuelle.

**Piège à éviter.** Modifier directement la production sans reproduction.

### B7 — Comment réagissez-vous à une critique ?

**Réponse courte.** Je demande des exemples précis, je vérifie l'impact et j'utilise le retour pour améliorer la solution.

**Points à couvrir.** Écoute, données, décision documentée.

**Piège à éviter.** Se justifier avant de comprendre.

### B8 — Comment priorisez-vous ?

**Réponse courte.** Je commence par sécurité, intégrité métier et blocages utilisateur, puis risque, valeur et effort.

**Points à couvrir.** Backlog, tests, lots petits et vérifiables.

**Piège à éviter.** Prioriser uniquement ce qui est le plus visible.

### B9 — Comment documentez-vous ?

**Réponse courte.** Je documente les décisions, preuves, limites, commandes et résultats pour qu'ils soient vérifiables.

**Points à couvrir.** RNCP source de vérité, plan de preuves, runbooks.

**Piège à éviter.** Écrire après coup sans vérifier l'état réel.

### B10 — Comment apprenez-vous une technologie ?

**Réponse courte.** Je pars de la documentation officielle, réalise un test ciblé, compare les impacts, puis documente la décision.

**Points à couvrir.** Exemple Flyway/PostgreSQL 18 ou WebP.

**Piège à éviter.** Copier une solution sans comprendre.

### B11 — Que faites-vous si vous ne savez pas ?

**Réponse courte.** Je le dis, je définis ce qu'il faut vérifier, puis je recherche et teste de manière contrôlée.

**Points à couvrir.** Logs, docs officielles, reproduction, preuve.

**Piège à éviter.** Inventer une réponse technique.

### B12 — Comment gérez-vous une deadline ?

**Réponse courte.** Je clarifie le périmètre, livre les risques critiques en premier, découpe en lots et remonte les limites tôt.

**Points à couvrir.** MVP, priorisation, preuve à chaque jalon.

**Piège à éviter.** Promettre tout sans arbitrage.

### B13 — Seul ou en équipe ?

**Réponse courte.** Je suis autonome, mais je sais travailler avec des règles partagées : Git, tickets, revue, documentation et communication factuelle.

**Points à couvrir.** WatYouFace est solo ; aptitude à intégrer une équipe.

**Piège à éviter.** Prétendre une équipe sur un projet solo.

### B14 — Quelle est votre force ?

**Réponse courte.** La rigueur : je cherche à vérifier le comportement réel et à documenter les limites.

**Points à couvrir.** PostgreSQL 5/5 séparés des skipped Maven.

**Piège à éviter.** Qualité vague sans exemple.

### B15 — Quelle compétence renforcer ?

**Réponse courte.** L'observabilité et les opérations de sauvegarde/restauration en environnement professionnel.

**Points à couvrir.** GAP actuel, plan de progression concret.

**Piège à éviter.** Répondre perfectionnisme.

### B16 — Où vous voyez-vous dans trois ans ?

**Réponse courte.** Dans un poste où je combine développement sécurisé, AppSec ou SOC junior, avec une expérience professionnelle consolidée.

**Points à couvrir.** Progression réaliste, apprentissage continu.

**Piège à éviter.** Promettre un poste senior immédiat.

### B17 — Quel type d'entreprise recherchez-vous ?

**Réponse courte.** Un environnement qui valorise qualité, sécurité, revue de code et accompagnement technique.

**Points à couvrir.** Stage cybersécurité, développement sécurisé, culture d'apprentissage.

**Piège à éviter.** Exiger une technologie unique.

### B18 — Pourquoi la cybersécurité ?

**Réponse courte.** Elle prolonge le développement : une fonctionnalité utile doit aussi protéger données, droits et règles métier.

**Points à couvrir.** RBAC, BOLA/IDOR, uploads, secrets.

**Piège à éviter.** Présenter la sécurité comme un contrôle final seulement.

### B19 — Pourquoi AppSec ?

**Réponse courte.** Parce que je veux intégrer la sécurité dès la conception et dans le cycle de développement.

**Points à couvrir.** Tests négatifs, CI, permissions, validation.

**Piège à éviter.** Dire que l'AppSec remplace les développeurs.

### B20 — Pourquoi SOC junior ?

**Réponse courte.** La détection, les logs et l'analyse d'incidents complètent mon profil développeur et mon intérêt pour la sécurité.

**Points à couvrir.** PurpleWatch solo comme expérience complémentaire, observabilité à renforcer.

**Piège à éviter.** Exagérer une expérience SOC en entreprise.

### B21 — Comment votre passé professionnel vous aide-t-il ?

**Réponse courte.** Le terrain m'a appris rigueur, autonomie, observation et prise en compte des contraintes réelles.

**Points à couvrir.** Reconversion structurée, persévérance.

**Piège à éviter.** Dévaloriser l'expérience précédente.

### B22 — Comment garantissez-vous la qualité ?

**Réponse courte.** Je combine critères, validations backend, tests positifs/négatifs, CI et revue des limites.

**Points à couvrir.** Maven, PostgreSQL dédié, frontend, diff check.

**Piège à éviter.** Dire les tests prouvent tout.

### B23 — Comment testez-vous avant livraison ?

**Réponse courte.** Je reproduis le scénario, lance la suite adaptée, vérifie le build et documente le résultat.

**Points à couvrir.** Tests ciblés puis CI ; E2E si périmètre prévu.

**Piège à éviter.** Déployer avant tests.

### B24 — Comment gérez-vous un désaccord technique ?

**Réponse courte.** Je compare critères, risques, coût et preuves, puis je propose un test ou prototype ciblé.

**Points à couvrir.** Décision WebP fondée sur décodage réel.

**Piège à éviter.** Transformer le désaccord en préférence personnelle.

### B25 — Comment choisissez-vous entre deux solutions ?

**Réponse courte.** Je compare sécurité, maintenabilité, compatibilité, effort et preuve disponible.

**Points à couvrir.** Flyway validate contre ddl-auto update.

**Piège à éviter.** Choisir uniquement la solution la plus récente.

### B26 — Quelle décision referiez-vous différemment ?

**Réponse courte.** J'anticiperais plus tôt le test de backup/restore et l'observabilité avant la fin du projet.

**Points à couvrir.** Recul concret et plan.

**Piège à éviter.** Dire aucune décision.

### B27 — Quelle partie vous rend le plus fier ?

**Réponse courte.** La sécurisation du cycle Marketplace : ownership, transactions, modération sans bypass ADMIN et preuves dédiées.

**Points à couvrir.** BOLA/IDOR, hidden, PostgreSQL.

**Piège à éviter.** Parler seulement du design.

### B28 — Quelle partie est la moins aboutie ?

**Réponse courte.** L'exploitation : backup/restore, métriques/alertes, révocation JWT et captures E2E complètes.

**Points à couvrir.** GAP assumés, priorités futures.

**Piège à éviter.** Masquer ces limites.

### B29 — Qu'avez-vous appris sur vous-même ?

**Réponse courte.** Je suis plus efficace lorsque je transforme une incertitude en hypothèse, test, décision et preuve.

**Points à couvrir.** Méthode autonome, documentation.

**Piège à éviter.** Formulation psychologique vague.

### B30 — Que feriez-vous avec trois mois de plus ?

**Réponse courte.** Je prioriserais backup/restore testés, observabilité, audit accessibilité, scan média et automatisation backend après validation.

**Points à couvrir.** Ordre de risque et non liste infinie.

**Piège à éviter.** Ajouter des fonctionnalités au hasard.

## 19. Méthode STAR

**Rappel.** S = Situation, T = Tâche, A = Action, R = Résultat. Répondre
en une à deux minutes, avec un fait mesurable et une leçon.

### STAR 1 — WebP et ImageIO

- **Situation :** L'upload annonçait WebP, mais la stack Java ne le décodait pas fiablement.
- **Tâche :** Garantir un upload robuste sans 500.
- **Action :** J'ai vérifié le décodage réel, réduit le contrat à JPEG/PNG/GIF/BMP, ajouté validation MIME/contenu et erreurs client.
- **Résultat :** Flux maîtrisé ; WebP volontairement refusé. Leçon : ne pas annoncer une capacité non testée.

### STAR 2 — Déploiement Hostinger

- **Situation :** Le Git natif pouvait déployer les sources Vite au lieu de dist.
- **Tâche :** Publier le frontend compilé de manière reproductible.
- **Action :** J'ai retenu GitHub Actions : tests, build Vite, rsync SSH, smoke test.
- **Résultat :** CI/CD frontend prouvé par run 39165084863. Leçon : vérifier l'artefact réellement déployé.

### STAR 3 — Flyway sur base non vide

- **Situation :** Les migrations V2/V3 devaient respecter des listings existants.
- **Tâche :** Éviter perte ou NULL en production.
- **Action :** J'ai privilégié une migration additive et validé sur PostgreSQL 16 avec 37 listings.
- **Résultat :** 0 suppression, 0 NULL. Leçon : une base vide ne prouve pas une migration réelle.

### STAR 4 — BOLA/IDOR Marketplace

- **Situation :** Des transitions pouvaient être ciblées par identifiant d'objet.
- **Tâche :** Garantir que chaque acteur n'agit que sur sa ressource.
- **Action :** J'ai consolidé ownership et rôles serveur, sans bypass ADMIN transactionnel.
- **Résultat :** Contrôles et tests ciblés. Leçon : le frontend n'est pas une frontière de sécurité.

### STAR 5 — Volume média Railway

- **Situation :** Un volume monté appartenait à root et provoquait AccessDeniedException.
- **Tâche :** Rendre le média persistant accessible sans exécuter Java en root.
- **Action :** Entrypoint root ciblé sur /app/media, chown, baisse de privilèges vers spring.
- **Résultat :** Java sous spring UID 999, persistance validée après redeploy. Leçon : moindre privilège et permissions ciblées.

## 20. Questions à poser au jury ou recruteur

1. Quelle est la composition de l'équipe et comment sont organisées les revues de code ?
2. Quelles pratiques de sécurité applicative et de gestion des vulnérabilités utilisez-vous ?
3. Comment sont accompagnés les stagiaires ou profils juniors dans leur montée en compétences ?
4. Quels outils de CI/CD, de supervision et de gestion d'incidents sont utilisés ?
5. Quels seraient les objectifs concrets des premières semaines sur ce poste ou stage ?

## 21. Phrases à éviter et formulations solides

| À éviter | Préférer |
|---|---|
| Je sais tout. | Je maîtrise ce périmètre et je sais identifier ce que je dois approfondir. |
| C'est sécurisé. | J'ai mis en place ces contrôles et la limite actuelle est celle-ci. |
| C'est conforme. | Des mesures sont intégrées, mais la conformité complète n'est pas revendiquée. |
| Ça marche. | J'ai validé ce scénario par tel test, CI ou recette. |
| J'ai fait une équipe. | J'ai réalisé ce projet seul ; ma méthode est compatible avec le travail en équipe. |

## 22. Simulation entretien final — 20 minutes

### S1 — Présentez-vous.

**Réponse attendue.** Pitch 60 secondes : reconversion, Holberton, CDA, développement sécurisé/AppSec-SOC junior.

**Point de vigilance.** Rester sous une minute.

### S2 — Pourquoi WatYouFace ?

**Réponse attendue.** Projet principal montrant Feed, Chat, Marketplace, admin, sécurité, tests et production.

**Point de vigilance.** Ne pas en faire un paiement réel.

### S3 — Quelle contribution personnelle ?

**Réponse attendue.** Conception, développement, sécurité, tests, CI/CD frontend, déploiement, documentation et recette.

**Point de vigilance.** Citer un exemple par volet.

### S4 — Quel problème le plus complexe ?

**Réponse attendue.** Marketplace : ownership, états, wallet démo et intégrité transactionnelle.

**Point de vigilance.** Expliquer solution serveur.

### S5 — Quelle erreur avez-vous corrigée ?

**Réponse attendue.** WebP : capacité annoncée non vérifiée, contrat réduit après diagnostic.

**Point de vigilance.** Montrer apprentissage.

### S6 — Comment sécurisez-vous l'application ?

**Réponse attendue.** JWT HttpOnly, CSRF, CORS, RBAC, ownership/BOLA, uploads, secrets externalisés.

**Point de vigilance.** Ne pas dire parfaitement sécurisée.

### S7 — Quels tests prouvent votre qualité ?

**Réponse attendue.** Maven 104 rapportés avec 5 PG skipped ; PostgreSQL 5/5 ; frontend 47/47 ; CI.

**Point de vigilance.** Distinguer chaque portée.

### S8 — Application déployée ?

**Réponse attendue.** Oui : Hostinger frontend, Railway backend/PostgreSQL/volume ; CI/CD frontend et Railway manuel.

**Point de vigilance.** Ne pas dire CD backend.

### S9 — Quel est le principal GAP ?

**Réponse attendue.** Backup/restore réel non testé, puis observabilité.

**Point de vigilance.** Proposer plan.

### S10 — Comment travaillez-vous seul ?

**Réponse attendue.** Hypothèse, reproduction, logs, docs, test, décision documentée.

**Point de vigilance.** Donner Flyway/WebP.

### S11 — Pourquoi la reconversion ?

**Réponse attendue.** Évoluer vers résolution de problèmes et développement, puis sécurité.

**Point de vigilance.** Rester factuel.

### S12 — Pourquoi CDA ?

**Réponse attendue.** Maîtriser cycle complet de conception à déploiement sécurisé.

**Point de vigilance.** Relier au projet.

### S13 — Pourquoi cybersécurité ?

**Réponse attendue.** Compléter développement par protection des utilisateurs et règles métier.

**Point de vigilance.** Citer AppSec.

### S14 — Projet à court terme ?

**Réponse attendue.** Chercher un stage cybersécurité, AppSec, développement sécurisé ou SOC junior.

**Point de vigilance.** Ne pas promettre emploi garanti.

### S15 — Projet à moyen terme ?

**Réponse attendue.** Consolider expérience professionnelle, sécurité applicative, automatisation et détection.

**Point de vigilance.** Plan réaliste.

### S16 — Qualité principale ?

**Réponse attendue.** Rigueur démontrée par preuves et limites explicites.

**Point de vigilance.** Citer exemple tests.

### S17 — Axe de progrès ?

**Réponse attendue.** Observabilité et backup/restore.

**Point de vigilance.** Dire mesures concrètes.

### S18 — Travail en équipe ?

**Réponse attendue.** Projet solo, mais méthode compatible : Git, documentation, tickets, revue.

**Point de vigilance.** Pas d'équipe fictive.

### S19 — Que feriez-vous avec trois mois ?

**Réponse attendue.** Industrialiser exploitation avant nouvelles features.

**Point de vigilance.** Prioriser.

### S20 — Pourquoi vous ?

**Réponse attendue.** Double compétence dev/cyber, autonomie, production réelle, capacité à apprendre.

**Point de vigilance.** Réponse 45 secondes.

## 23. Fiche mémo finale — une page

### Présentation

Kouakou Brice Kanga : ancien technicien forestier, reconversion full-stack à
Holberton School Paris, spécialisation cybersécurité, préparation CDA niveau 6.

### Projet

WatYouFace : Feed, Chat, Marketplace, ADMIN, React/Vite, Spring Boot,
PostgreSQL ; frontend Hostinger, backend Railway.

### Cinq réalisations

1. Cycle Marketplace sécurisé par rôles, états et transactions.
2. RBAC ADMIN, wallet démo et modération sans bypass transactionnel.
3. Uploads validés : JPEG/PNG/GIF/BMP, 10 Mo, décodage réel et nettoyage.
4. Flyway V2/V3 validées sur 37 listings sans perte ni NULL.
5. Tests, CI, Docker, volume média non-root et documentation de preuves.

### Cinq compétences

- Frontend et backend full-stack.
- Modélisation SQL, migrations et transactions.
- Sécurité applicative : JWT, CSRF, RBAC, BOLA/IDOR.
- Tests, CI et déploiement.
- Analyse, documentation, veille et priorisation.

### Trois limites à citer

- Backup/restore réel non testé.
- Observabilité, métriques et alerting limités.
- CD backend, révocation JWT et conformité complète absents.

### Projet professionnel

Stage cybersécurité, AppSec, développement sécurisé ou SOC junior ; construire
à moyen terme un profil hybride développement + cybersécurité.

### Conclusion

Je sais concevoir, développer, sécuriser, tester et déployer un projet ; je sais
aussi qualifier les limites et prioriser la prochaine amélioration.

## 24. Contrôle qualité

- WatYouFace est présenté comme projet principal et personnel.
- PurpleWatch peut être cité comme projet solo complémentaire, jamais comme équipe.
- Bagage Voyage reste une expérience développement complémentaire, sans détail non prouvé.
- Aucun déploiement Railway automatique, conformité totale ou backup testé n'est revendiqué.
- Ton personnel, professionnel et factuel ; aucune expérience inventée.
