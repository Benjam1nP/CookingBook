package org.manager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("recipe/new")
public class RecipeFormView extends VerticalLayout {

    private final RecipeService recipeService;

    private final TextField nameField = new TextField("Recipe Name");
    private final TextArea descriptionField = new TextArea("Description");
    private final ComboBox<Difficulty> difficultyCombo = new ComboBox<>("Difficulty");
    private final IntegerField servingsField = new IntegerField("Servings");

    public RecipeFormView(RecipeService recipeService) {
        this.recipeService = recipeService;

        setSizeFull();
        setPadding(true);

        H2 title = new H2("Add New Recipe");

        FormLayout formLayout = new FormLayout();
        configureForm(formLayout);

        Button saveButton = new Button("Save Recipe");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveRecipe());

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(RecipeListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        add(title, formLayout, buttonLayout);
    }

    private void configureForm(FormLayout formLayout) {
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

    private void saveRecipe() {
        if (nameField.getValue() == null || nameField.getValue().isEmpty()) {
            Notification.show("Please enter a recipe name!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Recipe recipe = new Recipe(
                nameField.getValue(),
                descriptionField.getValue(),
                difficultyCombo.getValue(),
                servingsField.getValue()
        );

        recipeService.saveRecipe(recipe);

        Notification.show("Recipe saved successfully!", 3000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        getUI().ifPresent(ui -> ui.navigate(RecipeListView.class));
    }
}