# RGPD-INFO-03 — Information utilisateur et documents légaux

Date : 20 octobre 2026
Périmètre : frontend React/Vite et mécanismes backend existants. Cette note ne constitue pas un avis juridique ni une déclaration de conformité RGPD.

## Parcours d’inscription

`RegisterForm` collecte : pseudo, adresse e-mail et mot de passe. Les contraintes visibles correspondent au DTO backend (`username` 3–50, e-mail, mot de passe 12–128). L’inscription mène ensuite au contrat actif récupéré depuis `/api/contracts/active`; la création de compte transmet `acceptTerms: true` uniquement après l’action explicite « Accepter » sur ce contrat.

La page `/register` affiche désormais : « En continuant, vous reconnaissez avoir pris connaissance des Conditions d’utilisation et de la Politique de confidentialité », avec deux liens consultables avant l’inscription. Il n’existe pas de case « accepter le RGPD » : la confidentialité est une information à fournir, pas un consentement global fictif.

## Documents accessibles

| Route | Contenu | État |
|---|---|---|
| `/legal/terms` | conditions d’utilisation : contrat actif réel, titre, version et contenu | présent ; indisponibilité backend signalée sans inventer de texte |
| `/legal/privacy` | politique de confidentialité structurée | présent ; limites et éléments à formaliser explicitement indiqués |
| `/legal/cookies` | non créée | aucun analytics, newsletter, prospection ou cookie non strictement nécessaire identifié dans le code audité |

Les liens « Conditions d’utilisation » et « Politique de confidentialité » sont présents dans le footer des pages publiques, des pages légales et de l’application authentifiée. Les liens sont des liens React natifs, atteignables au clavier et bénéficient du focus visible global.

## Politique de confidentialité : contenu publié

La page Privacy couvre : responsable à formaliser, catégories de données, finalités, bases envisagées à valider, catégories de destinataires, hébergement Hostinger/Railway, durées non encore validées, mesures de sécurité, droits, modalité d’exercice incomplète, réclamation CNIL, version et date. La formulation indique que les données sont conservées le temps nécessaire aux finalités et obligations applicables, et que les durées détaillées par catégorie sont en cours de formalisation ; elle n’annonce aucun délai non approuvé. La matrice technique est dans `RGPD_RETENTION_POLICY.md`.

Elle ne présente pas de durée de conservation inventée, de contact fictif, ni de conformité déclarée. Avant ouverture publique, l’identité du responsable, un point de contact effectif, les durées, bases et sous-traitants doivent être validés et complétés.

## Matrice des droits observables

| Droit | Écran / endpoint | État | Preuve | Écart |
|---|---|---|---|---|
| Information | `/legal/privacy`, footer, inscription | PARTIEL | `Privacy.jsx`, `LegalFooter.jsx`, `RegisterForm.jsx` | responsable/contact/durées à formaliser |
| Accès | profil, wallet, conversations/messages, transactions et téléchargement JSON | PARTIEL | `/api/users/me`, endpoints métier, `GET /api/users/me/export` | export limité aux données du compte et à certains contenus, sans fichiers binaires ni données privées reçues de tiers |
| Rectification | pseudo, avatar, e-mail et mot de passe | PARTIEL | `Profile.jsx`, routes `/api/users/me/step-up/*`, routes de rectification, `RGPD_RECTIFICATION.md`, `SEC_STEPUP_SENSITIVE_ACTIONS.md` | code de vérification envoyé par e-mail à l'ancienne boîte (changement e-mail) ou boîte actuelle (mot de passe) ; nouvelle adresse non confirmée ; challenges mono-instance ; les JWT copiés restent valides jusqu'à expiration |
| Effacement | profil → confirmation `SUPPRIMER` → `DELETE /api/users/{id}` CSRF/cookie → redirection | PARTIEL | `Profile.jsx`, `api.deleteOwnAccount`, RGPD-ERASE-01 | validation PostgreSQL locale et information des durées résiduelles à finaliser |
| Portabilité / export | Profil → « Télécharger mes données » → JSON via `GET /api/users/me/export` | PARTIEL | `UserDataExportService`, `UserDataExportDTO`, `Profile.jsx`, `RGPD_EXPORT_PORTABILITE.md` | couverture des données et droits de tiers limitée par conception ; pas de fichiers binaires/ZIP | maintenir le format versionné et compléter uniquement après revue juridique/fonctionnelle |
| Limitation / opposition | aucun parcours dédié | GAP | aucun endpoint/écran identifié | décider traitement et procédure support |

## Parcours d’effacement frontend

Le profil propose « Supprimer mon compte ». Une première action affiche les conséquences, puis l’utilisateur doit saisir exactement `SUPPRIMER`. Le bouton de confirmation reste désactivé sinon. Après succès de la requête authentifiée et protégée CSRF, l’interface supprime son état UX local, émet l’événement de changement d’authentification et redirige vers `/login`. Aucun test ni action de cette phase n’a visé un compte de production.

## Consentements facultatifs

Aucun mécanisme de newsletter, prospection, analytics ou cookie non essentiel n’a été identifié dans le frontend audité. Aucun consentement facultatif ni case pré-cochée n’a donc été créé. Si un tel mécanisme est ajouté, son consentement devra être séparé des CGU et facultatif par défaut.

## Tests et captures à prévoir

Tests frontend réalisés : liens Terms/Privacy sur Register, focus clavier d’un lien, absence de checkbox, page Privacy lisible, page Terms avec contrat actif simulé, confirmation forte et appel simulé d’effacement. Résultat lors de RGPD-INFO-03 : `npm test` — **14 fichiers, 51 tests, 0 échec**. Après ajout de l’export RGPD-EXPORT-04 : **14 fichiers, 52 tests, 0 échec**.

Captures à prévoir pour le dossier : inscription avec liens légaux, page Privacy, page CGU/version, footer, étape de confirmation d’effacement (sans cliquer la confirmation sur un compte réel).

## Export et portabilité

Le Profil propose désormais le téléchargement authentifié du JSON versionné. L’endpoint dérive l’identité du contexte de sécurité et l’export ne comprend que les données rattachées au compte, avec minimisation des identifiants et contenus privés des tiers. Les modalités et exclusions sont décrites dans `RGPD_EXPORT_PORTABILITE.md`.

## Limites résiduelles

- aucune adresse/contact RGPD publiée ;
- durées de conservation et bases à valider ;
- limitation et opposition sans parcours dédié ; export JSON disponible, sans archive de médias ;
- changement d’e-mail et de mot de passe disponible avec réauthentification et code temporaire par e-mail ; ce step-up n’est pas une 2FA indépendante. SMTP doit être configuré pour activer réellement l’envoi ; confirmation du nouvel e-mail, traçabilité métier minimisée et révocation centralisée des JWT copiés restent à évaluer ;
- contenu CGU dépend de la disponibilité de l’API contrat ;
- politique cookies à réévaluer si un traceur non essentiel est ajouté.
