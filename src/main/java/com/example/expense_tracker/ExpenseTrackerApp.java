package com.example.expense_tracker;

import com.example.expense_tracker.dao.ExpenseDao;
import com.example.expense_tracker.model.Expense;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ExpenseTrackerApp extends Application {

    private ExpenseDao expenseDao = new ExpenseDao();
    private ObservableList<Expense> expenses = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        VBox expenseForm = createExpenseForm();
        TableView<Expense> expenseTable = createExpenseTable();
        PieChart expenseChart = createExpenseChart();

        root.setLeft(expenseForm);
        root.setCenter(expenseTable);
        root.setBottom(expenseChart);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Expense Tracker");
        primaryStage.show();

        refreshData();
    }

    private VBox createExpenseForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));

        TextField categoryField = new TextField();
        categoryField.setPromptText("Категория");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Описание");
        TextField amountField = new TextField();
        amountField.setPromptText("Сумма");

        Button addButton = new Button("Добавить");
        addButton.setOnAction(e -> {
            String category = categoryField.getText();
            String description = descriptionField.getText();
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Ошибка", "Введите корректную сумму!");
                return;
            }

            expenseDao.save(new Expense(category, description, amount));
            refreshData();

            categoryField.clear();
            descriptionField.clear();
            amountField.clear();
        });

        form.getChildren().addAll(new Label("Добавить расходы"), categoryField, descriptionField, amountField, addButton);
        return form;
    }

    private TableView<Expense> createExpenseTable() {
        TableView<Expense> table = new TableView<>();
        table.setItems(expenses);

        TableColumn<Expense, String> categoryCol = new TableColumn<>("Категория");
        categoryCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));

        TableColumn<Expense, String> descriptionCol = new TableColumn<>("Описание");
        descriptionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Expense, Double> amountCol = new TableColumn<>("Сумма");
        amountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAmount()).asObject());

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(e -> {
            Expense selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                expenseDao.delete(selected.getId());
                refreshData();
            }
        });

        table.getColumns().addAll(categoryCol, descriptionCol, amountCol);
        return table;
    }

    private PieChart createExpenseChart() {
        PieChart chart = new PieChart();
        chart.setTitle("Категории расходов");

        expenses.addListener((javafx.collections.ListChangeListener<Expense>) change -> {
            chart.getData().clear();
            expenses.stream()
                    .collect(java.util.stream.Collectors.groupingBy(Expense::getCategory, java.util.stream.Collectors.summingDouble(Expense::getAmount)))
                    .forEach((category, amount) -> chart.getData().add(new PieChart.Data(category, amount)));
        });

        return chart;
    }

    private void refreshData() {
        expenses.setAll(expenseDao.findAll());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}