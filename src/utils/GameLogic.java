package utils;

import ai.OurBoard;
import ai.OurPair;
import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Scanner;

public class GameLogic implements GamePlayer
{
	static OurBoard ourBoard;

	static GameClient gameClient = null;
	int roomId;
	static ArrayList<GameRoom> roomList;

	public static void main(String[] args) throws JAXBException
	{
		//gamelogic gamelogic = new gamelogic("team rocket","password123");

		String msg = "<action type='room-joined'><usrlist ucount='1'><usr name='team rocket' id='1'></usr></usrlist></action>";
		String msg2 = "<action type='move'> <queen move='a3-g3'></queen><arrow move='h4'></arrow></action>";

//		Message message = new Message();
//		message.unmarshal(msg);
////		message.marshal();
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
}
