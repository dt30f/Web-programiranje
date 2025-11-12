package rs.raf.demo.resources;

import rs.raf.demo.entities.Comment;
import rs.raf.demo.services.CommentsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentsResource {

    @Inject
    private CommentsService commentsService;

    @GET
    public List<Comment> getComments(@QueryParam("event_id") int eventId) {
        return commentsService.getAllComments(eventId);
    }

    @POST
    public Response addComment(Comment comment) {
        commentsService.addComment(comment);
        return Response.status(Response.Status.CREATED).entity(comment).build();
    }

    @POST
    @Path("/{commentId}/like")
    public void likeComment(@PathParam("commentId") int commentId) {commentsService.likeComment(commentId);}
    @POST
    @Path("/{commentId}/dislike")
    public void dislikeComment(@PathParam("commentId") int commentId) {commentsService.dislikeComment(commentId);}

   /* @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addComment(Comment comment) {
        commentsService.addComment(comment);
    }*/
}
