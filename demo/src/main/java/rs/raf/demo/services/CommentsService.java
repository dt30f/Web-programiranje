package rs.raf.demo.services;

import rs.raf.demo.entities.Comment;
import rs.raf.demo.repositories.comments.CommentsRepository;

import javax.inject.Inject;
import java.util.List;

public class CommentsService {
    @Inject
    private CommentsRepository commentsRepository;

    public List<Comment> getAllComments(int eventId) {return commentsRepository.getAllComments(eventId);}
    public void addComment(Comment comment) {
        commentsRepository.addComment(comment);
        return;
    }

    public void likeComment(int id) {commentsRepository.likeComment(id);}
    public void dislikeComment(int id) {commentsRepository.dislikeComment(id);}

}
