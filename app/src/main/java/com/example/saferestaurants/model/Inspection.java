package com.example.saferestaurants.model;

import java.util.ArrayList;
import java.util.Date;

public class Inspection {

    // attributes //
    private Date date;
    private String type;
    private int criticalIssues;
    private int nonCriticalIssues;
    private String hazardRating;
    private ArrayList<Violation> violations;

    // constructor //
    public Inspection(String date, String type, String criticalIssues, String nonCriticalIssues, String hazardRating, ArrayList<String> violations){
        setDate(date);
        this.type = type;
        this.criticalIssues = Integer.parseInt(criticalIssues);
        this.nonCriticalIssues = Integer.parseInt(nonCriticalIssues);
        this.hazardRating = hazardRating;
        this.violations = new ArrayList<>();
        setViolations(violations);
    }

    // setters //
    public void setViolations(ArrayList<String> violations) {
        for( String violation : violations ){
            String[] splitViolation = violation.split(",");
            this.violations.add(new Violation(splitViolation[0],    // code
                                              splitViolation[1],    // critical value
                                              splitViolation[2],    // description
                                              splitViolation[3])    // repeated variable
                                );
        }
    }

    public void setDate(String date){
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(4,6)) - 1;
        int day = Integer.parseInt(date.substring(6,8));
        this.date = new Date(year, month, day);

    }

    public void setType(String type){
        this.type = type;
    }

    public void setCriticalIssues(String criticalIssues){
        this.criticalIssues = Integer.parseInt(criticalIssues);
    }

    public void setNonCriticalIssues(String nonCriticalIssues){
        this.nonCriticalIssues = Integer.parseInt(nonCriticalIssues);
    }

    public void setHazardRating(String hazardRating){
        this.hazardRating = hazardRating;
    }

    // getters //
    public Date getDate(){
        return this.date;
    }

    public String getType(){
        return this.type;
    }

    public int getCriticalIssues(){
        return this.criticalIssues;
    }

    public int getNonCriticalIssues(){
        return this.nonCriticalIssues;
    }

    public String getHazardRating(){
        return this.hazardRating;
    }

    public ArrayList<Violation> getViolations(){
        return this.violations;
    }

    public String getDateDifferent(){

    }
}
