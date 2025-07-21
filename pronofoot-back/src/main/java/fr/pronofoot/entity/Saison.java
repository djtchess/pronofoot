package fr.pronofoot.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "saison")
public class Saison {
    @Id
    private Long id;

    private String annee;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "championnat_id",
            foreignKey = @ForeignKey(name = "saison_championnat_fk"))
    private Championnat championnat;

    public Saison() {
    }

    public Saison(Long id, String annee, LocalDate startDate, LocalDate endDate, Championnat championnat) {
        this.id = id;
        this.annee = annee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.championnat = championnat;
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

    public Championnat getChampionnat() {
        return championnat;
    }

    public void setChampionnat(Championnat championnat) {
        this.championnat = championnat;
    }
}
