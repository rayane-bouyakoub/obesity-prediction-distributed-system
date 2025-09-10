package Server.Implementations;

import Enums.*;
import Server.Entities.Patient;
import Server.Interfaces.PatientService;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import weka.core.Instances;
import weka.core.converters.DatabaseLoader;
import weka.classifiers.trees.J48;
import weka.core.SerializationHelper;
import weka.classifiers.Evaluation;

public class PatientServiceImpl extends UnicastRemoteObject implements PatientService {

    private static final String URL = "******";
    private static final String USER = "******";
    private static final String PASSWORD = "******";

    private static final int FIRST_TRAIN_THRESHOLD = 1000;
    private static final double RETRAIN_PERCENTAGE = 0.05;
    private int totalPatients;
    private int newPatientsCount = 0;

    public PatientServiceImpl() throws RemoteException {

        super();

        this.totalPatients = getTotalPatientsCount();

    }

    private Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL, USER, PASSWORD);

    }

    private int getTotalPatientsCount() {

        try (Connection connection = getConnection();

             PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM Patients");

             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return 0;
        }
    }

    @Override
    public Patient insertPatient(Gender gender, float age, float height,
                                 float weight, family_history_overweight fho, FAVC favc,
                                 float fcvc, float ncp, CAEC caec, SMOKE smoke,
                                 float ch2o, SCC scc, float faf, float tue,
                                 CALC calc, MTRANS mtrans, NObeyesdad nobeyesdad) throws RemoteException {

        String query = "INSERT INTO Patients (Gender, Age, Height, Weight, family_history_with_overweight, " +
                "FAVC, FCVC, NCP, CAEC, SMOKE, CH2O, SCC, FAF, TUE, CALC, MTRANS, NObeyesdad) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, gender.toString());
            statement.setFloat(2, age);
            statement.setFloat(3, height);
            statement.setFloat(4, weight);
            statement.setString(5, fho.toString());
            statement.setString(6, favc.toString());
            statement.setFloat(7, fcvc);
            statement.setFloat(8, ncp);
            statement.setString(9, caec.toString());
            statement.setString(10, smoke.toString());
            statement.setFloat(11, ch2o);
            statement.setString(12, scc.toString());
            statement.setFloat(13, faf);
            statement.setFloat(14, tue);
            statement.setString(15, calc.toString());
            statement.setString(16, mtrans.toString());
            statement.setString(17, nobeyesdad.toString());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RemoteException("Inserting patient failed, no rows affected.");
            }

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {

                    newPatientsCount++;

                    trainModelIfNeeded();

                    return new Patient(
                            result.getInt(1), gender, age, height, weight, fho, favc, fcvc,
                            ncp, caec, smoke, ch2o, scc, faf, tue, calc, mtrans, nobeyesdad
                    );
                } else {
                    throw new RemoteException("Inserting patient failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RemoteException("Database error while inserting patient: " + e.getMessage(), e);
        }
    }


    @Override
    public void insertPatientsFromCSV(byte[] csvData) throws RemoteException {

        String query = "INSERT INTO Patients (Gender, Age, Height, Weight, family_history_with_overweight, FAVC, FCVC, NCP, CAEC, SMOKE, CH2O, SCC, FAF, TUE, CALC, MTRANS, NObeyesdad) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(csvData)))) {

            connection.setAutoCommit(false);

            String line;

            boolean firstLine = true; // Skip headers

            while ((line = reader.readLine()) != null) {

                if (firstLine) {

                    firstLine = false;

                    continue;

                }

                String[] data = line.split(",");

                if (data.length != 17) continue; // Skip entry if it doesn't contain exactly 17 attributes.

                statement.setString(1, data[0]);
                statement.setFloat(2, Float.parseFloat(data[1]));
                statement.setFloat(3, Float.parseFloat(data[2]));
                statement.setFloat(4, Float.parseFloat(data[3]));
                statement.setString(5, data[4]);
                statement.setString(6, data[5]);
                statement.setFloat(7, Float.parseFloat(data[6]));
                statement.setFloat(8, Float.parseFloat(data[7]));
                statement.setString(9, data[8]);
                statement.setString(10, data[9]);
                statement.setFloat(11, Float.parseFloat(data[10]));
                statement.setString(12, data[11]);
                statement.setFloat(13, Float.parseFloat(data[12]));
                statement.setFloat(14, Float.parseFloat(data[13]));
                statement.setString(15, data[14]);
                statement.setString(16, data[15]);
                statement.setString(17, data[16]);

                statement.addBatch();

                newPatientsCount++;

            }

            statement.executeBatch();

            connection.commit();

            trainModelIfNeeded();


        } catch (SQLException | IOException e) {

            e.printStackTrace();

        }
    }

    public void trainModelIfNeeded() throws RemoteException {

        System.out.println("Total Patients: " + totalPatients);
        System.out.println("New Patients Count: " + newPatientsCount);
        System.out.println("Required new patients for training: " + (int) (totalPatients * RETRAIN_PERCENTAGE));

        if (totalPatients == 0 && newPatientsCount < FIRST_TRAIN_THRESHOLD) {

            return;

        }

        int requiredNewPatients = (int) (totalPatients * RETRAIN_PERCENTAGE);

        if (newPatientsCount < requiredNewPatients) {

            return; // Not many new patients have been inserted.

        }

        trainModel();

        totalPatients += newPatientsCount;

        newPatientsCount = 0;

    }

    private void trainModel() throws RemoteException {
        try {

            DatabaseLoader loader = new DatabaseLoader();

            loader.setSource(URL, USER, PASSWORD);

            loader.setQuery("SELECT Gender, Age, Height, Weight, family_history_with_overweight, FAVC, FCVC, NCP, CAEC, SMOKE, CH2O, SCC, FAF, TUE, CALC, MTRANS, NObeyesdad FROM Patients");

            Instances data = loader.getDataSet();

            data.setClassIndex(data.numAttributes() - 1);

            int trainSize = (int) Math.round(data.numInstances() * 0.8);

            int testSize = data.numInstances() - trainSize;

            data.randomize(new Random(42));

            Instances trainData = new Instances(data, 0, trainSize);
            Instances testData = new Instances(data, trainSize, testSize);

            J48 tree = new J48();
            tree.buildClassifier(trainData);

            Evaluation eval = new Evaluation(trainData);
            eval.evaluateModel(tree, testData);

            SerializationHelper.write("src/main/java/Server/Model/decision_tree.model", tree);
            SerializationHelper.write("src/main/java/Server/Model/evaluation.model", eval);

            visualizeTree(tree, "src/main/java/Server/Model/decision_tree.png");

            System.out.println("Model trained and saved.");

        } catch (Exception e) {
            throw new RemoteException("Error during model training: " + e.getMessage(), e);
        }
    }

    public static void visualizeTree(J48 tree, String imageFilename) {
        String dotFilename = "src/main/java/Server/Model/decision_tree.dot";

        // Save the tree structure to a DOT file
        try (FileWriter writer = new FileWriter(dotFilename)) {
            writer.write(tree.graph());
            System.out.println("Tree saved as DOT file: " + dotFilename);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Convert DOT to PNG using Graphviz
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFilename, "-o", imageFilename);
            Process process = pb.start();
            process.waitFor(); // Wait for the process to complete

            if (process.exitValue() == 0) {
                System.out.println("Tree image saved as: " + imageFilename);
            } else {
                System.err.println("Error during Graphviz execution.");
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updatePatient(Patient patient) throws RemoteException {

        String query = "UPDATE Patients SET Gender = ?, " +
                "Age = ?, Height = ?, Weight = ?, family_history_with_overweight = ?, FAVC = ?, " +
                "FCVC = ?, NCP = ?, CAEC = ?, SMOKE = ?, CH2O = ?, SCC = ?, FAF = ?, TUE = ?, CALC = ?, " +
                "MTRANS = ?, NObeyesdad = ? " +
                "WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, patient.getGender().toString());
            statement.setFloat(2, patient.getAge());
            statement.setFloat(3, patient.getHeight());
            statement.setFloat(4, patient.getWeight());
            statement.setString(5, patient.getFho().toString());
            statement.setString(6, patient.getFavc().toString());
            statement.setFloat(7, patient.getFcvc());
            statement.setFloat(8, patient.getNcp());
            statement.setString(9, patient.getCaec().toString());
            statement.setString(10, patient.getSmoke().toString());
            statement.setFloat(11, patient.getCh2o());
            statement.setString(12, patient.getScc().toString());
            statement.setFloat(13, patient.getFaf());
            statement.setFloat(14, patient.getTue());
            statement.setString(15, patient.getCalc().toString());
            statement.setString(16, patient.getMtrans().toString());
            statement.setString(17, patient.getDiagnosis().toString());
            statement.setInt(18, patient.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RemoteException("Patient with ID " + patient.getId() + " not found.");
            }

        } catch (SQLException e) {
            throw new RemoteException("Database error while updating patient: " + e.getMessage(), e);
        }
    }


    @Override
    public void deletePatient(int patientId) throws RemoteException {
        String query = "DELETE FROM Patients WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, patientId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RemoteException("Deletion failed: No patient found with ID " + patientId);
            }

        } catch (SQLException e) {
            throw new RemoteException("Database error while deleting patient: " + e.getMessage(), e);
        }
    }

    @Override
    public Patient getPatientById(int patientId) throws RemoteException {
        String query = "SELECT * FROM Patients WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, patientId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Patient(
                        resultSet.getInt("id"),
                        Gender.valueOf(resultSet.getString("Gender")),
                        resultSet.getFloat("Age"),
                        resultSet.getFloat("Height"),
                        resultSet.getFloat("Weight"),
                        family_history_overweight.valueOf(resultSet.getString("family_history_with_overweight")),
                        FAVC.valueOf(resultSet.getString("FAVC")),
                        resultSet.getFloat("FCVC"),
                        resultSet.getFloat("NCP"),
                        CAEC.valueOf(resultSet.getString("CAEC")),
                        SMOKE.valueOf(resultSet.getString("SMOKE")),
                        resultSet.getFloat("CH2O"),
                        SCC.valueOf(resultSet.getString("SCC")),
                        resultSet.getFloat("FAF"),
                        resultSet.getFloat("TUE"),
                        CALC.valueOf(resultSet.getString("CALC")),
                        MTRANS.valueOf(resultSet.getString("MTRANS")),
                        NObeyesdad.valueOf(resultSet.getString("NObeyesdad"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Patient> getAllPatients() throws RemoteException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM Patients";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                patients.add(new Patient(
                        resultSet.getInt("id"),
                        Gender.valueOf(resultSet.getString("Gender")),
                        resultSet.getFloat("Age"),
                        resultSet.getFloat("Height"),
                        resultSet.getFloat("Weight"),
                        family_history_overweight.valueOf(resultSet.getString("family_history_with_overweight")),
                        FAVC.valueOf(resultSet.getString("FAVC")),
                        resultSet.getFloat("FCVC"),
                        resultSet.getFloat("NCP"),
                        CAEC.valueOf(resultSet.getString("CAEC")),
                        SMOKE.valueOf(resultSet.getString("SMOKE")),
                        resultSet.getFloat("CH2O"),
                        SCC.valueOf(resultSet.getString("SCC")),
                        resultSet.getFloat("FAF"),
                        resultSet.getFloat("TUE"),
                        CALC.valueOf(resultSet.getString("CALC")),
                        MTRANS.valueOf(resultSet.getString("MTRANS")),
                        NObeyesdad.valueOf(resultSet.getString("NObeyesdad"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

}
