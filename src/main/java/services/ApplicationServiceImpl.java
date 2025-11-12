package services;

import entities.Application;
import enums.Status;
import lombok.RequiredArgsConstructor;
import repositories.ApplicationRepository;
import repositories.TripRepository;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final TripRepository tripRepository;

    @Override
    public Application createApplication(Application application) {
        Optional<Application> existingApplication = applicationRepository
                .findByTripIdAndApplicantId(application.getTripId(), application.getApplicantId());

        if (existingApplication.isPresent()) {
            throw new IllegalArgumentException("You have already applied for this trip");
        }

        Optional<entities.Trip> trip = tripRepository.findById(application.getTripId());
        if (trip.isPresent() && trip.get().getCreatorId().equals(application.getApplicantId())) {
            throw new IllegalArgumentException("You cannot apply for your own trip");
        }

        if (isTripFull(application.getTripId())) {
            throw new IllegalArgumentException("This trip is already full");
        }

        application.setStatus(Status.PENDING);
        return applicationRepository.save(application);
    }

    @Override
    public Optional<Application> findById(Long id) {
        return applicationRepository.findById(id);
    }

    @Override
    public List<Application> findByApplicantId(Long applicantId) {
        return applicationRepository.findByApplicantId(applicantId);
    }

    @Override
    public List<Application> findByTripId(Long tripId) {
        return applicationRepository.findByTripId(tripId);
    }

    @Override
    public Optional<Application> findByTripIdAndApplicantId(Long tripId, Long applicantId) {
        return applicationRepository.findByTripIdAndApplicantId(tripId, applicantId);
    }

    @Override
    public void updateApplicationStatus(Long applicationId, Status status, Long userId) {
        Optional<Application> applicationOpt = applicationRepository.findById(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new IllegalArgumentException("Application not found");
        }

        Application application = applicationOpt.get();
        Optional<entities.Trip> trip = tripRepository.findById(application.getTripId());

        if (trip.isEmpty() || !trip.get().getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to update this application");
        }

        if (status == Status.ACCEPTED && isTripFull(application.getTripId())) {
            throw new IllegalArgumentException("Cannot accept application - trip is full");
        }

        applicationRepository.updateStatus(applicationId, status);
    }

    @Override
    public boolean deleteApplication(Long applicationId, Long userId) {
        Optional<Application> application = applicationRepository.findById(applicationId);
        if (application.isPresent() && application.get().getApplicantId().equals(userId)) {
            return applicationRepository.deleteById(applicationId);
        }
        return false;
    }

    @Override
    public boolean canUserApply(Long tripId, Long applicantId) {
        if (applicationRepository.findByTripIdAndApplicantId(tripId, applicantId).isPresent()) {
            return false;
        }

        Optional<entities.Trip> trip = tripRepository.findById(tripId);
        if (trip.isPresent() && trip.get().getCreatorId().equals(applicantId)) {
            return false;
        }

        return !isTripFull(tripId);
    }

    @Override
    public int countApprovedApplicationsByTripId(Long tripId) {
        return applicationRepository.countApprovedByTripId(tripId);
    }

    private boolean isTripFull(Long tripId) {
        int currentParticipants = applicationRepository.countApprovedByTripId(tripId);
        Optional<entities.Trip> trip = tripRepository.findById(tripId);
        return trip.map(t -> currentParticipants >= t.getMaxParticipants()).orElse(true);
    }
}