package com.example.saferestaurants.model;

import java.util.Arrays;
import java.util.List;

public class Violation {

    // Types of Violations { "Food", "Pest", "Equipment", "Sanitary", "Employee", "Building", "Qualifiications" }
    private final List<String> FOOD = Arrays.asList("201", "202", "203", "204", "205", "206", "208", "209", "210", "211", "212");
    private final List<String> PEST = Arrays.asList("304", "305");
    private final List<String> EQUIPMENT = Arrays.asList("301", "302", "303", "307", "308", "309", "315");
    private final List<String> SANITARY = Arrays.asList("306", "310", "311", "312", "313", "314");
    private final List<String> EMPLOYEE = Arrays.asList("401", "402", "403", "404");
    private final List<String> BUILDING = Arrays.asList("101", "102", "103", "104");
    private final List<String> QUALIFICATIONS = Arrays.asList("501", "502");

    // attributes //
    private String code;
    private String criticalValue;
    private String description;
    private String repeated;
    private String type;

    // constructor //
    public Violation(String code, String criticalValue, String description, String repeated ){
        this.code = code;
        this.criticalValue = criticalValue;
        this.description = description;
        this.repeated = repeated;
        setType();
    }

    // getters //
    public String getCode(){
        return this.code;
    }
    public String getCriticalValue(){
        return this.criticalValue;
    }
    public String getDescription(){
        return this.description;
    }
    public String getRepeated(){
        return this.repeated;
    }
    public String getType(){ return this.type; }


    private void setType(){
        if(isFoodType()){
            type = "Food";
        } else if (isPestType()){
            type = "Pest";
        } else if (isEquipmentType()){
            type = "Equipment";
        } else if (isSanitaryType()){
            type = "Sanitary";
        } else if (isEmployeeType()){
            type = "Employee";
        } else if (isBuildingType()){
            type = "Building";
        } else if (isQualificationsType()){
            type = "Qualifications";
        } else {
            type = "Food";
        }
    }

    private boolean isFoodType(){
        return FOOD.contains(code);
    }
    private boolean isPestType(){
        return PEST.contains(code);
    }
    private boolean isEquipmentType(){
        return EQUIPMENT.contains(code);
    }
    private boolean isSanitaryType(){
        return SANITARY.contains(code);
    }
    private boolean isEmployeeType(){
        return EMPLOYEE.contains(code);
    }
    private boolean isBuildingType(){
        return BUILDING.contains(code);
    }
    private boolean isQualificationsType(){
        return QUALIFICATIONS.contains(code);
    }


}
