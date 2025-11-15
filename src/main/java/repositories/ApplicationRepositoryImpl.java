package repositories;

import entities.Application;
import enums.Status;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ApplicationRepositoryImpl implements ApplicationRepository {

    private static final String CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS applications (
            id BIGSERIAL PRIMARY KEY,
            trip_id BIGINT NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
            applicant_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
            message TEXT,
            status VARCHAR(50) DEFAULT 'PENDING',
            applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            UNIQUE(trip_id, applicant_id)
        );
        """;

    private static final String SAVE_APPLICATION = """
        INSERT INTO applications (trip_id, applicant_id, message, status)
        VALUES (?, ?, ?, ?);
        """;

    private static final String FIND_BY_ID = """
        SELECT a.*, u.username as applicant_username, t.title as trip_title, t.destination as trip_destination
        FROM applications a
        LEFT JOIN users u ON a.applicant_id = u.id
        LEFT JOIN trips t ON a.trip_id = t.id
        WHERE a.id = ?;
        """;

    private static final String FIND_ALL = """
        SELECT a.*, u.username as applicant_username, t.title as trip_title, t.destination as trip_destination
        FROM applications a
        LEFT JOIN users u ON a.applicant_id = u.id
        LEFT JOIN trips t ON a.trip_id = t.id
        ORDER BY a.applied_at DESC;
        """;

    private static final String FIND_BY_APPLICANT_ID = """
        SELECT a.*, u.username as applicant_username, t.title as trip_title, t.destination as trip_destination
        FROM applications a
        LEFT JOIN users u ON a.applicant_id = u.id
        LEFT JOIN trips t ON a.trip_id = t.id
        WHERE a.applicant_id = ?
        ORDER BY a.applied_at DESC;
        """;

    private static final String FIND_BY_TRIP_ID = """
        SELECT a.*, u.username as applicant_username, t.title as trip_title, t.destination as trip_destination
        FROM applications a
        LEFT JOIN users u ON a.applicant_id = u.id
        LEFT JOIN trips t ON a.trip_id = t.id
        WHERE a.trip_id = ?
        ORDER BY a.applied_at DESC;
    """;

    private static final String FIND_BY_TRIP_ID_AND_APPLICANT_ID = """
        SELECT a.*, u.username as applicant_username, t.title as trip_title, t.destination as trip_destination
        FROM applications a
        LEFT JOIN users u ON a.applicant_id = u.id
        LEFT JOIN trips t ON a.trip_id = t.id
        WHERE a.trip_id = ? AND a.applicant_id = ?;
        """;

    private static final String UPDATE_STATUS = """
        UPDATE applications SET status = ? WHERE id = ?;
        """;

    private static final String DELETE_APPLICATION = "DELETE FROM applications WHERE id = ?;";

    private static final String COUNT_APPROVED_BY_TRIP_ID = """
        SELECT COUNT(*) FROM applications WHERE trip_id = ? AND status = 'ACCEPTED';
        """;

    private final Properties properties;
    private final String url;

    public ApplicationRepositoryImpl() {
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
            statement.executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Application save(Application application) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(SAVE_APPLICATION, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, application.getTripId());
            statement.setLong(2, application.getApplicantId());
            statement.setString(3, application.getMessage());
            statement.setString(4, application.getStatus().name());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating application failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    application.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating application failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save application", e);
        }
        return application;
    }

    @Override
    public Optional<Application> findById(Long id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToApplication(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find application with id " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Application> findAll() {
        List<Application> applications = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                applications.add(mapResultSetToApplication(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all applications", e);
        }
        return applications;
    }

    @Override
    public List<Application> findByApplicantId(Long applicantId) {
        List<Application> applications = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_BY_APPLICANT_ID)) {

            statement.setLong(1, applicantId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                applications.add(mapResultSetToApplication(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find applications by applicant with id " + applicantId, e);
        }
        return applications;
    }

    @Override
    public List<Application> findByTripId(Long tripId) {
        List<Application> applications = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_BY_TRIP_ID)) {

            statement.setLong(1, tripId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                applications.add(mapResultSetToApplication(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find applications by trip with id " + tripId, e);
        }
        return applications;
    }

    @Override
    public Optional<Application> findByTripIdAndApplicantId(Long tripId, Long applicantId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_BY_TRIP_ID_AND_APPLICANT_ID)) {

            statement.setLong(1, tripId);
            statement.setLong(2, applicantId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToApplication(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find applications by trip with id " + tripId, e);
        }
        return Optional.empty();
    }

    @Override
    public void updateStatus(Long applicationId, Status status) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(UPDATE_STATUS)) {

            statement.setString(1, status.name());
            statement.setLong(2, applicationId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update application status", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(DELETE_APPLICATION)) {

            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int countApprovedByTripId(Long tripId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(COUNT_APPROVED_BY_TRIP_ID)) {

            statement.setLong(1, tripId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count approved applications by trip with id " + tripId, e);
        }
        return 0;
    }

    private Application mapResultSetToApplication(ResultSet resultSet) throws SQLException {
        return Application.builder()
                .id(resultSet.getLong("id"))
                .tripId(resultSet.getLong("trip_id"))
                .applicantId(resultSet.getLong("applicant_id"))
                .status(Status.valueOf(resultSet.getString("status")))
                .appliedAt(resultSet.getTimestamp("applied_at") != null ?
                        resultSet.getTimestamp("applied_at").toLocalDateTime() : null)
                .applicantUsername(resultSet.getString("applicant_username"))
                .tripTitle(resultSet.getString("trip_title"))
                .tripDestination(resultSet.getString("trip_destination"))
                .message(resultSet.getString("message"))
                .build();
    }
}
