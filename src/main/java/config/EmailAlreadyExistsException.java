package config;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class EmailAlreadyExistsException extends WebApplicationException {

    public EmailAlreadyExistsException(String message) {
        super(Response.status(Response.Status.CONFLICT) // 409 Conflict
                      .entity(new ErrorMessage(message, 409))
                      .type(MediaType.APPLICATION_JSON)
                      .build());
    }

    // Clase interna para devolver JSON limpio
    public static class ErrorMessage {
        private String error;
        private int status;

        public ErrorMessage(String error, int status) {
            this.error = error;
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public int getStatus() {
            return status;
        }
    }
}
