/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.learning.socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author Administrator
 */
public class UDPClientTestSpeed {
    
    public static void main(String[] args) throws Exception {
        byte[] buffer = new byte[1024 * 63];
        DatagramSocket ds = new DatagramSocket(12345);
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        long traffic = 0;
        long startTime = System.currentTimeMillis();
        while(true) {
            ds.receive(dp);
            traffic += dp.getLength();
            if((System.currentTimeMillis() - startTime) >= 1000) {
                System.out.println("traffic per second: " + traffic / 1024 / 1024 + "MB" + ", datagram packet size: " + dp.getLength());
                traffic = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }
}
