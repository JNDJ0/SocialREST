package repository.inmemory;

import model.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserRepoInMemory {
    private static final UserRepoInMemory instance = new UserRepoInMemory();
    private final Map<Integer, User> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0);

    private UserRepoInMemory() {}

    public static UserRepoInMemory getInstance() {
        return instance;
    }

    public List<User> list() {
        return new ArrayList<>(store.values());
    }

    public User getById(int id) {
        return store.get(id);
    }

    public User getByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public User create(User user) {
        int id = (int) idSeq.incrementAndGet();
        user.setId(id);
        store.put(id, user);
        return user;
    }

    public User update(int id, User user) {
        if (!store.containsKey(id)) {
            return null;
        }
        user.setId(id);
        store.put(id, user);
        return user;
    }

    public boolean delete(int id, PostRepoInMemory postRepo, CommentRepoInMemory commentRepo) {
        // Política: impedir eliminar si tiene posts o comentarios
        boolean hasPosts = postRepo.list().stream().anyMatch(p -> p.getAuthorId() == id);
        boolean hasComments = commentRepo.list().stream().anyMatch(c -> c.getAuthorId() == id);

        if (hasPosts || hasComments) {
            return false;
        }
        return store.remove(id) != null;
    }

    // Método opcional para precargar datos
    public void seed(User... users) {
        for (User u : users) {
            create(u);
        }
    }
}
