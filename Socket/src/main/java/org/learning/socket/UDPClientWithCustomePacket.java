/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.learning.socket;

import java.io.File;
import java.io.FileOutputStream;
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
public class UDPClientWithCustomePacket {

    public static final String IP = "192.168.1.2";
    public static final int PORT = 12345;

    public static final int REQUEST_CODE_LENGTH = 4;

    public static final int PACKET_LENGTH = 1472;
    public static final int TOTAL_LENGTH = 4;
    public static final int OFFSET = 4;
    public static final int LENGTH = 4;
    public static final int HEADER_LENGTH = TOTAL_LENGTH + OFFSET + LENGTH;
    public static final int BUFFER_LENGTH = PACKET_LENGTH - HEADER_LENGTH;
    public static final byte[] PACKET_BUFFER = new byte[HEADER_LENGTH + BUFFER_LENGTH];

    public static final int REQUEST_DATA = 10000;

    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket client = new DatagramSocket();
        client.setSoTimeout(1000);
        ByteBuffer packetPacker = ByteBuffer.allocate(REQUEST_CODE_LENGTH);
        packetPacker.putInt(REQUEST_DATA);
        byte[] requestCodeData = packetPacker.array();
        DatagramPacket packet4Send = new DatagramPacket(requestCodeData, requestCodeData.length, InetAddress.getByName(IP), PORT);
        client.send(packet4Send);

        ByteBuffer imageBuffer = ByteBuffer.allocate(1024 * 1024 * 2);
        DatagramPacket packet4Receive = new DatagramPacket(PACKET_BUFFER, PACKET_BUFFER.length);
        try {
            while (true) {
                client.receive(packet4Receive);
                ByteBuffer buffer = ByteBuffer.wrap(PACKET_BUFFER);
                int totalLength = buffer.getInt();
                int offset = buffer.getInt();
                int length = buffer.getInt();
                imageBuffer.position(offset);
                imageBuffer.put(PACKET_BUFFER, HEADER_LENGTH, length);
                System.out.println(totalLength + " " + offset + " " + length);
                if (offset + length == totalLength) {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FileOutputStream fos = new FileOutputStream(new File("C:\\pictures\\a.bmp"));
        fos.write(imageBuffer.array());
        fos.flush();
        fos.close();
    }
}
