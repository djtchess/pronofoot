package fr.pronofoot.dto;

import java.util.Set;


public class ChampionnatDto {
    private String code;
    private String nom;
    private String pays;
    private SaisonDto currentSeason;

    private Set<String> equipes;

    public ChampionnatDto() {
    }

    public ChampionnatDto(String code, String nom, String pays, SaisonDto currentSeason, Set<String> equipes) {
        this.code = code;
        this.nom = nom;
        this.pays = pays;
        this.currentSeason = currentSeason;
        this.equipes = equipes;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<String> getEquipes() {
        return equipes;
    }

    public void setEquipes(Set<String> equipes) {
        this.equipes = equipes;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public SaisonDto getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(SaisonDto currentSeason) {
        this.currentSeason = currentSeason;
    }
}
