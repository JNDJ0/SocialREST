//package util;
//
//import jakarta.annotation.Priority;
//import jakarta.ws.rs.Priorities;
//import jakarta.ws.rs.container.ContainerRequestContext;
//import jakarta.ws.rs.container.ContainerRequestFilter;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.ext.Provider;
//
//import io.jsonwebtoken.Claims;
//
//import java.io.IOException;
//import java.security.Principal;
//
//@Provider
//@Priority(Priorities.AUTHENTICATION)
//public class BearerAuthFilter implements ContainerRequestFilter {
//
//    @Override
//    public void filter(ContainerRequestContext ctx) throws IOException {
//        String path = ctx.getUriInfo().getPath();
//
//        if ("OPTIONS".equalsIgnoreCase(ctx.getMethod()) ||
//            path.startsWith("oauth")) {
//            return;
//        }
//
//        String h = ctx.getHeaderString("Authorization");
//        if (h == null || !h.startsWith("Bearer ")) {
//            abort(ctx);
//            return;
//        }
//        String token = h.substring("Bearer ".length());
//        try {
//            var jws = JwtUtil.parse(token);
//            Claims c = jws.getBody();
//            String subject = c.getSubject(); 
//
//            var old = ctx.getSecurityContext();
//            ctx.setSecurityContext(new jakarta.ws.rs.core.SecurityContext() {
//                @Override public Principal getUserPrincipal() {
//                    return () -> subject;
//                }
//                @Override public boolean isUserInRole(String role) { return true; }
//                @Override public boolean isSecure() { return old != null && old.isSecure(); }
//                @Override public String getAuthenticationScheme() {
//                    return "Bearer";
//                }
//            });
//        } catch (Exception e) {
//            abort(ctx);
//        }
//    }
//
//    private void abort(ContainerRequestContext ctx){
//        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
//                .entity("{\"error\":\"unauthorized\"}")
//                .build());
//    }
//}
