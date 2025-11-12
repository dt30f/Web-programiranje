package rs.raf.demo.repositories.events;

import rs.raf.demo.entities.Category;
import rs.raf.demo.entities.Event;
import rs.raf.demo.entities.dto.EventDto;
import rs.raf.demo.repositories.MySqlAbstractRepository;
import rs.raf.demo.repositories.comments.CommentsRepository;
import rs.raf.demo.repositories.tags.TagRepository;
import rs.raf.demo.repositories.user.UserRepository;
import rs.raf.demo.repositories.category.CategoryRepository;

import javax.inject.Inject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventRepositoryImpl extends MySqlAbstractRepository implements EventRepository {

    @Inject
    public CategoryRepository categoryRepository;

    @Inject
    public UserRepository userRepository;

    @Inject
    public TagRepository tagRepository;

    @Inject
    private CommentsRepository commentsRepository;


    @Override
    public List<EventDto> getHomeEvents(int page, int size) {
        String sql = "SELECT e.*, c.name AS category_name " +
                "FROM events e " +
                "LEFT JOIN categories c ON e.category_id = c.id " +
                "ORDER BY created_at DESC " +
                "LIMIT ? OFFSET ?";

        return getEvents(sql, page, size);
    }

    @Override
    public List<EventDto> getMostPopularEvents() {
        String sql = "SELECT e.*, c.name AS category_name " +
                "FROM events e " +
                "LEFT JOIN categories c ON e.category_id = c.id " +
                "ORDER BY views DESC LIMIT 10";
        return getEvents(sql);
    }

    @Override
    public List<EventDto> getEventsByCategory(String categoryName) {
        String sql = "SELECT e.*, c.name AS category_name " +
                "FROM events e " +
                "JOIN categories c ON e.category_id = c.id " +
                "WHERE c.name = ?";
        return getEvents(sql, categoryName);
    }

    @Override
    public EventDto getEventById(int id) {
        String sql = "SELECT e.*, c.name AS category_name " +
                "FROM events e " +
                "JOIN categories c ON e.category_id = c.id " +
                "WHERE e.id = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EventDto eventDto = mapResultSetToEventDto(rs);
                    return eventDto;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<EventDto> getEvents(String sqlQuery) {
        return getEvents(sqlQuery, null);
    }

    private List<EventDto> getEvents(String sqlQuery, String param) {
        List<EventDto> events = new ArrayList<>();

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sqlQuery)) {

            if (param != null) {
                ps.setString(1, param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventDto eventDto = mapResultSetToEventDto(rs);
                    events.add(eventDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }
    private List<EventDto> getEvents(String sqlQuery, int page, int size) {
        int offset = (page - 1) * size;
        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sqlQuery)) {
            ps.setInt(1, size);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();
            List<EventDto> events = new ArrayList<>();
            while (rs.next()) {
                events.add(mapResultSetToEventDto(rs));
            }
            return events;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Privatna metoda koja mapira jedan red ResultSet-a u EventDto
    private EventDto mapResultSetToEventDto(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdAtTs != null ? createdAtTs.toLocalDateTime() : null;

        Timestamp eventDateTs = rs.getTimestamp("event_date");
        LocalDateTime eventDate = eventDateTs != null ? eventDateTs.toLocalDateTime() : null;
        String location = rs.getString("location");
        int views = rs.getInt("views");
        int authorId = rs.getInt("author_id");
        int likes = rs.getInt("likes");
        int dislikes = rs.getInt("dislikes");
        Integer capacity = rs.getObject("capacity") != null ? rs.getInt("capacity") : null;

        String category = "unknown";
        Integer category_id = rs.getInt("category_id");

        category = categoryRepository.getCategoryRepositoryById(category_id);

        String authorName = userRepository.findById(authorId).getFirstName();
        String authorLastName = userRepository.findById(authorId).getLastName();

        String tags = tagRepository.getAllTags(id);

        return new EventDto(id, title, description, createdAt, eventDate, location, views, capacity, authorName, authorLastName, category, likes, dislikes, tags);
    }

    @Override
    public void likeEvent(int eventId) {
        String sql = "UPDATE events SET likes = likes + 1 WHERE id = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Lajkovanje eventa nije uspelo, event ne postoji.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dislikeEvent(int eventId) {
        String sql = "UPDATE events SET dislikes = dislikes + 1 WHERE id = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Lajkovanje eventa nije uspelo, event ne postoji.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void viewEvent(int eventId) {
        String sql = "UPDATE events SET views = views + 1 WHERE id = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("View eventa nije uspelo, event ne postoji.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<EventDto> getEventsByTag(String tag) {
        String sql = "SELECT e.* FROM events e " +
                "JOIN event_tags et ON e.id = et.event_id " +
                "JOIN tags t ON et.tag_id = t.id " +
                "WHERE t.name = ? " +
                "ORDER BY e.event_date ASC";

        return getEvents(sql, tag);
    }

    @Override
    public List<EventDto> getEventsByTag3(String tag) {
        String sql = "SELECT e.* FROM events e " +
                "JOIN event_tags et ON e.id = et.event_id " +
                "JOIN tags t ON et.tag_id = t.id " +
                "WHERE t.name = ? " +
                "ORDER BY e.event_date ASC LIMIT 3";

        return getEvents(sql, tag);
    }

    @Override
    public List<EventDto> getMostReactionsEvents() {
        String sql = "SELECT e.* FROM events e ORDER BY (e.likes + e.dislikes) DESC LIMIT 3";

        return getEvents(sql);
    }

    @Override
    public List<EventDto> getAllEvents() {
        return getEvents("SELECT * FROM events ORDER BY created_at DESC");
    }

    @Override
    public void deleteEvent(int id) {
        Connection connection = null;
        PreparedStatement psCheck = null;
        PreparedStatement psDelete = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();


            String sqlDelete = "DELETE FROM events WHERE id = ?";
            psDelete = connection.prepareStatement(sqlDelete);
            psDelete.setInt(1, id);
            int affectedRows = psDelete.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Brisanje events nije uspelo!");
            }
            commentsRepository.deleteAllComentsForEvent(id);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(psCheck);
            this.closeStatement(psDelete);
            this.closeConnection(connection);
        }
    }

    @Override
    public void editEvent(EventDto eventDto) {
        String sql = "UPDATE events SET title = ?, description = ?, event_date = ?, location = ?, capacity = ?, category_id = ? WHERE id = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, eventDto.getTitle());
            ps.setString(2, eventDto.getDescription());
            ps.setTimestamp(3, eventDto.getEventDate() != null ? Timestamp.valueOf(eventDto.getEventDate()) : null);
            ps.setString(4, eventDto.getLocation());
            if (eventDto.getCapacity() != null) {
                ps.setInt(5, eventDto.getCapacity());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            // Dobijanje category_id iz naziva kategorije
            Integer categoryId = categoryRepository.getCategoryIdByName(eventDto.getCategory());
            if (categoryId != null) {
                ps.setInt(6, categoryId);
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, eventDto.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Editovanje eventa nije uspelo, event ne postoji.");
            }

            // Update tagova
            //tagRepository.updateTagsForEvent(eventDto.getId(), eventDto.getTags());

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void addNewEvent(EventDto eventDto) {
        String sql = "INSERT INTO events (title, description, event_date, location, capacity, author_id, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, eventDto.getTitle());
            ps.setString(2, eventDto.getDescription());
            ps.setTimestamp(3, eventDto.getEventDate() != null ? Timestamp.valueOf(eventDto.getEventDate()) : null);
            ps.setString(4, eventDto.getLocation());
            if (eventDto.getCapacity() != null) {
                ps.setInt(5, eventDto.getCapacity());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            //int id = userRepository.findIdByName(eventDto.getAuthorName());

            ps.setInt(6, 6);

            Integer categoryId = categoryRepository.getCategoryIdByName(eventDto.getCategory());
            if (categoryId != null) {
                ps.setInt(7, 1);
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Dodavanje novog eventa nije uspelo.");
            }

            // Dohvati ID generisanog eventa
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newEventId = generatedKeys.getInt(1);
                    // Dodaj tagove
                    //tagRepository.updateTagsForEvent(newEventId, eventDto.getTags());
                } else {
                    throw new RuntimeException("Nije moguće dobiti ID novog eventa.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public int totalEvents() {
        String sql = "SELECT COUNT(*) AS total FROM events";
        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }



}
