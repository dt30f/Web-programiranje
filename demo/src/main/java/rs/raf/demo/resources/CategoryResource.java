package rs.raf.demo.resources;

import rs.raf.demo.entities.Category;
import rs.raf.demo.security.Secured;
import rs.raf.demo.services.CategoryService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/category")
public class CategoryResource {

    @Inject
    public CategoryService categoryService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getCategory(@QueryParam("categoryId") int categoryId) {
        return categoryService.getCategory(categoryId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Category> getAllCategoriesForClient() {
        return categoryService.getAllCategoriesForClient();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(roles = {"event_creator", "admin"})
    public Response addCategory(Category category) {
        try {
            Category created = categoryService.addCategory(category);
            return Response.ok(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(roles = {"event_creator", "admin"})
    public Response updateCategory(@PathParam("id") int id, Category category) {
        try {
            category.setId(id);
            Category updated = categoryService.updateCategory(category);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(roles = {"event_creator", "admin"})
    public Response deleteCategory(@PathParam("id") int id) {
        try {
            categoryService.deleteCategory(id);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
