/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import models.Entity;

/**
 *
 * @author AAGOOGLE
 */
public class EntityTableModel extends AbstractTableModel {

    Entity entityInstance;
    ArrayList<Entity> entites = new ArrayList<>();
    JTable entityTable = new JTable();
    Object[] columnNames = new Object[]{};
    Object[][] rowsData = new Object[][]{};
    Class<?>[] types = new Class[]{};

    public EntityTableModel(
            Class Entity,
            ArrayList<Entity> entites,
            JTable entityTable,
            Object[] columnNames,
            Object[][] rowsData) throws InstantiationException, IllegalAccessException {

        this.entityInstance = (Entity) Entity.newInstance();
        this.entites = entites;
        this.entityTable = entityTable;
        this.columnNames = columnNames;
        this.rowsData = rowsData;

        types = Arrays.stream(columnNames)
                .map(column -> entityInstance.getField(column).getType())
                .toArray(Class[]::new);
    }

    @Override
    public String getColumnName(int column) {
        return String.valueOf(columnNames[column]);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return types[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return entites.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int column) {
        try {
            if (column >= 0 && column < columnNames.length) {
                int row = entityTable.convertRowIndexToModel(rowIndex);
                return entites.get(row).getValue(getColumnName(column));
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            System.err.println(this.getClass().getName() + " " + ex);
            ex.printStackTrace(System.err);
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return entityTable.getColumnModel().getColumnIndex("id") != columnIndex && entityTable.getColumnModel().getColumnIndex("user") != columnIndex;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int column) {
        try {
            if (column >= 0 && column < columnNames.length) {
                int row = entityTable.convertRowIndexToModel(rowIndex);
                Object id = getValueAt(row, entityTable.getColumnModel().getColumnIndex("id"));
                Entity selectedEntity = entites.stream()
                        .filter(entity -> entity.getId().equals(id))
                        .findFirst()
                        .orElse(null);
                selectedEntity.setValue(getColumnName(column), value);
                fireTableDataChanged();
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | ClassNotFoundException ex) {
            System.err.println(this.getClass().getName() + " " + ex);
            ex.printStackTrace(System.err);
        }
    }

    public void addRow(Entity entity) {
        entites.add(entity);
        fireTableRowsInserted(entites.size() - 1, entites.size() - 1);
    }

    public void removeRow(int row) {
        if (row >= 0 && row < entites.size()) {
            entites.remove(row);
            if (row == 0) {
                fireTableRowsDeleted(row, row);
            } else {
                fireTableRowsDeleted(row - 1, row - 1);
            }
        }
    }

    public void removeRows(int[] rows) {
        for (int i = 0; i < rows.length; i++) {
            removeRow(rows[i]);
        }
    }
}
