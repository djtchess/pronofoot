package fr.pronofoot.dto;

public class MatchDto {

    private Long numJournee;
    private String date;
    private String equipeDomicile;
    private String equipeExterieur;
    private Integer scoreDomicile;
    private Integer scoreExterieur;
    private String championnatCode;
    private String saisonAnnee;
    private Long remoteId;

    public Long getNumJournee() {
        return numJournee;
    }

    public void setNumJournee(Long numJournee) {
        this.numJournee = numJournee;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEquipeDomicile() {
        return equipeDomicile;
    }

    public void setEquipeDomicile(String equipeDomicile) {
        this.equipeDomicile = equipeDomicile;
    }

    public String getEquipeExterieur() {
        return equipeExterieur;
    }

    public void setEquipeExterieur(String equipeExterieur) {
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

    public String getChampionnatCode() {
        return championnatCode;
    }

    public void setChampionnatCode(String championnatCode) {
        this.championnatCode = championnatCode;
    }

    public String getSaisonAnnee() {
        return saisonAnnee;
    }

    public void setSaisonAnnee(String saisonAnnee) {
        this.saisonAnnee = saisonAnnee;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
    }
}
