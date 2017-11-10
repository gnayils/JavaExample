package org.learning.nio.nio;

import org.learning.nio.Calculator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client extends Thread {

    public static final int PORT = Server.DEFAULT_PORT;
    public static final String IP = Server.DEFAULT_HOST;

    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean isRunning;

    ByteBuffer buffer = ByteBuffer.allocate(1024);

    public Client() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
    }

    @Override
    public void run() {
        try {
            if(socketChannel.connect(new InetSocketAddress(IP, PORT)));
            else socketChannel.register(selector, SelectionKey.OP_CONNECT);
            isRunning = true;
            while (isRunning) {
                selector.select(1000);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();
                SelectionKey selectionKey = null;
                while(selectionKeyIterator.hasNext()) {
                    selectionKey = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    if(selectionKey.isValid()) {
                        SocketChannel sc = (SocketChannel)selectionKey.channel();
                        assert sc == socketChannel;
                        if(selectionKey.isConnectable()) {
                            if(sc.finishConnect()) {
                                sc.register(selector, SelectionKey.OP_READ);
                            }
                        } else if(selectionKey.isReadable()) {
                            buffer.clear();
                            int readBytes = socketChannel.read(buffer);
                            if(readBytes > 0) {
                                buffer.flip();
                                byte[] bytes = new byte[buffer.remaining()];
                                buffer.get(bytes);
                                String result = new String(bytes, "UTF-8");
                                System.out.printf("client receive message: %s\n", result);
                            }
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean send(String message) throws IOException {
        if(isRunning) {
            if ("quit".equals(message)) {
                isRunning = false;
                return false;
            }
            socketChannel.write(ByteBuffer.wrap(message.getBytes("UTF-8")));
            System.out.println("client send message: " + message);
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.start();
        for(int i=0; i<10000; i++) {
            client.send(Calculator.generateRandom());
        }
    }

}
