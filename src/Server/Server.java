package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Сервер запущен");
        new Server().startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("Ожидание подключения клиента...");
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
                    new ClientHandler(clientSocket, clientWriters).start();
                } catch (IOException e) {
                    System.out.println("Ошибка при принятии соединения: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
}
