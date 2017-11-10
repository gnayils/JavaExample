package org.learning.encryption;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * To use the code, you need corresponding public and private RSA keys. RSA keys can be generated using the open source tool OpenSSL.
 * However, you have to be careful to generate them in the format required by the Java encryption libraries.
 *
 * To generate a private key of length 2048 bits:
 * bash$: openssl genrsa -out private.pem 2048
 *
 * To get it into the required (PKCS#8, DER) format:
 * bash$: openssl pkcs8 -topk8 -in private.pem -outform DER -out private.der -nocrypt
 *
 * To generate a public key from the private key:
 * bash$: openssl rsa -in private.pem -pubout -outform DER -out public.der
 */

public class RSAAESCryptography {

    Cipher rsaCipher, aesCipher;
    byte[] aesKey;
    SecretKeySpec aesKeySpec;

    public RSAAESCryptography() throws NoSuchPaddingException, NoSuchAlgorithmException {
        rsaCipher = Cipher.getInstance("RSA");
        aesCipher = Cipher.getInstance("AES");
    }

    public void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
        aesKeyGenerator.init(256);
        SecretKey aesSecretKey = aesKeyGenerator.generateKey();
        aesKey = aesSecretKey.getEncoded();
        System.out.println("aesKey.length: " + aesKey.length);
        aesKeySpec = new SecretKeySpec(aesKey, "AES");
    }

    public void loadKey(File in, File privateKeyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        byte[] encodedKey = new byte[(int) privateKeyFile.length()];
        FileInputStream fileInputStream = new FileInputStream(privateKeyFile);
        fileInputStream.read(encodedKey);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);

        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        aesKey = new byte[256 / 8];
        CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(in), rsaCipher);
        cipherInputStream.read(aesKey);
        aesKeySpec = new SecretKeySpec(aesKey, "AES");
    }

    public void saveKey(File out, File publicKeyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        byte[] encodedKey = new byte[(int) publicKeyFile.length()];
        FileInputStream fileInputStream = new FileInputStream(publicKeyFile);
        fileInputStream.read(encodedKey);

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);

        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        CipherOutputStream cipherOutputStream = new CipherOutputStream(new FileOutputStream(out), rsaCipher);
        cipherOutputStream.write(aesKey);
        cipherOutputStream.flush();
        cipherOutputStream.close();
    }

    public void encrypt(File in, File out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);
        copy(is, os);
        os.flush();
        os.close();
        is.close();
    }

    public void decrypt(File in, File out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec);
        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
        FileOutputStream os = new FileOutputStream(out);
        copy(is, os);
        os.flush();
        os.close();
        is.close();
    }

    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] bytes = new byte[1024];
        while((i = is.read(bytes)) != -1) {
            os.write(bytes, 0, i);
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IOException {
        RSAAESCryptography rsaCrypto = new RSAAESCryptography();
        if(RSAAESCryptography.class.getResource("/aes_key") == null) {
            rsaCrypto.makeKey();
            rsaCrypto.saveKey(
                    new File(RSAAESCryptography.class.getResource("/").getPath() + "aes_key"),
                    new File(RSAAESCryptography.class.getResource("/rsa_public_key.der").getPath()));
        }
        rsaCrypto.loadKey(
                new File(RSAAESCryptography.class.getResource("/aes_key").getPath()),
                new File(RSAAESCryptography.class.getResource("/rsa_private_key.der").getPath())
        );

//        rsaCrypto.encrypt(
//                new File("/Users/iUser/Desktop/google.png"),
//                new File("/Users/iUser/Desktop/google.encrypted.png")
//                );

        rsaCrypto.decrypt(
                new File("/Users/iUser/Desktop/google.encrypted.png"),
                new File("/Users/iUser/Desktop/google.decrypted.png")
        );

    }
}
