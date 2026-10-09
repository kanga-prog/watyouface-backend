# Architecture médias

## Actuel observé

| Sujet | Observation / preuve | État |
|---|---|---|
| Stockage | `${user.dir}/media`, `MediaStorageService` | observé |
| URL | `publicUrl` retourne `/media/<relative>` ; `WebConfig` sert ce dossier | observé, public |
| Chemin | `resolvePath` normalise et refuse une sortie de `media/` | contrôle anti-path traversal observé |
| Vidéo | `VideoService` appelle `ffmpeg`, 720p max, H.264/AAC | dépendance système / validation pré-upload à documenter |
| Images/avatar/listing | `FormData` frontend et endpoints dédiés | limites MIME/taille non uniformément prouvées |
| Accès | `SecurityConfig` autorise `/media/**` publiquement | à arbitrer selon confidentialité |

## Cible raisonnable

1. allow-list MIME vérifiée côté serveur et non seulement extension ;
2. limites de taille/dimensions, nom généré côté serveur, aucune reprise de chemin client ;
3. analyse/transcodage contrôlé, timeout/quotas pour ffmpeg ;
4. métadonnées associées à l’auteur et politique de suppression ;
5. URL publique seulement pour médias réellement publics, sinon endpoint autorisé ;
6. sauvegarde média et procédure de restauration dans le déploiement.

Ces contrôles sont à développer/tester : ils ne sont pas tous démontrés actuellement.
