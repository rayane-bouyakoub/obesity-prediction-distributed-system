/*

    Adding modules to project: https://www.youtube.com/watch?v=9ntKSLLDeSs

    Deleting weka errors: Run -> Edit configurations -> Select Main class ->

    right hand side -> Add VM options -> Paste this:

    --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang=weka

 */

package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/GUI/PatientsDashboard.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("ObesityPredictorRMI");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> exit(stage, event));

    }

    public static void main(String[] args) {
        launch();
    }

    public void exit(Stage stage, WindowEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Do you really want to close the application?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            event.consume(); // Consume the event to prevent further processing
        } else {
            stage.close();
        }
    }

}


//package Client;
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.chart.LineChart;
//import javafx.scene.chart.NumberAxis;
//import javafx.scene.chart.XYChart;
//import javafx.stage.Stage;
//import weka.classifiers.Evaluation;
//import weka.classifiers.trees.J48;
//import weka.classifiers.evaluation.ThresholdCurve;
//import weka.core.Instances;
//import weka.core.Utils;
//import weka.core.converters.CSVLoader;
//
//import java.io.File;
//import java.util.Random;
//
//public class Main extends Application {
//    private static XYChart.Series<Number, Number> rocSeries;
//    private static double aucValue;
//
//    public static void main(String[] args) throws Exception {
//        // Charger le dataset
//        CSVLoader loader = new CSVLoader();
//        loader.setSource(new File("/Users/rayane/Desktop/2SD/S2/IRIAD/PRJ1/ObesityDataSet_raw_and_data_sinthetic.csv"));
//        Instances data = loader.getDataSet();
//        data.setClassIndex(data.numAttributes() - 1); // Définir la variable cible
//
//        // Séparer en train/test (80%-20%)
//        data.randomize(new Random(42));
//        int trainSize = (int) Math.round(data.numInstances() * 0.8);
//        int testSize = data.numInstances() - trainSize;
//        Instances trainData = new Instances(data, 0, trainSize);
//        Instances testData = new Instances(data, trainSize, testSize);
//
//        // Entraîner un arbre de décision J48
//        J48 tree = new J48();
//        tree.buildClassifier(trainData);
//
//        // Évaluer le modèle
//        Evaluation eval = new Evaluation(trainData);
//        eval.evaluateModel(tree, testData);
//
//        // Extraire la courbe ROC
//        ThresholdCurve tc = new ThresholdCurve();
//        int classIndex = 1; // Indice de la classe positive (à ajuster selon le dataset)
//        Instances curve = tc.getCurve(eval.predictions(), classIndex);
//
//        // Créer une série de données pour JavaFX
//        rocSeries = new XYChart.Series<>();
//        rocSeries.setName("ROC Curve");
//
//        int fprIndex = curve.attribute("False Positive Rate").index();
//        int tprIndex = curve.attribute("True Positive Rate").index();
//
//        for (int i = 0; i < curve.numInstances(); i++) {
//            double fpr = curve.instance(i).value(fprIndex);
//            double tpr = curve.instance(i).value(tprIndex);
//            rocSeries.getData().add(new XYChart.Data<>(fpr, tpr));
//        }
//
//
//        // Récupérer l'AUC
//        aucValue = eval.areaUnderROC(classIndex);
//        System.out.println("AUC: " + Utils.doubleToString(aucValue, 4));
//
//        // Lancer JavaFX
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) {
//        stage.setTitle("ROC Curve - JavaFX");
//
//        // Définition des axes
//        NumberAxis xAxis = new NumberAxis(0, 1, 0.1);
//        NumberAxis yAxis = new NumberAxis(0, 1, 0.1);
//        xAxis.setLabel("False Positive Rate (FPR)");
//        yAxis.setLabel("True Positive Rate (TPR)");
//
//        // Création du graphique
//        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
//        lineChart.setTitle("ROC Curve - AUC: " + Utils.doubleToString(aucValue, 4));
//
//        // Ligne de référence (classificateur aléatoire)
//        XYChart.Series<Number, Number> randomLine = new XYChart.Series<>();
//        randomLine.setName("Random Classifier");
//        randomLine.getData().add(new XYChart.Data<>(0, 0));
//        randomLine.getData().add(new XYChart.Data<>(1, 1));
//
//        // Ajouter les séries au graphique
//        lineChart.getData().addAll(rocSeries, randomLine);
//
//        // Affichage
//        Scene scene = new Scene(lineChart, 800, 600);
//        stage.setScene(scene);
//        stage.show();
//    }
//}