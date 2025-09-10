package Client;

import Enums.Gender;
import Enums.NObeyesdad;
import Server.Entities.Patient;
import Server.Interfaces.DecisionTreeService;
import Server.Interfaces.PatientService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;

public class PatientsDashboardController {

    @FXML
    private Pane patients;

    @FXML
    private Pane predict;

    @FXML
    private Pane report;

    @FXML
    private Pane quit;

    @FXML
    private TextField searchField;

    @FXML
    private Button addBtn;

    @FXML
    private Button EditBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button importBtn;

    @FXML
    private TableView<Patient> patientTable;

    @FXML
    private TableColumn<Patient, Integer> id;

    @FXML
    private TableColumn<Patient, Gender> gender;

    @FXML
    private TableColumn<Patient, Float> age;

    @FXML
    private TableColumn<Patient, Float> height;

    @FXML
    private TableColumn<Patient, Float> weight;

    @FXML
    private TableColumn<Patient, NObeyesdad> diagnosis;

    private PatientService server;
    private ObservableList<Patient> patientList = FXCollections.observableArrayList();
    private FilteredList<Patient> filteredList;

    public void initialize() {
        try {
            addHoverEffect(patients, "#3498db", "#2c3e50");
            addHoverEffect(predict, "#3498db", "#2c3e50");
            addHoverEffect(report, "#3498db", "#2c3e50");
            addHoverEffect(quit, "#3498db", "#2c3e50");
            addHoverEffect(addBtn, "#2ecc71", "#27ae60");
            addHoverEffect(EditBtn, "#00a8ff", "#3498db");
            addHoverEffect(deleteBtn, "#ff5252", "#e74c3c");
            server = (PatientService) Naming.lookup("rmi://localhost:1099/PatientService");
            loadPatients();
            setupSearchFilter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshPatients() {
        loadPatients(); // Calls the function that retrieves and updates the patient list
    }

    private void loadPatients() {
        try {
            List<Patient> patients = server.getAllPatients();
            patientList.setAll(patients);

            // Créer une liste filtrée basée sur la liste originale
            filteredList = new FilteredList<>(patientList, p -> true);
            patientTable.setItems(filteredList);

            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
            age.setCellValueFactory(new PropertyValueFactory<>("age"));
            height.setCellValueFactory(new PropertyValueFactory<>("height"));
            weight.setCellValueFactory(new PropertyValueFactory<>("weight"));
            diagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPatients(newValue);
        });
    }

    private void filterPatients(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredList.setPredicate(p -> true); // Afficher tous les patients si le champ est vide
            return;
        }

        try {
            int searchId = Integer.parseInt(searchText.trim());
            filteredList.setPredicate(patient -> patient.getId() == searchId);
        } catch (NumberFormatException e) {
            filteredList.setPredicate(p -> false); // Masquer tous les patients si l'entrée n'est pas un nombre valide
        }
    }

    @FXML
    private void handleImportCSV() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(importBtn.getScene().getWindow());

        if (file != null) {

            try {

                byte[] csvData = Files.readAllBytes(file.toPath());

                // Assuming you have a reference to the RMI server
                server.insertPatientsFromCSV(csvData);

                // Refresh the table after import
                loadPatients();

                showAlert("Success", "CSV file imported successfully!");

            } catch (IOException e) {

                showAlert("Error", "Failed to import CSV: " + e.getMessage());

                e.printStackTrace();

            }
        }
    }


    private void addHoverEffect(Region node, String hoverColor, String normalColor) {
        node.setOnMouseEntered(event -> node.setStyle("-fx-background-color: " + hoverColor + ";"));
        node.setOnMouseExited(event -> node.setStyle("-fx-background-color: " + normalColor + ";"));
    }


    @FXML
    private void deleteSelectedPatient() {

        // Get the selected patient
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();

        if (selectedPatient == null) {
            showAlert("Warning", "Please select a patient to delete.");
            return;
        }

        int patientId = selectedPatient.getId();

        // Confirm before deleting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this patient?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {

                // Call the delete method on the server

                server.deletePatient(patientId);

                // Refresh the table
                loadPatients();

                showAlert("Success", "Patient deleted successfully!");

            } catch (RemoteException e) {

                showAlert("Error", "Failed to delete patient: " + e.getMessage());

                e.printStackTrace();

            }
        }
    }

    private void showAlert(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();

    }

    @FXML
    private void handleAddPatientButton(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/AddPatient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void switchToModelReport(MouseEvent event) {

        try {

            DecisionTreeService service = (DecisionTreeService) Naming.lookup("rmi://localhost/DecisionTreeService");

            boolean isNotEmpty = service.isModelFolderNotEmpty();

            if (isNotEmpty){

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/ModelReport.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(new Scene(root));
                stage.show();

            }
            else{

                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("Warning: No Model Found");

                alert.setHeaderText("No trained model available!");

                alert.setContentText("The model folder is empty. You cannot make predictions or view model statistics.");

                alert.showAndWait();

            }


        } catch (IOException e) {

            e.printStackTrace();

        } catch (NotBoundException e) {

            throw new RuntimeException(e);

        }
    }

    @FXML
    private void switchToPatientsDashboard(MouseEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/PatientsDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void switchToPredictObesity(MouseEvent event) {

        try {

            DecisionTreeService service = (DecisionTreeService) Naming.lookup("rmi://localhost/DecisionTreeService");

            boolean isNotEmpty = service.isModelFolderNotEmpty();

            if (isNotEmpty){

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/PredictObesity.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(new Scene(root));
                stage.show();

            }
            else{

                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("Warning: No Model Found");

                alert.setHeaderText("No trained model available!");

                alert.setContentText("The model folder is empty. You cannot make predictions or view model statistics.");

                alert.showAndWait();

            }


        } catch (IOException e) {

            e.printStackTrace();

        } catch (NotBoundException e) {

            throw new RuntimeException(e);

        }

    }

    @FXML
    private void handleQuitApplication(MouseEvent event) {
        if (showConfirmationAlert("Confirm Exit", "Are you sure you want to quit the application?")) {
            Platform.exit(); // Close the application
        }
    }

    // Method to show a confirmation alert
    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Wait for user response
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    @FXML
    private void handleEditPatientButton(ActionEvent event) {
        try {

            // Get the selected patient from the TableView

            Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();

            if (selectedPatient == null) {

                showAlert("Warning", "Please select a patient to update.");

                return;

            }

            // Load UpdatePatient.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/UpdatePatient.fxml"));

            Parent root = loader.load();

            // Get controller and pass the patient data
            UpdatePatientController controller = loader.getController();

            controller.initData(selectedPatient);

            // Replace scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}