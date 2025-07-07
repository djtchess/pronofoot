package fr.pronofoot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.pronofoot.dto.MatchDto;
import fr.pronofoot.entity.Match;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", expression = "java(java.time.LocalDate.parse(dto.getDate()))")
    @Mapping(target = "equipeDomicile", ignore = true)
    @Mapping(target = "equipeExterieur", ignore = true)
    @Mapping(target = "championnatSaison", ignore = true)
    Match toEntity(MatchDto dto);

    @Mapping(target = "date", expression = "java(entity.getDate().toString())")
    @Mapping(target = "equipeDomicile", source = "equipeDomicile.nom")
    @Mapping(target = "equipeExterieur", source = "equipeExterieur.nom")
    @Mapping(target = "championnatCode", source = "championnatSaison.championnat.code")
    @Mapping(target = "saisonAnnee", source = "championnatSaison.saison.annee")
    MatchDto toDto(Match entity);
}
