package es.uji.ei1027.sgovi.model;

public class CandidateProposal {
    private final PapPati papPati;
    private final int score;
    private final String matchSummary;

    public CandidateProposal(PapPati papPati, int score, String matchSummary) {
        this.papPati = papPati;
        this.score = score;
        this.matchSummary = matchSummary;
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
}

