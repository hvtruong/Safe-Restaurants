package com.example.saferestaurants;



import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Inspections;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class InspectionsTest {

    @Test
    public void inspections(){
        String date = "20181024";
        String type = "Follow-Up";
        String criticalIssues = "0";
        String nonCriticalIssues = "1";
        String hazardRating = "Low";
        ArrayList<String> violations = new ArrayList<>();
        violations.add("308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat");

        Inspections inspections = new Inspections();
        inspections.add(new Inspection(date, type, criticalIssues, nonCriticalIssues, hazardRating, violations));

        date = "20191002";
        inspections.add(new Inspection(date, type, criticalIssues, nonCriticalIssues, hazardRating, violations));

        date = "20180320";
        inspections.add(new Inspection(date, type, criticalIssues, nonCriticalIssues, hazardRating, violations));

        date = "20200122";
        inspections.add(new Inspection(date, type, criticalIssues, nonCriticalIssues, hazardRating, violations));

        Date date1 = new Date(2020-1900, 0, 22);
        assertTrue(date1.equals(inspections.get(0).getDate()));

        Date date2 = new Date(2019-1900, 9, 02);
        assertTrue(date2.equals(inspections.get(1).getDate()));

        Date date3 = new Date(2018-1900, 9, 24);
        assertTrue(date3.equals(inspections.get(2).getDate()));

        Date date4 = new Date(2018-1900, 2, 20);
        assertTrue(date4.equals(inspections.get(3).getDate()));
    }
}
