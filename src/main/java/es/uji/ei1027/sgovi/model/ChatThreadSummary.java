package es.uji.ei1027.sgovi.model;

public class ChatThreadSummary {
    private Negotiation negotiation;
    private Request request;
    private OviUser oviUser;
    private PapPati papPati;
    private Contract contract;
    private Message lastMessage;
    private int messageCount;
    private boolean active;

    public Negotiation getNegotiation() {
        return negotiation;
    }

    public void setNegotiation(Negotiation negotiation) {
        this.negotiation = negotiation;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public OviUser getOviUser() {
        return oviUser;
    }

    public void setOviUser(OviUser oviUser) {
        this.oviUser = oviUser;
    }

    public PapPati getPapPati() {
        return papPati;
    }

    public void setPapPati(PapPati papPati) {
        this.papPati = papPati;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

