# Exigences fonctionnelles détaillées — WatYouFace

**Statut :** exigences cibles d'analyse. Une exigence n'est pas réputée satisfaite parce qu'un écran ou un endpoint porte un nom voisin : la comparaison avec le code est consignée dans `14_MATRICE_EXIGENCE_CODE.md`.

## 1. Accès et identité

| ID | Exigence cible | Acteur | Résultat attendu | Erreurs / sécurité |
|---|---|---|---|---|
| AUTH-001 | Créer un compte avec username, email et mot de passe valides. | Visiteur | compte USER créé, mot de passe jamais retourné. | email/username uniques ; 400 si données invalides ou déjà utilisées. |
| AUTH-002 | Accepter la version active du contrat avant l'accès fonctionnel à l'espace membre. | Visiteur/USER | acceptation liée à l'identité et à la version du contrat. | accès refusé tant que l'acceptation manque ; l'identité ne vient jamais du client. |
| AUTH-003 | Se connecter par email et mot de passe valides. | Visiteur | jeton de session et informations minimales de profil retournés. | 401 générique si identifiants invalides ; validation d'entrée. |
| AUTH-004 | Se déconnecter de l'interface. | USER/ADMIN | le jeton local est supprimé et les pages protégées ne sont plus accessibles dans le navigateur. | pas de secret dans URL/logs ; révocation serveur à décider. |
| AUTH-005 | Bloquer les pages et API protégées aux non-authentifiés. | Visiteur | 401 API et redirection/retour d'accès UI cohérent. | contrôle serveur autoritaire. |
| AUTH-006 | Réserver l'administration aux ADMIN. | ADMIN/USER | ADMIN autorisé, USER refusé 403. | rôle évalué serveur. |

## 2. Profil et données personnelles

| ID | Exigence cible | Acteur | Résultat attendu | Erreurs / sécurité |
|---|---|---|---|
| PROFILE-001 | Consulter son propre profil. | USER/ADMIN | username, email, avatar, rôle et informations pertinentes du compte. | aucune lecture du profil privé d'autrui sans règle explicite. |
| PROFILE-002 | Modifier son username selon les contraintes définies. | Propriétaire | profil mis à jour et unicité préservée. | 400 validation, 409 si username déjà pris. |
| PROFILE-003 | Remplacer son avatar. | Propriétaire | URL d'avatar mise à jour après contrôle du fichier. | type/taille/contenu validés ; refus fichier invalide. |
| PROFILE-004 | Demander la suppression de son compte si cette fonction est conservée. | Propriétaire/ADMIN | suppression selon une politique de conservation à décider. | ownership ou rôle ADMIN ; effets de bord documentés. |

**Données personnelles identifiées à traiter en phase conformité :** email, username, avatar, messages, posts/commentaires, données de contrat, annonces, identifiants de transaction, soldes de démonstration et journaux techniques. Leur minimisation, visibilité, durée de conservation et droits d'accès/suppression restent à spécifier.

## 3. Feed social

| ID | Exigence cible | Acteur | Résultat attendu | Erreurs / sécurité |
|---|---|---|---|
| SOCIAL-001 | Consulter le feed des publications visibles. | USER/ADMIN | liste ordonnée, auteur, contenu, médias, compteurs like/commentaire. | visibilité et pagination à préciser ; aucune donnée privée inutile. |
| POST-001 | Créer une publication texte et, si retenu, joindre un média autorisé. | USER | post lié au USER authentifié et horodaté. | validation contenu/média ; 401 sans identité. |
| POST-002 | Modifier son propre post. | Propriétaire/ADMIN | seul le contenu autorisé est modifié. | 403 non-propriétaire ; règles ADMIN explicites. |
| POST-003 | Supprimer son propre post. | Propriétaire/ADMIN | post et dépendances gérées conformément à la politique de suppression. | 403 non-propriétaire ; 404 inexistant. |
| COMMENT-001 | Commenter une publication visible. | USER | commentaire rattaché au post et à l'auteur authentifié. | 400 contenu absent/invalide ; 404 post absent. |
| COMMENT-002 | Modifier/supprimer son commentaire. | Propriétaire/ADMIN | changement ou suppression du seul commentaire autorisé. | 403 non-propriétaire. |
| LIKE-001 | Liker ou retirer son like sur le post d'un autre USER. | USER | au plus un like par USER et par post ; action idempotente/toggle définie. | 403/400 sur cible interdite ; identité issue du jeton. |

## 4. Chat

| ID | Exigence cible | Acteur | Résultat attendu | Erreurs / sécurité |
|---|---|---|---|
| CHAT-001 | Créer ou retrouver une conversation privée avec un autre USER. | USER | conversation avec les deux participants, sans doublon logique. | 400 si destinataire invalide/soi-même selon règle à décider. |
| CHAT-002 | Créer une conversation de groupe si la fonction est retenue. | USER | créateur et participants valides sont membres. | validation titre/liste ; aucun participant implicite perdu. |
| CHAT-003 | Consulter la liste de ses conversations et l'historique d'une conversation dont il est membre. | Participant | seules ses conversations et leurs messages sont retournés. | 403 non-membre ; pagination/limites contrôlées. |
| CHAT-004 | Envoyer et recevoir un message dans une conversation. | Participant | message persistant, horodaté et diffusé aux participants. | 403 non-membre ; contenu non vide et borné. |
| CHAT-005 | S'abonner en temps réel uniquement à une conversation dont on est membre. | Participant | réception des événements de sa conversation. | connexion JWT STOMP et refus abonnement tiers. |
| CHAT-006 | Utiliser le chat pour une discussion sociale ou une négociation relative à une annonce. | USER/Acheteur/Vendeur | l'usage est possible sans créer de mécanisme automatique de transaction. | le lien conversation-annonce et sa visibilité sont à décider. |

## 5. Marketplace et wallet de démonstration

| ID | Exigence cible | Acteur | Résultat attendu | Erreurs / sécurité |
|---|---|---|---|---|
| MARKET-001 | Créer une annonce. | USER/Vendeur | vendeur issu de l'identité, état `AVAILABLE`, titre/prix valides. | 400 validation ; vendeur jamais fourni par le client. |
| MARKET-002 | Consulter les annonces autorisées. | USER/ADMIN | informations d'annonce, état et vendeur minimal. | pagination/visibilité à préciser. |
| MARKET-003 | Demander l'achat d'une annonce `AVAILABLE`. | USER/Acheteur | acheteur défini, état `PENDING`. | vendeur ne peut acheter sa propre annonce ; 409 état invalide. |
| MARKET-004 | Accepter ou refuser une demande. | Vendeur/ADMIN | `PENDING → ACCEPTED` ou `PENDING → REFUSED`. | 403 acteur non autorisé ; 409 état invalide. |
| MARKET-005 | Payer une annonce acceptée avec le wallet **simulé**. | Acheteur/ADMIN | débit acheteur, crédit vendeur, transaction et état `PAID` atomiques. | solde insuffisant/double paiement/état invalide refusés ; aucun PSP réel. |
| MARKET-006 | Marquer l'annonce expédiée puis reçue. | Vendeur puis Acheteur/ADMIN | `PAID → SHIPPED → RECEIVED`. | 403 acteur non concerné ; 409 état invalide. |
| WALLET-001 | Consulter son propre wallet de démonstration. | USER/ADMIN | solde du compte courant. | owner/admin uniquement sur un wallet ciblé. |
| WALLET-002 | Alimenter le wallet de démonstration. | ADMIN | crédit de démonstration limité à ADMIN, montant positif. | USER refusé 403 ; pas de paiement bancaire. |

## 6. Administration utile au MVP

| ID | Exigence cible | Acteur | Résultat attendu | Erreurs / sécurité |
|---|---|---|---|---|
| ADMIN-001 | Consulter les utilisateurs pour administration. | ADMIN | données strictement nécessaires à l'administration. | USER 403 ; limitation des données exposées à analyser. |
| ADMIN-002 | Modifier un rôle selon une politique de gouvernance. | ADMIN | rôle valide appliqué au compte cible. | 400 rôle invalide ; audit/auto-élévation à décider. |
| ADMIN-003 | Modérer post, commentaire ou annonce. | ADMIN | suppression selon une politique explicite. | 404 ressource absente ; conservation/audit à décider. |

## 7. Réponses attendues et limites

Les opérations réussies retournent un statut 2xx et les données minimales utiles. Les erreurs attendues sont : 400 donnée invalide, 401 non authentifié, 403 interdit, 404 absent, 409 conflit d'état métier. Les comportements 500, messages techniques, règles de visibilité, pagination, annulation et litiges ne doivent pas être confondus avec des règles déjà validées : ils sont ouverts ou consignés comme écarts.

## Traçabilité RNCP

Cette spécification soutient C2 (interfaces), C3 (règles), C5 (analyse du besoin), C6 (domaines reliés), C7/C8 (données/accès à concevoir) et C9 (résultats testables).
