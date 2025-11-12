package rs.raf.demo.entities.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDto {
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
    private String authorName;
    private String authorLastName;
    private String category;
    private int like;
    private int dislike;
    private String tags;

    public EventDto(Integer id, String title, String description, LocalDateTime createdAt, LocalDateTime eventDate,
                    String location, int views, Integer capacity, String authorName, String authorLastName, String category, int like, int dislike, String tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.eventDate = eventDate;
        this.location = location;
        this.views = views;
        this.capacity = capacity;
        this.authorName = authorName;
        this.category = category;
        this.authorLastName = authorLastName;
        this.like = like;
        this.dislike = dislike;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "EventDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", eventDate=" + eventDate +
                ", location='" + location + '\'' +
                ", views=" + views +
                ", capacity=" + capacity +
                ", authorName='" + authorName + '\'' +
                ", authorLastName='" + authorLastName + '\'' +
                ", category='" + category + '\'' +
                ", like=" + like +
                ", dislike=" + dislike +
                ", tags='" + tags + '\'' +
                '}';
    }
    public String toStringName(){
        return "EventDto name " + title;
    }
}
