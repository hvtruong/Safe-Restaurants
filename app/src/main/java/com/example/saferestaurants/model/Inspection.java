package com.example.saferestaurants.model;

import java.util.ArrayList;
import java.util.Date;

/* Inspection class holds the information for a single inspection on a restaurant
    including its date, type, number of critical and non critical issues, hazard rating, and list of
    violations
 */
public class Inspection {

    // attributes //
    private Date date;
    private String type;
    private int criticalIssues;
    private int nonCriticalIssues;
    private String hazardRating;
    private ArrayList<Violation> violations;
    private String inspectionDay, inspectionMonth, inspectionYear;

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
        int year = Integer.parseInt(date.substring(0,4)) - 1900;
        int month = Integer.parseInt(date.substring(4,6)) - 1;
        int day = Integer.parseInt(date.substring(6,8));
        this.date = new Date(year, month, day);
        year = year + 1900;

        this.inspectionDay = ("" + day);
        this.inspectionMonth = this.getDate().toString().substring(4,7);
        this.inspectionYear = ("" + year);
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

    public String getInspectionDay() {
        return this.inspectionDay;
    }

    public String getInspectionMonth() {
        return this.inspectionMonth;
    }

    public String getInspectionYear() {
        return this.inspectionYear;
    }

    //Time Different calculator
    public long inspectionTimeDifferent(){ ;
        Date currentDate = new Date();
        long inspectionTimeDifferent = (currentDate.getTime() - date.getTime())/(1000*60*60*24);

        return inspectionTimeDifferent;
    }
}
