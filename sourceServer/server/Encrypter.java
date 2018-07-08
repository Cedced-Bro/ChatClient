package server;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import server.io.ConfigAdapter;
import sun.misc.BASE64Encoder;

/**
 * This class provides methods for some basic encryption algorithms.
 * ECDH is not implemented, yet, so please make sure not to use it.
 * 
 * @author Cedric
 * @version 1.0
 */
public final class Encrypter {
	
	// *****************
	// * Key-Exchanges *
	// *****************
	public class RSA {
		private int keySize = -1;
		private KeyPair keyPair = null;
		private Key publicOutsideKey = null;
		private Cipher cipher = null;
		
		/**
		 * This Method created 2 separate RSA-Keys for encryption and decryption and returns the public key.
		 *
		 * @return the public Key of 2 new generated Keys
		 */
		public String generateKeyPair() {
			String publicKey = null;
			try {
				// Initializing KeyGenerator
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
				if (keySize == -1) Integer.parseInt(ConfigAdapter.getConfigString("RSAKeySize"));
				keyGen.initialize(keySize, new SecureRandom());
				keyPair = keyGen.genKeyPair();
				
				// Get Public Key
				return getPublicKey();
			} catch (NoSuchAlgorithmException exception) {
				// TODO!
			}
			return publicKey;
		}
		
		public void setKeySize() {
			keySize = Integer.parseInt(ConfigAdapter.getConfigString("RSAKeySize"));
		}
		
		public int getKeySize() {
			return keySize == -1 ? keySize = Integer.parseInt(ConfigAdapter.getConfigString("RSAKeySize")) : keySize;
		}
		
		/**
		 * This method sets the public Key from outside.
		 * 
		 * @param publicOutsideKey Key from outside
		 */
		public void setOutsideKey(String publicOutsideKey) {
			this.publicOutsideKey = Encrypter.getPublicKey(publicOutsideKey, "RSA");
		}
		
		/**
		 * This method returns the public key or null if noone has been set yet.
		 * 
		 * @return the public key
		 */
		public String getPublicKey() {			
			return Encrypter.publicKeyToString(keyPair.getPublic());
		}
		
		/**
		 * This method encrypts a String with the privateKey (and returns it) which then can be decrypted with the public key.
		 * 
		 * @param msg the message which needs to be encrypted
		 * @return the encrypted message
		 */
		public String encrypt(String msg) {
			try {
				// public key needs to be set first
				if (publicOutsideKey == null) return msg;
				// cipher has to be initialized
				if (cipher == null) cipher = Cipher.getInstance("RSA");
				
				cipher.init(Cipher.ENCRYPT_MODE, publicOutsideKey);
				
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
		public String decrypt(String msg) {			
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
		public void clear() {
			keyPair = null;
			publicOutsideKey = null;
		}
	}
	
	/**
	 * This class provides support for the Diffi Hellman key exchange.
	 * As this class is not completed at this state this is not working --> Deprecated
	 * 
	 * If you still want to do a key exchange, use the RSA method to do so
	 * 
	 * @author Cedric
	 * @version 0.1
	 */
	@Deprecated
	@SuppressWarnings("unused")
	public static class ECDH {
		@Deprecated
		private static PrivateKey privateKey = null;
		@Deprecated
		private static PublicKey publicKey = null;
		@Deprecated
		public static KeyPair generateKey() {
			try {
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "SunEC");
				String ellipticCurveSize = ConfigAdapter.getConfigString("encryption.ECDH.keySize");
				try {
					keyGen.initialize(Integer.parseInt(ellipticCurveSize), new SecureRandom());
				} catch (Exception e) {
					ECGenParameterSpec ecsp = new ECGenParameterSpec(ellipticCurveSize);
					keyGen.initialize(ecsp);
				}
				KeyPair keyPair = keyGen.generateKeyPair();
				privateKey = keyPair.getPrivate();
				publicKey = keyPair.getPublic();
				System.out.println(publicKey);
			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Deprecated
		public static void setupKeyAggreement(KeyPair outsideKey) {
			try {
				KeyAgreement ecdhKeyAgreement = KeyAgreement.getInstance("ECDH");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		@Deprecated
		public static String getPublicKey() {
			return Encrypter.publicKeyToString(publicKey);
		}
		@Deprecated
		public static String encrypt(String msg) {
			return msg;
		}
		@Deprecated
		public static String decrypt(String msg) {
			return msg;
		}
	}

	// ************************
	// * Symmetric Encryption *
	// ************************
	/**
	 * 
	 * @author Cedric
	 * @version 1.0
	 */
	public class AES {
		private SecretKey key = null;
		private Cipher cipher = null;
		
		public void setKey(String key) {
			this.key = Encrypter.getSecretKey(key, "AES");
		}
		
		public String getKey() {
			return Encrypter.secretKeyToString(key);
		}
		
		public String generateKey() {
			KeyGenerator keyGen;
			try {
				keyGen = KeyGenerator.getInstance("AES");
				keyGen.init(Integer.parseInt(ConfigAdapter.getConfigString("encryption.AES.keySize")), new SecureRandom());
				key = keyGen.generateKey();
				return Encrypter.secretKeyToString(key);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				key = null;
			}
			return null;
		}
		
		public String encrypt(String msg) {
			try {
				if (key == null) return msg;
				if (cipher == null) cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.ENCRYPT_MODE, key);
				return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));				
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return msg;
		}
		
		public String decrypt(String msg) {
			byte[] bytes = Base64.getDecoder().decode(msg);
			try {
				if (cipher == null) cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, key);
				return new String(cipher.doFinal(bytes), "UTF-8");
			} catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return msg;
		}
	}
	
	
	// *******************
	// * Private Methods *
	// *******************
	private static PublicKey getPublicKey(String key, String alg) {
		try {
			byte[] data = Base64.getDecoder().decode(key);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
			KeyFactory factory = KeyFactory.getInstance(alg);
			return factory.generatePublic(keySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static SecretKey getSecretKey(String key, String alg) {
		try {
			return new SecretKeySpec(Base64.getDecoder().decode(key.getBytes()), alg);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String secretKeyToString(SecretKey key) {
		return new BASE64Encoder().encode(key.getEncoded());
	}
	
	private static String publicKeyToString(PublicKey key) {
		return new BASE64Encoder().encode(key.getEncoded());
	}
	
}
