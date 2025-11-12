package services;

import entities.Session;
import entities.User;
import exceptions.AuthenticationException;
import repositories.SessionRepositoryImpl;
import repositories.UserRepositoryImpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class SecurityServiceImpl implements SecurityService {

    private final UserRepositoryImpl userRepository;
    private final SessionRepositoryImpl sessionRepository;
    private final Base64.Encoder base64Encoder;
    private final Duration sessionDuration;

    public SecurityServiceImpl(UserRepositoryImpl userRepository, SessionRepositoryImpl sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.base64Encoder = Base64.getEncoder();
        this.sessionDuration = Duration.ofMinutes(1);
    }

    @Override
    public String registerUser(String email, String username, String password, String passwordRepeat) {
        if (!password.equals(passwordRepeat)) {
            throw new AuthenticationException("Passwords doesnt match");
        }
        String salt = UUID.randomUUID().toString();
        String saltedPassword = password + salt;
        String passwordHash = getPasswordHash(saltedPassword);
        User user = User.builder()
                .email(email)
                .username(username)
                .passwordHash(passwordHash)
                .salt(salt)
                .build();
        Long userId = userRepository.save(user).getId();
        String sessionId = UUID.randomUUID().toString();
        sessionRepository.addSession(userId, sessionId, LocalDateTime.now().plus(sessionDuration));
        return sessionId;
    }

    @Override
    public String loginUser(String email, String password) {
        Optional<User> user;
        try {
            user = userRepository.findByEmail(email);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("Email not found");
        }
        String salt = user.get().getSalt();
        String saltedPassword = password + salt;
        String passwordHash = getPasswordHash(saltedPassword);
        if (!passwordHash.equals(user.get().getPasswordHash())) {
            throw new AuthenticationException("Invalid password");
        }
        String sessionId = UUID.randomUUID().toString();
        sessionRepository.addSession(user.get().getId(), sessionId, LocalDateTime.now().plus(sessionDuration));
        return sessionId;
    }

    @Override
    public Optional<User> getUser(String sessionId) {
        Session session;
        try {
            session = sessionRepository.getSessionById(sessionId);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("Session not found");
        }
        if (session.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("Session expired");
        }
        return userRepository.findById(session.getUserId());
    }

    private String getPasswordHash(String saltedPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] passwordHashBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return base64Encoder.encodeToString(passwordHashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}