package com.watyouface;

import com.watyouface.entity.Comment;
import com.watyouface.entity.Conversation;
import com.watyouface.entity.ConversationUser;
import com.watyouface.entity.Like;
import com.watyouface.entity.Listing;
import com.watyouface.entity.Message;
import com.watyouface.entity.Post;
import com.watyouface.entity.Transaction;
import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import com.watyouface.entity.VideoShare;
import com.watyouface.entity.Wallet;
import com.watyouface.entity.enums.ListingStatus;
import com.watyouface.entity.enums.Role;
import com.watyouface.entity.enums.TransactionStatus;
import com.watyouface.media.MediaStorageService;
import com.watyouface.repository.CommentRepository;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.ConversationUserRepository;
import com.watyouface.repository.LikeRepository;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.MessageRepository;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.TransactionRepository;
import com.watyouface.repository.UserContractRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import com.watyouface.repository.VideoShareRepository;
import com.watyouface.repository.WalletRepository;
import com.watyouface.service.AccountErasureService;
import com.watyouface.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "app.jwt.secret=account-erasure-test-secret-at-least-32-bytes",
        "app.cors.allowed-origins=http://localhost:5173",
        "app.media.base-dir=/tmp/watyouface-account-erasure-tests"
})
@ActiveProfiles("dev")
class AccountErasureIntegrationTests {

    @Autowired private AccountErasureService erasure;
    @Autowired private UserService userService;
    @Autowired private MediaStorageService storage;
    @Autowired private UserRepository users;
    @Autowired private WalletRepository wallets;
    @Autowired private PostRepository posts;
    @Autowired private CommentRepository comments;
    @Autowired private LikeRepository likes;
    @Autowired private ConversationRepository conversations;
    @Autowired private ConversationUserRepository conversationUsers;
    @Autowired private MessageRepository messages;
    @Autowired private ListingRepository listings;
    @Autowired private TransactionRepository transactions;
    @Autowired private VideoRepository videos;
    @Autowired private VideoShareRepository videoShares;
    @Autowired private UserContractRepository userContracts;

    @BeforeEach
    void cleanBefore() throws IOException {
        cleanDatabase();
        cleanMediaRoot();
    }

    @AfterEach
    void cleanAfter() throws IOException {
        cleanDatabase();
        cleanMediaRoot();
    }

    @Test
    void erasesSimpleAccountRemovesWalletAndInvalidatesExistingSessionLookup() {
        User subject = user("simple", "simple@example.test");
        subject.setAvatarUrl("/media/avatars/simple.jpg");
        users.saveAndFlush(subject);
        wallets.saveAndFlush(wallet(subject, 42.0));

        erasure.erase(subject.getId());

        User erased = users.findById(subject.getId()).orElseThrow();
        assertThat(erased.isDeleted()).isTrue();
        assertThat(erased.getUsername()).doesNotContain("simple");
        assertThat(erased.getEmail()).endsWith("@deleted.invalid");
        assertThat(erased.getAvatarUrl()).isNull();
        assertThat(erased.isAcceptedContract()).isFalse();
        assertThat(erased.getAcceptedContractVersion()).isNull();
        assertThat(wallets.findByUser_Id(subject.getId())).isEmpty();
        assertThatThrownBy(() -> userService.loadUserById(subject.getId()))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void scrubsSocialDataPreservesOtherParticipantsAndCleansOnlyUnreferencedManagedFiles() throws Exception {
        User subject = user("social-subject", "social-subject@example.test");
        subject.setAvatarUrl("/media/avatars/subject.jpg");
        subject = users.saveAndFlush(subject);
        User survivor = users.saveAndFlush(user("survivor", "survivor@example.test"));
        wallets.saveAndFlush(wallet(subject, 9.0));

        writeMedia("avatars/subject.jpg");
        writeMedia("posts/subject.jpg");
        writeMedia("posts/shared.jpg");
        writeMedia("videos/post-subject.mp4");
        writeMedia("videos/video-subject.mp4");
        writeMedia("marketplace/listing-subject.jpg");

        Post subjectPost = new Post("personal post", subject);
        subjectPost.setImageUrl("/media/posts/subject.jpg");
        subjectPost.setVideoUrl("/media/videos/post-subject.mp4");
        subjectPost = posts.saveAndFlush(subjectPost);
        Post survivorPost = new Post("shared reference", survivor);
        survivorPost.setImageUrl("/media/posts/shared.jpg");
        survivorPost = posts.saveAndFlush(survivorPost);

        Comment subjectComment = new Comment();
        subjectComment.setAuthor(subject);
        subjectComment.setPost(survivorPost);
        subjectComment.setContent("personal comment");
        subjectComment = comments.saveAndFlush(subjectComment);
        Like subjectLike = new Like();
        subjectLike.setUser(subject);
        subjectLike.setPost(survivorPost);
        subjectLike = likes.saveAndFlush(subjectLike);

        Video video = new Video();
        video.setUploader(subject);
        video.setTitle("personal video");
        video.setUrl("/media/videos/video-subject.mp4");
        video = videos.saveAndFlush(video);
        videoShares.saveAndFlush(new VideoShare(video, subject, survivor));

        Listing available = new Listing();
        available.setSeller(subject);
        available.setTitle("personal listing");
        available.setDescription("personal description");
        available.setImage("/media/marketplace/listing-subject.jpg");
        available.setPrice(12.0);
        available.setStatus(ListingStatus.AVAILABLE);
        available = listings.saveAndFlush(available);

        Conversation conversation = conversations.saveAndFlush(new Conversation());
        conversationUsers.saveAndFlush(membership(conversation, subject));
        conversationUsers.saveAndFlush(membership(conversation, survivor));
        Message outgoing = messages.saveAndFlush(message(conversation, subject, survivor, "private outgoing"));
        Message incoming = messages.saveAndFlush(message(conversation, survivor, subject, "private incoming"));

        erasure.erase(subject.getId());

        assertThat(posts.findById(subjectPost.getId())).isPresent().get()
                .extracting(Post::getContent, Post::getImageUrl, Post::getVideoUrl)
                .containsExactly("Publication supprimée", null, null);
        assertThat(posts.findById(survivorPost.getId())).isPresent();
        assertThat(comments.findById(subjectComment.getId())).isEmpty();
        assertThat(likes.findById(subjectLike.getId())).isEmpty();
        assertThat(videos.findById(video.getId())).isPresent().get()
                .extracting(Video::getTitle, Video::getUrl)
                .containsExactly("Vidéo supprimée", null);
        assertThat(videoShares.findAll()).isEmpty();
        assertThat(listings.findById(available.getId())).isEmpty();
        assertThat(conversationUsers.findByUser_Id(subject.getId())).isEmpty();
        assertThat(conversations.findById(conversation.getId())).isPresent();
        assertThat(messages.findById(outgoing.getId())).isPresent().get()
                .extracting(Message::getContent, Message::isDeleted)
                .containsExactly("Message supprimé par son auteur", true);
        assertThat(messages.findById(incoming.getId())).isPresent().get()
                .extracting(Message::getContent, Message::isDeleted)
                .containsExactly("private incoming", false);
        assertThat(wallets.findByUser_Id(subject.getId())).isEmpty();

        assertThat(mediaExists("avatars/subject.jpg")).isFalse();
        assertThat(mediaExists("posts/subject.jpg")).isFalse();
        assertThat(mediaExists("videos/post-subject.mp4")).isFalse();
        assertThat(mediaExists("videos/video-subject.mp4")).isFalse();
        assertThat(mediaExists("marketplace/listing-subject.jpg")).isFalse();
        assertThat(mediaExists("posts/shared.jpg")).isTrue();
    }

    @Test
    void preservesCompletedMarketplaceHistoryButAnonymizesTheErasedPartyAndListingContent() {
        User buyer = users.saveAndFlush(user("buyer", "buyer@example.test"));
        User seller = users.saveAndFlush(user("seller", "seller@example.test"));
        wallets.saveAndFlush(wallet(buyer, 70.0));
        wallets.saveAndFlush(wallet(seller, 30.0));

        Listing paid = new Listing();
        paid.setSeller(seller);
        paid.setBuyer(buyer);
        paid.setTitle("seller personal listing");
        paid.setDescription("seller personal description");
        paid.setImage("/media/marketplace/paid.jpg");
        paid.setPrice(30.0);
        paid.setStatus(ListingStatus.PAID);
        paid = listings.saveAndFlush(paid);

        Transaction transaction = new Transaction();
        transaction.setFromUser(buyer);
        transaction.setToUser(seller);
        transaction.setListing(paid);
        transaction.setAmount(30.0);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction = transactions.saveAndFlush(transaction);

        erasure.erase(seller.getId());

        Listing retained = listings.findById(paid.getId()).orElseThrow();
        assertThat(retained.getStatus()).isEqualTo(ListingStatus.PAID);
        assertThat(retained.isHidden()).isTrue();
        assertThat(retained.getTitle()).isEqualTo("Annonce supprimée");
        assertThat(retained.getDescription()).isNull();
        assertThat(retained.getImage()).isNull();
        Transaction retainedTransaction = transactions.findById(transaction.getId()).orElseThrow();
        assertThat(retainedTransaction.getListing().getId()).isEqualTo(paid.getId());
        assertThat(retainedTransaction.getToUser().getId()).isEqualTo(seller.getId());
        assertThat(users.findById(seller.getId()).orElseThrow().isDeleted()).isTrue();
        assertThat(wallets.findByUser_Id(seller.getId())).isEmpty();
        assertThat(wallets.findByUser_Id(buyer.getId())).isPresent();
    }

    @Test
    void deletesConversationsThatBecomeEmptyWithoutForeignKeyViolations() {
        User subject = users.saveAndFlush(user("alone", "alone@example.test"));
        Conversation conversation = conversations.saveAndFlush(new Conversation());
        conversationUsers.saveAndFlush(membership(conversation, subject));
        Message message = messages.saveAndFlush(message(conversation, subject, subject, "only participant message"));

        erasure.erase(subject.getId());

        assertThat(conversationUsers.findByUser_Id(subject.getId())).isEmpty();
        assertThat(messages.findById(message.getId())).isEmpty();
        assertThat(conversations.findById(conversation.getId())).isEmpty();
        assertThat(users.findById(subject.getId())).isPresent().get().extracting(User::isDeleted).isEqualTo(true);
    }

    private void cleanDatabase() {
        videoShares.deleteAll();
        likes.deleteAll();
        comments.deleteAll();
        messages.deleteAll();
        conversationUsers.deleteAll();
        transactions.deleteAll();
        listings.deleteAll();
        videos.deleteAll();
        posts.deleteAll();
        userContracts.deleteAll();
        wallets.deleteAll();
        conversations.deleteAll();
        users.deleteAll();
    }

    private User user(String username, String email) {
        User user = new User(username, email, "test-password-hash");
        user.setRole(Role.USER);
        return user;
    }

    private Wallet wallet(User user, double balance) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(balance);
        return wallet;
    }

    private ConversationUser membership(Conversation conversation, User user) {
        ConversationUser membership = new ConversationUser();
        membership.setConversation(conversation);
        membership.setUser(user);
        return membership;
    }

    private Message message(Conversation conversation, User sender, User receiver, String content) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        return message;
    }

    private void writeMedia(String relative) throws IOException {
        Files.writeString(Path.of(storage.resolvePath(relative)), "test-media");
    }

    private boolean mediaExists(String relative) {
        return Files.exists(Path.of(storage.resolvePath(relative)));
    }

    private void cleanMediaRoot() throws IOException {
        Path root = Path.of(storage.getBaseDir());
        if (!Files.exists(root)) return;
        try (var paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try { Files.deleteIfExists(path); } catch (IOException ignored) { }
            });
        }
    }
}
