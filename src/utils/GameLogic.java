package utils;

import ai.OurBoard;
import ai.OurPair;
import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameLogic implements GamePlayer
{
	static OurBoard ourBoard;

	static GameClient gameClient = null;
	int roomId;
	static ArrayList<GameRoom> roomList;

	static Action action;
	static JAXBContext jaxbContext;

	public static void main(String[] args) throws JAXBException
	{
		jaxbContext = JAXBContext.newInstance(Action.class);
		//gamelogic gamelogic = new gamelogic("team rocket","password123");

		String msg = "<action type='room-joined'><usrlist ucount='1'><usr name='team rocket' id='1'></usr></usrlist></action>";
		String msg2 = "<action type='move'> <queen move='a3-g3'></queen><arrow move='h4'></arrow></action>";

		Message message = new Message();
		message.unmarshal(msg);
		System.out.println(message.marshal());
//
//		Message message1 = new Message();
//		message1.unmarshal(msg2);
//		OurPair<Integer, Integer> ourPair = new OurPair<Integer, Integer>(6,7);
//		OurPair<Integer, Integer> ourPair1 = new OurPair<Integer, Integer>(0,5);
//		message1.setArrow(ourPair);
//		message1.setMove(ourPair, ourPair1);
//		System.out.println(message1.getArrow());
//
//		System.out.println(message1.marshal());

	}

	public GameLogic(String name, String passwd)
	{
		//initialize board
		ourBoard = new OurBoard();

		//initialize gui

		//make connection
		gameClient = new GameClient(name, passwd, this);

		//choose room
		getOurRooms();
		Scanner scanner = new Scanner(System.in);
		System.out.print("Input roomID to join: ");
		int roomId = scanner.nextInt();
		joinRoom(roomId);
	}

	//Prints the id, name, and user count of all available game rooms in the game client
	private void getOurRooms() {
		roomList = gameClient.getRoomLists();
		System.out.println("Available Game Rooms:");
		for(GameRoom room : roomList)
		{
			System.out.println("Room ID: "+room.roomID + "\tRoom Name: "+room.roomName+ "\tUser Count: "+room.userCount);
		}

	}

	public static boolean joinRoom(int roomId)
	{
		roomList = gameClient.getRoomLists();
		try {
			for(GameRoom r : roomList)
			{
				if(r.roomID == roomId)
				{
					gameClient.joinGameRoom(r.roomName);
					System.out.println("Joined room: " + roomId);
					return true;
				}
			}
			System.out.println("Failed to join room: " + roomId);
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	//general message from the server
	public boolean handleMessage(String arg0) throws Exception {
		String msg = XMLParser.parseXML(arg0);
		//at least print out the message so the other team can see you've timed out
		System.out.print(arg0);
		return true;
	}

	//game-specific message from the server
	public boolean handleMessage(GameMessage arg0) throws Exception {
		System.out.println("[SimplePlayer: The server said =]  " + arg0.toString());
		return true;
	}

	// You may want to implement a method like this as a central point for sending messages
	// to the server.
	public void sendToServer(String msgType, int roomID){
		// before sending the message to the server, you need to (1) build the text of the message
		// as a string
		String msg = "Message goes here";
		boolean isMove = true;
		//(2) compile the message by calling
		// the static method ServerMessage.compileGameMessage(msgType, roomID, actionMsg),

		ServerMessage.compileGameMessage(msgType, roomId, msg);
		// and (3) call the method gameClient.sendToServer() to send the compiled message.


		gameClient.sendToServer(msg, isMove);
		// For message types and message format, see the GameMessage API and the project notes
	}

	public void playGame() {
		while (true) {


			// if not our turn
			//
			// calculate potential moves
			// wait for message
			// check for end game status
			// alert team
			// set to our turn

			// else
			// check opponent move legality
			//if illegal
			// send error message
			// calculate move
			// check for end game status
			// alert team
			// send move
			// set to their move

		}
	}

	/**
	 *
	 * @param msg Reads in an XML string from the game server and unmarshalls
	 *            it into the object mappings
	 * @throws JAXBException
	 */

	public static void unmarshal(String msg) throws JAXBException {
		InputStream is = new ByteArrayInputStream(msg.getBytes());
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		action = (Action) unmarshaller.unmarshal(is);
	}

	/**
	 *
	 * @return String which is the XML formatting for the response message
	 *              to the server
	 * @throws JAXBException
	 */
	public static String marshal() throws JAXBException {
		OutputStream os = new ByteArrayOutputStream();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.marshal(action, os);

		return os.toString();
	}

	/**
	 * Root of the XML string
	 */
	@XmlRootElement(name="action")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Action
	{
		@XmlAttribute(name="type")
		String type;
		@XmlElement(name="usrlist")
		private UserList userList;
		@XmlElement(name="queen")
		private Queen queen;
		@XmlElement(name="arrow")
		private Arrow arrow;
	}

	/**
	 * List of Users Element
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class UserList
	{
		@XmlAttribute(name="ucount")
		int ucount;
		@XmlElement(name="usr")
		List<User> users;
	}

	/**
	 * User element and attributs
	 * None of the examples in the
	 * message format guide include anything
	 * except attributes
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class User
	{
		@XmlAttribute(name="name")
		String name;
		@XmlAttribute(name="id")
		int id;
		@XmlAttribute(name="role")
		String role;
	}

	/**
	 * Queen, stores a move in a string of the
	 * format previous-current such as g7-a4.
	 * Is not stored in our internal game board representation
	 * See getMove for getter of the newest move
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Queen
	{
		@XmlAttribute(name="move")
		static String move;
		/**
		 *
		 * @param InitialQ Where the queen started in our turn
		 * @param FinalQ Where the queen was moved to in our turn
		 *
		 */
		public void setMove(OurPair<Integer, Integer> InitialQ, OurPair<Integer, Integer> FinalQ)
		{
			String s1 = new StringBuilder().append(Character.toChars(InitialQ.getX() + 'a')).append(Character.toChars(InitialQ.getY() + '0')).toString();
			String s2 = new StringBuilder().append(Character.toChars(FinalQ.getX() + 'a')).append(Character.toChars(FinalQ.getY() + '0')).toString();

			this.move = s1 + "-" + s2;
		}

		/**
		 *
		 * @return The location that the queen moved to
		 */
		public OurPair<Integer, Integer> getMove(){
			String[] str = Queen.move.split("-");
			return new OurPair<Integer, Integer>(str[1].charAt(0)-'a', str[1].charAt(1)-'0');
		}

	}

	/**
	 * Stores location that the arrow was fired after
	 * the queen made her move
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Arrow
	{
		@XmlAttribute(name="move")
		static String arrow;


		/**
		 *
		 * @return location that the arrow was placed
		 */
		public OurPair<Integer, Integer> getArrow()
		{
			return new OurPair<Integer, Integer>(this.arrow.charAt(0)-'a', this.arrow.charAt(1)-'0');
		}

		/**
		 *
		 * @param arrow takes OurPair object for location that we placed our arrow
		 */
		public void setArrow(OurPair<Integer, Integer> arrow)
		{
			this.arrow = new StringBuilder().append(Character.toChars(arrow.getX() + 'a')).append(Character.toChars(arrow.getY() + '0')).toString();
		}
	}
}
