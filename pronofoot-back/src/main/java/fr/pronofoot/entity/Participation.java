package fr.pronofoot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "participation")
public class Participation {

    @Id
    @SequenceGenerator(name = "match_seq", sequenceName = "match_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_seq")
    private Long id;

    @ManyToOne
    private Equipe equipe;

    @ManyToOne
    private ChampionnatSaison championnatSaison;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public ChampionnatSaison getChampionnatSaison() {
        return championnatSaison;
    }

    public void setChampionnatSaison(ChampionnatSaison championnatSaison) {
        this.championnatSaison = championnatSaison;
    }
}
