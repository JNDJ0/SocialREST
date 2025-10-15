package config;

import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Set;

public class BasicSecurityContext implements SecurityContext {
    private final Principal principal;
    private final boolean secure;
    private final Set<String> roles;

    public BasicSecurityContext(Principal principal, boolean secure, Set<String> roles){
        this.principal = principal;
        this.secure = secure;
        this.roles = roles;
    }

    @Override public Principal getUserPrincipal(){ return principal; }
    @Override public boolean isUserInRole(String role){ return roles != null && roles.contains(role); }
    @Override public boolean isSecure(){ return secure; }
    @Override public String getAuthenticationScheme(){ return "BASIC"; }
}
