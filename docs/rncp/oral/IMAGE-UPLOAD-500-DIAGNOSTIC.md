# Diagnostic des HTTP 500 d'upload image

Date : 2026-11-03. Périmètre : lecture du code courant et tests locaux uniquement. Aucun accès Railway, déploiement, écriture PostgreSQL, suppression de média, push ou commit n'a été effectué.

## 1. Symptômes observés

Les symptômes fournis sont des `500 Internal Server Error` sur :

```text
POST /api/users/avatar
POST /api/marketplace/listings/upload
POST /api/posts
```

Les logs Feed fournis contiennent :

```text
Post media processing failed for post XX (IOException)
Post creation failed (IllegalStateException)
```

Le warning navigateur `Permissions policy violation: unload is not allowed in this document` n'a aucun appel, contrôleur, filtre ou gestionnaire d'erreur commun avec les uploads dans le code inspecté. Son lien causal avec les 500 n'est pas démontré.

Le contexte opérationnel confirmé indique que `/app/media`, ses quatre sous-répertoires et l'écriture/suppression par `spring` uid/gid 999 fonctionnent ; cette vérification n'a pas été répétée depuis cette session. Elle écarte les permissions générales et les répertoires absents comme explication principale, mais n'écarte pas un I/O ponctuel (espace disque, erreur de montage, erreur de déplacement, etc.).

## 2. Architecture actuelle des images

| Domaine | Endpoint | Chaîne réelle | Sortie / erreur actuelle |
|---|---|---|---|
| Avatar | `POST /api/users/avatar` | `UserController.uploadAvatar` → `Authz.me` → `AvatarService.saveAvatar` → `Thumbnailator.of(inputStream)` → crop center 256×256 → JPEG `toFile` sous `MediaStorageService.resolvePath(avatars/...)` → `UserService.updateAvatarUrl` | URL `/media/avatars/avatar_<user>_<uuid>.jpg`; `IOException` est captée par le controller et renvoie 500, sans log. |
| Feed | `POST /api/posts` multipart | `PostController.createPost` → `PostService.createPost` → prévalidation MIME/taille → premier `PostRepository.save` → `ImageService.savePostImage` → `ImageUploadService.store(posts, post)` | URL `/media/posts/post_<uuid>.jpg`; `IOException` supprime le Post sauvegardé puis devient `IllegalStateException`, ensuite 500 dans le controller. |
| Marketplace | `POST /api/marketplace/listings/upload` multipart | `MarketplaceController.uploadListingImage` → `MarketplaceImageService.saveListingImage` → `ImageUploadService.store(marketplace, listing)` | URL `/media/marketplace/listing_<uuid>.jpg`; l'`IOException` sort du controller puis atteint le catch-all global 500, sans log. |

### Feed / Marketplace : pipeline réellement partagé

`ImageService` et `MarketplaceImageService` ne font que déléguer à `ImageUploadService.store`. Ce service :

1. refuse fichier vide, taille > 10 MiB et MIME hors JPEG/PNG/GIF/BMP ;
2. crée un `ImageInputStream`, sélectionne un `ImageReader`, compare son format au MIME déclaré et lit la première image ;
3. refuse > 25 000 000 pixels ;
4. redimensionne par Thumbnailator (maximum 1400×1400) ;
5. écrit explicitement un JPEG dans `.<uuid>.jpg.tmp` ;
6. déplace temporaire → destination avec `ATOMIC_MOVE`, puis retente sans `ATOMIC_MOVE` uniquement si celui-ci n'est pas supporté ;
7. nettoie temporaire/destination en cas d'échec de l'écriture/déplacement.

GIF et BMP sont volontairement normalisés en JPEG ; un GIF perd donc son animation après la première frame.

### Avatar : pipeline distinct

`AvatarService` ne passe pas par `ImageUploadService`. Il limite à 5 MiB et admet le MIME déclaré JPEG, PNG ou WebP, mais :

- il ne compare pas le MIME déclaré avec le format réellement décodé ;
- il ne contrôle pas les dimensions/pixels avant décodage ;
- il écrit directement le fichier final avec Thumbnailator, sans temporaire + déplacement atomique ;
- il n'a pas de journalisation de l'IOException.

### Stockage et chemins alternatifs

`MediaStorageService` centralise le répertoire configurable `app.media.base-dir` (sinon `${user.dir}/media`, donc `/app/media` pour le conteneur courant) et les URLs `/media/...`. `WebConfig` sert ce même répertoire sous `/media/**`.

Deux chemins historiques existent mais ne sont pas appelés par les endpoints actuels :

- `PostService.saveMediaFile` écrit directement dans un chemin relatif `media/` avec `Files.write`; aucune référence n'appelle cette méthode.
- `UserService.updateAvatar(userId, MultipartFile)` appelle AvatarService, mais `/api/users/avatar` appelle directement AvatarService puis `updateAvatarUrl`.

Ils ne sont donc pas la cause prouvée des trois requêtes observées, mais constituent une dette de centralisation à traiter séparément.

## 3. Preuves établies

### Runtime Java et dépendances

Le `pom.xml` déclare Thumbnailator 0.4.20 et ne déclare aucun plugin ImageIO WebP, HEIC/HEIF ou AVIF (ni TwelveMonkeys, ni équivalent). Le runtime Java 17 local annonce les lecteurs ImageIO :

```text
BMP, GIF, JPEG/JPG, PNG, TIFF/TIF, WBMP
```

WebP, HEIC/HEIF et AVIF ne figurent pas dans cette liste. Une image WebP 2×2 réelle générée localement a produit `hasReader=false` avec `ImageIO.getImageReaders`.

### Reproduction locale confirmée : PNG transparent Feed / Marketplace

Un PNG valide `TYPE_INT_ARGB` (alpha/transparence) est accepté, décodé et redimensionné, puis échoue sur :

```text
ImageUploadService.store(...)
→ ImageIO.write(normalized, "jpg", temporary.toFile())
→ false
→ IOException("Encodeur JPEG indisponible")
```

Le test `FeedImageUploadTests.transparentPngCannotCurrentlyBeEncodedAsJpeg` confirme ce résultat et l'absence de fichier résiduel. C'est une cause de `IOException` strictement identique à la classe affichée dans les logs Feed : PostService la transforme ensuite en `IllegalStateException`, et PostController en HTTP 500.

Le défaut est commun au Feed et Marketplace car ils passent par le même `ImageUploadService`. Il ne démontre toutefois pas que les fichiers effectivement envoyés sur Railway étaient transparents : ils ne sont pas disponibles dans cette analyse.

### Reproduction locale confirmée : WebP Avatar

`AvatarService` accepte `image/webp`, puis Thumbnailator échoue avec `IOException` sur le WebP réel car aucun décodeur ImageIO n'est disponible. Le test `AvatarUploadPipelineTests.declaredWebpReachesThumbnailatorButFailsWithoutAWebpImageReader` le reproduit. `UserController.uploadAvatar` transforme directement toute `IOException` en HTTP 500.

Un PNG avatar transparent standard, en revanche, est correctement converti par le pipeline Avatar actuel en JPEG 256×256 : cette différence avec Feed/Marketplace est testée.

### Tests exécutés

```text
./mvnw -Dtest=FeedImageUploadTests,MarketplaceImageUploadTests,MediaUploadSecurityTests,AvatarUploadPipelineTests test
```

Résultat : 23 tests, 0 failure, 0 error, 0 skipped.

Couverture existante confirmée : JPEG/PNG/GIF/BMP standards, MIME non autorisé, MIME déclaré différent du contenu, vide, >10 MiB, contenu PNG illisible, persistance Marketplace et authentification. Couverture ajoutée pendant ce diagnostic : PNG transparent Feed et PNG/WebP Avatar réels.

## 4. Hypothèses étudiées

| Hypothèse | Résultat |
|---|---|
| Volume absent ou permission générale d'écriture | Non retenue comme cause principale : le contexte confirmé atteste répertoires et écriture effective ; les chemins I/O spécifiques restent à observer. |
| Feed et Marketplace ont des implémentations image différentes | Éliminée : même `ImageUploadService`. |
| PNG transparent échoue à la conversion JPEG | **Confirmée localement**. |
| WebP Avatar est réellement décodable | Éliminée localement : MIME accepté mais aucun lecteur ImageIO WebP. |
| WebP Feed/Marketplace passe au décodage | Éliminée : il est refusé dès la whitelist MIME. |
| HEIC/HEIF/AVIF sont décodables | Non supportés par les lecteurs ImageIO présents ; les deux premiers sont aussi hors whitelist Feed/Marketplace. Avatar n'accepte pas leurs MIME. |
| JPEG CMYK/progressif ou PNG atypique | Non reproduit ; ce sont des candidats possibles de lecture/transformation, pas des causes attribuées à la production. |
| Fichier corrompu ou MIME mensonger Feed/Marketplace | Le code les transforme en `IllegalArgumentException`, donc 400 dans les routes actuelles, pas le log Feed `IOException` fourni. Pour Avatar, l'absence de vérification réelle peut encore conduire à 500. |
| `ATOMIC_MOVE` Railway non supporté | Pas démontré. Le fallback existe pour `AtomicMoveNotSupportedException`; un autre échec de `Files.move` reste possible. |
| Dépassement de limite multipart serveur | Candidat distinct à vérifier en production. `application-prod.properties` ne fixe aucune limite; la classe Spring Boot 3.3.5 installée initialise 1 048 576 B par fichier et 10 485 760 B par requête. Une éventuelle surcharge Railway n'est pas accessible ici. Un dépassement se produit avant le controller et le catch-all global actuel peut le présenter comme 500. Il ne peut toutefois expliquer le log Feed fourni, puisque celui-ci prouve que ce fichier a atteint PostService. |

## 5. Hypothèses éliminées

- Le warning `unload` n'est pas relié dans le code au traitement Multipart/ImageIO/Thumbnailator.
- Les formats standards RGB JPEG, PNG opaque, GIF et BMP passent les tests Feed/Marketplace.
- Feed/Marketplace ne divergent pas dans leurs validations ou leur écriture.
- Une erreur de décodage ImageIO Feed/Marketplace est reclassée en `IllegalArgumentException`; elle devrait suivre le chemin 400, non le chemin spécifique `IOException` déjà vu dans le log Feed.
- Il n'y a pas de second endpoint actuel qui appelle `PostService.saveMediaFile`.

## 6. Cause(s) racine(s) démontrée(s)

Deux défauts de code reproductibles sont démontrés, mais l'attribution de chacun aux fichiers de production n'est pas possible sans les fichiers fautifs ou une stacktrace Railway complète.

1. **Feed/Marketplace — transparence PNG :** le `BufferedImage` avec alpha issu de Thumbnailator n'est pas aplati vers RGB avant l'écriture JPEG. `ImageIO.write(..., "jpg", ...)` retourne `false`, ce que le code transforme en `IOException("Encodeur JPEG indisponible")`. C'est un candidat directement cohérent avec le message Feed observé.
2. **Avatar — WebP :** le MIME `image/webp` est autorisé, alors que le runtime/les dépendances actuels n'ont pas de lecteur WebP. Thumbnailator échoue et UserController répond 500.
3. **Observabilité/HTTP :** les trois routes peuvent rendre 500 sans stacktrace exploitable : Feed ne logue que le nom de classe, Avatar ne logue pas l'IOException, Marketplace atteint un `@ExceptionHandler(Exception.class)` sans log. Cela empêche d'attribuer le 500 Railway à une étape précise.

La cause exacte de chaque incident Railway reste donc non prouvée : ni les fichiers uploadés, ni la stacktrace complète, ni la configuration multipart effective Railway n'ont été lus.

```text
REPRODUCTION_EXACTE=NON
REPRODUCTION_PAR_HYPOTHESE=OUI
```

## 7. Limites de preuve

- Les fichiers ayant échoué en production ne sont pas disponibles.
- Les logs Railway complets ne sont pas accessibles dans cette session ; la CLI Railway était non authentifiée lors du diagnostic précédent.
- La valeur effective de variables Railway pouvant surcharger la taille multipart n'est pas connue.
- Aucun test local ne reproduit le système de fichiers ou le montage Railway ; les capacités d'écriture générales données dans le contexte sont prises comme preuve opérationnelle fournie.
- Les tests ne couvrent pas actuellement HEIC/HEIF, AVIF, JPEG CMYK/progressif, TIFF ou fichier multipart dépassant la limite du resolver Spring.

## 8. Correctif minimal proposé

**Aucun correctif production n'a été appliqué.** Les changements suivants sont minimaux et doivent être revus, testés puis déployés séparément.

### A. Corriger la conversion JPEG partagée

Avant `ImageIO.write`, convertir explicitement l'image normalisée dans un `BufferedImage.TYPE_INT_RGB`, avec une couleur de fond explicitement choisie (par exemple blanc), puis écrire ce canvas RGB en JPEG. Ce choix rend la transparence déterministe et corrige le cas PNG alpha partagé Feed/Marketplace. Le test actuellement descriptif doit alors devenir un test de succès.

### B. Arrêter d'accepter WebP Avatar sans décodeur

Option minimale sûre : retirer `image/webp` de `AvatarService.ALLOWED` tant qu'aucun décodeur WebP contrôlé et testé n'est livré. Répondre 415 plutôt que laisser Thumbnailator produire 500. Ajouter un plugin ImageIO est une évolution distincte, qui impose des tests sécurité, licence, mémoire et production.

### C. Rendre les logs exploitables sans fuite de données

Changements proposés, non appliqués :

```java
log.error("Post media processing failed for post {}", saved.getId(), e);
log.error("Avatar image processing failed", e);
log.error("Unhandled media request failure", e);
```

La stacktrace est passée comme dernier argument SLF4J. Ne pas inclure contenu binaire, JWT, cookie, mot de passe, e-mail, ni `originalFilename` dans le message. Un handler spécifique `IOException` ou une exception métier média peut éviter le catch-all silencieux Marketplace.

### D. Aligner la limite Multipart sur les politiques métier

Vérifier d'abord les variables Railway effectives. Si aucune surcharge n'existe, définir explicitement en production des limites cohérentes avec les règles actuelles (local utilise 205 MiB par fichier et 255 MiB par requête, tandis que les images sont limitées applicativement à 10 MiB et les vidéos à 200 MiB). Ajouter un handler `MaxUploadSizeExceededException` qui retourne 413.

## 9. Refactor futur éventuel

Ce n'est pas nécessaire pour le premier correctif et ne doit pas bloquer sa livraison. Cible recommandée :

```text
AvatarService ─┐
ImageService ──┼→ ImageUploadService → MediaStorageService → /app/media
Marketplace ───┘
```

`ImageUploadService` recevrait une politique par usage (avatar/feed/marketplace) : MIME réellement décodable, vérification MIME/format, taille, pixels, conversion RGB/JPEG, temporaire+move, erreurs typées et logs cohérents. Avatar conserverait seulement ses paramètres métier de crop/256×256. Le chemin dormant `PostService.saveMediaFile` devrait ensuite être supprimé ou migré après recherche d'usage, jamais en même temps qu'une correction urgente.

## 10. Tests nécessaires

Avant toute livraison du correctif :

1. conserver JPEG, PNG opaque, GIF et BMP standards ;
2. transformer le test PNG transparent en succès, vérifier JPEG lisible et fond choisi ;
3. vérifier absence de fichier temporaire après erreur d'écriture/move (filesystem simulé) ;
4. vérifier 413 pour dépassement du resolver Multipart et taille métier ;
5. vérifier 415 pour MIME explicitement non supporté ;
6. vérifier 422 ou le code décidé pour contenu illisible, MIME/bytes incohérents et pixels excessifs ;
7. vérifier Avatar JPEG/PNG, contenu faux et WebP rejeté ou décodé selon la décision ;
8. ajouter de vraies fixtures contrôlées pour JPEG CMYK/progressif, PNG avec alpha, WebP, HEIC/HEIF/AVIF si et seulement si ces formats sont annoncés ;
9. test d'intégration de la configuration `prod` avec limite multipart explicite ;
10. recette Railway non destructive : images de test connues JPEG, PNG opaque, PNG alpha et fichier rejeté, puis lecture `/media/...`.

## 11. Risques de régression

- Aplatir l'alpha change visuellement les zones transparentes : la couleur de fond doit être une décision produit explicite.
- Retirer WebP Avatar transforme un 500 actuel en rejet explicite pour des clients qui envoient réellement WebP.
- Ajouter un plugin WebP/HEIF/AVIF augmente la surface de dépendances et doit être testé contre fichiers malveillants et bombes d'image.
- Modifier la limite Multipart sans distinguer image/vidéo peut ouvrir inutilement la réception de gros corps ; la limite serveur doit rester alignée sur les plafonds métier.
- Le cleanup de `ImageUploadService` supprime aussi `output` après une erreur : avec UUID c'est normalement isolé, mais ce comportement doit être conservé sous test.

## 12. Plan de validation production

1. Obtenir l'accès Railway en lecture et collecter stacktraces des 500, taille reçue et exception complète, sans afficher les contenus uploadés.
2. Vérifier les valeurs effectives multipart et l'espace disque/montage, sans modifier volume ou données.
3. Comparer la signature/famille des fichiers ayant échoué avec les deux reproductions locales (PNG alpha, WebP Avatar).
4. Choisir le correctif minimal : aplatissage RGB partagé, rejet WebP Avatar, limites multipart et logging ciblé selon les preuves.
5. Ajouter les tests de régression puis exécuter Maven complet et recette locale avec répertoire temporaire.
6. Faire relire le diff ; ne pas modifier Flyway, volume, médias existants ou PostgreSQL.
7. Déployer uniquement par le workflow normal, puis vérifier logs, réponses 2xx/4xx attendues et lecture publique des médias de test.
8. Ne pas supprimer de compte, média réel ou donnée PostgreSQL pour cette recette.

## Résumé machine lisible

```text
IMAGE_VOLUME_PRESENT=YES
IMAGE_VOLUME_WRITABLE_AS_APP_USER=YES
FEED_MARKETPLACE_SHARED_PIPELINE=YES
AVATAR_SHARED_PIPELINE=NO
ROOT_CAUSE_CONFIRMED=NO
ROOT_CAUSE=UNKNOWN; confirmed local defects: transparent PNG to JPEG encoding failure and Avatar WebP decoder absence
LOCAL_REPRODUCTION=YES
LOGGING_INSUFFICIENT=YES
SAFE_FIX_IDENTIFIED=YES
PRODUCTION_CHANGE_PERFORMED=NO
```

## Mise à jour — correctif local appliqué le 2026-11-03

Le correctif minimal identifié a été appliqué localement, sans déploiement à ce stade :

- `ImageUploadService` aplatit maintenant les images alpha sur fond blanc dans un `TYPE_INT_RGB` avant l'encodage JPEG. Le test PNG transparent Feed est devenu un test de succès.
- `AvatarService` n'accepte désormais que JPEG et PNG ; il vérifie le format réellement décodé avant transformation. WebP est retourné en 415 tant qu'aucun décodeur WebP testé n'est livré.
- `MediaUploadException` porte les statuts 400, 413, 415, 422 ou 500 selon la cause ; Feed, Marketplace et Avatar l'utilisent.
- `ApiExceptionHandler` retourne 413 pour `MaxUploadSizeExceededException` et journalise la stacktrace des erreurs serveur ; les logs n'incluent ni contenu, ni cookie, ni JWT, ni nom de fichier utilisateur.
- `application-prod.properties` fixe explicitement `spring.servlet.multipart.max-file-size=205MB` et `spring.servlet.multipart.max-request-size=255MB`, cohérents avec les limites métier image (10 MiB) et vidéo (200 MiB).

Validation locale après correction :

```text
tests média ciblés : 24 passés, 0 erreur, 0 échec
./mvnw test : 143 tests, 0 erreur, 0 échec, 6 PostgreSQL conditionnels ignorés
```

Le déploiement et la recette Railway restent requis avant de modifier `PRODUCTION_CHANGE_PERFORMED`.

## Mise à jour — déploiement et recette Railway le 2026-11-03

Le commit `1b62f2fa825e59171d15b464b3f47f810b5ca77f` (`fix(media): harden image upload processing`) a été poussé sur `main` puis livré exclusivement par le workflow normal **Backend CI** GitHub Actions :

```text
run GitHub Actions : 41718195547
Maven tests              : SUCCESS
Docker build             : SUCCESS
Deploy Railway production: SUCCESS
Smoke test du workflow   : SUCCESS
```

Une recette applicative contrôlée a ensuite été faite contre `https://api.watyouface.com`, avec un compte de smoke test à identifiant unique, créé puis supprimé par les routes applicatives. Aucun compte existant, média existant, volume ou donnée PostgreSQL n'a été modifié manuellement.

| Vérification | Résultat HTTP | Preuve fonctionnelle |
|---|---:|---|
| `GET /api/auth/csrf` | 200 | API et cookie CSRF disponibles |
| Register / login du compte de test | 200 / 200 | cookie d'authentification utilisable |
| `GET /api/users/me` | 200 | session authentifiée vérifiée |
| `POST /api/users/avatar` avec WebP réel | 415 | WebP est rejeté avant stockage |
| `POST /api/marketplace/listings/upload` avec PNG transparent | 200 | conversion PNG alpha → JPEG réussie |
| Création puis suppression de l'annonce de test | 200 / 200 | nettoyage applicatif exercé |
| `POST /api/posts` avec PNG transparent | 200 | ancien chemin Feed reproduit sans 500 |
| Suppression du post de test | 204 | nettoyage applicatif exercé |
| Suppression du compte de test | 200 | compte de recette retiré par l'API |

Cette recette démontre que les deux défauts locaux corrigés se comportent correctement en production : le PNG transparent ne provoque plus l'échec d'encodage JPEG, et le WebP Avatar non décodable retourne désormais 415. Elle ne permet pas d'attribuer rétrospectivement chaque 500 historique aux mêmes fichiers, car leurs originaux et stacktraces n'étaient pas disponibles.

Les logs des échecs média serveur sont maintenant passés avec l'exception SLF4J complète, sans inclure de contenu binaire, cookie, JWT, mot de passe, e-mail ou nom de fichier client. La recette réussie ne déclenche pas de stacktrace ; l'observation de ces logs nécessite un échec serveur réel, qui n'a volontairement pas été provoqué en production.

## Résumé machine lisible — état après déploiement

```text
IMAGE_VOLUME_PRESENT=YES
IMAGE_VOLUME_WRITABLE_AS_APP_USER=YES
FEED_MARKETPLACE_SHARED_PIPELINE=YES
AVATAR_SHARED_PIPELINE=NO
ROOT_CAUSE_CONFIRMED=NO
ROOT_CAUSE=UNKNOWN; two concrete defects fixed and reproduced successfully in production, but historic failing files/stacktraces were unavailable
LOCAL_REPRODUCTION=YES
LOGGING_INSUFFICIENT=NO
SAFE_FIX_IDENTIFIED=YES
PRODUCTION_CHANGE_PERFORMED=YES
```
