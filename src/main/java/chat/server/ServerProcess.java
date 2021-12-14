/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.User;

/**
 *
 * @author dangt
 */
public class ServerProcess {
    private ServerSocket serverSocket;
    public static Map<User, ClientHandler> mapClients;

    public ServerProcess() {
        mapClients = new HashMap<>();
    }
    
    public void start(int port) {
        System.out.println("Server starting!!!");
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(serverSocket.getInetAddress().getHostName());
            System.out.println(serverSocket.getLocalPort());
            while (true) {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
