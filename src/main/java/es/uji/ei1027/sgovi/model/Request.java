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
    private String preferredGender;
    private String preferredPc;
    private Integer preferredAge;
    private String status;
    private Integer idOviUser;

    public Request() {}

    public Request(String description, String training, LocalDate startDate, LocalDate endDate, Integer experience, String experienceType, String preferredGender, String preferredPc, Integer preferredAge, String status, Integer idOviUser) {
        this.description = description;
        this.training = training;
        this.startDate = startDate;
        this.endDate = endDate;
        this.experience = experience;
        this.experienceType = experienceType;
        this.preferredGender = preferredGender;
        this.preferredPc = preferredPc;
        this.preferredAge = preferredAge;
        this.status = status;
        this.idOviUser = idOviUser;
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
    public String getPreferredGender() { return preferredGender; }
    public void setPreferredGender(String preferredGender) { this.preferredGender = preferredGender; }
    public String getPreferredPc() { return preferredPc; }
    public void setPreferredPc(String preferredPc) { this.preferredPc = preferredPc; }
    public Integer getPreferredAge() { return preferredAge; }
    public void setPreferredAge(Integer preferredAge) { this.preferredAge = preferredAge; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getIdOviUser() { return idOviUser; }
    public void setIdOviUser(Integer idOviUser) { this.idOviUser = idOviUser; }
}

