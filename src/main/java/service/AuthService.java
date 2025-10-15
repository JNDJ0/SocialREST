package service;

import model.User;
import repository.inmemory.UserRepoInMemory;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.util.Map;
import java.util.HashMap;

import util.JwtUtil;
import util.PasswordUtils;

public class AuthService {
    private static AuthService instance;

    private final Map<String, User> usersByEmail = new HashMap<>();

    private final UserRepoInMemory userRepo = UserRepoInMemory.getInstance();

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean validate(String email, String password) {
        if (email == null || password == null) return false;
        User user = usersByEmail.get(email);
        return user != null && PasswordUtils.verifyPassword(password, user.getPassword());
    }

    public Set<String> getRoles(String email) {
        User user = usersByEmail.get(email);
        if (user != null && user.getRoles() != null) {
            return user.getRoles();
        }
        return Collections.emptySet();
    }
    
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    public void addUser(User user) {
        usersByEmail.put(user.getEmail(), user);
    }

    public String issueAccessToken(User user) {
        var claims = new java.util.HashMap<String,Object>();
        claims.put("uid", user.getId());
        claims.put("name", user.getName());
        claims.put("roles", new ArrayList<>(user.getRoles()));
        return JwtUtil.generateAccessToken(String.valueOf(user.getId()), claims);
    }

    public String issueRefreshToken(User user) {
        return JwtUtil.generateRefreshToken(String.valueOf(user.getId()));
    }
}