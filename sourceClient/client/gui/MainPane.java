package client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.GregorianCalendar;

import client.Main;
import client.User;
import client.graphics.Button;
import client.graphics.ClickablePanel;
import client.graphics.Pane;
import client.graphics.Panel;
import client.graphics.TextInput;
import client.graphics.TextView;

public class MainPane extends Pane{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color colorContact = new Color(245, 245, 245);
	private Color colorBackgroundContact = new Color(200, 200, 200);
	private Color colorMain = new Color(245, 100, 100);
	
	private ActionListener listener;
	
	private static String date;
	private static String time;
	private Panel centerPanel;
	public static final String CENTER_PANEL = "centerPanel";
	private Panel northPanel;
	public static final String NORTH_PANEL = "northPanel";
	private Panel eastPanel;
	public static final String EAST_PANEL = "eastPanel";
	public static final String ID_TXTVIEW_KONTAKTE = "txtviewKontakte";
	public static final String ID_BTN_OPTIONS = "btnOptions";
	public static final String ID_TXTIN_SEARCH = "txtinSearch";
	private TextView txtviewUsername;
	public static final String ID_TXTVIEW_USERNAME = "txtviewUsername";
	private TextView txtviewUserID;
	public static final String ID_TXTVIEW_USER_ID = "txtviewUserID";
	public static TextView txtviewDate;
	public static final String ID_TXTVIEW_DATE = "txtviewDate";
	private TextView txtviewTime;
	public static final String ID_TXTVIEW_TIME = "txtviewTime";
	private Color colorBackground;
	private User usr;
	
	public MainPane(ActionListener listener){
		super();
		
		this.usr = Main.getUser();
		
		this.listener = listener;
		
		colorBackground = new Color(245, 245, 245);
		setBackground(colorBackground);
		centerPanel = new Panel(0f, 0.2f, 0.7f, 0.85f, Panel.ROUNDBOX);
		centerPanel.setColorBackground(colorBackgroundContact);
		components.addComponent(centerPanel, CENTER_PANEL);
		
		setupNorth();
		setupEast();
		
		TextView txtviewKontakte = new TextView(0f, 0.12f, 0.7f, 0.11f, "Kontakte", TextView.ROUNDBOX);
		txtviewKontakte.setColorBackground(colorMain);
		txtviewKontakte.setAlignment(TextView.ALIGNMENT_LEFT);
		txtviewKontakte.setFont(new Font(null, Font.BOLD, 12));
		components.addComponent(txtviewKontakte, ID_TXTVIEW_KONTAKTE);
		
		ThreadTime timecycle = new ThreadTime();
		timecycle.start();
	}
	
	public void setUser(User usr){
		this.usr = usr;
		txtviewUsername.setText(usr.getUsr());
		txtviewUserID.setText(usr.id);
	}
	
	public User getUser(){return usr;}
	
	public void setupNorth(){
		northPanel = new Panel(0f, -0.02f, 0.7f, 0.12f, Panel.ROUNDBOX);
		northPanel.setColorBackground(colorBackgroundContact);
		Button btnOptions = new Button(0f, 0f, 0.3f, 1f, "Optionen");
		btnOptions.setBorder(4);
		btnOptions.setColorBackground(colorMain);
		btnOptions.addActionListener(listener);
		btnOptions.setActionCommand(ID_BTN_OPTIONS);
		northPanel.addComponent(btnOptions, ID_BTN_OPTIONS);
		TextInput txtinSearch = new TextInput(0.3f, 0f, 0.7f, 1f, "Finde Kontakt...", TextInput.ROUNDBOX);
		txtinSearch.setBorder(4);
		txtinSearch.setColorBackground(colorContact);
		northPanel.addComponent(txtinSearch, ID_TXTIN_SEARCH);
		components.addComponent(northPanel, NORTH_PANEL);
	}
	
	private void setupEast(){
		eastPanel = new Panel(0.7f, -0.02f, 0.3f, 1.04f, Panel.ROUNDBOX);
		eastPanel.setColorBackground(colorBackgroundContact);
		txtviewUsername = new TextView(0f, 0.4f, 1f, 0.15f, usr.getUsr(), TextView.BASE_TOPLINE);
		eastPanel.addComponent(txtviewUsername, ID_TXTVIEW_USERNAME);
		txtviewUserID = new TextView(0f, 0.55f, 1f, 0.15f, usr.getUsr(), TextView.BASE_TOPLINE);
		eastPanel.addComponent(txtviewUserID, ID_TXTVIEW_USER_ID);
		txtviewDate = new TextView(0f, 0.7f, 1f, 0.15f, "Datum", TextView.DEFAULT);
		eastPanel.addComponent(txtviewDate, ID_TXTVIEW_DATE);
		txtviewTime = new TextView(0f, 0.85f, 1f, 0.15f, "Zeit", TextView.DEFAULT);
		eastPanel.addComponent(txtviewTime, ID_TXTVIEW_TIME);
		
		components.addComponent(eastPanel, EAST_PANEL);
	}
	
	public void addContact(Image profile, String username, String userID){
		float contactY = centerPanel.getNumberElements()*0.125f+0.01f;
		ClickablePanel contact = new ClickablePanel(0f, contactY, 1f, 0.125f, Panel.ROUNDBOX, userID);
		contact.setBorder(4);
		contact.setColorBackground(colorContact);
		contact.addActionListener(listener);
		contact.setActionCommand("AAAAA"+userID);
		
		TextView txtviewUsername = new TextView(0.15f, 0f, 0.45f, 0.5f, username, TextView.DEFAULT);
		txtviewUsername.setAlignment(TextView.ALIGNMENT_LEFT);
		
		TextView txtviewUserID = new TextView(0.15f, 0.5f, 0.45f, 0.5f, userID, TextView.DEFAULT);
		txtviewUserID.setAlignment(TextView.ALIGNMENT_LEFT);
		
		contact.addComponent(txtviewUsername, "txtviewName"+userID);
		contact.addComponent(txtviewUserID, "txtviewID"+userID);
		centerPanel.addComponent(contact, userID);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.drawLine((int)(0.7f*width), 0, (int)(0.7f*width), height);
		
	}
	
	private class ThreadTime extends Thread{
		@Override
		public void run() {
			GregorianCalendar now;
	        DateFormat df;
			while(true){
				try {
					Thread.sleep(100);
				} catch (Exception e) {}
				now = new GregorianCalendar();
		        df = DateFormat.getDateInstance(DateFormat.LONG);
		        date = df.format(now.getTime());
		        txtviewDate.setText(date);
		        df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		        time = df.format(now.getTime());
		        txtviewTime.setText(time);
		        repaint();
			}
		}
	}
	
	
	
}








