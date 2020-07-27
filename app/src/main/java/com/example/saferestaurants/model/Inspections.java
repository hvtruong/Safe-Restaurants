package com.example.saferestaurants.model;

import java.util.ArrayList;

/* Inspections class holds a collection of all inspections on a single restaurant
    ordered by date (most recent to latest)
 */
public class Inspections {

    // attributes //
    ArrayList<Inspection> inspections = new ArrayList<>();

    // 'setter' //
    public void add(Inspection inspection){
        inspections.add(inspection);
        this.sort();
    }

    // 'getter' //
    public Inspection get(int index){
        return inspections.get(index);
    }

    // sorting from most recent inspection at index 0 to oldest inspection at index size()-1 //
    // uses selection sort algorithm //
    public void sort(){
        int n = this.size();

        for( int i = 0; i < n - 1; i++ ){
            int minIndex = i;

            for( int j = i+1; j < n; j++ ){
                if(isRecentDate(j, minIndex)){
                    minIndex = j;
                }
            }

            Inspection temp = inspections.get(minIndex);
            inspections.set(minIndex, inspections.get(i));
            inspections.set(i, temp);
        }
    }

    public int size(){
        return inspections.size();
    }

    private boolean isRecentDate(int j, int minIndex){
        return inspections.get(minIndex).getDate().compareTo(inspections.get(j).getDate()) < 0;
    }

    public ArrayList<Inspection> getInspections() {
        return this.inspections;
    }

    public int totalNumberOfCriticalIssuesLastYear(){
        int result = 0;
        for(int i = 0; i < inspections.size(); i++){
            Inspection inspection = inspections.get(i);
            if(inspection.inspectionTimeDifferent() <= 365){
                result += inspection.getCriticalIssues();
            }
            break;
        }
        return result;
    }
}
