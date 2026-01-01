package org.manager;

import jakarta.persistence.*;

@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private long quantity;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @Enumerated(EnumType.STRING)
    private IngredientCategory category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    protected Ingredient() { }

    public Ingredient(String name, long quantity, Unit unit, IngredientCategory category) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");}
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
    }

    public Long getId() { return id; }
    public String getName() { return name; }

    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }
        this.quantity = quantity;
    }

    public Unit getUnit() { return unit; }
    public IngredientCategory getCategory() { return category; }

    public Recipe getRecipe() { return recipe; }
    void setRecipe(Recipe recipe) { this.recipe = recipe; }
}
