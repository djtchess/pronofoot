package fr.pronofoot.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import fr.pronofoot.dto.MatchDto;
import fr.pronofoot.entity.Match;

@Mapper(
        componentModel   = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = { Instant.class, LocalDateTime.class, ZoneId.class }
)
public interface MatchMapper {

    /* -------- DTO ➜ Entity ------------------------------------------- */
    @Mapping(target = "id", ignore = true)

    /* Parse "2025-03-29T21:30:00Z" → LocalDateTime (Europe/Paris) */
    @Mapping(
            target      = "date",
            expression  =
                    "java( LocalDateTime.ofInstant(" +
                            "          Instant.parse(dto.getDate())," +
                            "          ZoneId.of(\"Europe/Paris\") ) )"
    )

    @Mapping(target = "equipeDomicile",   ignore = true)
    @Mapping(target = "equipeExterieur",  ignore = true)
    @Mapping(target = "championnatSaison", ignore = true)
    Match toEntity(MatchDto dto);

    /* -------- Entity ➜ DTO ------------------------------------------- */
    @Mapping(
            target     = "date",
            expression =
                    "java( entity.getDate() == null ? null :" +
                            "      entity.getDate()" +
                            "            .atZone(ZoneId.of(\"Europe/Paris\"))" +
                            "            .toInstant()" +
                            "            .toString() )"
    )
    @Mapping(target = "equipeDomicile",   source = "equipeDomicile.nom")
    @Mapping(target = "equipeExterieur",  source = "equipeExterieur.nom")
    @Mapping(target = "championnatCode",  source = "championnatSaison.championnat.code")
    @Mapping(target = "saisonAnnee",      source = "championnatSaison.saison.annee")
    MatchDto toDto(Match entity);
}
