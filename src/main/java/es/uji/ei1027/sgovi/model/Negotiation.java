package es.uji.ei1027.sgovi.model;

public class Negotiation {
    private int idNegotiation;
    private String stateOfApproval;
    private Integer idRequest;
    private Integer idPapPati;

    public Negotiation() {}

    public Negotiation(String stateOfApproval) {
        this.stateOfApproval = stateOfApproval;
    }

    public int getIdNegotiation() { return idNegotiation; }
    public void setIdNegotiation(int idNegotiation) { this.idNegotiation = idNegotiation; }
    public String getStateOfApproval() { return stateOfApproval; }
    public void setStateOfApproval(String stateOfApproval) { this.stateOfApproval = stateOfApproval; }
    public Integer getIdRequest() { return idRequest; }
    public void setIdRequest(Integer idRequest) { this.idRequest = idRequest; }
    public Integer getIdPapPati() { return idPapPati; }
    public void setIdPapPati(Integer idPapPati) { this.idPapPati = idPapPati; }
}

