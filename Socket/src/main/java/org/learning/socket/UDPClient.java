/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.learning.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 *
 * @author Administrator
 */
public class UDPClient {

    public static final String IP = "192.168.1.2";
    public static final int PORT = 12345;

    
    public static final int REQUEST_CODE_LENGTH = 4;
    public static final int BUFFER_LENGTH = 1472;
    public static final byte[] DATA_BUFFER = new byte[BUFFER_LENGTH];

    public static final int REQUEST_DATA = 10000;


    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket client = new DatagramSocket();
        ByteBuffer bytePacker = ByteBuffer.allocate(REQUEST_CODE_LENGTH);
        bytePacker.putInt(REQUEST_DATA);
        byte[] requestCodeData = bytePacker.array();
        DatagramPacket packet4Send = new DatagramPacket(requestCodeData, requestCodeData.length, InetAddress.getByName(IP), PORT);
        client.send(packet4Send);
        
        DatagramPacket packet4Receive = new DatagramPacket(DATA_BUFFER, DATA_BUFFER.length);
        int receivedFileLength = 0;
        boolean receiving = true;
        while(receiving) {
            packet4Receive.setData(DATA_BUFFER);
            client.receive(packet4Receive);
        }
    }

//    public static final int PORT = 12345;
//    
//    public static final int BUFFER_LENGTH = 1472;
//    public static final byte[] BUFFER = new byte[BUFFER_LENGTH];
//    
//    public static void main(String[] args) throws SocketException, IOException {
//        DatagramSocket client = new DatagramSocket();
//        String message = "hello, server.";
//        byte[] buffer = message.getBytes();
//        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("192.168.1.2"), PORT);
//        client.send(packet);
//        
//        buffer = new byte[100];
//        packet = new DatagramPacket(buffer, buffer.length);
//        client.receive(packet);
//        message = new String(packet.getData(), 0, packet.getLength());
//        System.out.println(message);
//        client.close();
//    }
}
