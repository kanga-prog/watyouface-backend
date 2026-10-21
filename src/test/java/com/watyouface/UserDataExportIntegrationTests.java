package com.watyouface;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.watyouface.entity.*;
import com.watyouface.entity.enums.Role;
import com.watyouface.entity.enums.TransactionStatus;
import com.watyouface.repository.*;
import com.watyouface.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "app.jwt.secret=user-export-integration-secret-with-32-bytes",
        "app.cors.allowed-origins=http://localhost:5173"
})
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class UserDataExportIntegrationTests {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository users;
    @Autowired private PostRepository posts;
    @Autowired private CommentRepository comments;
    @Autowired private LikeRepository likes;
    @Autowired private ConversationRepository conversations;
    @Autowired private ConversationUserRepository participants;
    @Autowired private MessageRepository messages;
    @Autowired private ListingRepository listings;
    @Autowired private TransactionRepository transactions;
    @Autowired private WalletRepository wallets;
    @Autowired private VideoRepository videos;
    @Autowired private VideoShareRepository videoShares;
    @Autowired private UserContractRepository userContracts;
    @Autowired private ContractRepository contracts;

    @Test
    void requiresAuthentication() throws Exception {
        mvc.perform(get("/api/users/me/export"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserGetsOnlyTheirDataWithoutCredentialsOrThirdPartyPrivateFields() throws Exception {
        User me = saveUser("export-owner", "owner-data@example.test", Role.USER);
        User other = saveUser("export-other-private-name", "other-private@example.test", Role.USER);

        Post ownPost = new Post("Mon contenu", me);
        ownPost.setImageUrl("/media/posts/own.jpg");
        ownPost = posts.save(ownPost);
        Post othersPost = posts.save(new Post("Contenu tiers", other));
        Comment ownComment = new Comment();
        ownComment.setAuthor(me);
        ownComment.setPost(othersPost);
        ownComment.setContent("Mon commentaire");
        comments.save(ownComment);
        Like ownLike = new Like();
        ownLike.setUser(me);
        ownLike.setPost(othersPost);
        likes.save(ownLike);

        Conversation conversation = conversations.save(new Conversation());
        participants.save(participant(conversation, me));
        participants.save(participant(conversation, other));
        Message sent = new Message();
        sent.setConversation(conversation);
        sent.setSender(me);
        sent.setReceiver(other);
        sent.setContent("Message envoye");
        messages.save(sent);
        Message received = new Message();
        received.setConversation(conversation);
        received.setSender(other);
        received.setReceiver(me);
        received.setContent("Message prive recu d'un tiers");
        messages.save(received);

        Listing ownListing = listing(me, null, "Mon annonce");
        Listing boughtListing = listing(other, me, "Annonce achetee");
        Transaction transaction = new Transaction();
        transaction.setAmount(12.5);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setFromUser(me);
        transaction.setToUser(other);
        transaction.setListing(boughtListing);
        transactions.save(transaction);

        Wallet wallet = new Wallet();
        wallet.setUser(me);
        wallet.setBalance(42.0);
        wallets.save(wallet);

        Video video = new Video();
        video.setTitle("Ma vidéo");
        video.setUrl("/media/videos/own.mp4");
        video.setUploader(me);
        video = videos.save(video);
        videoShares.save(new VideoShare(video, me, other));

        Contract contract = new Contract();
        contract.setTitle("Conditions démo");
        contract.setVersion("2026-1");
        contract.setContent("Contenu des conditions");
        contract.setActive(true);
        contract = contracts.save(contract);
        userContracts.save(new UserContract(me, contract, true));
        me.setAcceptedContract(true);
        me.setAcceptedContractVersion(contract);
        users.save(me);

        String json = mvc.perform(get("/api/users/me/export").header("Authorization", bearer(me)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"watyouface-data-export.json\""))
                .andReturn().getResponse().getContentAsString();
        JsonNode exported = mapper.readTree(json);

        assertThat(exported.path("formatVersion").asText()).isEqualTo("1.0");
        assertThat(exported.path("user").path("id").asLong()).isEqualTo(me.getId());
        assertThat(exported.path("user").path("email").asText()).isEqualTo(me.getEmail());
        assertThat(exported.path("posts").size()).isEqualTo(1);
        assertThat(exported.path("posts").get(0).path("id").asLong()).isEqualTo(ownPost.getId());
        assertThat(exported.path("comments").size()).isEqualTo(1);
        assertThat(exported.path("likes").size()).isEqualTo(1);
        assertThat(exported.path("messages").size()).isEqualTo(1);
        assertThat(exported.path("messages").get(0).path("content").asText()).isEqualTo("Message envoye");
        assertThat(exported.path("conversations").size()).isEqualTo(1);
        assertThat(exported.path("listings").size()).isEqualTo(2);
        assertThat(exported.path("transactions").size()).isEqualTo(1);
        assertThat(exported.path("wallet").path("balance").asDouble()).isEqualTo(42.0);
        assertThat(exported.path("videos").size()).isEqualTo(1);
        assertThat(exported.path("videoShares").size()).isEqualTo(1);
        assertThat(exported.path("contracts").size()).isEqualTo(1);
        assertThat(json).doesNotContain("password", "passwordHash", "jwt", "JWT", "secret", "other-private@example.test",
                "export-other-private-name", "Message prive recu d'un tiers", "receiverId", "senderId", "fromUserId");
        assertThat(json).contains("Mon annonce", "Annonce achetee");
    }

    @Test
    void adminCanExportOnlyTheAdminAccountAndCannotChooseAnotherUserId() throws Exception {
        User admin = saveUser("export-admin", "admin-export@example.test", Role.ADMIN);
        User privateUser = saveUser("export-private-user", "private-export@example.test", Role.USER);
        posts.save(new Post("admin data", admin));
        posts.save(new Post("private user data", privateUser));

        String json = mvc.perform(get("/api/users/me/export?userId=" + privateUser.getId())
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode exported = mapper.readTree(json);

        assertThat(exported.path("user").path("id").asLong()).isEqualTo(admin.getId());
        assertThat(exported.path("posts").size()).isEqualTo(1);
        assertThat(json).doesNotContain("private-export@example.test", "private user data");
    }

    @Test
    void anonymizedAccountCannotGetAnExport() throws Exception {
        User erased = saveUser("deleted-export-user", "deleted-export@example.test", Role.USER);
        erased.setDeletedAt(java.time.LocalDateTime.now());
        erased.setUsername("deleted-export-user+erased@example.invalid");
        users.save(erased);

        mvc.perform(get("/api/users/me/export").header("Authorization", bearer(erased)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accountWithNoRelatedDataGetsStableEmptyCollections() throws Exception {
        User user = saveUser("export-empty", "export-empty@example.test", Role.USER);
        JsonNode exported = mapper.readTree(mvc.perform(get("/api/users/me/export")
                        .header("Authorization", bearer(user)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        assertThat(exported.path("posts").isArray()).isTrue();
        assertThat(exported.path("posts").size()).isZero();
        assertThat(exported.path("messages").size()).isZero();
        assertThat(exported.path("wallet").isNull()).isTrue();
    }

    private User saveUser(String username, String email, Role role) {
        User user = new User(username, email, "$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNOPQRSTUV1234567890");
        user.setRole(role);
        return users.save(user);
    }

    private ConversationUser participant(Conversation conversation, User user) {
        ConversationUser row = new ConversationUser();
        row.setConversation(conversation);
        row.setUser(user);
        return row;
    }

    private Listing listing(User seller, User buyer, String title) {
        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription("Annonce export test");
        listing.setPrice(12.5);
        listing.setSeller(seller);
        listing.setBuyer(buyer);
        return listings.save(listing);
    }

    private String bearer(User user) {
        return "Bearer " + jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
    }
}
