package es.uji.ei1027.sgovi.model;

import java.time.LocalDate;

public class Contract {
    private int idContract;
    private String wage;
    private LocalDate startDate;
    private LocalDate endDate;
    private String url;

    public Contract() {}

    public Contract(String wage, LocalDate startDate, LocalDate endDate, String url) {
        this.wage = wage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.url = url;
    }

    public int getIdContract() { return idContract; }
    public void setIdContract(int idContract) { this.idContract = idContract; }
    public String getWage() { return wage; }
    public void setWage(String wage) { this.wage = wage; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}

