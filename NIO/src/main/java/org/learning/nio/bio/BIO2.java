package org.learning.nio.bio;

import org.learning.nio.Calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIO2 {

    static class Server {

        static final String IP = "127.0.0.1";
        static final int PORT = 12345;

        static ServerSocket server;

        static ExecutorService executorService = Executors.newFixedThreadPool(5);

        static void start() throws IOException {
            start(PORT);
        }

        static synchronized void start(int port) throws IOException {
            if (server != null) return;
            try {
                server = new ServerSocket(port);
                System.out.println("server started with port: " + port);
                while (true) {
                    Socket socket = server.accept();
                    executorService.execute(new ServerHandler(socket));
                }
            } finally {
                if (server != null) {
                    System.out.println("server closed");
                    server.close();
                    server = null;
                }
            }
        }

        static class ServerHandler implements Runnable {

            Socket socket;

            ServerHandler(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run() {
                BufferedReader in = null;
                PrintWriter out = null;
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    String expression, result;
                    while (true) {
                        if ((expression = in.readLine()) == null) break;
                        System.out.println("server got message: " + expression);
                        try {
                            result = Calculator.cal(expression).toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            result = "calculate error: " + e.getMessage();
                        }
                        out.println(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {
                        }
                        in = null;
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e) {
                        }
                        out = null;
                    }
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                        }
                        socket = null;
                    }
                }
            }
        }
    }

    static class Client {

        static void send(String expression) {
            Socket socket = null;
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                socket = new Socket(Server.IP, Server.PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(expression);
                System.out.printf("expression is %s, result is: %s\n", expression, in.readLine());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                    in = null;
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                    }
                    out = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                    }
                    socket = null;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(100);
        final Random random = new Random(System.currentTimeMillis());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    String expression = String.format("%d%s%d", random.nextInt(10), Calculator.operatorChars[random.nextInt(4)], random.nextInt(10));
                    Client.send(expression);
                    try {
                        Thread.sleep(random.nextInt(200));
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
