package service;

import model.Comment;
import repository.inmemory.CommentRepoInMemory;
import validation.CommentValidator;
import validation.CommentValidatorImpl;
import validation.ValidationResult;
import config.CommentNotFoundException;

import java.util.List;

public class CommentService {
    private static final CommentService instance = new CommentService();
    private final CommentRepoInMemory commentRepo = CommentRepoInMemory.getInstance();
    private final CommentValidator validator = new CommentValidatorImpl();

    private CommentService() {}

    public static CommentService getInstance() {
        return instance;
    }

    public List<Comment> getCommentsByPost(int postId) {
        return commentRepo.listByPostId(postId);
    }

    public Comment getCommentById(int id) {
        Comment c = commentRepo.getById(id);
        if (c == null) {
            throw new CommentNotFoundException("Comment with id " + id + " not found");
        }
        return c;
    }

    public Comment createComment(Comment comment) {
        ValidationResult result = validator.validateForCreate(comment);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid comment: " + result.getFieldErrors());
        }
        return commentRepo.create(comment);
    }

    public Comment updateComment(int id, int postId, Comment c) {
    	Comment existing = commentRepo.getById(id);
    	if (existing == null) {
    	    throw new CommentNotFoundException("Comment with id " + id + " not found");
    	}
    	if (existing.getPostId() != postId) {
    	    throw new CommentNotFoundException("Comment " + id + " does not belong to post " + postId);
    	}
        // Mantener la pertenencia
        c.setId(id);
        c.setPostId(postId);
        return commentRepo.update(id, c);
    }

    public void deleteComment(int id, int postId) {
        Comment existing = commentRepo.getById(id);
        if (existing == null) {
            throw new CommentNotFoundException("Comment with id " + id + " not found");
        }
        if (existing.getPostId() != postId) {
            throw new CommentNotFoundException("Comment " + id + " does not belong to post " + postId);
        }
        commentRepo.delete(id);
    }


    // Likes
    public void likeComment(int id) {
        Comment c = getCommentById(id);
        c.addLike();
        commentRepo.update(id, c);
    }

    public int getCommentLikes(int id) {
        return getCommentById(id).getLikes();
    }
}
