/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.learning.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class UDPServer {

    public static final int PORT = 12345;

    public static final int REQUEST_CODE_LENGTH = 4;
    public static final byte[] REQUEST_CODE_DATA = new byte[REQUEST_CODE_LENGTH];
    public static final int BUFFER_LENGTH = 1472;
    public static final byte[] DATA_BUFFER = new byte[BUFFER_LENGTH];
    
    public static final int REQUEST_DATA = 10000;
    
    public static final String PATH = "C:/pics";

    public static void main(String[] args) {
        try {
            DatagramSocket server = new DatagramSocket(PORT);
            DatagramPacket packet4Receive = new DatagramPacket(REQUEST_CODE_DATA, REQUEST_CODE_DATA.length);
            boolean listening = true;
            while (listening) {
                packet4Receive.setData(REQUEST_CODE_DATA);
                server.receive(packet4Receive);
                int requestCode = ByteBuffer.wrap(packet4Receive.getData(), 0, packet4Receive.getLength()).getInt();
                switch(requestCode) {
                    case REQUEST_DATA:
                        sendData(server, packet4Receive.getAddress(), packet4Receive.getPort());
                        break;
                }
            }

        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
     
    }
    
    public static void sendData(DatagramSocket server, InetAddress address, int port){
        File directory = new File(PATH);
        File[] files = directory.listFiles();
        try {
            DatagramPacket packet4Send = new DatagramPacket(DATA_BUFFER, DATA_BUFFER.length, address, port);
            int count = 0;
            int fileIndex = 0;
            int fileSentLength = 0;
            while (true) {
                FileInputStream fis = new FileInputStream(files[fileIndex]);
                int readLength = 0;
                while ((readLength = fis.read(DATA_BUFFER)) != -1) {
                    packet4Send.setData(DATA_BUFFER, 0, readLength);
                    server.send(packet4Send);
                    fileSentLength += readLength;
                }
                fis.close();
                System.out.println("file: " + files[fileIndex].getName() + ", length: " + fileSentLength + ", " + count++);
                fileSentLength = 0;
                fileIndex++;
                if (fileIndex >= files.length) {
                    fileIndex = 0;
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
    public static final int PORT = 12345;
    
    public static void main(String[] args) throws SocketException, IOException {
        DatagramSocket server = new DatagramSocket(PORT);
        byte[] buffer = new byte[100];
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        server.receive(receivePacket);
        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println(message);
        
        
        message = "hello, client.";
        buffer = message.getBytes();
        receivePacket = new DatagramPacket(buffer, buffer.length, receivePacket.getAddress(), receivePacket.getPort());
        server.send(receivePacket);
        server.close();
    }
    */
}
