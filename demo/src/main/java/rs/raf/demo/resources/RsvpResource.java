package rs.raf.demo.resources;

import rs.raf.demo.entities.Rsvp;
import rs.raf.demo.services.RsvpService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/rsvp")
public class RsvpResource {
    @Inject
    private RsvpService rsvpService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getRsvp(@PathParam("id") int id) {
        int br;
        try {
            br  = rsvpService.getRsvp(id);
        }catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).entity(br).build();
    }

    @POST
    @Path("/addRsvp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRsvp(Rsvp rsvp) {
        rsvpService.addRsvp(rsvp);
        return Response.status(Response.Status.OK).build();
    }
}
