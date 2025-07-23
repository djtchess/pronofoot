package fr.pronofoot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "match")
public class Match {
    @Id
    @SequenceGenerator(name = "match_seq", sequenceName = "match_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_seq")
    private Long id;

    private Long numJournee;

    private LocalDateTime date;

    @ManyToOne(optional = false)
    private Equipe equipeDomicile;

    @ManyToOne(optional = false)
    private Equipe equipeExterieur;

    private Integer scoreDomicile;
    private Integer scoreExterieur;

    private Long remoteId;

    @ManyToOne(optional = false)
    private ChampionnatSaison championnatSaison;

    public Match() {
    }

    public Match(Long id, LocalDateTime date, Equipe equipeDomicile, Equipe equipeExterieur, Integer scoreDomicile, Integer scoreExterieur, ChampionnatSaison championnatSaison, Long remoteId) {
        this.id = id;
        this.date = date;
        this.equipeDomicile = equipeDomicile;
        this.equipeExterieur = equipeExterieur;
        this.scoreDomicile = scoreDomicile;
        this.scoreExterieur = scoreExterieur;
        this.championnatSaison = championnatSaison;
        this.remoteId = remoteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumJournee() {
        return numJournee;
    }

    public void setNumJournee(Long numJournee) {
        this.numJournee = numJournee;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Equipe getEquipeDomicile() {
        return equipeDomicile;
    }

    public void setEquipeDomicile(Equipe equipeDomicile) {
        this.equipeDomicile = equipeDomicile;
    }

    public Equipe getEquipeExterieur() {
        return equipeExterieur;
    }

    public void setEquipeExterieur(Equipe equipeExterieur) {
        this.equipeExterieur = equipeExterieur;
    }

    public Integer getScoreDomicile() {
        return scoreDomicile;
    }

    public void setScoreDomicile(Integer scoreDomicile) {
        this.scoreDomicile = scoreDomicile;
    }

    public Integer getScoreExterieur() {
        return scoreExterieur;
    }

    public void setScoreExterieur(Integer scoreExterieur) {
        this.scoreExterieur = scoreExterieur;
    }

    public ChampionnatSaison getChampionnatSaison() {
        return championnatSaison;
    }

    public void setChampionnatSaison(ChampionnatSaison championnatSaison) {
        this.championnatSaison = championnatSaison;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
    }
}
