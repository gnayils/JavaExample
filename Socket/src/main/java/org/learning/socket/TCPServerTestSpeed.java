/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.learning.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Administrator
 */
public class TCPServerTestSpeed {
    
    public static void main(String[] args) throws Exception {
        File f = new File("/home/sg/lf489159/Documents/Pictures/desktop.bmp");
        FileInputStream fis = new FileInputStream(f);
        MappedByteBuffer mbb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.length());
        byte[] buffer = new byte[(int)f.length()];
        mbb.get(buffer);
        ServerSocket ss = new ServerSocket(12345);
        Socket s = ss.accept();
        OutputStream os = s.getOutputStream();
        long traffic = 0;
        long startTime = System.currentTimeMillis();
        while(true) {
            os.write(buffer);
            os.flush();
            traffic += buffer.length;
            if((System.currentTimeMillis() - startTime) >= 1000) {
                System.out.println("traffic per second: " + traffic / 1024 / 1024 + "MB");
                traffic = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }
}
