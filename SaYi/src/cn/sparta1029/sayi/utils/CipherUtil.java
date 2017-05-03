package cn.sparta1029.sayi.utils;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {
	private static String myKey="sparta1029";
	public static String encrypt(String src) throws Exception {   
        byte[] rawKey = getRawKey(myKey.getBytes());   
        byte[] result = encrypt(rawKey, src.getBytes());   
        return toHex(result);   
    }   
       
    public static String decrypt(String encrypted) throws Exception {   
        byte[] rawKey = getRawKey(myKey.getBytes());   
        byte[] enc = toByte(encrypted);   
        byte[] result = decrypt(rawKey, enc);   
        return new String(result);   
    }   
  
    private static byte[] getRawKey(byte[] seed) throws Exception {   
        KeyGenerator kgen = KeyGenerator.getInstance("AES"); 
        // SHA1PRNG ǿ��������㷨, Ҫ����4.2���ϰ汾�ĵ��÷���
         SecureRandom sr = null;
       if (android.os.Build.VERSION.SDK_INT >=  17) {
         sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
       } else {
         sr = SecureRandom.getInstance("SHA1PRNG");
       } 
        sr.setSeed(seed);   
        kgen.init(256, sr); //256 bits or 128 bits,192bits
        SecretKey skey = kgen.generateKey();   
        byte[] raw = skey.getEncoded();   
        return raw;   
    }   
  
       
    private static byte[] encrypt(byte[] key, byte[] src) throws Exception {   
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");   
        Cipher cipher = Cipher.getInstance("AES");   
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);   
        byte[] encrypted = cipher.doFinal(src);   
        return encrypted;   
    }   
  
    private static byte[] decrypt(byte[] key, byte[] encrypted) throws Exception {   
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");   
        Cipher cipher = Cipher.getInstance("AES");   
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);   
        byte[] decrypted = cipher.doFinal(encrypted);   
        return decrypted;   
    }   
  
    public static String toHex(String txt) {   
        return toHex(txt.getBytes());   
    }   
    public static String fromHex(String hex) {   
        return new String(toByte(hex));   
    }   
       
    public static byte[] toByte(String hexString) {   
        int len = hexString.length()/2;   
        byte[] result = new byte[len];   
        for (int i = 0; i < len; i++)   
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();   
        return result;   
    }   
  
    public static String toHex(byte[] buf) {   
        if (buf == null)   
            return "";   
        StringBuffer result = new StringBuffer(2*buf.length);   
        for (int i = 0; i < buf.length; i++) {   
            appendHex(result, buf[i]);   
        }   
        return result.toString();   
    }   
    private final static String HEX = "0123456789ABCDEF";   
    private static void appendHex(StringBuffer sb, byte b) {   
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));   
    }   
}
