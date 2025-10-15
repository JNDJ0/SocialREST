package repository.inmemory;

import model.Comment;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CommentRepoInMemory {
    private static final CommentRepoInMemory instance = new CommentRepoInMemory();
    private final Map<Integer, Comment> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0);

    private CommentRepoInMemory() {}

    public static CommentRepoInMemory getInstance() {
        return instance;
    }

    public List<Comment> list() {
        return new ArrayList<>(store.values());
    }

    public List<Comment> listByPostId(int postId) {
        List<Comment> result = new ArrayList<>();
        for (Comment c : store.values()) {
            if (c.getPostId() == postId) {
                result.add(c);
            }
        }
        return result;
    }

    public Comment getById(int id) {
        return store.get(id);
    }

    public Comment create(Comment comment) {
        int id = (int) idSeq.incrementAndGet();
        comment.setId(id);
        comment.setCreatedAt(LocalDateTime.now());      
        store.put(id, comment);
        return comment;
    }

    public Comment update(int id, Comment comment) {
        if (!store.containsKey(id)) {
            return null;
        }
        comment.setId(id);
        store.put(id, comment);
        return comment;
    }

    public boolean delete(int id) {
        return store.remove(id) != null;
    }

    public void deleteByPostId(int postId) {
        store.values().removeIf(c -> c.getPostId() == postId);
    }

    public void seed(Comment... comments) {
        for (Comment c : comments) {
            create(c);
        }
    }
}
