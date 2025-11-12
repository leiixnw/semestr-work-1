package repositories;

import entities.User;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class UserRepositoryImpl implements UserRepository {
    private static final String USERS_CREATE_TABLE = """
        create table if not exists users (
        id bigserial primary key,
        username varchar(255) not null,
        email varchar(255) not null,
        role varchar(255),
        password_hash varchar(512) not null,
        salt varchar(50) not null
        );
    """;

    private static final String SAVE_USER = """
            insert into users (username, email, role, password_hash, salt) values (?, ?, ?, ?, ?);
            """;

    private static final String FIND_BY_USERNAME = """
            select * from users where username = ?;
            """;

    private static final String FIND_BY_ID = """
            select * from users where id = ?;
            """;

    private static final String FIND_BY_EMAIL = """
            select * from users where email = ?;
            """;

    private static final String FIND_ALL_USERS = """
            select * from users;
            """;

    private static final String UPDATE_USER = """
            update  users set username = ?, email = ?, role = ?,
                              password_hash = ?, salt = ?
                               where id = ?;
            """;

    private static final String DELETE_USER = """
            delete from users where id = ?;
            """;

    private Properties properties;
    private String url;

    public UserRepositoryImpl () {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        properties = new Properties();
        InputStream inputStream = getClass().getResourceAsStream("/application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        url = properties.getProperty("url");
        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(USERS_CREATE_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User save(User user) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(SAVE_USER, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getRole());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getSalt());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving user", e);
        }
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Connection connection = DriverManager.getConnection(url, properties);
        PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(long id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
        PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding user by id = " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding user by email = " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, properties);
        PreparedStatement statement = connection.prepareStatement(FIND_ALL_USERS)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                users.add(toUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public void update(User user) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getRole());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getSalt());
            statement.setLong(6, user.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user with id: " + user.getId(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(DELETE_USER)) {

            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user with id: " + id, e);
        }
    }

    private User toUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setRole(resultSet.getString("role"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setSalt(resultSet.getString("salt"));
        return user;
    }
}
