package client;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public final class Encrypter {
	
	public static class RSA {
		private static KeyPair keyPair = null;
		private static String publicOutsideKey = null;
		private static Cipher cipher = null;

		/**
		 * This Method created 2 seperate RSA-Keys for encryption and decryption and returns the public key.
		 *
		 * @return the public Key of 2 new generated Keys
		 */
		public static String generateKeyPair() {
			String publicKey = null;
			try {
				// Initializing KeyGenerator
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//				keyGen.initialize(Integer.parseInt(Client.defaultIni.getString(Client.defaultIniPath, "encryption.keySize")));
				keyGen.initialize(2048, new SecureRandom());
				keyPair = keyGen.genKeyPair();
				
				// Get Public Key
				byte[] publicKeyB = keyPair.getPublic().getEncoded();
				StringBuffer publicBuffer = new StringBuffer();
				for (int i = 0; i < publicKeyB.length; i++) publicBuffer.append(Integer.toHexString(0x0100 + (publicKeyB[i] & 0x00FF)).substring(1));
				publicKey = publicBuffer.toString();
			} catch (NoSuchAlgorithmException exception) {
				// TODO!
			}
			return publicKey;
		}
		/**
		 * This method sets the public Key from outside.
		 * 
		 * @param publicOutsideKey Key from outside
		 */
		public static void setOutsideKey(String publicOutsideKey) {
			RSA.publicOutsideKey = publicOutsideKey;
		}
		
		/**
		 * This method returns the public key or null if noone has been set yet.
		 * 
		 * @return the public key
		 */
		public static String getPublicKey() {			
			byte[] publicKeyB = keyPair.getPublic().getEncoded();
			StringBuffer publicBuffer = new StringBuffer();
			for (int i = 0; i < publicKeyB.length; i++) publicBuffer.append(Integer.toHexString(0x0100 + (publicKeyB[i] & 0x00FF)).substring(1));
			return publicBuffer.toString();
		}
		
		/**
		 * This method encrypts a String with the privateKey (and returns it) which then can be decrypted with the public key.
		 * 
		 * @param msg the message which needs to be encrpyted
		 * @return the encrypted message
		 */
		public static String encrypt(String msg) {
			try {
				if (cipher == null) cipher = Cipher.getInstance("RSA");
				
				cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
				
				return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
				e.printStackTrace();
				// TODO!
			}
			return null;
		}
		
		/**
		 * This method decrypts an encrypted String with the publicKey and returns it.
		 * 
		 * @param msg the encrypted String
		 * @return the decrypted message
		 */
		public static String decrypt(String msg) {			
			try {
				byte[] bytes = Base64.getDecoder().decode(msg);
				cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
				
				return new String(cipher.doFinal(bytes), "UTF-8");
			} catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
				// TODO!
				e.printStackTrace();
			}
			
			return null;
		}
		
		/**
		 * This method clears all keys and the memory.
		 * WARNING: This deletes all saved keys!
		 */
		public static void clear() {
			keyPair = null;
			publicOutsideKey = null;
		}
	}
	
	public static class DEH {
		private static String privateKey = null;
		private static String outsideKey = null;

		public static String generateKeyPair() {
			return privateKey;
		}
		public static void setOutsideKey(String outsideKey) {
			DEH.outsideKey = outsideKey;
		}
		public static String getKey() {
			return privateKey;
		}
		public static String encrypt(String msg) {
			return msg;
		}
		public static String decrypt(String msg) {
			return msg;
		}
	}

}
