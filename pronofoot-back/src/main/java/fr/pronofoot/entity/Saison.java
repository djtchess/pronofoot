package fr.pronofoot.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


@Entity
@Table(name = "saison")
public class Saison {
    @Id
    @SequenceGenerator(name = "saison_seq", sequenceName = "saison_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saison_seq")
    private Long id;

    private String annee; // Exemple : "2024-2025"

    @OneToMany(mappedBy = "saison", cascade = CascadeType.ALL)
    private List<ChampionnatSaison> championnats = new ArrayList<>();

    public Saison() {
    }

    public Saison(Long id, String annee, List<ChampionnatSaison> championnats) {
        this.id = id;
        this.annee = annee;
        this.championnats = championnats;
    }

    public Saison(String annee) {
        this.annee = annee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public List<ChampionnatSaison> getChampionnats() {
        return championnats;
    }

    public void setChampionnats(List<ChampionnatSaison> championnats) {
        this.championnats = championnats;
    }
}
