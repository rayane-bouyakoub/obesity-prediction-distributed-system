package Client;

import Enums.*;
import Server.Entities.Patient;
import Server.Interfaces.DecisionTreeService;
import Server.Interfaces.ObesityPrediction;
import Server.Interfaces.PatientService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class PredictObesityController implements Initializable {

    @FXML
    private Pane patients;

    @FXML
    private Pane predict;

    @FXML
    private Pane report;

    @FXML
    private Pane quit;

    @FXML
    private Button predictBtn;

    @FXML
    private TextField age;

    @FXML
    private TextField height;

    @FXML
    private TextField weight;


    @FXML
    private TextField faf;

    @FXML
    private TextField tue;

    @FXML
    private TextField fcvc;


    @FXML
    private TextField ncp;


    @FXML
    private TextField ch2o;


    @FXML
    private ChoiceBox<CAEC> caec;

    @FXML
    private ChoiceBox<CALC> calc;


    @FXML
    private ChoiceBox<family_history_overweight> fho;

    @FXML
    private ChoiceBox<FAVC> favc;


    @FXML
    private ChoiceBox<Gender> gender;


    @FXML
    private ChoiceBox<MTRANS> mtrans;

    @FXML
    private ChoiceBox<SCC> scc;

    @FXML
    private ChoiceBox<SMOKE> smoke;

    private ObesityPrediction server;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gender.getItems().addAll(Gender.values());
        caec.getItems().addAll(CAEC.values());
        calc.getItems().addAll(CALC.values());
        fho.getItems().addAll(family_history_overweight.values());
        favc.getItems().addAll(FAVC.values());
        mtrans.getItems().addAll(MTRANS.values());
        scc.getItems().addAll(SCC.values());
        smoke.getItems().addAll(SMOKE.values());

        addHoverEffect(patients, "#3498db", "#2c3e50");
        addHoverEffect(predict, "#3498db", "#2c3e50");
        addHoverEffect(report, "#3498db", "#2c3e50");
        addHoverEffect(quit, "#3498db", "#2c3e50");
        addHoverEffect(predictBtn, "#00a8ff", "#3498db");

        try {

            server = (ObesityPrediction) Naming.lookup("rmi://localhost:1099/ObesityPredictionService");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void addHoverEffect(Region node, String hoverColor, String normalColor) {
        node.setOnMouseEntered(event -> node.setStyle("-fx-background-color: " + hoverColor + ";"));
        node.setOnMouseExited(event -> node.setStyle("-fx-background-color: " + normalColor + ";"));
    }


    private boolean areFieldsEmpty() {
        return age.getText().isEmpty() || height.getText().isEmpty() || weight.getText().isEmpty() ||
                fcvc.getText().isEmpty() || ncp.getText().isEmpty() || ch2o.getText().isEmpty() ||
                faf.getText().isEmpty() || tue.getText().isEmpty() ||
                gender.getValue() == null || fho.getValue() == null || favc.getValue() == null ||
                caec.getValue() == null || smoke.getValue() == null || scc.getValue() == null ||
                calc.getValue() == null || mtrans.getValue() == null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private void predictObesity() {

        if (areFieldsEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all fields.");
            return;
        }

        float age, height, weight, fcvc, ncp, ch2o, faf, tue;

        try {
            age = Float.parseFloat(this.age.getText());
            height = Float.parseFloat(this.height.getText());
            weight = Float.parseFloat(this.weight.getText());
            fcvc = Float.parseFloat(this.fcvc.getText());
            ncp = Float.parseFloat(this.ncp.getText());
            ch2o = Float.parseFloat(this.ch2o.getText());
            faf = Float.parseFloat(this.faf.getText());
            tue = Float.parseFloat(this.tue.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numerical values.");
            return;
        }

        try {
            // Call the remote method
            NObeyesdad obesityPrediction = server.predictObesity(
                    gender.getValue(),
                    age, height, weight,
                    fho.getValue(),
                    favc.getValue(),
                    fcvc, ncp,
                    caec.getValue(),
                    smoke.getValue(),
                    ch2o,
                    scc.getValue(),
                    faf, tue,
                    calc.getValue(),
                    mtrans.getValue()
            );

            // Display the predicted obesity level
            showAlert(Alert.AlertType.INFORMATION, "Prediction Result",
                    "The predicted obesity level is: " + obesityPrediction.toString());

        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Prediction Error", "Failed to retrieve prediction from the server.");
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

}
