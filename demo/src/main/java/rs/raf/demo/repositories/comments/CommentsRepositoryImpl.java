package rs.raf.demo.repositories.comments;

import rs.raf.demo.entities.Comment;
import rs.raf.demo.repositories.MySqlAbstractRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentsRepositoryImpl extends MySqlAbstractRepository implements CommentsRepository {

    @Override
    public void addComment(Comment comment) {
        String sql = "INSERT INTO comments (author_name, comment_text, created_at, likes, dislikes, event_id) VALUES (?, ?, ?, 0, 0, ?)";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            System.out.println(comment.getAuthorName() + " " + comment.getCommentText());
            ps.setString(1, comment.getAuthorName());
            ps.setString(2, comment.getCommentText());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(4, comment.getEventId());


            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating comment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Comment> getAllComments(int eventId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE event_id = ? ORDER BY created_at DESC";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getInt("id"));
                    comment.setEventId(rs.getInt("event_id"));
                    comment.setAuthorName(rs.getString("author_name"));
                    comment.setCommentText(rs.getString("comment_text"));
                    comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    comment.setLikes(rs.getInt("likes"));
                    comment.setDislikes(rs.getInt("dislikes"));
                    comments.add(comment);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return comments;
    }

    @Override
    public void likeComment(int commentId) {
        String sql = "UPDATE comments SET likes = likes + 1 WHERE id = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Lajkovanje komentara nije uspelo, komentar ne postoji.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dislikeComment(int commentId) {
        String sql = "UPDATE comments SET dislikes = dislikes + 1 WHERE id = ?";

        try (Connection connection = this.newConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Lajkovanje komentara nije uspelo, komentar ne postoji.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAllComentsForEvent(int id) {
        String sql = "DELETE FROM comments WHERE event_id = ?";

        try (Connection connection = this.newConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
