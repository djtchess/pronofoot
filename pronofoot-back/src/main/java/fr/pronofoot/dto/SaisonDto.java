package fr.pronofoot.dto;

public class SaisonDto {
    private Long id;
    private String startDate;
    private String endDate;
    private Integer year;

    public SaisonDto() {
    }

    public SaisonDto(Long id, String startDate, String endDate, Integer year) {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
