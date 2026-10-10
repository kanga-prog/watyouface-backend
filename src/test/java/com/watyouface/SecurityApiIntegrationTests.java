package com.watyouface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.watyouface.entity.Contract;
import com.watyouface.entity.Conversation;
import com.watyouface.entity.ConversationUser;
import com.watyouface.entity.Listing;
import com.watyouface.entity.Message;
import com.watyouface.entity.Transaction;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.Role;
import com.watyouface.entity.enums.TransactionStatus;
import com.watyouface.repository.ContractRepository;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.LikeRepository;
import com.watyouface.repository.MessageRepository;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.TransactionRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.UserContractRepository;
import com.watyouface.repository.WalletRepository;
import com.watyouface.security.JwtUtil;
import com.watyouface.security.LoginRateLimiter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockPart;

import java.util.Map;
import java.util.Set;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=integration-test-secret-that-is-at-least-32-bytes",
        "app.cors.allowed-origins=http://localhost:5173"
})
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SecurityApiIntegrationTests {

    private static final String TEST_JWT_SECRET = "integration-test-secret-that-is-at-least-32-bytes";
    private static final String RATE_LIMIT_TEST_IP = "198.51.100.45";

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private UserContractRepository userContractRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private ContractRepository contractRepository;
    @Autowired private ConversationRepository conversationRepository;
    @Autowired private ListingRepository listingRepository;
    @Autowired private MessageRepository messageRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private LikeRepository likeRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private LoginRateLimiter loginRateLimiter;

    @BeforeEach
    void cleanUsers() {
        loginRateLimiter.clear(RATE_LIMIT_TEST_IP);
        likeRepository.deleteAll();
        postRepository.deleteAll();
        transactionRepository.deleteAll();
        messageRepository.deleteAll();
        listingRepository.deleteAll();
        conversationRepository.deleteAll();
        userContractRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void loginAllowsAttemptsBelowFailureThreshold() throws Exception {
        for (int attempt = 0; attempt < 5; attempt++) {
            mvc.perform(loginRequest("password-not-echoed"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void loginReturns429AfterFailureThreshold() throws Exception {
        for (int attempt = 0; attempt < 5; attempt++) {
            mvc.perform(loginRequest("password-not-echoed"))
                    .andExpect(status().isUnauthorized());
        }

        mvc.perform(loginRequest("password-not-echoed"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"));
    }

    @Test
    void rateLimitResponseDoesNotExposeCredentialsOrToken() throws Exception {
        for (int attempt = 0; attempt < 5; attempt++) {
            mvc.perform(loginRequest("SENSITIVE-TEST-PASSWORD"));
        }

        mvc.perform(loginRequest("SENSITIVE-TEST-PASSWORD"))
                .andExpect(status().isTooManyRequests())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("Trop de tentatives");
                    assertThat(body).doesNotContain("SENSITIVE-TEST-PASSWORD");
                    assertThat(body).doesNotContain("token");
                });
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder loginRequest(String password) {
        return post("/api/auth/login")
                .with(csrf())
                .with(request -> {
                    request.setRemoteAddr(RATE_LIMIT_TEST_IP);
                    return request;
                })
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"unknown@example.test\",\"password\":\"" + password + "\"}");
    }

    @Test
    void protectedEndpointWithoutTokenIsUnauthorized() throws Exception {
        mvc.perform(get("/api/wallet/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidAndExpiredJwtAreRejected() throws Exception {
        mvc.perform(get("/api/wallet/me").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());

        Date now = new Date();
        String expired = Jwts.builder().setSubject("123").setIssuedAt(new Date(now.getTime() - 120_000))
                .setExpiration(new Date(now.getTime() - 60_000))
                .signWith(Keys.hmacShaKeyFor(TEST_JWT_SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
        mvc.perform(get("/api/wallet/me").header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void revokedAdminRoleInDatabaseOverridesTheStillValidTokenRole() throws Exception {
        User formerAdmin = saveUser("demoted-admin", Role.ADMIN);
        String oldAdminToken = bearer(formerAdmin);

        formerAdmin.setRole(Role.USER);
        userRepository.saveAndFlush(formerAdmin);

        mvc.perform(get("/api/admin/users").header("Authorization", oldAdminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void securityHeadersAndCorsPolicyAreApplied() throws Exception {
        mvc.perform(get("/api/wallet/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"));

        mvc.perform(options("/api/wallet/me")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));

        mvc.perform(options("/api/wallet/me")
                        .header("Origin", "https://untrusted.example")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void registrationThenLoginAcceptsValidCredentialsAndRejectsInvalidCredentials() throws Exception {
        String email = "login-" + System.nanoTime() + "@example.test";
        mvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "loginuser", "email", email,
                                "password", "SecurePassword123!", "acceptTerms", true))))
                .andExpect(status().isOk());

        mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", "SecurePassword123!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("WATYOUFACE_AUTH="),
                        org.hamcrest.Matchers.containsString("HttpOnly"),
                        org.hamcrest.Matchers.containsString("SameSite=Lax"))));

        mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", "WrongPassword123!"))))
                .andExpect(status().isUnauthorized());

        mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "missing-" + System.nanoTime() + "@example.test",
                                "password", "SecurePassword123!"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrationRejectsEachInvalidFieldWithAUsefulValidationMessage() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "ab", "email", "username-invalid@example.test",
                                "password", "SecurePassword123!", "acceptTerms", true))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("username:")));

        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "invalid-email", "email", "not-an-email",
                                "password", "SecurePassword123!", "acceptTerms", true))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("email:")));

        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "short-password", "email", "short-password@example.test",
                                "password", "short", "acceptTerms", true))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("password:")));
    }

    @Test
    void registrationDuplicatesReturnConflictForEmailAndUsername() throws Exception {
        String email = "duplicate-" + System.nanoTime() + "@example.test";
        String username = "duplicate-" + System.nanoTime();
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username, "email", email,
                                "password", "SecurePassword123!", "acceptTerms", true))))
                .andExpect(status().isOk());

        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username + "-other", "email", email,
                                "password", "SecurePassword123!", "acceptTerms", true))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Cette adresse e-mail est déjà utilisée."));

        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username, "email", "other-" + System.nanoTime() + "@example.test",
                                "password", "SecurePassword123!", "acceptTerms", true))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ce nom d'utilisateur est déjà pris."));
    }

    @Test
    void registrationRequiresCsrfAndAcceptsTheFrontendPayloadWhenTokenIsValid() throws Exception {
        String email = "csrf-register-" + System.nanoTime() + "@example.test";
        String payload = objectMapper.writeValueAsString(Map.of(
                "username", "csrf-register-" + System.nanoTime(),
                "email", email,
                "password", "SecurePassword123!",
                "acceptTerms", true));

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.needsContractAcceptance").value(false));
    }

    @Test
    void loginIsForbiddenUntilTheActiveContractIsAccepted() throws Exception {
        String email = "contract-login-" + System.nanoTime() + "@example.test";
        mvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "waitingcontract", "email", email,
                                "password", "SecurePassword123!", "acceptTerms", false))))
                .andExpect(status().isOk());

        mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", "SecurePassword123!"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Acceptation du contrat requise"));
    }

    @Test
    void nonParticipantCannotReadLegacyConversationEndpoint() throws Exception {
        User participant = saveUser("chat-participant", Role.USER);
        User outsider = saveUser("chat-outsider", Role.USER);
        Conversation conversation = new Conversation();
        ConversationUser membership = new ConversationUser();
        membership.setConversation(conversation);
        membership.setUser(participant);
        conversation.setParticipants(Set.of(membership));
        conversation = conversationRepository.save(conversation);

        mvc.perform(get("/api/messages/{id}/all", conversation.getId())
                        .header("Authorization", bearer(outsider)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/messages/conversations/{id}/messages", conversation.getId())
                        .header("Authorization", bearer(outsider)))
                .andExpect(status().isForbidden());
    }

    @Test
    void participantCanReadAndSendMessageButBlankContentIsRejected() throws Exception {
        User participant = saveUser("chat-sender", Role.USER);
        User otherParticipant = saveUser("chat-receiver", Role.USER);
        Conversation conversation = new Conversation();
        ConversationUser first = new ConversationUser();
        first.setConversation(conversation);
        first.setUser(participant);
        ConversationUser second = new ConversationUser();
        second.setConversation(conversation);
        second.setUser(otherParticipant);
        conversation.setParticipants(Set.of(first, second));
        conversation = conversationRepository.save(conversation);

        mvc.perform(post("/api/messages/conversations/{id}", conversation.getId())
                        .header("Authorization", bearer(participant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Bonjour\",\"senderId\":" + otherParticipant.getId() + "}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/messages/conversations/{id}/messages", conversation.getId())
                        .header("Authorization", bearer(participant)))
                .andExpect(status().isOk());

        mvc.perform(post("/api/messages/conversations/{id}", conversation.getId())
                        .header("Authorization", bearer(participant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void databaseRejectsMoreThanOneTransactionForAListing() {
        User seller = saveUser("unique-seller", Role.USER);
        User buyer = saveUser("unique-buyer", Role.USER);
        Listing listing = new Listing();
        listing.setTitle("Objet démo");
        listing.setPrice(12.0);
        listing.setSeller(seller);
        listing = listingRepository.saveAndFlush(listing);

        Transaction first = transaction(buyer, seller, listing);
        transactionRepository.saveAndFlush(first);

        Transaction duplicate = transaction(buyer, seller, listing);
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> transactionRepository.saveAndFlush(duplicate));
    }

    @Test
    void missingMarketplaceResourceIs404AndInvalidPaymentStateIs409() throws Exception {
        User seller = saveUser("error-seller", Role.USER);
        User buyer = saveUser("error-buyer", Role.USER);

        mvc.perform(get("/api/marketplace/listings/999999")
                        .header("Authorization", bearer(buyer)))
                .andExpect(status().isNotFound());

        Listing listing = new Listing();
        listing.setTitle("Objet démo");
        listing.setPrice(12.0);
        listing.setSeller(seller);
        listing.setBuyer(buyer);
        listing.setStatus(com.watyouface.entity.enums.ListingStatus.PENDING);
        listing = listingRepository.saveAndFlush(listing);

        mvc.perform(post("/api/marketplace/listings/{id}/pay", listing.getId())
                        .header("Authorization", bearer(buyer)))
                .andExpect(status().isConflict());
    }

    @Test
    void postApiEnforcesAuthenticatedCreationAndOwnerOnlyUpdateDelete() throws Exception {
        User owner = saveUser("post-api-owner", Role.USER);
        User outsider = saveUser("post-api-outsider", Role.USER);

        var created = mvc.perform(multipart("/api/posts")
                        .part(new MockPart("content", "Initial post".getBytes()))
                        .header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andReturn();
        long postId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(put("/api/posts/{id}", postId).header("Authorization", bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"Updated\"}"))
                .andExpect(status().isOk());
        mvc.perform(put("/api/posts/{id}", postId).header("Authorization", bearer(outsider))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"forbidden\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/posts/{id}", postId).header("Authorization", bearer(outsider)))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/posts/{id}", postId).header("Authorization", bearer(owner)))
                .andExpect(status().isNoContent());
    }

    @Test
    void likeApiRejectsSelfLikeAllowsOtherUserAndDisablesLegacyRoutes() throws Exception {
        User author = saveUser("like-api-author", Role.USER);
        User liker = saveUser("like-api-user", Role.USER);
        Post post = postRepository.saveAndFlush(new Post("Like test", author));

        mvc.perform(post("/api/likes/toggle").header("Authorization", bearer(liker))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"postId\":" + post.getId() + "}"))
                .andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertTrue(likeRepository.findByPostAndUser(post, liker).isPresent());

        mvc.perform(post("/api/likes/toggle").header("Authorization", bearer(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"postId\":" + post.getId() + "}"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/likes/post/{id}", post.getId()).header("Authorization", bearer(liker)))
                .andExpect(status().isGone());
        mvc.perform(delete("/api/likes/{id}", 999L).header("Authorization", bearer(liker)))
                .andExpect(status().isGone());
    }

    private Transaction transaction(User buyer, User seller, Listing listing) {
        Transaction tx = new Transaction();
        tx.setAmount(listing.getPrice());
        tx.setFromUser(buyer);
        tx.setToUser(seller);
        tx.setListing(listing);
        tx.setStatus(TransactionStatus.COMPLETED);
        return tx;
    }

    @Test
    void invalidRegistrationAndNegativeListingAreRejected() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"email\":\"not-an-email\",\"password\":\"short\",\"acceptTerms\":true}"))
                .andExpect(status().isBadRequest());

        User seller = saveUser("seller", Role.USER);
        mvc.perform(post("/api/marketplace/listings")
                        .header("Authorization", bearer(seller))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\",\"description\":\"Annonce\",\"price\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void walletCreditIsDeniedForUserAndAllowedForAdmin() throws Exception {
        User user = saveUser("wallet-user", Role.USER);
        User admin = saveUser("wallet-admin", Role.ADMIN);

        mvc.perform(post("/api/wallet/me/credit").header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":100}"))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/wallet/me/credit").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":100}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    void contractUsesAuthenticatedUserAndIgnoresClientSuppliedUserId() throws Exception {
        User userA = saveUser("contract-a", Role.USER);
        User userB = saveUser("contract-b", Role.USER);
        Contract contract = contractRepository.findByActiveTrue().orElseGet(() -> {
            Contract created = new Contract();
            created.setTitle("Test contract");
            created.setVersion("test");
            created.setContent("Test");
            created.setActive(true);
            return contractRepository.save(created);
        });

        mvc.perform(post("/api/contracts/accept").header("Authorization", bearer(userA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contractId\":" + contract.getId() + ",\"accepted\":true,\"userId\":" + userB.getId() + "}"))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(userA.getId()).orElseThrow().isAcceptedContract()).isTrue();
        assertThat(userRepository.findById(userB.getId()).orElseThrow().isAcceptedContract()).isFalse();
    }

    private User saveUser(String username, Role role) {
        User user = new User(username, username + "@example.test", "$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMNOPQRSTUV1234567890");
        user.setRole(role);
        return userRepository.save(user);
    }

    private String bearer(User user) {
        return "Bearer " + jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
    }
}
