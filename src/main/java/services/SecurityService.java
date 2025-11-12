package services;

import entities.User;

import java.util.Optional;

public interface SecurityService {
    String registerUser(String email, String username, String password, String passwordRepeat);
    String loginUser(String email, String password);
    Optional<User> getUser(String sessionId);
}
