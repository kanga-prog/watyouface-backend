# Recette complète Marketplace — état avant déploiement

Date de réévaluation : 2026-10-10. Périmètre : backend et frontend sur `main`, puis corrections isolées sur `fix/marketplace-final-validation`.

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
| MKT-08 | Recharger le navigateur et vérifier la persistance UI | UI rechargée depuis l’API | navigateur graphique indisponible; aucun test E2E | NON TESTE |
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
| `PENDING → ACCEPTED` | Vendeur (ou admin) seulement; acheteur requis | Test API + service |
| `PENDING → REFUSED` | Vendeur (ou admin) seulement | Test API/H2 dédié |
| `ACCEPTED → PAID` | Acheteur associé; wallet démo; une transaction par annonce | service, verrou pessimiste et contrainte DB; tests PostgreSQL non rejoués |
| `PAID → SHIPPED` | Vendeur (ou admin) | test de service de cycle complet |
| `SHIPPED → RECEIVED` | Acheteur associé (ou admin) | test de service de cycle complet |

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
- Upload image: parcours réellement implémenté en deux requêtes — POST multipart authentifié `/api/marketplace/listings/upload` (JPEG/PNG/WebP, 10 Mo max) renvoie une URL `/media/marketplace/...`; puis POST JSON création d’annonce persiste cette URL dans `Listing.image`. Le nom de fichier est généré par UUID, le stockage est `media/marketplace/`, et `/media/**` sert le fichier publiquement pour l’affichage. Un test MockMvc/H2 vérifie upload, fichier servi, création, URL persistée et relecture du détail. La sélection frontend est limitée aux formats acceptés par le backend et rejette les fichiers de plus de 10 Mo.
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

**Blocage levé : NON pour la clôture avant déploiement.** Le paiement et ses invariants sont maintenant prouvés sur PostgreSQL réel; upload, demande d’achat cookie/CSRF et contact vendeur ont des tests automatisés. La recette utilisateur réelle avec deux comptes, confirmation image après refresh, message et captures reste manuelle et n’a pas été exécutée dans cet environnement.
