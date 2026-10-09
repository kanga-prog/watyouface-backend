# Planning RNCP — 07 au 17/10/2026

Le planning est volontairement serré et privilégie le MVP et les preuves. Une tâche bloquée déclenche l'usage du plan B du registre de risques ; aucun enrichissement P2/P3 ne doit retarder un P0.

| Date | Objectif | Tâches prioritaires | Prio | Livrable | Critère de fin | RNCP |
|---|---|---|---|---|---|---|
| 07/10 | Gouverner le reste du projet | finaliser phase 3, geler P0, préparer board et branches | P0 | `17` à `24`, SSOT | backlog, risques, planning approuvés | C4,C5 |
| 08/10 | Concevoir l'expérience MVP | wireframes feed/chat/marketplace + erreurs ; décider visibilité, chat-annonce, PENDING | P1/P0 décision | wireframes, décisions | parcours MUST lisibles et décisions bloquantes tracées | C2,C5 |
| 09/10 | Concevoir technique et données | architecture, ADR, MCD/MLD/MPD ; stratégie migrations/jeu d'essai | P0/P1 | diagrammes, scripts planifiés | modèle validé et mapping entités établi | C6,C7,C8 |
| 10/10 | Corriger sécurité P0 — lot A | contrat/login, auto-like, legacy Like ; tests associés | P0 | code + tests + rapport | commandes vertes et cas 401/403/409 passants | C3,C8,C9 |
| 11/10 | Corriger sécurité P0 — lot B | lecture messages sans appartenance ; validation post/commentaire/message ; tests STOMP/recette | P0/P1 | code + tests | non-membre refusé partout ; validation démontrée | C3,C8,C9 |
| 12/10 | Rendre les données reproductibles | migrations, jeu d'essai fictif, test solde insuffisant, rapport tests | P0/P1 | migrations/seed/plan de tests | base vierge initialisable, cycle marketplace testable | C7,C8,C9 |
| 13/10 | Rendre l'application portable | Docker/Compose, variables, build images, smoke test local | P0 | compose + procédure brouillon | démarrage reproductible sans secret committé | C1,C10 |
| 14/10 | Automatiser et documenter l'exploitation | CI lint/build/tests, procédure déploiement/rollback/backup ; smoke test | P0 | pipeline + runbook | pipeline verte et déploiement documenté/testé | C10,C11 |
| 15/10 | Qualité, conformité et preuves | recette UX/RGAA, RGPD, éco-conception, captures, mise à jour SSOT | P1/P0 docs | checklists + preuves | parcours MVP validés et preuves classées | C2,C4,C9 |
| 16/10 | Assembler les livrables jury | dossier projet/DP, slides, démonstration chronométrée, simulation | P0 | dossiers + slides + grille | aucune compétence sans preuve planifiée | C1-C11 |
| 17/10 | Stabiliser et remettre | correction uniquement de régressions critiques, revalidation complète, simulation finale, archivage preuves | P0 | rapport final / checklist | build, tests, CI, démo et dossiers prêts | C1-C11 |

## Travaux parallélisables

- Documentation RNCP et source de vérité : en continu, après chaque jalon.
- Wireframes (08/10) et préparation modèle de données (09/10) : parallélisables après décision de périmètre.
- Tests peuvent être écrits avec les corrections P0, jamais reportés après la correction.
- Dossier/DP/slides peuvent être alimentés dès les premières preuves ; leur finalisation attend les jalons techniques.

## Règle de replanification

Si un P0 dépasse une demi-journée sans résultat vérifiable, arrêter l'extension, documenter le blocage, utiliser le plan B et protéger le temps Docker/CI/dossier. P2 et P3 basculent hors MVP avant de déplacer une preuve C7/C9/C10/C11.
