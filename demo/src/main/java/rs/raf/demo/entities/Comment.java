package rs.raf.demo.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    private Integer id;
    private String authorName;
    private String commentText;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer likes;
    private Integer dislikes;
    private Integer eventId;
}
