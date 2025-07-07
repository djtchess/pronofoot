package fr.pronofoot.dto;

import java.util.Set;


public class ChampionnatSaisonDto {
    private String codeChampionnat;
    private String nomChampionnat;
    private String anneeSaison;
    private Set<String> equipes;

    public String getCodeChampionnat() {
        return codeChampionnat;
    }

    public void setCodeChampionnat(String codeChampionnat) {
        this.codeChampionnat = codeChampionnat;
    }

    public String getNomChampionnat() {
        return nomChampionnat;
    }

    public void setNomChampionnat(String nomChampionnat) {
        this.nomChampionnat = nomChampionnat;
    }

    public String getAnneeSaison() {
        return anneeSaison;
    }

    public void setAnneeSaison(String anneeSaison) {
        this.anneeSaison = anneeSaison;
    }

    public Set<String> getEquipes() {
        return equipes;
    }

    public void setEquipes(Set<String> equipes) {
        this.equipes = equipes;
    }
}
