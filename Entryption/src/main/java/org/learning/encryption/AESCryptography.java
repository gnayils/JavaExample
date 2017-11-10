package org.learning.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AESCryptography {

    public static void doFinal(int mode, String key, File inputFile, File outputFile) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, secretKey);
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(inputFile);
            os = new FileOutputStream(outputFile);
            byte[] buffer = new byte[102400];
            int readLength;
            while ((readLength = is.read(buffer)) != -1) {
                byte[] finalBytes = cipher.doFinal(buffer, 0, readLength);
                os.write(finalBytes);
            }
            os.flush();

        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        String key = "Mary has one cat";

//        File inputFile = new File("/Users/iUser/Desktop/google.png");
//        File encryptedFile = new File("/Users/iUser/Desktop/google.encrypted.png");
//        File decryptedFile = new File("/Users/iUser/Desktop/google.decrypted.png");

        File inputFile = new File("/Users/iUser/Desktop/Code.java");
        File encryptedFile = new File("/Users/iUser/Desktop/Code.encrypted.java");
        File decryptedFile = new File("/Users/iUser/Desktop/Code.decrypted.java");

        //doFinal(Cipher.ENCRYPT_MODE, key, inputFile, encryptedFile);
        doFinal(Cipher.DECRYPT_MODE, key, encryptedFile, decryptedFile);
    }
}
