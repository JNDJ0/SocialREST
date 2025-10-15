package service;

import model.User;
import util.PasswordUtils;
import repository.inmemory.UserRepoInMemory;
import repository.inmemory.PostRepoInMemory;
import repository.inmemory.CommentRepoInMemory;
import validation.UserValidator;
import validation.UserValidatorImpl;
import validation.ValidationResult;
import config.UserNotFoundException;
import config.ValidationException;
import config.EmailAlreadyExistsException;
import service.AuthService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class UserService {
    private static final UserService instance = new UserService();
    private final UserRepoInMemory userRepo = UserRepoInMemory.getInstance();
    private final PostRepoInMemory postRepo = PostRepoInMemory.getInstance();
    private final CommentRepoInMemory commentRepo = CommentRepoInMemory.getInstance();
    private final UserValidator validator = new UserValidatorImpl();
    private final AuthService authService = AuthService.getInstance();

    private UserService() {}

    public static UserService getInstance() {
        return instance;
    }

    public List<User> getAllUsers() {
        return userRepo.list();
    }

    public User getUserById(int id) {
        User u = userRepo.getById(id);
        if (u == null) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        return u;
    }

    public User createUser(String name, String email, String password) {
        if (userRepo.getByEmail(email) != null) {
            throw new EmailAlreadyExistsException("Email already registered: " + email);
        }

        String hashed = PasswordUtils.hashPassword(password);

        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(hashed);
        u.setRoles(new HashSet<>(Arrays.asList("USER")));
        u.setCreatedAt(LocalDateTime.now());

        return userRepo.create(u);
    }

    public User updateUser(int id, User user) {
    	if (id <= 0) {
            throw new IllegalArgumentException("Id must not be null");
    	}
    	String userPassword = getUserById(id).getPassword();
    	user.setPassword(userPassword);
        ValidationResult result = validator.validateForUpdate(user);
        if (!result.isValid()) {
        	System.out.println(result.getFieldErrors());
            throw new IllegalArgumentException("Invalid user: " + result.getFieldErrors());
        }
        User updated = userRepo.update(id, user);
        if (updated == null) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        return updated;
    }

    public void deleteUser(int id) {
        boolean deleted = userRepo.delete(id, postRepo, commentRepo);
        if (!deleted) {
            throw new UserNotFoundException("User with id " + id + " not found or has posts/comments");
        }
    }

    public User loginUser(String email, String password) {
        User u = userRepo.getByEmail(email);
        if (u == null) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
        boolean valid = PasswordUtils.verifyPassword(password, u.getPassword());
        if (!valid) {
            throw new ValidationException("Invalid credentials");
        }
        authService.addUser(u);
        
        return u;
    }
}
