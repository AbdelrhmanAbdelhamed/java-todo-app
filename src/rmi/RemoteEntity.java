package rmi;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import models.Entity;

/**
 *
 * @author AAGOOGLE
 */
public interface RemoteEntity extends Remote {

    public ArrayList<Entity> listAll(Class Entity, String filters) throws RemoteException;

    public ArrayList<Entity> listAll(Class Entity) throws RemoteException;

    public Integer create(Entity entity) throws RemoteException;

    public Entity read(Class Entity, String filters) throws RemoteException;

    public Integer update(Entity entity) throws RemoteException;

    public Integer delete(Entity entity) throws RemoteException;
}
