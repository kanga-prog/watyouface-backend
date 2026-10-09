# Flux WebSocket / STOMP

```mermaid
sequenceDiagram
  participant R as React + SockJS/STOMP
  participant WS as /ws + SockJS
  participant I as JwtChannelInterceptor
  participant C as ChatController/MessageService
  participant DB as PostgreSQL
  R->>WS: CONNECT Authorization: Bearer JWT
  WS->>I: preSend CONNECT
  I->>I: valide JWT, charge User, construit StompPrincipal
  R->>WS: SUBSCRIBE /topic/conversations/{id}
  WS->>I: preSend SUBSCRIBE
  I->>DB: existsByIdAndParticipants_User_Id(id,user)
  alt participant
    I-->>R: abonnement accepté
  else non participant
    I-->>R: message bloqué (aucun abonnement)
  end
  R->>C: SEND destination applicative
  C->>C: vérifier participant, valider, persister
  C->>DB: MessageRepository.save
  C-->>R: publication topic conversation
```

## Observé et exigences

- `/ws` est enregistré avec SockJS ; `/app` est préfixe applicatif et `/topic`/`/queue` utilisent le simple broker (`WebSocketConfig`).
- Le `CONNECT` exige `Bearer JWT` ; le `SUBSCRIBE` sur `/topic/conversations/{id}` vérifie l’appartenance (`JwtChannelInterceptor`).
- Les contrôles REST de lecture/envoi de message doivent rester cohérents avec cette barrière ; l’endpoint legacy `/api/messages/{conversationId}/all` est un gap P0 historique (issue #3) à traiter/vérifier.
- Le navigateur n’est jamais une autorité : le JWT et l’appartenance doivent être revalidés côté serveur.
