package es.uji.ei1027.sgovi.model;

public class Trainer {
    private int idTrainer;
    private String name;
    private String occupation;
    private String lastName;
    private String email;
    private String phone;
    private String address;

    public Trainer() {}

    public Trainer(String name, String occupation, String lastName, String email, String phone, String address) {
        this.name = name;
        this.occupation = occupation;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public int getIdTrainer() { return idTrainer; }
    public void setIdTrainer(int idTrainer) { this.idTrainer = idTrainer; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}

