package service;

import model.Post;
import repository.inmemory.PostRepoInMemory;
import repository.inmemory.CommentRepoInMemory;
import validation.PostValidator;
import validation.PostValidatorImpl;
import validation.ValidationResult;
import config.PostNotFoundException;

import java.util.List;

public class PostService {
    private static final PostService instance = new PostService();
    private final PostRepoInMemory postRepo = PostRepoInMemory.getInstance();
    private final CommentRepoInMemory commentRepo = CommentRepoInMemory.getInstance();
    private final PostValidator validator = new PostValidatorImpl();

    private PostService() {}

    public static PostService getInstance() {
        return instance;
    }

    public List<Post> getAllPosts() {
        return postRepo.list();
    }

    public Post getPostById(int id) {
        Post p = postRepo.getById(id);
        if (p == null) {
            throw new PostNotFoundException("Post with id " + id + " not found");
        }
        return p;
    }

    public Post createPost(Post post) {
        ValidationResult result = validator.validateForCreate(post);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid post: " + result.getFieldErrors());
        }
        return postRepo.create(post);
    }

    public Post updatePost(int id, Post post) {
        ValidationResult result = validator.validateForUpdate(post);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid post: " + result.getFieldErrors());
        }
        Post updated = postRepo.update(id, post);
        if (updated == null) {
            throw new PostNotFoundException("Post with id " + id + " not found");
        }
        return updated;
    }

    public void deletePost(int id) {
        boolean deleted = postRepo.delete(id, commentRepo);
        if (!deleted) {
            throw new PostNotFoundException("Post with id " + id + " not found");
        }
    }

    // Likes
    public void likePost(int id) {
        Post p = getPostById(id);
        p.addLike();
        postRepo.update(id, p);
    }

    public int getPostLikes(int id) {
        return getPostById(id).getLikes();
    }
}
