package org.minjae;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae
 * @fileName : decrypt
 * @date : 2024-10-17
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2024-10-17
 * MinjaeKim       최초 생성
 */
public class decrypt {

    @Test
    void decrypt() throws Exception {
//         String hexEncodedString = "838a49bd95758877d89f42f995330380";
         String hexEncodedString = "5ea71e8e6601fb57c3b0ad950caff4ae6881a665afb8a40165466dae384012738fed19f85c288654f2ea5db580495ff3eeba2747e143579767caa1fff5cd498a";

        byte[] encryptedBytes = hexStringToByteArray(hexEncodedString);

        // Key and IV
        String keyString = "fkdekgkxksoelovmsekakgkdkemawwre";  // 32 characters long (256-bit)
        String ivString = "sfksvnckoprtyyaj";                // 16 characters long (128-bit)

        byte[] key = keyString.getBytes("UTF-8");
        byte[] iv = ivString.getBytes("UTF-8");

        String decryptedText = decryptAES256CBC(encryptedBytes, key, iv);

        System.out.println("Decrypted Text: " + decryptedText);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String decryptAES256CBC(byte[] encryptedBytes, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

}
