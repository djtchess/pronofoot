package fr.pronofoot.dto;

import java.util.List;
import java.util.Set;


public class ChampionnatDto {
    private String code;
    private String nom;
    private String pays;
    private SaisonDto currentSeason;

    private Set<String> equipes;
    private List<SaisonDto> saisons;

    public ChampionnatDto() {
    }

    public ChampionnatDto(String code, String nom, String pays, SaisonDto currentSeason, Set<String> equipes, List<SaisonDto> saison) {
        this.code = code;
        this.nom = nom;
        this.pays = pays;
        this.currentSeason = currentSeason;
        this.equipes = equipes;
        this.saisons = saisons;
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

    public List<SaisonDto> getSaisons() {
        return saisons;
    }

    public void setSaisons(List<SaisonDto> saisons) {
        this.saisons = saisons;
    }
}
