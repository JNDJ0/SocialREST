package resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import model.User;
import service.UserService;
import util.Hateoas;
import dto.LoginRequest;
import dto.RegisterRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService = UserService.getInstance();

    @GET
    public Response getAllUsers(@QueryParam("name") String name, @Context UriInfo uri) {
        List<User> users = userService.getAllUsers();

        if (name != null && !name.isBlank()) {
            users = users.stream()
                    .filter(u -> u.getName().equalsIgnoreCase(name))
                    .collect(Collectors.toList());
        }

        List<User> withLinks = users.stream()
                .map(u -> Hateoas.addUserLinks(u, uri))
                .collect(Collectors.toList());

        return Response.ok(withLinks).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") int id, @Context UriInfo uri) {
        User u = userService.getUserById(id);
        return Response.ok(Hateoas.addUserLinks(u, uri)).build();
    }

    
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(RegisterRequest req, @Context UriInfo uri) {
        User created = userService.createUser(req.getName(), req.getEmail(), req.getPassword());
        User withLinks = Hateoas.addUserLinks(created, uri);
        UriBuilder b = uri.getAbsolutePathBuilder().path(String.valueOf(created.getId()));
        return Response.created(b.build()).entity(withLinks).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") int id, User u, @Context UriInfo uri) {
        User updated = userService.updateUser(id, u);
        return Response.ok(Hateoas.addUserLinks(updated, uri)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteUser(@PathParam("id") int id) {
        userService.deleteUser(id);
        return Response.noContent().build(); // 204
    }
    
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(LoginRequest loginRequest) {
        try {
            User authenticated = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            String rawCredentials = loginRequest.getEmail() + ":" + loginRequest.getPassword();
            String basicToken = Base64.getEncoder().encodeToString(rawCredentials.getBytes(StandardCharsets.UTF_8));
            return Response.ok(authenticated)
                           .header(HttpHeaders.AUTHORIZATION, "Basic " + basicToken)
                           .build();
        } 
        catch (WebApplicationException e) {
            return Response.status(e.getResponse().getStatus())
                           .entity(e.getMessage())
                           .type(MediaType.TEXT_PLAIN)
                           .build();
        }
    }

}