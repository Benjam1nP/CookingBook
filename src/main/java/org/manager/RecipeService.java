package org.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // CRUD Operations
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    public List<Recipe> searchRecipesByName(String name) {
        if (name == null || name.isEmpty()) {
            return getAllRecipes();
        }
        return recipeRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Recipe> findByDifficulty(Difficulty difficulty) {
        return recipeRepository.findByDifficulty(difficulty);
    }

    // Business Logic Method 1: Calculate Portions
    public void calculatePortions(Recipe recipe, int targetServings) {
        if (targetServings <= 0) throw new IllegalArgumentException("targetServings must be > 0");
        if (recipe.getServings() <= 0) throw new IllegalStateException("recipe.servings must be > 0");

        double factor = (double) targetServings / recipe.getServings();

        for (Ingredient ing : recipe.getIngredients()) {
            long oldQty = ing.getQuantity();
            long newQty = Math.round(oldQty * factor);
            if (newQty < 1) newQty = 1; // Minimum 1 unit
            ing.setQuantity(newQty);
        }
        recipe.setServings(targetServings);
        recipeRepository.save(recipe);
    }

    // Business Logic Method 2: Check if Vegetarian
    public boolean isVegetarian(Recipe recipe) {
        for (Ingredient ing : recipe.getIngredients()) {
            IngredientCategory c = ing.getCategory();
            if (c == IngredientCategory.MEAT ||
                    c == IngredientCategory.POULTRY ||
                    c == IngredientCategory.FISH ||
                    c == IngredientCategory.SEAFOOD) {
                return false;
            }
        }
        return true;
    }

    // Business Logic Method 3: Calculate Total Cooking Time
    public int calculateTotalCookingTime(Recipe recipe) {
        int sum = 0;
        for (InstructionStep step : recipe.getSteps()) {
            sum += step.getDurationMinutes();
        }
        return sum;
    }
}
