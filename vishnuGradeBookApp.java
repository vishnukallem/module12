// Modifications made:
// Added background colors to buttons and gridpane
// Improved error handling for file I/O operations
// Added error messages wherever necessary 
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;

public class vishnuGradeBookApp extends Application {

    private Label firstNameLabel = new Label("First Name:");
    private Label lastNameLabel = new Label("Last Name:");
    private Label courseLabel = new Label("Course:");
    private Label gradeLabel = new Label("Grade:");

    private TableView<Student> tableView = new TableView<>();
    private ObservableList<Student> students = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {

        // Creating text fields for user input
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField courseField = new TextField();
        ComboBox<String> gradeComboBox = new ComboBox<>();
        gradeComboBox.getItems().addAll("A", "B", "C", "D", "E", "F");

        // Creating buttons 
        Button clearButton = new Button("Clear");
        Button saveButton = new Button("Save");
        Button viewButton = new Button("View Saved Grades");

        // Adding background colors to the buttons
        clearButton.setStyle("-fx-background-color: #FF6F61; -fx-text-fill: white;");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        viewButton.setStyle("-fx-background-color: #62b6cb; -fx-text-fill: white;");

        // Setting up the form layout 
        GridPane formGridPane = new GridPane();
        formGridPane.setPadding(new Insets(20));
        formGridPane.setHgap(10);
        formGridPane.setVgap(10);
        formGridPane.setAlignment(Pos.CENTER);
        formGridPane.setStyle("-fx-background-color: #ADD8E6;");

        // Adding form fields to the grid
        formGridPane.add(firstNameLabel, 0, 0);
        formGridPane.add(firstNameField, 1, 0);
        formGridPane.add(lastNameLabel, 0, 1);
        formGridPane.add(lastNameField, 1, 1);
        formGridPane.add(courseLabel, 0, 2);
        formGridPane.add(courseField, 1, 2);
        formGridPane.add(gradeLabel, 0, 3);
        formGridPane.add(gradeComboBox, 1, 3);

        // Adding buttons to the grid
        HBox buttonRow = new HBox(10);
        buttonRow.getChildren().addAll(clearButton, saveButton);
        buttonRow.setAlignment(Pos.CENTER);
        formGridPane.add(buttonRow, 1, 4);

        HBox viewButtonRow = new HBox(10);
        viewButtonRow.getChildren().addAll(viewButton);
        viewButtonRow.setAlignment(Pos.CENTER);
        formGridPane.add(viewButtonRow, 1, 5);

        // Centering the elements
        for (int i = 0; i < 4; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            formGridPane.getColumnConstraints().add(columnConstraints);
        }

        // Adding ScrollPane to the table
        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(600);

        // VBox to hold the form and table
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(formGridPane, scrollPane);
        vbox.setAlignment(Pos.CENTER);

        // Setting background color for the application
        vbox.setStyle("-fx-background-color: lightblue;");

        // Scene and stage setup
        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setTitle("Grade Book App");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Clear button functionality
        clearButton.setOnAction(event -> clearForm(firstNameField, lastNameField, courseField, gradeComboBox));

        // Save button functionality
        saveButton.setOnAction(event -> saveGrade(firstNameField, lastNameField, courseField, gradeComboBox));

        // View button functionality
        viewButton.setOnAction(event -> viewSavedGrades());
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Method to clear the form fields
    private void clearForm(TextField firstNameField, TextField lastNameField, TextField courseField, ComboBox<String> gradeComboBox) {
        firstNameField.clear();
        lastNameField.clear();
        courseField.clear();
        gradeComboBox.getSelectionModel().clearSelection();
    }

    // Method to save the grade to a CSV file
    private void saveGrade(TextField firstNameField, TextField lastNameField, TextField courseField, ComboBox<String> gradeComboBox) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String course = courseField.getText();
        String grade = gradeComboBox.getValue();

        // Validate form fields before saving
        if (firstName.isEmpty() || lastName.isEmpty() || course.isEmpty() || grade == null) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all the fields");
            return;
        }

        Student student = new Student(firstName, lastName, course, grade);

        File file = new File("grades.csv");
        boolean fileExists = file.exists();

        // Write student data to the CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                writer.write("FirstName,LastName,Course,Grade");
                writer.newLine();
            }
            writer.write(student.toString());
            writer.newLine();
            showAlert(Alert.AlertType.INFORMATION, "Success!", "Grade saved successfully.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "An error occurred while saving the grade.");
            e.printStackTrace();
        }
    }

    // Method to view saved grades from the CSV file
    private void viewSavedGrades() {
        students.clear();

        if (tableView.getColumns().isEmpty()) {
            // Adding Table columns
            TableColumn<Student, String> firstNameColumn = new TableColumn<>("First Name");
            firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

            TableColumn<Student, String> lastNameColumn = new TableColumn<>("Last Name");
            lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

            TableColumn<Student, String> courseColumn = new TableColumn<>("Course");
            courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));

            TableColumn<Student, String> gradeColumn = new TableColumn<>("Grade");
            gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

            tableView.getColumns().addAll(firstNameColumn, lastNameColumn, courseColumn, gradeColumn);

            // Setting width for the table columns
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        }

        // Reading student data from the CSV file
        try (BufferedReader reader = new BufferedReader(new FileReader("grades.csv"))) {
            String line = reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    students.add(new Student(data[0], data[1], data[2], data[3]));
                }
            }
            tableView.setItems(students);
        } catch (FileNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "The grades file was not found.");
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "An error occurred while reading the grades file.");
            e.printStackTrace();
        }
    }

    // Method to show alert messages
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
