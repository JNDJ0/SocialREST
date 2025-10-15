//package config;
//
//import jakarta.annotation.Priority;
//import jakarta.ws.rs.Priorities;
//import jakarta.ws.rs.container.*;
//import jakarta.ws.rs.core.*;
//import jakarta.ws.rs.ext.Provider;
//import service.AuthService;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.security.Principal;
//import java.util.Base64;
//
//@Provider
//@Priority(Priorities.AUTHENTICATION)
//public class BasicAuthFilter implements ContainerRequestFilter {
//
//    private final AuthService authService = AuthService.getInstance();
//
//    @Override
//    public void filter(ContainerRequestContext ctx) throws IOException {
//        String header = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
//        if (header == null || !header.startsWith("Basic ")) {
//            abort(ctx);
//            return;
//        }
//        try {
//            String base64 = header.substring("Basic ".length()).trim();
//            String cred = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
//            String[] parts = cred.split(":", 2);
//            if (parts.length != 2) throw new AuthenticationException("Invalid authorization token");
//            
//            String email = parts[0]; 
//            String password = parts[1];
//
//            if (!authService.validate(email, password)) {
//                throw new AuthenticationException("Invalid email or password");
//            }
//
//            Principal principal = () -> email; // The principal is now the email
//            boolean isSecure = ctx.getSecurityContext() != null && ctx.getSecurityContext().isSecure();
//            ctx.setSecurityContext(new BasicSecurityContext(principal, isSecure, authService.getRoles(email)));
//
//        } catch (AuthenticationException ex) {
//            throw ex;
//        } catch (Exception ex) {
//            abort(ctx);
//        }
//    }
//
//    private void abort(ContainerRequestContext ctx) {
//        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
//                .header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"TIES\"")
//                .entity("Unauthorized")
//                .build());
//    }
//}
