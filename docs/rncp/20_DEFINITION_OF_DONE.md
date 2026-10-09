# Definition of Done (DoD)

## DoD générale

Une tâche est `DONE` uniquement si :

1. son exigence et son critère d'acceptation sont satisfaits ;
2. le code nécessaire est revu localement, sans secret, dette critique ni régression connue ;
3. le test approprié est ajouté/actualisé et exécuté avec résultat conservé ;
4. les erreurs 400/401/403/404/409 attendues sont couvertes lorsque la tâche expose une API ;
5. la documentation, la matrice exigence/code, le backlog et la source de vérité sont mis à jour ;
6. la preuve RNCP (fichier, commande, capture ou décision) est classée ;
7. la branche est intégrable selon la stratégie Git.

## DoD par type

| Type | Conditions additionnelles |
|---|---|
| Correction sécurité | scénario d'abus défensif refusé côté serveur ; test de non-régression ; impact des anciens endpoints vérifié ; pas de secret dans diff. |
| Règle métier | cas nominal, état invalide et acteur non autorisé testés ; transition/données documentées. |
| Interface | wireframe/maquette ou exigence suivie ; responsive et clavier contrôlés ; erreurs UI vérifiées. |
| Données/migration | modèle mis à jour ; migration versionnée, idempotence/rollback documentés ; base vierge et jeu d'essai testés. |
| Docker/déploiement | variables externes ; démarrage/smoke test ; persistance, backup et rollback documentés. |
| CI/CD | workflow versionné ; déclencheur, logs, échecs et artefacts vérifiés ; aucun secret affiché. |
| Documentation RNCP | source citée, date, compétence, preuve et limite connues ; contenu réutilisable dossier/slide/oral. |

## Statuts de suivi

`TODO` : non préparé ; `READY` : critères et dépendances levés ; `IN PROGRESS` : travail en cours ; `BLOCKED` : dépendance explicite ; `REVIEW` : preuve/test à vérifier ; `DONE` : DoD satisfaite.
