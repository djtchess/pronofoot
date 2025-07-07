package fr.pronofoot.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "ChampionnatSaison")
public class ChampionnatSaison {

    @Id
    @SequenceGenerator(name = "ChampionnatSaison_seq", sequenceName = "ChampionnatSaison_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChampionnatSaison_seq")
    private Long id;

    @ManyToOne
    private Championnat championnat;

    @ManyToOne
    private Saison saison;

    @OneToMany(mappedBy = "championnatSaison", cascade = CascadeType.ALL)
    private List<Participation> participations = new ArrayList<>();

    public ChampionnatSaison() {
    }

    public ChampionnatSaison(Long id, Championnat championnat, Saison saison, List<Participation> participations) {
        this.id = id;
        this.championnat = championnat;
        this.saison = saison;
        this.participations = participations;
    }

    public ChampionnatSaison(Championnat championnat, Saison saison) {
        this.championnat = championnat;
        this.saison = saison;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Championnat getChampionnat() {
        return championnat;
    }

    public void setChampionnat(Championnat championnat) {
        this.championnat = championnat;
    }

    public Saison getSaison() {
        return saison;
    }

    public void setSaison(Saison saison) {
        this.saison = saison;
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<Participation> participations) {
        this.participations = participations;
    }
}

