package com.example.mealtrackerapp.databases;


import java.io.Serializable;

/**
 * Class for logs in food diary.
 * Implements Serializable so that the object of this class can be put as extras in an intent.
 */
public class FoodLog implements Serializable {
    /**
     * id for database entry
     */
    private int id;
    /**
     * date of the log
     */
    private String date;
    /**
     * name of the food log
     */
    private String name;
    /**
     * meal of the log
     */
    private String meal;
    /**
     * time of the log
     */
    private String time;
    /**
     * caloric content in kCal
     */
    private int calories;
    /**
     * carbs content in grams
     */
    private double carbs;
    /**
     * protein content in grams
     */
    private double protein;
    /**
     * fat content in grams
     */
    private double fat;

    public FoodLog(int id, String date, String name, String meal, String time, int calories, double carbs, double protein, double fat) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.meal = meal;
        this.time = time;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    //typical setters and getters for each variable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    /**
     * Get simplified string of food log. Showing values in grams for macronutrients.
     * @return String with name, calories in kCal and macronutrients in grams.
     */
    public String getStringGrams() {
        return name + " : " + calories + " kcal (" + Math.round(carbs) + "g/" + Math.round(protein) + "g/" + Math.round(fat) + "g)";
    }

    /**
     * Get string with macronutrient values in kCal
     * @return String with time, meal, name, calories and macronutrients in kCal
     */
    @Override
    public String toString() {
        return time + " | " + meal + " | " + name + " : " + calories + " kcal (" + Math.round(carbs*4) + "/" + Math.round(protein*4) + "/" + Math.round(fat*8) + ")";
    }
}
