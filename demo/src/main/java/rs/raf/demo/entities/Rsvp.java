package rs.raf.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rsvp {
    private Integer id;
    private String email;
    private Integer eventId;
    private LocalDateTime registeredAt;
}
