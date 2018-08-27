/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JTable;
import models.Entity;
import models.Todo;

/**
 *
 * @author AAGOOGLE
 */
public class TodoServerHandler implements Runnable {

    public Socket clientSocket;
    public ObjectInputStream input;
    public ObjectOutputStream output;

    private final JTable todosTable;
    private final EntityTableModel entityTableModel;

    public TodoServerHandler(JTable todosTable, Socket clientSocket, ObjectInputStream input, ObjectOutputStream output) {
        this.clientSocket = clientSocket;
        this.input = input;
        this.output = output;
        this.todosTable = todosTable;
        this.entityTableModel = (EntityTableModel) this.todosTable.getModel();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Entity.Action action = (Entity.Action) input.readObject();
                Object[] data = (Object[]) input.readObject();

                if (data != null && data.length > 0) {
                    switch (action) {
                        case CREATE:
                            Todo todo = (Todo) data[0];
                            entityTableModel.addRow(todo);
                            break;
                        case READ:
                            break;
                        case UPDATE:
                            Object value = data[0];
                            int row = (int) data[1];
                            int column = (int) data[2];
                            entityTableModel.setValueAt(value, row, column);
                            break;
                        case DELETE:
                            int rowToDelete = (int) data[0];
                            entityTableModel.removeRow(rowToDelete);
                            break;
                    }
                }
            } catch (ClassNotFoundException | IOException ex) {
                System.err.println(this.getClass().getName() + " " + ex);
                ex.printStackTrace(System.err);
            }
        }
    }

    public void broadcast(Entity.Action action, Object... data) throws IOException {
        output.writeObject(action);
        output.writeObject(data);
    }

}
