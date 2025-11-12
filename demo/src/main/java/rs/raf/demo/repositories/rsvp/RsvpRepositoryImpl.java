package rs.raf.demo.repositories.rsvp;

import rs.raf.demo.entities.Rsvp;
import rs.raf.demo.repositories.MySqlAbstractRepository;

import java.sql.*;
import java.time.LocalDateTime;

public class RsvpRepositoryImpl extends MySqlAbstractRepository implements RsvpRepository {
    @Override
    public void addRsvp(Rsvp rsvp) {
        String checkSql = "SELECT COUNT(*) FROM rsvps WHERE email = ? AND event_id = ?";
        String insertSql = "INSERT INTO rsvps (email, event_id, registered_at) VALUES (?, ?, ?)";

        try (Connection connection = this.newConnection();
             PreparedStatement checkPs = connection.prepareStatement(checkSql)) {

            // Provera da li već postoji
            checkPs.setString(1, rsvp.getEmail());
            checkPs.setInt(2, rsvp.getEventId());
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("RSVP za ovaj email i događaj već postoji!");
                }
            }

            // Ako ne postoji, ubacujemo novi RSVP
            try (PreparedStatement insertPs = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertPs.setString(1, rsvp.getEmail());
                insertPs.setInt(2, rsvp.getEventId());
                insertPs.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

                insertPs.executeUpdate();

                try (ResultSet rs = insertPs.getGeneratedKeys()) {
                    if (rs.next()) {
                        rsvp.setId(rs.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error while adding RSVP: " + e.getMessage(), e);
        }
    }


    @Override
    public int getAllForRsvpId(int eventId) {
        String sql = "SELECT COUNT(*) FROM rsvps WHERE event_id = ?";
        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("RSVP count: " + count);
                    return count;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error while counting RSVPs: " + e.getMessage(), e);
        }
        return 0;
    }
}
