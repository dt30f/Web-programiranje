package rs.raf.demo.repositories.tags;

import rs.raf.demo.repositories.MySqlAbstractRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TagRepositoryImpl extends MySqlAbstractRepository implements TagRepository {

    @Override
    public String getAllTags(int eventId) {
        String sql = "SELECT t.name " +
                "FROM tags t " +
                "JOIN event_tags et ON t.id = et.tag_id " +
                "WHERE et.event_id = ?";

        StringBuilder tags = new StringBuilder();

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (tags.length() > 0) {
                        tags.append(", "); // dodaj zarez između tagova
                    }
                    tags.append(rs.getString("name"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tags.toString();
    }
}

