package fr.pronofoot.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "saison")
public class Saison {
    @Id
    private Long id;

    private String annee;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "saison", cascade = CascadeType.ALL)
    private List<ChampionnatSaison> championnats = new ArrayList<>();

    public Saison() {
    }

    public Saison(Long id, String annee, LocalDate startDate, LocalDate endDate, List<ChampionnatSaison> championnats) {
        this.id = id;
        this.annee = annee;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<ChampionnatSaison> getChampionnats() {
        return championnats;
    }

    public void setChampionnats(List<ChampionnatSaison> championnats) {
        this.championnats = championnats;
    }
}
