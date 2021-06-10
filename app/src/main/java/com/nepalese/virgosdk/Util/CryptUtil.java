package com.nepalese.virgosdk.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author nepalese on 2021/5/17 09:45
 * @usage 加密解密算法：AES, MD5
 */
public class CryptUtil {
    private static final char[] DIGITS_HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    private static final String ALGORITHM_AES = "AES";
    private static final String ALGORITHM_MD5 = "MD5";
    private static final int KEY_SIZE = 128;

    //1. Hex：1个字符两个16制表示
    /**
     * 十六进制加密
     * @param str : Stay With Me
     * @return 十六进制串 : 537461792057697468204D65
     */
    public static String hexEnCrypt(String str) {
        byte[] data = str.getBytes();
        int outLength = data.length;
        char[] out = new char[outLength << 1];
        for (int i = 0, j = 0; i < outLength; i++) {
            out[j++] = DIGITS_HEX[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_HEX[0x0F & data[i]];
        }
        return new String(out);
    }

    /**
     * 十六进制解密
     * @param hex
     * @return
     */
    public static String hexDeCrypt(String hex) {
        /*兼容带有\x的十六进制串*/
        hex = hex.replace("\\x","");
        char[] data = hex.toCharArray();
        int len = data.length;
        if ((len & 0x01) != 0) {
            throw new RuntimeException("字符个数应该为偶数");
        }
        byte[] out = new byte[len >> 1];
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f |= toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return new String(out, StandardCharsets.UTF_8);
    }

    private static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    //2. MD5: Message-Digest Algorithm 5（信息-摘要算法), 把一个任意长度的字节串变换成一定长的大整数
    /**
     * 1.只有在明文相同的情况下，才能等到相同的密文；
     * 2.并且这个算法是不可逆的，即便得到了加密以后的密文，也不可能通过解密算法反算出明文;
     * @param str: Stay With Me
     * @return : d22d0122e75845be0ef6f299489dbe27
     */
    public static String getMD5(String str) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance(ALGORITHM_MD5).digest(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException",e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            int value = b & 0xFF;
            if ( value < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(value));
        }
        return hex.toString();
    }

    //3. AES: 级加密标准（Advanced Encryption Standard）
    /**
     * 1. 迭代的、对称密钥分组的密码，它可以使用128、192 和 256 位密钥；
     * 2. 用 128 位（16字节）分组加密和解密数据；
     *
     * @param str 明文 ：Stay With Me
     * @param key 密钥 ：29FECF0DCBF1505AF35C36DEE63D207E
     * @return
     */
    public static String aesEncryptStr(String str, String key) {
        try {
            SecretKeySpec secretKeySpec = generateSecretKey(key);
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] buffer = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : buffer) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
            }
            return sb.toString();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    /**
     * AES 解密
     * @param str 待解密内容
     * @param key 解密的密钥
     * @return
     */
    public static String aesDecryptStr(String str, String key) {
        if (str.isEmpty() || str.length() < 1)
            return null;

        byte[] buffer = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            int high = Integer.parseInt(str.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(str.substring(i * 2 + 1, i * 2 + 2),16);
            buffer[i] = (byte) (high * 16 + low);
        }

        try {
            SecretKeySpec secretKeySpec = generateSecretKey(key);
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);//Decrypt_mode指解密操作

            byte[] result = cipher.doFinal(buffer);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件AES加密、解密
     * @param src 目标文件
     * @param out 加密、解密后文件
     * @param key 密钥
     * @param isEncrypt 加密：true； 解密：false；
     */
    public static void aesEncryptFile(File src, File out, String key, boolean isEncrypt){
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            SecretKeySpec secretKeySpec = generateSecretKey(key);
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
            cipher.init(isEncrypt? Cipher.ENCRYPT_MODE: Cipher.DECRYPT_MODE, secretKeySpec);

            inputStream = new FileInputStream(src);
            outputStream = new FileOutputStream(out);

            byte[] buffer = new byte[1024];
            int len;
            // 循环读取数据 加密/解密
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(cipher.update(buffer, 0, len));
            }
            outputStream.write(cipher.doFinal());
        } catch (NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | IOException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //生成安全密钥
    private static SecretKeySpec generateSecretKey(String key) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes(StandardCharsets.UTF_8));
        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM_AES);
        kgen.init(KEY_SIZE, secureRandom);

        SecretKey secretKey = kgen.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM_AES);
    }
}
