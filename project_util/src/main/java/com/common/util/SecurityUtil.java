package com.common.util;

import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;

public class SecurityUtil {

    /**
     * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용)
     * 
     * @param password
     *            암호화될 패스워드
     * @param id
     *            salt로 사용될 사용자 ID 지정
     * @return
     * @throws Exception
     */
    public static String encryptPassword(String password, String id) throws Exception {
        if (password == null || id == null) {
            return "";
        }
        byte[] hashValue = null; // 해쉬값
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.reset();
        md.update(id.getBytes());
        hashValue = md.digest(password.getBytes());
        return new String(Base64.encodeBase64(hashValue));
    }
    
    /**
     * 문자열을 SHA-256 방식으로 암호화
     * 
     * @param txt
     *            암호화 하려하는 문자열
     * @return String
     * @throws Exception
     */ 
    public static String getEncryptSHA256(String txt) throws Exception {
        StringBuffer sbuf = new StringBuffer();
        MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        mDigest.update(txt.getBytes());
        byte[] msgStr = mDigest.digest();
        for (int i = 0; i < msgStr.length; i++) {
            byte tmpStrByte = msgStr[i];
            String tmpEncTxt = Integer.toString((tmpStrByte & 0xff) + 0x100, 16).substring(1);
            sbuf.append(tmpEncTxt);
        }
        return sbuf.toString();
    }

    /**
     * 문자열을 MD5 방식으로 암호화
     * 
     * @param txt
     *            암호화 하려하는 문자열
     * @return String
     * @throws Exception
     */
    public static String getEncryptMD5(String txt) throws Exception {
        StringBuffer sbuf = new StringBuffer();
        MessageDigest mDigest = MessageDigest.getInstance("MD5");
        mDigest.update(txt.getBytes());
        byte[] msgStr = mDigest.digest();
        for (int i = 0; i < msgStr.length; i++) {
            String tmpEncTxt = Integer.toHexString((int) msgStr[i] & 0x00ff);
            sbuf.append(tmpEncTxt);
        }
        return sbuf.toString();
    }
    public static String getEcisPwByMd5(String plainPw) throws Exception {
        return getEncryptMD5(plainPw).substring(0, 16);
    }
}
