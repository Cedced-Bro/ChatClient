package client.gui;

import java.awt.Color;
import java.awt.event.ActionListener;

import client.graphics.Button;
import client.graphics.Pane;
import client.graphics.Panel;
import client.graphics.TextInput;
import client.graphics.TextView;

public class ContactPane extends Pane{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color colorContact = new Color(245, 245, 245);
	private Color colorBackground = new Color(200, 200, 200);
	private Color colorMain = new Color(245, 100, 100);
	private Color colorRecievedMessage = new Color(100, 245, 100);
	
	private ActionListener listener;
	
	private Panel contactPanel;
	public static final String ID_PNL_CONTACT = "pnlContact";
	private Panel contactInfoPanel;
	public static final String ID_PNL_CONTACT_INFO = "pnlContactInfo";
	private Panel writePanel;
	public static final String ID_PNL_WRITE = "pnlWrite";
	private Panel contactMessagePanel;
	public static final String ID_PNL_MESSAGE_CONTACT = "pnlMessageContact";

	private TextInput messageTextInput;
	public static final String ID_TXTIN_MESSAGE = "txtinMessage";
	
	public static final String ID_BTN_BACK = "btnBack";
	public static final String ID_BTN_SEND = "btnSend";
	
	private String userIDTarget;
	
	public ContactPane(String username, String userID, ActionListener listener){
		super();
		
		this.listener = listener;
		
		userIDTarget = userID;
		
		contactPanel = new Panel(0.2f, 0.1f, 0.6f, 0.8f, Panel.ROUNDBOX);
		contactPanel.setColorBackground(colorBackground);
		
		setupContactInfoPanel(username, userID);
		
		setupMessageContactPanel();
		
		setupWritePanel();
		
		components.addComponent(contactPanel, ID_PNL_CONTACT);
	}
	
	private void setupMessageContactPanel(){
		contactMessagePanel = new Panel(0f, 0.15f, 1f, 0.7f, Panel.DEFAULT);
		
		contactPanel.addComponent(contactMessagePanel, ID_PNL_MESSAGE_CONTACT);
	}

	private void setupWritePanel() {
		writePanel = new Panel(0f, 0.85f, 1f, 0.15f, Panel.DEFAULT);
		writePanel.setColorBackground(colorBackground);
		
		Button btnSend = new Button(0f, 0f, 0.3f, 1f, "Senden");
		btnSend.setColorBackground(colorMain);
		btnSend.addActionListener(listener);
		btnSend.setActionCommand(ID_BTN_SEND);
		writePanel.addComponent(btnSend, ID_BTN_SEND);
		
		messageTextInput = new TextInput(0.3f, 0f, 0.7f, 1f, "", TextInput.ROUNDBOX);
		messageTextInput.setColorBackground(colorContact);
		messageTextInput.addActionListener(listener);
		messageTextInput.setActionCommand(ID_TXTIN_MESSAGE);
		writePanel.addComponent(messageTextInput, ID_TXTIN_MESSAGE);
		
		contactPanel.addComponent(writePanel, ID_PNL_WRITE);
	}

	private void setupContactInfoPanel(String username, String userID) {
		contactInfoPanel = new Panel(0f, 0f, 1f, 0.15f, Panel.ROUNDBOX);
		contactInfoPanel.setColorBackground(colorContact);
		
		Button btnBack = new Button(0f, 0f, 0.3f, 1f, "Zurück");
		btnBack.setColorBackground(colorMain);
		btnBack.addActionListener(listener);
		btnBack.setActionCommand(ID_BTN_BACK);
		contactInfoPanel.addComponent(btnBack, ID_BTN_BACK);
		
		TextView txtviewUsername = new TextView(0.45f, 0f, 0.45f, 0.5f, username, TextView.DEFAULT);
		txtviewUsername.setAlignment(TextView.ALIGNMENT_LEFT);
		
		TextView txtviewUserID = new TextView(0.45f, 0.5f, 0.45f, 0.5f, userID, TextView.DEFAULT);
		txtviewUserID.setAlignment(TextView.ALIGNMENT_LEFT);
		
		contactInfoPanel.addComponent(txtviewUsername, "txtviewName"+userID);
		contactInfoPanel.addComponent(txtviewUserID, "txtviewID"+userID);
		
		contactPanel.addComponent(contactInfoPanel, ID_PNL_CONTACT_INFO);
	}
	
	public TextInput getMessageTextInput(){return messageTextInput;}
	
	public void addSentMessage(String message){
		TextView messageView = new TextView(0.15f, 0.01f+contactMessagePanel.getNumberElements()*0.11f, 0.84f, 0.1f, message, TextView.ROUNDBOX);
		messageView.setColorBackground(colorMain);
		contactMessagePanel.addComponent(messageView, message);
	}
	
	public void addRecievedMessage(String message){
		TextView messageView = new TextView(0.01f, 0.01f+contactMessagePanel.getNumberElements()*0.11f, 0.84f, 0.1f, message, TextView.ROUNDBOX);
		messageView.setColorBackground(colorRecievedMessage);
		contactMessagePanel.addComponent(messageView, message);
	}
	
	public String getTargetID() {
		return userIDTarget;
	}
	
}




