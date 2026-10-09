# Cadrage WatYouFace — RNCP 6 CDA

**Projet :** WatYouFace  
**Titre visé :** Concepteur développeur d'applications, niveau 6 (TP-01281)  
**Phase :** 1 — Cadrage  
**Date :** 07/10/2026  
**Échéance cible :** 17/10/2026

## 1. Finalité du cadrage

Ce document fixe le problème, les limites, les objectifs et les résultats attendus de la version de démonstration RNCP. Il ne constitue ni une maquette, ni une spécification détaillée, ni un engagement de production. Les fonctionnalités retenues sont issues d'une lecture du code existant et deviennent officielles seulement lorsqu'elles apparaissent dans le périmètre et le MVP de cette phase.

## 2. Constat initial

Les personnes utilisent souvent plusieurs plateformes distinctes pour créer ou maintenir du lien social, publier et interagir, discuter, exposer un produit, le rechercher, l'acheter ou négocier sa vente. Cette dispersion multiplie les comptes, profils, informations personnelles communiquées et contextes de sécurité à maîtriser. Elle ne prouve pas qu'une application unique serait automatiquement plus sûre : elle déplace et concentre aussi des responsabilités.

Le code existant met à disposition, dans une même application communautaire, un feed de publications et interactions, une messagerie, une marketplace avec portefeuille virtuel, un profil, des vidéos et une administration. Sans cadrage explicite, la démonstration restait déséquilibrée : les trois usages centraux n'étaient pas suffisamment affirmés et plusieurs exigences de sécurité, de qualité et de mise en service restent à prouver.

## 3. Problématique

### Variantes étudiées

1. Comment réduire la dispersion des usages sociaux, conversationnels et d'échange entre membres sans prétendre éliminer les risques de confidentialité ?
2. Comment centraliser un feed social, un chat et une marketplace tout en appliquant les règles d'identité, d'accès et d'intégrité côté serveur ?
3. Comment rendre démontrable, dans un temps contraint, une application communautaire sécurisée autour de ces trois piliers ?

### Formulation retenue

**Comment permettre à des membres d'une communauté de créer du lien social, communiquer et réaliser un échange marketplace dans une même application web, en centralisant l'identité et les contrôles d'accès côté serveur ?**

La variante 2 est retenue : elle relie les trois piliers à des compétences d'interface, métier, données, sécurité et tests, sans affirmer qu'une centralisation supprime les risques ni inventer un modèle commercial ou de paiement réel.

## 4. Réponse proposée et valeur

WatYouFace est une application web communautaire séparant un frontend React et une API Spring Boot. Elle centralise trois usages : le feed social, le chat et la marketplace. Autour de ces piliers, une identité, une authentification, une autorisation, une validation et des protections de données sont appliquées ou planifiées. Le portefeuille présent dans le code est traité comme **un mécanisme de démonstration interne**, non comme un moyen de paiement réel.

La valeur proposée est de réduire la dispersion des usages en regroupant publication, interaction, échange privé, négociation et achat/vente entre membres dans un environnement applicatif unique. La centralisation ne supprime pas les risques de sécurité ou de confidentialité : elle rend indispensables des contrôles d'identité, d'accès, de minimisation et de protection des données cohérents et côté serveur.

## 5. Vision produit

> Pour les membres d'une communauté qui souhaitent créer du lien social, communiquer et proposer ou acquérir un produit, WatYouFace est une application web communautaire sécurisée qui centralise un feed d'actualité, une messagerie et une marketplace. Contrairement à une juxtaposition d'outils génériques séparés, WatYouFace réunit ces usages dans un même environnement avec une identité centralisée et des contrôles d'accès appliqués côté serveur.

## 6. Décisions de cadrage

| ID | Décision | Motif | Conséquence |
|---|---|---|---|
| DEC-01 | Feed social, chat et marketplace sont les trois piliers fonctionnels | Ils répondent ensemble au problème fondateur de dispersion des usages | Le MVP, les personas, le backlog, l'architecture et les tests doivent les couvrir de façon équilibrée |
| DEC-02 | Marketplace retenue comme scénario technique démonstrateur principal, sans être le seul cœur du produit | Elle couvre des règles d'état, transaction et contrôles d'accès particulièrement riches | Les scénarios, tests et présentation y consacrent un fil rouge, sans reléguer feed ou chat |
| DEC-03 | Le crédit wallet reste une simulation réservée à ADMIN | Aucun prestataire de paiement réel n'est démontré | Ne pas présenter le wallet comme un paiement réel ou un service financier |
| DEC-04 | Visiteur, USER et ADMIN sont les rôles techniques de référence | Ce sont les rôles observés dans le backend | Acheteur/vendeur restent des rôles contextuels |
| DEC-05 | Vidéos et administration sont secondaires au MVP | Fonctionnalités existantes mais non nécessaires aux trois piliers retenus | Elles peuvent servir de preuves complémentaires, pas de fil rouge |
| DEC-06 | La production RNCP doit privilégier preuve et traçabilité | Les lacunes documentaires sont plus fortes que les lacunes de code isolées | Chaque phase doit produire tests, décisions et documents réutilisables |

## 7. Sources et limites

Sources : `RNCP6_WATYOUFACE_AUDIT_INITIAL.md`, `RNCP6_SECURITY_HARDENING_REPORT.md`, le code des deux dépôts et `docs/rncp/security-test-matrix.md`. Les besoins utilisateurs n'ont pas été collectés auprès de personnes réelles : personas, indicateurs et priorités sont des hypothèses de conception à valider lors de la phase d'analyse. Aucun modèle économique, volume d'utilisateurs ou niveau de disponibilité en production n'est affirmé.

## Traçabilité RNCP

| Compétence | Apport du document | Usage |
|---|---|---|
| C4 | périmètre, décisions, priorisation | dossier projet et entretien |
| C5 | problème, vision, proposition de valeur et parcours des trois piliers | dossier projet et slides |
| C6 | contraintes de séparation, liaison des trois domaines et limites | préparation de l'architecture |
| C2/C3 | interfaces et règles métier feed/chat/marketplace à détailler | conception et entretien |
| C7/C8 | données et accès associés aux trois domaines à modéliser | phase données |
| C9 | permissions et règles métier à tester | plan de tests futur |
| C10/C11 | objectifs de preuve et livrables futurs | planification, sans preuve de déploiement à ce stade |
