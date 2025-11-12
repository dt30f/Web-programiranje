package rs.raf.demo.resources;

import rs.raf.demo.entities.dto.UserDto;
import rs.raf.demo.requests.LoginRequest;
import rs.raf.demo.security.Secured;
import rs.raf.demo.services.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/users")
public class UserResource {

    @Inject
    private UserService userService;

    @POST
    @Path("/login")
    @Produces({MediaType.APPLICATION_JSON})
    public Response login(@Valid LoginRequest loginRequest)
    {
        Map<String, String> response = new HashMap<>();

        String jwt = this.userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (jwt == null) {
            response.put("message", "These credentials do not match our records");
            return Response.status(422, "Unprocessable Entity").entity(response).build();
        }

        response.put("jwt", jwt);

        return Response.ok(response).build();
    }

    @GET
    @Path("/all")
    @Secured(roles = {"admin"})
    public Response getAllUsers() {
        List<UserDto> users = userService.findAll();
        return Response.ok(users).build();
    }

    @POST
    @Path("/add_user")
    @Secured(roles = {"admin"})
    public Response addUser(UserDto user) {
        try {
            UserDto created = userService.create(user);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Greška pri kreiranju korisnika: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Secured(roles = {"admin"})
    public Response updateUser(@PathParam("id") int id, UserDto user) {
        try {
            user.setId(id);
            UserDto updated = userService.update(user);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Greška pri izmeni korisnika: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}/toggle")
    @Secured(roles = {"admin"})
    public Response toggleUser(@PathParam("id") int id, UserDto toggleRequest) {
        try {
            UserDto updated = userService.toggleActive(id, toggleRequest.isActive());
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Greška pri promeni statusa korisnika: " + e.getMessage())
                    .build();
        }
    }
}

