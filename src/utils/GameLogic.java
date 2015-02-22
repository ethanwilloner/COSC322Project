package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import MessageParsing.Arrow;
import MessageParsing.Queen;
import MessageParsing.User;
import ai.OurPair;
import minimax.concurrentMinimax;
import minimax.minimaxSearch;
import net.n3.nanoxml.IXMLElement;
import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer;
import MessageParsing.Action;
import ai.OurBoard;

public class GameLogic implements GamePlayer
{
	public static final Logger debug;
	private static Handler handler;
	
	static
	{
		debug = Logger.getLogger(GameLogic.class.getPackage().getName());
		debug.setLevel(Level.ALL);
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.ALL);
		Calendar date = Calendar.getInstance();
		String fileName = date.get(Calendar.YEAR)+"-"+(date.get(Calendar.MONTH)+1)+"-"+date.get(Calendar.DATE)+"-"+date.get(Calendar.HOUR_OF_DAY)+""+date.get(Calendar.MINUTE);
		try
		{
			new File("logs").mkdir();
			handler = new FileHandler("logs/"+fileName+".xml", true);
			handler.setFormatter(new XMLFormatter());
			handler.setLevel(Level.ALL);
			debug.addHandler(handler);
		}
		catch (Exception e)
		{
			debug.warning("Could not add FileHandler to debug logger! Stack trace to follow:");
			e.printStackTrace();
		}
	}
	
    static OurBoard ourBoard;
    static String TeamName = "Team Rocket";
    static String TeamPassword = "password";
    static String TeamRole;
    static int TeamID;
    static int TeamSide;
    static GameClient gameClient = null;
    static int roomId;
    static ArrayList<GameRoom> roomList;
    static Action receivedAction;
    static Action sendAction;
    static JAXBContext jaxbContext;
    static minimaxSearch minimaxSearch;

    public static void main(String[] args) throws JAXBException
    {
        jaxbContext = JAXBContext.newInstance(Action.class);
        GameLogic gamelogic = new GameLogic(TeamName,TeamPassword);
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

        //see whose turn it is

//		OurBoard board = new OurBoard();
//		
//		minimaxSearch minimax = new minimaxSearch();
//		
//		
//		minimaxNode move = minimax.minimax(board, 1, false, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
//		
//		System.out.println("Minimax value: " + move.getValue());
//		
//		board.makeMove(move.getMove());
//		
//		System.out.println(board);

        //samplePlay();
    }

    //Prints the id, name, and user count of all available game rooms in the game client
    private static void getOurRooms() {
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
        System.out.println(arg0);
        return true;
    }

    public boolean handleMessage(GameMessage arg0) throws Exception {
        System.out.println("[SimplePlayer: The server said =]  " + arg0.toString());

        // unmarshal message into object
        receivedAction = unmarshal(arg0.toString());

        if(receivedAction.type.toString().equalsIgnoreCase(GameMessage.ACTION_ROOM_JOINED))
        {
            System.out.println("Users in the current room:");
            // Print list of users in the room
            for(User user : receivedAction.getUserList().getUsers())
            {
                System.out.println("\tName: " + user.getName() + ", ID: " + user.getId());
            }

        }
        else if (receivedAction.type.toString().equalsIgnoreCase(GameMessage.ACTION_GAME_START))
        {
            //TODO add logging for start of game message
            System.out.println("Game has started");
            for(User user : receivedAction.getUserList().getUsers())
            {
                if(user.name.equalsIgnoreCase(TeamName))
                {
                    TeamRole = user.getRole();
                    TeamID = user.getId();
                    TeamSide = (TeamRole.equalsIgnoreCase("W") ? 1:2);
                }
            }
            //TODO add logging for what our side and purpose is
            System.out.println("Team name: " + TeamName);
            System.out.println("ID: " + TeamID);

            //TODO add support for being a spectator if there are too many people in the room
            System.out.println("Team Role: " + (TeamSide == 1? "White":"Black"));

            if(TeamSide == 1) {
                makeFirstMove();
            }

        }
        else if(receivedAction.type.toString().equalsIgnoreCase(GameMessage.ACTION_MOVE))
        {

            handleMove();
        }

        return true;
    }

    public static void makeFirstMove() throws JAXBException {
        minimaxSearch = new minimaxSearch();

        concurrentMinimax cMinimax = new concurrentMinimax(10);
        long start, end;

        start = System.currentTimeMillis();
        // Get a move from the concurrent minimax
        Move move = cMinimax.minimaxDecision(ourBoard, TeamID);

        //Make the move on our board
        ourBoard.makeMove(move);
        // Construct the new Action object that we will send to the server

        sendAction = new Action();
        sendAction.type = GameMessage.ACTION_MOVE;
        Queen ourQueen = new Queen();
        ourQueen.setMove(move.getInitialQ(), move.getFinalQ());
        Arrow ourArrow = new Arrow();
        ourArrow.setArrow(move.getArrow());
        sendAction.setQueen(ourQueen);
        sendAction.setArrow(ourArrow);

        String marshalledAction = marshal(sendAction);
        System.out.println("Our marshalled Action: " + marshalledAction);

        String serverMsg = ServerMessage.compileGameMessage(GameMessage.ACTION_MOVE, roomId, marshalledAction);
        gameClient.sendToServer(serverMsg, true);

        // Repositioned the timer to take into account the time used to build the object and send to the server
        end = System.currentTimeMillis() - start;

        // End of turn statistics
        System.out.println("Time: " + end / 1000 + " seconds");
        System.out.println("move made: " + move);
        System.out.println("Current evaluation: " + OurEvaluation.evaluateBoard(ourBoard, 1)[0] + "\t" + OurEvaluation.evaluateBoard(ourBoard, 1)[1]);
        System.out.println(ourBoard);
    }

    public static void handleMove() throws JAXBException {
        //Get initialQ, finalQ and arrow from the move that the opponent made, and make it on our board
        OurPair initialQ = receivedAction.queen.getInitialQ();
        OurPair finalQ = receivedAction.queen.getFinalQ();
        OurPair arrow = receivedAction.arrow.getArrow();
        Move opponentMove = new Move(initialQ, finalQ, arrow);
        ourBoard.makeMove(opponentMove);

        //If it is the end of the game, print end game stats and then exit the application
        if (GameRules.checkEndGame(ourBoard) == 1)
        {
            System.out.println(GameRules.checkEndGame(ourBoard));
            System.out.println("All legal white moves: " + GameRules.getLegalMoves(ourBoard, 1));
            System.out.println("All legal black moves: " + GameRules.getLegalMoves(ourBoard, 2));
            System.out.println("\n\nGame over");
            //TODO not sure what to do when game is actually over
            System.exit(0);
        }

        minimaxSearch = new minimaxSearch();

        concurrentMinimax cMinimax = new concurrentMinimax(10);
        long start, end;

        start = System.currentTimeMillis();
        // Get a move from the concurrent minimax
        Move move = cMinimax.minimaxDecision(ourBoard, TeamID);

        //Make the move on our board
        ourBoard.makeMove(move);
        // Construct the new Action object that we will send to the server
        sendAction = new Action();
        sendAction.type = GameMessage.ACTION_MOVE;
        Queen ourQueen = new Queen();
        ourQueen.setMove(move.getInitialQ(), move.getFinalQ());
        Arrow ourArrow = new Arrow();
        ourArrow.setArrow(move.getArrow());
        sendAction.setQueen(ourQueen);
        sendAction.setArrow(ourArrow);

        String marshalledAction = marshal(sendAction);
        System.out.println("Our marshalled Action: " + marshalledAction);

        String serverMsg = ServerMessage.compileGameMessage(GameMessage.ACTION_MOVE, roomId, marshalledAction);
        gameClient.sendToServer(serverMsg, true);

        // Repositioned the timer to take into account the time used to build the object and send to the server
        end = System.currentTimeMillis() - start;

        // End of turn statistics
        System.out.println("Time: " + end/1000 + " seconds");
        System.out.println("move made: " + move);
        System.out.println("Current evaluation: "+ OurEvaluation.evaluateBoard(ourBoard, 1)[0] + "\t" + OurEvaluation.evaluateBoard(ourBoard, 1)[1]);
        System.out.println(ourBoard);
    }

    /**
     * @param msg Reads in an XML string from the game server and unmarshalls
     *            it into the object mappings
     * @throws JAXBException
     */

    public static Action unmarshal(String msg) throws JAXBException {
        InputStream is = new ByteArrayInputStream(msg.getBytes());
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Action action = (Action) unmarshaller.unmarshal(is);
        return action;
    }

    /**
     * @return String which is the XML formatting for the response message
     * to the server
     * @throws JAXBException
     */
    public static String marshal(Action action) throws JAXBException {
        OutputStream os = new ByteArrayOutputStream();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(action, os);

        return os.toString();
    }

    public static void samplePlay()
    {
        int side = 1;

        OurBoard board = new OurBoard();

        minimaxSearch minimax = new minimaxSearch();
        
        concurrentMinimax cMinimax = new concurrentMinimax(10);
        long start, end;

        //while we are still playing
        //while (OurEvaluation.evaluateBoard(board, side)[1] == 0)
        while(GameRules.checkEndGame(board) == 0)
        {
        	//time run
        	start = System.currentTimeMillis();
        	
            //minimaxNode node = minimax.minimax(board, 2, true, side, Integer.MIN_VALUE, Integer.MAX_VALUE);
            
            Move move = cMinimax.minimaxDecision(board, side);
            
            //Move move = minimax.getDecision(board, side, 2);
            
            end = System.currentTimeMillis() - start;
            
            board.makeMove(move);

            System.out.println("Time: " + end/1000 + " seconds");
            System.out.println("move made: " + move);

            //System.out.println("minimax score " + node.getValue());

            System.out.println("Current evaluation: "+ OurEvaluation.evaluateBoard(board, 1)[0] + "\t" + OurEvaluation.evaluateBoard(board, 1)[1]);

            System.out.println(board);

            side = (side==1)?2:1;

        }

        System.out.println(GameRules.checkEndGame(board));
        
        System.out.println("All legal white moves: " + GameRules.getLegalMoves(board, 1));
        
        System.out.println("All legal black moves: " + GameRules.getLegalMoves(board, 2));
    }
}
