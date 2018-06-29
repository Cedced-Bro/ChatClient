package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {}
			
			try {
				Socket s = new Socket("localhost", 8442);
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter out = new PrintWriter(s.getOutputStream());
				out.println("Test");
			} catch (Exception e) {
				
			}
		}
	}
}
