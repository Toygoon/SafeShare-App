package com.toygoon.safeshare.data;

public class RiskFactorDTO {
    public String name;
    public Integer riskLevel;
    public Integer riskImpact;

    public RiskFactorDTO(String name, Integer riskLevel, Integer riskImpact) {
        this.name = name;
        this.riskLevel = riskLevel;
        this.riskImpact = riskImpact;
    }

    public String toString() {
        return this.name + ": " + riskLevel + "lv, " + riskImpact + "impact";
    }
}
