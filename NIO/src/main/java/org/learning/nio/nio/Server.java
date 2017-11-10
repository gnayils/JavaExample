package org.learning.nio.nio;

import org.demo.Calculator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {

    public static final int DEFAULT_PORT = 12345;
    public static final String DEFAULT_HOST = "127.0.0.1";

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ExecutorService executorService;
    private volatile boolean isRunning;


    public Server() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(DEFAULT_PORT), 1024);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        executorService = Executors.newCachedThreadPool();
        System.out.println("server start up at port: " + DEFAULT_PORT);
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();
                SelectionKey selectionKey = null;
                while (selectionKeyIterator.hasNext()) {
                    selectionKey = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    if (selectionKey.isValid()) {
                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
                            assert ssc == serverSocketChannel;
                            SocketChannel sc = ssc.accept();
                            System.out.printf("server accept the connection %s\n", sc.getRemoteAddress());
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                        } else if (selectionKey.isReadable()) {
                            executorService.execute(new ReadableKeyHandler(selectionKey));
                        }
                    }

                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (serverSocketChannel != null) {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        isRunning = false;
    }

    static class ReadableKeyHandler implements Runnable {

        SelectionKey selectionKey;
        SocketChannel socketChannel;
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        ReadableKeyHandler(SelectionKey key) {
            selectionKey = key;
            socketChannel = (SocketChannel) key.channel();
        }

        @Override
        public void run() {
            try {
                int readBytes = socketChannel.read(buffer);
                if (readBytes > 0) {
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String expression = new String(bytes, "UTF-8");
                    System.out.println("server receive the message: " + expression);
                    String result = null;
                    try {
                        result = Calculator.cal(expression).toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("server send message: " + result);
                    socketChannel.write(ByteBuffer.wrap(result.getBytes()));
                }
            } catch (IOException e) {
                if (selectionKey.channel() != null) {
                    try {
                        selectionKey.channel().close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                selectionKey.cancel();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }
}

