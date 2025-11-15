package entities;

import enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    private Long id;
    private Long tripId;
    private Long applicantId;
    private Status status;
    String message;
    private LocalDateTime appliedAt;
    private String applicantUsername;
    private String tripTitle;
    private String tripDestination;

    public String getFormattedAppliedAt() {
        return appliedAt != null ?
                appliedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "";
    }
}
