package ai;

import java.util.ArrayList;
import java.util.Stack;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;
import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer;
import ai.gui.GUI;
import ai.search.Agent;


/**
 * A GamePlayer for our group
 * @author Yarko Senyuta
 *
 */
public class OurPlayer implements GamePlayer {

	/**
	 * the GameClient connected to
	 */
	private GameClient client;
	/**
	 * the search agent
	 */
	private Agent agent;
	/**
	 * a game board instance
	 */
	private OurBoard board;
	/**
	 * the GUI attached to this player
	 */
	private GUI gui;
	/**
	 * for XML parsing
	 */
	private XMLParser parser;

	
	private int whiteTiles;
	private int blackTiles;
	private int bothCanReach;

	private String userName;

	/**
	 * number of rows in the game
	 */
	private final int ROWS = 10;
	/**
	 * number of columns in the game
	 */
	private final int COLS = 10;
	
	/**
	 * white queen code
	 */
	private final int WQUEEN = 1;
	/**
	 * black queen code
	 */
	private final int BQUEEN = 2;
	/**
	 * placed arrow code
	 */
	private final int ARROW = 3;
	
	/**
	 * the player's code
	 */
	private int playerID;
	private String role;

	/**
	 * is it the opponent's turn?
	 */
	private boolean isOpponentsTurn;
	/**
	 * is the game over?
	 */
	private boolean finished;
	
	/**
	 * which room we are in
	 */
	private int roomNumber;

	/**
	 * constructor
	 * @param userName user name for server
	 * @param password password for server
	 */
	public OurPlayer(String userName, String password) {

		//set username
		this.userName = userName;

		//connect to client
		client = new GameClient(userName, password, this);
		//initialize board
		board = new OurBoard(ROWS, COLS);
		//initialize gui
		gui = new GUI(board, ROWS, COLS);
		//initialize xml parser
		parser = new XMLParser();
	
	}

	/**
	 * connect to a random game room
	 */
	public void joinServer(){
		client.roomList = getRooms();	
		gui.init();
		
		for (GameRoom g : client.roomList) {
			try {
				client.joinGameRoom(g.roomName);
				roomNumber = g.roomID;
				break;
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * join a specific room
	 * @param roomName the game room wanting to join
	 */
	public void joinServer(String roomName){
		client.roomList = getRooms();
		gui.init();
		
		try {
			client.joinGameRoom(roomName);
			roomNumber = client.roomList.indexOf(roomName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 * start the game
	 * @param playerNumber role assigned
	 */
	public void startGame(int playerNumber) {
		
		System.out.println("Game started");
		
		if (playerNumber == 1) {
			isOpponentsTurn = false;
			playerID = 1;
			role = "W";
		} else {
			isOpponentsTurn = true;
			playerID = 2;
			role = "B";
		}
		//initialize search agent
		agent = new Agent(board, ROWS, COLS, playerID);

		
		finished = false;

		//launch game player
		inGame();

	}

	private void inGame() {

		//while the game hasn't finished,
		do {

			if (isOpponentsTurn) {
				// TODO: Plan ahead based on possible moves
				waitForMove();

				isOpponentsTurn = false;
				finished = isFinished();

			} else {
				// TODO: Pick a move and send it to the server
				String move = agent.selectMove();
				String serverMessage = ServerMessage.compileGameMessage(ServerMessage.USR_MSG, roomNumber, GameMessage.ACTION_MOVE);
				gui.addServerMessage("My ", serverMessage);
				
				client.sendToServer(serverMessage, true);
				isOpponentsTurn = true;
			}

		} while (!finished);

	}

	/**
	 * wait for opponent's turn, could do something at this time
	 */
	private void waitForMove(){
		while (isOpponentsTurn){
			//isOpponentsTurn = false;
		}
	}

	/**
	 * check if at goal state
	 * 
	 */
	private boolean isFinished() {

		//get positions
		ArrayList<OurPair<Integer, Integer>> wPositions = board.getWhitePositions();
		ArrayList<OurPair<Integer, Integer>> bPositions = board.getBlackPositions();

		//tiles checked
		int[][] hasChecked = new int[ROWS][COLS];

		//count the number of reachable tiles for each white queen
		for (OurPair<Integer, Integer> OurPair : wPositions) {
			countReachableTiles(OurPair, WQUEEN, hasChecked);
		}

		//count the number of reachable tiles for each black queen
		for (OurPair<Integer, Integer> OurPair : bPositions) {
			countReachableTiles(OurPair, BQUEEN, hasChecked);
		}

		whiteTiles = 0;
		blackTiles = 0;
		bothCanReach = 0;

		//count the number of moves reachable by each side, including the neutral ones
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				switch (hasChecked[i][j]) {
				case (1):
					whiteTiles++;
				break;
				case (2):
					blackTiles++;
				break;
				case (3):
					bothCanReach++;
				break;
				}
			}
		}

		//check if at goal state
		if (blackTiles > whiteTiles + bothCanReach) {
			return true;
		} else if (whiteTiles > blackTiles + bothCanReach) {
			return true;
		}
		return false;
	}

	/****************************************************************************************
	 * This is a stack based search of the game board, we flag each tile in the grid as either
	 * belonging to White, Black, or is Neutral. If one sides score is larger than the others,
	 * plus the neutral tiles, then that side is declared the winner.
	 * 
	 * @param source
	 *            - An integer OurPairing (x,y) for where the amazon piece is
	 * @param player
	 *            - 1 for White, 2 for Black
	 * @param hasChecked[][]
	 * 			  - 2D integer array for mapping which pieces can reach which tiles in the grid
	 * 
	 ***************************************************************************************/
	private void countReachableTiles(OurPair<Integer, Integer> source, int player,
			int[][] hasChecked) {

		int opponent;
		switch (player) {
		case (WQUEEN):
			opponent = BQUEEN;
		break;
		default:
			opponent = WQUEEN;
		}

		Stack<OurPair<Integer, Integer>> stack = new Stack<>();

		hasChecked[source.getLeft()][source.getRight()] = player;

		stack.push(source);

		while (!stack.empty()) {
			// Check 8 diagonal positions.
			OurPair<Integer, Integer> top = stack.pop();
			int xPos = top.getLeft();
			int yPos = top.getRight();

			// Check boundary
			if (xPos - 1 >= 0) {
				// If it is free
				if (!board.isMarked((xPos - 1), yPos)) {
					// If we haven't looked at it yet
					if (hasChecked[xPos - 1][yPos] == 0) {
						stack.push(new OurPair<>(xPos - 1, yPos));
						hasChecked[xPos - 1][yPos] = player;
					} else if (hasChecked[xPos - 1][yPos] == opponent) {
						stack.push(new OurPair<>(xPos - 1, yPos));
						hasChecked[xPos - 1][yPos] = ARROW;
					}
				}
			}
			if (xPos + 1 < ROWS) {
				if (!board.isMarked((xPos + 1), yPos)) {
					if (hasChecked[xPos + 1][yPos] == 0) {
						// If we haven't looked at it yet
						stack.push(new OurPair<>(xPos + 1, yPos));
						hasChecked[xPos + 1][yPos] = player;
					} else if (hasChecked[xPos + 1][yPos] == opponent) {
						stack.push(new OurPair<>(xPos + 1, yPos));
						hasChecked[xPos + 1][yPos] = ARROW;
					}
				}
			}
			if (yPos - 1 >= 0) {
				if (!board.isMarked((xPos), yPos - 1)) {
					if (hasChecked[xPos][yPos - 1] == 0) {
						// If we haven't looked at it yet
						stack.push(new OurPair<>(xPos, yPos - 1));
						hasChecked[xPos][yPos - 1] = player;
					} else if (hasChecked[xPos][yPos - 1] == opponent) {
						stack.push(new OurPair<>(xPos, yPos - 1));
						hasChecked[xPos][yPos - 1] = ARROW;
					}
				}
			}
			if (yPos + 1 < COLS) {
				if (!board.isMarked(xPos, yPos + 1)) {
					if (hasChecked[xPos][yPos + 1] == 0) {
						// If we haven't looked at it yet
						stack.push(new OurPair<>(xPos, yPos + 1));
						hasChecked[xPos][yPos + 1] = player;
					} else if (hasChecked[xPos][yPos + 1] == opponent) {
						stack.push(new OurPair<>(xPos, yPos + 1));
						hasChecked[xPos][yPos + 1] = ARROW;
					}
				}
			}
			if ((xPos + 1 < ROWS) && (yPos + 1 < COLS)) {
				if (!board.isMarked((xPos + 1), yPos + 1)) {
					if (hasChecked[xPos + 1][yPos + 1] == 0) {
						// If we haven't looked at it yet
						stack.push(new OurPair<>(xPos + 1, yPos + 1));
						hasChecked[xPos + 1][yPos + 1] = player;
					} else if (hasChecked[xPos + 1][yPos + 1] == opponent) {
						hasChecked[xPos + 1][yPos + 1] = ARROW;
						stack.push(new OurPair<>(xPos + 1, yPos + 1));
					}
				}
			}
			if ((xPos + 1 < ROWS) && (yPos - 1 >= 0)) {
				if (!board.isMarked((xPos + 1), yPos - 1)) {
					if (hasChecked[xPos + 1][yPos - 1] == 0) {
						// If we haven't looked at it yet
						stack.push(new OurPair<>(xPos + 1, yPos - 1));
						hasChecked[xPos + 1][yPos - 1] = player;
					} else if (hasChecked[xPos + 1][yPos - 1] == opponent) {
						stack.push(new OurPair<>(xPos + 1, yPos - 1));
						hasChecked[xPos + 1][yPos - 1] = ARROW;
					}
				}
			}
			if ((xPos - 1 >= 0) && (yPos + 1 < COLS)) {
				if (!board.isMarked((xPos - 1), yPos + 1)) {
					if (hasChecked[xPos - 1][yPos + 1] == 0) {
						// If we haven't looked at it yet
						stack.push(new OurPair<>(xPos - 1, yPos + 1));
						hasChecked[xPos - 1][yPos + 1] = player;

					} else if (hasChecked[xPos - 1][yPos + 1] == opponent) {
						stack.push(new OurPair<>(xPos - 1, yPos + 1));
						hasChecked[xPos - 1][yPos + 1] = ARROW;
					}
				}
			}
			if ((xPos - 1 >= 0) && (yPos - 1 >= 0)) {
				if (!board.isMarked((xPos - 1), yPos - 1)) {
					if (hasChecked[xPos - 1][yPos - 1] == 0) {
						// If we haven't looked at it yet
						stack.push(new OurPair<>(xPos - 1, yPos - 1));
						hasChecked[xPos - 1][yPos - 1] = player;
					} else if (hasChecked[xPos - 1][yPos - 1] == opponent) {
						stack.push(new OurPair<>(xPos - 1, yPos - 1));
						hasChecked[xPos - 1][yPos - 1] = ARROW;
					}
				}
			}
		}
	}

	public ArrayList<GameRoom> getRooms() {
		ArrayList<GameRoom> rooms = client.getRoomLists();
//		for (GameRoom g : rooms) {
//			System.out.println(g.roomID + " " + g.roomName);
//		}
		return rooms;
	}

	@Override
	public boolean handleMessage(String message) throws Exception {
		gui.addServerMessage("Server message: ", message);
		return false;
	}

	@Override
	public boolean handleMessage(GameMessage message) throws Exception {

		/**
		 * These are the NanoXML classes we need to convert the message to XML and such, need to
		 * figure out the message header
		 */

		IXMLParser iParser = XMLParserFactory.createDefaultXMLParser();
		IXMLReader reader = StdXMLReader.stringReader(message.toString());
		iParser.setReader(reader);
		IXMLElement xml = (IXMLElement) iParser.parse();

		String answer = parser.handleXML(xml);
		
		if (answer.equals(GameMessage.ACTION_GAME_START)){
			
			parser.getUserInfo(xml);
			
//			startGame(BQUEEN);
		} 
		// Handle the different types of messages that we recieve.
		gui.addServerMessage("Server other message: ", message.toString());


		return false;
	}
	
	
	public void sendToServer(String messageType, String message) {

		
	}

	public static void main(String[] args) {
		OurPlayer player = new OurPlayer("KillaBot", "2222");
		
		if (args.length == 0){
			player.joinServer();
		} else {
			player.joinServer(args[0] + " " + args[1]);
		}
		
		
	}
}
