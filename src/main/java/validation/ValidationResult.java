package validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ValidationResult {
    private boolean valid;
    private String message;
    private Map<String, String> fieldErrors;

    // Constructor privado para forzar el uso de los métodos estáticos
    private ValidationResult(boolean valid, String message, Map<String, String> fieldErrors) {
        this.valid = valid;
        this.message = message;
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
    }

    // Métodos estáticos de fábrica
    public static ValidationResult ok() {
        return new ValidationResult(true, "Validation passed", null);
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(false, message, null);
    }

    public static ValidationResult error(String message, Map<String, String> fieldErrors) {
        return new ValidationResult(false, message, fieldErrors);
    }

    // Getters
    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getFieldErrors() {
        return Collections.unmodifiableMap(fieldErrors);
    }

    // Métodos de conveniencia
    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }

    public void addFieldError(String field, String errorMessage) {
        this.fieldErrors.put(field, errorMessage);
    }
}
