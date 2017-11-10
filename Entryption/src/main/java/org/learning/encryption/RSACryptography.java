package org.learning.encryption;


import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import sun.security.util.BigInt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;

public class RSACryptography {


    public static void main(String[] args) throws Exception {
        RSACryptoUsageA.main(args);
        RSACryptoUsageB.main(args);
        RSACryptoUsageC.main(args);
    }

    static class RSACryptoUsageC {

        static String data = "hello world";
        static String modulusString = "104870442480913195087614139514328167913661711756436741132314972679456823503187634004975808308908949120932485729721087243359095210908000571427043045452979513456493718611158983513891959377411859468519789856134823369691968409776942242231706247459247870725893637205407849705491854973001591976625101499673045429217";
        static String publicExponentString = "65537";
        static String privateExponentString = "53639350327337095267282810788711636428793723237064803080646508067016680358428867349448312713779039069270455683750866922267424974668463419973540289098209035784941246436865540491148938668684976571711506089104103934581228143737339092222637164857408935460122526631063773134109190539674036318680678980411804256853";

        static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {

            BigInteger modulus = new BigInteger(modulusString);
            BigInteger publicExponent = new BigInteger(publicExponentString);
            BigInteger privateExponent = new BigInteger(privateExponentString);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
            PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);

            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
            PrivateKey privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);

            KeyPair keyPair = new KeyPair(publicKey, privateKey);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
            System.out.println("encrypted: " + new String(encryptedBytes, "UTF-8"));

            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            System.out.println("decrypted: " + new String(decryptedBytes, "UTF-8"));

        }
    }

    static class RSACryptoUsageB {

        static String data = "hello world";
        static String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVVyjcxL5tvW6GEVKwzJbTXF43xdQSueNzEBLvNgOam2SVDmbniswcTrdUrIuIWc2kCpPK3NNMSpndTrAxNT7hrxbnzpTY6sl1LtbXTvM2lg6vV2CrylmlcWc80WJUkfSmML6oyXkJ4AjiTHM0cWkoTEMF+ACDaZmb9oYbeNTr4QIDAQAB";
        static String privateKeyString = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJVXKNzEvm29boYRUrDMltNcXjfF1BK543MQEu82A5qbZJUOZueKzBxOt1Ssi4hZzaQKk8rc00xKmd1OsDE1PuGvFufOlNjqyXUu1tdO8zaWDq9XYKvKWaVxZzzRYlSR9KYwvqjJeQngCOJMczRxaShMQwX4AINpmZv2hht41OvhAgMBAAECgYBMYo1rBlLkrJzIhDnIZozKxRcH7e8AOQZGhzU4F2eWf97dgsVNryehXv3UmICyGETfn+1iOLr27b1vfd0O/k4PtZreM+g00WvG0K94W5LVOybpFtH61V4LQobcu+3zYd4fn0K9hWVzGdXvx0/N9ODhZBnPa35VoTVlqCZLr2qKVQJBAN6cxa47VRkBeo3CiZ8DYu8G1aRrgDxEusAwgYLzWwjYM5xOljyAx740BlTsdVw1biuStW4e7a3EwJkEtKPfousCQQCrvRqJcm3OCNSlmSYYwNSD+75v36yzpDR/4LjDzSbisHbYkoZPssPzjv+2vLkDifZI1b1VQahPdlHHzDMjogFjAkAl6FxhRwk+sNnFuhLlWRZHzcojrYqwuKN0hs4HFpmx798aBufeda+N3B4X6Aw0H4UbNmhe0DV5GcASTpLUNQjxAkEAmIqAHqN/+qiHUIuGdigY2x5pGW+AM5PfedI96tS2/FUnSthi6jlrqVNTuJlKcGVDV5BZ+nc2K1Z571Bl//lu5wJBALB/QOVWYMsv1ScpAOOiEjPVKzIT3UIMXgmsG30N0RffZeH0TgrioCe52B75qhtsb/syy1C+nkobRC8flwwRk20=";

        static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            byte[] publicKeyBytes = Base64.decode(publicKeyString);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

            byte[] privateKeyBytes = Base64.decode(privateKeyString);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

            KeyPair keyPair = new KeyPair(publicKey, privateKey);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
            System.out.println("encrypted: " + new String(encryptedBytes, "UTF-8"));

            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            System.out.println("decrypted: " + new String(decryptedBytes, "UTF-8"));

        }
    }

    static class RSACryptoUsageA {

        static String data = "hello world";

        static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            System.out.println("public key: " + Base64.encode(keyPair.getPublic().getEncoded()));
            System.out.println("private key: " + Base64.encode(keyPair.getPrivate().getEncoded()));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
            System.out.println("encrypted: " + new String(encryptedBytes, "UTF-8"));

            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            System.out.println("decrypted: " + new String(decryptedBytes, "UTF-8"));
        }
    }
}
