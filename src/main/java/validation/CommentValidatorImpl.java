package validation;

import model.Comment;
import service.UserService;
import service.PostService;

public class CommentValidatorImpl implements CommentValidator {

    private final UserService userService = UserService.getInstance();
    private final PostService postService = PostService.getInstance();

    @Override
    public ValidationResult validateForCreate(Comment comment) {
        ValidationResult result = ValidationResult.ok();

        if (comment.getContent() == null || comment.getContent().isBlank()) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("content", "Content must not be empty");
        }

        if (comment.getAuthorId() <= 0 || userService.getUserById(comment.getAuthorId()) == null) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("authorId", "Author must exist and have a valid id");
        }

        if (comment.getPostId() <= 0 || postService.getPostById(comment.getPostId()) == null) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("postId", "Post must exist and have a valid id");
        }

        return result;
    }

    @Override
    public ValidationResult validateForUpdate(Comment comment) {
        ValidationResult result = validateForCreate(comment);

        if (comment.getId() <= 0) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("id", "Id must be greater than 0 for update");
        }

        return result;
    }
}
