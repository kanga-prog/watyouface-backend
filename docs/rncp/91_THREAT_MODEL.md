# Phase 11 — Threat model STRIDE

Périmètre observé : navigateur React, REST Spring Security/JWT, WebSocket STOMP, services métier, médias locaux et PostgreSQL. Analyse qualitative; ce document n'est ni une certification ni un test d'intrusion.

| STRIDE | Composant / actif | Scénario | Contrôle observé | Preuve / limite |
|---|---|---|---|---|
| Spoofing | Login/JWT/CONNECT | Token absent, altéré, expiré ou utilisateur absent | HS256, expiration, secret externalisé >=32 octets, utilisateur rechargé | Tests JWT; révocation immédiate/refresh absents |
| Spoofing | Autorité ADMIN | Ancien JWT conserve le rôle après rétrogradation | Rôle courant issu de la base, pas du claim | Test de régression |
| Tampering | Post/listing/wallet/message | Client falsifie auteur, prix, état ou userId | Acteur authentifié; contrôles métier et validation | Tests ciblés; couverture API par endpoint incomplète |
| Tampering | Upload | MIME falsifié, traversal, fichier excessif | Whitelist, plafonds, nom serveur, chemin sous media, décodage/transcodage | Test MIME; pas de sandbox antivirus |
| Repudiation | Paiement/admin | Contestation d'une opération | Transaction et horodatage persistés | Journal d'audit métier/rétention non défini |
| Information Disclosure | Chat/STOMP | Non-membre lit une conversation | Membership REST et SUBSCRIBE; destination SEND bornée | Tests interceptor; pas E2E navigateur |
| Information Disclosure | Erreurs/logs | Stack trace, SQL, secret renvoyé/logué | 500 générique; logs des chemins inspectés assainis | Revue non exhaustive en production |
| Information Disclosure | JWT navigateur | XSS vole le bearer localStorage | React échappe le texte; pas de dangerouslySetInnerHTML repéré | Risque résiduel localStorage; CSP à définir |
| Denial of Service | Login/API | Brute force / rafale | Aucun rate limit identifié | Risque ouvert; limiter au proxy/edge et distribué si multi-instance |
| Denial of Service | Vidéo | Fichier lourd ou conversion bloquée | Taille max service, type autorisé, timeout ffmpeg 120 s | Limites multipart profil à vérifier par déploiement |
| Denial of Service | Dépendances frontend | Composant runtime vulnérable | npm audit production exécuté | 2 high et 1 critical ouverts |
| Elevation of Privilege | Wallet/rôles | USER crédite wallet ou ancien ADMIN reste autorisé | Endpoint admin; rôle rechargé depuis DB | Tests ciblés; contrôler toute nouvelle route privilégiée |
| Elevation of Privilege | CORS | Origine tierce avec credentials | Allowlist exacte, wildcard interdit | Test preflight; config production à fournir |

## Frontières de confiance

```text
Navigateur (entrée non fiable, bearer localStorage)
    -- HTTPS attendu au déploiement --> REST / WebSocket
Spring Security (JWT + CORS + autorisation)
    --> services métier / validation
Services média --> fichiers sous media/ (servis publics par configuration actuelle)
Services JPA --> PostgreSQL (identifiants runtime externalisés)
```

Les protections frontend améliorent l'expérience mais ne remplacent jamais l'autorisation backend.

