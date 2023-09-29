package CleverIT.KeepItSimple.payload.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class TaskRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String deadline;

    private String status;

    private List<String> tags;


    public TaskRequest() {

    }

    public TaskRequest(String title, String description, String deadline, String status, List<String> tags) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.tags = tags;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getDeadline() { return deadline; }

    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public List<String> getTags() { return tags; }

    public void setTags(List<String> tags) { this.tags = tags; }
}