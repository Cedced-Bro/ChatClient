package server;

/**
 * This class helps to make loading and changing of Data in the Chat-Client easier especially later when it gets
 * connected to a SQLite-Database.
 * The class just saves some important information connected to a specific User
 * 
 * @author Cedric
 * @version 1.0
 */
public class User {

	public final String id;							// This is final so needn't be private

	private String usr;
	private String pwd;
	private String email;							// Will not be implemented now but for later use it is already written into the User-Class
	
	/**
	 * The constructor sets the final id of a User
	 * 
	 * @param id the final id connected to one specific User
	 */
	public User(String id) {
		this.id = id;
	}
	
	/**
	 * The constructor sets the final id of a User and the default_username
	 * 
	 * @param id the final id connected to one specific User
	 * @param usr is the Username of a User
	 */
	public User(String id, String usr) {
		this(id);
		this.usr = usr;
	}
	
	/**
	 * The constructor sets the final id of a User, the default_username and the default_password
	 * 
	 * @param id the final id connected to one specific User
	 * @param usr is the Username of a User
	 * @param pwd is the Password of a User
	 */
	public User(String id, String usr, String pwd) {
		this(id, usr);
		this.pwd = pwd;
	}
	
	/**
	 * Returns the Username of a User
	 * 
	 * @return the Username connected to the User
	 */
	public String getUsr() {
		return usr;
	}
	
	/**
	 * Returns the Password of a User
	 * 
	 * @return the Password connected to the User
	 */
	public String getPwd() {
		return pwd;
	}
	
	/**
	 * Does nothing important now
	 * 
	 * @return the Emailaddress connected to a User
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Sets a new Username for a User
	 * 
	 * @param usr the new Username
	 */
	public void setUsr(String usr) {
		this.usr = usr;
	}
	
	/**
	 * Sets a new Password for a User
	 * 
	 * @param pwd the new Password
	 */
	public void changePwd(String pwd) {
		this.pwd = pwd;
	}
	
	/**
	 * Does nothing important at the moment
	 * 
	 * @param email Sets new Email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
