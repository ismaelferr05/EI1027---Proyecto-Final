package es.uji.ei1027.sgovi.model;

public class PapPati {
    private int idPapPati;
    private String phone;
    private String password;
    private String province;
    private String town;
    private String pc;
    private int age;
    private String gender;
    private String cvUrl;
    private String training;
    private String experience;
    private String experienceType;

    public PapPati() {}

    public PapPati(String phone, String password, String province, String town, String pc, int age, String gender, String cvUrl, String training, String experience, String experienceType) {
        this.phone = phone;
        this.password = password;
        this.province = province;
        this.town = town;
        this.pc = pc;
        this.age = age;
        this.gender = gender;
        this.cvUrl = cvUrl;
        this.training = training;
        this.experience = experience;
        this.experienceType = experienceType;
    }

    public int getIdPapPati() { return idPapPati; }
    public void setIdPapPati(int idPapPati) { this.idPapPati = idPapPati; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getTown() { return town; }
    public void setTown(String town) { this.town = town; }
    public String getPc() { return pc; }
    public void setPc(String pc) { this.pc = pc; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getCvUrl() { return cvUrl; }
    public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }
    public String getTraining() { return training; }
    public void setTraining(String training) { this.training = training; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getExperienceType() { return experienceType; }
    public void setExperienceType(String experienceType) { this.experienceType = experienceType; }
}

