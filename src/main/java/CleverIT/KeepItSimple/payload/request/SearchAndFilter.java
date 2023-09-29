package CleverIT.KeepItSimple.payload.request;

import java.util.List;

public class SearchAndFilter {
    private String title;
    private List<String> tags;

    private List<String> status;

    public SearchAndFilter() {

    }

    public SearchAndFilter(String title, List<String> tags, List<String> status) {
        this.title = title;
        this.tags = tags;
        this.status = status;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<String> getTags() { return tags; }

    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getStatus() { return status; }

    public void setStatus(List<String> status) { this.status = status; }
}
