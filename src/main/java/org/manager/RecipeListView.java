package org.manager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
public class RecipeListView extends VerticalLayout {

    private final RecipeService recipeService;
    private final Grid<Recipe> grid = new Grid<>(Recipe.class, false);
    private final TextField searchField = new TextField("Search");

    public RecipeListView(RecipeService recipeService) {
        this.recipeService = recipeService;

        setSizeFull();
        configureGrid();

        add(
            getToolbar(),
            grid
        );

        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Recipe::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Recipe::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Recipe::getDifficulty).setHeader("Difficulty").setAutoWidth(true);
        grid.addColumn(Recipe::getServings).setHeader("Servings").setAutoWidth(true);
        grid.addColumn(recipe -> recipe.getIngredients().size())
             .setHeader("# Ingredients").setAutoWidth(true);
        grid.addColumn(recipe -> recipe.getSteps().size())
             .setHeader("# Steps").setAutoWidth(true);

        grid.asSingleSelect().addValueChangeListener(event -> {
            Recipe selected = event.getValue();
            if (selected != null) {
                getUI().ifPresent(ui -> ui.navigate(RecipeDetailView.class, selected.getId()));
            }
        });
    }

    private HorizontalLayout getToolbar() {
        searchField.setPlaceholder("Search by name...");
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> updateList());

        Button addButton = new Button("Add Recipe");
        addButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(RecipeFormView.class));
        });

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addButton, refreshButton);
        toolbar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        return toolbar;
    }

    private void updateList() {
        String searchTerm = searchField.getValue();
        if (searchTerm == null || searchTerm.isEmpty()) {
            grid.setItems(recipeService.getAllRecipes());
        } else {
            grid.setItems(recipeService.searchRecipesByName(searchTerm));
        }
    }
}