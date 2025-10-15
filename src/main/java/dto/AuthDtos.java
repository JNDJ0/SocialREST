package dto;

public class AuthDtos {
    public static class TokenRequest {
        public String grant_type;    
        public String username;       
        public String password;       
        public String refresh_token;
    }

    public static class TokenResponse {
        public String token_type = "Bearer";
        public String access_token;
        public long   expires_in;
        public String refresh_token;

        public TokenResponse(String access, long expiresIn, String refresh) {
            this.access_token = access;
            this.expires_in = expiresIn;
            this.refresh_token = refresh;
        }
    }
}
