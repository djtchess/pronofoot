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
    private List<ChampionnatSaison> saisons = new ArrayList<>();

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

    public List<ChampionnatSaison> getSaisons() {
        return saisons;
    }

    public void setSaisons(List<ChampionnatSaison> saisons) {
        this.saisons = saisons;
    }
}
