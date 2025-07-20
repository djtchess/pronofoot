package fr.pronofoot.dto.record;

public record TeamStandingDto(
        String equipe,
        int joues,
        int gagnes,
        int nuls,
        int perdus,
        int butsPour,
        int butsContre,
        int goalDifference,
        int points
) {}
