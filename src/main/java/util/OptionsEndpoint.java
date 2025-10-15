package util;

import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Path("{path: .*}")
@Provider
public class OptionsEndpoint {
    @OPTIONS
    public Response options() {
        return Response.ok().build();
    }
}
