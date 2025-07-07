package fr.pronofoot.dto;

public class MatchItem {
    private String utcDate;
    private Team homeTeam;
    private Team awayTeam;
    private Score score;

    public String getUtcDate() { return utcDate; }
    public void setUtcDate(String utcDate) { this.utcDate = utcDate; }

    public Team getHomeTeam() { return homeTeam; }
    public void setHomeTeam(Team homeTeam) { this.homeTeam = homeTeam; }

    public Team getAwayTeam() { return awayTeam; }
    public void setAwayTeam(Team awayTeam) { this.awayTeam = awayTeam; }

    public Score getScore() { return score; }
    public void setScore(Score score) { this.score = score; }
}
