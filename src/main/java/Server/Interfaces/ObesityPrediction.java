package Server.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import Enums.*;

public interface ObesityPrediction extends Remote {

    NObeyesdad predictObesity(Gender gender, float age, float height, float weight, family_history_overweight fho, FAVC favc, float fcvc, float ncp, CAEC caec, SMOKE smoke, float ch2o, SCC scc, float faf, float tue, CALC calc, MTRANS mtrans) throws RemoteException;

}