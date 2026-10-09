# Direction visuelle — WatYouFace

## Intention

WatYouFace est une application communautaire qui rassemble **Feed social**, **Chat** et **Marketplace**. La direction visuelle doit faire sentir une application unique, calme et fiable : contenu prioritaire, actions explicites, sécurité compréhensible. Elle ne cherche ni à imiter une banque, ni à faire du paiement réel.

## Principes

1. **Une identité, trois contextes** : même en-tête, mêmes actions et mêmes statuts ; l’onglet actif donne le contexte.
2. **Lecture avant décoration** : surfaces claires, cartes sobres, hiérarchie nette et densité modérée.
3. **Confiance par la clarté** : propriétaire, état d’une vente, erreur et action dangereuse sont exprimés en texte, pas seulement par une icône ou une couleur.
4. **Mobile structurel** : navigation basse, feed une colonne, chat liste puis conversation, marketplace en cartes pleine largeur.
5. **Sécurité visible mais non trompeuse** : le wallet est toujours libellé « Wallet de démonstration » ; le client prépare l’action, le serveur l’autorise.

## Ton, densité et hiérarchie

| Aspect | Décision |
|---|---|
| Ton | accueillant, direct, non promotionnel |
| Densité | moyenne : une action principale par zone, détails progressifs |
| Titres | titre de page, puis titres de cartes ; pas de titres décoratifs |
| Actions | primaire pour la prochaine action, secondaire/texte pour le reste, danger isolé |
| Statuts | badge avec libellé intégral (`PENDING`, `PAID`…) + description contextualisée |
| Médias | aperçu contraint, texte alternatif prévu, pas d’autoplay nécessaire |

## Cohérence des piliers

- **Feed** : carte de publication, auteur/date, interactions ; actions d’édition seulement pour le propriétaire ou ADMIN visuellement autorisé.
- **Chat** : deux types de bulles, liste lisible, état non lu et retour mobile ; l’entrée « Contacter le vendeur » reste un besoin UX, pas une relation technique supposée.
- **Marketplace** : cartes d’annonces, badge d’état, action unique adaptée au rôle et à la transition ; la progression `AVAILABLE → … → RECEIVED` est lisible.

## Accessibilité visuelle préparatoire

- contraste cible d’au moins 4,5:1 pour texte normal ;
- focus clavier fortement visible et non supprimé ;
- taille de cible minimale visée : 44 × 44 px pour les actions tactiles ;
- labels toujours visibles sur les champs ;
- erreur = titre/texte + couleur + association au champ ;
- icône seule interdite pour les actions majeures ; avatar et média prévoient une alternative textuelle.

Ces choix préparent le RGAA ; ils ne constituent pas une déclaration de conformité.

## Écoconception — principes ciblés

- pas d’effet vidéo, parallaxe ou animation décorative ;
- états et composants réutilisables pour éviter des interfaces redondantes ;
- médias affichés avec aperçu borné ; chargement progressif et formats adaptés à mettre en œuvre ultérieurement ;
- une navigation simple limite les vues et appels inutiles à terme.

## Livrables visuels

Les rendus basse/moyenne fidélité exportables sont regroupés dans [`mockups/WATYOUFACE_UI_MOCKUPS.svg`](mockups/WATYOUFACE_UI_MOCKUPS.svg). Ils restent des maquettes de conception, non du JSX/CSS applicatif.

