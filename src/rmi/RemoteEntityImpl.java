/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import database.DatabaseManager;
import models.Entity;

public class RemoteEntityImpl extends UnicastRemoteObject implements RemoteEntity {

    public RemoteEntityImpl() throws RemoteException {
    }

    @Override
    public ArrayList<Entity> listAll(Class Entity, String filters) throws RemoteException {
        ArrayList<Entity> entities = new ArrayList<>();
        try {
            Entity entity = (Entity) Entity.newInstance();
            ResultSet rs = DatabaseManager.querySQL("SELECT * FROM "
                    + Entity.getField("tableName").get(entity)
                    + filters);
            while (rs.next()) {
                entity.fillFields(rs);
                entities.add(entity);
                entity = (Entity) Entity.newInstance();
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | SQLException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            System.err.println(this.getClass().getName() + " " + ex);
            ex.printStackTrace(System.err);
        }
        return entities;
    }

    @Override
    public ArrayList<Entity> listAll(Class Entity) throws RemoteException {
        return listAll(Entity, "");
    }

    @Override
    public Integer create(Entity entity) throws RemoteException {
        try {
            return DatabaseManager.updateSQL("INSERT INTO "
                    + entity.getTableName()
                    + "(" + entity.joinFields(",") + ")"
                    + "VALUES" + "(" + entity.toString() + ")");
        } catch (SQLException ex) {
            System.err.println(this.getClass().getName() + " " + ex);
            ex.printStackTrace(System.err);
            return -1;
        }
    }

    @Override
    public Entity read(Class Entity, String filters) throws RemoteException, IllegalArgumentException {
        Entity entity = null;
        try {
            entity = (Entity) Entity.newInstance();
            ResultSet rs = DatabaseManager.querySQL("SELECT "
                    + " * "
                    + " FROM "
                    + Entity.getField("tableName").get(entity)
                    + filters);
            if (rs.next()) {
                entity.setId(rs.getInt("id"));
                entity.fillFields(rs);
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | SQLException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            System.err.println(this.getClass().getName() + " " + ex);
            ex.printStackTrace(System.err);
        }
        return entity;
    }

    @Override
    public Integer update(Entity entity) throws RemoteException {
        try {
            String updatedValues = entity.getFieldsExcept("ignoreFieldsArray",
                    "ignoreGettersArray",
                    "staticInstance",
                    "userId",
                    "user",
                    "id",
                    "tableName",
                    "fields",
                    "todos").stream()
                    .map(field -> {
                        try {
                            String entry;
                            if (entity.getField(field).getType().equals(Boolean.class)) {
                                entry = field + "=" + "'" + (entity.getValue(field).equals(Boolean.TRUE) ? "1" : "0") + "'";
                            } else {
                                entry = field + "=" + "'" + entity.getValue(field) + "'";
                            }
                            return entry;
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            System.err.println(this.getClass().getName() + " " + ex);
                            ex.printStackTrace(System.err);
                            return null;
                        }
                    })
                    .collect(Collectors.joining(","));

            return DatabaseManager.updateSQL("UPDATE "
                    + entity.getTableName()
                    + " SET "
                    + updatedValues
                    + " WHERE id = "
                    + entity.getId());
        } catch (SQLException | IllegalArgumentException ex) {
            System.err.println(this.getClass().getName() + " " + ex);
            ex.printStackTrace(System.err);
            return -1;
        }

    }

    @Override
    public Integer delete(Entity entity) throws RemoteException {
        try {
            return DatabaseManager.updateSQL("DELETE FROM " + entity.getTableName() + " WHERE id = " + entity.getId());
        } catch (SQLException ex) {
            System.err.println(this.getClass().getName() + " " + ex);
            ex.printStackTrace(System.err);
            return -1;
        }
    }

}
