package org.learning.nio.nio;

import org.demo.Calculator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import org.demo.Calculator;

public class NIO {

    public static void main(String[] args) throws InterruptedException, IOException {
        Server server =  new Server();
        server.start();
        Thread.sleep(500);
        Client client = new Client();
        client.start();
        while(client.send(new Scanner(System.in).nextLine()));
    }
}
