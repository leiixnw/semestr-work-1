package services;

import entities.User;
import lombok.RequiredArgsConstructor;
import repositories.UserRepositoryImpl;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final SecurityServiceImpl securityService;
    private final UserRepositoryImpl userRepository;

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void updateUserProfile(Long userId, String username, String email) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Optional<User> user = Optional.of(userOpt.get());

        if (!user.get().getUsername().equals(username)) {
            if (userRepository.findByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.get().setUsername(username);
        }

        if (!user.get().getEmail().equals(email)) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.get().setEmail(email);
        }

        userRepository.update(user.orElse(null));
    }

    public void changeUserRole(Long userId, String newRole) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        user.setRole(newRole);
        userRepository.update(user);
    }

    public boolean deleteUser(Long userId) {
        return userRepository.deleteById(userId);
    }
}
