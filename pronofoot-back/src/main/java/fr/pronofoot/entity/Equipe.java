package fr.pronofoot.entity;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipe")
public class Equipe {

    @Id
    @SequenceGenerator(name = "equipe_seq", sequenceName = "equipe_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipe_seq")
    private Long id;

    @Column(unique = true)
    private String nom;

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL)
    private List<Participation> participations = new ArrayList<>();

    public Equipe() {
    }

    public Equipe(Long id, String nom, List<Participation> participations) {
        this.id = id;
        this.nom = nom;
        this.participations = participations;
    }

    public Equipe(String nom) {
        this.nom = nom;
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<Participation> participations) {
        this.participations = participations;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
