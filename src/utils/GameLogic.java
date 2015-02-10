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

	public static void main(String[] args) throws JAXBException
	{
		//gamelogic gamelogic = new gamelogic("team rocket","password123");

		String msg = "<action type='room-joined'><usrlist ucount='1'><usr name='team rocket' id='1'></usr></usrlist></action>";
		String msg2 = "<action type='move'> <queen move='a3-g3'></queen><arrow move='h4'></arrow></action>";

		OurPair<Integer, Integer> InitialQ = new OurPair<Integer, Integer>(0,0);
		OurPair<Integer, Integer> FinalQ = new OurPair<Integer, Integer>(5,5);
		OurPair<Integer, Integer> Arrow = new OurPair<Integer, Integer>(5,2);

		Message message = new Message();
		message.unmarshal(msg);

//		message.setType("move");
//		message.setMove(InitialQ, FinalQ);
//		message.setArrow(Arrow);

		System.out.println(message.getArrow());
		System.out.println(message.marshal());
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
}
