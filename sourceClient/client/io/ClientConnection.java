package client.io;

import client.Encrypter;

public class ClientConnection {
	private ClientConnection() {
		
	}
	
	public static boolean connect(String usr, String pwd) {
		System.out.println(Encrypter.RSA.generateKeyPair());
		System.out.println(Encrypter.RSA.getPublicKey());
		Encrypter.RSA.setOutsideKey(Encrypter.RSA.getPublicKey());
		System.out.println(Encrypter.RSA.encrypt("Hallo das ist ein Test!"));
		System.out.println(Encrypter.RSA.decrypt(Encrypter.RSA.encrypt("Hallo das ist ein Test!")));
		
		System.out.println(Encrypter.AES.generateKey());
		System.out.println(Encrypter.AES.decrypt(Encrypter.AES.encrypt("Nein das ist bös")));
		System.out.println(Encrypter.AES.encrypt("Nein das ist bös"));
		return true;
	}
}
