package org.manager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.Optional;

@Route("recipe")
public class RecipeDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RecipeService recipeService;
    private Recipe currentRecipe;

    private final H2 title = new H2();
    private final Paragraph description = new Paragraph();
    private final VerticalLayout detailsLayout = new VerticalLayout();
    private final VerticalLayout ingredientsLayout = new VerticalLayout();
    private final VerticalLayout stepsLayout = new VerticalLayout();
    private final VerticalLayout nutritionLayout = new VerticalLayout();

    public RecipeDetailView(RecipeService recipeService) {
        this.recipeService = recipeService;

        setSizeFull();
        setPadding(true);

        Button backButton = new Button("← Back to List");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(RecipeListView.class)));

        add(backButton, title, description);
    }

    @Override
    public void setParameter(BeforeEvent event, Long recipeId) {
        Optional<Recipe> recipeOpt = recipeService.getRecipeById(recipeId);

        if (recipeOpt.isPresent()) {
            currentRecipe = recipeOpt.get();
            displayRecipe();
        } else {
            Notification.show("Recipe not found!", 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate(RecipeListView.class));
        }
    }

    private void displayRecipe() {
        removeAll();

        Button backButton = new Button("← Back to List");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(RecipeListView.class)));

        title.setText(currentRecipe.getName());
        description.setText(currentRecipe.getDescription());

        // Details section
        detailsLayout.removeAll();
        detailsLayout.add(new H3("Details"));
        detailsLayout.add(new Span("Difficulty: " + currentRecipe.getDifficulty()));
        detailsLayout.add(new Span("Servings: " + currentRecipe.getServings()));

        // Business Logic Buttons
        HorizontalLayout businessLogicButtons = createBusinessLogicButtons();

        // Ingredients section
        ingredientsLayout.removeAll();
        ingredientsLayout.add(new H3("Ingredients (" + currentRecipe.getIngredients().size() + ")"));
        for (Ingredient ing : currentRecipe.getIngredients()) {
            Span ingSpan = new Span(ing.getQuantity() + " " + ing.getUnit() + " " + ing.getName() +
                    " (" + ing.getCategory() + ")");
            ingredientsLayout.add(ingSpan);
        }

        // Steps section
        stepsLayout.removeAll();
        stepsLayout.add(new H3("Cooking Instructions"));
        for (InstructionStep step : currentRecipe.getSteps()) {
            VerticalLayout stepLayout = new VerticalLayout();
            stepLayout.setPadding(false);
            H3 stepTitle = new H3("Step " + step.getStepNumber());
            Span stepDesc = new Span(step.getDescription());
            Span stepDuration = new Span("Duration: " + step.getDurationMinutes() + " minutes");
            stepDuration.getStyle().set("color", "var(--lumo-secondary-text-color)");
            stepLayout.add(stepTitle, stepDesc, stepDuration);
            stepsLayout.add(stepLayout);
        }

        // Nutrition section
        nutritionLayout.removeAll();
        if (currentRecipe.getNutritionalInfo() != null) {
            nutritionLayout.add(new H3("Nutritional Information (per serving)"));
            NutritionalInfo info = currentRecipe.getNutritionalInfo();
            nutritionLayout.add(new Span("Calories: " + info.getCalories() + " kcal"));
            nutritionLayout.add(new Span("Protein: " + info.getProtein() + " g"));
            nutritionLayout.add(new Span("Fat: " + info.getFat() + " g"));
            nutritionLayout.add(new Span("Carbohydrates: " + info.getCarbohydrates() + " g"));
        }

        add(backButton, title, description, businessLogicButtons, detailsLayout,
            ingredientsLayout, stepsLayout, nutritionLayout);
    }

    private HorizontalLayout createBusinessLogicButtons() {
        // Button 1: Calculate Portions
        Button calculatePortionsBtn = new Button("Adjust Portions");
        calculatePortionsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        calculatePortionsBtn.addClickListener(e -> showPortionsDialog());

        // Button 2: Check Vegetarian
        Button checkVegetarianBtn = new Button("Check Vegetarian");
        checkVegetarianBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        checkVegetarianBtn.addClickListener(e -> checkVegetarian());

        // Button 3: Show Cooking Time
        Button showCookingTimeBtn = new Button("Total Cooking Time");
        checkVegetarianBtn.addClickListener(e -> showTotalCookingTime());

        return new HorizontalLayout(calculatePortionsBtn, checkVegetarianBtn, showCookingTimeBtn);
    }

    private void showPortionsDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Adjust Portions");

        IntegerField servingsField = new IntegerField("New number of servings");
        servingsField.setValue(currentRecipe.getServings());
        servingsField.setMin(1);
        servingsField.setMax(100);
        servingsField.setStepButtonsVisible(true);

        Button calculateButton = new Button("Calculate", event -> {
            int newServings = servingsField.getValue();
            recipeService.calculatePortions(currentRecipe, newServings);
            dialog.close();
            displayRecipe();
            Notification.show("Portions adjusted to " + newServings + " servings!",
                    3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        calculateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        dialog.add(new VerticalLayout(servingsField));
        dialog.getFooter().add(cancelButton, calculateButton);
        dialog.open();
    }

    private void checkVegetarian() {
        boolean isVegetarian = recipeService.isVegetarian(currentRecipe);

        Notification notification;
        if (isVegetarian) {
            notification = Notification.show("✓ This recipe is VEGETARIAN!",
                    3000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            notification = Notification.show("⚠ Warning: This recipe is NOT vegetarian!",
                    4000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showTotalCookingTime() {
        int totalMinutes = recipeService.calculateTotalCookingTime(currentRecipe);
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        String timeText = totalMinutes > 0
                ? String.format("Total cooking time: %d hours %d minutes", hours, minutes)
                : "Total cooking time: " + minutes + " minutes";

        Notification.show(timeText, 4000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }
}