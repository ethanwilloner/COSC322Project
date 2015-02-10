package utils;

import MessageParsing.Action;
import MessageParsing.Arrow;
import MessageParsing.Queen;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
		action = new Action();
		//gamelogic gamelogic = new gamelogic("team rocket","password123");

		String msg = "<action type='room-joined'><usrlist ucount='1'><usr name='team rocket' id='1'></usr></usrlist></action>";
		String msg2 = "<action type='move'> <queen move='a3-g3'></queen><arrow move='h4'></arrow></action>";

		OurPair<Integer, Integer> InitialQ = new OurPair<Integer, Integer>(0,0);
		OurPair<Integer, Integer> FinalQ = new OurPair<Integer, Integer>(5,5);
		OurPair<Integer, Integer> Arrow = new OurPair<Integer, Integer>(5,2);

		action.setType("move");
		action.setQueen(new Queen());
		action.setArrow(new Arrow());
		action.queen.setMove(InitialQ, FinalQ);
		action.arrow.setArrow(Arrow);

		System.out.println(marshal());

		action = new Action();
		unmarshal(msg);
		System.out.println(marshal());

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

	public boolean handleMessage(String arg0) throws Exception {
		String msg = XMLParser.parseXML(arg0);
		System.out.print(arg0);
		return true;
	}

	public boolean handleMessage(GameMessage arg0) throws Exception {
		System.out.println("[SimplePlayer: The server said =]  " + arg0.toString());
		return true;
	}

	public void sendToServer(String msgType, int roomID){
		String msg = "Message goes here";
		boolean isMove = true;
		ServerMessage.compileGameMessage(msgType, roomId, msg);
		gameClient.sendToServer(msg, isMove);
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
	 * @return String which is the XML formatting for the response message
	 * to the server
	 * @throws JAXBException
	 */
	public static String marshal() throws JAXBException {
		OutputStream os = new ByteArrayOutputStream();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.marshal(action, os);

		return os.toString();
	}
}
