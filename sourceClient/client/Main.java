package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import client.graphics.TextInput;
import client.gui.Gui;
import client.io.ClientConnection;
import client.io.IniAdapter;
/*
 * Main.java kombiniert/verbindet die Verschlüsselung, den Netzwerkverkehr und das GUI
 */
/**
 * This is the main-class of the Chat-Client we had to program at school. Feel free to use the codes in your
 * own programs
 * 
 * @author Cedric, Jesko
 * @version 1.0
 *
 */
public class Main {

	// *************
	// * Constants *
	// *************
	public static final String ACTIONEVENT_SEND;
	public static final IniAdapter defaultIni;
	public static final String defaultIniPath;
	
	// **********************
	// * Private Attributes *
	// **********************
	private static Gui gui;
	private static ActionListener listener;
	private static User usr;
	
	// *********************
	// * Public Attributes *
	// *********************
	// This is just a testPWD and a testUSR
	public final static String[] usrT = {"root", "admin", "Willi", "Hans"};
	public final static String[] pwdT = {"toor", "nimda", "illiW", "snaH"};
	public final static String[] IDT = {"0000", "0001", "0002", "0003"};
	
	static {
		defaultIni = new IniAdapter();
		defaultIniPath = "/res/client.ini";
		ACTIONEVENT_SEND = "actevSend";
	}
	
	
	// *******************
	// * Private Methods *
	// *******************
	private static void showSentMessage(String message){
		gui.getPaneContact().addSentMessage(message);
		gui.getPaneContact().resize();
		gui.getPaneContact().repaint();
	}
	
	/**
	 * This Method starts the Connection to the Server
	 */
	private static void connect() {
		ClientConnection.connect(usr.getUsr(), usr.getPwd());
	}
	
	
	// ******************
	// * Public Methods *
	// ******************
	public static void setupGui() {
		listener = new ActionlistenerMain();
		gui = new Gui(600, 400, "GHChat", usr, listener);
		gui.setVisible(true);
	}
	
	public static class ActionlistenerMain implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if(command.equals(ACTIONEVENT_SEND)){
				TextInput txtinMessage = gui.getPaneContact().getMessageTextInput();
				String message = txtinMessage.getText();
				txtinMessage.setText("");
				showSentMessage(message);
				sendMessage(message);
			}
		}
	}
	
	/*
	 * Wird aufgerufen, wenn Button "Senden" gedrückt oder wenn Enter in der Nachrichtenzeile gedrückt
	 * message ist der Inhalt der Eingabezeile für die Nachrichten
	 */
	public static void sendMessage(String msg) {
		ClientConnection.addToPrintList(msg);
	}
	
	/*
	 * Zeigt eine eingegangene Nachricht (message) bei dem aktuell geöffneten Kontakt an, wenn einer geöffnet ist
	 */
	public static void showReceivedMessage(String message){
		if(contactSelected()){
			gui.getPaneContact().addRecievedMessage(message);
			gui.getPaneContact().resize();
			gui.getPaneContact().repaint();
		}
	}
	
	/*
	 * Zeigt eine Kontaktauswahlmöglichkeit mit den angegebenen Daten auf dem MainPane an
	 */
	public static void showContact(User usr){
		gui.getPaneMain().addContact(null, usr.getUsr(), usr.id);
		gui.getPaneMain().resize();
		gui.getPaneMain().repaint();
	}
	
	/*
	 * Falls die Kontaktseite geöffnet ist, gibt die Methode true zurück
	 */
	public static boolean contactSelected(){
		if(gui.getPaneContact() == null) return false;
		else return true;
	}
	
	/*
	 * Gibt die UserID des geöffneten Kontakts, wenn kein Kontakt geöffnet null
	 */
	public static String getContactUserID(){
		if(contactSelected()) return gui.getPaneContact().getTargetID();
		else return null;
	}
	
	/*
	 * Gibt die UserID des Benutzers selbst
	 */
	public static User getOwnUser(){
		return gui.getPaneMain().getUser();
	}
	
	/*
	 * Setzt den Username und die UserID des Benutzers selbst
	 */
	public static void setUser(User usr){
		gui.getPaneMain().setUser(usr);
	}
	
	// ***************
	// * Main Method *
	// ***************
	public static void main(String[] args) {
		int user = 1;
		usr = new User(IDT[user], usrT[user], pwdT[user]);
		setupGui();
		for (int i = 0; i < IDT.length; i++) {
			if (i == user) continue;
			else showContact(new User(IDT[i], usrT[i], pwdT[i]));
		}
		connect();
	}
	
	public static User getUser() {
		return usr;
	}
	
	public static Gui getGui() {
		return gui;
	}

	public static String getUsr(String id) {
		if (id.equals("0000")) return usrT[0];
		else if (id.equals("0001")) return usrT[1];
		else if (id.equals("0002")) return usrT[2];
		else if (id.equals("0003")) return usrT[3];
		return null;
	}
}
