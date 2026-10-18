# Phase 15 — Conformité et qualité

Date d'audit : 18/10/2026. Ce document est un audit technique proportionné au MVP WatYouFace ; il ne constitue ni avis juridique, ni audit RGPD/RGAA certifiant, ni preuve de sauvegarde/restauration production.

Références de cadrage : [principes RGPD de la CNIL](https://www.cnil.fr/fr/comprendre-le-rgpd/les-six-grands-principes-du-rgpd), [registre des activités de traitement](https://cnil.fr/fr/RGPD-le-registre-des-activites-de-traitement), [critères RGAA](https://accessibilite.numerique.gouv.fr/methode/criteres-et-tests/) et [RGESN 2024](https://ecoresponsable.numerique.gouv.fr/publications/referentiel-general-ecoconception/).

## 1. RGPD — PARTIAL

### Cartographie technique observée

| Catégorie | Finalité applicative observée | Visibilité / mesure existante | Conservation |
|---|---|---|---|
| Username, email, mot de passe chiffré | compte, authentification, contact de compte | email et mot de passe ne sont pas exposés par la liste publique ; BCrypt et DTO/admin dédiés | **GAP** : durée non définie |
| Avatar | identification visuelle de profil | URL média publique ; remplacement d'avatar nettoie l'ancien fichier géré | **GAP** : suppression de compte et médias orphelins non définis |
| Posts, commentaires, likes | réseau social et modération | ownership et RBAC ; ADMIN peut modérer | **GAP** : rétention/purge non définie |
| Messages, conversations | messagerie privée | contrôles de membership ; pagination messages | **GAP** : rétention/purge et effet suppression compte non définis |
| Listings, transactions, wallet démo | Marketplace et simulation de paiement | accès acteurs/RBAC, contraintes PostgreSQL, modération `hidden` | **GAP** : politique d'archivage/anonymisation non définie |
| Vidéos et médias | publication de contenus | stockage persistant `/app/media`, contrôles de type/taille selon flux | **GAP** : inventaire/purge des fichiers orphelins absent |
| Logs techniques | diagnostic Spring/Railway/CI | niveau `INFO`, secrets externalisés, CI sans secret affiché | **GAP** : durée, redaction et accès aux logs non formalisés |

### Droits et minimisation

- **Accès : PARTIAL.** L'utilisateur authentifié obtient son profil via `/api/users/me`; aucun export exhaustif de compte, contenus et messages n'est implémenté.
- **Rectification : PARTIAL.** Le pseudo et l'avatar sont modifiables. Une rectification self-service de l'email n'est pas démontrée.
- **Effacement : GAP.** `DELETE /api/users/{id}` existe avec contrôle propriétaire/ADMIN et protection du dernier ADMIN. Toutefois les clés étrangères `wallet`, `listings`, `transaction`, `conversation_users`, `video_share` et d'autres relations ne sont pas couvertes par une politique d'effacement/anonymisation explicite. Une suppression peut donc être bloquée ou laisser des données métier à traiter selon le graphe relationnel. Aucun essai PostgreSQL complet de suppression d'un compte actif n'est revendiqué.
- **Médias : GAP.** Le remplacement d'avatar tente de supprimer l'ancien avatar géré. La suppression d'un post ou d'une annonce ne supprime pas systématiquement l'image/vidéo associée ; aucun job de détection/purge d'orphelins n'existe.
- **Minimisation : PARTIAL.** Les routes publiques utilisateurs renvoient id, username et avatar ; le mot de passe est `@JsonIgnore`; l'email est réservé au profil concerné et au DTO ADMIN. Les finalités, base légale, information des personnes, destinataires/sous-traitants et durées doivent être consignés dans un registre/une politique de confidentialité avant une exploitation réelle.

### Sécurité des données constatée

Secrets dans les variables d'environnement, mots de passe BCrypt, JWT en cookie, CORS à origines explicites, CSRF sur mutations, RBAC/ownership et contraintes PostgreSQL constituent des mesures techniques réelles. PostgreSQL Railway et le volume média sont persistants. La sécurité opérationnelle des accès Railway/Hostinger, des logs et des sauvegardes n'a pas été auditée de façon exhaustive.

## 2. RGAA — PARTIAL

### Corrections ciblées réalisées

- Labels associés aux champs création/édition d'annonce, contenu de publication et modification de pseudo.
- Noms accessibles pour les boutons icône (suppression de fichier, enregistrer/annuler le pseudo) et fermeture de dialogue localisée.
- Alternatives d'images de prévisualisation et avatar améliorées.
- Recherche ADMIN nommée ; erreurs Admin annoncées avec `role="alert"`.
- Tables de modération : `caption`, en-têtes `th scope="col"` et cellule vide avec `colSpan`.
- Focus visible global CSS ; composants Radix Dialog utilisés pour la gestion de focus de modales.

### État et limites

| Sujet | État | Limite |
|---|---|---|
| Labels/noms accessibles des flux corrigés | PASS ciblé | inventaire manuel, pas scan exhaustif |
| Clavier/focus de base | PARTIAL | focus visible observé dans CSS et Dialog Radix ; parcours clavier navigateur non rejoué sur échantillon RGAA |
| Images | PARTIAL | principaux `alt` corrigés ; pertinence de chaque alternative à vérifier visuellement |
| Contraste, zoom, responsive | PARTIAL | responsive existant ; ratios de contraste et zoom 200 % non mesurés |
| Navigation, titres, liens | PARTIAL | titres et navigation présents ; pas de lien d'évitement, plan du site ou audit d'ordre de tabulation complet |
| Audit RGAA complet | NOT TESTED | exige échantillon, tests RGAA applicables, technologies d'assistance et rapport dédié |

## 3. Éco-conception — PARTIAL

Mesure build Vite du 18/10/2026 : JS principal **446,02 kB** brut / **139,57 kB gzip** ; CSS **43,23 kB** brut / **8,44 kB gzip**. Le bundle est actuellement monolithique ; aucun code splitting n'est démontré.

| Mesure favorable | Limite / amélioration prioritaire |
|---|---|
| Images Feed/Marketplace validées, plafonnées à 10 Mo et normalisées en JPEG ; dimensions plafonnées côté serveur. | Les avatars restent sur un flux distinct ; vidéo jusqu'à 200 Mo sans transcodage ni profils de diffusion. |
| Pagination côté ADMIN et messagerie. | Feed, catalogue Marketplace et certaines listes utilisent encore des lectures globales : pagination/chargement progressif à prévoir avant montée en charge. |
| Prévisualisations locales libérées via `URL.revokeObjectURL` dans les flux principaux. | Mesure Lighthouse/WebPageTest, cache statique/HTTP et nombre réel de requêtes non effectués. |
| Pas de bibliothèque de graphique lourde identifiée. | Bundle unique à découper par route si le poids/temps réel le justifie ; supprimer les dépendances inutilisées après audit lockfile. |

Le RGESN recommande notamment un chargement à la demande plutôt qu'un mur de contenu et des images adaptées à leur contexte ; ces objectifs ne sont pas déclarés pleinement atteints.

## 4. Qualité du code — PARTIAL

| Domaine | Observation | Décision |
|---|---|---|
| Backend | Controllers/services séparés, DTO ADMIN dédiés, transactions Marketplace/wallet et handler d'erreurs présents. | PASS ciblé |
| Configuration Hibernate | Dialectes H2/PostgreSQL explicitement déclarés produisaient des avertissements Hibernate inutiles. | Corrigé : autodétection Hibernate, tests PASS |
| Open Session in View | Non désactivé explicitement ; certains contrôleurs sérialisent encore des entités. | PARTIAL : ne pas changer sans audit lazy-loading/DTO complet |
| Flyway | Migrations versionnées et validées PostgreSQL 16. | PARTIAL : avertissement de compatibilité avec versions de moteur non officiellement testées (dont PostgreSQL 18) à traiter par mise à jour/revalidation planifiée |
| Frontend API wallet | Ancien appel `/api/wallet/me/credit` subsistait alors que le crédit manuel est exclusivement ADMIN. | Corrigé : appel retiré, crédit centralisé dans dashboard ADMIN |
| Lint frontend | 0 erreur, 1 avertissement `react-hooks/exhaustive-deps` dans `Admin.jsx`. | PARTIAL : comportement de recherche volontairement débounced ; à refactorer/tester séparément plutôt que masquer l'avertissement |
| Upload avatar | Politique distincte avec MIME WebP annoncé alors qu'aucun décodeur WebP n'est prouvé ; erreur de décodage avatar peut rester 500. | GAP : à aligner/tester dans un lot dédié, sans l'annoncer comme corrigé ici |

## 5. Sécurité résiduelle

| Risque | Impact | Mesure actuelle | Amélioration future |
|---|---|---|---|
| Rate limiting in-memory mono-instance | contournable entre instances/restarts | limite login locale | stockage partagé/proxy/WAF avec métriques |
| JWT sans révocation centrale | token valide reste utilisable jusqu'à expiration | cookie sécurisé, logout client | rotation/révocation serveur si besoin métier |
| Médias sans AV/quarantaine | fichier malveillant ou contenu indésirable | MIME, taille, décodage image Feed/Marketplace, chemin borné | stockage objet, scan/quarantaine, modération |
| Avatar WebP/décodage | format annoncé mais non prouvé, erreur client possible | taille/MIME et chemin borné | politique unifiée et tests avatar |
| STOMP E2E non exhaustif | régression d'autorisation temps réel | contrôles services/tests ciblés | E2E multi-utilisateur STOMP et métriques |
| Formats WebP/AVIF/HEIC | rejet de formats usuels | JPEG/PNG/GIF/BMP validés | ajouter décodeur maintenu seulement après étude/tests |
| CD backend manuel | déploiement moins reproductible | CI Maven/Docker, Railway contrôlé | CD contrôlé avec approbation, rollback documenté |
| Observabilité limitée | détection tardive incident | logs Spring/Railway/CI | health, métriques, alertes, centralisation logs |
| Flyway/moteur récent | risque compatibilité migration | validation PostgreSQL 16 | matrice version + mise à jour Flyway/rejeu avant PostgreSQL 18 |

## 6. Sauvegarde et restauration — GAP

Aucune sauvegarde Railway PostgreSQL ou volume `/app/media`, ni restauration, n'est prouvée dans ce projet. Avant une migration critique, la procédure cible est :

1. Mettre la version applicative et l'heure de référence dans un ticket/runbook.
2. Exporter PostgreSQL avec un compte à privilèges minimaux vers un emplacement chiffré et à accès restreint : `pg_dump --format=custom --file=watyouface-AAAAMMJJ.dump "$DB_URL"`.
3. Archiver le volume média `/app/media` (snapshot Railway ou archive hors plateforme), avec inventaire/horodatage.
4. Vérifier l'intégrité et restaurer **sur une base isolée**, par exemple `pg_restore --clean --if-exists --no-owner --dbname="$TARGET_DB_URL" watyouface-AAAAMMJJ.dump` ; contrôler schéma, comptes de test, listings et références média.
5. Consigner le résultat, la durée, le responsable, la rétention et le chiffrement ; ne jamais sauvegarder les secrets dans le dépôt.

La cohérence DB/fichiers n'est pas atomique : une restauration doit restaurer des sauvegardes prises à une fenêtre cohérente ou être suivie d'une vérification des références média.

## 7. Rollback — PARTIAL

- **Frontend :** identifier un commit connu bon, lancer le workflow GitHub Actions sur ce commit/une branche contrôlée ou revert `main`, puis vérifier les deux smoke tests. Le déploiement reste GitHub Actions → Vite → rsync SSH ; aucun Git natif Hostinger.
- **Backend :** redeployer sur Railway une image/commit connu bon après revue. Les migrations Flyway ne sont pas automatiquement réversibles : privilégier une migration corrective *forward* ; ne restaurer la base qu'après sauvegarde validée et procédure testée.
- Aucun rollback réel de production n'a été exécuté : cette procédure est documentaire et **non prouvée**.

## 8. Observabilité — PARTIAL

| Existant | Manquant / recommandé |
|---|---|
| Logs Spring Boot niveau INFO, logs Railway, sorties CI, erreurs HTTP et smoke tests. | Endpoint health/actuator exposé de façon maîtrisée, métriques HTTP/JVM/DB, alertes, centralisation/recherche logs, dashboard et rétention des logs. |
| Logs applicatifs ciblés sur quelques échecs médias/posts. | Métriques et alertes WebSocket/STOMP ; corrélation requête/utilisateur sans journaliser données sensibles. |

## Verdict de phase

**RGPD : PARTIAL / GAP de gouvernance. RGAA : PARTIAL. Éco-conception : PARTIAL. Qualité technique : PARTIAL renforcée.**

Le MVP peut passer à la veille technologique, à condition de garder les chantiers rétention/effacement, sauvegarde-restauration, observabilité, audit RGAA et conformité juridique dans les livrables restants.
