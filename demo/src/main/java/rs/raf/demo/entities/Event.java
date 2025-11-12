package rs.raf.demo.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class Event {
    private Integer id;
    private String title;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDate;
    private String location;
    private int views;
    private Integer capacity;
    private int authorId;
    private int categoryId;
    private int likes;
    private int dislikes;

    public Event(Integer id, String title, String description, LocalDateTime createdAt, LocalDateTime eventDate,
                 String location, int views, Integer capacity, int authorId, int categoryId, int likes, int dislikes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.eventDate = eventDate;
        this.location = location;
        this.views = views;
        this.capacity = capacity;
        this.authorId = authorId;
        this.categoryId = categoryId;
        this.likes = likes;
        this.dislikes = dislikes;
    }


}
