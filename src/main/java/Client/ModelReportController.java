package Client;

import Server.Interfaces.DecisionTreeService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ModelReportController {


    @FXML
    private Text accuracy;

    @FXML
    private Text precision;

    @FXML
    private Text recall;

    @FXML
    private Text f1;

    @FXML
    private LineChart<Number, Number> rocChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label aucLabel;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ImageView imageView;

    @FXML
    private StackPane imageContainer;

    @FXML
    private Pane patients;

    @FXML
    private Pane predict;

    @FXML
    private Pane report;

    @FXML
    private Pane quit;

    @FXML
    private Text a00;

    @FXML
    private BarChart<String, Number> classifReport;

    private DecisionTreeService decisionTreeService; // RMI interface

    private Parent root;

    @FXML
    public void initialize() {

        addHoverEffect(patients, "#3498db", "#2c3e50");
        addHoverEffect(predict, "#3498db", "#2c3e50");
        addHoverEffect(report, "#3498db", "#2c3e50");
        addHoverEffect(quit, "#3498db", "#2c3e50");

        root = a00.getParent();

        try {
            // Lookup RMI server
            decisionTreeService = (DecisionTreeService) Naming.lookup("rmi://localhost:1099/DecisionTreeService");

            // Fetch image bytes from server
            byte[] imageData = decisionTreeService.getDecisionTreeImage();

            if (imageData != null && imageData.length > 0) {
                // Convert byte[] to JavaFX Image
                Image image = new Image(new ByteArrayInputStream(imageData));
                imageView.setImage(image);

                // Set initial size of the ImageView
                imageView.setFitWidth(image.getWidth());
                imageView.setFitHeight(image.getHeight());

                // Ensure the StackPane resizes with the image
                imageContainer.setPrefSize(image.getWidth(), image.getHeight());
            } else {
                System.out.println("Failed to load image from server.");
            }


            byte[] evalData = decisionTreeService.getSerializedEvaluation();

            if (evalData != null && evalData.length > 0) {

                Evaluation eval = (Evaluation) SerializationHelper.read(new ByteArrayInputStream(evalData));

                // Generate ROC Curve and display it

                plotROCCurve(eval);


                double accuracyValue = eval.pctCorrect(); // Already in percentage
                double precisionValue = eval.weightedPrecision();
                double recallValue = eval.weightedRecall();
                double f1Value = eval.weightedFMeasure();

                accuracy.setText(String.format("%.1f%%", accuracyValue));
                precision.setText(String.format("%.1f%%", precisionValue*100));
                recall.setText(String.format("%.1f%%", recallValue*100));
                f1.setText(String.format("%.1f%%", f1Value*100));


                double[][] confusionMatrix = eval.confusionMatrix();

                updateConfusionMatrix(confusionMatrix);

                // Fill in the classification report

                CategoryAxis xAxis = (CategoryAxis) classifReport.getXAxis();
                NumberAxis yAxis = (NumberAxis) classifReport.getYAxis();

                xAxis.setLabel("Obesity Types");
                yAxis.setLabel("Score");

                // Extract classification metrics from eval
                List<String> classNames = new ArrayList<>();
                List<Double> precisions = new ArrayList<>();
                List<Double> recalls = new ArrayList<>();
                List<Double> fMeasures = new ArrayList<>();

                for (int i = 0; i < eval.getHeader().numClasses(); i++) {
                    classNames.add(eval.getHeader().classAttribute().value(i));  // Class name
                    precisions.add(eval.precision(i));
                    recalls.add(eval.recall(i));
                    fMeasures.add(eval.fMeasure(i));
                }

                // Create series for Precision, Recall, F-Measure
                XYChart.Series<String, Number> precisionSeries = new XYChart.Series<>();
                XYChart.Series<String, Number> recallSeries = new XYChart.Series<>();
                XYChart.Series<String, Number> fMeasureSeries = new XYChart.Series<>();

                precisionSeries.setName("Precision");
                recallSeries.setName("Recall");
                fMeasureSeries.setName("F-Measure");

                // Populate series
                for (int i = 0; i < classNames.size(); i++) {

                    double precision = Double.isNaN(precisions.get(i)) ? 0.0 : precisions.get(i);
                    double recall = Double.isNaN(recalls.get(i)) ? 0.0 : recalls.get(i);
                    double fMeasure = Double.isNaN(fMeasures.get(i)) ? 0.0 : fMeasures.get(i);

                    precisionSeries.getData().add(new XYChart.Data<>(classNames.get(i), precision));
                    recallSeries.getData().add(new XYChart.Data<>(classNames.get(i), recall));
                    fMeasureSeries.getData().add(new XYChart.Data<>(classNames.get(i), fMeasure));
                }

                // Add series to chart
                classifReport.getData().addAll(precisionSeries, recallSeries, fMeasureSeries);

                // Add hover effect
                addHoverEffect(precisionSeries);
                addHoverEffect(recallSeries);
                addHoverEffect(fMeasureSeries);


            } else {

                System.out.println("Failed to load evaluation data.");

            }



        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error fetching decision tree image.");
        }

        // Enable panning
        scrollPane.setPannable(true);

        // Zoom functionality with mouse scroll
        imageView.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.05 : 0.95;
            imageView.setScaleX(imageView.getScaleX() * zoomFactor);
            imageView.setScaleY(imageView.getScaleY() * zoomFactor);

            // Resize StackPane to match the ImageView's new scale
            imageContainer.setPrefSize(
                    imageView.getBoundsInParent().getWidth(),
                    imageView.getBoundsInParent().getHeight()
            );
        });
    }

    private void addHoverEffect(XYChart.Series<String, Number> series) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            // Use JavaFX's built-in Tooltip class
            Platform.runLater(() -> {
                Node node = data.getNode();
                if (node != null) {
                    Tooltip tooltip = new Tooltip();
                    tooltip.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

                    // Format the tooltip text
                    double value = data.getYValue().doubleValue();
                    String valueText = String.format("%.3f", value);
                    tooltip.setText(series.getName() + ": " + valueText);

                    // Install the tooltip on the node
                    Tooltip.install(node, tooltip);

                    // Customize tooltip showing behavior
                    tooltip.setShowDelay(Duration.millis(1));
                    tooltip.setHideDelay(Duration.millis(1));
                }
            });
        }
    }

    private void plotROCCurve(Evaluation eval) {
        try {
            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 1; // Adjust if needed
            Instances curve = tc.getCurve(eval.predictions(), classIndex);

            // Create a series for ROC Curve
            XYChart.Series<Number, Number> rocSeries = new XYChart.Series<>();
            rocSeries.setName("ROC Curve");

            // Get indices of FPR & TPR
            int fprIndex = curve.attribute("False Positive Rate").index();
            int tprIndex = curve.attribute("True Positive Rate").index();

            // Add ROC data points
            for (int i = 0; i < curve.numInstances(); i++) {
                double fpr = curve.instance(i).value(fprIndex);
                double tpr = curve.instance(i).value(tprIndex);
                rocSeries.getData().add(new XYChart.Data<>(fpr, tpr));
            }

            // Calculate and display AUC
            double aucValue = eval.areaUnderROC(classIndex);
            aucLabel.setText("AUC (Area Under Curve): " + String.format("%.4f", aucValue));

            // Add reference diagonal (random classifier)
            XYChart.Series<Number, Number> randomLine = new XYChart.Series<>();
            randomLine.setName("Random Classifier");
            randomLine.getData().add(new XYChart.Data<>(0, 0));
            randomLine.getData().add(new XYChart.Data<>(1, 1));

            // Add perfect classifier (goes from (0,0) to (0,1) to (1,1))
            XYChart.Series<Number, Number> perfectLine = new XYChart.Series<>();
            perfectLine.setName("Perfect Classifier");
            perfectLine.getData().add(new XYChart.Data<>(0, 0));
            perfectLine.getData().add(new XYChart.Data<>(0.01, 1));
            perfectLine.getData().add(new XYChart.Data<>(0.1, 1));
            perfectLine.getData().add(new XYChart.Data<>(1, 1));

            // Clear previous data and add new data
            rocChart.getData().clear();
            rocChart.getData().addAll(rocSeries, randomLine, perfectLine);

//            rocChart.lookupAll(".chart-legend-item").forEach(node -> {
//                ((Labeled) node).setMinWidth(150); // Set equal width for all items
//            });

            rocChart.lookup(".chart-legend").setStyle("-fx-hgap: 20px;");


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error plotting ROC Curve.");
        }
    }


    private void addHoverEffect(Region node, String hoverColor, String normalColor) {
        node.setOnMouseEntered(event -> node.setStyle("-fx-background-color: " + hoverColor + ";"));
        node.setOnMouseExited(event -> node.setStyle("-fx-background-color: " + normalColor + ";"));
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
    private void updateConfusionMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                String fxId = "a" + i + j;
                Text textNode = (Text) root.lookup("#" + fxId);
                if (textNode != null) {
                    textNode.setText(String.valueOf((int) matrix[i][j])); // Convert to integer for display
                }
            }
        }
    }


}
