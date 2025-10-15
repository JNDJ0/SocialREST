package util; // Or your preferred package

import config.BasicSecurityContext;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import service.AuthService;
import javax.naming.AuthenticationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private final AuthService authService = AuthService.getInstance();

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String path = ctx.getUriInfo().getPath(true);

        System.out.println("PATH: " + path);   
        if ("OPTIONS".equalsIgnoreCase(ctx.getMethod()) ||
            path.endsWith("jwt/token") ||
            path.endsWith("users/register") ||
            path.endsWith("users/login")) {
            return;
        }

        String authHeader = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            abort(ctx, "Authorization header is required.");
            return;
        }

        if (authHeader.startsWith("Bearer ")) {
            handleBearerAuth(ctx, authHeader.substring("Bearer ".length()).trim());
        } 
        else if (authHeader.startsWith("Basic ")) {
            handleBasicAuth(ctx, authHeader.substring("Basic ".length()).trim());
        } 
        else {
            abort(ctx, "Unsupported Authorization scheme.");
        }
    }

    private void handleBearerAuth(ContainerRequestContext ctx, String token) {
        try {
            Claims claims = JwtUtil.parse(token).getBody();
            String subject = claims.getSubject();
            
            List<String> roles = claims.get("roles", List.class);
            
            SecurityContext originalContext = ctx.getSecurityContext();
            ctx.setSecurityContext(new SecurityContext() {
                @Override public Principal getUserPrincipal() { return () -> subject; }

                @Override public boolean isUserInRole(String role) {
                    return roles != null && roles.contains(role);
                }
                
                @Override public boolean isSecure() { return originalContext.isSecure(); }
                @Override public String getAuthenticationScheme() { return "Bearer"; }
            });
        } catch (Exception e) {
            System.err.println("JWT Validation Error: " + e.getMessage());
            abort(ctx, "Invalid or expired Bearer token.");
        }
    }

    private void handleBasicAuth(ContainerRequestContext ctx, String base64) {
        try {
            String cred = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            String[] parts = cred.split(":", 2);
            if (parts.length != 2) throw new AuthenticationException("Invalid token format");

            String email = parts[0];
            String password = parts[1];

            if (!authService.validate(email, password)) {
                throw new AuthenticationException("Invalid email or password");
            }

            Principal principal = () -> email;
            boolean isSecure = ctx.getSecurityContext() != null && ctx.getSecurityContext().isSecure();
            ctx.setSecurityContext(new BasicSecurityContext(principal, isSecure, authService.getRoles(email)));

        } catch (AuthenticationException ex) {
            abort(ctx, ex.getMessage());
        } catch (Exception ex) {
            abort(ctx, "Invalid Basic credentials.");
        }
    }

    private void setSecurityContext(ContainerRequestContext ctx, String subject, String scheme) {
        SecurityContext originalContext = ctx.getSecurityContext();
        ctx.setSecurityContext(new SecurityContext() {
            @Override public Principal getUserPrincipal() { return () -> subject; }
            @Override public boolean isUserInRole(String role) { return true; }
            @Override public boolean isSecure() { return originalContext.isSecure(); }
            @Override public String getAuthenticationScheme() { return scheme; }
        });
    }

    private void abort(ContainerRequestContext ctx, String message) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer, Basic realm=\"SocialREST\"")
                .entity("{\"error\":\"" + message + "\"}")
                .build());
    }
}

