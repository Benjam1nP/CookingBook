package org.manager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(RecipeRepository recipeRepository) {
        return args -> {
            // Recipe 1: Spaghetti Carbonara (Vegetarian version without meat)
            Recipe carbonara = new Recipe(
                    "Spaghetti Carbonara",
                    "A delicious Italian pasta dish with eggs, cheese, and vegetables.",
                    Difficulty.MEDIUM,
                    4
            );

            carbonara.addIngredient(new Ingredient("Spaghetti", 400, Unit.G, IngredientCategory.VEGETARIAN));
            carbonara.addIngredient(new Ingredient("Eggs", 4, Unit.PCS, IngredientCategory.VEGETARIAN));
            carbonara.addIngredient(new Ingredient("Parmesan Cheese", 100, Unit.G, IngredientCategory.VEGETARIAN));
            carbonara.addIngredient(new Ingredient("Black Pepper", 5, Unit.G, IngredientCategory.VEGETARIAN));
            carbonara.addIngredient(new Ingredient("Salt", 10, Unit.G, IngredientCategory.VEGETARIAN));

            carbonara.addStep(new InstructionStep(1, "Bring a large pot of salted water to boil.", 10));
            carbonara.addStep(new InstructionStep(2, "Cook spaghetti according to package directions.", 10));
            carbonara.addStep(new InstructionStep(3, "Beat eggs and mix with grated Parmesan cheese.", 5));
            carbonara.addStep(new InstructionStep(4, "Drain pasta and immediately mix with egg mixture.", 5));
            carbonara.addStep(new InstructionStep(5, "Season with black pepper and serve hot.", 2));

            NutritionalInfo carbInfo = new NutritionalInfo(520, 18.5, 15.2, 72.3);
            carbonara.setNutritionalInfo(carbInfo);

            recipeRepository.save(carbonara);

            // Recipe 2: Chicken Tikka Masala (Non-vegetarian)
            Recipe tikka = new Recipe(
                    "Chicken Tikka Masala",
                    "A popular Indian curry dish with tender chicken in creamy tomato sauce.",
                    Difficulty.HARD,
                    6
            );

            tikka.addIngredient(new Ingredient("Chicken Breast", 800, Unit.G, IngredientCategory.POULTRY));
            tikka.addIngredient(new Ingredient("Yogurt", 200, Unit.ML, IngredientCategory.VEGETARIAN));
            tikka.addIngredient(new Ingredient("Tomato Sauce", 400, Unit.ML, IngredientCategory.VEGAN));
            tikka.addIngredient(new Ingredient("Heavy Cream", 200, Unit.ML, IngredientCategory.VEGETARIAN));
            tikka.addIngredient(new Ingredient("Garam Masala", 20, Unit.G, IngredientCategory.VEGAN));
            tikka.addIngredient(new Ingredient("Ginger", 30, Unit.G, IngredientCategory.VEGAN));
            tikka.addIngredient(new Ingredient("Garlic", 40, Unit.G, IngredientCategory.VEGAN));

            tikka.addStep(new InstructionStep(1, "Marinate chicken in yogurt and spices for 2 hours.", 120));
            tikka.addStep(new InstructionStep(2, "Grill or bake marinated chicken until cooked through.", 25));
            tikka.addStep(new InstructionStep(3, "Prepare sauce by cooking tomatoes, cream, and spices.", 20));
            tikka.addStep(new InstructionStep(4, "Add grilled chicken to the sauce and simmer.", 15));
            tikka.addStep(new InstructionStep(5, "Garnish with cilantro and serve with rice or naan.", 5));

            NutritionalInfo tikkaInfo = new NutritionalInfo(380, 32.0, 22.5, 12.8);
            tikka.setNutritionalInfo(tikkaInfo);

            recipeRepository.save(tikka);

            // Recipe 3: Greek Salad (Vegetarian)
            Recipe greekSalad = new Recipe(
                    "Greek Salad",
                    "A refreshing Mediterranean salad with fresh vegetables and feta cheese.",
                    Difficulty.EASY,
                    4
            );

            greekSalad.addIngredient(new Ingredient("Tomatoes", 400, Unit.G, IngredientCategory.VEGAN));
            greekSalad.addIngredient(new Ingredient("Cucumber", 300, Unit.G, IngredientCategory.VEGAN));
            greekSalad.addIngredient(new Ingredient("Red Onion", 100, Unit.G, IngredientCategory.VEGAN));
            greekSalad.addIngredient(new Ingredient("Feta Cheese", 200, Unit.G, IngredientCategory.VEGETARIAN));
            greekSalad.addIngredient(new Ingredient("Olives", 150, Unit.G, IngredientCategory.VEGAN));
            greekSalad.addIngredient(new Ingredient("Olive Oil", 60, Unit.ML, IngredientCategory.VEGAN));
            greekSalad.addIngredient(new Ingredient("Oregano", 5, Unit.G, IngredientCategory.VEGAN));

            greekSalad.addStep(new InstructionStep(1, "Chop tomatoes, cucumber, and onion into bite-sized pieces.", 10));
            greekSalad.addStep(new InstructionStep(2, "Combine vegetables in a large bowl.", 3));
            greekSalad.addStep(new InstructionStep(3, "Add olives and crumbled feta cheese.", 5));
            greekSalad.addStep(new InstructionStep(4, "Drizzle with olive oil and sprinkle oregano.", 2));
            greekSalad.addStep(new InstructionStep(5, "Toss gently and serve immediately.", 2));

            NutritionalInfo saladInfo = new NutritionalInfo(220, 8.5, 16.8, 12.5);
            greekSalad.setNutritionalInfo(saladInfo);

            recipeRepository.save(greekSalad);

            // Recipe 4: Grilled Salmon (Non-vegetarian with fish)
            Recipe salmon = new Recipe(
                    "Grilled Salmon with Lemon",
                    "Perfectly grilled salmon fillets with a fresh lemon butter sauce.",
                    Difficulty.MEDIUM,
                    2
            );

            salmon.addIngredient(new Ingredient("Salmon Fillets", 400, Unit.G, IngredientCategory.FISH));
            salmon.addIngredient(new Ingredient("Lemon", 2, Unit.PCS, IngredientCategory.VEGAN));
            salmon.addIngredient(new Ingredient("Butter", 50, Unit.G, IngredientCategory.VEGETARIAN));
            salmon.addIngredient(new Ingredient("Garlic", 20, Unit.G, IngredientCategory.VEGAN));
            salmon.addIngredient(new Ingredient("Fresh Dill", 10, Unit.G, IngredientCategory.VEGAN));

            salmon.addStep(new InstructionStep(1, "Preheat grill to medium-high heat.", 10));
            salmon.addStep(new InstructionStep(2, "Season salmon with salt, pepper, and lemon juice.", 5));
            salmon.addStep(new InstructionStep(3, "Grill salmon for 4-5 minutes per side.", 10));
            salmon.addStep(new InstructionStep(4, "Melt butter with garlic and fresh dill.", 5));
            salmon.addStep(new InstructionStep(5, "Drizzle butter sauce over grilled salmon and serve.", 2));

            NutritionalInfo salmonInfo = new NutritionalInfo(320, 28.0, 22.0, 2.5);
            salmon.setNutritionalInfo(salmonInfo);

            recipeRepository.save(salmon);

            System.out.println("Sample data initialized successfully!");
        };
    }
}