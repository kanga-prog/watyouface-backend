# Inventaire UI futur — sans prescription de refonte

Cet inventaire décrit les responsabilités UX probables. Il ne force pas une structure de composants ni un refactoring du code actuel.

| Domaine | Composant / zone | Responsabilité | États critiques | Exigences liées |
|---|---|---|---|---|
| Global | `GlobalNavigation` | accès Feed, Chat, Marketplace, Profil, Admin conditionnel, logout | actif, mobile, non authentifié | AUTH-005, NFR-ACCESS-001 |
| Auth | `LoginForm`, `RegisterForm`, `ContractScreen` | accès, validation et retour formulaire | erreur champ, mauvais login, contrat refusé | AUTH-001 à 004 |
| Feed | `FeedPage`, `FeedState` | chargement, vide, liste et erreur | loading/empty/error | SOCIAL-001 |
| Feed | `CreatePost` | texte, média autorisé, aperçu, soumission | invalide, upload, succès | POST-001 |
| Feed | `PostCard`, `PostOwnerActions` | contenu, auteur, actions owner/admin | normal, édition, suppression confirmée, forbidden | POST-002 à 004 |
| Feed | `LikeButton`, `CommentList`, `CommentComposer` | interaction et compteur | liké, auto-like indisponible, erreur | LIKE-001, COMMENT-001 |
| Chat | `ConversationList`, `ConversationEmpty` | liste, sélection, création privée | loading, vide, actif, forbidden | CHAT-001/003 |
| Chat | `ChatWindow`, `MessageList`, `MessageInput` | historique, réception temps réel, saisie | loading, erreur, message invalide | CHAT-002 à 005 |
| Chat | `ContactSellerEntry` | entrée UX depuis annonce | disponible / « lien à concevoir » | CHAT-006 |
| Marketplace | `MarketplaceList`, `ListingCard`, `Filters` | liste, recherche, disponibilité et états | loading, empty, error | MARKET-002 |
| Marketplace | `ListingDetail`, `MarketplaceActions` | détail, actions selon rôle/état | AVAILABLE→RECEIVED, forbidden | MARKET-003 à 006 |
| Marketplace | `CreateListing`, `SellerSales` | création et suivi vendeur | validation, PENDING, ACCEPTED, PAID | MARKET-001/004/006 |
| Wallet | `WalletSummary`, `DemoCredit` | présenter solde et caractère simulé | solde insuffisant, ADMIN only | WALLET-001/002 |
| Profile | `ProfileCard`, `AvatarUpload`, `AccountDangerZone` | identité privée, avatar, suppression confirmée | édition, erreur, confirmation | PROFILE-001 à 004 |
| Admin | `AdminPanel`, `ModerationAction` | actions ADMIN limitées | forbidden, confirmation, erreur | ADMIN-001 à 003 |

## Composants transverses

`PageHeading`, `FieldError`, `StatusMessage`, `ConfirmDialog`, `EmptyState`, `LoadingState`, `ForbiddenState`, `ErrorState`, `ResponsiveShell`. Ils doivent recevoir des libellés explicites et permettre un focus logique ; ils ne sont pas créés dans cette phase.
