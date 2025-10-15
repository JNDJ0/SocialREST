package validation;

import model.User;

import java.util.regex.Pattern;

public class UserValidatorImpl implements UserValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public ValidationResult validateForCreate(User user) {
        ValidationResult result = ValidationResult.ok();

        if (user.getName() == null || user.getName().isBlank()) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("name", "Name must not be empty");
        }

        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("email", "Email is invalid or empty");
        }

        if (user.getAge() != null && user.getAge() <= 0) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("age", "Age must be greater than 0");
        }
        
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("password", "Password must not be empty");
        }

        return result;
    }

    @Override
    public ValidationResult validateForUpdate(User user) {
        ValidationResult result = validateForCreate(user);

        if (user.getId() <= 0) {
            result = ValidationResult.error("Validation failed");
            result.addFieldError("id", "Id must be greater than 0 for update");
        }

        return result;
    }
}
