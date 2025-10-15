package dto;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginResponse {
    private String message;
    private int userId;
    private String role;
    private String token; // opcional, si luego implementas JWT

    // Constructor vacío (necesario para Jackson/Jersey)
    public LoginResponse() {}

    // Constructor con parámetros
    public LoginResponse(String message, int userId, String role, String token) {
        this.message = message;
        this.userId = userId;
        this.role = role;
        this.token = token;
    }

    // Getters y Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
