package validation;

import model.Post;
import service.UserService;

public class PostValidatorImpl implements PostValidator {

    private final UserService userService = UserService.getInstance();

    @Override
    public ValidationResult validateForCreate(Post post) {
        ValidationResult result = ValidationResult.ok();

        if (post.getTitle() == null || post.getTitle().isBlank()) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("title", "Title must not be empty");
        }

        if (post.getContent() == null || post.getContent().isBlank()) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("content", "Content must not be empty");
        }

        if (post.getAuthorId() <= 0 || userService.getUserById(post.getAuthorId()) == null) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("authorId", "Author must exist and have a valid id");
        }

        return result;
    }

    @Override
    public ValidationResult validateForUpdate(Post post) {
        ValidationResult result = validateForCreate(post);

        if (post.getId() <= 0) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("id", "Id must be greater than 0 for update");
        }

        return result;
    }
}
