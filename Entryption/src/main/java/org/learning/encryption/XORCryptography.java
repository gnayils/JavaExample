package org.learning.encryption;

import java.io.*;

public class XORCryptography {

    public static void doFinal(String key, File inputFile, File outputFile) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
            outputStream = new FileOutputStream(outputFile);
            byte[] keyBytes = key.getBytes("UTF-8");
            int i = 0;
            while (inputStream.available() > 0) {
                int sourceByte = inputStream.read();
                int targetByte = sourceByte ^ keyBytes[i++ % keyBytes.length];
                outputStream.write(targetByte);
            }
            outputStream.flush();
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
            if(outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
//        File inputFile = new File("/Users/iUser/Desktop/Code.java");
//        File encryptedFile = new File("/Users/iUser/Desktop/Code.encrypted.java");
//        File decryptedFile = new File("/Users/iUser/Desktop/Code.decrypted.java");


        File inputFile = new File("/Users/iUser/Desktop/google.png");
        File encryptedFile = new File("/Users/iUser/Desktop/google.encrypted.png");
        File decryptedFile = new File("/Users/iUser/Desktop/google.decrypted.png");

        String key = "password";
        //doFinal(key, inputFile, encryptedFile);

        doFinal(key, encryptedFile, decryptedFile);
    }
}
