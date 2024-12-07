package Client;

import java.io.*;
import java.net.*;

public class ReadThread extends Thread {
    private BufferedReader in;

    public ReadThread(Socket socket) {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Поток чтения успешно настроен.");
        } catch (IOException e) {
            System.err.println("Ошибка при получении потока ввода: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            if ("Socket closed".equals(e.getMessage())) {
                System.out.println("Сокет был закрыт, остановка потока чтения.");
            } else {
                e.printStackTrace();
            }
        } finally {
            close();
        }
    }

    private void close() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии потока ввода: " + e.getMessage());
        }
    }
}
