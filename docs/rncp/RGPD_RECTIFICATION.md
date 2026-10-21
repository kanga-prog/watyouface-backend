# RGPD-RECTIFY-05 — Rectification de l’e-mail et du mot de passe

Date : 20 octobre 2026
Périmètre : fonctions de compte frontend/backend. Ce document décrit l’implémentation observée ; il ne constitue pas une déclaration de conformité juridique.

## Champs rectifiables

| Champ | Modifiable | Route / écran | Validation et contrôle | État |
|---|---|---|---|---|
| `username` | Oui | `PUT /api/users/update`, Profil | endpoint limité au compte connecté ; valeur trimée et non vide ; pas de DTO/validation de longueur ou gestion explicite de conflit sur cette route | Disponible ; validation et réponse conflit à renforcer séparément |
| `avatarUrl` | Oui | `POST /api/users/avatar`, Profil | upload avatar associé au compte authentifié ; validations de média existantes | Disponible |
| `email` | Oui | `PATCH /api/users/me/email`, Profil | mot de passe actuel et step-up `EMAIL_CHANGE` vérifié sur l’ancienne adresse ; format e-mail, maximum 255 caractères ; trim puis minuscules ; unicité insensible à la casse ; compte actif uniquement | Disponible avec vérification renforcée par e-mail |
| `password` | Oui | `PATCH /api/users/me/password`, Profil | mot de passe actuel et step-up `PASSWORD_CHANGE` vérifié à l’adresse actuelle ; nouveau et confirmation identiques ; nouveau différent de l’ancien ; 12–128 caractères ; encodage BCrypt | Disponible avec vérification renforcée par e-mail |
| Autres champs personnels | Non identifiés dans le modèle utilisateur actuel | — | aucun champ additionnel de profil n’a été ajouté | N/A dans cette phase |

Les deux nouvelles routes ne prennent aucun `userId`. Le propriétaire est exclusivement obtenu avec `Authz.me()` depuis le principal Spring Security. Un rôle `ADMIN` n’autorise donc la modification que de son propre compte via ces routes. Un compte anonymisé/inactif est refusé.

## Endpoints et réponses

- `PATCH /api/users/me/email` reçoit `newEmail` et `currentPassword`. En cas de succès, la réponse ne contient que l’identifiant et le nouvel e-mail ; aucun hash ou mot de passe.
- `PATCH /api/users/me/password` reçoit `currentPassword`, `newPassword` et `confirmPassword`. En cas de succès, l’API répond `204 No Content` et expire le cookie d’authentification courant.
- Les opérations sensibles exigent d’abord `POST /api/users/me/step-up/request`, puis `POST /api/users/me/step-up/verify`; la preuve courte obtenue doit être incluse dans le PATCH correspondant. Pour un changement d’e-mail, le code est envoyé à l’ancienne adresse enregistrée.
- Les requêtes sont authentifiées par le mécanisme existant et les mutations sont protégées contre CSRF. Un appel non authentifié est refusé ; une requête mutative sans jeton CSRF valide est refusée.
- Un mot de passe courant incorrect est refusé ; une adresse invalide ou une confirmation/politique invalide est rejetée ; une adresse déjà utilisée produit un conflit `409`.

## Politique de mot de passe

La politique existante de l’inscription est réutilisée : 12 à 128 caractères. Aucun trim n’est appliqué aux mots de passe : les espaces voulus font partie du secret. Le mot de passe courant est vérifié via `PasswordEncoder.matches`; le nouveau est encodé par le `BCryptPasswordEncoder` configuré avant sauvegarde. Le DTO de requête n’est jamais renvoyé et aucun mot de passe/hash n’est écrit dans un log par ces flux.

Le changement d’e-mail normalise la valeur avec trim et minuscules (`Locale.ROOT`) pour rester cohérent et réduire les doublons de casse. L’unicité est vérifiée sans tenir compte de la casse ; la contrainte unique en base protège également contre les collisions concurrentes.

Le modèle d’authentification audité utilise e-mail/mot de passe ; aucun fournisseur OAuth/social n’a été identifié.

## Vérification renforcée

Le code OTP est à six chiffres, généré par `SecureRandom`, valable 5 minutes par défaut et à usage unique. Il est stocké hashé en BCrypt en mémoire ; la preuve temporaire correspondante est liée à l’utilisateur et à l’action. Cinq tentatives sont autorisées par challenge et trois demandes par utilisateur sur quinze minutes par défaut. Le mail est désactivé par défaut en développement et en production tant que `MAIL_ENABLED` n’est pas activé explicitement ; l’absence d’envoi configuré échoue fermé. Les limites et challenges sont par instance, non distribués. Le détail est décrit dans `SEC_STEPUP_SENSITIVE_ACTIONS.md`.

Ce step-up s’appuie sur le même canal e-mail et ne constitue pas une 2FA indépendante. Il faut configurer et tester le SMTP avant d’activer ces opérations dans un environnement réel.

## Session, JWT et limites

Après changement de mot de passe, le cookie de la session courante est effacé et le frontend redirige vers la connexion. Le JWT est stateless, sans version de mot de passe ni révocation centralisée : un jeton déjà copié hors du navigateur peut rester valide jusqu’à son expiration, au plus 24 heures selon la configuration actuelle. Le système ne prétend pas révoquer immédiatement ces copies. Une révocation centralisée nécessiterait une évolution dédiée du modèle/filtre et de son stockage ; elle n’a pas été introduite ici.

## Interface utilisateur

Le Profil contient les formulaires « Modifier mon e-mail » et « Modifier mon mot de passe » avec étapes demande, saisie/vérification du code et confirmation. Les champs code/mot de passe ont labels, erreurs/états accessibles et attributs autocomplete (`one-time-code`, `current-password`, `new-password`, `email`). Les valeurs secrètes ne sont pas préremplies ni placées dans le stockage persistant du navigateur. Après changement de mot de passe, la session du navigateur est fermée et l’utilisateur est redirigé vers la connexion.

## Tests

`SecurityApiIntegrationTests` couvre le changement d’e-mail normalisé avec preuve step-up, une adresse invalide, un conflit d’unicité, un mauvais mot de passe courant, le changement/hash BCrypt du mot de passe avec preuve, une confirmation erronée, les longueurs invalides, l’impossibilité de réutiliser le même mot de passe, l’absence d’authentification, la protection CSRF, la portée ADMIN limitée à soi et le refus d’un compte anonymisé. `StepUpChallengeServiceTests` couvre les challenges et limites. Les réponses vérifiées ne contiennent pas de mot de passe/hash.

`Profile.test.jsx` couvre le rendu des formulaires, l’absence de préremplissage, les attributs d’autocomplétion, les succès, les erreurs accessibles (e-mail déjà utilisé/mauvais mot de passe) et la fermeture de session après changement de mot de passe.

## Limites résiduelles

- le changement d’e-mail valide la possession de l’ancienne boîte, pas celle de la nouvelle adresse ; une confirmation sur la nouvelle boîte reste une amélioration distincte ;
- aucun historique métier dédié des changements n’est persisté ; les logs existants ne doivent pas recevoir les corps de requête ;
- le changement de mot de passe ne révoque pas centralement les JWT copiés avant expiration ;
- le changement de `username` historique reste à renforcer séparément pour une réponse de conflit structurée et une validation dédiée ;
- les bases juridiques, la procédure de demande de droits et les durées de conservation restent à valider dans les documents d’information.
