package rs.raf.demo.resources;


import rs.raf.demo.entities.Event;
import rs.raf.demo.entities.dto.EventDto;
import rs.raf.demo.security.Secured;
import rs.raf.demo.services.EventsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/events")
public class EventResource {
    @Inject
    public EventsService eventsService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{page}/{size}")
    public List<EventDto> getHomeEvents(
            @PathParam("page") @DefaultValue("1") int page,
            @PathParam("size") @DefaultValue("5") int size) {
        return eventsService.getHomeEvents(page, size);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pages")
    public int totalEvents() {
        return eventsService.totalEvents();
    }


    @GET
    @Path("/popular")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EventDto> getMostPopularEvents() {return eventsService.getMostPopularEvents();}

    @GET
    @Path("/category")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EventDto> getEventsByCategory(@QueryParam("category") String category) {
        return eventsService.getEventsByCategory(category);}

    @GET
    @Path("/event")
    @Produces(MediaType.APPLICATION_JSON)
    public EventDto getEventById(@QueryParam("event_id") int eventId) {
        return eventsService.getEventById(eventId);
    }

    @POST
    @Path("/{eventId}/like")
    public void likeEvent(@PathParam("eventId") int eventId) {eventsService.likeEvent(eventId);}
    @POST
    @Path("/{eventId}/dislike")
    public void dislikeEvent(@PathParam("eventId") int eventId) {eventsService.dislikeEvent(eventId);}

    @GET
    @Path("/view_event")
    @Produces(MediaType.APPLICATION_JSON)
    public void viewEventById(@QueryParam("event_id") int eventId) {
        eventsService.viewEventById(eventId);
    }

    @GET
    @Path("/byTag")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventsByTag(@QueryParam("tag") String tag) {
        if (tag == null || tag.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Tag parametar je obavezan")
                    .build();
        }

        try {
            List<EventDto> events = eventsService.getEventsByTag(tag.trim());
            return Response.ok(events).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Greška prilikom preuzimanja eventova po tagu")
                    .build();
        }
    }

    @GET
    @Path("/byTag3")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getThreeEventsByTag(@QueryParam("tag") String tag) {
        if (tag == null || tag.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Tag parametar je obavezan")
                    .build();
        }

        try {
            List<EventDto> events = eventsService.getEventsByTag3(tag.trim());
            return Response.ok(events).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Greška prilikom preuzimanja eventova po tagu")
                    .build();
        }
    }

    @GET
    @Path("/most_reactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMostReactions() {
        List<EventDto> events = eventsService.getMostReactionsEvents();
        return Response.ok(events).build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEvents() {
        List<EventDto> events = eventsService.getAllEvents();
        return Response.ok(events).build();
    }
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(roles = {"event_creator", "admin"})
    public Response deleteEvent(@PathParam("id") int id) {
        try {
            eventsService.deleteEvent(id);
            return Response.ok("Uspesno obrisano").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(roles = {"event_creator", "admin"})
    public Response updateEvent(@PathParam("id") int id, EventDto eventDto) {
        eventsService.editEvent(eventDto);
        return Response.ok().build();
    }

    @POST
    @Path("/add_event")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(roles = {"event_creator", "admin"})
    public Response createEvent(EventDto eventDto) {
        eventsService.addNewEvent(eventDto);
        return Response.ok().build();
    }



}
