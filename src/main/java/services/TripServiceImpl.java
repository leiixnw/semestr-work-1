package services;

import entities.Trip;
import lombok.RequiredArgsConstructor;
import repositories.TripRepository;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;

    @Override
    public Trip createTrip(Trip trip) {
        if (trip.getStartDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (trip.getMaxParticipants() <= 0) {
            throw new IllegalArgumentException("Max participants must be positive");
        }

        return tripRepository.save(trip);
    }

    @Override
    public Optional<Trip> findById(Long id) {
        return tripRepository.findById(id);
    }

    @Override
    public List<Trip> findAllTrips() {
        return tripRepository.findAll();
    }

    @Override
    public List<Trip> findTripsByCreator(Long creatorId) {
        return tripRepository.findByCreatorId(creatorId);
    }

    @Override
    public List<Trip> findActiveTrips() {
        return tripRepository.findActiveTrips();
    }

    @Override
    public List<Trip> findTripsWithFilters(String destination, String status) {
        return tripRepository.findTripsWithFilters(destination, status);
    }

    @Override
    public void updateTrip(Trip trip) {
        Optional<Trip> existingTrip = tripRepository.findById(trip.getId());
        if (existingTrip.isEmpty()) {
            throw new IllegalArgumentException("Trip not found");
        }

        int currentParticipants = getCurrentParticipantsCount(trip.getId());
        if (trip.getMaxParticipants() < currentParticipants) {
            throw new IllegalArgumentException("Cannot set max participants below current participants count");
        }

        tripRepository.update(trip);
    }

    @Override
    public boolean deleteTrip(Long tripId, Long userId) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        if (trip.isPresent() && trip.get().getCreatorId().equals(userId)) {
            return tripRepository.deleteById(tripId);
        }
        return false;
    }

    @Override
    public boolean isTripFull(Long tripId) {
        int currentParticipants = getCurrentParticipantsCount(tripId);
        Optional<Trip> trip = tripRepository.findById(tripId);
        return trip.map(t -> currentParticipants >= t.getMaxParticipants()).orElse(true);
    }

    @Override
    public int getCurrentParticipantsCount(Long tripId) {
        return tripRepository.countParticipants(tripId);
    }
}