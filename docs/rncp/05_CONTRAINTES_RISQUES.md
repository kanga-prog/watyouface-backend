# Contraintes, exigences non fonctionnelles et risques

## Contraintes

| Catégorie | Contrainte | Incidence projet |
|---|---|---|
| Technique | SPA React/Vite et API Java 17/Spring Boot séparées ; JPA/PostgreSQL ; WebSocket/STOMP ; médias locaux | documenter les contrats et l'architecture répartie ; ne pas refondre de stack |
| Sécurité | JWT, rôles, ownership, validation, secrets, données en entrée | contrôles appliqués serveur et régressions de sécurité obligatoires |
| Juridique | données de compte, avatar et messages potentiellement personnels | politique de confidentialité, droits, minimisation et rétention à définir |
| Accessibilité | interface web responsive, navigation clavier, libellés, retours d'erreur | audit RGAA proportionné et corrections ciblées à planifier |
| Projet | travail individuel, temps limité, échéance 17/10/2026 | réduire le périmètre au MVP et livrer les preuves prioritaires |
| Exploitation | futur déploiement, secrets, base de données, médias, sauvegarde et supervision | Docker, CI/CD, rollback et backup à traiter dans des phases dédiées |

## Exigences non fonctionnelles initiales

| ID | Catégorie | Exigence | Critère vérifiable |
|---|---|---|---|
| NFR-SEC-001 | SEC | Une ressource d'un autre USER est refusée sauf droit explicite. | tests API/service USER A vs USER B renvoient 403/refus. |
| NFR-SEC-002 | SEC | Les secrets ne sont pas codés en dur ni loggés. | config par variables, `.env` local ignoré, revue de configuration. |
| NFR-SEC-003 | SEC | Les entrées sensibles sont validées avant traitement. | cas email invalide, champ requis, prix négatif retournent 400. |
| NFR-SEC-004 | SEC | Les erreurs client ne révèlent ni secret ni stacktrace. | réponses 400/401/403/404/409 contrôlées. |
| NFR-SEC-005 | SEC | Un USER ne modifie/supprime pas le post d'un autre USER ; les règles de like sont appliquées côté serveur. | tests post owner/autre USER et test de la règle cible d'auto-like. |
| NFR-SEC-006 | SEC | Un utilisateur ne consulte ni n'émet dans une conversation dont il n'est pas membre. | tests service/API/STOMP d'appartenance. |
| NFR-PERF-001 | PERF | Le parcours MVP reste utilisable avec un jeu d'essai limité. | temps de réponse observé et volume de jeu d'essai consignés ; seuil à fixer en phase tests. |
| NFR-ACCESS-001 | ACCESS | Les formulaires MVP sont utilisables au clavier et correctement libellés. | test manuel documenté sur login/annonce. |
| NFR-MAINT-001 | MAINT | La logique métier principale est séparée des contrôleurs. | architecture et tests de services documentés. |
| NFR-REL-001 | RELIABILITY | Un paiement démo ne peut pas être exécuté deux fois. | test de double paiement vert, transaction/versionnement à documenter. |
| NFR-UX-001 | UX | Une action invalide informe l'utilisateur sans ambiguïté technique. | messages UI/API contrôlés dans les parcours MVP. |
| NFR-PORT-001 | PORTABILITY | L'application est lançable par une procédure versionnée. | procédure, variables, conteneurisation future et validation exécutée. |
| NFR-PRIV-001 | PRIVACY | Les données de démonstration sont fictives et minimisées. | jeu d'essai non personnel, inventaire des données et politique à produire. |

## Registre des risques

| ID | Risque | Probabilité | Impact | Priorité | Mesure | Statut |
|---|---|---:|---:|---|---|---|
| R-01 | Sur-périmètre fonctionnel avant l'échéance | élevée | critique | P0 | respecter MoSCoW et stabiliser les trois piliers sans fonctionnalités secondaires | ouvert |
| R-02 | Secrets anciennement présents dans Git | moyenne | critique | P0 | rotation JWT/BDD/SMTP, revue historique avant déploiement | ouvert |
| R-03 | Contrôles d'accès régressent | moyenne | critique | P0 | tests AUTHZ/BOLA à chaque évolution | partiellement maîtrisé |
| R-04 | Tests insuffisants ou non reproductibles | moyenne | élevé | P0 | plan de tests, jeux d'essai, CI future, exécution locale | partiellement maîtrisé |
| R-05 | Absence de preuve de déploiement/DevOps | élevée | élevé | P1 | phase Docker, CI/CD, procédure, rollback et preuve | ouvert |
| R-06 | Documentation RNCP incomplète | élevée | élevé | P0 | planning documentaire et source de vérité | en cours |
| R-07 | Token localStorage/XSS et headers absents | moyenne | élevé | P1 | CSP, rate-limit, stratégie session à analyser | ouvert |
| R-08 | Upload média dangereux ou stockage non maîtrisé | moyenne | élevé | P1 | limites, contrôle contenu, antivirus/stockage à évaluer | ouvert |
| R-09 | Données personnelles sans information/rétention | moyenne | élevé | P1 | RGPD : inventaire, mentions, droits, durée | ouvert |
| R-10 | Incohérences marketplace/concurrence | faible à moyenne | élevé | P1 | tests d'état, transaction, verrou et revue montants | partiellement maîtrisé |
| R-11 | Accessibilité non démontrée | moyenne | moyen | P2 | audit ciblé clavier, contrastes, labels, responsive | ouvert |
| R-12 | Dépendances vulnérables | moyenne | moyen à élevé | P1 | audit SCA contrôlé et mises à jour testées | ouvert |
| RISK-PRIVACY-01 | Centraliser les usages ne supprime pas les risques : WatYouFace porte un ensemble de données personnelles plus large. | moyenne | élevé | P1 | minimisation, contrôle d'accès, rétention, sécurité, RGPD et journalisation appropriée | ouvert |

## Traçabilité RNCP

| Compétence | Preuve | Usage |
|---|---|---|
| C4 | registre des risques et mesures | gestion de projet |
| C5 | contraintes exprimées | cahier des charges |
| C6 | contraintes d'architecture | choix techniques |
| C9 | exigences testables | plan de tests |
| C10/C11 | risques d'exploitation et mesures futures | déploiement / DevOps |
