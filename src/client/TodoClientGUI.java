/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import static client.TodoClient.user;
import database.DatabaseFilter;
import database.DatabaseFilterBuilder;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import models.Entity;
import models.Todo;

/**
 *
 * @author AAGOOGLE
 */
public class TodoClientGUI extends javax.swing.JFrame {

    /**
     * Creates new form TodoClientJFrame
     *
     * @throws java.rmi.NotBoundException
     * @throws java.net.MalformedURLException
     * @throws java.rmi.RemoteException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.ClassNotFoundException
     */
    public TodoClientGUI() throws NotBoundException, MalformedURLException, RemoteException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);

        UserLabel.setText("Welcome " + TodoClient.user.getName());

        Object[] todoColumns = Todo.getStaticInstance().getFieldsExcept(
                "ignoreFieldsArray",
                "ignoreGettersArray",
                "staticInstance",
                "userId",
                "isPrivate",
                "tableName",
                "fields",
                "todos"
        ).toArray();

        ArrayList<Entity> privateTodos = TodoClient.remoteEntity.listAll(Todo.class, new DatabaseFilterBuilder()
                .where(new DatabaseFilter("userId", TodoClient.user.getId()))
                .and(new DatabaseFilter("isPrivate", true))
                .toString());

        ArrayList<Entity> todos = TodoClient.remoteEntity.listAll(Todo.class, new DatabaseFilterBuilder()
                .where(new DatabaseFilter("isPrivate", false))
                .toString());

        Object[][] privateTodosData = privateTodos.stream()
                .map(todoItem -> todoItem.getValuesExcept(
                "ignoreFieldsArray",
                "ignoreGettersArray",
                "staticInstance",
                "userId",
                "isPrivate",
                "tableName",
                "fields",
                "todos"))
                .toArray(Object[][]::new);

        Object[][] todosData = todos.stream()
                .map(todoItem -> todoItem.getValuesExcept(
                "ignoreFieldsArray",
                "ignoreGettersArray",
                "staticInstance",
                "userId",
                "isPrivate",
                "user",
                "tableName",
                "fields",
                "todos"))
                .toArray(Object[][]::new);

        privateTodosTable.setModel(new EntityTableModel(Todo.class, privateTodos, privateTodosTable, todoColumns, privateTodosData));
        privateTodosTable.putClientProperty("terminateEditOnFocusLost", true);

        new TableCellListener(privateTodosTable, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                try {
                    int row = privateTodosTable.convertRowIndexToModel(tcl.getRow());
                    if (row >= 0 && row < privateTodos.size()) {
                        TodoClient.remoteEntity.update(privateTodos.get(row));
                    }
                } catch (IOException ex) {
                    System.err.println(this.getClass().getName() + " " + ex);
                    ex.printStackTrace(System.err);
                }
            }
        });

        todosTable.setModel(new EntityTableModel(Todo.class, todos, todosTable, todoColumns, todosData));
        todosTable.putClientProperty("terminateEditOnFocusLost", true);

        new TableCellListener(todosTable, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener) e.getSource();
                try {
                    int row = todosTable.convertRowIndexToModel(tcl.getRow());
                    int column = tcl.getColumn();

                    TableModel model = todosTable.getModel();
                    Object value = model.getValueAt(row, column);
                    if (row >= 0 && row < todos.size()) {
                        TodoClient.remoteEntity.update(todos.get(row));

                        TodoClient.serverHandler.broadcast(Entity.Action.UPDATE, value, row, column);

                        /*
                        TodoClient.output.writeObject(Entity.Action.UPDATE);
                        TodoClient.output.writeObject(value);
                        TodoClient.output.writeObject(row);
                        TodoClient.output.writeObject(column);*/
                    }

                } catch (IOException ex) {
                    System.err.println(this.getClass().getName() + " " + ex);
                    ex.printStackTrace(System.err);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        todosNameTextArea = new javax.swing.JTextArea();
        deleteTodosButton = new javax.swing.JButton();
        addTodosButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        todosTabbedPane = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        todosTable = new JTable() {
            {
                getTableHeader().setFont ( new Font( "Times New Roman" , 1 , 14 ) );
                DefaultTableCellRenderer centerHeaderRenderer = (DefaultTableCellRenderer) getTableHeader().getDefaultRenderer();
                centerHeaderRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            }
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                if( (renderer instanceof DefaultTableCellRenderer) ) {
                    DefaultTableCellRenderer renderCenter = (DefaultTableCellRenderer) renderer;
                    renderCenter.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
                }
                Component component = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    Boolean type = (Boolean) getModel().getValueAt(convertRowIndexToModel(row), getColumnModel().getColumnIndex("completed"));
                    component.setBackground(Boolean.TRUE.equals(type) ? Color.GREEN : getBackground());
                }
                return component;
            }
        };
        jScrollPane3 = new javax.swing.JScrollPane();
        privateTodosTable = new JTable() {
            {
                getTableHeader().setFont ( new Font( "Times New Roman" , 1 , 14 ) );
                DefaultTableCellRenderer centerHeaderRenderer = (DefaultTableCellRenderer) getTableHeader().getDefaultRenderer();
                centerHeaderRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            }
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                if( (renderer instanceof DefaultTableCellRenderer) ) {
                    DefaultTableCellRenderer renderCenter = (DefaultTableCellRenderer) renderer;
                    renderCenter.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
                }
                Component component = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    Boolean type = (Boolean) getModel().getValueAt(convertRowIndexToModel(row), getColumnModel().getColumnIndex("completed"));
                    component.setBackground(Boolean.TRUE.equals(type) ? Color.GREEN : getBackground());
                }
                return component;
            }
        };
        UserLabel = new javax.swing.JLabel();
        logOutButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Todo List");
        setMaximumSize(new java.awt.Dimension(900, 900));

        todosNameTextArea.setColumns(20);
        todosNameTextArea.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        todosNameTextArea.setRows(5);
        jScrollPane1.setViewportView(todosNameTextArea);

        deleteTodosButton.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        deleteTodosButton.setText("Delete Selected Todo(s)");
        deleteTodosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTodosButtonActionPerformed(evt);
            }
        });

        addTodosButton.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        addTodosButton.setText("Add New Todo");
        addTodosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTodosButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel1.setText("Todo Name:");

        todosTabbedPane.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N

        todosTable.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        todosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "name", "completed"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class

                , java.lang.String.class

                , java.lang.Boolean.class

            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        todosTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        todosTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        todosTable.setDoubleBuffered(true);
        todosTable.setRowHeight(26);
        todosTable.setRowMargin(5);
        todosTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(todosTable);
        todosTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        todosTabbedPane.addTab("Global Todos", jScrollPane2);

        privateTodosTable.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        privateTodosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "name", "completed"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class

                , java.lang.String.class

                , java.lang.Boolean.class

            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        privateTodosTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        privateTodosTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        privateTodosTable.setDoubleBuffered(true);
        privateTodosTable.setRowHeight(26);
        privateTodosTable.setRowMargin(5);
        privateTodosTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(privateTodosTable);
        privateTodosTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        todosTabbedPane.addTab("Private Todos", jScrollPane3);

        UserLabel.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        UserLabel.setText("Welcome");

        logOutButton.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        logOutButton.setForeground(new java.awt.Color(255, 51, 51));
        logOutButton.setText("Logout");
        logOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1017, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 685, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(addTodosButton, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(todosTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
                                        .addComponent(UserLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(logOutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(deleteTodosButton)
                                        .addGap(0, 104, Short.MAX_VALUE)))))))
                .addGap(62, 62, 62))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logOutButton)
                            .addComponent(UserLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(75, 75, 75)
                        .addComponent(deleteTodosButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(todosTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(70, 70, 70)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(42, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addTodosButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(88, 88, 88))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteTodosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTodosButtonActionPerformed
        int[] selectedTodos = todosTabbedPane.getSelectedIndex() == 0 ? todosTable.getSelectedRows() : privateTodosTable.getSelectedRows();
        if (selectedTodos.length > 0) {
            EntityTableModel entityTableModel = todosTabbedPane.getSelectedIndex() == 0 ? (EntityTableModel) todosTable.getModel() : (EntityTableModel) privateTodosTable.getModel();
            for (int i = selectedTodos.length - 1; i >= 0; i--) {
                try {
                    if (selectedTodos[i] < entityTableModel.entites.size()) {
                        TodoClient.remoteEntity.delete(entityTableModel.entites.get(selectedTodos[i]));
                        if (todosTabbedPane.getSelectedIndex() == 1) {
                            entityTableModel.removeRow(selectedTodos[i]);
                        } else {
                            TodoClient.serverHandler.broadcast(Entity.Action.DELETE, selectedTodos[i]);
                        }
                    }
                } catch (IOException ex) {
                    System.err.println(TodoClientGUI.class.getName() + " " + ex);
                    ex.printStackTrace(System.err);
                }
            }
        }
    }//GEN-LAST:event_deleteTodosButtonActionPerformed

    private void addTodosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTodosButtonActionPerformed
        String todoName = todosNameTextArea.getText();
        if (todoName.trim().length() > 0) {
            try {
                Todo newTodo = new Todo(todoName, TodoClient.user.getId(), false, todosTabbedPane.getSelectedIndex() == 1);
                Integer newTodoId = TodoClient.remoteEntity.create(newTodo);
                if (newTodoId > -1) {
                    newTodo.setId(newTodoId);
                    newTodo.setUser(user.getName());
                    if (todosTabbedPane.getSelectedIndex() == 1) {
                        EntityTableModel entityTableModel = (EntityTableModel) privateTodosTable.getModel();
                        entityTableModel.addRow(newTodo);
                    } else {
                        /*TodoClient.output.writeObject(Entity.Action.CREATE);
                        TodoClient.output.writeObject(newTodo);*/

                        TodoClient.serverHandler.broadcast(Entity.Action.CREATE, newTodo);

                    }
                    todosNameTextArea.setText("");

                }
            } catch (IOException ex) {
                System.err.println(TodoClientGUI.class.getName() + " " + ex);
                ex.printStackTrace(System.err);
            }
        }
    }//GEN-LAST:event_addTodosButtonActionPerformed

    private void logOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutButtonActionPerformed
        try {
            String userFileName = "user.ser";  
            File userObjectFile = new File(userFileName);
            Files.deleteIfExists(userObjectFile.toPath());
            
            EventQueue.invokeLater(() -> {
                new UserNameFrame().setVisible(true);
                dispose();
            });

        } catch (IOException ex) {
            System.err.println(TodoClientGUI.class.getName() + " " + ex);
            ex.printStackTrace(System.err);
        }

    }//GEN-LAST:event_logOutButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel UserLabel;
    private javax.swing.JButton addTodosButton;
    private javax.swing.JButton deleteTodosButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton logOutButton;
    public javax.swing.JTable privateTodosTable;
    private javax.swing.JTextArea todosNameTextArea;
    private javax.swing.JTabbedPane todosTabbedPane;
    public javax.swing.JTable todosTable;
    // End of variables declaration//GEN-END:variables
}
