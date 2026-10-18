# WatYouFace — Préparation questionnaire RNCP CDA

> Source de préparation de Kouakou Brice Kanga, basée sur WatYouFace et les
> preuves RNCP existantes. Les définitions sont générales ; les exemples projet
> ne revendiquent aucune fonctionnalité non prouvée.

## Mode d'emploi

- Chaque réponse comprend une définition, une explication, un piège et un exemple.
- Les exemples WatYouFace servent à contextualiser ; ils ne remplacent pas la définition.
- Les niveaux sont : 20 faciles, 56 intermédiaires et 30 difficiles.

## 1. Questions faciles — 20

### Q1 — Qu'est-ce qu'une variable ?

**Réponse.** Une zone nommée qui stocke une valeur modifiable.

**Explication.** Elle possède un type et une durée de vie selon le langage.

**Piège éventuel.** Confondre variable et constante.

**Exemple WatYouFace.** Un montant de wallet avant calcul.

### Q2 — Qu'est-ce qu'une constante ?

**Réponse.** Une valeur qui ne doit pas être réaffectée après initialisation.

**Explication.** Elle exprime une règle stable ou une configuration immuable.

**Piège éventuel.** Croire qu'un objet référencé est toujours profondément immuable.

**Exemple WatYouFace.** La limite d'upload de 10 Mo.

### Q3 — Qu'est-ce qu'un type ?

**Réponse.** Une catégorie de valeurs et d'opérations autorisées.

**Explication.** Exemples : entier, chaîne, booléen.

**Piège éventuel.** Penser que tous les types se convertissent sans risque.

**Exemple WatYouFace.** Un statut Marketplace est une valeur contrôlée.

### Q4 — Qu'est-ce qu'une condition ?

**Réponse.** Une instruction qui choisit un chemin selon une expression booléenne.

**Explication.** Elle permet de traiter un cas métier ou une erreur.

**Piège éventuel.** Oublier le cas négatif.

**Exemple WatYouFace.** Refuser un paiement si le solde est insuffisant.

### Q5 — Qu'est-ce qu'une boucle ?

**Réponse.** Une répétition contrôlée d'instructions.

**Explication.** Elle parcourt par exemple une collection.

**Piège éventuel.** Créer une boucle infinie ou coûteuse.

**Exemple WatYouFace.** Parcourir une liste de listings paginée.

### Q6 — Qu'est-ce qu'une fonction ?

**Réponse.** Un bloc réutilisable qui reçoit éventuellement des paramètres et produit un résultat.

**Explication.** Elle réduit la duplication.

**Piège éventuel.** La confondre avec une classe.

**Exemple WatYouFace.** Valider un montant strictement positif.

### Q7 — Différence méthode et fonction ?

**Réponse.** Une méthode est une fonction associée à une classe ou un objet.

**Explication.** Elle peut agir sur l'état de cet objet.

**Piège éventuel.** Dire qu'il n'existe aucune différence selon le contexte objet.

**Exemple WatYouFace.** Une méthode de service Marketplace.

### Q8 — Qu'est-ce qu'une classe ?

**Réponse.** Un modèle qui définit état et comportements d'objets.

**Explication.** Elle regroupe attributs et méthodes.

**Piège éventuel.** La confondre avec une instance.

**Exemple WatYouFace.** Une classe Java de service.

### Q9 — Qu'est-ce qu'un objet ?

**Réponse.** Une instance concrète d'une classe.

**Explication.** Il possède un état propre à cette instance.

**Piège éventuel.** Confondre classe et objet.

**Exemple WatYouFace.** Un listing chargé depuis la base.

### Q10 — Qu'est-ce qu'une interface Java ?

**Réponse.** Un contrat de méthodes qu'une classe peut implémenter.

**Explication.** Elle favorise abstraction et découplage.

**Piège éventuel.** La confondre avec l'interface graphique.

**Exemple WatYouFace.** Un repository Spring Data.

### Q11 — Qu'est-ce que l'héritage ?

**Réponse.** Le mécanisme par lequel une classe reprend des membres d'une autre.

**Explication.** Il modélise une relation est-un.

**Piège éventuel.** L'utiliser quand la composition suffit.

**Exemple WatYouFace.** Une exception spécialisée hérite d'une exception.

### Q12 — Qu'est-ce que le polymorphisme ?

**Réponse.** La possibilité de manipuler plusieurs implémentations via un même contrat.

**Explication.** Le comportement concret dépend de l'objet.

**Piège éventuel.** Le réduire à l'héritage uniquement.

**Exemple WatYouFace.** Une interface de stockage avec plusieurs implémentations.

### Q13 — Qu'est-ce que l'encapsulation ?

**Réponse.** Le fait de protéger l'état interne derrière une API contrôlée.

**Explication.** Elle limite les modifications incohérentes.

**Piège éventuel.** Exposer directement les champs sensibles.

**Exemple WatYouFace.** Le solde wallet modifié via un service contrôlé.

### Q14 — Qu'est-ce qu'une exception ?

**Réponse.** Un signal d'erreur ou de situation anormale géré par le programme.

**Explication.** Elle doit être traduite en réponse adaptée.

**Piège éventuel.** Attraper toutes les exceptions et les ignorer.

**Exemple WatYouFace.** Image illisible transformée en erreur client 400.

### Q15 — Qu'est-ce que HTTP ?

**Réponse.** Un protocole requête-réponse entre client et serveur.

**Explication.** Il transporte méthodes, headers et corps.

**Piège éventuel.** Le confondre avec HTTPS.

**Exemple WatYouFace.** React appelle l'API Spring Boot en HTTP sécurisé.

### Q16 — Que signifie HTTPS ?

**Réponse.** HTTP protégé par TLS.

**Explication.** TLS chiffre le transport et authentifie le serveur selon le certificat.

**Piège éventuel.** Dire que HTTPS chiffre la base de données.

**Exemple WatYouFace.** Le cookie Secure est envoyé sur HTTPS en production.

### Q17 — Qu'est-ce qu'une requête HTTP ?

**Réponse.** Un message client vers serveur avec méthode, URL, headers et éventuellement body.

**Explication.** Le serveur renvoie une réponse.

**Piège éventuel.** Oublier que GET peut aussi avoir des headers.

**Exemple WatYouFace.** POST admin de crédit wallet.

### Q18 — Que signifie JSON ?

**Réponse.** Un format texte structuré d'échange de données.

**Explication.** Il représente objets, tableaux, chaînes, nombres et booléens.

**Piège éventuel.** Le confondre avec une base de données.

**Exemple WatYouFace.** DTO REST envoyé au frontend.

### Q19 — Qu'est-ce qu'une clé primaire ?

**Réponse.** Une colonne ou combinaison qui identifie une ligne de façon unique.

**Explication.** Elle sert aux relations et à l'identité.

**Piège éventuel.** La confondre avec un index quelconque.

**Exemple WatYouFace.** Id d'utilisateur ou de listing.

### Q20 — Qu'est-ce qu'un commit Git ?

**Réponse.** Un instantané versionné avec message dans un dépôt.

**Explication.** Il sert à tracer une évolution cohérente.

**Piège éventuel.** Confondre commit local et push distant.

**Exemple WatYouFace.** Un commit documentaire RNCP.

## 2. Questions intermédiaires — 56

### Q21 — Complexité O(n) : que signifie-t-elle ?

**Réponse.** Le temps croît approximativement linéairement avec le nombre d'éléments.

**Explication.** Un parcours unique d'une liste est souvent O(n).

**Piège éventuel.** Croire que O(n) donne un temps exact.

**Exemple WatYouFace.** Parcourir une page de listings.

### Q22 — Qu'est-ce que la récursivité ?

**Réponse.** Une fonction qui s'appelle elle-même jusqu'à une condition d'arrêt.

**Explication.** Elle convient à certains arbres ou parcours.

**Piège éventuel.** Oublier la condition d'arrêt.

**Exemple WatYouFace.** Non nécessaire au flux Marketplace.

### Q23 — GET sert à quoi ?

**Réponse.** À lire une ressource sans la modifier.

**Explication.** Il doit être sans effet de bord métier.

**Piège éventuel.** Utiliser GET pour un crédit wallet.

**Exemple WatYouFace.** Lister des annonces.

### Q24 — POST sert à quoi ?

**Réponse.** À créer une ressource ou déclencher une action métier.

**Explication.** Il contient souvent un body.

**Piège éventuel.** Dire qu'il crée toujours une ligne.

**Exemple WatYouFace.** Demander un achat ou créditer un wallet ADMIN.

### Q25 — PUT et PATCH : différence ?

**Réponse.** PUT remplace généralement une représentation complète ; PATCH modifie partiellement.

**Explication.** Le contrat API doit préciser le comportement.

**Piège éventuel.** Les considérer strictement identiques.

**Exemple WatYouFace.** Mise à jour partielle d'un profil.

### Q26 — DELETE sert à quoi ?

**Réponse.** À demander la suppression d'une ressource.

**Explication.** La suppression peut être physique ou logique.

**Piège éventuel.** Supposer qu'il doit toujours retourner 200.

**Exemple WatYouFace.** Modérer un listing AVAILABLE.

### Q27 — Que sont les headers HTTP ?

**Réponse.** Des métadonnées de requête ou réponse.

**Explication.** Ils portent notamment type, auth, cache ou CSRF.

**Piège éventuel.** Les mettre dans le body sans raison.

**Exemple WatYouFace.** Content-Type et token CSRF.

### Q28 — Qu'est-ce qu'un body HTTP ?

**Réponse.** Le contenu de la requête ou réponse.

**Explication.** Il transporte souvent du JSON ou multipart.

**Piège éventuel.** Croire qu'une requête GET doit avoir un body.

**Exemple WatYouFace.** Payload de crédit wallet.

### Q29 — Cookie et session : différence ?

**Réponse.** Le cookie est stocké côté navigateur ; la session classique est souvent stockée côté serveur.

**Explication.** Un cookie peut contenir un identifiant ou un JWT.

**Piège éventuel.** Dire qu'un cookie est forcément une session.

**Exemple WatYouFace.** JWT WatYouFace en cookie HttpOnly.

### Q30 — Qu'est-ce qu'une API REST ?

**Réponse.** Une API HTTP orientée ressources et représentations, utilisant des méthodes et statuts cohérents.

**Explication.** REST est un style, pas un protocole.

**Piège éventuel.** Dire que toute API JSON est REST.

**Exemple WatYouFace.** Routes utilisateurs, listings et admin.

### Q31 — HTTP 200 ?

**Réponse.** Succès avec réponse.

**Explication.** Utilisé quand une lecture ou action réussit.

**Piège éventuel.** L'utiliser pour toute création.

**Exemple WatYouFace.** Lecture réussie.

### Q32 — HTTP 201 ?

**Réponse.** Ressource créée avec succès.

**Explication.** Souvent accompagné de sa représentation ou localisation.

**Piège éventuel.** Le confondre avec 200 obligatoire.

**Exemple WatYouFace.** Création d'une ressource.

### Q33 — HTTP 204 ?

**Réponse.** Succès sans contenu de réponse.

**Explication.** Utile après une suppression réussie selon contrat.

**Piège éventuel.** Envoyer un body avec 204.

**Exemple WatYouFace.** Suppression qui ne retourne rien.

### Q34 — HTTP 400 ?

**Réponse.** Requête invalide côté client.

**Explication.** Format, validation ou contenu non lisible.

**Piège éventuel.** L'utiliser pour une absence de droit.

**Exemple WatYouFace.** Image corrompue ou montant invalide.

### Q35 — HTTP 404 ?

**Réponse.** Ressource demandée inexistante.

**Explication.** Le serveur ne trouve pas l'objet ciblé.

**Piège éventuel.** Masquer systématiquement un 403 derrière un 404.

**Exemple WatYouFace.** Utilisateur cible absent pour crédit.

### Q36 — HTTP 409 ?

**Réponse.** Conflit avec l'état courant de la ressource.

**Explication.** La requête est comprise mais impossible dans cet état.

**Piège éventuel.** Le confondre avec toute validation syntaxique.

**Exemple WatYouFace.** Solde Marketplace insuffisant.

### Q37 — HTTP 415 ?

**Réponse.** Type de média non supporté.

**Explication.** Le Content-Type n'est pas accepté par la ressource.

**Piège éventuel.** Le confondre avec image corrompue.

**Exemple WatYouFace.** MIME d'upload non autorisé.

### Q38 — HTTP 422 ?

**Réponse.** Syntaxe compréhensible mais validation sémantique impossible.

**Explication.** Son usage dépend du contrat API.

**Piège éventuel.** Dire qu'il est obligatoire en REST.

**Exemple WatYouFace.** Un champ métier valide en JSON mais incohérent.

### Q39 — HTTP 500, 502, 503 ?

**Réponse.** 500 est une erreur interne ; 502 une mauvaise réponse d'amont ; 503 un service indisponible.

**Explication.** Ils ne sont pas tous des erreurs de code applicatif.

**Piège éventuel.** Confondre 502 et 503.

**Exemple WatYouFace.** Proxy ou plateforme indisponible.

### Q40 — Qu'est-ce que le DOM ?

**Réponse.** La représentation objet du document HTML dans le navigateur.

**Explication.** JavaScript et React le mettent à jour.

**Piège éventuel.** Dire que React réécrit toujours toute la page.

**Exemple WatYouFace.** React met à jour l'interface.

### Q41 — Qu'est-ce qu'une SPA ?

**Réponse.** Une application dont la navigation principale se fait côté client sans rechargement complet.

**Explication.** Le backend reste indispensable.

**Piège éventuel.** Croire qu'une SPA n'a pas de routes serveur.

**Exemple WatYouFace.** Frontend React WatYouFace.

### Q42 — Qu'est-ce qu'un composant React ?

**Réponse.** Une unité réutilisable de rendu et de comportement.

**Explication.** Il reçoit props et peut posséder un état.

**Piège éventuel.** Mettre toute l'application dans un seul composant.

**Exemple WatYouFace.** CreateListingDialog.

### Q43 — Props et state ?

**Réponse.** Props viennent du parent ; state appartient au composant et évolue.

**Explication.** Un changement d'état déclenche un rendu.

**Piège éventuel.** Modifier directement une prop.

**Exemple WatYouFace.** État d'un formulaire upload.

### Q44 — Que fait useEffect ?

**Réponse.** Il synchronise le composant avec un effet externe après rendu.

**Explication.** Exemples : appel API ou abonnement.

**Piège éventuel.** L'utiliser pour tout calcul simple.

**Exemple WatYouFace.** Charger une liste après montage.

### Q45 — Que font useMemo et useCallback ?

**Réponse.** Ils mémorisent respectivement une valeur calculée ou une fonction.

**Explication.** Ils servent seulement lorsqu'une mesure justifie l'optimisation.

**Piège éventuel.** Les appliquer systématiquement.

**Exemple WatYouFace.** Filtre coûteux ou callback enfant.

### Q46 — Qu'est-ce qu'un composant contrôlé ?

**Réponse.** Un champ dont la valeur est pilotée par le state React.

**Explication.** Il facilite validation et messages d'erreur.

**Piège éventuel.** Mélanger sans raison state et DOM non contrôlé.

**Exemple WatYouFace.** Champ montant de crédit.

### Q47 — Qu'est-ce que le routing frontend ?

**Réponse.** La correspondance entre URL et écran côté SPA.

**Explication.** Il améliore navigation et liens directs.

**Piège éventuel.** Croire qu'il sécurise une route API.

**Exemple WatYouFace.** Route admin masquée ou affichée selon rôle.

### Q48 — Responsive design ?

**Réponse.** Interface qui s'adapte aux tailles et modes d'écran.

**Explication.** Il combine CSS, structure et tests visuels.

**Piège éventuel.** Le réduire à réduire les polices.

**Exemple WatYouFace.** Cartes Marketplace mobile.

### Q49 — Accessibilité : principe de base ?

**Réponse.** Rendre contenus et actions utilisables par différents profils et aides techniques.

**Explication.** Labels, noms accessibles, focus et contraste sont essentiels.

**Piège éventuel.** Dire qu'un aria-label résout tout.

**Exemple WatYouFace.** Formulaires et tableau ADMIN améliorés.

### Q50 — Lazy loading ?

**Réponse.** Chargement différé d'une ressource ou module.

**Explication.** Il réduit le coût initial quand pertinent.

**Piège éventuel.** L'utiliser sans mesurer ni gérer état de chargement.

**Exemple WatYouFace.** Écran non critique chargé à la demande.

### Q51 — Bundling ?

**Réponse.** Regroupement et optimisation des modules pour le navigateur.

**Explication.** Vite produit le bundle de production.

**Piège éventuel.** Croire qu'un bundle faible garantit l'accessibilité.

**Exemple WatYouFace.** Build frontend mesuré dans la documentation.

### Q52 — Dependency injection ?

**Réponse.** Fourniture d'une dépendance à une classe plutôt que création directe interne.

**Explication.** Elle facilite testabilité et découplage.

**Piège éventuel.** Confondre injection et héritage.

**Exemple WatYouFace.** Spring injecte un service ou repository.

### Q53 — Annotation Spring ?

**Réponse.** Métadonnée déclarative qui configure un comportement du framework.

**Explication.** Exemples : composant, route, transaction.

**Piège éventuel.** Croire qu'elle remplace toute logique.

**Exemple WatYouFace.** Controller et service annotés.

### Q54 — REST controller ?

**Réponse.** Composant qui expose des endpoints HTTP et renvoie souvent JSON.

**Explication.** Il délègue la logique au service.

**Piège éventuel.** Faire les requêtes SQL dans le controller.

**Exemple WatYouFace.** Endpoint de wallet ADMIN.

### Q55 — Validation backend ?

**Réponse.** Contrôle serveur des entrées et règles de contrat.

**Explication.** Elle complète la validation frontend.

**Piège éventuel.** Faire confiance au navigateur.

**Exemple WatYouFace.** Montant wallet supérieur à zéro.

### Q56 — Exception handler ?

**Réponse.** Composant central qui transforme une exception en réponse HTTP cohérente.

**Explication.** Il évite stack trace client.

**Piège éventuel.** Retourner une 500 pour une erreur utilisateur.

**Exemple WatYouFace.** Image illisible devient 400.

### Q57 — Configuration et profils ?

**Réponse.** Paramètres séparés selon environnement, par exemple développement ou production.

**Explication.** Les secrets restent externalisés.

**Piège éventuel.** Versionner les mots de passe.

**Exemple WatYouFace.** Port Railway via variable PORT.

### Q58 — Variable d'environnement ?

**Réponse.** Valeur injectée au processus à l'exécution.

**Explication.** Elle sert à configurer sans coder le secret.

**Piège éventuel.** La confondre avec un secret forcément.

**Exemple WatYouFace.** URL base ou profil actif.

### Q59 — SQL : qu'est-ce que c'est ?

**Réponse.** Langage de définition et interrogation des bases relationnelles.

**Explication.** Il manipule schéma et données.

**Piège éventuel.** Croire que JPA supprime le besoin de SQL.

**Exemple WatYouFace.** Migrations Flyway SQL.

### Q60 — Table, ligne, colonne ?

**Réponse.** Table : collection structurée ; ligne : enregistrement ; colonne : attribut.

**Explication.** Le schéma impose types et contraintes.

**Piège éventuel.** Dire qu'une colonne est une clé forcément.

**Exemple WatYouFace.** Table listings et colonne price.

### Q61 — Clé étrangère ?

**Réponse.** Contrainte qui référence une clé d'une autre table.

**Explication.** Elle protège l'intégrité référentielle.

**Piège éventuel.** Croire qu'elle valide les droits applicatifs.

**Exemple WatYouFace.** Listing lié à son vendeur.

### Q62 — Index ?

**Réponse.** Structure qui accélère certaines recherches au prix d'espace et de coût d'écriture.

**Explication.** Il se choisit selon les requêtes.

**Piège éventuel.** Indexer toutes les colonnes.

**Exemple WatYouFace.** Recherche fréquente par statut.

### Q63 — UNIQUE et NOT NULL ?

**Réponse.** UNIQUE interdit les doublons ; NOT NULL interdit l'absence de valeur.

**Explication.** Ce sont des contraintes de base.

**Piège éventuel.** Croire que UNIQUE implique clé primaire.

**Exemple WatYouFace.** Unicité de transaction listing.

### Q64 — ACID ?

**Réponse.** Atomicité, cohérence, isolation, durabilité.

**Explication.** Ce sont les propriétés attendues d'une transaction.

**Piège éventuel.** Dire qu'elles éliminent tous les bugs métier.

**Exemple WatYouFace.** Paiement wallet cohérent.

### Q65 — JOIN, INNER JOIN, LEFT JOIN ?

**Réponse.** JOIN relie tables ; INNER ne garde que correspondances ; LEFT garde toutes les lignes de gauche.

**Explication.** Le choix dépend du résultat voulu.

**Piège éventuel.** Croire que LEFT est toujours meilleur.

**Exemple WatYouFace.** Lister users même sans wallet selon requête.

### Q66 — Normalisation ?

**Réponse.** Organisation des données pour limiter redondance et anomalies.

**Explication.** Elle sépare des concepts liés par clés.

**Piège éventuel.** La pousser au détriment de toute performance.

**Exemple WatYouFace.** User, wallet, listing séparés.

### Q67 — 1-N et N-N ?

**Réponse.** 1-N : un parent vers plusieurs enfants ; N-N : plusieurs des deux côtés via table de jointure.

**Explication.** Les relations se modélisent explicitement.

**Piège éventuel.** Créer une N-N sans table de jointure relationnelle.

**Exemple WatYouFace.** Conversation et utilisateurs.

### Q68 — ORM ?

**Réponse.** Technique qui mappe objets et tables.

**Explication.** Elle accélère le développement mais exige compréhension SQL.

**Piège éventuel.** Croire qu'elle supprime les performances ou migrations.

**Exemple WatYouFace.** Hibernate mappe entities JPA.

### Q69 — N+1 query ?

**Réponse.** Une requête principale suivie d'une requête par élément lié.

**Explication.** Elle peut dégrader fortement les listes.

**Piège éventuel.** Croire que lazy est toujours mauvais.

**Exemple WatYouFace.** Liste de posts avec auteur.

### Q70 — Git repository ?

**Réponse.** Dossier avec historique Git et métadonnées.

**Explication.** Il peut avoir des remotes.

**Piège éventuel.** Confondre dépôt local et GitHub.

**Exemple WatYouFace.** Backend et frontend WatYouFace.

### Q71 — Branch et HEAD ?

**Réponse.** Une branche pointe vers une ligne de commits ; HEAD désigne la référence courante.

**Explication.** Ils servent au travail isolé.

**Piège éventuel.** Dire que HEAD est toujours un commit fixe.

**Exemple WatYouFace.** main pointe aux checkpoints.

### Q72 — Fetch et pull ?

**Réponse.** Fetch récupère les références distantes ; pull récupère puis intègre.

**Explication.** Pull peut créer un merge ou rebase selon config.

**Piège éventuel.** Croire que fetch modifie le fichier de travail.

**Exemple WatYouFace.** Audit origin/main.

### Q73 — Merge et rebase ?

**Réponse.** Merge crée une intégration avec historique de branche ; rebase rejoue des commits sur une autre base.

**Explication.** Le choix dépend de la politique d'historique.

**Piège éventuel.** Rebase une branche partagée sans précaution.

**Exemple WatYouFace.** PR intégrée par merge.

### Q74 — Pull request ?

**Réponse.** Demande de revue et intégration d'une branche.

**Explication.** Elle centralise discussion et CI.

**Piège éventuel.** Croire qu'elle remplace les tests.

**Exemple WatYouFace.** PR de lot backend.

### Q75 — .gitignore ?

**Réponse.** Liste de fichiers non suivis à ignorer.

**Explication.** Elle évite artefacts et secrets locaux.

**Piège éventuel.** Croire qu'elle retire un secret déjà commité.

**Exemple WatYouFace.** Ignorer target et fichiers environnement.

### Q76 — CI, CD et pipeline ?

**Réponse.** CI automatise contrôle/build ; CD automatise livraison ou déploiement selon définition ; pipeline enchaîne étapes.

**Explication.** Il faut décrire le périmètre réel.

**Piège éventuel.** Dire CI/CD si seul build existe.

**Exemple WatYouFace.** Frontend possède CI/CD ; backend CI et deploy manuel.

## 3. Questions difficiles — 30

### Q77 — Qu'est-ce qu'un artefact CI ?

**Réponse.** Le résultat produit par un pipeline, par exemple un build ou une image.

**Explication.** Il peut être réutilisé pour déploiement.

**Piège éventuel.** Le confondre avec le code source.

**Exemple WatYouFace.** Dist Vite ou image Docker.

### Q78 — Qu'est-ce qu'un runner CI ?

**Réponse.** Machine ou environnement qui exécute les jobs.

**Explication.** Il reçoit permissions et secrets limités.

**Piège éventuel.** Supposer qu'il a accès à la production.

**Exemple WatYouFace.** GitHub Actions runner.

### Q79 — Pourquoi un secret CI ?

**Réponse.** Pour fournir une donnée sensible sans la mettre dans le dépôt.

**Explication.** Il doit être limité et masqué.

**Piège éventuel.** L'afficher dans les logs.

**Exemple WatYouFace.** Clé SSH de déploiement frontend.

### Q80 — Image et container Docker ?

**Réponse.** Image : modèle immuable ; container : instance exécutée de cette image.

**Explication.** Une image peut lancer plusieurs containers.

**Piège éventuel.** Confondre image et volume.

**Exemple WatYouFace.** Backend Java construit puis exécuté.

### Q81 — Dockerfile et layers ?

**Réponse.** Dockerfile décrit la construction ; chaque instruction produit souvent une couche réutilisable.

**Explication.** L'ordre influe sur cache et taille.

**Piège éventuel.** Mettre secrets dans une layer.

**Exemple WatYouFace.** Multi-stage Maven puis JRE.

### Q82 — Volume et bind mount ?

**Réponse.** Volume géré par Docker ou plateforme ; bind mount lie un chemin hôte.

**Explication.** Les deux persistent hors cycle container selon contexte.

**Piège éventuel.** Croire que système de fichiers container est durable.

**Exemple WatYouFace.** Volume Railway /app/media.

### Q83 — Port mapping ?

**Réponse.** Association d'un port hôte/plateforme à un port container.

**Explication.** Le serveur doit écouter le port prévu.

**Piège éventuel.** Exposer inutilement toutes les interfaces.

**Exemple WatYouFace.** Railway fournit PORT.

### Q84 — Utilisateur non-root Docker ?

**Réponse.** Compte sans privilèges administrateur pour exécuter l'application.

**Explication.** Il réduit l'impact d'une compromission.

**Piège éventuel.** Penser que root temporaire d'entrypoint implique Java root.

**Exemple WatYouFace.** Java Railway s'exécute sous spring.

### Q85 — Multi-stage build ?

**Réponse.** Construction dans une image lourde puis copie du seul artefact dans une image runtime légère.

**Explication.** Réduit surface et taille finale.

**Piège éventuel.** Dire qu'il supprime toutes vulnérabilités.

**Exemple WatYouFace.** Maven build puis JRE 17.

### Q86 — VM ou container ?

**Réponse.** VM virtualise un système complet ; container partage le noyau hôte avec isolation processus.

**Explication.** Un container est plus léger mais pas une VM.

**Piège éventuel.** Dire qu'un container est un OS complet.

**Exemple WatYouFace.** Runtime Railway.

### Q87 — IP, DNS et port ?

**Réponse.** IP identifie un hôte réseau ; DNS traduit un nom ; port identifie un service.

**Explication.** Ils participent au routage.

**Piège éventuel.** Confondre URL et IP.

**Exemple WatYouFace.** api.watyouface.com résolu vers service.

### Q88 — TCP et UDP ?

**Réponse.** TCP est connecté et fiable ; UDP est non connecté avec moins de garanties.

**Explication.** Le choix dépend du protocole.

**Piège éventuel.** Dire TCP est toujours plus rapide.

**Exemple WatYouFace.** HTTP/WebSocket reposent généralement sur TCP.

### Q89 — Localhost ?

**Réponse.** Adresse de boucle locale de la machine.

**Explication.** Elle ne désigne pas un autre container ou la production.

**Piège éventuel.** Utiliser localhost pour joindre la base hôte depuis container.

**Exemple WatYouFace.** Tests locaux.

### Q90 — Reverse proxy ?

**Réponse.** Composant devant l'application qui reçoit les requêtes et les relaie.

**Explication.** Il peut gérer TLS, routage et headers.

**Piège éventuel.** Le confondre avec le backend métier.

**Exemple WatYouFace.** Plateforme devant API.

### Q91 — Firewall ?

**Réponse.** Règles réseau qui autorisent ou bloquent des flux.

**Explication.** Il complète, sans remplacer, l'auth applicative.

**Piège éventuel.** Dire qu'il gère RBAC.

**Exemple WatYouFace.** Limiter exposition des ports.

### Q92 — Monolithe ou microservices ?

**Réponse.** Monolithe : application déployée ensemble ; microservices : services indépendants.

**Explication.** Chaque choix a coûts et bénéfices.

**Piège éventuel.** Dire microservices sont toujours plus modernes.

**Exemple WatYouFace.** Backend WatYouFace est un monolithe en couches.

### Q93 — Stateless ou stateful ?

**Réponse.** Stateless ne conserve pas d'état de session serveur entre requêtes ; stateful conserve un état.

**Explication.** Les données persistent toujours en base.

**Piège éventuel.** Croire stateless signifie sans base.

**Exemple WatYouFace.** JWT aide l'API stateless.

### Q94 — Scalabilité horizontale et verticale ?

**Réponse.** Horizontale : ajouter instances ; verticale : augmenter ressources d'une instance.

**Explication.** Elles posent des contraintes différentes.

**Piège éventuel.** Oublier stockage et rate limiter partagés.

**Exemple WatYouFace.** Rate limiter in-memory limite horizontal.

### Q95 — Cache ?

**Réponse.** Stockage temporaire de données pour accélérer lecture.

**Explication.** Il exige stratégie d'invalidation.

**Piège éventuel.** Mettre en cache une autorisation sans précaution.

**Exemple WatYouFace.** Possible évolution futures listes.

### Q96 — Broker STOMP ?

**Réponse.** Composant qui route les messages entre destinations et abonnés.

**Explication.** Il organise les échanges Chat.

**Piège éventuel.** Dire qu'il remplace toutes autorisations.

**Exemple WatYouFace.** WebSocket/STOMP Chat.

### Q97 — Subscription STOMP ?

**Réponse.** Inscription d'un client à une destination de messages.

**Explication.** Elle doit respecter les droits d'accès.

**Piège éventuel.** Croire qu'une destination est publique par défaut.

**Exemple WatYouFace.** Conversation autorisée.

### Q98 — Migration additive ?

**Réponse.** Évolution qui ajoute ou complète sans supprimer brutalement des données.

**Explication.** Elle facilite passage sur base non vide.

**Piège éventuel.** Dire qu'elle est automatiquement sans risque.

**Exemple WatYouFace.** V2/V3 listings validées sur 37 lignes.

### Q99 — Baseline Flyway ?

**Réponse.** Point de départ versionné représentant le schéma initial connu.

**Explication.** Il permet à Flyway de suivre les versions.

**Piège éventuel.** Le confondre avec backup.

**Exemple WatYouFace.** V1 baseline WatYouFace.

### Q100 — Donnée personnelle RGPD ?

**Réponse.** Information relative à une personne identifiée ou identifiable.

**Explication.** Email, username ou avatar peuvent être concernés.

**Piège éventuel.** Dire qu'un pseudo n'est jamais personnel.

**Exemple WatYouFace.** Données de compte WatYouFace.

### Q101 — Minimisation RGPD ?

**Réponse.** Ne collecter et exposer que les données nécessaires à la finalité.

**Explication.** Elle guide DTO et interface.

**Piège éventuel.** Croire qu'elle garantit conformité complète.

**Exemple WatYouFace.** Ne pas renvoyer hash password.

### Q102 — Privacy by design ?

**Réponse.** Intégrer protection des données dès conception.

**Explication.** Elle combine choix techniques et organisationnels.

**Piège éventuel.** La réduire à une bannière cookies.

**Exemple WatYouFace.** DTO, RBAC et minimisation.

### Q103 — RGAA : alt, label, focus, contraste ?

**Réponse.** Alt décrit image utile ; label nomme un champ ; focus visible aide clavier ; contraste aide lisibilité.

**Explication.** Ce sont des critères et non une certification automatique.

**Piège éventuel.** Croire aria suffit sans test.

**Exemple WatYouFace.** Corrections Phase 15.

### Q104 — RGESN : sobriété ?

**Réponse.** Réduire ressources et impacts numériques inutiles.

**Explication.** Bundle, médias, pagination et lazy loading sont des leviers.

**Piège éventuel.** Dire qu'un petit bundle prouve conformité.

**Exemple WatYouFace.** WatYouFace reste PARTIAL.

### Q105 — Agile, Scrum et Kanban ?

**Réponse.** Agile est un ensemble de principes ; Scrum un cadre d'itérations ; Kanban visualise et limite le flux.

**Explication.** Ils peuvent inspirer sans être appliqués dogmatiquement.

**Piège éventuel.** Dire Agile signifie absence de plan.

**Exemple WatYouFace.** Backlog et lots Git documentés.

### Q106 — Backlog, user story, MVP, recette et veille ?

**Réponse.** Backlog : travail priorisé ; user story : besoin utilisateur ; MVP : périmètre minimal ; recette : validation ; veille : surveillance des évolutions.

**Explication.** Chaque objet a un usage distinct.

**Piège éventuel.** Confondre prototype et recette production.

**Exemple WatYouFace.** Documents RNCP de WatYouFace.

## 4. QCM — 30 questions

> Une seule bonne réponse, sauf indication contraire.

### QCM 1

**Question.** Un JWT signé est :

A. forcément chiffré
B. vérifiable par signature
C. une base de données
D. un cookie obligatoire

**Correction.** B

**Explication.** La signature garantit intégrité et origine selon clé, pas confidentialité.

### QCM 2

**Question.** HTTP 401 signifie :

A. ressource absente
B. authentification absente/invalide
C. droit refusé après auth
D. conflit état

**Correction.** B

**Explication.** 403 correspond à une identité reconnue sans droit.

### QCM 3

**Question.** HTTP 409 est adapté à :

A. solde insuffisant selon règle métier
B. JSON mal formé
C. token absent
D. serveur indisponible

**Correction.** A

**Explication.** Le paiement entre en conflit avec l'état métier.

### QCM 4

**Question.** CORS est appliqué principalement par :

A. PostgreSQL
B. navigateur
C. Dockerfile
D. Flyway

**Correction.** B

**Explication.** Il ne bloque pas Postman ou curl.

### QCM 5

**Question.** CSRF protège surtout :

A. contre cookie envoyé automatiquement sur mutation
B. contre tout XSS
C. contre SQL injection
D. contre DNS

**Correction.** A

**Explication.** Il est pertinent avec auth cookie.

### QCM 6

**Question.** Une clé étrangère :

A. chiffre une colonne
B. référence une clé d'autre table
C. remplace RBAC
D. est toujours unique

**Correction.** B

**Explication.** Elle protège intégrité référentielle.

### QCM 7

**Question.** ACID : A signifie :

A. autorisation
B. atomicité
C. authentification
D. archivage

**Correction.** B

**Explication.** Toutes opérations réussissent ou aucune.

### QCM 8

**Question.** JPA est :

A. ORM concret uniquement
B. spécification de persistance
C. base de données
D. pipeline

**Correction.** B

**Explication.** Hibernate est l'implémentation ORM utilisée.

### QCM 9

**Question.** Flyway sert à :

A. générer React
B. versionner migrations
C. stocker JWT
D. faire CDN

**Correction.** B

**Explication.** Il suit les versions dans schema history.

### QCM 10

**Question.** Une BOLA est :

A. erreur CSS
B. autorisation objet cassée
C. sauvegarde base
D. image Docker

**Correction.** B

**Explication.** IDOR est un cas fréquent.

### QCM 11

**Question.** Le rôle Service doit surtout :

A. rendre HTML
B. porter règles et transactions
C. stocker secrets
D. compiler Vite

**Correction.** B

**Explication.** Le controller délègue la logique métier.

### QCM 12

**Question.** Le rôle Repository est surtout :

A. persistance
B. CORS
C. CSS
D. TLS

**Correction.** A

**Explication.** Il encapsule accès données JPA.

### QCM 13

**Question.** Un cookie HttpOnly :

A. est lisible par JS
B. limite lecture par JS
C. remplace CSRF
D. chiffre JWT

**Correction.** B

**Explication.** Il réduit une exposition XSS, sans tout résoudre.

### QCM 14

**Question.** SameSite=Lax :

A. remplace HTTPS
B. réduit certains envois cross-site
C. désactive CORS
D. chiffre le body

**Correction.** B

**Explication.** CSRF reste nécessaire pour mutations cookie.

### QCM 15

**Question.** Un test E2E :

A. isole une méthode
B. suit un parcours utilisateur
C. remplace intégration
D. est toujours production

**Correction.** B

**Explication.** Il peut être local ou production selon stratégie.

### QCM 16

**Question.** CI signifie :

A. déploiement automatique garanti
B. intégration continue tests/build
C. chiffrement
D. cache image

**Correction.** B

**Explication.** CD est distinct.

### QCM 17

**Question.** Dans WatYouFace, le backend Railway :

A. CD automatique prouvé
B. deploy manuel contrôlé
C. pas de CI
D. frontend statique

**Correction.** B

**Explication.** CI Maven/Docker est automatisée.

### QCM 18

**Question.** Dans WatYouFace, frontend Hostinger :

A. Git natif source Vite
B. Actions build puis rsync
C. Docker Railway
D. sans tests

**Correction.** B

**Explication.** Tests, build, rsync et smoke sont prouvés.

### QCM 19

**Question.** Un volume Docker sert à :

A. augmenter CPU
B. persister données hors cycle container
C. signer JWT
D. compiler Java

**Correction.** B

**Explication.** Media Railway est persistant.

### QCM 20

**Question.** Un utilisateur non-root :

A. augmente privilèges
B. réduit impact compromission
C. remplace firewall
D. désactive volume

**Correction.** B

**Explication.** Java Railway tourne sous spring.

### QCM 21

**Question.** Formats Feed/Marketplace supportés :

A. WebP AVIF HEIC
B. JPEG PNG GIF BMP
C. PDF DOCX
D. tous image/*

**Correction.** B

**Explication.** WebP/AVIF/HEIC sont refusés volontairement.

### QCM 22

**Question.** Upload robuste :

A. extension seule
B. MIME et décodage réel
C. nom client direct
D. aucun nettoyage

**Correction.** B

**Explication.** Temporaire et nettoyage évitent résidus.

### QCM 23

**Question.** Listing engagé modéré :

A. suppression toujours
B. hidden=true
C. transfert ADMIN
D. changement prix

**Correction.** B

**Explication.** Préserve historique transactionnel.

### QCM 24

**Question.** RBAC ADMIN :

A. bouton caché suffit
B. contrôle backend par rôle
C. index SQL
D. cache navigateur

**Correction.** B

**Explication.** UI ne remplace jamais contrôle serveur.

### QCM 25

**Question.** PostgreSQL dédié :

A. 104/104 sans skip
B. 5/5 exécutés
C. non lancé
D. 47 tests frontend

**Correction.** B

**Explication.** Les 5 sont séparés des skipped conditionnels Maven.

### QCM 26

**Question.** RGAA complet :

A. prouvé et certifié
B. PARTIAL avec corrections ciblées
C. inutile pour web
D. assuré par React

**Correction.** B

**Explication.** Audit formel reste à faire.

### QCM 27

**Question.** Rate limiting actuel :

A. distribué Redis
B. in-memory mono-instance
C. absent de toute mesure
D. PostgreSQL

**Correction.** B

**Explication.** Évolution distribuée à prévoir en scaling.

### QCM 28

**Question.** Rollback Flyway actuel :

A. automatique prouvé
B. non automatique, forward préférable
C. inutile
D. déclenché par React

**Correction.** B

**Explication.** Backup avant migration critique reste nécessaire.

### QCM 29

**Question.** WebSocket est utile pour :

A. Chat bidirectionnel persistant
B. migration SQL
C. CSS
D. hashing password

**Correction.** A

**Explication.** STOMP organise messages et subscriptions.

### QCM 30

**Question.** BCrypt sert à :

A. hasher mots de passe
B. chiffrer le trafic TLS
C. faire CORS
D. packager Docker

**Correction.** A

**Explication.** Un hash de mot de passe n'est pas du chiffrement réversible.

## 5. Mini examen blanc — 30 minutes

**Consigne.** Répondre sans document en 30 minutes. Une phrase précise par question.

### E1

**Question.** Différence 401/403 ?

### E2

**Question.** Pourquoi CSRF avec cookie JWT ?

### E3

**Question.** Quel statut pour solde Marketplace insuffisant ?

### E4

**Question.** Quel acteur expédie ?

### E5

**Question.** Quelle limite JWT WatYouFace ?

### E6

**Question.** JPA et Hibernate ?

### E7

**Question.** Pourquoi PostgreSQL dédié ?

### E8

**Question.** Résultat tests PostgreSQL dédiés ?

### E9

**Question.** Flyway V3 ?

### E10

**Question.** Pourquoi migration base non vide ?

### E11

**Question.** Formats upload admis ?

### E12

**Question.** Pourquoi fichier temporaire upload ?

### E13

**Question.** BOLA ?

### E14

**Question.** Pourquoi ADMIN ne paie pas ?

### E15

**Question.** Pourquoi hidden=true ?

### E16

**Question.** CI backend réelle ?

### E17

**Question.** CD backend automatique ?

### E18

**Question.** CI/CD frontend ?

### E19

**Question.** Docker multi-stage ?

### E20

**Question.** Pourquoi Java non-root ?

### E21

**Question.** CORS bloque-t-il curl ?

### E22

**Question.** Hash ou chiffrement password ?

### E23

**Question.** ACID : atomicité ?

### E24

**Question.** INNER et LEFT JOIN ?

### E25

**Question.** Rate limiter actuel ?

### E26

**Question.** Backup/restore testé ?

### E27

**Question.** RGPD/RGAA complet ?

### E28

**Question.** WebSocket/STOMP ?

### E29

**Question.** Pourquoi DTO ?

### E30

**Question.** 104/104 exécutés ?

## 6. Corrigé du mini examen

### E1

**Réponse attendue.** 401 absence/invalidité auth ; 403 authentifié sans droit.

### E2

**Réponse attendue.** Le cookie est envoyé automatiquement ; token CSRF protège mutation.

### E3

**Réponse attendue.** 409 Conflict.

### E4

**Réponse attendue.** Le vendeur après PAID.

### E5

**Réponse attendue.** Pas de révocation centrale.

### E6

**Réponse attendue.** JPA spécification ; Hibernate implémentation ORM.

### E7

**Réponse attendue.** H2 peut différer du moteur de production.

### E8

**Réponse attendue.** 5/5, 0 skipped/failure/error.

### E9

**Réponse attendue.** Ajout hidden aux listings.

### E10

**Réponse attendue.** Vérifier ajout/backfill sans perte de données.

### E11

**Réponse attendue.** JPEG, PNG, GIF, BMP.

### E12

**Réponse attendue.** Éviter fichier final partiel ; nettoyer sur erreur.

### E13

**Réponse attendue.** Broken Object Level Authorization ; défaut d'autorisation objet.

### E14

**Réponse attendue.** ADMIN modère, ne contourne pas rôles transactionnels.

### E15

**Réponse attendue.** Masquer listing engagé sans casser historique.

### E16

**Réponse attendue.** Maven et build Docker, run 39165116209.

### E17

**Réponse attendue.** Non, Railway manuel contrôlé.

### E18

**Réponse attendue.** Tests/build/rsync Hostinger/smoke, run 39165084863.

### E19

**Réponse attendue.** Build lourd séparé du runtime léger.

### E20

**Réponse attendue.** Réduire impact d'une compromission.

### E21

**Réponse attendue.** Non, CORS est politique navigateur.

### E22

**Réponse attendue.** Hash BCrypt non réversible, pas chiffrement.

### E23

**Réponse attendue.** Toutes opérations réussissent ou aucune.

### E24

**Réponse attendue.** INNER correspondances ; LEFT garde toutes lignes gauche.

### E25

**Réponse attendue.** In-memory mono-instance.

### E26

**Réponse attendue.** Non, GAP.

### E27

**Réponse attendue.** Non ; PARTIAL/GAP et audit restant.

### E28

**Réponse attendue.** Connexion persistante ; STOMP structure destinations/messages.

### E29

**Réponse attendue.** Contrat API et exposition minimale de données.

### E30

**Réponse attendue.** Non : 104 rapportés et 5 PG conditionnels skipped.

## 7. Fiches express

### Codes HTTP

- 200 : succès ; 201 : ressource créée ; 204 : succès sans contenu.
- 400 : requête invalide ; 401 : identité absente/invalide ; 403 : droit insuffisant.
- 404 : ressource absente ; 409 : conflit d'état ; 415 : type média non supporté.
- 422 : validation sémantique selon contrat ; 500 : erreur interne.
- 502 : mauvaise réponse amont ; 503 : service indisponible.

### Sécurité

- Authentification : qui êtes-vous ; autorisation : avez-vous le droit.
- JWT signé en cookie HttpOnly, Secure production, SameSite=Lax.
- CSRF protège mutations par cookie ; CORS est une politique navigateur.
- RBAC, ownership et BOLA/IDOR sont contrôlés côté backend.
- BCrypt hash les mots de passe ; un hash n'est pas du chiffrement.
- Limites : révocation JWT, AV média, rate limiter distribué absents.

### SQL et base de données

- PK identifie ; FK relie ; UNIQUE évite doublon ; NOT NULL impose valeur.
- INNER JOIN garde correspondances ; LEFT JOIN garde toutes lignes gauche.
- Transaction ACID : cohérence atomique et durable.
- PostgreSQL production ; H2 n'est pas équivalent.
- Flyway versionne migrations ; Hibernate validate contrôle le schéma.

### Git

- Commit : instantané local ; push : envoi distant ; fetch : récupère références.
- Pull : fetch puis intégration ; branch : ligne de travail ; HEAD : référence courante.
- Merge intègre une branche ; rebase rejoue des commits sur une nouvelle base.
- PR centralise revue et CI ; gitignore évite artefacts/secrets locaux.

### Docker et DevOps

- Dockerfile construit image ; container exécute image ; volume persiste données.
- Multi-stage sépare build Maven et runtime JRE.
- Non-root réduit privilèges ; PORT et secrets passent par environnement.
- CI : tests/build automatisés ; CD : déploiement automatisé.
- WatYouFace : CD frontend prouvé ; backend Railway manuel contrôlé.

### Architecture

- React/Vite → REST/STOMP → Spring Security → Controllers → Services → JPA → PostgreSQL.
- Controller : HTTP ; Service : métier/sécurité/transaction ; Repository : persistance.
- DTO : contrat API, minimisation des données.
- WatYouFace : monolithe backend structuré en couches.

### Tests

- Unitaire isole ; intégration combine composants ; E2E suit parcours utilisateur.
- Maven : 104 rapportés, 0 failure/error, 5 PostgreSQL conditionnels skipped.
- PostgreSQL dédié : 5/5 réels ; frontend : 47/47 PASS.
- Build Vite PASS ; lint 0 erreur, 1 warning documenté.
- Recette E2E fonctionnelle, plusieurs captures/traces PARTIAL.

### RGPD, RGAA et RGESN

- RGPD : minimisation, finalité, sécurité, droits et rétention à organiser.
- RGAA : alt, label, focus, clavier, contraste ; audit complet non réalisé.
- RGESN : sobriété bundle, médias, pagination et lazy loading.
- WatYouFace : RGPD PARTIAL/GAP, RGAA PARTIAL, RGESN PARTIAL.
- Ne jamais déclarer conformité complète ou certifiée.

## 8. Contrôle qualité du questionnaire

- Codes 401, 403, 400, 409 et 422 distingués explicitement.
- JWT signé distingué d'un JWT chiffré ; hash distingué du chiffrement.
- CI backend et CI/CD frontend séparés ; aucun CD backend automatique déclaré.
- Exemples WatYouFace limités au périmètre prouvé.
- Aucun secret, identifiant sensible ou configuration confidentielle n'est inclus.
