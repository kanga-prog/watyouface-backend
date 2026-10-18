# Recette complète Marketplace — historique et référence consolidée

Les sections jusqu'à « État de référence » conservent les constats datés de la
recette initiale (10–17/10/2026). Elles ne décrivent plus le contrat courant.
La référence actuelle est la section du 18/10/2026, complétée par
`BE-E2E-01-production-recipe.txt` et `RNCP6_SOURCE_DE_VERITE.md`.

Date de réévaluation initiale : 2026-10-10. Périmètre historique : backend et frontend sur `main`, puis corrections isolées sur `fix/marketplace-final-validation`.

## Périmètre observé

L’API réelle est `/api/marketplace/listings` : création, lecture, mise à jour, suppression et actions `request`, `accept`, `refuse`, `pay`, `ship`, `receive`. La création persiste le vendeur authentifié et impose `AVAILABLE`; le DTO exige un titre non blanc (120 caractères max), une description de 2000 caractères max et un prix positif. La réponse de création est HTTP 200 (pas 201). Le paiement utilise les wallets de démonstration, une transaction SQL et un verrou pessimiste sur l’annonce.

Le Marketplace ne crée pas de contrat de vente dédié ni de lien technique entre une annonce et une conversation. Le bouton de contact ouvre le chat générique entre utilisateurs. Le contrat présent dans le parcours d’inscription est le contrat général de la plateforme, pas un contrat de transaction Marketplace.

## Scénarios

Statuts : **PASS** = preuve automatisée au niveau indiqué; **PARTIEL** = une partie est prouvée mais pas le parcours réel complet; **NON TESTE** = aucune preuve d’exécution dans cet environnement. Les captures navigateur sont absentes : aucun statut visuel n’est déclaré PASS.

| ID | Précondition et action | Attendu | Preuve/niveau disponible | Résultat |
|---|---|---|---|---|
| MKT-01 | Utilisateur authentifié crée une annonce valide | HTTP 200; annonce persistée, `AVAILABLE`, vendeur issu de l’auth | `SecurityApiIntegrationTests.marketplaceListingApiPersistsAuthenticatedSellerAndAvailableState` — API/H2 | PASS |
| MKT-02 | Créer avec titre blanc ou prix nul/négatif | HTTP 400, aucune annonce invalide | `marketplaceListingCreationRequiresAuthenticationAndValidFields`; validation `ListingDTO` — API/H2 | PASS |
| MKT-03 | Créer sans authentification | HTTP 401 avec CSRF de test valide | même test — API/H2 | PASS |
| MKT-04 | Champs obligatoires invalides | HTTP 400 | test MKT-02 et DTO Bean Validation — API/H2 | PASS |
| MKT-05 | Consulter la liste | liste issue du repository | endpoint GET constaté; pas de scénario dédié de liste non vide | PARTIEL |
| MKT-06 | Consulter une annonce créée | détail et statut persisté | test MKT-01 relit le détail — API/H2 | PASS |
| MKT-07 | Lire un ID absent | HTTP 404 | `missingMarketplaceResourceIs404AndInvalidPaymentStateIs409` — API/H2 | PASS |
| MKT-08 | Recharger le navigateur et vérifier la persistance UI | UI rechargée depuis l’API | **Constat historique du 10/10** : navigateur graphique indisponible | HISTORIQUE — remplacé par recette manuelle 18/10, traces à archiver |
| MKT-09 | Vendeur effectue une action de son annonce | action autorisée selon état | API/H2: vendeur modifie/supprime; cycle API accepte/expédie — navigateur non exécuté | PARTIEL |
| MKT-10 | Un tiers tente une action réservée au vendeur | HTTP 403 | tests API/H2 modification/suppression/acceptation + service — UI non testée | PASS API |
| MKT-11 | Utilisateur anonyme tente une action protégée | HTTP 401 | création anonyme MKT-03; endpoint de transition anonyme non dédié | PARTIEL |
| MKT-12 | Acheteur demande l’annonce `AVAILABLE` | transition persistée vers `PENDING`; vendeur ne peut acheter sa propre annonce | test API/H2 cycle complet et `marketplaceSellerCannotRequestPurchaseOfOwnListing` | PASS API/H2 |
| MKT-13 | Vendeur accepte depuis `PENDING` | `ACCEPTED`; tiers 403; mauvais état 409 | test API acceptation + séquence service — API/H2/service | PASS |
| MKT-14 | Vendeur refuse depuis `PENDING` | `REFUSED`; action non-vendeur refusée | `marketplaceRefusalIsSellerOnlyAndCannotBeRepeated` — API/H2 | PASS API |
| MKT-15 | Acheteur paie depuis `ACCEPTED` | transaction, transfert wallet démo, statut `PAID` atomiques | cycle 2 utilisateurs via MockMvc/H2 vérifie transaction, soldes et statuts; PostgreSQL non rejoué | PARTIEL (H2 PASS) |
| MKT-16 | Solde insuffisant | refus et rollback total | `insufficientDemoWalletLeavesListingAndTransactionUnchanged` — API/H2; test PostgreSQL dédié ignoré actuellement | PARTIEL (H2 PASS) |
| MKT-17 | Répéter le paiement | un seul paiement; garde service et contrainte unique DB | seconde requête du cycle API H2 refusée sans nouvelle déduction; PostgreSQL non rejoué | PARTIEL (H2 PASS) |
| MKT-18 | Deux paiements concurrents | un succès, aucun double débit/crédit | test PostgreSQL spécialisé présent mais non rejoué; historique RNCP séparé, non utilisé comme résultat courant | PARTIEL |

## Cycle métier et autorité

| Transition | Règle observée | Preuve actuelle |
|---|---|---|
| `AVAILABLE → PENDING` | Acheteur authentifié non vendeur; demande uniquement si disponible | Test de service de cycle complet |
| `PENDING → ACCEPTED` | Vendeur seulement; acheteur requis | Test API + service |
| `PENDING → REFUSED` | Vendeur seulement | Test API/H2 dédié |
| `ACCEPTED → PAID` | Acheteur associé; wallet démo; une transaction par annonce | service, verrou pessimiste et contrainte DB; tests PostgreSQL non rejoués |
| `PAID → SHIPPED` | Vendeur seulement | test de service de cycle complet |
| `SHIPPED → RECEIVED` | Acheteur associé seulement | test de service de cycle complet |

Les autres transitions sont refusées par la vérification d’état du service (`IllegalStateException`, normalement HTTP 409). Le jeu complet de transitions interdites n’a pas été exécuté comme test API exhaustif.

## Chat, contrat et deux utilisateurs

- Le chat reste générique; membership de conversation est contrôlé par les services Chat, mais aucun `listing_id` ne relie une conversation à une annonce. « Contacter le vendeur » utilise l’API générique de conversation.
- Aucun contrat spécifique de vente, d’acceptation acheteur/vendeur ou de livraison n’est rattaché à `Listing`.
- Le cycle complet a désormais un test d’intégration API MockMvc/H2 avec deux identités distinctes et soldes fictifs, mais aucun parcours navigateur UI à deux comptes n’a été exécuté. Ce test API ne vaut pas preuve PostgreSQL ni E2E navigateur.

## Réexécution finale (2026-10-10)

- Backend `./mvnw clean test`: **PASS**, 65 tests rapportés, dont 60 exécutés avec succès et 5 ignorés, 0 échec, 0 erreur (configuration PostgreSQL dédiée absente). Cela inclut les tests d’intégration API/H2; cela ne constitue pas une preuve PostgreSQL.
- PostgreSQL installé: client 16.15. Le serveur local ne répond pas (`pg_isready`: aucune réponse sur `/var/run/postgresql:5432`); `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` et `JWT_SECRET` ne sont pas définis dans cet environnement. Les tests PostgreSQL, la contrainte `uq_transaction_listing_id` sur le moteur réel, le rollback DB et la concurrence DB sont donc **NON EXÉCUTÉS** pendant cette réévaluation.
- Frontend: ESLint PASS (un avertissement préexistant dans `Admin.jsx`), Vitest **9 fichiers / 22 tests PASS**, build Vite PASS. `npm audit --omit=dev`: **0 vulnérabilité**.
- PR backend #32 et frontend #7: ouvertes et mergeables; GitHub ne rapporte aucun check CI pour ces branches.
- Aucun navigateur graphique n’est disponible dans l’environnement. Les captures et scénarios navigateur restent **MANUELS / NON TESTÉS**; aucune preuve visuelle n’est revendiquée.

## Correctifs UI et réévaluation (2026-10-12)

- PostgreSQL 16.15, base `watyouface_db`, utilisateur `watuser`: connexion locale confirmée. La suite ciblée `PostgresPaymentIntegrationTests` a été rejouée avec **5 tests, 0 échec, 0 erreur, 0 ignoré**. Elle couvre commit, contrainte unique `uq_transaction_listing_id`, rollback forcé, solde insuffisant et paiements concurrents; la contrainte a rejeté la seconde écriture. Les valeurs sensibles ne sont pas consignées.
- Cause des skips JUnit: `@EnabledIfEnvironmentVariable` exige `DB_URL` correspondant à `jdbc:postgresql:.*`, `DB_USERNAME` non vide, `DB_PASSWORD` défini (vide permis) et `JWT_SECRET` d’au moins 32 caractères. Sans ces variables dans le processus Maven, les cinq méthodes sont ignorées même si PostgreSQL fonctionne. Exécuter séparément la classe PostgreSQL: l’injection de `DB_URL` dans toute la suite ferait utiliser l’URL PostgreSQL à des tests `dev` qui attendent H2.
- Upload image — **jalon historique du 17/10/2026** : le premier correctif a réduit le contrat Marketplace à JPEG/PNG (10 Mo) et refusé WebP. Il a été validé par le run CI 38943060289. Ce jalon explique l'incident WebP, mais il est **supersédé** par la politique commune actuelle Feed/Marketplace : JPEG/PNG/GIF/BMP, 10 Mo, décodage réel et sortie JPEG, décrite plus bas. WebP, AVIF et HEIC/HEIF restent volontairement refusés.
- Demande d’achat: `POST /api/marketplace/listings/{id}/request`. Le transport frontend commun envoie `credentials: "include"`, récupère le jeton CSRF et le transmet dans `X-XSRF-TOKEN`; le cookie auth est joint par le navigateur. Le test avec cookie valide passe à `PENDING`; sans cookie, la réponse est 401; avec cookie mais sans CSRF, 403. Ainsi, le 401 observé correspond au refus par Spring Security faute d’un principal établi (cookie absent, invalide ou expiré). Le code ne permet pas d’identifier lequel s’est produit dans le navigateur sans examiner la requête réseau concernée.
- Contact vendeur: le clic créait déjà/récupérait la conversation privée via `/api/conversations/with/{sellerId}`, mais la page Marketplace ne fournissait aucun callback de navigation. Correction UI: passage vers `/messages` avec l’identifiant de conversation; la page Chat sélectionne cette conversation. Un test API confirme création/réutilisation et membership vendeur + acheteur. La conversation demeure générique; aucun lien `Listing`–`Conversation` n’est ajouté. Le droit de lecture/envoi reste borné aux membres par les contrôles Chat existants.
- Résultats complets: backend `./mvnw clean test` **79 tests rapportés, 74 succès, 0 échec/erreur, 5 tests PostgreSQL ignorés** car variables absentes pour cette exécution H2; la suite PostgreSQL séparée est **5/5 PASS**. Frontend lint PASS (un avertissement préexistant `Admin.jsx`), 10 fichiers / 30 tests PASS, build PASS, `npm audit --omit=dev`: 0 vulnérabilité.
- Recette visuelle réelle non exécutée: pas de navigateur graphique détecté. L’image persistée/servable et les actions sont prouvées par API/tests; image après refresh navigateur, saisie/envoi message et captures restent MANUELS.

## Limites de la passe

- PostgreSQL local n’est pas disponible (`pg_isready`: aucune réponse sur le socket local); les tests PostgreSQL dédiés n’ont donc pas été rejoués. H2/service ne vaut pas preuve PostgreSQL.
- Aucun navigateur graphique n’est disponible dans cette passe : refresh, affichage des statuts, erreurs UX et captures restent à exécuter manuellement.
- Une lecture de code a identifié une course potentielle entre transitions concurrentes autres que le paiement. La branche de validation applique désormais le verrou pessimiste existant aux mutations d’annonce; faute de PostgreSQL disponible, la concurrence de ces transitions reste non prouvée par un test DB réel.
- Vérification automatisée courante : voir « Correctifs UI et réévaluation » ci-dessus (backend 79 rapportés, 74 PASS, 5 ignorés; PostgreSQL séparé 5/5; frontend lint, 30 tests, build et audit runtime PASS).
- Aucune capture PNG n’est fabriquée ou revendiquée. Les preuves attendues MKT-01 à MKT-08 sont à produire après recette navigateur réelle.

## Résultat de clôture Marketplace

**Clôture historique (avant la recette du 18/10) :** blocage upload levé, mais E2E navigateur encore PARTIEL. Ce statut est remplacé par la recette production consolidée ci-dessous : les parcours sont fonctionnels validés, tandis que les captures/traces réutilisables restent PARTIAL.

## État de référence — administration, médias et migrations (18/10/2026)

Cette section remplace les mentions antérieures contradictoires sur le contrat d'image, les privilèges ADMIN et les tests PostgreSQL.

### ADMIN, RBAC et wallets

- L'unique compte ADMIN existant (`demo`) est **modérateur et gestionnaire du wallet de démonstration**. Aucun second ADMIN n'a été créé.
- Toutes les routes `/api/admin/**` exigent ADMIN : non authentifié → **401**, USER → **403**, ADMIN autorisé ; les mutations restent soumises à CSRF.
- Le seul crédit manuel est `POST /api/admin/users/{userId}/wallet/credit`. Il exige un montant strictement positif, est transactionnel et renvoie le solde courant. Aucune route USER ne crée, crédite ou fixe directement un solde.
- La route de changement de rôle a été retirée. L'inscription force le rôle USER et les DTO ne permettent pas d'assigner `role` (pas de mass assignment).
- La suppression de son propre compte ADMIN et la suppression du dernier ADMIN sont refusées explicitement. Le rôle du dernier ADMIN ne peut pas être rétrogradé.

### Modération Marketplace et intégrité transactionnelle

- L'ADMIN n'est plus un acheteur/vendeur universel : il ne peut pas demander, accepter, refuser, payer, expédier ou réceptionner à la place des parties.
- Une annonce `AVAILABLE` ou `REFUSED` peut être supprimée physiquement par la modération.
- Pour `PENDING`, `ACCEPTED`, `PAID`, `SHIPPED` et `RECEIVED`, la modération positionne `hidden=true` au lieu de supprimer la ligne : la transaction et ses références sont conservées.
- Une annonce masquée est invisible à un tiers dans le catalogue ; vendeur, acheteur concerné et ADMIN la conservent accessible. Cette règle permet la modération sans rompre le cycle transactionnel.

### Politique commune d'upload Feed et Marketplace

- Formats réellement acceptés : **JPEG/JPG, PNG, GIF et BMP**, limite **10 Mo**. WebP, AVIF et HEIC/HEIF sont volontairement refusés car aucun décodeur Java fiable correspondant n'est embarqué.
- Le MIME est filtré puis le contenu est réellement décodé avec ImageIO. Un fichier vide, falsifié ou corrompu produit une erreur client HTTP 400, jamais une erreur 500 générique.
- GIF : seule la première image est normalisée ; BMP : normalisé. Le fichier final est un **JPEG** sous un nom UUID, placé dans le répertoire media prévu.
- L'écriture passe par un fichier temporaire puis un déplacement final après succès ; le temporaire est supprimé lors d'un échec. Les protections de chemin existantes sont conservées.

### PostgreSQL et Flyway

- `PostgresPaymentIntegrationTests` a été exécuté séparément contre PostgreSQL 16 : **5/5 PASS, 0 failure, 0 error, 0 skipped**.
- La suite Maven globale actuelle contient **104 tests rapportés**, dont **5 tests PostgreSQL conditionnels ignorés** lorsque leurs variables dédiées ne sont pas injectées. Ce run ne doit pas être présenté comme l'exécution des cinq tests PostgreSQL ; la preuve dédiée ci-dessus les couvre.
- V2 ajoute `listings.created_at` avec une valeur par défaut compatible table non vide. V3 ajoute `listings.hidden` avec défaut `false`. Les migrations ont été validées sur PostgreSQL 16 avec **37 listings existants**, sans suppression de donnée et avec **0 NULL** pour `created_at` et `hidden` après migration.

### Limites de recette

Les tests unitaires/intégration et les migrations sont prouvés. La recette fonctionnelle E2E de production décrite ci-dessous a ensuite été validée manuellement ; les captures et traces HTTP réutilisables pour le jury restent à ajouter, notamment pour les uploads.

## Recette fonctionnelle production consolidée (18/10/2026)

La validation ci-dessous est une recette manuelle de production rapportée après les CI vertes. **PASS fonctionnel** signifie que le comportement a été validé ; lorsqu'aucune capture/trace n'est encore archivée, la preuve documentaire est explicitement **PARTIAL**.

| Domaine | Scénario / résultat | Verdict fonctionnel | Preuve documentaire |
|---|---|---|---|
| Infrastructure | Frontend Hostinger déployé par GitHub Actions ; Backend Railway déployé ; Flyway V2 et V3 appliquées ; Spring Boot/Tomcat démarré. | PASS | PASS : run frontend et recette déclarée ; journal Railway/captures à centraliser |
| Smoke API | `GET /api/users/me` sans session → 401 ; `GET /api/auth/csrf` → 200 avec `XSRF-TOKEN`. | PASS | PARTIAL : résultat fonctionnel validé, trace HTTP à archiver |
| ADMIN / wallet | Connexion `demo`, dashboard, liste utilisateurs, solde et crédit par ADMIN. Crédit USER interdit par architecture ; dernier ADMIN protégé et changement de rôle retiré par tests. | PASS | PASS tests backend ; PARTIAL pour captures de l'interface |
| Feed | Listes Admin, suppression d'un post et suppression d'un commentaire. | PASS production | PARTIAL : captures/actions réseau à ajouter |
| Marketplace nominal | `AVAILABLE → PENDING → ACCEPTED → PAID → SHIPPED → RECEIVED`. | PASS production | PARTIAL : scénario validé, traces/captures à ajouter |
| Marketplace alternatif | `AVAILABLE → PENDING → REFUSED`. | PASS production | PARTIAL : scénario validé, traces/captures à ajouter |
| Négatifs Marketplace | Solde insuffisant → 409 ; ADMIN sans bypass vendeur/acheteur ; tiers exclu d'un listing masqué. | PASS production/tests | PASS pour tests automatisés ; PARTIAL pour trace production 409 |
| Modération Marketplace | `REFUSED` supprimé physiquement. Listing engagé masqué (`hidden=true`), absent du catalogue tiers et conservé au dashboard ADMIN sans casser la transaction. | PASS production/tests | PARTIAL : captures/trace à ajouter |
| Upload JPEG | Upload réel à 10 Mo maximum, normalisé en JPEG selon la politique serveur. | **FONCTIONNEL VALIDÉ** | **PREUVE VISUELLE PRODUCTION À AJOUTER** |
| Upload PNG | Upload réel à 10 Mo maximum, normalisé en JPEG selon la politique serveur. | **FONCTIONNEL VALIDÉ** | **PREUVE VISUELLE PRODUCTION À AJOUTER** |
| Upload GIF | Upload réel à 10 Mo maximum ; première image normalisée en JPEG. | **FONCTIONNEL VALIDÉ** | **PREUVE VISUELLE PRODUCTION À AJOUTER** |
| Upload BMP | Upload réel à 10 Mo maximum, normalisé en JPEG. | **FONCTIONNEL VALIDÉ** | **PREUVE VISUELLE PRODUCTION À AJOUTER** |
| Formats volontairement refusés | WebP, AVIF et HEIC/HEIF ne sont pas supportés. | PASS par contrat/tests | PASS tests ; pas de support à démontrer |

Cette recette ne prétend pas prouver un CD backend automatique : le déploiement Railway reste contrôlé/manuellement déclenché. Elle ne remplace pas les futures captures, mais établit le statut fonctionnel validé pour les scénarios listés.
