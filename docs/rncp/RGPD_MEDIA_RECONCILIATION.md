# RGPD-MEDIA-02 — Inventaire, réconciliation et purge sûre

Date : 20 octobre 2026
Périmètre : backend local et répertoire média local. Aucune purge n'a été exécutée sur Railway ou en production.

## Cartographie réelle

| Type | Entité / champ BDD | Répertoire géré | Upload | Suppression / référence active | Accès |
|---|---|---|---|---|---|
| Avatar | `User.avatarUrl` | `avatars/` | `AvatarService` : JPEG/PNG/WebP, 5 Mo, crop 256², nom UUID, sortie JPG | remplacement et effacement de compte : nettoyage post-commit si URL non référencée | public `/media/**` |
| Image Feed | `Post.imageUrl` | `posts/` | `ImageUploadService` via `ImageService` : JPEG/PNG/GIF/BMP, 10 Mo, normalisation JPG, UUID | suppression de post et effacement de compte : nettoyage post-commit si non référencée | public |
| Vidéo Feed | `Post.videoUrl` | `videos/` | `VideoService` : MP4/WebM/QuickTime, 200 Mo, FFmpeg vers MP4 ; nom basé sur id post | suppression de post et effacement de compte : même règle | public |
| Vidéo métier | `Video.url` | `videos/` si URL gérée | entité `Video` ; aucun endpoint d’upload binaire distinct identifié | suppression de vidéo et effacement de compte : même règle | public |
| Image Marketplace | `Listing.image` | `marketplace/` | `MarketplaceImageService` : même politique image, 10 Mo, UUID | remplacement/suppression d’annonce et effacement de compte : même règle | public |

Le répertoire historique `media/images/` et les fichiers temporaires/bruts observés ne correspondent pas à une des quatre références BDD prises en charge. Ils sont classés `UNKNOWN` ou `ORPHAN` par l’outil et ne sont jamais supprimés en dry-run.

## Algorithme

`MediaReconciliationService` collecte les URLs de `User.avatarUrl`, `Post.imageUrl`, `Post.videoUrl`, `Listing.image` et `Video.url`. Chaque URL est normalisée : séparateurs Windows/Linux, préfixe obligatoire `/media/`, répertoire connu, refus des URI externes, chemins absolus, `..`, double séparateur et URLs malformées.

Le parcours filesystem est borné à `MediaStorageService.getBasePath()`. Les liens symboliques, les répertoires inconnus, `avatars/default.*` et `.keep` ne sont jamais candidats à une purge. Chaque fichier régulier reçoit un statut :

- `REFERENCED` : au moins une référence BDD normalisée ;
- `ORPHAN` : sous-répertoire géré et aucune référence BDD ;
- `UNKNOWN` : répertoire inconnu ou lien symbolique ;
- `PROTECTED` : fichier système/default explicitement protégé.

Une URL partagée reste `REFERENCED` tant qu’au moins une entité la référence.

## Exécution sûre

Le composant est désactivé par défaut. Aucune tâche ne s’exécute automatiquement au démarrage normal.

Dry-run local explicite :

```bash
JWT_SECRET='<valeur locale de test>' ./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  '-Dspring-boot.run.arguments=--spring.main.web-application-type=none --app.media.reconciliation.enabled=true --app.media.reconciliation.mode=dry-run'
```

`dry-run` est la valeur de mode par défaut lorsque l’outil est activé. Il retourne les compteurs, tailles, anomalies et entrées inventoriées ; `filesDeleted` reste à zéro.

Une purge future nécessite en plus, explicitement :

```text
--app.media.reconciliation.enabled=true
--app.media.reconciliation.mode=execute
--app.media.reconciliation.confirmation=PURGE_ORPHANS
```

La commande ne doit pas être exécutée contre Railway avant la procédure de production ci-dessous. Il n’existe ni endpoint ADMIN de purge, ni nettoyage automatique au démarrage, ni suppression Railway dans le workflow CI/CD.

## Consistance

La base et le filesystem ne partagent pas de transaction distribuée. Les flux applicatifs normaux suivent donc :

```text
transaction BDD → COMMIT → vérification de références BDD → suppression fichier géré
```

`MediaReferenceCleanupService` enregistre le nettoyage après commit. En cas de rollback, il ne supprime rien. Une erreur I/O est absorbée sans modifier la base ; le fichier reste détectable lors d’une réconciliation ultérieure.

## Tests

`MediaReconciliationServiceTests` vérifie : référencé conservé, orphelin détecté, dry-run non destructif, purge explicite, référence partagée, fichier absent, traversal refusé, fichiers inconnus/protégés.

`MediaReferenceCleanupServiceTests` vérifie : nettoyage seulement sans référence, report après commit, absence de suppression sur rollback et traversal refusé.

Les suppressions et remplacements de post, vidéo, avatar et annonce utilisent désormais le même nettoyage post-commit, sous réserve qu’ils passent par les services métier concernés.

Validation globale locale après cette correction : `./mvnw test` — **120 tests, 0 échec, 0 erreur, 6 ignorés conditionnellement**.

## Dry-run local réalisé

Le 20 octobre 2026, un dry-run local a parcouru le répertoire `media/` du dépôt avec le profil `dev` et une base H2 vide : **94 fichiers scannés, 0 référencé, 91 orphelins candidats, 2 inconnus, 1 protégé, 83 067 979 octets récupérables, 0 supprimé**.

Ce résultat n’est **pas autoritatif pour une purge** : la base H2 vide ne contient pas les références de la base qui a produit les fichiers historiques. Il prouve le comportement non destructif de l’outil et met en évidence des fichiers historiques à réconcilier avec une sauvegarde/snapshot PostgreSQL appropriée.

## Limites et procédure de production future

- L’outil ne devine pas la valeur métier d’un fichier sans référence ; il le classe seulement candidat `ORPHAN`.
- Les uploads Marketplace non finalement attachés à une annonce restent possibles jusqu’à leur inventaire ; aucune suppression immédiate ne doit casser le parcours de création en deux temps.
- Le chemin legacy `media/images/` doit être inventorié et migré/traité séparément avant toute purge.
- Les fichiers publics restent publiquement servis ; la politique de visibilité n’est pas résolue par RGPD-MEDIA-02.

Avant toute purge Railway :

1. sauvegarder PostgreSQL et le volume `/app/media` ;
2. effectuer un dry-run contre le même environnement et conserver le rapport ;
3. examiner les `UNKNOWN`, références invalides et candidats ;
4. obtenir une validation explicite ;
5. exécuter la purge avec les trois paramètres de sécurité ;
6. refaire un dry-run et un contrôle applicatif post-purge ;
7. consigner le résultat sans exposer les données ou chemins personnels dans un rapport public.
