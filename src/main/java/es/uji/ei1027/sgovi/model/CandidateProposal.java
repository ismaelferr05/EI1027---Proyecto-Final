package es.uji.ei1027.sgovi.model;

import java.util.List;

public class CandidateProposal {
    private final PapPati papPati;
    private final int score;
    private final String matchSummary;
    private final List<String> reasonDetails;

    public CandidateProposal(PapPati papPati, int score, String matchSummary, List<String> reasonDetails) {
        this.papPati = papPati;
        this.score = score;
        this.matchSummary = matchSummary;
        this.reasonDetails = reasonDetails;
    }

    public PapPati getPapPati() {
        return papPati;
    }

    public int getScore() {
        return score;
    }

    public String getMatchSummary() {
        return matchSummary;
    }

    public List<String> getReasonDetails() {
        return reasonDetails;
    }
}

