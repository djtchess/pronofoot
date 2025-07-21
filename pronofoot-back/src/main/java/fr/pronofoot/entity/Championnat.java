package fr.pronofoot.entity;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Entity
@Table (name = "championnat")
public class Championnat {
    @Id
    @SequenceGenerator(name = "championnat_seq", sequenceName = "championnat_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "championnat_seq")
    private Long id;

    private String code;

    private String nom;

    @OneToMany(mappedBy = "championnat", cascade = CascadeType.ALL)
    private List<Saison> saisons = new ArrayList<>();

    /**
     * Saison actuellement en cours (FK current_season_id).
     * Nullable : certains championnats n’ont peut-être pas encore commencé.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_season_id",
            foreignKey = @ForeignKey(name = "championnat_current_season_fk"))
    private Saison currentSeason;

    public Championnat() {}

    public Championnat(String code, String nom) {
        this.code = code;
        this.nom = nom;
    }

    // Getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public List<Saison> getSaisons() {
        return saisons;
    }

    public void setSaisons(List<Saison> saisons) {
        this.saisons = saisons;
    }

    public Saison getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(Saison currentSeason) {
        this.currentSeason = currentSeason;
    }
}
