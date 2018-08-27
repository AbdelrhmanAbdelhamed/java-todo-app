package server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import models.Entity;

/**
 *
 * @author AAGOOGLE
 */
public class TodoClientHandler implements Runnable {

    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final Socket serverSocket;

    public TodoClientHandler(Socket serverSocket, ObjectInputStream input, ObjectOutputStream output) {
        this.serverSocket = serverSocket;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Entity.Action action = (Entity.Action) input.readObject();
                Object[] data = (Object[]) input.readObject();

                sendToAll(action);
                sendToAll(data);

            } catch (IOException | ClassNotFoundException ex) {
                try {
                    this.logOut();
                    break;
                } catch (IOException ex1) {
                    System.err.println(this.getClass().getName() + " " + ex1);
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    private void logOut() throws IOException {
        serverSocket.close();
        TodoServer.clients.remove(this);
    }

    private static void sendToAll(Object data) throws IOException {
        for (TodoClientHandler client : TodoServer.clients) {
                client.output.writeObject(data);
        }
    }
}
