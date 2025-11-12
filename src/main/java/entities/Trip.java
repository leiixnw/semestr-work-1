package entities;

import lombok.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    private Long id;
    private String title;
    private String description;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private int maxParticipants;
    private Long creatorId;
    private String status;
    private int currentParticipants;
    private String creatorUsername;

    public String getFormattedStartDate() {
        return startDate != null ? startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public String getFormattedEndDate() {
        return endDate != null ? endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
}
