package resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import model.Comment;
import service.CommentService;
import util.Hateoas;

import java.util.List;
import java.util.stream.Collectors;

@Path("/posts/{postId}/comments")
@Produces(MediaType.APPLICATION_JSON)
public class CommentResource {

    private final CommentService commentService = CommentService.getInstance();

    @GET
    public Response getComments(@PathParam("postId") int postId, @Context UriInfo uri) {
        List<Comment> comments = commentService.getCommentsByPost(postId);

        List<Comment> withLinks = comments.stream()
                .map(c -> Hateoas.addCommentLinks(c, uri))
                .collect(Collectors.toList());

        return Response.ok(withLinks).build();
    }

    @GET
    @Path("/{id}")
    public Response getCommentById(@PathParam("id") int id, @Context UriInfo uri) {
        Comment c = commentService.getCommentById(id);
        return Response.ok(Hateoas.addCommentLinks(c, uri)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createComment(@PathParam("postId") int postId, Comment c, @Context UriInfo uri) {
        c.setPostId(postId);
        Comment created = commentService.createComment(c);
        Comment withLinks = Hateoas.addCommentLinks(created, uri);
        UriBuilder b = uri.getAbsolutePathBuilder().path(String.valueOf(created.getId()));
        return Response.created(b.build()).entity(withLinks).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateComment(@PathParam("postId") int postId,
                                  @PathParam("id") int id,
                                  Comment c,
                                  @Context UriInfo uri) {
        Comment updated = commentService.updateComment(id, postId, c);
        return Response.ok(Hateoas.addCommentLinks(updated, uri)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteComment(@PathParam("postId") int postId,
                                  @PathParam("id") int id) {
        commentService.deleteComment(id, postId);
        return Response.noContent().build();
    }

    // Likes
    @POST
    @Path("/{id}/likes")
    public Response likeComment(@PathParam("id") int id) {
        commentService.likeComment(id);
        return Response.ok().entity("{\"message\":\"Comment liked\"}").build();
    }

    @GET
    @Path("/{id}/likes")
    public Response getCommentLikes(@PathParam("id") int id) {
        int likes = commentService.getCommentLikes(id);
        return Response.ok("{\"likes\":" + likes + "}").build();
    }
}
