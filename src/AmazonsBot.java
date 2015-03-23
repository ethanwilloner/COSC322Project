import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import AmazonBoard.GameBoardRules;
import AmazonBoard.GameMove;
import AmazonBoard.IllegalMoveException;
import EvaluationFunctions.MullerTegosEvaluation;
import MiniMax.ConcurrentMiniMax;
import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer;
import AbstractClasses.Evaluation;
import AbstractClasses.GameSearch;
import EvaluationFunctions.SimpleEvaluation;
import Messaging.Action;
import Messaging.Arrow;
import Messaging.Queen;
import Messaging.User;
import Messaging.XMLParser;
import AmazonBoard.GameBoard;

public class AmazonsBot implements GamePlayer
{
    static GameBoard gameBoard = new GameBoard();
    static String TeamName = "Team Rocket1";
    static String TeamPassword = "password";
    static GameClient gameClient;

    static ArrayList<GameRoom> roomList;
    static int TeamID;
    static int TeamSide;
    static int roomId;
    static boolean gameStarted;

    static XMLParser xmlParser;
    static Action receivedAction;
    static Action sendAction;

    static int threadCount = 4;
    static Evaluation simpleEval = new SimpleEvaluation();
    static Evaluation eval = new MullerTegosEvaluation();
    
//  static GameSearch minimaxSearch = new minimaxSearch();
    static GameSearch minimaxSearch = new ConcurrentMiniMax(threadCount, eval);

    //set the evaluation
    static
    {
    	minimaxSearch.setEvaluation(eval);
    }
    
    public static void main(String[] args) throws JAXBException
    {
        if(args.length > 1)
        {
            if(args.length == 2)
            {
                TeamName = args[0];
                threadCount = Integer.parseInt(args[1]);
            } else {
                System.out.println("Usage:");
                System.out.println("\tAmazonBot [Team Name] [Thread Count]");
                System.exit(1);
            }
        }

        System.out.println("Starting Amazons Bot with:");
        System.out.println("\tTeam Name: " + TeamName);
        System.out.println("\tThread Count: " + threadCount);
        System.out.println();

        xmlParser = new XMLParser();
        AmazonsBot amazonsBot = new AmazonsBot(TeamName,TeamPassword);

//        samplePlay();
    }

    public AmazonsBot(String name, String password)
    {
        gameClient = new GameClient(name, password, this);
        do {
            getOurRooms();
            Scanner scanner = new Scanner(System.in);
            System.out.print("Input roomID to join: ");
            roomId = scanner.nextInt();

        }while (joinRoom(roomId) == false);
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
            System.out.println("\nFailed to join room: " + roomId + "\n");
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
        receivedAction = xmlParser.unmarshal(arg0.toString());

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
            gameStarted = true;
            System.out.println("\n\nGame has started");

            for(User user : receivedAction.getUserList().getUsers())
            {
                // Determine if we are W, B, or Spectator
                if(user.name.equalsIgnoreCase(TeamName))
                {
                    TeamID = user.getId();
                    switch (user.getRole())
                    {
                        case "W":
                            TeamSide = 1;
                            break;
                        case "B":
                            TeamSide = 2;
                            break;
                        case "S":
                            TeamSide = -1;
                            break;
                        default:
                            TeamSide = -1;
                            break;
                    }
                }
            }
            System.out.println("Team name: " + TeamName);
            System.out.println("ID: " + TeamID);
            System.out.println("Team Role: " + TeamSide);

            if(TeamSide == 1)
            {
                handleMove(true);
            }
        }
        else if(receivedAction.type.toString().equalsIgnoreCase(GameMessage.ACTION_MOVE))
        {
            System.out.println("Received action");
            handleMove(false);
        }
        return true;
    }

    private static void handleMove(boolean makeFirstMove) throws JAXBException {
        if(!gameStarted){
            return;
        }
        if(TeamSide == -1)
        {
            System.out.println("We are a spectator, no moves can be made");
            return;
        }

        //If it is the end of the game, print end game stats and then exit the application
        checkEndGame(gameBoard);

        // if we are not the ones making the first gameMove in the game, we can start parsing the first action message we get
        if(makeFirstMove == false)
        {
            //Get initialQ, finalQ and arrow from the gameMove that the opponent made, and make it on our board
            GameMove opponentGameMove = new GameMove(receivedAction.getQueen().getInitialQ(),
                                            receivedAction.getQueen().getFinalQ(),
                                            receivedAction.getArrow().getArrow());

            // Checks to make sure that the opponents gameMove was legal
            try {
                gameBoard.makeMove(opponentGameMove);
            }catch (IllegalMoveException e)
            {
                System.out.println("Opponent made an illegal gameMove: ");
                opponentGameMove.moveInfo(gameBoard);
                e.printStackTrace();
                System.exit(0);
            }

            // Print the opponents gameMove
            System.out.println("\n\nOpponents Turn:");
            opponentGameMove.moveInfo(gameBoard);
            
        }
        
        checkEndGame(gameBoard);
        
        long start, end;

        start = System.currentTimeMillis();

        GameMove gameMove = null;
        try {
            // Get a gameMove from the MiniMax search
            gameMove = minimaxSearch.getMove(gameBoard, TeamSide);
        } catch (IllegalMoveException e) {
            System.out.println("Failed to get gameMove: ");
            e.printStackTrace();
            //TODO implement method for getting first available gameMove incase concurrent search fails
            System.exit(1);
        }

        //Make the gameMove on our board
        try
        {
            gameBoard.makeMove(gameMove);
        } catch (IllegalMoveException e)
        {
            System.out.println("Illegal GameMove made: ");
            e.printStackTrace();
            System.exit(0);
        }

        // Construct the new Action object that we will send to the server
        sendAction = new Action(GameMessage.ACTION_MOVE);
        sendAction.setQueen(new Queen(gameMove.getInitialQ(), gameMove.getFinalQ()));
        sendAction.setArrow(new Arrow(gameMove.getArrow()));

        // Send message to server
        sendToServer(sendAction, roomId);

        // Repositioned the timer to take into account the time used to build the object and send to the server
        end = System.currentTimeMillis() - start;

        // End of turn statistics
        System.out.println("\n\nOur Turn:");
        System.out.println("Time: " + end / 1000 + " seconds");
        gameMove.moveInfo(gameBoard);

        //If it is the end of the game, print end game stats and then exit the application
        checkEndGame(gameBoard);

        // Call the garbage collector when we are done each turn
        System.runFinalization();
        System.gc();
    }

    /**
     *
     * @param action
     * @param roomID
     * @throws JAXBException
     *
     * Takes in an Action object, marshals it into XML, and then uses the built in
     * compileGameMessage to convert to server format and sends with gameClient.sendToServer
     */
    public static void sendToServer(Action action, int roomID) throws JAXBException {
        String actionMsg = xmlParser.marshal(action);
        String compiledGameMessage = ServerMessage.compileGameMessage(GameMessage.MSG_GAME, roomID, actionMsg);
        gameClient.sendToServer(compiledGameMessage, true);
    }

    public static void checkEndGame(GameBoard board)
    {
        if (GameBoardRules.checkEndGame(gameBoard) != 0)
        {
            System.out.println(GameBoardRules.checkEndGame(gameBoard));
            System.out.println("All legal white moves: " + GameBoardRules.getLegalMoves(gameBoard, 1));
            System.out.println("All legal black moves: " + GameBoardRules.getLegalMoves(gameBoard, 2));
            System.out.println("\n\nGame over");
            System.exit(0);
        }
    }

    public static void sendToChat(String msg, int roomID) throws JAXBException {

    }

    public static void samplePlay() throws IllegalMoveException {
        int side = 1;

        GameBoard board = new GameBoard();

        GameSearch search = minimaxSearch;

        long start, end;
        Evaluation e = eval;
        
        //while we are still playing
        //while (MullerTegosEvaluation.evaluateBoard(board, side)[1] == 0)
        while(GameBoardRules.checkEndGame(board) == 0)
        {
//        	if (side == 1)
//        	{
//        		e = simpleEval;
//        	}
//        	else
//        		e = eval;
        	
        	search.setEvaluation(e);
        	
        	
            //time run
            start = System.currentTimeMillis();

            //minimaxNode node = MiniMax.MiniMax(board, 2, true, side, Integer.MIN_VALUE, Integer.MAX_VALUE);

            GameMove gameMove = search.getMove(board, side);

            //GameMove gameMove = MiniMax.getDecision(board, side, 2);

            end = System.currentTimeMillis() - start;

            try {
                board.makeMove(gameMove);
            } catch (IllegalMoveException e1) {
                System.out.println("Illegal GameMove made: ");
                e1.printStackTrace();
                System.exit(0);
            }

            System.out.println("Time: " + end/1000 + " seconds");
            System.out.println("gameMove made: " + gameMove);

            //System.out.println("MiniMax score " + node.getValue());

            System.out.println("Current evaluation: "+ MullerTegosEvaluation.evaluateBoard(board, 1, true)[0] + "\t" + MullerTegosEvaluation.evaluateBoard(board, 1, false)[1]);
            System.out.println("Simple evaluation: "+ simpleEval.evaluateBoard(board, 1));
            System.out.println(board);

            side = (side==1)?2:1;

        }

        System.out.println(GameBoardRules.checkEndGame(board));

        System.out.println("All legal white moves: " + GameBoardRules.getLegalMoves(board, 1));

        System.out.println("All legal black moves: " + GameBoardRules.getLegalMoves(board, 2));
    }
}
