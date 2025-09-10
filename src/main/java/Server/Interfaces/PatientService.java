package Server.Interfaces;

import Enums.*;
import Server.Entities.Patient;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PatientService extends Remote {

    Patient insertPatient(Gender gender, float age, float height, float weight, family_history_overweight fho, FAVC favc, float fcvc, float ncp, CAEC caec, SMOKE smoke, float ch2o, SCC scc, float faf, float tue, CALC calc, MTRANS mtrans, NObeyesdad nobeyesdad) throws RemoteException;

    void insertPatientsFromCSV(byte[] csvData) throws RemoteException;

    void updatePatient(Patient p) throws RemoteException;

    void deletePatient(int patientId) throws RemoteException;

    Patient getPatientById(int patientId) throws RemoteException;

    List<Patient> getAllPatients() throws RemoteException;

}