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

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("Ожидание подключения клиента...");
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    System.out.println("Ошибка при принятии соединения: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
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
                System.out.println("Получено сообщение: " + message);
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Клиент отключился: " + socket.getInetAddress());
                    break;
                }
                broadcastMessage(message);
            }
            cleanup();
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
}
