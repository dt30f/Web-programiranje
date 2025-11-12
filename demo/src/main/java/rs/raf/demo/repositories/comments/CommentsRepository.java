package rs.raf.demo.repositories.comments;

import rs.raf.demo.entities.Comment;

import java.util.List;

public interface CommentsRepository {
    public void addComment(Comment comment);
    public List<Comment> getAllComments(int eventId);
    public void likeComment(int commentId);
    public void dislikeComment(int commentId);

    void deleteAllComentsForEvent(int id);
}
