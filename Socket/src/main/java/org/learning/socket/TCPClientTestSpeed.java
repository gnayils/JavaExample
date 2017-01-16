/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.learning.socket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 *
 * @author Administrator
 */
public class TCPClientTestSpeed {

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("3.35.62.94", 12345);
        InputStream is = s.getInputStream();
        byte[] buffer = new byte[15552054];
        long traffic = 0;
        long startTime = System.currentTimeMillis();
        while (true) {
            int readLength = 0;
            do {
                readLength += is.read(buffer, readLength, buffer.length - readLength);
            } while (readLength < buffer.length);
            traffic += readLength;
            if((System.currentTimeMillis() - startTime) >= 1000) {
                System.out.println("traffic per second: " + traffic / 1024 / 1024 + "MB");
                traffic = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }
}
