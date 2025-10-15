package bootstrap;

import model.User;
import model.Post;

import java.util.Arrays;
import java.util.HashSet;

import model.Comment;
import repository.inmemory.UserRepoInMemory;
import repository.inmemory.PostRepoInMemory;
import repository.inmemory.CommentRepoInMemory;

import service.AuthService;
import util.PasswordUtils; 

public class TestDataLoader {

    public static void initialize() {
        UserRepoInMemory userRepo = UserRepoInMemory.getInstance();
        PostRepoInMemory postRepo = PostRepoInMemory.getInstance();
        CommentRepoInMemory commentRepo = CommentRepoInMemory.getInstance();
        AuthService authService = AuthService.getInstance();

        User alice = new User(0, "Alice", "alice@mail.com", 25, PasswordUtils.hashPassword("password1"), new HashSet<>(Arrays.asList("USER")));
        User bob   = new User(0, "Bob", "bob@mail.com", 30, PasswordUtils.hashPassword("password2"), new HashSet<>(Arrays.asList("ADMIN", "USER")));
        User admin = new User(0, "Admin", "admin@mail.com", 30, PasswordUtils.hashPassword("admin"), new HashSet<>(Arrays.asList("ADMIN", "USER"))); 
        
        alice = userRepo.create(alice);
        admin = userRepo.create(admin);
        bob   = userRepo.create(bob);   
        authService.addUser(alice);
        authService.addUser(admin);
        authService.addUser(bob);

        Post post1 = new Post(0, "Primer post de Alice", "Contenido de Alice", alice.getId());
        Post post2 = new Post(0, "Primer post de Bob", "Contenido de Bob", bob.getId());

        post1 = postRepo.create(post1);
        post2 = postRepo.create(post2);

        // Precargar comentarios
        Comment comment1 = new Comment(0, post1.getId(), bob.getId(), "Buen post, Alice!");
        Comment comment2 = new Comment(0, post2.getId(), alice.getId(), "Gracias por compartir, Bob!");

        commentRepo.create(comment1);
        commentRepo.create(comment2);
    }
}
