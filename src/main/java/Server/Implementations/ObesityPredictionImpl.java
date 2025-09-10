package Server.Implementations;

import Server.Interfaces.ObesityPrediction;
import Enums.*;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ObesityPredictionImpl extends UnicastRemoteObject implements ObesityPrediction {

    public ObesityPredictionImpl() throws RemoteException {

        super();

    }

    @Override
    public NObeyesdad predictObesity(Gender gender, float age, float height, float weight,
                                     family_history_overweight fho, FAVC favc, float fcvc, float ncp,
                                     CAEC caec, SMOKE smoke, float ch2o, SCC scc, float faf,
                                     float tue, CALC calc, MTRANS mtrans) throws RemoteException {
        try {

            String filePath = "src/main/java/Server/Model/decision_tree.model";

            FileInputStream fis = new FileInputStream(filePath);

            ObjectInputStream ois = new ObjectInputStream(fis);

            J48 decisionTree = (J48) ois.readObject();

            ois.close();

            Instances datasetStructure = createDatasetStructure();

            Instance instance = new DenseInstance(datasetStructure.numAttributes());
            instance.setDataset(datasetStructure);
            instance.setValue(0, gender.toString());
            instance.setValue(1, age);
            instance.setValue(2, height);
            instance.setValue(3, weight);
            instance.setValue(4, fho.toString());
            instance.setValue(5, favc.toString());
            instance.setValue(6, fcvc);
            instance.setValue(7, ncp);
            instance.setValue(8, caec.toString());
            instance.setValue(9, smoke.toString());
            instance.setValue(10, ch2o);
            instance.setValue(11, scc.toString());
            instance.setValue(12, faf);
            instance.setValue(13, tue);
            instance.setValue(14, calc.toString());
            instance.setValue(15, mtrans.toString());

            // Predict Obesity type
            double predictionIndex = decisionTree.classifyInstance(instance);
            String predictedClass = datasetStructure.classAttribute().value((int) predictionIndex);

//            System.out.println(instance.classAttribute());
//            System.out.println(instance);
//            System.out.println(instance);
//            System.out.println(predictionIndex);
//            System.out.println(predictedClass);
//            System.out.println(NObeyesdad.valueOf(predictedClass));

            // Return Obesity Type
            return NObeyesdad.valueOf(predictedClass);

        } catch (Exception e) {

            e.printStackTrace();

            throw new RemoteException("Erreur lors de la prédiction", e);

        }
    }

//    private Instances createDatasetStructure() {
//
//        ArrayList<Attribute> attributes = new ArrayList<>();
//
//        // Define attributes (Name + Type)
//        attributes.add(new Attribute("Gender", Arrays.asList("Male", "Female")));
//        attributes.add(new Attribute("Age"));
//        attributes.add(new Attribute("Height"));
//        attributes.add(new Attribute("Weight"));
//        attributes.add(new Attribute("family_history_overweight", Arrays.asList("yes", "no")));
//        attributes.add(new Attribute("FAVC", Arrays.asList("yes", "no")));
//        attributes.add(new Attribute("FCVC"));
//        attributes.add(new Attribute("NCP"));
//        attributes.add(new Attribute("CAEC", Arrays.asList("no", "Sometimes", "Frequently", "Always")));
//        attributes.add(new Attribute("SMOKE", Arrays.asList("yes", "no")));
//        attributes.add(new Attribute("CH2O"));
//        attributes.add(new Attribute("SCC", Arrays.asList("yes", "no")));
//        attributes.add(new Attribute("FAF"));
//        attributes.add(new Attribute("TUE"));
//        attributes.add(new Attribute("CALC", Arrays.asList("no", "Sometimes", "Frequently", "Always")));
//        attributes.add(new Attribute("MTRANS", Arrays.asList("Public_Transportation", "Automobile", "Walking", "Motorbike", "Bike")));
//
//        // Define Output Label.
//        ArrayList<String> classValues = new ArrayList<>(Arrays.asList(
//                "Insufficient_Weight", "Normal_Weight", "Overweight_Level_I", "Overweight_Level_II",
//                "Obesity_Type_I", "Obesity_Type_II", "Obesity_Type_III"
//        ));
//        attributes.add(new Attribute("NObeyesdad", classValues));
//
//        // Create an instance that defines the dataset structure.
//        Instances dataset = new Instances("ObesityDataset", attributes, 0);
//        dataset.setClassIndex(dataset.numAttributes() - 1); // Last column being the output label.
//
//        return dataset;
//    }

    private Instances createDatasetStructure() {

        ArrayList<Attribute> attributes = new ArrayList<>();

        // Use Gender enum values
        List<String> genderValues = Arrays.stream(Gender.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        attributes.add(new Attribute("Gender", genderValues));

        // Numeric attributes
        attributes.add(new Attribute("Age"));
        attributes.add(new Attribute("Height"));
        attributes.add(new Attribute("Weight"));

        // Use family_history_overweight enum values
        List<String> fhoValues = Arrays.stream(family_history_overweight.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        attributes.add(new Attribute("family_history_with_overweight", fhoValues));

        // Use FAVC enum values
        List<String> favcValues = Arrays.stream(FAVC.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        attributes.add(new Attribute("FAVC", favcValues));

        // Numeric attributes
        attributes.add(new Attribute("FCVC"));
        attributes.add(new Attribute("NCP"));

        // Use CAEC enum values
        List<String> caecValues = Arrays.stream(CAEC.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        attributes.add(new Attribute("CAEC", caecValues));

        // Use SMOKE enum values
        List<String> smokeValues = Arrays.stream(SMOKE.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        attributes.add(new Attribute("SMOKE", smokeValues));

        // Numeric attribute
        attributes.add(new Attribute("CH2O"));

        // Use SCC enum values
        List<String> sccValues = Arrays.stream(SCC.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        attributes.add(new Attribute("SCC", sccValues));

        // Numeric attributes
        attributes.add(new Attribute("FAF"));
        attributes.add(new Attribute("TUE"));

        // Use CALC enum values
        List<String> calcValues = Arrays.stream(CALC.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        attributes.add(new Attribute("CALC", calcValues));

        // Use MTRANS enum values
        List<String> mtransValues = Arrays.stream(MTRANS.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        attributes.add(new Attribute("MTRANS", mtransValues));

        // Critical part: Use NObeyesdad enum values in the exact order they appear in the enum
        List<String> classValues = Arrays.stream(NObeyesdad.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        attributes.add(new Attribute("NObeyesdad", classValues));

        // Create an instance that defines the dataset structure
        Instances dataset = new Instances("ObesityDataset", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);

        return dataset;
    }

}
