package client;

/**
 * An own implementation of the java.util.Queue. This was needed for our Software-Project and is now implemented
 * and fully operational.
 * 
 * @author Simon, Cedric (modified original)
 * @version 1.0
 *
 */
public class FiFo_List {
	
	/**
	 * A backbone for all List-Elements.
	 * 
	 * @author Simon, Cedric (modified original)
	 */
	private abstract class ListPart {
		private DataElement data;
		
		/**
		 * Constructor which initializes an element with its inherent Data.
		 * 
		 * @param data saved in an element
		 */
		protected ListPart(DataElement data) {
			this.data = data;
		}
		
		/**
		 * Returns the Data of the element.
		 * 
		 * @return the Data of the element
		 */
		public DataElement getData() {
			return data;
		}

		/**
		 * This adds a new element to the List.
		 * 
		 * @param data which should be saved in the new element
		 */
		protected abstract void addElement(DataElement data);
		/**
		 * Returns the Data of the List at position "index".
		 * 
		 * @param index position of the Data in the List
		 * @return the Data at the specified position
		 */
		protected abstract DataElement get(int index);
	}
	
	/**
	 * A class representing a normal element of a List.
	 * 
	 * @author Simon, Cedric (modified original)
	 */
	private class ListElement extends ListPart {
		
		private ListPart nextPart;
		
		private ListElement(DataElement data) {
			super(data);
			nextPart = null;
		}
		
		/**
		 * Returns the next List-Element in the List binded to this Object.
		 * 
		 * @return the next List-Element binded to it
		 */
		private ListPart getNextPart() {
			return nextPart;
		}
		
		/**
		 * Sets a new List-End with the data "data" as the next List-Element.
		 * 
		 * @param data the Data-Element of the new List-End
		 */
		private void setEnd(DataElement data) {
			nextPart = new ListEnd(data);
		}
		
		@Override
		protected void addElement(DataElement data) {
			if (nextPart instanceof ListEnd) {
				DataElement dataBuffer = nextPart.getData();
				nextPart = new ListElement(dataBuffer);
				((ListElement) nextPart).setEnd(data);
			} else
				nextPart.addElement(data);
		}

		@Override
		protected DataElement get(int index) {
			if (index == 0) return getData();
			else return nextPart.get(index-1);
		}
		
	}
	
	/**
	 * A class to indicate the end of a List.
	 * 
	 * @author Simon, Cedric (modified original)
	 */
	private class ListEnd extends ListPart {
		public ListEnd(DataElement data) {
			super(data);
		}

		@Override
		protected void addElement(DataElement data) {}

		@Override
		protected DataElement get(int index) {
			return getData();
		}
	}
	
	/**
	 * An interface for other wrappers to save elements in the List.
	 * 
	 * @author Simon, Cedric (modified original)
	 */
	private interface DataElement {}
	
	/**
	 * A class wrapper for saving Strings in the List.
	 * 
	 * @author Simon, Cedric (modified original)
	 */
	public class MessageData implements DataElement {
		public final String message;					// This is a final attribute so it can also be public
		public MessageData(String message) {
			this.message = message;
		}
	}
	
	private int size;
	private ListPart first;
	
	/**
	 * Default Constructor which initializes the List with size=-1.
	 */
	public FiFo_List() {
		this.size = -1;
	}
	
	/**
	 * This will add a Message-Element to the List.
	 * 
	 * @param message a message you want to save in the List
	 */
	public void addMessageElement(MessageData message) {
		if (first == null)
			first = new ListEnd(message);
		else if (first instanceof ListEnd) {
			DataElement dataBuffer = first.getData();
			first = new ListElement(dataBuffer);
			((ListElement) first).setEnd(message);
		} else
			first.addElement(message);
		size++;
	}
	
	/**
	 * This method goes to position "index" in the List and returns its Data.
	 * If "index" is too big, the last Data-Object will be returned.
	 * If the List is empty null will be returned.
	 * 
	 * @param index specifies which Object needs to be returned
	 * @return the Data of an Object saved in the List at index "index"
	 */
	public DataElement get(int index) {
		if (first != null && size > -1) return first.get(index);
		else return null;
	}
	
	/**
	 * This method removes the first Object from the List and returns it.
	 * 
	 * @return first Object of the List
	 */
	public DataElement removeFirstElement() {
		DataElement dataBuffer = null;
		if (first != null) {
			 dataBuffer = first.getData();
			 if (first instanceof ListElement) first = ((ListElement)first).getNextPart();
			 else first = null;
		}
		size--;
		return dataBuffer;
	}
	
	/**
	 * This method returns the size of a List-Object.
	 * 
	 * @return the size of the List
	 */
	public int getSize() {
		return size+1;
	}
}
