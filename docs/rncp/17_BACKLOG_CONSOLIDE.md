# Backlog consolidé — WatYouFace jusqu'au 17/10/2026

**Légende effort :** XS ≤ 2 h, S ≤ 4 h, M ≤ 1 j, L 1–2 j. Les estimations sont des hypothèses de planification à ajuster après le premier lot technique. `TODO` signifie non démarré ; aucune ligne de ce backlog ne modifie le code par elle-même.

| ID | Epic | Titre | Description / résultat attendu | Prio | RNCP | Dépendances | Effort | Risque | Preuve attendue | Statut |
|---|---|---|---|---|---|---|---|---|---|---|
| PRJ-001 | EPIC-01 | Valider périmètre | Geler MVP trois piliers et décisions ouvertes utiles aux P0. | P0 | C4,C5 | phases 1-2 | XS | sur-périmètre | source de vérité à jour | READY |
| UX-001 | EPIC-07 | Wireframes MVP | Écrans accès, feed, chat, annonces/cycle ; parcours et erreurs. | P1 | C2,C5 | PRJ-001 | M | UX tardive | wireframes annotés | TODO |
| UX-002 | EPIC-07 | Maquettes/prototype | Maquettes responsive et prototype des parcours MUST. | P1 | C2,C5,C9 | UX-001 | L | temps | lien/prototype + recette | TODO |
| ARCH-001 | EPIC-08 | Architecture cible | Diagramme couches, flux REST/STOMP, médias, frontières de sécurité et ADR. | P1 | C6 | PRJ-001 | M | incohérence | diagramme + ADR | TODO |
| DATA-001 | EPIC-09 | MCD/MLD/MPD | Modéliser User, contenu, conversation, listing, wallet, transaction et contraintes. | P0 | C7,C8 | exigences phase 2 | M | données incorrectes | MCD/MLD/MPD revus | TODO |
| DATA-002 | EPIC-09 | Migrations versionnées | Introduire migrations reproductibles à partir du modèle validé. | P0 | C7,C8,C10 | DATA-001 | L | schéma existant | scripts + exécution vierge | TODO |
| DATA-003 | EPIC-09 | Jeu d'essai | Créer données fictives couvrant rôles, états marketplace et erreurs. | P1 | C7,C9 | DATA-002 | M | données personnelles | seed/notice d'usage | TODO |
| AUTH-001 | EPIC-05 | Contrat au login | Corriger GAP-AUTH-001 : refuser accès membre sans contrat accepté. | P0 | C3,C9 | PRJ-001 | S | régression accès | tests sans/avec contrat | TODO |
| SOCIAL-001 | EPIC-02 | Interdire auto-like | Corriger GAP-LIKE-001 côté serveur. | P0 | C3,C8,C9 | règles BR-LIKE | S | contournement | test auteur 403/409 | TODO |
| SOCIAL-002 | EPIC-02 | Sécuriser legacy Like | Retirer ou sécuriser endpoints legacy ; garantir ownership/unicité. | P0 | C3,C8,C9 | SOCIAL-001, DATA-001 | M | compatibilité front | tests tiers/doublon | TODO |
| CHAT-001 | EPIC-03 | Protéger lecture diagnostic | Corriger/supprimer `/api/messages/{id}/all` sans appartenance. | P0 | C3,C8,C9 | règles chat | XS | fuite messages | test non-membre 403 | TODO |
| CHAT-002 | EPIC-03 | Valider messages | Contenu obligatoire et borné REST/STOMP. | P1 | C3,C9 | CHAT-001 | S | incohérence canaux | tests vide/trop long | TODO |
| CHAT-003 | EPIC-03 | Tester STOMP | Automatiser ou formaliser recette défensive abonnement tiers. | P1 | C3,C9 | CHAT-001 | M | preuve faible | test/trace STOMP | TODO |
| CHAT-004 | EPIC-03 | Décider chat annonce | Trancher lien Conversation-Listing ou chat générique documenté. | P2 | C5,C6,C7 | PRJ-001 | S | surconception | ADR / exigence mise à jour | TODO |
| SOCIAL-003 | EPIC-02 | Validation posts/commentaires | Définir/implémenter DTO, limites, visibilité et tests. | P1 | C3,C9 | UX-001 | M | upload/XSS | 400 + tests owner | TODO |
| PROFILE-001 | EPIC-05 | Profil/RGPD minimal | Validation username, conflit, décision suppression/conservation. | P1 | C3,C7,C8,C9 | DATA-001 | M | données | DTO/tests/politique | TODO |
| MARKET-001 | EPIC-04 | Tests cycle complet | Compléter cas état, solde insuffisant, rôle et concurrence. | P1 | C3,C7,C8,C9 | DATA-001 | M | transaction | rapport tests | TODO |
| MARKET-002 | EPIC-04 | Règle PENDING | Décider édition/suppression annonce PENDING ; documenter et tester. | P2 | C3,C9 | PRJ-001 | XS | ambiguïté | décision + test | TODO |
| SEC-001 | EPIC-06 | Rotation secrets | Rotation effective JWT/BDD/SMTP potentiellement présents dans historique ; inventaire sans purge destructive. | P0 | C1,C3,C10 | accès environnements | S | secret compromis | fiche rotation sans valeurs | TODO |
| SEC-002 | EPIC-06 | Défense web résiduelle | Planifier CSP, headers, rate limiting, upload post/vidéo et stratégie token. | P1 | C3,C9,C10 | ARCH-001 | M | temps | ADR + tests ciblés | TODO |
| TEST-001 | EPIC-10 | Plan de tests RNCP | Cas ID, préconditions, jeux, attendus, verdicts pour MVP. | P0 | C9 | exigences/règles | M | non-régression | plan + jeu d'essai | TODO |
| TEST-002 | EPIC-10 | Régression P0 | Ajouter/exécuter tests API/services/front des corrections P0. | P0 | C3,C8,C9 | AUTH-001,SOCIAL-001/002,CHAT-001 | M | tests rouges | commande verte/rapport | TODO |
| TEST-003 | EPIC-10 | Recette UI/accessibilité | Recette clavier, responsive, erreurs et parcours nominaux/refusés. | P1 | C2,C9 | UX-002 | M | défaut démo | checklist/captures | TODO |
| DEP-001 | EPIC-11 | Docker/Compose | Conteneuriser frontend, backend, base et variables sans secrets. | P0 | C10 | DATA-002,SEC-001 | L | environnement | compose lancé + capture | TODO |
| DEP-002 | EPIC-11 | Procédure déploiement | Installation, variables, migrations, backup, rollback, smoke test. | P0 | C10 | DEP-001,DATA-002 | M | absence exploitation | runbook validé | TODO |
| DEVOPS-001 | EPIC-12 | CI lint/build/tests | Workflow versionné sur branches/PR : backend tests, frontend lint/build/tests. | P0 | C11 | TEST-002,DEP-001 | M | pipeline rouge | exécution CI verte | TODO |
| DEVOPS-002 | EPIC-12 | Qualité/sécurité CI | Ajouter analyse dépendances/secrets selon faisabilité et conserver résultats. | P1 | C11 | DEVOPS-001 | S | faux positifs | rapport CI | TODO |
| COMP-001 | EPIC-13 | RGPD | Inventaire données, finalités, minimisation, rétention, droits et mentions. | P1 | C4,C5 | PROFILE-001 | M | conformité | fiche RGPD | TODO |
| COMP-002 | EPIC-13 | RGAA/éco-conception | Audit ciblé labels/clavier/contraste/assets/appels ; corrections prioritaires. | P1 | C2,C9 | UX-002 | temps | checklist + captures | TODO |
| DOC-001 | EPIC-14 | Source de vérité | Mettre à jour après chaque jalon et relier décision→preuve. | P0 | C4 | tous jalons | XS/jour | divergence | SSOT datée | TODO |
| DOC-002 | EPIC-14 | Dossier projet | Assembler besoins, UX, architecture, données, sécurité, tests, déploiement. | P0 | C1-C11 | phases UX à DevOps | L | dossier incomplet | version PDF/Markdown | TODO |
| DOC-003 | EPIC-14 | Dossier professionnel | Sélectionner situations et preuves personnalisées. | P0 | C1-C11 | DOC-002 | M | preuve faible | trame DP | TODO |
| JURY-001 | EPIC-15 | Slides/scénario | Slides : problème, trois piliers, marketplace, sécurité, tests, architecture. | P0 | C1-C11 | DOC-002 | M | oral dispersé | support + notes | TODO |
| JURY-002 | EPIC-15 | Simulation jury | Répéter démo et questions ; noter écarts restants. | P0 | C1-C11 | JURY-001,TEST-002,DEP-002 | M | démo fragile | grille / chronométrage | TODO |
| PERF-001 | EPIC-02/04 | Pagination | Décider puis traiter feed/annonces bornés. | P2 | C2,C8,C9 | décision visibilité | M | temps | test pagination | TODO |
| BONUS-001 | EPIC-03 | Conversations groupe | Sécuriser créateur/membres/validation si conservées. | P3 | C3,C8,C9 | CHAT-004 | M | hors MVP | tests groupe | TODO |

## P0 bloquants et ordre obligatoire

| ID | Pourquoi bloquant | Dépendances | Action / test attendu | Preuve RNCP |
|---|---|---|---|---|
| AUTH-001 | contrat accepté est une règle d'accès explicite | PRJ-001 | login sans contrat refusé, avec contrat accepté | C3/C9 : test BOLA/accès |
| SOCIAL-001/002 | règles feed et sécurité contournables | règles Like, DATA-001 | auto-like refusé ; legacy non contournable ; unicité | C3/C8/C9 |
| CHAT-001 | fuite potentielle de messages | aucune | non-membre 403 sur tous les endpoints | C3/C8/C9 |
| SEC-001 | secrets historiques nécessitent rotation avant démonstration/déploiement | accès environnements | clés/credentials renouvelés sans valeur versionnée | C1/C3/C10 |
| DATA-001/002 | C7/C8 et reproductibilité non démontrés | modèle validé | MCD/MLD/MPD, migrations sur base vierge | C7/C8/C10 |
| TEST-001/002 | aucune correction n'est démontrable sans non-régression | exigences + P0 code | plan, jeu et commandes vertes | C9 |
| DEP-001/002 | C10 non démontrable sans lancement/déploiement documenté | migrations, secrets | Compose + runbook/rollback/smoke test | C10 |
| DEVOPS-001 | C11 non démontrable sans automatisation | tests, build, Compose | pipeline CI vert | C11 |
| DOC-001/002/003 + JURY-001/002 | preuves non exploitables par le jury sans dossier/scénario | tous jalons | dossier, DP, slides et répétition | C1-C11 |

## GitHub Issues

Les tâches P0/P1 sont matérialisées dans GitHub. Le numéro, dépôt et URL de chaque tâche sont maintenus dans `25_GITHUB_ISSUES_MAPPING.md` afin d'éviter de réécrire la table consolidée. Une issue regroupe exceptionnellement `SOCIAL-001` et `SOCIAL-002` car l'interdiction d'auto-like et la fermeture des contournements legacy doivent être corrigées/testées ensemble.
