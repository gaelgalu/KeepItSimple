package CleverIT.KeepItSimple.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "tags")
public class Tag {
    @Id
    private String id;

    @NotBlank
    @Size(max = 30)
    private String tagname;

    @DBRef
    @JsonIgnore
    private List<Task> tasks;

    @DBRef
    @JsonIgnore
    private User createdBy;

    public Tag() {

    }

    public Tag(String tagname, List<Task> tasks, User createdBy) {
        this.tagname = tagname;
        this.tasks = tasks;
        this.createdBy = createdBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public List<Task> getTasks() { return tasks; }

    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    public User getCreatedBy() { return createdBy; }

    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public void addTask(Task task) {
        if (this.tasks == null) {
            this.tasks = new java.util.ArrayList<>();
        }
        this.tasks.add(task);
    }

}