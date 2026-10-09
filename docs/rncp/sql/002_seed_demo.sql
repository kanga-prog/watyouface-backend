-- WatYouFace — jeu d’essai fictif pour le schéma de référence Phase 8.
-- Aucun secret ni donnée personnelle réelle. À réserver à dev/test.

INSERT INTO contract (id,title,content,version,active,created_at) VALUES
  (1,'Contrat WatYouFace démo','Texte fictif de contrat.','demo-1',TRUE,'2026-10-07 09:00:00');

INSERT INTO users (id,username,email,password,accepted_contract,avatar_url,role,contract_id) VALUES
  (1,'Alice Demo','alice@example.com','$2a$demo.hash.non.utilisable',TRUE,'/media/avatars/alice-demo.png','USER',1),
  (2,'Bruno Demo','bruno@example.com','$2a$demo.hash.non.utilisable',TRUE,'/media/avatars/bruno-demo.png','USER',1),
  (3,'Charlie Demo','charlie@example.com','$2a$demo.hash.non.utilisable',TRUE,'/media/avatars/charlie-demo.png','USER',1),
  (4,'Admin Demo','admin@example.com','$2a$demo.hash.non.utilisable',TRUE,NULL,'ADMIN',1);

INSERT INTO user_contract (id,user_id,contract_id,accepted,accepted_at) VALUES
  (1,1,1,TRUE,'2026-10-07 09:05:00'),(2,2,1,TRUE,'2026-10-07 09:05:00'),
  (3,3,1,TRUE,'2026-10-07 09:05:00'),(4,4,1,TRUE,'2026-10-07 09:05:00');

INSERT INTO wallet (id,user_id,balance) VALUES (1,1,100.00),(2,2,30.00),(3,3,0.00),(4,4,1000.00);
INSERT INTO video (id,title,url,uploaded_at,uploader_id) VALUES (1,'Vidéo démo','/media/videos/demo.mp4','2026-10-07 09:10:00',1);
INSERT INTO post (id,content,image_url,video_url,created_at,author_id) VALUES
  (1,'Bienvenue sur WatYouFace — publication fictive.',NULL,NULL,'2026-10-07 09:15:00',1),
  (2,'Je vends un vélo urbain en bon état.', '/media/images/velo-demo.jpg',NULL,'2026-10-07 09:20:00',2);
INSERT INTO comment (id,content,created_at,author_id,post_id,video_id) VALUES
  (1,'Annonce intéressante !','2026-10-07 09:22:00',1,2,NULL),
  (2,'Commentaire de vidéo démo.','2026-10-07 09:23:00',2,NULL,1);
INSERT INTO likes (id,user_id,post_id,video_id) VALUES (1,1,2,NULL),(2,2,1,NULL),(3,3,NULL,1);

INSERT INTO conversations (id,is_group,title,created_at) VALUES (1,FALSE,'Alice / Bruno','2026-10-07 09:25:00+00');
INSERT INTO conversation_users (id,conversation_id,user_id) VALUES (1,1,1),(2,1,2);
INSERT INTO messages (id,conversation_id,sender_id,receiver_id,content,sent_at,edited,deleted) VALUES
  (1,1,1,2,'Bonjour Bruno, le vélo est-il disponible ?','2026-10-07 09:26:00+00',FALSE,FALSE),
  (2,1,2,1,'Oui, il est disponible.','2026-10-07 09:27:00+00',FALSE,FALSE);

INSERT INTO listings (id,title,description,price,image,status,seller_id,buyer_id) VALUES
  (1,'Vélo urbain démo','Annonce PENDING fictive.',30.00,'/media/images/velo-demo.jpg','PENDING',2,1),
  (2,'Livre illustré démo','Annonce reçue fictive.',15.00,'/media/images/livre-demo.jpg','RECEIVED',2,3);
INSERT INTO transaction (id,amount,status,from_user_id,to_user_id,listing_id,created_at) VALUES
  (1,15.00,'COMPLETED',3,2,2,'2026-10-07 09:30:00');
INSERT INTO video_share (id,video_id,sender_id,receiver_id,seen) VALUES (1,1,1,3,FALSE);

-- En cas d’exécution manuelle avec IDs explicites, réaligner les séquences/identities avant les prochains inserts.
