# Matrice de validation Marketplace

Date : 2026-10-10. Les statuts visuels exigent une capture de l’application réelle; ils ne sont pas déduits de tests de service.

| ID | Scénario | Acteur | État initial | Action | État attendu | Test automatique | Test manuel | Résultat | Preuve |
|---|---|---|---|---|---|---|---|---|---|
| MKT-01 | Création valide | Vendeur | Aucun | POST annonce | AVAILABLE | API/H2 ajouté | À faire | PASS auto si suite verte; visuel à faire | test API |
| MKT-02 | Champs invalides | Vendeur | Aucun | POST titre blanc/prix nul | 400 | API/H2 ajouté | À faire | PASS auto si suite verte | test API |
| MKT-03 | Anonyme crée | Visiteur | Aucun | POST annonce | 401 | API/H2 ajouté | À faire | PASS auto si suite verte | test API |
| MKT-04 | Détail persistant | Vendeur | AVAILABLE | GET ID créé | Même annonce/statut | API/H2 ajouté | Refresh à faire | PARTIEL | test API |
| MKT-05 | Liste | Membre | Annonces présentes | GET collection | Liste API | Endpoint existant, scénario dédié manquant | À faire | PARTIEL | code endpoint |
| MKT-06 | Ressource absente | Membre | ID inconnu | GET ID | 404 | API/H2 existant | À faire | PASS | test SecurityApiIntegrationTests |
| MKT-07 | Demande achat | Acheteur | AVAILABLE | POST request | PENDING | Test intégration API/H2 du cycle complet | À faire | PASS API/H2 | SecurityApiIntegrationTests |
| MKT-08 | Refus vendeur | Vendeur | PENDING | POST refuse | REFUSED | API/H2 vendeur autorisé, tiers 403, répétition 409 | À faire | PASS API/H2 | SecurityApiIntegrationTests |
| MKT-09 | Acceptation vendeur | Vendeur | PENDING | POST accept | ACCEPTED | API/H2 ajouté + service | À faire | PASS API/H2 | tests API/service |
| MKT-10 | Tiers accepte | Tiers | PENDING | POST accept | 403, état inchangé | API/H2 ajouté + service | À faire | PASS API/H2 | tests API/service |
| MKT-11 | Paiement valide | Acheteur | ACCEPTED | POST pay | PAID; transfert démo + transaction | Cycle API/H2 vérifie soldes/transaction; PostgreSQL non rejoué | À faire | PARTIEL (H2 PASS) | test intégration API |
| MKT-12 | Solde insuffisant | Acheteur | ACCEPTED | POST pay | Erreur, aucune mutation | API/H2 vérifie état/soldes/absence transaction; PG non rejoué | À faire | PARTIEL (H2 PASS) | test intégration API |
| MKT-13 | Double paiement | Acheteur | PAID / transaction existante | POST pay | Refus, une transaction | Deuxième POST refusé et solde inchangé en H2; PG non rejoué | À faire | PARTIEL (H2 PASS) | test intégration API |
| MKT-14 | Paiement concurrent | Deux requêtes | ACCEPTED | Deux POST simultanés | Un seul paiement | Test PG dédié non rejoué | À faire | PARTIEL | test PG présent |
| MKT-15 | Expédition | Vendeur | PAID | POST ship | SHIPPED | Service cycle complet | À faire | PARTIEL | MarketplaceTransitionTests |
| MKT-16 | Réception | Acheteur | SHIPPED | POST receive | RECEIVED | Service cycle complet | À faire | PARTIEL | MarketplaceTransitionTests |
| MKT-17 | Transitions impossibles | Acteurs divers | États variés | Actions hors cycle | 409, aucun état incohérent | Cas partiels service/API; exhaustivité manquante | À faire | PARTIEL | tests existants |
| MKT-18 | Chat/contrat/deux comptes/refresh | Vendeur + acheteur | Cycle | Cycle complet deux utilisateurs, chat générique, contrat, reload | Comportements réellement supportés | Cycle API/H2 deux identités; pas d’E2E UI | À faire | PARTIEL | test API; recette UI à faire |

## Matrice des permissions observées

| Action | Autorité observée | Limite |
|---|---|---|
| Créer annonce | Utilisateur authentifié; vendeur pris du contexte Authz | HTTP réel testé par intégration H2 |
| Demander achat | Acheteur authentifié, vendeur ne peut pas acheter sa propre annonce | Test du chemin service; test API détaillé à compléter |
| Accepter/refuser | Vendeur ou administrateur | acceptation non-vendeur testée; refus dédié manquant |
| Payer | Acheteur attaché à l’annonce ou administrateur; état ACCEPTED; transaction unique | Tests PostgreSQL non exécutés pendant la passe courante |
| Expédier | Vendeur ou administrateur, état PAID | Service cycle couvert seulement |
| Confirmer réception | Acheteur attaché ou administrateur, état SHIPPED | Service cycle couvert seulement |
| Mettre à jour/supprimer | Vendeur ou administrateur; contraintes d’état | parcours API/UI spécifique à compléter |
| Chat | Participants de conversation selon sécurité Chat générique | aucune association annonce-conversation |

## Critère de clôture

La matrice n’autorise pas encore la clôture Marketplace : preuves PostgreSQL, recette visuelle, refus, vérification exhaustive des transitions et scénario de bout en bout à deux utilisateurs sont encore requis. Aucun statut NON TESTE/PARTIEL ne doit être converti en PASS sans preuve.
