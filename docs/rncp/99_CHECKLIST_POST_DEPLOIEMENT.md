# 99 — Checklist post-déploiement Hostinger

Statut : **NON EXÉCUTÉE — aucun déploiement réel**. Hébergeur cible : Hostinger; offre (VPS ou Web/Cloud) et domaine à confirmer dans le compte. Compléter date, commit, environnement, URL non sensible, résultat et preuve seulement après exécution.

## Pré-vol Hostinger (bloquant avant provisionnement)

- [ ] Confirmer dans hPanel l’offre active et les ressources. Pour déployer l’ensemble React + Spring Boot/Java + PostgreSQL + WebSocket sous Hostinger, cible recommandée : VPS; une offre Web/Cloud Node.js seule n’établit pas la capacité à exécuter Spring Boot.
- [ ] Confirmer domaine/DNS, accès SSH, région, stockage persistant, sauvegarde incluse et propriétaire de l’exploitation.
- [ ] Mettre en place une chaîne reproductible de baseline/migrations PostgreSQL; à ce jour le profil `prod` valide seulement un schéma existant (`ddl-auto=validate`).
- [ ] Préparer les valeurs d’environnement et le build frontend avec les vrais domaines Hostinger; ne pas utiliser les valeurs de test `example.invalid`.
- [ ] Confirmer Nginx, systemd, TLS et répertoire `media/` persistant. Rien de cela n’est installé par cette recette.

| ID | Vérification | Attendu | Résultat initial |
|---|---|---|---|
| DEP-01 | Frontend HTTPS | page accessible, certificat valide | À FAIRE |
| DEP-02 | Backend health/availability | réponse attendue; endpoint health à définir ou tester route sûre | À FAIRE |
| DEP-03 | Register + CSRF | inscription valide; cookie CSRF et header distincts | À FAIRE |
| DEP-04 | Login | session créée; aucune fuite JWT JSON | À FAIRE |
| DEP-05 | Cookie auth | HttpOnly, Secure, Path et SameSite attendus | À FAIRE |
| DEP-06 | API protégée | refus sans session, succès avec cookie | À FAIRE |
| DEP-07 | Feed | lecture et action autorisée | À FAIRE |
| DEP-08 | Chat REST | participant autorisé/non participant refusé | À FAIRE |
| DEP-09 | WebSocket/STOMP | connexion HTTPS puis WSS, origine permise et membership vérifié | À FAIRE |
| DEP-10 | Marketplace | statuts/actions conformes, wallet de démo explicitement simulé | À FAIRE |
| DEP-11 | Profil | données privées non publiques, wallet de démo | À FAIRE |
| DEP-12 | Upload | limites/type, chemin persistant, URL attendu et sauvegarde testée | À FAIRE |
| DEP-13 | Logout | cookie auth expiré, endpoint protégé refusé après logout | À FAIRE |
| DEP-14 | 404 | page/réponse générique, aucune stacktrace | À FAIRE |
| DEP-15 | 500 | réponse générique, détails uniquement dans logs protégés | À FAIRE |
| DEP-16 | HTTPS/HSTS | redirection HTTPS; HSTS seulement après TLS vérifié | À FAIRE |
| DEP-17 | CORS | origines exactes, credentials, aucun wildcard | À FAIRE |
| DEP-18 | DB | connexion privée, sauvegarde et restauration testées | À FAIRE |
| DEP-19 | Logs | pas de JWT, cookie, password ou secret; rotation/rétention | À FAIRE |
| DEP-20 | Rollback | procédure version précédente testée | À FAIRE |
| DEP-21 | Offre Hostinger | offre/ressources confirmées, VPS compatible Java/PostgreSQL si tout hébergé chez Hostinger | À FAIRE |
| DEP-22 | Build Vite Hostinger | VITE_API_BASE/VITE_WS_URL vrais, aucun localhost dans dist | À FAIRE |
| DEP-23 | Nginx + systemd | service persistant, proxy REST/media/SockJS/WSS, headers vérifiés | À FAIRE |
| DEP-24 | Persistance médias | répertoire hors release, redémarrage sans perte et sauvegarde vérifiée | À FAIRE |
| DEP-25 | Schéma PostgreSQL | base vide provisionnée depuis baseline/migrations versionnées | BLOQUÉ — baseline complète absente |

Conserver preuves anonymisées dans `docs/rncp/evidence/deployment/`; ne pas inclure cookies, identifiants ni données de personnes réelles.
