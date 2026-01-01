package org.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // GET all recipes
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    // GET recipe by ID
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST create new recipe
    @PostMapping
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeService.saveRecipe(recipe);
    }

    // PUT update existing recipe
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        return recipeService.getRecipeById(id)
                .map(existing -> ResponseEntity.ok(recipeService.saveRecipe(recipe)))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE recipe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        if (recipeService.getRecipeById(id).isPresent()) {
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET search recipes by name
    @GetMapping("/search")
    public List<Recipe> searchRecipes(@RequestParam String name) {
        return recipeService.searchRecipesByName(name);
    }

    // POST calculate portions (Business Logic 1)
    @PostMapping("/{id}/calculate-portions")
    public ResponseEntity<Recipe> calculatePortions(
            @PathVariable Long id,
            @RequestParam int servings) {
        return recipeService.getRecipeById(id)
                .map(recipe -> {
                    recipeService.calculatePortions(recipe, servings);
                    return ResponseEntity.ok(recipe);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // GET check if vegetarian (Business Logic 2)
    @GetMapping("/{id}/is-vegetarian")
    public ResponseEntity<Map<String, Boolean>> isVegetarian(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(recipe -> {
                    Map<String, Boolean> response = new HashMap<>();
                    response.put("vegetarian", recipeService.isVegetarian(recipe));
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // GET calculate total cooking time (Business Logic 3)
    @GetMapping("/{id}/cooking-time")
    public ResponseEntity<Map<String, Integer>> getCookingTime(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(recipe -> {
                    Map<String, Integer> response = new HashMap<>();
                    response.put("totalMinutes", recipeService.calculateTotalCookingTime(recipe));
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}