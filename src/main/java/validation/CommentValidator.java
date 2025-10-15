package validation;

import model.Comment;

public interface CommentValidator {
    ValidationResult validateForCreate(Comment comment);
    ValidationResult validateForUpdate(Comment comment);
}
