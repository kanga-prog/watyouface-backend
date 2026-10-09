# ADR — Architecture WatYouFace

| ADR | Contexte / décision | Alternatives | Impact / RNCP |
|---|---|---|---|
| ADR-001 | adopter frontend React/Vite séparé de Spring Boot REST | monolithe serveur rendu | déploiement réparti ; C6 |
| ADR-002 | conserver architecture Controller → Service → Repository → JPA | logique en controller/client | responsabilités et tests ; C3/C6/C8 |
| ADR-003 | REST pour CRUD/transitions, DTO aux frontières | exposition directe entités | contrat API et validation ; C6/C8 |
| ADR-004 | JWT Bearer stateless + BCrypt, secret env | session serveur/cookie | scalabilité simple ; localStorage observé = risque à traiter ; C3/C6 |
| ADR-005 | STOMP/SockJS pour chat, participant vérifié serveur | polling | temps réel ciblé ; C3/C6 |
| ADR-006 | différer relation Listing–Conversation ; option B si négociation livrée | chat sans contexte permanent | voir document 55 ; C5/C6/C7 |
| ADR-007 | PostgreSQL/JPA pour données relationnelles | NoSQL exclusif | relations, transaction paiement ; C7/C8 |
| ADR-008 | paiement démo transactionnel avec verrou pessimiste | mise à jour sans verrou | double paiement réduit ; C3/C7/C8/C9 |
| ADR-009 | stockage média local provisoire, politique future de validation/accès | stockage objet | limite déploiement/sauvegarde ; C6/C10 |
| ADR-010 | React 19 + Vite pour SPA composantisée | Vue, Angular, HTML/JS | cohérent UI MVP ; C2/C6 |
| ADR-011 | Java 17 + Spring Boot pour monolithe multicouche | Node/Nest, Django/FastAPI, ASP.NET | sécurité, JPA, transactions ; C3/C6/C8 |
| ADR-012 | PostgreSQL/JPA pour modèle relationnel transactionnel | MySQL, MongoDB, JDBC/jOOQ | intégrité et relations ; C7/C8 |
| ADR-013 | REST pour CRUD/métier et WebSocket/STOMP/SockJS pour chat | GraphQL/RPC, polling/SSE | protocole adapté à chaque interaction ; C2/C3/C6 |

Chaque ADR est une décision de conception, non la preuve que tous ses impacts sont déjà implémentés.
