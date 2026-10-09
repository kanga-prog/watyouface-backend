# Matrice sécurité backend — Phase 10

| Risque | Avant / constat | Contrôle présent après inspection/correction | Test / preuve | Statut |
|---|---|---|---|---|
| BOLA contrat | userId client pouvait influencer l'acceptation | identité résolue par Authz du contexte authentifié | MockMvc User A/B | PASS |
| Self-like / Like legacy | auteur pouvait potentiellement liker son contenu; routes historiques exposées | garde service; Like résout désormais l'identité par ID authentifié; routes legacy renvoient 410 | MockMvc : autre USER 200, self-like 403, legacy 410 | PASS API MockMvc/H2 |
| Ownership Post | accès propriétaire à confirmer | création prend l'auteur authentifié; update/delete owner ou ADMIN | MockMvc création, owner update/delete, non-owner 403 + tests service | PASS API MockMvc/H2 |
| Fuite Chat REST | route legacy de lecture à risque | contrôle membership sur chemins REST vérifiés | tests MockMvc membre/non-membre | PASS REST |
| Souscription Chat STOMP | précédemment non prouvée | interceptor filtre destination conversation via principal et membership | test direct de l'intercepteur : membre autorisé, non-membre bloqué | PASS composant; PARTIEL transport E2E non testé |
| Validation message | DTO REST/STOMP et garde service | `MessageDTO` `@NotBlank`/`@Size(2000)`, identité sender du principal | MockMvc message blanc; tests DTO/service/handler blank + overlong; forge senderId rejetée | PASS règles serveur; PARTIEL broker E2E |
| Paiement double | garde service et verrou existaient; contrainte PG non appliquée | contrainte réelle `uq_transaction_listing_id`, verrou `findByIdForUpdate`, garde service | PostgreSQL 16.15 : doublon rejeté SQLSTATE 23505; 4 tests PG PASS | PASS DB |
| Transaction/paiement concurrence | concurrence non prouvée | `@Transactional`, verrou pessimiste et UQ | deux appels parallèles PostgreSQL : un commit seulement, état/soldes/transaction cohérents | PASS PostgreSQL |
| Solde et rollback paiement | transfert atomique à confirmer | `@Transactional` et service de transfert conservés | PostgreSQL commit : acheteur 100→70, vendeur 0→30; échec injecté après transfert restaure soldes, transaction et état | PASS PostgreSQL |
| Erreurs HTTP | plusieurs exceptions étaient regroupées comme 400 ou détails variables | 400/403/404/409 dédiés; 500 générique | MockMvc validation, 403, 404, 409 | PARTIEL (test 500 générique manquant; contrôleurs legacy à harmoniser) |
| Secrets | références env dans configuration actuelle | aucun secret imprimé dans les résultats; variables absentes du sandbox | inspection env names seulement | NON AUDITÉ HISTORIQUE/rotation requise selon déploiement |
| Upload média | contrôles d'accès/type/chemin ne font pas partie du lot final démontré | ne pas revendiquer de correction | audit dédié nécessaire | NON PROUVÉ |

Le contrôle de visibilité UI n'est pas une autorisation. Les contrôles critiques restent serveur. H2 ne démontre pas les mêmes garanties de verrouillage que PostgreSQL.
