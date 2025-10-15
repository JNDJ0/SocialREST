package validation;

import model.Post;

public interface PostValidator {
    ValidationResult validateForCreate(Post post);
    ValidationResult validateForUpdate(Post post);
}
