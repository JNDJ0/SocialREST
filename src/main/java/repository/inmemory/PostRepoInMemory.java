package repository.inmemory;

import model.Post;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepoInMemory {
    private static final PostRepoInMemory instance = new PostRepoInMemory();
    private final Map<Integer, Post> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0);

    private PostRepoInMemory() {}

    public static PostRepoInMemory getInstance() {
        return instance;
    }

    public List<Post> list() {
        return new ArrayList<>(store.values());
    }

    public Post getById(int id) {
        return store.get(id);
    }

    public Post create(Post post) {
        int id = (int) idSeq.incrementAndGet();
        post.setId(id);
        post.setCreatedAt(LocalDateTime.now());      
        store.put(id, post);
        return post;
    }

    public Post update(int id, Post post) {
        if (!store.containsKey(id)) {
            return null;
        }
        post.setId(id);
        store.put(id, post);
        return post;
    }

    public boolean delete(int id, CommentRepoInMemory commentRepo) {
        // Política: cascade-delete de comentarios asociados
        commentRepo.deleteByPostId(id);
        return store.remove(id) != null;
    }

    public void seed(Post... posts) {
        for (Post p : posts) {
            create(p);
        }
    }
}
