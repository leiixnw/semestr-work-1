package repositories;

import entities.Trip;

import java.util.List;
import java.util.Optional;

public interface TripRepository {
    Trip save(Trip trip);
    Optional<Trip> findById(Long id);
    List<Trip> findAll();
    List<Trip> findByCreatorId(Long creatorId);
    List<Trip> findActiveTrips();
    List<Trip> findTripsWithFilters(String destination, String status);
    void update(Trip trip);
    boolean deleteById(Long id);
    int countParticipants(Long tripId);
}
