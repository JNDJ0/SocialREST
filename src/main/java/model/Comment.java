package model;

import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL) // evita que aparezcan campos nulos en JSON
public class Comment {
    private int id;
    private int postId;     // referencia al Post al que pertenece
    private int authorId;   // referencia al User que escribió el comentario
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private int likes;
    
    // Campo para HATEOAS
    private List<Map<String, String>> links = new ArrayList<>();

    // Constructor vacío (obligatorio para Jersey/Jackson)
    public Comment() {}

    // Constructor con parámetros principales
    public Comment(int id, int postId, int authorId, String content) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.likes = 0;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    // Métodos de conveniencia
    public void addLike() {
        this.likes++;
    }
    
    public List<Map<String, String>> getLinks() {
        return links;
    }
    
    public void setLinks(List<Map<String, String>> links) {
        this.links = links;
    }
}
