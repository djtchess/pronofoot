package fr.pronofoot.dto;

import java.util.Set;


public class ChampionnatDto {
    private String code;
    private String nom;
    private Set<String> equipes; // noms d’équipes

    public ChampionnatDto() {
    }

    public ChampionnatDto(String code, String nom) {
        this.code = code;
        this.nom = nom;
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
}
