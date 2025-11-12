package repositories;

import entities.Application;
import enums.Status;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {
    Application save(Application application);
    Optional<Application> findById(Long id);
    List<Application> findAll();
    List<Application> findByApplicantId(Long applicantId);
    List<Application> findByTripId(Long tripId);
    Optional<Application> findByTripIdAndApplicantId(Long tripId, Long applicantId);
    void updateStatus(Long applicationId, Status status);
    boolean deleteById(Long id);
    int countApprovedByTripId(Long tripId);
}
