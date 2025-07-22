package fr.pronofoot.dto;

import java.time.LocalDate;
import java.util.Objects;

public class SaisonDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String year;

    public SaisonDto() {
    }

    public SaisonDto(Long id, LocalDate startDate, LocalDate endDate, String year) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaisonDto saisonDto = (SaisonDto) o;
        return Objects.equals(id, saisonDto.id) && Objects.equals(startDate, saisonDto.startDate) && Objects.equals(endDate, saisonDto.endDate) && Objects.equals(year, saisonDto.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, year);
    }
}
