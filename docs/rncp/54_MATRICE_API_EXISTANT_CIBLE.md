# Matrice API actuel / cible

| Endpoint / flux | Actuel observé | Cible | État | Action future |
|---|---|---|---|---|
| login | `/api/auth/login`, JWT généré | contrat accepté contrôlé dans tous flux | PARTIEL | GAP-AUTH-001, tests |
| post CRUD | routes `/api/posts` et ownership | DTO/validation homogènes, 404 cohérent | PARTIEL | validation + tests |
| likes | `/toggle` et `/post/{postId}` | une règle serveur : pas auto-like, unicité | NON CONFORME | issue #2 |
| commentaires | routes dédiées | validation/ownership/erreurs homogènes | PARTIEL | issue #11 |
| conversation/message | REST + STOMP participant | aucune lecture legacy hors participant | PARTIEL | issue #3/#12/#13 |
| listing cycle | routes transitions observées | contrats DTO, transitions testées | PARTIEL | issue #15 |
| paiement | verrou pessimiste + transaction | contrainte BDD, solde/test concurrence | PARTIEL | migrations/tests |
| wallet credit | `/me/credit` hardening présent | ADMIN exclusivement + UI cohérente | PARTIEL | test/UX |
| media | upload et `/media/**` | validation type/taille, accès selon besoin | PARTIEL | politique médias |
| erreurs | handler 400/403/409 | 401/404/500 uniformes | PARTIEL | compléter handler/contrats |
