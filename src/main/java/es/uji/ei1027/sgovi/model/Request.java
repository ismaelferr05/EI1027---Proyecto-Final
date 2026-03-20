package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class Request {
    private int idRequest;
    private String description;
    private String training;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer experience;
    private String experienceType;
    private Integer idOviUser;

    public Request() {}

    public Request(String description, String training, LocalDate startDate, LocalDate endDate, Integer experience, String experienceType) {
        this.description = description;
        this.training = training;
        this.startDate = startDate;
        this.endDate = endDate;
        this.experience = experience;
        this.experienceType = experienceType;
    }

    public int getIdRequest() { return idRequest; }
    public void setIdRequest(int idRequest) { this.idRequest = idRequest; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTraining() { return training; }
    public void setTraining(String training) { this.training = training; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }
    public String getExperienceType() { return experienceType; }
    public void setExperienceType(String experienceType) { this.experienceType = experienceType; }
    public Integer getIdOviUser() { return idOviUser; }
    public void setIdOviUser(Integer idOviUser) { this.idOviUser = idOviUser; }
}

