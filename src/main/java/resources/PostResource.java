package resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import model.Post;
import service.PostService;
import util.Hateoas;

import java.util.List;
import java.util.stream.Collectors;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final PostService postService = PostService.getInstance();

    @GET
    public Response getAllPosts(@QueryParam("authorId") Integer authorId, @Context UriInfo uri) {
        List<Post> posts = postService.getAllPosts();

        if (authorId != null) {
            posts = posts.stream()
                    .filter(p -> p.getAuthorId() == authorId)
                    .collect(Collectors.toList());
        }

        List<Post> withLinks = posts.stream()
                .map(p -> Hateoas.addPostLinks(p, uri))
                .collect(Collectors.toList());

        return Response.ok(withLinks).build();
    }

    @GET
    @Path("/{id}")
    public Response getPostById(@PathParam("id") int id, @Context UriInfo uri) {
        Post p = postService.getPostById(id);
        return Response.ok(Hateoas.addPostLinks(p, uri)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPost(Post p, @Context UriInfo uri) {
        Post created = postService.createPost(p);
        Post withLinks = Hateoas.addPostLinks(created, uri);
        UriBuilder b = uri.getAbsolutePathBuilder().path(String.valueOf(created.getId()));
        return Response.created(b.build()).entity(withLinks).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePost(@PathParam("id") int id, Post p, @Context UriInfo uri) {
        Post updated = postService.updatePost(id, p);
        return Response.ok(Hateoas.addPostLinks(updated, uri)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response deletePost(@PathParam("id") int id) {
        postService.deletePost(id);
        return Response.noContent().build();
    }

    // Likes
    @POST
    @Path("/{id}/likes")
    public Response likePost(@PathParam("id") int id) {
        postService.likePost(id);
        return Response.ok().entity("{\"message\":\"Post liked\"}").build();
    }

    @GET
    @Path("/{id}/likes")
    public Response getPostLikes(@PathParam("id") int id) {
        int likes = postService.getPostLikes(id);
        return Response.ok("{\"likes\":" + likes + "}").build();
    }
}
