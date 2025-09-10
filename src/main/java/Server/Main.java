//package Server;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.sql.SQLException;
//
//public class Main {
//
//    public static void main (String [] args){
//
//        try {
//            Connection connection = DriverManager.getConnection(
//                    "jdbc:mysql://127.0.0.1:3306/obesity_predictor_db", "root", "rootbdd2003"
//            );
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM Patients;");
//            if(resultSet.next()) {
//                System.out.println(resultSet.getInt(1));
//            }
//        }catch(SQLException e){
//            e.printStackTrace();
//        }
//
//    }
//
//}


//package Server;
//
//import Server.Implementations.ObesityPredictionImpl;
//import Server.Interfaces.ObesityPrediction;
//
//import java.rmi.Naming;
//import java.rmi.registry.LocateRegistry;
//
//public class Main {
//    public static void main(String[] args) {
//        try {
//            // Démarrer le registre RMI sur le port 1099 (par défaut)
//            LocateRegistry.createRegistry(1099);
//            System.out.println("RMI Registry démarré sur le port 1099...");
//
//            // Créer une instance du service
//            ObesityPrediction predictionService = new ObesityPredictionImpl();
//
//            // Publier le service avec un nom
//            String serviceName = "rmi://localhost/ObesityPredictionService";
//            Naming.rebind(serviceName, predictionService);
//
//            System.out.println("Serveur RMI prêt et en attente de connexions...");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

//package Server;
//
//import Server.Implementations.DecisionTreeServiceImpl;
//import Server.Interfaces.DecisionTreeService;
//
//import java.rmi.Naming;
//import java.rmi.registry.LocateRegistry;
//
//public class Main {
//    public static void main(String[] args) {
//        try {
//            // Start RMI registry
//            LocateRegistry.createRegistry(1099);
//            System.out.println("RMI Registry started on port 1099...");
//
//            // Bind DecisionTreeService
//            DecisionTreeService decisionTreeService = new DecisionTreeServiceImpl();
//            Naming.rebind("rmi://localhost/DecisionTreeService", decisionTreeService);
//
//            System.out.println("Server is ready...");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

package Server;

import Server.Implementations.DecisionTreeServiceImpl;
import Server.Implementations.ObesityPredictionImpl;
import Server.Implementations.PatientServiceImpl;
import Server.Interfaces.DecisionTreeService;
import Server.Interfaces.ObesityPrediction;
import Server.Interfaces.PatientService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Main {

    public static void main(String[] args) {

        try {

            // Démarrer le registre RMI sur le port 1099 (par défaut)
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry démarré sur le port 1099...");

            // Créer une instance du service
            PatientService patientService = new PatientServiceImpl();
            // Publier le service avec un nom
            Naming.rebind("rmi://localhost/PatientService", patientService);


            //Créer une instance du service
            ObesityPrediction predictionService = new ObesityPredictionImpl();
            // Publier le service avec un nom
            Naming.rebind("rmi://localhost/ObesityPredictionService", predictionService);


            //Créer une instance du service
            DecisionTreeService decisionTreeService = new DecisionTreeServiceImpl();
            // Publier le service avec un nom
            Naming.rebind("rmi://localhost/DecisionTreeService", decisionTreeService);

            System.out.println("Serveur RMI prêt et en attente de connexions...");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}