package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Set<PrintWriter> clientWriters;

    public ClientHandler(Socket socket, Set<PrintWriter> clientWriters) {
        this.socket = socket;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        handleClient();
    }

    private void handleClient() {
        System.out.println("Обработка клиента: " + socket.getInetAddress());
        try {
            setupStreams();
            addClientWriter();
            waitForMessages();
        } catch (IOException e) {
            System.out.println("Ошибка при обработке клиента: " + e.getMessage());
        }
    }

    private void setupStreams() throws IOException {
        System.out.println("Настройка потоков для: " + socket.getInetAddress());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Потоки успешно настроены для: " + socket.getInetAddress());
    }

    private void addClientWriter() {
        synchronized (clientWriters) {
            clientWriters.add(out);
        }
    }

    private void waitForMessages() throws IOException {
        String message;
        while ((message = in.readLine()) != null) {
            System.out.println("Получено сообщение от " + socket.getInetAddress() + ": " + message);
            if (message.contains("exit")) {
                System.out.println("Клиент отключился: " + socket.getInetAddress());
                break; // Выход из цикла при команде на выход
            }
            broadcastMessage(message); // Рассылка сообщения с именем клиента
        }
        cleanup(); // Очистка ресурсов
    }

    private void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    private void cleanup() {
        System.out.println("Очистка ресурсов для: " + socket.getInetAddress());
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии сокета: " + e.getMessage());
        }
        synchronized (clientWriters) {
            clientWriters.remove(out);
        }
    }
}