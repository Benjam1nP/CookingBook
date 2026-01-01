package org.manager;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING) // must be a string
    private Difficulty difficulty;

    private int servings;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Ingredient> ingredients = new HashSet<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<InstructionStep> steps = new HashSet<>();

    @OneToOne(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private NutritionalInfo nutritionalInfo;

    protected Recipe() { }

    public Recipe(String name, String description, Difficulty difficulty, int servings) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.servings = servings;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setRecipe(null);
    }

    public void addStep(InstructionStep step) {
        steps.add(step);
        step.setRecipe(this);
    }

    public void removeStep(InstructionStep step) {
        steps.remove(step);
        step.setRecipe(null);
    }

    public void setNutritionalInfo(NutritionalInfo info) {
        this.nutritionalInfo = info;
        if (info != null) {
            info.setRecipe(this);
        }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public List<Ingredient> getIngredients() {
        return ingredients.stream()
                .sorted((a, b) -> Long.compare(a.getId() != null ? a.getId() : 0, b.getId() != null ? b.getId() : 0))
                .collect(Collectors.toList());
    }

    public List<InstructionStep> getSteps() {
        return steps.stream()
                .sorted((a, b) -> Integer.compare(a.getStepNumber(), b.getStepNumber()))
                .collect(Collectors.toList());
    }

    public NutritionalInfo getNutritionalInfo() { return nutritionalInfo; }
}
