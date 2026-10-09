# Preuves sécurité Phase 11

Preuves non sensibles générées le 08/10/2026; aucun secret, token ou mot de passe inclus.

- backend-tests.txt : résultats Maven H2 et PostgreSQL.
- frontend-tests.txt : lint, tests, build et audit runtime npm.
- security-controls.txt : synthèse des contrôles inspectés.
- rate-limit-tests.txt : cas de limitation du login, 429, expiration et absence de fuite.
- maven-dependency-check.txt : essai Dependency-Check et cause technique du blocage NVD.
- secrets-history-checklist.md : état de l'externalisation et rotation historique, sans valeurs sensibles.

Les logs complets ne sont pas archivés car ils contiennent du bruit SQL; les synthèses conservent les commandes, nombres et résultats.
