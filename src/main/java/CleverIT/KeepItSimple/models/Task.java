package CleverIT.KeepItSimple.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "tasks")
public class Task {
    @Id
    private String id;

    @NotBlank
    @Size(max = 64)
    private String title;

    @NotBlank
    @Size(max = 256)
    private String description;

    private LocalDate deadline;

    @NotBlank
    @Size(max = 32)
    private String status;

    @DBRef
    private List<Tag> tags;

    @DBRef
    @JsonIgnore
    private User user;


    public Task() {

    }

    public Task(String title, String description, LocalDate deadline, String status, List<Tag> tags, User user) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.tags = tags;
        this.user = user;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }

    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public List<Tag> getTags() { return tags; }

    public void setTags(List<Tag> tags) { this.tags = tags; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public void addTag(Tag tag) {
        if (this.tags == null) {
            this.tags = new java.util.ArrayList<>();
        }
        this.tags.add(tag);
    }

}
