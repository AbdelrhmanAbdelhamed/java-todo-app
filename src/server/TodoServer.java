 package server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author AAGOOGLE
 */
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import rmi.Registry;
import rmi.RemoteEntityImpl;
import java.util.ArrayList;
import java.rmi.Remote;

public class TodoServer {

    public static ArrayList<TodoClientHandler> clients = new ArrayList<>();

    public static final int SOCKET_PORT = 3333;

    public static void main(String[] args) throws Exception {

        Remote RemoteEntityImpl = new RemoteEntityImpl();
        Registry.put("entity", RemoteEntityImpl);

        Socket clientSocket;
        ServerSocket serverSocket = new ServerSocket(SOCKET_PORT);

        System.out.println("Server is listening on port: " + SOCKET_PORT);

        while (true) {
            clientSocket = serverSocket.accept();

            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

            TodoClientHandler client = new TodoClientHandler(clientSocket, input, output);
            clients.add(client);

            Thread clientThread = new Thread(client);
            clientThread.start();

        }
    }
}
