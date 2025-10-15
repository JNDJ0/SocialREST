package model;

import jakarta.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private int id;
    private String name;
    private String email;
    private Integer age;
    @JsonIgnore
    private String password;
    private Set<String> roles;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<Integer> postIds = new ArrayList<>();
    
    private List<Map<String, String>> links = new ArrayList<>();
    
    public User() {}

    public User(int id, String name, String email, String password, Set<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.createdAt = LocalDateTime.now();
    }

    public User(int id, String name, String email, Integer age, String password, Set<String> roles) {
        this(id, name, email, password, roles);
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Set<String> getRoles() {
    	return roles;
    }
    
    public void setRoles(Set<String> roles) {
    	this.roles = roles;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Integer> getPostIds() {
        return postIds;
    }

    public void setPostIds(List<Integer> postIds) {
        this.postIds = postIds;
    }

    public void addPostId(int postId) {
        this.postIds.add(postId);
    }
    
    public List<Map<String, String>> getLinks() {
        return links;
    }
    
    public void setLinks(List<Map<String, String>> links) {
        this.links = links;
    }
}
