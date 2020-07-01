package com.example.saferestaurants;

import com.example.saferestaurants.model.Violation;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ViolationTest {
    @Test
    public void violation(){
        String violationExample = "101,Not Critical,Plans/construction/alterations not in accordance with the Regulation [s. 3; s. 4],Not Repeat";
        String[] splicedExample = violationExample.split(",");
        Violation violation = new Violation(splicedExample[0],
                splicedExample[1],
                splicedExample[2],
                splicedExample[3]);

        assertEquals("101", violation.getCode());
        assertEquals("Not Critical", violation.getCriticalValue());
        assertEquals(splicedExample[2], violation.getDescription());
        assertEquals("Not Repeat", violation.getRepeated());
        assertEquals("Building", violation.getType());
    }

    @Test
    public void setType(){
        String foodViolation = "201,Critical,Food contaminated or unfit for human consumption [s. 13],Not Repeat";
        String pestViolation = "304,Not Critical,Premises not free of pests [s. 26(a)],Not Repeat";
        String buildingViolation = "102,Not Critical,Operation of an unapproved food premises [s. 6(1)],Not Repeat";
        String equipmentViolation = "301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)],Not Repeat";
        String sanitaryViolation = "310,Not Critical,Single use containers & utensils are used more than once [s. 20],Not Repeat";
        String employeeViolation = "402,Critical,Employee does not wash hands properly or at adequate frequency [s. 21(3)],Not Repeat";
        String qualificationsViolation = "502,Not Critical,In operator’s absence no staff on duty has FOODSAFE Level 1 or equivalent [s. 10(2)],Not Repeat";

        String[] types = {"Food", "Pest", "Building", "Equipment", "Sanitary", "Employee", "Qualifications"};
        int i = 0;

        List<String> violationStrings = Arrays.asList(foodViolation, pestViolation, buildingViolation, equipmentViolation, sanitaryViolation, employeeViolation, qualificationsViolation);
        for( String violationString : violationStrings){
            String [] splicedViolation = violationString.split(",");
            Violation violation = new Violation(splicedViolation[0], splicedViolation[1], splicedViolation[2], splicedViolation[3]);
            assertEquals(types[i], violation.getType());
            i++;
        }
    }
}
