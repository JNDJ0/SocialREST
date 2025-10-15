package resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import dto.AuthDtos.TokenRequest;
import dto.AuthDtos.TokenResponse;
import model.User;
import service.AuthService;
import service.UserService;
import util.JwtUtil;

@Path("/jwt")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService = AuthService.getInstance();
    private final UserService userService = UserService.getInstance();

    @POST
    @Path("/token")
    public Response token(TokenRequest req) {
        if (req == null || req.grant_type == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"invalid_request\"}").build();
        }

        switch (req.grant_type) {
            case "password": {
                if (req.username == null || req.password == null)
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\":\"invalid_request\"}").build();
                User user = userService.loginUser(req.username, req.password);
                if (user == null)
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"invalid_grant\"}").build();

                String access = authService.issueAccessToken(user);
                String refresh = authService.issueRefreshToken(user);
                return Response.ok(new TokenResponse(access, util.JwtUtil.getAccessTtlSeconds(), refresh)).build();
            }
            case "refresh_token": {
                if (req.refresh_token == null)
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\":\"invalid_request\"}").build();
                try {
                    Jws<Claims> parsed = JwtUtil.parse(req.refresh_token);
                    if (!"refresh".equals(parsed.getBody().get("typ")))
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("{\"error\":\"invalid_grant\"}").build();

                    int uid = Integer.parseInt(parsed.getBody().getSubject());
                    User user = userService.getUserById(uid);

                    String access = authService.issueAccessToken(user);
                    String refresh = authService.issueRefreshToken(user);
                    return Response.ok(new TokenResponse(access, util.JwtUtil.getAccessTtlSeconds(), refresh)).build();
                } catch (Exception e) {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"invalid_grant\"}").build();
                }
            }
            default:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"unsupported_grant_type\"}").build();
        }
    }
}
