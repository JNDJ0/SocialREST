package validation;

import model.User;

public interface UserValidator {
    ValidationResult validateForCreate(User user);
    ValidationResult validateForUpdate(User user);
}
