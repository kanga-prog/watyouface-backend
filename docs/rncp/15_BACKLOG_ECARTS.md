# Backlog des écarts — à ne pas coder dans la phase 2

| ID | Exigence | Constat | Impact | Priorité | Action future | Test attendu | RNCP |
|---|---|---|---|---|---|---|---|
| GAP-AUTH-001 | AUTH-002 / BR-AUTH-002 | le login réellement appelé émet un JWT sans contrôler `acceptedContract`. | accès membre avant acceptation contrat | P0 | appliquer le contrôle dans le flux authentification utilisé. | compte sans contrat → 401/403 ; avec contrat → 200. | C3,C9 |
| GAP-LIKE-001 | BR-LIKE-002 | aucun blocage de l'auto-like. | violation règle feed et intégrité interaction | P0 | refuser côté service avant création/toggle. | auteur like son post → 403/409, aucun Like. | C3,C8,C9 |
| GAP-LIKE-002 | BR-LIKE-001 | endpoints legacy de création/suppression de Like sans ownership ; unicité DB non prouvée. | spoof/doublons/suppression tiers possible | P0 | retirer ou sécuriser endpoints ; contrainte unique post/user. | tiers ne peut supprimer ; doublon impossible. | C3,C8,C9 |
| GAP-CHAT-001 | BR-CHAT-001 | `/api/messages/{conversationId}/all` ne vérifie pas l'appartenance. | fuite de messages entre membres | P0 | supprimer ou protéger avec la règle participant. | non-membre lecture endpoint → 403. | C3,C8,C9 |
| GAP-CHAT-002 | BR-CHAT-002 | message contenu non validé explicitement (vide/longueur). | erreurs, UX et qualité données | P1 | DTO `@Valid` et limites cohérentes REST/STOMP. | vide/trop long → 400/refus STOMP. | C3,C9 |
| GAP-CHAT-003 | BR-CHAT-003 | contrôle STOMP codé mais pas de test E2E. | preuve sécurité incomplète | P1 | test STOMP automatisé ou recette défensive reproductible. | non-membre ne reçoit aucun événement. | C3,C9 |
| GAP-CHAT-004 | CHAT-004 | aucun lien modèle/API entre conversation et annonce. | négociation marketplace non traçable | P2 | décider besoin : lien explicite ou chat générique documenté. | use case négociation validé. | C5,C6,C7,C8 |
| GAP-CHAT-005 | CHAT-002 | création groupe ne garantit pas créateur participant ni validation membres. | conversation incohérente/accès inattendu | P2 | définir règles groupe et valider IDs/titre. | créateur présent, IDs invalides refusés. | C3,C8,C9 |
| GAP-POST-001 | POST-001/002 | post créé/modifié via entrées peu validées ; limite média/contenu incomplète. | qualité, sécurité upload, données incohérentes | P1 | DTO/contraintes et contrôles média serveurs. | contenu invalide/fichier interdit → 400. | C3,C9 |
| GAP-COMMENT-001 | COMMENT-001 | longueur/visibilité de commentaire non définies. | règles feed incomplètes | P1 | décider visibilité, limites et DTO. | post invisible ou texte trop long → refus. | C3,C5,C9 |
| GAP-PROFILE-001 | PROFILE-002/004 | profil via Map, unicité réponse et politique suppression/RGPD non définies. | intégrité/données personnelles | P1 | DTO, erreurs 409, politique conservation/anonymisation. | username dupliqué → 409 ; suppression conforme politique. | C3,C7,C8,C9 |
| GAP-MARKET-001 | MARKET-002 | feed annonces non paginé ; visibilité non définie. | performance/produit imprécis | P2 | décider pagination/visibilité puis implémenter. | page/taille/ordre documentés. | C2,C5,C9 |
| GAP-MARKET-002 | modification/suppression annonce | édition autorisée aussi PENDING et suppression PENDING : règle produit non arrêtée. | changement possible pendant négociation | P2 | décision métier explicite, puis tests d'état. | action PENDING conforme décision. | C3,C9 |
| GAP-RELIAB-001 | NFR-REL-002 | test solde insuffisant et précision monétaire `Double` non traités. | intégrité financière de démonstration | P1 | test transactionnel ; évaluer `BigDecimal` avant toute extension. | solde insuffisant : aucun débit/crédit/Transaction. | C3,C7,C8,C9 |
| GAP-PRIV-001 | NFR-PRIV-001/002 | pas de politique RGPD/rétention ni inventaire finalisé. | risque conformité et discours naïf sur centralisation | P1 | registre de traitements, mentions, durées, droits et journalisation. | revue documentaire validée. | C4,C5 |
| GAP-PERF-001 | NFR-PERF-001 | listes feed/annonces sans pagination. | croissance non maîtrisée | P2 | API paginées et tests/recette. | réponse bornée avec taille max. | C2,C8,C9 |
| GAP-ADMIN-001 | BR-ADMIN-002 | actions ADMIN sans journalisation métier démontrée. | auditabilité/modération | P2 | définir trace minimale et conservation. | action admin produit une trace attendue. | C3,C9 |

## Ordre recommandé

Traiter d'abord les P0 (contrat, likes, fuite messages), puis les P1 qui sécurisent le MVP (validation, tests STOMP, profil, solde, RGPD), avant les décisions P2 de confort, performance et extension.
