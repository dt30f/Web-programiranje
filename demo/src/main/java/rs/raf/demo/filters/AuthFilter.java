package rs.raf.demo.filters;

import rs.raf.demo.services.UserService;
import rs.raf.demo.security.Secured;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    UserService userService;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!isAuthRequired()) {
            return;
        }
        try {
            String token = requestContext.getHeaderString("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.replace("Bearer ", "");
            } else {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                return;
            }

            // proveri JWT token
            if (!userService.isAuthorized(token)) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                return;
            }

            // PROVERA ROLE
            String[] allowedRoles = getAllowedRoles();
            String userRole = userService.getUserRoleFromToken(token);
            boolean ok = false;
            for (String role : allowedRoles) {
                if (role.equalsIgnoreCase(userRole)) {
                    ok = true;
                    break;
                }
            }

            if (!ok) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Forbidden").build());
            }

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private String[] getAllowedRoles() {


        if (resourceInfo.getResourceMethod().isAnnotationPresent(Secured.class)) {
            return resourceInfo.getResourceMethod().getAnnotation(Secured.class).roles();
        }
        if (resourceInfo.getResourceClass().isAnnotationPresent(Secured.class)) {
            return resourceInfo.getResourceClass().getAnnotation(Secured.class).roles();
        }
        return new String[0];
    }


    private boolean isAuthRequired() {
        if (resourceInfo.getResourceMethod().isAnnotationPresent(Secured.class)) {
            return true;
        }
        if (resourceInfo.getResourceClass().isAnnotationPresent(Secured.class)) {
            return true;
        }

        return false;
    }
}
