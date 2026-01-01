package org.manager;

import jakarta.persistence.*;
// values per serving
// optional to a recipe
@Entity
public class NutritionalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int calories;
    private double protein;
    private double fat;
    private double carbohydrates;

    @OneToOne(optional = false)
    @JoinColumn(name = "recipe_id", unique = true)
    private Recipe recipe;

    protected NutritionalInfo() { }

    public NutritionalInfo(int calories, double protein, double fat, double carbohydrates) {
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    public Long getId() { return id; }
    public int getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public double getCarbohydrates() { return carbohydrates; }

    public Recipe getRecipe() { return recipe; }
    void setRecipe(Recipe recipe) { this.recipe = recipe; }
}
