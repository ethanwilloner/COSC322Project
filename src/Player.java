import java.util.ArrayList;

import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer;
/**
 * 
 * Our version of the Simple Player class
 */

public class Player implements GamePlayer {

	GameClient gameClient = null; 
	int roomId;
	
	/*
	 * Constructor 
	 */
	public Player(String name, String passwd) {
		
		//A player has to maintain an instance of GameClient, and register itself with the  
		//GameClient. Whenever there is a message from the server, the Gameclient will invoke 
		//the player's handleMessage() method.
		
		//Three arguments: user name (any), passwd (any), this (delegate)   
	    gameClient = new GameClient(name, passwd, this);
	    this.getOurRooms();
	}
	
	//Prints the id, name, and user count of all available game rooms in the game client
	private void getOurRooms() {
		ArrayList<GameRoom> roomList = gameClient.getRoomLists();
		System.out.println("Available Game Rooms:");
		for(GameRoom room : roomList)
		{
			System.out.println("Room ID: "+room.roomID + "\tRoom Name: "+room.roomName+ "\tUser Count: "+room.userCount);
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
	
	
	
}