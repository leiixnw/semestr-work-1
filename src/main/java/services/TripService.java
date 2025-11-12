package services;

import entities.Trip;
import java.util.List;
import java.util.Optional;

public interface TripService {
    Trip createTrip(Trip trip);
    Optional<Trip> findById(Long id);
    List<Trip> findAllTrips();
    List<Trip> findTripsByCreator(Long creatorId);
    List<Trip> findActiveTrips();
    List<Trip> findTripsWithFilters(String destination, String status);
    void updateTrip(Trip trip);
    boolean deleteTrip(Long tripId, Long userId);
    boolean isTripFull(Long tripId);
    int getCurrentParticipantsCount(Long tripId);
}
