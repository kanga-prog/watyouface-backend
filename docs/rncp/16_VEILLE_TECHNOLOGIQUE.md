# Phase 16 — Veille technologique WatYouFace

> Relecture Phase 17 (18/10/2026) : cette veille reste un relevé daté et une
> méthode de décision, non la preuve d'un upgrade, d'un scanner activé ou d'un
> CD backend automatique. Les décisions et limites sont synchronisées avec
> `RNCP6_SOURCE_DE_VERITE.md` et `17_DOCUMENTATION_FINALE.md`.

Date du relevé : 18/10/2026. Cette veille est un instantané documenté à cette date : elle ne prétend pas reconstituer une veille historique continue et ne déclenche aucune mise à jour de production.

## 1. Périmètre et technologies réellement utilisées

| Axe | État WatYouFace observé | Objet de veille |
|---|---|---|
| Backend | Java 17, Spring Boot 3.3.5, Spring Security, Maven, JPA/Hibernate 6.5.3, JWT cookie HttpOnly, CSRF, WebSocket/STOMP | LTS, correctifs sécurité, compatibilité API/configuration et dépendances |
| Données | PostgreSQL 18.6 Railway déclaré, PostgreSQL 16 validé localement, Flyway 10.10.0, transactions SQL | compatibilité moteur/migrations, correctifs, sauvegarde/restauration |
| Frontend | React 19.1, Vite 7.3.7 observé au build, Radix UI, Vitest | compatibilité, poids bundle, accessibilité et dépendances |
| DevOps | Docker multi-stage, GitHub Actions, Railway, Hostinger via rsync SSH | images, CI, secrets, déploiement et rollback |
| Sécurité | RBAC, BOLA/IDOR, CORS, CSRF, rate limit, uploads, WebSocket | OWASP API, CVE et régressions d'autorisation |
| Qualité | RGPD, RGAA, RGESN | décisions de minimisation, accessibilité et performance sobre |

## 2. Fiches de veille prioritaires

### Java / Spring Boot / Spring Security

| Élément | Analyse rattachée à WatYouFace |
|---|---|
| Pourquoi surveiller | Le Docker runtime et la CI backend sont Java 17 ; Spring Boot gère REST, JPA, sécurité et WebSocket. |
| État actuel | Java 17 et Spring Boot 3.3.5 stables pour le périmètre. La documentation Spring Boot 3.3 demande Java 17 au minimum et prend en charge Java 21. |
| Tendance / risque | Java 21 LTS peut apporter performances et évolutions d'outillage ; une migration non testée pourrait affecter dépendances, Docker, CI et Railway. Les évolutions Spring Security peuvent modifier les recommandations CSRF/cookies et API. |
| Décision | **Conserver Java 17 avant soutenance.** Étudier Java 21 après stabilisation, via branche dédiée, build Docker, suite Maven et smoke Railway. |
| Action future | Revue trimestrielle des release notes Spring et des alertes Maven ; patcher les CVE avec CI obligatoire. |
| Références | [Spring Boot requirements](https://docs.spring.io/spring-boot/3.3/system-requirements.html), [Spring Security CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html). |

### PostgreSQL / Flyway / transactions et restauration

| Élément | Analyse rattachée à WatYouFace |
|---|---|
| Pourquoi surveiller | Marketplace dépend des contraintes, verrous et migrations ; Railway production est déclaré PostgreSQL 18.6. |
| État actuel | Flyway 10.10.0 est géré par Spring Boot 3.3.5. V1–V3 et les tests paiement PostgreSQL 16 sont prouvés. Un avertissement de version PostgreSQL 18 a été observé avec la pile actuelle. |
| Évolution | PostgreSQL publie correctifs/migrations dans ses notes. La documentation Flyway récente annonce PostgreSQL 18 vérifiée, mais ce statut ne prouve pas que Flyway 10.10.0 du projet le supporte sans avertissement. |
| Risque | Ignorer le warning ou mettre à jour Flyway/moteur sans test peut bloquer le démarrage ou une migration sur table non vide. |
| Décision | **Ne pas migrer aveuglément.** Conserver l'alerte comme signal, suivre Flyway/PostgreSQL, tester d'abord sur copie/base vide et relancer validation + tests PostgreSQL. |
| Action future | Avant toute mise à niveau : sauvegarde testée, matrice versions JDBC/Flyway/PostgreSQL, `flyway validate/migrate`, smoke et plan de retour vers l'avant. |
| Références | [PostgreSQL 18.6 release notes](https://www.postgresql.org/docs/release/18.6/), [support PostgreSQL Flyway](https://documentation.red-gate.com/flyway/reference/database-driver-reference/postgresql-database). |

### React / Vite / accessibilité / performance

| Élément | Analyse rattachée à WatYouFace |
|---|---|
| Pourquoi surveiller | Le frontend React/Vite porte Feed, Marketplace, Admin et Chat et est déployé automatiquement vers Hostinger. |
| État actuel | Bundle Vite mesuré : JS 446,02 kB brut / 139,57 kB gzip ; CSS 43,23 kB brut / 8,44 kB gzip. Pas de découpage par route prouvé. Labels, alertes et tables Admin ont reçu des corrections ciblées Phase 15. |
| Évolution | React `lazy`/`Suspense` et le code splitting Vite permettent un chargement différé. Les changements majeurs React/Vite et dépendances doivent être testés car le chat, Radix et les routes sont sensibles. |
| Risque | Un bundle monolithique ou une mise à jour majeure non testée dégrade chargement/accessibilité ou casse build Hostinger. |
| Décision | **Mesurer avant optimiser** : aucun code splitting tardif avant soutenance. Envisager lazy loading Admin/Messages après mesure de navigation réelle. |
| Action future | Budget bundle, analyse dépendances, Lighthouse/WebPageTest, test clavier/RGAA sur routes prioritaires. |
| Références | [React lazy](https://react.dev/reference/react/lazy), [Vite production build](https://vite.dev/guide/build), [RGAA](https://accessibilite.numerique.gouv.fr/methode/criteres-et-tests/). |

### Sécurité API et WebSocket

| Élément | Analyse rattachée à WatYouFace |
|---|---|
| Pourquoi surveiller | Les API exposent PII, contenus, wallet démo et actions Marketplace ; STOMP introduit un second canal d'autorisation. |
| État actuel | RBAC ADMIN, ownership/BOLA, CSRF cookie, CORS sans wildcard, JWT HttpOnly, routes admin sécurisées et tests autorisation existent. Limiteur login in-memory et JWT sans révocation centrale. |
| Tendance / risque | OWASP API maintient BOLA/IDOR, auth et consommation de ressources parmi les risques prioritaires. Un endpoint nouveau, une mutation STOMP ou une pagination oubliée peut réintroduire une fuite. |
| Décision | **Conserver les contrôles serveur comme autorité**, ne jamais compter sur l'UI. Pas d'architecture Redis/révocation avant besoin réel de scaling. |
| Action future | Revue sécurité à chaque endpoint/action STOMP, tests 401/403/404 ownership, Dependabot/CVE et E2E STOMP multi-utilisateur. |
| Références | [OWASP API Security](https://owasp.org/projects/api-security-project), [OWASP API Top 10 2023](https://api-security.owasp.org/editions/2023/en/0x04-release-notes/). |

### Uploads et stockage média

| Élément | Analyse rattachée à WatYouFace |
|---|---|
| Pourquoi surveiller | Les uploads persistent sur volume Railway et constituent une surface d'attaque/facteur de coût. |
| État actuel | Feed/Marketplace : JPEG/PNG/GIF/BMP, 10 Mo, filtre MIME + décodage réel, UUID, temporaire/nettoyage, sortie JPEG. WebP/AVIF/HEIC sont refusés volontairement. |
| Évolution | Les formats modernes réduisent potentiellement le poids, mais nécessitent un décodeur Java maintenu, une politique de sécurité et des tests de fichiers réels/corrompus. AV/quarantaine restent absents. |
| Risque | Ajouter seulement un MIME WebP/AVIF/HEIC sans décodeur fiable crée des 500 ou des fichiers incohérents ; accepter du binaire non vérifié augmente le risque. |
| Décision | **Ne pas supporter WebP/AVIF/HEIC avant une étude/test de dépendance.** Le GIF est normalisé sur sa première image ; BMP en JPEG. |
| Action future | Évaluer une dépendance ImageIO maintenue, test de charge/corruption et, si le volume croît, stockage objet + antivirus/quarantaine. |

### Observabilité

| Élément | Analyse rattachée à WatYouFace |
|---|---|
| État actuel | Logs Spring Boot/Railway/GitHub Actions et smoke tests existent ; aucune métrique, alerte, health endpoint exposé ou centralisation n'est prouvée. |
| Évolution | Actuator fournit health/metrics, Micrometer les instrumente ; Prometheus/Grafana/alerting apportent supervision mais ajoutent surface d'exploitation. |
| Décision | **Ne pas surcharger le MVP avant soutenance.** Conserver logs et smoke tests, documenter les manques. |
| Action future | Actuator restreint, health check sans donnée sensible, métriques HTTP/JVM/DB, alertes disponibilité/erreur et politique de logs. |
| Références | [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html), [Micrometer](https://docs.micrometer.io/micrometer/reference/). |

### Sauvegarde / restauration et déploiement

| Élément | Analyse rattachée à WatYouFace |
|---|---|
| État actuel | Procédure `pg_dump`/`pg_restore` et archive média documentée, mais non testée. Backend Railway contrôlé/manuellement déployé ; frontend GitHub Actions → Vite → rsync Hostinger est automatique. |
| Risque | Une migration ou un incident sans restauration validée met en danger DB et volume ; CD backend tardif peut diffuser un défaut non détecté. |
| Décision | **Conserver le déploiement backend contrôlé/manuellement avant soutenance.** Ne pas prétendre au CD backend automatique. |
| Action future | Test isolé de restore DB + média, runbook horodaté, approbation de déploiement et rollback applicatif. |
| Références | [PostgreSQL backup](https://www.postgresql.org/docs/current/backup.html), [Docker multi-stage](https://docs.docker.com/build/building/multi-stage/), [Railway Dockerfiles](https://docs.railway.com/builds/dockerfiles), [GitHub Actions](https://docs.github.com/en/actions). |

## 3. Méthode de veille proposée

Fréquence réaliste : veille légère hebdomadaire (release notes et advisories), revue dépendances mensuelle, traitement immédiat d'une CVE critique touchant une dépendance directe, revue architecture avant toute évolution majeure (Java, PostgreSQL, Flyway, stockage média ou déploiement).

Canaux : documentations officielles et release notes Java/Spring/PostgreSQL/Flyway/React/Vite/Docker/Railway ; OWASP ; GitHub Security Advisories, Dependabot et NVD ; CNIL, RGAA et RGESN. GitHub indique que Dependabot peut proposer des correctifs de dépendances vulnérables ; son activation et ses PR restent à décider/revoir, elles ne sont pas déclarées actives dans WatYouFace.

Processus de décision :

```text
SOURCE → ANALYSE → IMPACT WATYOUFACE → PRIORITÉ → TEST ISOLÉ
       → DÉCISION (adopter / différer / refuser) → DOCUMENTATION + preuve
```

Une information externe ne devient jamais une modification de production sans compatibilité, test, contrôle des secrets et décision versionnée.

## 4. Décisions issues de cette veille

1. **PostgreSQL 18 / Flyway** : l'avertissement de la pile Flyway 10.10.0 n'est pas ignoré. Suivre Flyway, vérifier la matrice de compatibilité puis tester migrations et démarrage avant toute mise à jour.
2. **Uploads WebP** : ImageIO de la pile n'apportait pas de décodage WebP fiable. Le contrat Feed/Marketplace refuse donc WebP plutôt que de le promettre ; JPEG/PNG/GIF/BMP restent les formats prouvés.
3. **Rate limiting** : l'implémentation in-memory est acceptable sur l'instance actuelle ; Redis ou un limiteur distribué devient prioritaire si scalabilité horizontale ou attaques observées.
4. **Observabilité** : logs et smoke tests suffisent au périmètre de soutenance ; Actuator/Micrometer sont une évolution post-stabilisation, avec contrôle d'accès.
5. **Java LTS** : Java 17 est conservé pour stabilité ; Java 21 est un sujet d'étude après soutenance, pas une migration à exécuter tardivement.
6. **Dépendances/CI** : dépendances Maven/npm et actions GitHub doivent être revues mensuellement. Les PR de mise à jour restent soumises aux tests et à revue humaine, sans merge automatique.

## 5. Sources suivies et information recherchée

| Catégorie | Sources | Ce qui est surveillé pour WatYouFace |
|---|---|---|
| Runtime/backend | OpenJDK, Spring Boot, Spring Security, Maven | LTS, breaking changes, correctifs CVE, CSRF/cookie/SecurityFilterChain |
| Données | PostgreSQL, Flyway/Redgate, JDBC | notes de maintenance, compatibilité versions, verrouillage, sauvegarde/migration |
| Frontend | React, Vite, npm, Radix | breaking changes, bundle, code splitting, accessibilité, avis de sécurité |
| Sécurité | OWASP, GitHub Security Advisories, NVD/CVE | BOLA/IDOR, dépendances vulnérables, API/uploads/WebSocket |
| DevOps | Docker, GitHub Actions, Railway, Hostinger | images, permissions, secrets, actions vulnérables, déploiement/rollback |
| Conformité | CNIL, RGAA, RGESN | droits, conservation, accessibilité, sobriété numérique |

## 6. Limites et statut

- Aucun abonnement, Dependabot, scanner SCA, benchmark ou test de compatibilité nouveau n'a été activé durant cette phase.
- Les liens externes sont des sources à surveiller, pas une preuve que toutes les recommandations sont déjà appliquées.
- La veille renforce le raisonnement de conception/sécurité/DevOps ; elle ne couvre pas artificiellement toutes les compétences RNCP.

**Statut : PASS documentaire. Décisions de production : différées sauf les choix déjà versionnés et testés.**
