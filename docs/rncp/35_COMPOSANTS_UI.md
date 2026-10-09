# Composants UI — spécification visuelle

| Domaine | Composant | Rôle visuel | États prioritaires |
|---|---|---|---|
| Global | `AppHeader`, `DesktopNavigation`, `MobileNavigation` | accès constant aux trois piliers, actif explicite | normal, actif, focus, admin conditionnel |
| Global | `Button`, `Input`, `TextArea`, `Modal`, `Alert` | action et formulaire cohérents | disabled, loading, error, success |
| Global | `EmptyState`, `LoadingState`, `ErrorState`, `ForbiddenState` | rendre les situations non nominales compréhensibles | empty/loading/error/forbidden |
| Global | `Avatar`, `Badge` | identité minimale et statut textuel | image absente, statut long |
| Feed | `PostCard`, `PostComposer`, `PostActions` | publication et ownership visible | auteur/non-auteur, édition, confirmation |
| Feed | `LikeButton`, `CommentButton`, `CommentItem` | interaction sociale sans ambiguïté | liked, auto-like indisponible, erreur |
| Chat | `ConversationListItem`, `ConversationHeader`, `MessageBubble`, `MessageInput`, `UnreadBadge` | liste, discussion et réception | vide, non lu, envoi, forbidden |
| Marketplace | `ListingCard`, `ListingDetail`, `ListingStatusBadge` | découverte et lecture d’état | AVAILABLE à RECEIVED, vide, erreur |
| Marketplace | `PurchaseAction`, `SellerActions`, `WalletSummary` | une action valide par rôle/état | PENDING, solde insuffisant, admin only |
| Profile | `ProfileHeader`, `ProfileForm` | données personnelles privées et édition | édition, erreur, confirmation |
| Admin | `AdminUserRow`, `AdminAction` | administration réduite et contextualisée | forbidden, confirmation, erreur |

## Principes d’assemblage

Les composants sont une cible de conception et non une demande de refonte du code actuel. Les composants d’état transverses doivent être réemployés plutôt que recréés par page. Toute autorisation réelle reste contrôlée côté serveur.

