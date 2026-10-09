# Registre des risques — phase d'exécution

| ID | Risque | Prob. | Impact | Priorité | Mitigation | Plan B | Responsable | Statut |
|---|---|---|---|---|---|---|---|---|
| R-01 | Retard / trop de tâches avant le 17/10 | élevée | critique | P0 | gel MVP, P0 quotidiens, timebox demi-journée | reporter P2/P3 et documenter limites | porteur projet | ouvert |
| R-02 | Régression lors des P0 sécurité | moyenne | critique | P0 | tests avant/avec correction, petites branches | revert du seul commit atomique, conserver preuve échec | porteur projet | ouvert |
| R-03 | Secrets historiques non tournés | moyenne | critique | P0 | rotation JWT/BDD/SMTP, inventaire environnements | ne pas déployer/partager les environnements concernés | porteur projet | ouvert |
| R-04 | Fuite données chat/Like ou BOLA persiste | moyenne | critique | P0 | corrections GAP P0 + tests 403/STOMP | désactiver endpoint non sûr / exclure scénario | porteur projet | ouvert |
| R-05 | Modèle/migrations non prêts | moyenne | élevé | P0 | MCD/MLD/MPD avant migration, test base vierge | documenter schéma actuel et réduire périmètre data | porteur projet | ouvert |
| R-06 | Tests insuffisants ou instables | moyenne | élevé | P0 | plan, jeu d'essai, commande unique, CI | conserver recette manuelle datée, ne pas masquer échec | porteur projet | ouvert |
| R-07 | Docker/déploiement échoue tard | moyenne | élevé | P0 | Compose dès le 13/10, variables externes, smoke test | procédure locale reproductible documentée avec limite | porteur projet | ouvert |
| R-08 | CI rouge/non disponible | moyenne | élevé | P0 | workflow minimal lint/build/tests, logs sauvegardés | exécution locale horodatée, expliquer absence CI | porteur projet | ouvert |
| R-09 | Données personnelles/conformité incomplètes | moyenne | élevé | P1 | inventaire, finalités, rétention, données fictives | présenter limites et plan de mise en conformité | porteur projet | ouvert |
| R-10 | UX/RGAA non démontrée | moyenne | moyen | P1 | maquettes et recette clavier/erreurs/responsive | captures + liste de limites | porteur projet | ouvert |
| R-11 | Dépendances vulnérables / upload | moyenne | élevé | P1 | audit SCA et contrôles média ciblés | éviter fonctionnalité non durcie en démo | porteur projet | ouvert |
| R-12 | Dossier/DP/slides divergents | moyenne | élevé | P0 | source de vérité et références croisées quotidiennes | figer une version cohérente même moins complète | porteur projet | ouvert |
| R-13 | Démonstration jury fragile | moyenne | élevé | P0 | jeu d'essai stable, script, simulation, captures secours | démonstration vidéo/captures factuelles si panne locale | porteur projet | ouvert |

## Revue quotidienne

En fin de journée, mettre à jour pour chaque risque P0/P1 : évolution, action du lendemain, preuve disponible et décision de maintien/réduction du périmètre. Un risque sans responsable ni plan B est considéré non maîtrisé.
