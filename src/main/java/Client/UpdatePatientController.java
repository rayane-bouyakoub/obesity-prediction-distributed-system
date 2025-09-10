package Client;

import Enums.*;
import Server.Entities.Patient;
import Server.Interfaces.DecisionTreeService;
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

public class UpdatePatientController implements Initializable {

    @FXML
    private Pane patients;

    @FXML
    private Pane predict;

    @FXML
    private Pane report;

    @FXML
    private Pane quit;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

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
    private ChoiceBox<NObeyesdad> obesity;


    @FXML
    private ChoiceBox<SCC> scc;

    @FXML
    private ChoiceBox<SMOKE> smoke;


    @FXML
    private TextField id;

    private PatientService server;

    private Patient patient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gender.getItems().addAll(Gender.values());
        caec.getItems().addAll(CAEC.values());
        calc.getItems().addAll(CALC.values());
        fho.getItems().addAll(family_history_overweight.values());
        favc.getItems().addAll(FAVC.values());
        mtrans.getItems().addAll(MTRANS.values());
        obesity.getItems().addAll(NObeyesdad.values());
        scc.getItems().addAll(SCC.values());
        smoke.getItems().addAll(SMOKE.values());

        addHoverEffect(patients, "#3498db", "#2c3e50");
        addHoverEffect(predict, "#3498db", "#2c3e50");
        addHoverEffect(report, "#3498db", "#2c3e50");
        addHoverEffect(quit, "#3498db", "#2c3e50");
        addHoverEffect(save, "#00a8ff", "#3498db");
        addHoverEffect(cancel, "#CBD5E0", " #e0e6ed");


        try {

            server = (PatientService) Naming.lookup("rmi://localhost:1099/PatientService");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void addHoverEffect(Region node, String hoverColor, String normalColor) {
        node.setOnMouseEntered(event -> node.setStyle("-fx-background-color: " + hoverColor + ";"));
        node.setOnMouseExited(event -> node.setStyle("-fx-background-color: " + normalColor + ";"));
    }


    public void initData(Patient patient) {
        this.patient = patient;
        id.setText(String.valueOf(patient.getId()));
        age.setText(String.valueOf(patient.getAge()));
        height.setText(String.valueOf(patient.getHeight()));
        weight.setText(String.valueOf(patient.getWeight()));
        gender.setValue(patient.getGender());
        fho.setValue(patient.getFho());
        faf.setText(String.valueOf(patient.getFaf()));
        scc.setValue(patient.getScc());
        smoke.setValue(patient.getSmoke());
        mtrans.setValue(patient.getMtrans());
        obesity.setValue(patient.getDiagnosis());
        fcvc.setText(String.valueOf(patient.getFcvc()));
        ncp.setText(String.valueOf(patient.getNcp()));
        ch2o.setText(String.valueOf(patient.getCh2o()));
        favc.setValue(patient.getFavc());
        calc.setValue(patient.getCalc());
        tue.setText(String.valueOf(patient.getTue()));
        caec.setValue(patient.getCaec());
    }



    private boolean areFieldsEmpty() {
        return age.getText().isEmpty() || height.getText().isEmpty() || weight.getText().isEmpty() ||
                fcvc.getText().isEmpty() || ncp.getText().isEmpty() || ch2o.getText().isEmpty() ||
                faf.getText().isEmpty() || tue.getText().isEmpty() ||
                gender.getValue() == null || fho.getValue() == null || favc.getValue() == null ||
                caec.getValue() == null || smoke.getValue() == null || scc.getValue() == null ||
                calc.getValue() == null || mtrans.getValue() == null || obesity.getValue() == null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private void updatePatient() {

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

        patient.setAge(age);
        patient.setHeight(height);
        patient.setWeight(weight);
        patient.setFaf(faf);
        patient.setFcvc(fcvc);
        patient.setNcp(ncp);
        patient.setCh2o(ch2o);
        patient.setTue(tue);

        patient.setGender(gender.getValue());
        patient.setFho(fho.getValue());
        patient.setScc(scc.getValue());
        patient.setSmoke(smoke.getValue());
        patient.setMtrans(mtrans.getValue());
        patient.setNobeyesdad(obesity.getValue());
        patient.setFavc(favc.getValue());
        patient.setCalc(calc.getValue());
        patient.setCaec(caec.getValue());

        try {

            server.updatePatient(patient);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Patient with ID " + patient.getId() + " has been successfully updated.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/PatientsDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) id.getScene().getWindow(); // Get current window
            stage.setScene(new Scene(root));
            stage.show();


        } catch (RemoteException e) {

            e.printStackTrace();

        } catch (IOException e) {

            throw new RuntimeException(e);


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
    private void switchToPatientsDashboard2(ActionEvent event) {
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
