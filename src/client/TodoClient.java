package client;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author AAGOOGLE
 */
import database.DatabaseFilter;
import database.DatabaseFilterBuilder;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import models.Entity;
import models.User;
import rmi.RemoteEntity;
import rmi.Registry;

public class TodoClient {

    public static TodoServerHandler serverHandler;
    public static RemoteEntity remoteEntity;
    public static Entity user;

    public static void startApp(String userName) throws RemoteException, FileNotFoundException, IOException {
        Socket clientSocket = new Socket("localhost", server.TodoServer.SOCKET_PORT);
        ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());

        if (user == null) {
            user = remoteEntity.read(User.class, new DatabaseFilterBuilder().where(new DatabaseFilter("name", userName)).toString());
            if (user.toString().equals("'null'")) {
                user = new User(userName);
                int newInsertedUserId = remoteEntity.create(user);
                user.setId(newInsertedUserId);
            }
            FileOutputStream fileOutputStream = new FileOutputStream("user.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(user);
            objectOutputStream.close();
            fileOutputStream.close();
        }

        EventQueue.invokeLater(() -> {
            try {
                TodoClientGUI clientGUI = new TodoClientGUI();
                clientGUI.setVisible(true);

                serverHandler = new TodoServerHandler(clientGUI.todosTable, clientSocket, input, output);
                Thread todoServerThread = new Thread(serverHandler);
                todoServerThread.start();

            } catch (NotBoundException | InstantiationException | IllegalAccessException | ClassNotFoundException | IOException ex) {
                System.err.println(TodoClient.class.getName() + " " + ex);
                ex.printStackTrace(System.err);
            }
        });
    }

    public static void main(String args[]) throws IOException, NotBoundException, ClassNotFoundException {
        remoteEntity = (RemoteEntity) Registry.get("entity");

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println(TodoClient.class.getName() + " " + ex);
            ex.printStackTrace(System.err);
        }

        EventQueue.invokeLater(() -> {

            try {

                String userFileName = "user.ser";
                if (Files.exists(Paths.get(userFileName))) {
                    FileInputStream fileInputStream = new FileInputStream(userFileName);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    user = (User) objectInputStream.readObject();

                    objectInputStream.close();
                    fileInputStream.close();

                    if (user != null) {
                        startApp(user.getName());
                    }
                } else {
                    UserNameFrame userNameFrame = new UserNameFrame();
                    userNameFrame.setVisible(true);
                }

            } catch (ClassNotFoundException | IOException ex) {
                System.err.println(TodoClient.class.getName() + " " + ex);
                ex.printStackTrace(System.err);
            }

        });

    }
}
