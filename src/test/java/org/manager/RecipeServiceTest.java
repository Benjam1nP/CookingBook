package org.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe testRecipe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a test recipe with 4 servings
        testRecipe = new Recipe("Test Recipe", "Test Description", Difficulty.MEDIUM, 4);

        // Add vegetarian ingredients
        testRecipe.addIngredient(new Ingredient("Tomato", 400, Unit.G, IngredientCategory.VEGETARIAN));
        testRecipe.addIngredient(new Ingredient("Cheese", 200, Unit.G, IngredientCategory.VEGETARIAN));

        // Add cooking steps
        testRecipe.addStep(new InstructionStep(1, "Step 1", 10));
        testRecipe.addStep(new InstructionStep(2, "Step 2", 20));
        testRecipe.addStep(new InstructionStep(3, "Step 3", 15));
    }

    // ===== Business Logic Method 1: calculatePortions =====

    @Test
    @DisplayName("Calculate Portions: Double the servings (4 to 8)")
    void testCalculatePortions_DoubleSServings() {
        // Arrange
        int originalServings = testRecipe.getServings();
        long originalTomatoQty = testRecipe.getIngredients().get(0).getQuantity();
        long originalCheeseQty = testRecipe.getIngredients().get(1).getQuantity();

        // Act
        recipeService.calculatePortions(testRecipe, 8);

        // Assert
        assertEquals(8, testRecipe.getServings(), "Servings should be updated to 8");
        assertEquals(originalTomatoQty * 2, testRecipe.getIngredients().get(0).getQuantity(),
                "Tomato quantity should be doubled");
        assertEquals(originalCheeseQty * 2, testRecipe.getIngredients().get(1).getQuantity(),
                "Cheese quantity should be doubled");
        verify(recipeRepository, times(1)).save(testRecipe);
    }

    @Test
    @DisplayName("Calculate Portions: Half the servings (4 to 2)")
    void testCalculatePortions_HalfServings() {
        // Act
        recipeService.calculatePortions(testRecipe, 2);

        // Assert
        assertEquals(2, testRecipe.getServings(), "Servings should be updated to 2");
        assertEquals(200, testRecipe.getIngredients().get(0).getQuantity(),
                "Tomato quantity should be halved (400 -> 200)");
        assertEquals(100, testRecipe.getIngredients().get(1).getQuantity(),
                "Cheese quantity should be halved (200 -> 100)");
        verify(recipeRepository, times(1)).save(testRecipe);
    }

    @Test
    @DisplayName("Calculate Portions: Exception when target servings is zero or negative")
    void testCalculatePortions_InvalidTargetServings() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            recipeService.calculatePortions(testRecipe, 0);
        }, "Should throw exception when target servings is 0");

        assertThrows(IllegalArgumentException.class, () -> {
            recipeService.calculatePortions(testRecipe, -5);
        }, "Should throw exception when target servings is negative");
    }

    @Test
    @DisplayName("Calculate Portions: Exception when recipe servings is invalid")
    void testCalculatePortions_InvalidRecipeServings() {
        // Arrange
        Recipe invalidRecipe = new Recipe("Invalid", "Test", Difficulty.EASY, 0);
        invalidRecipe.addIngredient(new Ingredient("Salt", 10, Unit.G, IngredientCategory.VEGETARIAN));

        // Assert
        assertThrows(IllegalStateException.class, () -> {
            recipeService.calculatePortions(invalidRecipe, 4);
        }, "Should throw exception when recipe servings is 0");
    }

    // ===== Business Logic Method 2: isVegetarian =====

    @Test
    @DisplayName("Is Vegetarian: Recipe with only vegetarian ingredients")
    void testIsVegetarian_OnlyVegetarianIngredients() {
        // Assert
        assertTrue(recipeService.isVegetarian(testRecipe),
                "Recipe with only vegetarian ingredients should be vegetarian");
    }

    @Test
    @DisplayName("Is Vegetarian: Recipe with meat ingredient")
    void testIsVegetarian_WithMeat() {
        // Arrange
        testRecipe.addIngredient(new Ingredient("Beef", 500, Unit.G, IngredientCategory.MEAT));

        // Assert
        assertFalse(recipeService.isVegetarian(testRecipe),
                "Recipe with meat should not be vegetarian");
    }

    @Test
    @DisplayName("Is Vegetarian: Recipe with poultry ingredient")
    void testIsVegetarian_WithPoultry() {
        // Arrange
        testRecipe.addIngredient(new Ingredient("Chicken", 400, Unit.G, IngredientCategory.POULTRY));

        // Assert
        assertFalse(recipeService.isVegetarian(testRecipe),
                "Recipe with poultry should not be vegetarian");
    }

    @Test
    @DisplayName("Is Vegetarian: Recipe with fish ingredient")
    void testIsVegetarian_WithFish() {
        // Arrange
        testRecipe.addIngredient(new Ingredient("Salmon", 300, Unit.G, IngredientCategory.FISH));

        // Assert
        assertFalse(recipeService.isVegetarian(testRecipe),
                "Recipe with fish should not be vegetarian");
    }

    @Test
    @DisplayName("Is Vegetarian: Recipe with seafood ingredient")
    void testIsVegetarian_WithSeafood() {
        // Arrange
        testRecipe.addIngredient(new Ingredient("Shrimp", 250, Unit.G, IngredientCategory.SEAFOOD));

        // Assert
        assertFalse(recipeService.isVegetarian(testRecipe),
                "Recipe with seafood should not be vegetarian");
    }

    @Test
    @DisplayName("Is Vegetarian: Recipe with vegan ingredients (should be vegetarian)")
    void testIsVegetarian_WithVeganIngredients() {
        // Arrange
        Recipe veganRecipe = new Recipe("Vegan Salad", "Healthy salad", Difficulty.EASY, 2);
        veganRecipe.addIngredient(new Ingredient("Lettuce", 200, Unit.G, IngredientCategory.VEGAN));
        veganRecipe.addIngredient(new Ingredient("Tomato", 150, Unit.G, IngredientCategory.VEGAN));

        // Assert
        assertTrue(recipeService.isVegetarian(veganRecipe),
                "Recipe with only vegan ingredients should be vegetarian");
    }

    // ===== Business Logic Method 3: calculateTotalCookingTime =====

    @Test
    @DisplayName("Calculate Total Cooking Time: Multiple steps")
    void testCalculateTotalCookingTime_MultipleSteps() {
        // Act
        int totalTime = recipeService.calculateTotalCookingTime(testRecipe);

        // Assert
        assertEquals(45, totalTime, "Total cooking time should be 10 + 20 + 15 = 45 minutes");
    }

    @Test
    @DisplayName("Calculate Total Cooking Time: Single step")
    void testCalculateTotalCookingTime_SingleStep() {
        // Arrange
        Recipe singleStepRecipe = new Recipe("Quick Recipe", "Fast food", Difficulty.EASY, 2);
        singleStepRecipe.addStep(new InstructionStep(1, "Quick step", 5));

        // Act
        int totalTime = recipeService.calculateTotalCookingTime(singleStepRecipe);

        // Assert
        assertEquals(5, totalTime, "Total cooking time should be 5 minutes");
    }

    @Test
    @DisplayName("Calculate Total Cooking Time: No steps")
    void testCalculateTotalCookingTime_NoSteps() {
        // Arrange
        Recipe noStepsRecipe = new Recipe("No Steps Recipe", "Description", Difficulty.EASY, 2);

        // Act
        int totalTime = recipeService.calculateTotalCookingTime(noStepsRecipe);

        // Assert
        assertEquals(0, totalTime, "Total cooking time should be 0 when there are no steps");
    }

    @Test
    @DisplayName("Calculate Total Cooking Time: Steps with zero duration")
    void testCalculateTotalCookingTime_ZeroDuration() {
        // Arrange
        Recipe zeroDurationRecipe = new Recipe("Zero Duration", "Test", Difficulty.EASY, 2);
        zeroDurationRecipe.addStep(new InstructionStep(1, "Instant step", 0));
        zeroDurationRecipe.addStep(new InstructionStep(2, "Another instant", 0));

        // Act
        int totalTime = recipeService.calculateTotalCookingTime(zeroDurationRecipe);

        // Assert
        assertEquals(0, totalTime, "Total cooking time should be 0 when all steps have zero duration");
    }
}