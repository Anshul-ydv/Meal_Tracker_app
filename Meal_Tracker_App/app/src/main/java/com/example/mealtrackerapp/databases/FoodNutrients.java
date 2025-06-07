package com.example.mealtrackerapp.databases;

/**
 * Class to handle objects containing the nutrition values of foods from the FINELI database.
 */
public class FoodNutrients {
    /**
     * food id from the database
     */
    private int id;
    /**
     * food name from the db
     */
    private String name;
    /**
     * food caloric value in kCal per 100g from the db
     */
    private int calories;
    /**
     * food carbs value in grams per 100g
     */
    private double carbs;
    /**
     * food protein value in grams per 100g
     */
    private double protein;
    /**
     * food fat value in grams per 100g
     */
    private double fat;

    public FoodNutrients(int id, String name, int calories, double carbs, double protein, double fat) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public FoodNutrients() {
    }

    //regular getters and setters
    public int getCalories() {
        return calories;
    }
    public double getCarbs() {
        return carbs;
    }
    public double getProtein() {
        return protein;
    }
    public double getFat() {
        return fat;
    }

    @Override
    public String toString() {
        return name;
    }
}
