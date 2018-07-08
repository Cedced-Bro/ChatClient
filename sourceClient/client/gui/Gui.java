package client.gui;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import client.Main;
import client.User;
import client.io.ClientConnection;

public class Gui extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ActionListener mainListener;
	private Actionlistener listener;
	private Container content;
	private ContactPane paneContact;
	private MainPane paneMain;
	
	public Gui(int width, int height, String title, User usr, ActionListener mainListener) {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ClientConnection.closeConnection();
				dispose();
				System.exit(0);
			}
		});
		setSize(width, height);
		setTitle(title);
		
		this.mainListener = mainListener;
		listener = new Actionlistener();
		
		paneMain = new MainPane(listener);
		paneMain.setUser(usr);
		
		content = this.getContentPane();
		content.add(paneMain);
		paneMain.repaint();
	}
	
	public ContactPane getPaneContact(){return paneContact;}
	public MainPane getPaneMain(){return paneMain;}
	
	public void paint(Graphics g) {
		super.paint(g);
		try {
			content.getComponent(0).repaint();
		} catch (Exception e) {}
	}
	
	private class Actionlistener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if(command.equals(MainPane.ID_BTN_OPTIONS)){
				
			}else if(command.equals(ContactPane.ID_BTN_SEND) || command.equals(ContactPane.ID_TXTIN_MESSAGE)){
				fireActionEvent(Main.ACTIONEVENT_SEND);
			}else if(command.equals(ContactPane.ID_BTN_BACK)){
				Gui.this.remove(paneContact);
				Gui.this.paneContact = null;
				Gui.this.content.add(paneMain);
				Gui.this.setVisible(false);
				Gui.this.setVisible(true);
				Gui.this.repaint();
			}else{
				paneContact = new ContactPane("Standard", command.substring(5), listener);
				Gui.this.remove(paneMain);
				Gui.this.content.add(paneContact);
				Gui.this.setVisible(false);
				Gui.this.setVisible(true);
				Gui.this.repaint();
			}
		}
		
	}
	
	private void fireActionEvent(String command) {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
		mainListener.actionPerformed(event);
	}
}





