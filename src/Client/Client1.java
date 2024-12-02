package Client;

import java.io.*;
import java.net.*;

public class Client1 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        new Client1().startClient();
    }

    private void startClient() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Подключен к серверу: " + SERVER_ADDRESS);
            new ReadThread(socket).start();
            new WriteThread(socket).start();
        } catch (UnknownHostException e) {
            System.err.println("Сервер не найден: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        }
    }

    private static class ReadThread extends Thread {
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

    private static class WriteThread extends Thread {
        private PrintWriter out;
        private BufferedReader keyboard;
        private Socket socket;

        public WriteThread(Socket socket) {
            this.socket = socket;
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
                    System.out.print("Введите текст(exit code 0 команда на выход):");
                    message = keyboard.readLine();
                    out.println(message);
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
}
