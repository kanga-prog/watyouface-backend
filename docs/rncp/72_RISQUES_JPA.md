# Risques JPA / Hibernate

| Relation / zone | Risque | État / piste future |
|---|---|---|
| User 1,N collections cascade | LAZY implicite, N+1 si sérialisation | DTO/projection, ne pas retourner entités |
| Post → author/comment/like | feed avec auteur/interactions peut générer N+1 | fetch join/EntityGraph/pagination après mesure |
| Conversation → participants | `Set` + cascade/orphanRemoval, liste conversation | projection DTO, requête ciblée |
| Message → conversation/sender/receiver | historique volumineux | pagination déjà exposée repository, index composite |
| Listing seller/buyer LAZY | mapping DTO appelle relations | fetch join ciblé ; éviter boucle |
| Video/Comment/Like | associations croisées et JSON | DTO + `@JsonIgnore` actuel, tests sérialisation |
| Transaction | relations User/Listing | `@JsonIgnore` évite exposition directe, DTO souhaitable |

La présence de `LAZY` ne résout pas seule le N+1. Les optimisations ne sont pas implémentées dans cette phase ; elles doivent être mesurées et couvertes par tests/pagination.

