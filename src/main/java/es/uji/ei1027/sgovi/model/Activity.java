package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class Activity {
    private int idActivity;
    private String name;
    private LocalDate date;
    private int duration;
    private String location;
    private String category;
    private String description;
    private Integer idTrainer;

    public Activity() {}

    public Activity(String name, LocalDate date, int duration, String location, String category, String description) {
        this.name = name;
        this.date = date;
        this.duration = duration;
        this.location = location;
        this.category = category;
        this.description = description;
    }

    public int getIdActivity() { return idActivity; }
    public void setIdActivity(int idActivity) { this.idActivity = idActivity; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getIdTrainer() { return idTrainer; }
    public void setIdTrainer(Integer idTrainer) { this.idTrainer = idTrainer; }
}

