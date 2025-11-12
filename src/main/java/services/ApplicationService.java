package services;

import entities.Application;
import enums.Status;
import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    Application createApplication(Application application);
    Optional<Application> findById(Long id);
    List<Application> findByApplicantId(Long applicantId);
    List<Application> findByTripId(Long tripId);
    Optional<Application> findByTripIdAndApplicantId(Long tripId, Long applicantId);
    void updateApplicationStatus(Long applicationId, Status status, Long userId);
    boolean deleteApplication(Long applicationId, Long userId);
    boolean canUserApply(Long tripId, Long applicantId);
    int countApprovedApplicationsByTripId(Long tripId);
}
