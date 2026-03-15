package es.uji.ei1027.sgovi.model;

public class OviUser {
    private int oviuser_id;
    private String name;
    private String phone;
    private String password;
    private String province;
    private String town;
    private String pc;
    private int age;
    private String gender;

    public OviUser() {}

    public OviUser(String name, String phone, String password, String province, String town, String pc, int age, String gender) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.province = province;
        this.town = town;
        this.pc = pc;
        this.age = age;
        this.gender = gender;
    }

    public int getOviuser_id() {
        return oviuser_id;
    }

    public void setOviuser_id(int oviuser_id) {
        this.oviuser_id = oviuser_id;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
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
}

