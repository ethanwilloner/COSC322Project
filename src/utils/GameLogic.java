package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import minimax.concurrentMinimax;
import minimax.minimaxNode;
import minimax.minimaxSearch;
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

    static GameClient gameClient = null;
    int roomId;
    static ArrayList<GameRoom> roomList;
    static Action action;
    static Action recvAction;
    static Action sendAction;
    static JAXBContext jaxbContext;

    public static void main(String[] args) throws JAXBException
    {
        /*jaxbContext = JAXBContext.newInstance(Action.class);
        action = new Action();

        // Example code for using the JAXB objects to read and create new objects
		String msg = "<action type='room-joined'><usrlist ucount='1'><usr name='team rocket' id='1'></usr></usrlist></action>";
		String msg2 = "<action type='move'> <queen move='a3-g3'></queen><arrow move='h4'></arrow></action>";

		OurPair InitialQ = new OurPair(0,0);
		OurPair FinalQ = new OurPair(5,5);
		OurPair Arrow = new OurPair(5,2);

		action.setType("move");
		action.setQueen(new Queen());
		action.setArrow(new Arrow());
		action.queen.setMove(InitialQ, FinalQ);
		action.arrow.setArrow(Arrow);

		System.out.println(marshal());

		action = new Action();
		unmarshal(msg);
		System.out.println(marshal());
*/
        GameLogic gamelogic = new GameLogic("team rocket","password123");
        
        
    }

    public GameLogic(String name, String passwd)
    {
        /*
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
        */

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

        samplePlay();
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
        String msg = XMLParser.parseXML(arg0);
        System.out.print(arg0);
        return true;
    }

    public boolean handleMessage(GameMessage arg0) throws Exception {
        System.out.println("[SimplePlayer: The server said =]  " + arg0.toString());

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


        return true;
    }

    public void sendToServer(String msgType, int roomID){
        String msg = "Message goes here";
        boolean isMove = true;
        ServerMessage.compileGameMessage(msgType, roomId, msg);
        gameClient.sendToServer(msg, isMove);
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

    public static void samplePlay()
    {
        int side = 1;

        OurBoard board = new OurBoard();

        minimaxSearch minimax = new minimaxSearch();
        
        concurrentMinimax cMinimax = new concurrentMinimax(3);
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
