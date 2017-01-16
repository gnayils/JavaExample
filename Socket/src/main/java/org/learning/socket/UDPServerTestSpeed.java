/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.learning.socket;

import static org.learning.socket.UDPServer.REQUEST_CODE_DATA;
import static org.learning.socket.UDPServer.REQUEST_DATA;
import static org.learning.socket.UDPServer.sendData;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Administrator
 */
public class UDPServerTestSpeed {
    
    public static void main(String[] args) throws Exception {
        File f = new File("/home/sg/lf489159/Documents/Pictures/desktop.bmp");
        FileInputStream fis = new FileInputStream(f);
        MappedByteBuffer mbb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.length());
        byte[] buffer = new byte[1024 * 63];
        mbb.get(buffer);
        
        DatagramSocket ds = new DatagramSocket();
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("3.35.62.94"), 12345);
        
        long traffic = 0;
        long startTime = System.currentTimeMillis();
        while(true) {
            if(mbb.remaining() >= buffer.length) {
                mbb.get(buffer);
                dp.setData(buffer);
            } else {
                mbb.get(buffer, 0, mbb.remaining());
                dp.setData(buffer, 0, mbb.remaining());
                mbb.clear();
            }
            ds.send(dp);
            traffic += buffer.length;
            if((System.currentTimeMillis() - startTime) >= 1000) {
                System.out.println("traffic per second: " + traffic / 1024 / 1024 + "MB");
                traffic = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }
}
