package com.example.saferestaurants.model;

import java.util.ArrayList;

public class Restaurants {

    // attributes //
    private static Restaurants instance;
    private ArrayList<Restaurant> restaurants = new ArrayList<>();

    // instance //
    public static Restaurants getInstance(){
        if (instance == null){
            instance = new Restaurants();
        }
        return instance;
    }

    // 'setter' //
    public void add(Restaurant restaurant){
        this.restaurants.add(restaurant);
        this.sort();
    }

    // 'getter' //
    public Restaurant get(int index){
        return restaurants.get(index);
    }

    // sorting restaurants to be in alphabetical order //
    // uses selection sort algorithm //
    public void sort(){
        int n = this.size();

        for(int i = 0; i < n-1; i++){
            int minIndex = i;

            for(int j = i+1; j < n; j++){
                if(isAlphabeticallyGreater(j, minIndex)){
                    minIndex = j;
                }
            }

            Restaurant temp = restaurants.get(minIndex);
            restaurants.set(minIndex, restaurants.get(i));
            restaurants.set(i, temp);
        }
    }

    public int size(){
        return restaurants.size();
    }
    public boolean isAlphabeticallyGreater(int j, int minIndex){
        return this.get(minIndex).getName().compareTo(this.get(j).getName()) > 0;
    }
}
