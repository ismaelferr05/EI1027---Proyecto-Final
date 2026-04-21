package es.uji.ei1027.sgovi.model;

public class OviUser {
    private int idOviUser;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String province;
    private String town;
    private String pc;
    private Integer age;
    private String gender;
    private String status;
    private boolean lopdConsent;

    public OviUser() {}

    public OviUser(String name, String lastName, String email, String phone, String password, String province, String town, String pc, Integer age, String gender, String status, boolean lopdConsent) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.province = province;
        this.town = town;
        this.pc = pc;
        this.age = age;
        this.gender = gender;
        this.status = status;
        this.lopdConsent = lopdConsent;
    }

    public int getIdOviUser() {
        return idOviUser;
    }

    public void setIdOviUser(int idOviUser) {
        this.idOviUser = idOviUser;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isLopdConsent() { return lopdConsent; }
    public void setLopdConsent(boolean lopdConsent) { this.lopdConsent = lopdConsent; }
}

