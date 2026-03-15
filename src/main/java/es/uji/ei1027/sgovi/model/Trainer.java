package es.uji.ei1027.sgovi.model;

public class Trainer {
    private int trainer_id;
    private String name;
    private String lastName;
    private String occupation;
    private String email;
    private String phone;
    private String address;

    public Trainer() {}

    public Trainer(String name, String lastName, String occupation, String email, String phone, String address) {
        this.name = name;
        this.lastName = lastName;
        this.occupation = occupation;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTrainer_id() {
        return trainer_id;
    }

    public void setTrainer_id(int trainer_id) {
        this.trainer_id = trainer_id;
    }
}

