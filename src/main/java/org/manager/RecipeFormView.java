package org.manager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("recipe/new")
public class RecipeFormView extends VerticalLayout {

    private final RecipeService recipeService;

    // Basic recipe fields
    private final TextField nameField = new TextField("Recipe Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final ComboBox<Difficulty> difficultyCombo = new ComboBox<>("Difficulty");
    private final IntegerField servingsField = new IntegerField("Servings");

    // Ingredient fields
    private final List<IngredientRow> ingredientRows = new ArrayList<>();
    private final VerticalLayout ingredientsContainer = new VerticalLayout();

    // Step fields
    private final List<StepRow> stepRows = new ArrayList<>();
    private final VerticalLayout stepsContainer = new VerticalLayout();

    // Nutritional info fields
    private final IntegerField caloriesField = new IntegerField("Calories (per serving)");
    private final NumberField proteinField = new NumberField("Protein (g)");
    private final NumberField fatField = new NumberField("Fat (g)");
    private final NumberField carbsField = new NumberField("Carbohydrates (g)");

    public RecipeFormView(RecipeService recipeService) {
        this.recipeService = recipeService;

        setSizeFull();
        setPadding(true);

        H2 title = new H2("Add New Recipe");

        // Basic info section
        FormLayout basicInfoForm = new FormLayout();
        configureBasicInfoForm(basicInfoForm);

        // Ingredients section
        H3 ingredientsTitle = new H3("Ingredients");
        Button addIngredientBtn = new Button("+ Add Ingredient");
        addIngredientBtn.addClickListener(e -> addIngredientRow());
        addIngredientBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

        ingredientsContainer.setPadding(false);
        ingredientsContainer.setSpacing(true);
        addIngredientRow(); // Add first row by default

        // Steps section
        H3 stepsTitle = new H3("Cooking Instructions");
        Button addStepBtn = new Button("+ Add Step");
        addStepBtn.addClickListener(e -> addStepRow());
        addStepBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

        stepsContainer.setPadding(false);
        stepsContainer.setSpacing(true);
        addStepRow(); // Add first row by default

        // Nutritional info section
        H3 nutritionTitle = new H3("Nutritional Information (Optional)");
        FormLayout nutritionForm = new FormLayout();
        configureNutritionForm(nutritionForm);

        // Action buttons
        Button saveButton = new Button("Save Recipe");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveRecipe());

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(RecipeListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        add(title, basicInfoForm,
            ingredientsTitle, addIngredientBtn, ingredientsContainer,
            stepsTitle, addStepBtn, stepsContainer,
            nutritionTitle, nutritionForm,
            buttonLayout);
    }

    private void configureBasicInfoForm(FormLayout formLayout) {
        nameField.setRequired(true);
        nameField.setPlaceholder("e.g., Spaghetti Carbonara");

        descriptionField.setPlaceholder("Describe your recipe...");
        descriptionField.setHeight("150px");

        difficultyCombo.setItems(Difficulty.values());
        difficultyCombo.setValue(Difficulty.MEDIUM);

        servingsField.setValue(4);
        servingsField.setMin(1);
        servingsField.setMax(50);
        servingsField.setStepButtonsVisible(true);

        formLayout.add(nameField, difficultyCombo, servingsField, descriptionField);
        formLayout.setColspan(descriptionField, 2);
    }

    private void configureNutritionForm(FormLayout formLayout) {
        caloriesField.setMin(0);
        caloriesField.setPlaceholder("e.g., 350");

        proteinField.setMin(0);
        proteinField.setPlaceholder("e.g., 15.5");

        fatField.setMin(0);
        fatField.setPlaceholder("e.g., 12.0");

        carbsField.setMin(0);
        carbsField.setPlaceholder("e.g., 45.0");

        formLayout.add(caloriesField, proteinField, fatField, carbsField);
    }

    private void addIngredientRow() {
        IngredientRow[] rowHolder = new IngredientRow[1];
        rowHolder[0] = new IngredientRow(() -> removeIngredientRow(rowHolder[0]));
        ingredientRows.add(rowHolder[0]);
        ingredientsContainer.add(rowHolder[0]);
    }

    private void removeIngredientRow(IngredientRow row) {
        ingredientRows.remove(row);
        ingredientsContainer.remove(row);
    }

    private void addStepRow() {
        int stepNumber = stepRows.size() + 1;
        StepRow[] rowHolder = new StepRow[1];
        rowHolder[0] = new StepRow(stepNumber, () -> removeStepRow(rowHolder[0]));
        stepRows.add(rowHolder[0]);
        stepsContainer.add(rowHolder[0]);
    }

    private void removeStepRow(StepRow row) {
        stepRows.remove(row);
        stepsContainer.remove(row);
        // Renumber remaining steps
        for (int i = 0; i < stepRows.size(); i++) {
            stepRows.get(i).setStepNumber(i + 1);
        }
    }

    private void saveRecipe() {
        // Validate basic info
        if (nameField.getValue() == null || nameField.getValue().isEmpty()) {
            Notification.show("Please enter a recipe name!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Create recipe
        Recipe recipe = new Recipe(
                nameField.getValue(),
                descriptionField.getValue(),
                difficultyCombo.getValue(),
                servingsField.getValue()
        );

        // Add ingredients
        for (IngredientRow row : ingredientRows) {
            if (row.isValid()) {
                Ingredient ingredient = new Ingredient(
                        row.getName(),
                        row.getQuantity(),
                        row.getUnit(),
                        row.getCategory()
                );
                recipe.addIngredient(ingredient);
            }
        }

        // Validate at least one ingredient
        if (recipe.getIngredients().isEmpty()) {
            Notification.show("Please add at least one ingredient!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Add steps
        for (StepRow row : stepRows) {
            if (row.isValid()) {
                InstructionStep step = new InstructionStep(
                        row.getStepNumber(),
                        row.getDescription(),
                        row.getDurationMinutes()
                );
                recipe.addStep(step);
            }
        }

        // Validate at least one step
        if (recipe.getSteps().isEmpty()) {
            Notification.show("Please add at least one cooking step!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Add nutritional info if provided
        if (caloriesField.getValue() != null && caloriesField.getValue() > 0) {
            NutritionalInfo nutritionalInfo = new NutritionalInfo(
                    caloriesField.getValue(),
                    proteinField.getValue() != null ? proteinField.getValue() : 0.0,
                    fatField.getValue() != null ? fatField.getValue() : 0.0,
                    carbsField.getValue() != null ? carbsField.getValue() : 0.0
            );
            recipe.setNutritionalInfo(nutritionalInfo);
        }

        recipeService.saveRecipe(recipe);

        Notification.show("Recipe saved successfully!", 3000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        getUI().ifPresent(ui -> ui.navigate(RecipeListView.class));
    }

    // Inner class for ingredient row
    private static class IngredientRow extends HorizontalLayout {
        private final TextField nameField = new TextField();
        private final IntegerField quantityField = new IntegerField();
        private final ComboBox<Unit> unitCombo = new ComboBox<>();
        private final ComboBox<IngredientCategory> categoryCombo = new ComboBox<>();

        public IngredientRow(Runnable onRemove) {
            setAlignItems(Alignment.END);
            setSpacing(true);

            nameField.setPlaceholder("Ingredient name");
            nameField.setWidth("200px");

            quantityField.setPlaceholder("Qty");
            quantityField.setWidth("80px");
            quantityField.setMin(1);
            quantityField.setValue(1);
            quantityField.setStepButtonsVisible(true);

            unitCombo.setItems(Unit.values());
            unitCombo.setValue(Unit.G);
            unitCombo.setWidth("100px");

            categoryCombo.setItems(IngredientCategory.values());
            categoryCombo.setValue(IngredientCategory.OTHER);
            categoryCombo.setWidth("150px");

            Button removeBtn = new Button("Remove");
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            removeBtn.addClickListener(e -> onRemove.run());

            add(nameField, quantityField, unitCombo, categoryCombo, removeBtn);
        }

        public boolean isValid() {
            return nameField.getValue() != null && !nameField.getValue().isEmpty()
                    && quantityField.getValue() != null && quantityField.getValue() > 0;
        }

        public String getName() {
            return nameField.getValue();
        }

        public long getQuantity() {
            return quantityField.getValue().longValue();
        }

        public Unit getUnit() {
            return unitCombo.getValue();
        }

        public IngredientCategory getCategory() {
            return categoryCombo.getValue();
        }
    }

    // Inner class for step row
    private static class StepRow extends VerticalLayout {
        private final Span stepLabel = new Span();
        private int stepNumber;
        private final TextArea descriptionField = new TextArea();
        private final IntegerField durationField = new IntegerField();

        public StepRow(int stepNumber, Runnable onRemove) {
            this.stepNumber = stepNumber;
            setPadding(true);
            setSpacing(true);
            getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                      .set("border-radius", "var(--lumo-border-radius-m)");

            stepLabel.setText("Step " + stepNumber);
            stepLabel.getStyle().set("font-weight", "bold");

            descriptionField.setPlaceholder("Describe this step...");
            descriptionField.setWidthFull();
            descriptionField.setHeight("80px");

            durationField.setPlaceholder("Duration in minutes");
            durationField.setWidth("200px");
            durationField.setMin(0);
            durationField.setValue(10);
            durationField.setStepButtonsVisible(true);

            Button removeBtn = new Button("Remove Step");
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            removeBtn.addClickListener(e -> onRemove.run());

            HorizontalLayout durationLayout = new HorizontalLayout(durationField, removeBtn);
            durationLayout.setAlignItems(Alignment.END);

            add(stepLabel, descriptionField, durationLayout);
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
            stepLabel.setText("Step " + stepNumber);
        }

        public boolean isValid() {
            return descriptionField.getValue() != null && !descriptionField.getValue().isEmpty();
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public String getDescription() {
            return descriptionField.getValue();
        }

        public int getDurationMinutes() {
            return durationField.getValue() != null ? durationField.getValue() : 0;
        }
    }
}
