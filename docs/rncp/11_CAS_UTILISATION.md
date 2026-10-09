# Cas d'utilisation textuels — WatYouFace

Les scénarios alternatifs sont des résultats attendus de l'analyse, pas des comportements présumés du code. Les états d'implémentation sont dans `14_MATRICE_EXIGENCE_CODE.md`.

## UC-01 — Inscription

**Acteur principal :** Visiteur. **Secondaire :** contrat actif.  
**Préconditions :** contrat disponible ; visiteur non connecté. **Déclencheur :** soumission du formulaire.  
**Nominal :** 1) le visiteur saisit username, email et mot de passe ; 2) il accepte le contrat ; 3) le système valide format et unicité ; 4) le système hache le mot de passe, crée le compte USER et lie la version du contrat ; 5) il retourne une confirmation sans mot de passe.  
**Alternatifs/exceptions :** données invalides ou identifiant déjà utilisé → 400/409 ; contrat absent → création/activation refusée ; contrat non accepté → compte éventuellement incomplet mais accès membre refusé selon BR-AUTH-002.  
**Postconditions :** compte créé avec état d'acceptation explicite. **Données :** User, Contract/UserContract. **Sécurité :** validation serveur, BCrypt, message non sensible.

## UC-02 — Connexion

**Acteur principal :** Visiteur. **Préconditions :** compte existant, contrat accepté. **Déclencheur :** soumission email/mot de passe.  
**Nominal :** validation → authentification → émission JWT → accès aux pages/API protégées.  
**Alternatifs/exceptions :** identifiants erronés ou contrat non accepté → 401 générique ; champ invalide → 400 ; accès sans JWT → 401.  
**Postconditions :** contexte utilisateur accessible ; logout efface le jeton local. **Données :** User, JWT. **Sécurité :** pas d'énumération de comptes, expiration/stockage documentés.

## UC-03 — Publier un post

**Acteur principal :** USER. **Préconditions :** authentifié. **Déclencheur :** publication du formulaire.  
**Nominal :** saisie contenu/média → validation → identité issue du JWT → post créé, horodaté et affichable dans le feed.  
**Alternatifs/exceptions :** absence JWT → 401 ; contenu/média invalide → 400 ; erreur stockage média → erreur contrôlée, pas de post incohérent.  
**Postconditions :** Post lié au USER. **Règles :** BR-POST-001. **Données :** Post, média éventuel. **Sécurité :** taille/type/contenu média à durcir.

## UC-04 — Modifier ou supprimer son post

**Acteur principal :** Propriétaire ; **secondaire :** ADMIN. **Préconditions :** post existant. **Déclencheur :** action modifier/supprimer.  
**Nominal :** système compare l'auteur et l'identité → met à jour le champ autorisé ou supprime le post.  
**Alternatifs/exceptions :** non-propriétaire → 403 ; post absent → 404 ; contenu invalide → 400.  
**Postconditions :** feed cohérent ; dépendances traitées selon politique. **Règles :** BR-POST-002 à 004. **Sécurité :** ownership serveur.

## UC-05 — Liker ou commenter

**Acteur principal :** USER. **Préconditions :** post visible et authentification. **Déclencheur :** clic like ou soumission commentaire.  
**Nominal like :** système vérifie que le post n'appartient pas à l'acteur, crée/retire son unique like et actualise le compteur.  
**Nominal commentaire :** système valide un contenu non vide, rattache commentaire au post et à l'acteur.  
**Alternatifs/exceptions :** auto-like → 403/409 ; doublon like → retrait/idempotence selon API ; post absent → 404 ; commentaire vide → 400.  
**Postconditions :** interaction attribuée. **Règles :** BR-LIKE-001/002, BR-COMMENT-001/002. **Sécurité :** l'auteur ne vient pas du body.

## UC-06 — Démarrer et utiliser une conversation

**Acteur principal :** USER/Participant. **Acteurs secondaires :** autre USER, participants groupe éventuels. **Préconditions :** comptes existants. **Déclencheur :** ouverture/création de conversation.  
**Nominal :** système crée ou retrouve une conversation privée incluant les deux participants → participant consulte l'historique → se connecte STOMP avec JWT → s'abonne au canal autorisé → envoie/reçoit un message persistant.  
**Alternatifs/exceptions :** participant inconnu → 404/400 ; non-membre en lecture/envoi/abonnement → 403/refus ; contenu vide/trop long → 400.  
**Postconditions :** échange accessible uniquement aux membres. **Règles :** BR-CHAT-001 à 003. **Données :** Conversation, ConversationUser, Message.

## UC-07 — Créer une annonce

**Acteur principal :** Vendeur (USER). **Préconditions :** authentifié. **Déclencheur :** publication d'annonce.  
**Nominal :** validation titre/prix/description → seller issu du JWT → annonce `AVAILABLE`.  
**Alternatifs/exceptions :** prix négatif/champ requis absent → 400 ; absence JWT → 401.  
**Postconditions :** Listing associé au vendeur. **Règle :** BR-MARKET-001. **Données :** Listing, image éventuelle.

## UC-08 — Demander un achat

**Acteur principal :** Acheteur (USER). **Secondaire :** Vendeur. **Préconditions :** annonce `AVAILABLE`, acteur différent du vendeur. **Déclencheur :** demande d'achat.  
**Nominal :** contrôle identité/état → buyer défini → état `PENDING`.  
**Alternatifs/exceptions :** vendeur demande sa propre annonce → refus ; annonce non AVAILABLE → 409 ; annonce absente → 404.  
**Postconditions :** vendeur peut accepter/refuser. **Règle :** BR-MARKET-002.

## UC-09 — Accepter ou refuser

**Acteur principal :** Vendeur. **Préconditions :** annonce `PENDING`, buyer défini. **Déclencheur :** action accepter/refuser.  
**Nominal :** contrôle vendeur/ADMIN et état → `ACCEPTED` ou `REFUSED`.  
**Alternatifs/exceptions :** acheteur ou tiers → 403 ; autre état → 409.  
**Postconditions :** paiement seulement après acceptation ; refus ne crée pas de transaction. **Règles :** BR-MARKET-003/004.

## UC-10 — Paiement simulé

**Acteur principal :** Acheteur. **Secondaire :** Vendeur ; wallet de démonstration. **Préconditions :** annonce `ACCEPTED`, acheteur désigné, solde suffisant, aucune transaction de l'annonce. **Déclencheur :** action payer.  
**Nominal :** verrou annonce → contrôle identité/état → débit wallet acheteur + crédit vendeur + Transaction → annonce `PAID`, dans une transaction applicative.  
**Alternatifs/exceptions :** tiers → 403 ; second paiement/état invalide → 409 ; solde insuffisant → conflit métier contrôlé.  
**Postconditions :** une transaction simulée existe, sans appel PSP. **Règles :** BR-MARKET-005, BR-WALLET-003.

## UC-11 — Expédier

**Acteur principal :** Vendeur. **Préconditions :** annonce `PAID`. **Déclencheur :** action expédier.  
**Nominal :** contrôle vendeur/ADMIN et état → `SHIPPED`.  
**Alternatifs/exceptions :** tiers → 403 ; état autre que PAID → 409. **Postconditions :** acheteur peut confirmer. **Règle :** BR-MARKET-006.

## UC-12 — Confirmer réception

**Acteur principal :** Acheteur. **Préconditions :** annonce `SHIPPED`, acheteur désigné. **Déclencheur :** confirmation.  
**Nominal :** contrôle acheteur/ADMIN et état → `RECEIVED`.  
**Alternatifs/exceptions :** vendeur/tier → 403 ; état autre → 409. **Postconditions :** cycle terminé. **Règle :** BR-MARKET-007.

## UC-13 — Administrer (périmètre limité)

**Acteur principal :** ADMIN. **Préconditions :** rôle ADMIN. **Déclencheur :** administration utilisateurs/rôles/modération.  
**Nominal :** contrôle rôle → opération sur la ressource prévue → réponse contrôlée.  
**Alternatifs/exceptions :** USER → 403 ; rôle invalide → 400 ; ressource absente → 404. **Postconditions :** action de modération/rôle effectuée selon politique à compléter. **Règle :** BR-ADMIN-001/002.
