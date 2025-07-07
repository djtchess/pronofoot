package fr.pronofoot.dto;

import java.util.List;

public class TeamsApiResponse {
    private List<TeamItem> teams;

    public List<TeamItem> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamItem> teams) {
        this.teams = teams;
    }
}

