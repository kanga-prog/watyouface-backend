# Stratégie Git et convention de commits

## Branches

Deux dépôts distincts sont conservés : frontend et backend. `main` doit rester la référence stable. Utiliser des branches courtes, une seule intention par branche :

| Branche | Usage |
|---|---|
| `rncp6/security` | P0 accès, Like, chat, secrets résiduels |
| `rncp6/feed` | règles/validation feed |
| `rncp6/chat` | messages, STOMP, groupes |
| `rncp6/marketplace` | règles/tests cycle marketplace |
| `rncp6/data` | migrations, seeds, modèle |
| `rncp6/tests` | plan et régressions sans logique supplémentaire |
| `rncp6/devops` | Docker, CI, scripts de déploiement |
| `rncp6/docs` | documents RNCP, sans fichiers applicatifs |

La branche actuelle `rncp6/security-hardening` contient les changements de sécurisation non commités déjà observés ; avant tout nouveau lot, inventorier le diff, ne pas écraser les changements locaux et découper en commits atomiques. Une branche `develop` n'est pas nécessaire au délai : branches courtes → revue → `main` si les validations sont vertes.

## Flux de travail

1. Lier la tâche à une exigence, un gap et un test attendu.
2. Créer une branche courte depuis une base connue ; un seul dépôt si le changement est unilatéral.
3. Développer puis exécuter les validations pertinentes.
4. Relire le diff : secrets, fichiers générés, régression, documentation.
5. Commit atomique ; intégrer après résultat vert. Une auto-revue documentée est acceptable pour un projet individuel, avec commande et résultat.
6. Mettre à jour `17_BACKLOG_CONSOLIDE.md`, la matrice exigence/code et la source de vérité.

## Convention Conventional Commits

`type(scope): résumé impératif court`

Types : `feat`, `fix`, `security`, `test`, `docs`, `refactor`, `build`, `ci`, `chore`.

Exemples :

```text
security(auth): enforce accepted contract before login
security(like): prevent self-like and legacy bypass
fix(chat): enforce membership on message history endpoint
test(marketplace): cover insufficient balance and invalid transitions
docs(rncp): add Phase 3 project plan
build(data): add versioned database migrations
ci(quality): run frontend and backend validation
```

## Preuves Git à conserver

- branche et commit SHA associés à chaque correction P0 ;
- `git status`, `git diff --check`, logs de tests et pipeline ;
- message de commit lié au gap/test ;
- aucune capture ne doit montrer secret, token ou donnée personnelle réelle.
