package es.uji.ei1027.sgovi.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Contract {
    private int idContract;
    private BigDecimal wage;
    private LocalDate startDate;
    private LocalDate endDate;
    private String url;
    private Integer idNegotiation;

    public Contract() {}

    public Contract(BigDecimal wage, LocalDate startDate, LocalDate endDate, String url, Integer idNegotiation) {
        this.wage = wage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.url = url;
        this.idNegotiation = idNegotiation;
    }

    public int getIdContract() { return idContract; }
    public void setIdContract(int idContract) { this.idContract = idContract; }
    public BigDecimal getWage() { return wage; }
    public void setWage(BigDecimal wage) { this.wage = wage; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Integer getIdNegotiation() {
        return idNegotiation;
    }

    public void setIdNegotiation(Integer idNegotiation) {
        this.idNegotiation = idNegotiation;
    }
}

