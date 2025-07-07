package fr.pronofoot.dto;

import java.awt.geom.Area;

public class CompetitionItem {
    private String code;
    private String name;
    private Area area;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }
}
