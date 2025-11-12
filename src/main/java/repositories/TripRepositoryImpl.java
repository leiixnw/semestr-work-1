package repositories;

import entities.Trip;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class TripRepositoryImpl implements TripRepository {

    private static final String CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS trips (
            id BIGSERIAL PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            description TEXT,
            destination VARCHAR(255) NOT NULL,
            start_date DATE NOT NULL,
            end_date DATE NOT NULL,
            max_participants INTEGER NOT NULL,
            creator_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
            status VARCHAR(50) DEFAULT 'ACTIVE',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        """;

    private static final String SAVE_TRIP = """
        INSERT INTO trips (title, description, destination, start_date, end_date, max_participants, creator_id, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """;

    private static final String FIND_BY_ID = """
        SELECT t.*, u.username as creator_username,
               (SELECT COUNT(*) FROM applications a WHERE a.trip_id = t.id AND a.status = 'ACCEPTED') as current_participants
        FROM trips t 
        LEFT JOIN users u ON t.creator_id = u.id 
        WHERE t.id = ?;
        """;

    private static final String FIND_ALL = """
        SELECT t.*, u.username as creator_username,
               (SELECT COUNT(*) FROM applications a WHERE a.trip_id = t.id AND a.status = 'ACCEPTED') as current_participants
        FROM trips t 
        LEFT JOIN users u ON t.creator_id = u.id 
        ORDER BY t.created_at DESC;
        """;

    private static final String FIND_BY_CREATOR_ID = """
        SELECT t.*, u.username as creator_username,
               (SELECT COUNT(*) FROM applications a WHERE a.trip_id = t.id AND a.status = 'ACCEPTED') as current_participants
        FROM trips t 
        LEFT JOIN users u ON t.creator_id = u.id 
        WHERE t.creator_id = ? 
        ORDER BY t.created_at DESC;
        """;

    private static final String FIND_ACTIVE_TRIPS = """
        SELECT t.*, u.username as creator_username,
               (SELECT COUNT(*) FROM applications a WHERE a.trip_id = t.id AND a.status = 'ACCEPTED') as current_participants
        FROM trips t 
        LEFT JOIN users u ON t.creator_id = u.id 
        WHERE t.status = 'ACTIVE' AND t.start_date >= CURRENT_DATE
        ORDER BY t.created_at DESC;
        """;

    private static final String FIND_WITH_FILTERS = """
        SELECT t.*, u.username as creator_username,
               (SELECT COUNT(*) FROM applications a WHERE a.trip_id = t.id AND a.status = 'ACCEPTED') as current_participants
        FROM trips t 
        LEFT JOIN users u ON t.creator_id = u.id 
        WHERE 1=1
        """;

    private static final String UPDATE_TRIP = """
        UPDATE trips SET title = ?, description = ?, destination = ?, start_date = ?, 
                        end_date = ?, max_participants = ?, status = ? 
        WHERE id = ?;
        """;

    private static final String DELETE_TRIP = "DELETE FROM trips WHERE id = ?;";

    private static final String COUNT_PARTICIPANTS = """
        SELECT COUNT(*) FROM applications WHERE trip_id = ? AND status = 'ACCEPTED';
        """;

    private final Properties properties;
    private final String url;

    public TripRepositoryImpl() {
        this.properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/application.properties")) {
            properties.load(inputStream);
            Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialize TripRepository", e);
        }
        this.url = properties.getProperty("url");
        initializeTable();
    }

    private void initializeTable() {
        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create trips table", e);
        }
    }

    @Override
    public Trip save(Trip trip) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(SAVE_TRIP, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, trip.getTitle());
            statement.setString(2, trip.getDescription());
            statement.setString(3, trip.getDestination());
            statement.setDate(4, Date.valueOf(trip.getStartDate()));
            statement.setDate(5, Date.valueOf(trip.getEndDate()));
            statement.setInt(6, trip.getMaxParticipants());
            statement.setLong(7, trip.getCreatorId());
            statement.setString(8, trip.getStatus() != null ? trip.getStatus() : "ACTIVE");

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating trip failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    trip.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating trip failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save trip", e);
        }
        return trip;
    }

    @Override
    public Optional<Trip> findById(Long id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(toTrip(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find trip with id " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Trip> findAll() {
        List<Trip> trips = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                trips.add(toTrip(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all trips", e);
        }
        return trips;
    }

    @Override
    public List<Trip> findByCreatorId(Long creatorId) {
        List<Trip> trips = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CREATOR_ID)) {

            statement.setLong(1, creatorId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                trips.add(toTrip(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find trips by creator id " + creatorId, e);
        }
        return trips;
    }

    @Override
    public List<Trip> findActiveTrips() {
        List<Trip> trips = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(FIND_ACTIVE_TRIPS)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                trips.add(toTrip(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active trips", e);
        }
        return trips;
    }

    @Override
    public List<Trip> findTripsWithFilters(String destination, String status) {
        List<Trip> trips = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(FIND_WITH_FILTERS);
        List<Object> parameters = new ArrayList<>();

        if (destination != null && !destination.trim().isEmpty()) {
            sqlBuilder.append(" AND LOWER(t.destination) LIKE LOWER(?)");
            parameters.add("%" + destination + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            sqlBuilder.append(" AND t.status = ?");
            parameters.add(status);
        }

        sqlBuilder.append(" ORDER BY t.created_at DESC");

        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                trips.add(toTrip(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find trips with status " + status, e);
        }
        return trips;
    }

    @Override
    public void update(Trip trip) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(UPDATE_TRIP)) {

            statement.setString(1, trip.getTitle());
            statement.setString(2, trip.getDescription());
            statement.setString(3, trip.getDestination());
            statement.setDate(4, Date.valueOf(trip.getStartDate()));
            statement.setDate(5, Date.valueOf(trip.getEndDate()));
            statement.setInt(6, trip.getMaxParticipants());
            statement.setString(7, trip.getStatus());
            statement.setLong(8, trip.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update trip", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(DELETE_TRIP)) {

            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int countParticipants(Long tripId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(COUNT_PARTICIPANTS)) {

            statement.setLong(1, tripId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find trip with id " + tripId, e);
        }
        return 0;
    }

    private Trip toTrip(ResultSet resultSet) throws SQLException {
        return Trip.builder()
                .id(resultSet.getLong("id"))
                .title(resultSet.getString("title"))
                .description(resultSet.getString("description"))
                .destination(resultSet.getString("destination"))
                .startDate(resultSet.getDate("start_date").toLocalDate())
                .endDate(resultSet.getDate("end_date").toLocalDate())
                .maxParticipants(resultSet.getInt("max_participants"))
                .creatorId(resultSet.getLong("creator_id"))
                .status(resultSet.getString("status"))
                .build();
    }
}
