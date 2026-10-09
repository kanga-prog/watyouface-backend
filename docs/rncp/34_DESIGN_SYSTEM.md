# Design system minimal — WatYouFace

## Tokens fonctionnels

| Token | Valeur cible | Usage |
|---|---:|---|
| `--color-primary` | `#2156A5` | action principale, navigation active, liens |
| `--color-secondary` | `#5B47B7` | accent conversationnel ponctuel |
| `--color-background` | `#F5F7FB` | fond de page |
| `--color-surface` | `#FFFFFF` | cartes, champs, fenêtres |
| `--color-text-primary` | `#172033` | texte courant |
| `--color-text-secondary` | `#526070` | métadonnées, aides |
| `--color-success` | `#157A4A` | confirmation, état reçu |
| `--color-warning` | `#8A5A00` | attente, attention |
| `--color-danger` | `#B42318` | action destructive, erreur |
| `--color-info` | `#155E9A` | information neutre |
| `--color-border` | `#D4DAE5` | contours et séparateurs |
| `--color-focus` | `#F2A900` | contour de focus visible |

Les contrastes devront être revalidés lors de l’implémentation avec les combinaisons réelles.

## Typographie

| Style | Taille / interligne | Emploi |
|---|---|---|
| H1 | 28 / 36 px, 700 | titre de page |
| H2 | 22 / 30 px, 700 | section majeure |
| H3 | 18 / 26 px, 600 | carte ou panneau |
| Body | 16 / 24 px, 400 | contenu courant |
| Small | 14 / 20 px, 400 | aide, date, métadonnée |
| Label | 14 / 20 px, 600 | libellé permanent de champ |
| Button | 16 / 20 px, 600 | action cliquable |

Police cible : police système sans sérif (`system-ui`, `Segoe UI`, `Roboto`, `Arial`) pour une lecture fiable et sans téléchargement imposé.

## Espacements, rayons et élévation

| Famille | Tokens |
|---|---|
| Espacement | 4, 8, 12, 16, 24, 32, 48 px |
| Rayon | small 6 px ; medium 10 px ; large 16 px |
| Élévation | none ; card `0 1px 3px rgba(23,32,51,.12)` ; modal `0 12px 32px rgba(23,32,51,.22)` |

## Règles de composants

- **Bouton primaire** : fond `primary`, texte blanc, un seul par bloc décisionnel.
- **Bouton secondaire** : fond surface, bordure visible ; jamais sans libellé.
- **Danger** : `danger` réservé à suppression/refus irréversible ; confirmation préalable selon UX-010.
- **Champ** : label au-dessus, aide et erreur sous le champ ; `aria-describedby` à prévoir en implémentation.
- **Carte** : surface blanche, bordure légère, rayon medium, padding 16 ou 24.
- **Badge** : texte du statut, pas uniquement une pastille ; contraste vérifié.

## Responsive

| Palier | Règle cible |
|---|---|
| Mobile < 768 px | gouttière 16 px, navigation basse, une colonne |
| Desktop ≥ 768 px | en-tête supérieur, panneaux/chat deux colonnes si utile |

Ces tokens constituent une spécification de conception ; aucun CSS applicatif n’est modifié dans cette phase.

