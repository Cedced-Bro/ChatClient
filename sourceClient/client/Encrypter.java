package client;

public class Encrypter {
	
	static class RSA {
		private static String privateKey = null;
		private static String publicKey = null;
		private static String publicOutsideKey = null;

		public static String generateKeyPair() {
			String publicKey = null;
			return publicKey;
		}
		public static void setOutsideKey(String publicOutsideKey) {
			RSA.publicOutsideKey = publicOutsideKey;
		}
		public static String getPublicKey() {
			return publicKey;
		}
		public static String encrypt(String msg) {
			return msg;
		}
		public static String decrypt(String msg) {
			return msg;
		}
	}
	
	static class DEH {
		private static String privateKey = null;
		private static String publicKey = null;
		private static String publicOutsideKey = null;

		public static String generateKeyPair() {
			return publicKey;
		}
		public static void setOutsideKey(String publicOutsideKey) {
			DEH.publicOutsideKey = publicOutsideKey;
		}
		public static String getPublicKey() {
			return publicKey;
		}
		public static String encrypt(String msg) {
			return msg;
		}
		public static String decrypt(String msg) {
			return msg;
		}
	}

}
