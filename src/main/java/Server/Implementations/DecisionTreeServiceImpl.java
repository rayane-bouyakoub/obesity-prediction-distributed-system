package Server.Implementations;

import Server.Interfaces.DecisionTreeService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class DecisionTreeServiceImpl extends UnicastRemoteObject implements DecisionTreeService {

    public DecisionTreeServiceImpl() throws RemoteException {

        super();

    }

    @Override
    public byte[] getDecisionTreeImage() throws RemoteException {

        try {

            String imagePath = "src/main/java/Server/Model/decision_tree.png";

            File file = new File(imagePath);

            return Files.readAllBytes(file.toPath());

        } catch (IOException e) {

            e.printStackTrace();

            throw new RemoteException("Error reading image file", e);

        }
    }


    @Override
    public boolean isModelFolderNotEmpty() throws RemoteException {

        File folder = new File("src/main/java/Server/Model");

        // Check if the folder exists and contains at least one file
        return folder.exists() && folder.isDirectory() && folder.listFiles() != null && folder.listFiles().length > 0;

    }


    @Override
    public byte[] getSerializedEvaluation() throws RemoteException {
        try {
            File evalFile = new File("src/main/java/Server/Model/evaluation.model");

            if (!evalFile.exists()) {
                throw new RemoteException("Evaluation file not found: " + evalFile.getAbsolutePath());
            }

            return Files.readAllBytes(evalFile.toPath());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Error reading evaluation file", e);
        }
    }


}