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
public class UDPServerWithCustomePacket {

    public static final int PORT = 12345;

    public static final int REQUEST_CODE_LENGTH = 4;
    public static final byte[] REQUEST_CODE_DATA = new byte[REQUEST_CODE_LENGTH];
    
    public static final int PACKET_LENGTH = 1472;
    public static final int TOTAL_LENGTH = 4;
    public static final int OFFSET = 4;
    public static final int LENGTH = 4;
    public static final int HEADER_LENGTH = TOTAL_LENGTH + OFFSET + LENGTH;
    public static final int BUFFER_LENGTH = PACKET_LENGTH - HEADER_LENGTH;
    public static final byte[] DATA_BUFFER = new byte[BUFFER_LENGTH];
    public static final ByteBuffer PACKET_BUFFER = ByteBuffer.allocate(HEADER_LENGTH + BUFFER_LENGTH);

    public static final int REQUEST_DATA = 10000;

    public static final String PATH = "C:/pics";

    public static void main(String[] args) throws SocketException, IOException {
        DatagramSocket server = new DatagramSocket(PORT);
        DatagramPacket packet4Receive = new DatagramPacket(REQUEST_CODE_DATA, REQUEST_CODE_DATA.length);
        packet4Receive.setData(REQUEST_CODE_DATA);
        server.receive(packet4Receive);

        int requestCode = ByteBuffer.wrap(packet4Receive.getData(), 0, packet4Receive.getLength()).getInt();
        if (requestCode == REQUEST_DATA) {
            File directory = new File(PATH);
            File[] files = directory.listFiles();
            sendData(server, packet4Receive.getAddress(), packet4Receive.getPort(), files);
        }
    }
    
    public static void sendData(DatagramSocket serverSocket, InetAddress address, int port, File[] files) throws FileNotFoundException, IOException {
        int count = 0;
        int fileIndex = 0;
        while (true) {
            System.out.println("file: " + files[fileIndex].getName() + ", length: " + files[fileIndex].length() + ", " + count++);
            FileInputStream fis = new FileInputStream(files[fileIndex]);
            DatagramPacket packet4Send = new DatagramPacket(PACKET_BUFFER.array(), PACKET_BUFFER.array().length, address, port);
            int offset = 0;
            int readLength = 0;
            while ((readLength = fis.read(DATA_BUFFER)) != -1) {
                PACKET_BUFFER.clear();
                PACKET_BUFFER.putInt((int)files[fileIndex].length());
                PACKET_BUFFER.putInt(offset);
                PACKET_BUFFER.putInt(readLength);
                PACKET_BUFFER.put(DATA_BUFFER);
                serverSocket.send(packet4Send);
                offset += readLength;
            }
            fis.close();
            fileIndex++;
            if (fileIndex >= files.length) {
                fileIndex = 0;
            }
        }
    }
    
    static class DelaySender extends Thread {
        
        private DatagramSocket server;
        private byte[] bytes;
        private InetAddress address;
        private int port;
        private int delayTime;
        
        public DelaySender(DatagramSocket server, byte[] bytes, InetAddress address, int port) throws IOException {
            this.server = server;
            this.bytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
            this.address = address;
            this.port = port;
            this.delayTime = (int)(Math.random() * 10);
        }
        
        public void run() {
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException ex) {
            }
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
            try {
                server.send(packet);
                System.out.println("sent in delay time: " + delayTime);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
