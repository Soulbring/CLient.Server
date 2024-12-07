package Client2;

import java.io.*;
import java.net.*;

public class Client2 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private String clientName;

    public static void main(String[] args) {
        new Client2().startClient();
    }

    private void startClient() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Введите ваше имя: ");
            clientName = reader.readLine();

            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Подключен к серверу: " + SERVER_ADDRESS);
            new ReadThread(socket).start();
            new WriteThread(socket, clientName).start();
            System.out.println("Уважаемый " + clientName + ". Для выхода из чата наберите: exit code 0 ");
        } catch (UnknownHostException e) {
            System.err.println("Сервер не найден: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        }
    }
}

