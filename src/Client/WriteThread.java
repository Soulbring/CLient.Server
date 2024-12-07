package Client;

import java.io.*;
import java.net.*;

public class WriteThread extends Thread {
    private PrintWriter out;
    private BufferedReader keyboard;
    private Socket socket;
    private String clientName;

    public WriteThread(Socket socket, String clientName) {
        this.socket = socket;
        this.clientName = clientName;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            keyboard = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Поток записи успешно настроен.");
        } catch (IOException e) {
            System.err.println("Ошибка при получении потока вывода: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while (true) {
                message = keyboard.readLine();
                out.println(clientName + ": " + message);
                if (message.equalsIgnoreCase("exit code 0")) {
                    System.out.println("Команда на выход отправлена. Завершение работы клиента.");
                    break;
                }
            }
        } catch (IOException e) {
            if ("Socket closed".equals(e.getMessage())) {
                System.out.println("Сокет был закрыт, остановка потока записи.");
            } else {
                e.printStackTrace();
            }
        } finally {
            close();
        }
    }

    private void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (keyboard != null) {
                keyboard.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии потоков или сокета: " + e.getMessage());
        }
    }
}
