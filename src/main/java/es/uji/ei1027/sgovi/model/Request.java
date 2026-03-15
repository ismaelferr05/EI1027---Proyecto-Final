package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class Request {
    private int request_id;
    private String description;
    private String training;
    private LocalDate startDate;
    private LocalDate endDate;
    private String experience;
    private String experienceType;
    private Integer oviUser_id;

    public Request() {}

    public Request(String description, String training, LocalDate startDate, LocalDate endDate, String experience, String experienceType) {
        this.description = description;
        this.training = training;
        this.startDate = startDate;
        this.endDate = endDate;
        this.experience = experience;
        this.experienceType = experienceType;
    }

    public int getIdRequest() { return request_id; }
    public void setIdRequest(int idRequest) { this.request_id = idRequest; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTraining() { return training; }
    public void setTraining(String training) { this.training = training; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getExperienceType() { return experienceType; }
    public void setExperienceType(String experienceType) { this.experienceType = experienceType; }
    public Integer getIdOvilUser() { return oviUser_id; }
    public void setIdOvilUser(Integer idOvilUser) { this.oviUser_id = idOvilUser; }
}

