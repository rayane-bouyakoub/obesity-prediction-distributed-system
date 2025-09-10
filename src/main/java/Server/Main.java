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
