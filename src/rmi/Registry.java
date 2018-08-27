/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 *
 * @author AAGOOGLE
 */
public class Registry {

    public static final int RMI_PORT = 1099;
    public static final String RMI_URL = "rmi://localhost:" + RMI_PORT + "/";
    public static boolean created = false;

    public static void put(String key, Remote object) throws RemoteException, MalformedURLException {
        if (!created) {
            LocateRegistry.createRegistry(RMI_PORT);
            created = true;
        }
        Naming.rebind(RMI_URL + key, object);
    }

    public static Remote get(String key) throws NotBoundException, MalformedURLException, RemoteException {
        return Naming.lookup(key);
    }
}
