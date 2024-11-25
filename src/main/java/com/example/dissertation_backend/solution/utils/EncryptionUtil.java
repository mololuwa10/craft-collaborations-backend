package com.example.dissertation_backend.solution.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
// import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

  private static final String ALGORITHM = "AES";
  private static SecretKey secretKey;
  private static final byte[] KEY;

  static {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
      keyGenerator.init(256);
      secretKey = keyGenerator.generateKey();
      KEY = secretKey.getEncoded();
    } catch (Exception e) {
      throw new RuntimeException("Error initializing encryption key", e);
    }
  }

  public static String encrypt(String data) {
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      byte[] encryptedBytes = cipher.doFinal(data.getBytes());
      return Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
      throw new RuntimeException("Error while encrypting", e);
    }
  }

  public static String decrypt(String encryptedData) {
    try {
      String trimmedData = encryptedData.trim();
      byte[] encryptedBytes = Base64.getDecoder().decode(trimmedData);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
      byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

      return new String(decryptedBytes);
    } catch (BadPaddingException e) {
      throw new IllegalArgumentException("Decryption error. Data may be corrupted or key might be wrong", e);
    } catch (Exception e) {
      throw new RuntimeException("Error while decrypting", e);
    }
  }

}
