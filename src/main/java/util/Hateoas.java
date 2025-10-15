package util;

import jakarta.ws.rs.core.UriInfo;
import model.User;
import model.Post;
import model.Comment;

import java.util.*;

public class Hateoas {

    public static User addUserLinks(User u, UriInfo uri) {
        List<Map<String, String>> links = new ArrayList<>();

        links.add(link("self", uri.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(u.getId()))
                .build().toString()));

        // Enlace opcional a los posts del usuario
        links.add(link("posts", uri.getBaseUriBuilder()
                .path("posts")
                .queryParam("authorId", u.getId())
                .build().toString()));

        u.setLinks(links);
        return u;
    }

    public static Post addPostLinks(Post p, UriInfo uri) {
        List<Map<String, String>> links = new ArrayList<>();

        links.add(link("self", uri.getBaseUriBuilder()
                .path("posts")
                .path(String.valueOf(p.getId()))
                .build().toString()));

        links.add(link("comments", uri.getBaseUriBuilder()
                .path("posts")
                .path(String.valueOf(p.getId()))
                .path("comments")
                .build().toString()));

        p.setLinks(links);
        return p;
    }

    public static Comment addCommentLinks(Comment c, UriInfo uri) {
        List<Map<String, String>> links = new ArrayList<>();

        links.add(link("self", uri.getBaseUriBuilder()
                .path("posts")
                .path(String.valueOf(c.getPostId()))
                .path("comments")
                .path(String.valueOf(c.getId()))
                .build().toString()));
        
        links.add(link("author", uri.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(c.getAuthorId()))
                .build().toString()));

        links.add(link("post", uri.getBaseUriBuilder()
                .path("posts")
                .path(String.valueOf(c.getPostId()))
                .build().toString()));

        c.setLinks(links);
        return c;
    }

    private static Map<String, String> link(String rel, String href) {
        Map<String, String> map = new HashMap<>();
        map.put("rel", rel);
        map.put("href", href);
        return map;
    }
}
