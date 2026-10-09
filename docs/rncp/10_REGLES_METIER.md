# Règles métier et critères d'acceptation

## Authentification et profil

| ID | Règle cible | Critères d'acceptation testables |
|---|---|---|
| BR-AUTH-001 | Un compte exige un username non vide de 3 à 50 caractères, un email valide et un mot de passe de 12 à 128 caractères. | **AC-AUTH-001** GIVEN données valides WHEN inscription THEN compte créé ; GIVEN email/username existant ou données invalides WHEN inscription THEN 400/409 et aucun compte utilisable supplémentaire. |
| BR-AUTH-002 | L'acceptation du contrat actif est enregistrée pour l'identité concernée avant l'accès membre. | **AC-AUTH-002** GIVEN contrat non accepté WHEN login/espace protégé THEN accès fonctionnel refusé ; GIVEN contrat accepté WHEN connexion THEN accès accordé. |
| BR-AUTH-003 | Une connexion invalide ne révèle pas si l'email existe. | **AC-AUTH-003** GIVEN mauvais email ou mot de passe WHEN login THEN 401 et message générique ; GIVEN identifiants valides THEN jeton émis. |
| BR-PROFILE-001 | Seul le propriétaire ou ADMIN selon politique explicite gère un profil. | **AC-PROFILE-001** GIVEN USER A WHEN modification profil A THEN succès ; GIVEN USER B WHEN modification profil A THEN 403. |
| BR-PROFILE-002 | Un avatar est contrôlé avant stockage. | **AC-PROFILE-002** GIVEN fichier conforme WHEN upload THEN avatar mis à jour ; GIVEN fichier non conforme WHEN upload THEN refus contrôlé. |

## Feed social

| ID | Règle cible | Critères d'acceptation testables | État code initial |
|---|---|---|---|
| BR-POST-001 | Un USER authentifié peut créer une publication. | **AC-POST-001** GIVEN USER authentifié WHEN création avec entrée valide THEN post lié à ce USER ; GIVEN visiteur THEN 401. | conforme pour l'identité ; validation contenu/média partielle. |
| BR-POST-002 | Le propriétaire peut modifier sa publication. | **AC-POST-002** GIVEN propriétaire WHEN modification THEN 200 ; GIVEN non-propriétaire THEN 403. | conforme pour ownership ; contenu non validé. |
| BR-POST-003 | Le propriétaire peut supprimer sa publication. | **AC-POST-003** GIVEN propriétaire WHEN suppression THEN 204 ; GIVEN non-propriétaire THEN 403. | conforme. |
| BR-POST-004 | Un USER ne modifie/supprime pas la publication d'un autre USER ; ADMIN agit seulement selon règle explicitée. | **AC-POST-004** GIVEN ADMIN WHEN action post tiers THEN résultat autorisé et traçable ; GIVEN USER tiers THEN 403. | conforme pour autorisation ; traçabilité non prouvée. |
| BR-COMMENT-001 | Un USER authentifié peut commenter un post visible. | **AC-COMMENT-001** GIVEN USER et post visible WHEN commentaire non vide THEN commentaire lié à USER/post ; GIVEN vide/inexistant THEN 400/404. | partiel : visibilité et longueur non définies. |
| BR-COMMENT-002 | Seul l'auteur ou ADMIN modifie/supprime un commentaire. | **AC-COMMENT-002** GIVEN auteur/ADMIN THEN succès ; GIVEN tiers THEN 403. | conforme. |
| BR-LIKE-001 | Un USER peut liker/retirer son like sur un post d'un autre USER, avec un seul like par USER/post. | **AC-LIKE-001** GIVEN post d'autrui WHEN toggle THEN un like créé/supprimé ; GIVEN répétition WHEN état déjà liké THEN retrait sans doublon. | partiel : toggle existe, unicité DB non prouvée et anciens endpoints contournent la règle. |
| BR-LIKE-002 | Un USER ne peut pas liker sa propre publication. | **AC-LIKE-002** GIVEN auteur du post WHEN like THEN refus 403/409 et aucun like créé. | non conforme : aucun contrôle observé. |

## Chat

| ID | Règle cible | Critères d'acceptation testables | État code initial |
|---|---|---|---|
| BR-CHAT-001 | Seul un participant consulte les messages d'une conversation. | **AC-CHAT-001** GIVEN participant WHEN lecture THEN 200 ; GIVEN non-membre WHEN lecture par chaque endpoint THEN 403. | partiel : endpoints usuels contrôlés, endpoint `/all` non contrôlé. |
| BR-CHAT-002 | Seul un participant peut envoyer un message. | **AC-CHAT-002** GIVEN participant WHEN envoi REST/STOMP THEN message persistant/diffusé ; GIVEN tiers THEN 403/refus. | conforme côté REST/service ; validation contenu partielle. |
| BR-CHAT-003 | Un non-membre ne s'abonne pas au canal temps réel d'une conversation. | **AC-CHAT-003** GIVEN non-membre connecté STOMP WHEN SUBSCRIBE THEN abonnement refusé, aucun événement reçu. | partiel : contrôle serveur codé, E2E STOMP restant. |
| BR-CHAT-004 | Une discussion de négociation peut accompagner une annonce sans automatiser l'achat. | **AC-CHAT-004** GIVEN acheteur/vendeur WHEN discussion THEN messages autorisés ; THEN aucune transition marketplace automatique. | non prouvé : aucune relation Conversation-Listing observée. |

## Marketplace et wallet simulé

| ID | Règle cible | Critères d'acceptation testables | État code initial |
|---|---|---|---|
| BR-MARKET-001 | Une annonce créée appartient au USER authentifié et démarre AVAILABLE. | **AC-MARKET-001** GIVEN USER WHEN annonce valide THEN seller=USER, status=AVAILABLE ; entrée invalide THEN 400. | conforme. |
| BR-MARKET-002 | Seul un autre USER peut demander une annonce AVAILABLE. | **AC-MARKET-002** GIVEN acheteur tiers WHEN request THEN PENDING/buyer défini ; GIVEN vendeur ou état non AVAILABLE THEN refus. | conforme. |
| BR-MARKET-003 | Seul le vendeur ou ADMIN accepte une demande PENDING. | **AC-MARKET-003** GIVEN vendeur WHEN accept THEN ACCEPTED ; GIVEN tiers/état différent THEN 403/409. | conforme. |
| BR-MARKET-004 | Seul le vendeur ou ADMIN refuse une demande PENDING. | **AC-MARKET-004** GIVEN vendeur WHEN refuse THEN REFUSED ; GIVEN tiers/état différent THEN 403/409. | conforme. |
| BR-MARKET-005 | Seul l'acheteur ou ADMIN paie une annonce ACCEPTED une seule fois ; débit, crédit, transaction et statut forment une opération atomique. | **AC-MARKET-005** GIVEN acheteur/solde WHEN pay THEN PAID + transaction ; GIVEN second pay/tier/solde insuffisant THEN conflit/refus et aucune double transaction. | conforme pour verrou et double paiement ; test solde insuffisant à compléter. |
| BR-MARKET-006 | Seul le vendeur ou ADMIN expédie une annonce PAID. | **AC-MARKET-006** GIVEN vendeur WHEN ship THEN SHIPPED ; sinon 403/409. | conforme. |
| BR-MARKET-007 | Seul l'acheteur ou ADMIN confirme une annonce SHIPPED. | **AC-MARKET-007** GIVEN acheteur WHEN receive THEN RECEIVED ; sinon 403/409. | conforme. |
| BR-WALLET-001 | Un USER normal ne se crédite jamais lui-même. | **AC-WALLET-001** GIVEN USER WHEN credit THEN 403. | conforme et testé. |
| BR-WALLET-002 | Le crédit de démonstration est réservé à ADMIN et un montant positif. | **AC-WALLET-002** GIVEN ADMIN/montant positif WHEN credit THEN solde crédité ; GIVEN non positif THEN 400. | conforme. |
| BR-WALLET-003 | Le wallet n'est pas un PSP ni une preuve de paiement bancaire réel. | **AC-WALLET-003** GIVEN démonstration WHEN paiement THEN écran/documentation l'indiquent comme simulation. | partiel : backend simule ; wording UI à vérifier. |

## Administration

| ID | Règle cible | Critères d'acceptation |
|---|---|---|
| BR-ADMIN-001 | Seul ADMIN exécute les endpoints d'administration. | **AC-ADMIN-001** GIVEN USER WHEN admin endpoint THEN 403 ; GIVEN ADMIN THEN action autorisée. |
| BR-ADMIN-002 | Toute politique de modération définit l'action, la ressource et la conservation/audit. | **AC-ADMIN-002** GIVEN suppression ADMIN WHEN action THEN résultat et trace répondent à la politique ; politique encore à définir. |
