package fr.pronofoot.dto;

import java.util.List;

public class CompetitionApiResponse {
    private List<CompetitionItem> competitions;

    public List<CompetitionItem> getCompetitions() { return competitions; }
    public void setCompetitions(List<CompetitionItem> competitions) { this.competitions = competitions; }
}
