package Server.Interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DecisionTreeService extends Remote {
    byte[] getDecisionTreeImage() throws RemoteException;

    boolean isModelFolderNotEmpty() throws RemoteException;

    byte[] getSerializedEvaluation() throws RemoteException;

}