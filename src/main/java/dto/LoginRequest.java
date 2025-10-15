package dto;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginRequest {
    private String email;
    private String password;
    private String type;

    // Constructor vacío (necesario para Jackson/Jersey)
    public LoginRequest() {}

    // Constructor con parámetros
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getType() {
        return type;
    }
}
