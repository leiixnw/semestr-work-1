package entities;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String salt;
    private String profileInfo;
    private Integer completedTrips;
    private String role;
}