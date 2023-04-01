package model;

public class Calendar_Event {

    private String id;
    private String title;
    private String time;
    private String description;

    public Calendar_Event() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Calendar_Event(String id, String title, /*String time*/ String description) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
