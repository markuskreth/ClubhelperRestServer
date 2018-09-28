package de.kreth.clubhelperbackend.config;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Encryptor {

	Logger log = LoggerFactory.getLogger(getClass());
			
	private static final String encType = "AES";
	private static final String key = "ClubhelperSecuri";
	private static final Charset charset = Charset.forName("UTF-8");
	DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL,
			SimpleDateFormat.MEDIUM, Locale.US);

	private Key aesKey;
	private Cipher cipher;

	public Encryptor() {
		// Create key and cipher
		try {
			byte[] keyBytes = key.getBytes(charset);
			aesKey = new SecretKeySpec(keyBytes, encType);
			cipher = Cipher.getInstance(encType);
		} catch (NoSuchAlgorithmException|NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} 
	}

	public String encrypt(Date theDate, String userAgent)
			throws IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException {
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] bytes = (df.format(theDate) + userAgent).getBytes(charset);
		byte[] encrypted = cipher.doFinal(bytes);
		char[] encodeHex = Hex.encodeHex(encrypted);
		return new String(encodeHex);
	}

}
