package AmazonsBot;
import AbstractClasses.Evaluation;
import AbstractClasses.GameSearch;
import AmazonBoard.GameBoard;
import AmazonBoard.GameBoardRules;
import AmazonBoard.GameMove;
import AmazonBoard.IllegalMoveException;
import EvaluationFunctions.MullerTegosEvaluation;
import EvaluationFunctions.SimpleEvaluation;
import Messaging.*;
import MiniMax.ConcurrentMiniMax;
import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer;

import javax.xml.bind.JAXBException;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class AmazonsBot implements GamePlayer {
    static GameBoard gameBoard = new GameBoard();
    static String TeamName = "Team Rocket";
    static String TeamPassword = "password";
    static GameClient gameClient;
    static XMLParser xmlParser;
    static int threadCount = 4;
    static Evaluation simpleEval = new SimpleEvaluation();
    static Evaluation eval = new MullerTegosEvaluation();
    //  static GameSearch minimaxSearch = new minimaxSearch();
    static GameSearch minimaxSearch = new ConcurrentMiniMax(threadCount, eval);
    //set the evaluation
    static {
        minimaxSearch.setEvaluation(eval);
    }
    ArrayList<GameRoom> roomList;
    int TeamID;
    int TeamSide;
    String TeamRole;
    int roomId;
    public static int moveCount = 0;
    boolean gameStarted;
    Action receivedAction;
    Action sendAction;

    static boolean doLocalPlay = false;

    public static void main(String[] args) throws JAXBException, IllegalMoveException {
        if(doLocalPlay){
            System.out.println("Starting game locally.");
            localPlay();
        }
        else if (args.length > 1) {
            if (args.length == 2) {
                TeamName = args[0];
                threadCount = Integer.parseInt(args[1]);
            } else {
                System.out.println("Usage:");
                System.out.println("\tAmazonBot [Team Name] [Thread Count]");
                System.exit(1);
            }
        } else {
            System.out.println("Starting Amazons Bot with:");
            System.out.println("\tTeam Name: " + TeamName);
            System.out.println("\tNumber of Threads Used: " + threadCount);
            System.out.println("\tNumber of Processors Available: " + Runtime.getRuntime().availableProcessors());
            System.out.println();

            xmlParser = new XMLParser();
            new AmazonsBot(TeamName, TeamPassword);
        }
    }

    public AmazonsBot(String name, String password) {
        gameClient = new GameClient(name, password, this);
        do {
            getOurRooms();
            Scanner scanner = new Scanner(System.in, Charset.defaultCharset().toString());
            System.out.print("Input roomID to join: ");
            roomId = scanner.nextInt();

        } while (joinRoom(roomId) == false);
    }

    public static void sendToChat(String msg, int roomID) throws JAXBException {

    }

    //Prints the id, name, and user count of all available game rooms in the game client
    private void getOurRooms() {
        roomList = gameClient.getRoomLists();
        System.out.println("Available Game Rooms:");
        for (GameRoom room : roomList) {
            System.out.println("Room ID: " + room.roomID + "\tRoom Name: " + room.roomName + "\tUser Count: " + room.userCount);
        }

    }

    public boolean joinRoom(int roomId) {
        roomList = gameClient.getRoomLists();
        try {
            for (GameRoom r : roomList) {
                if (r.roomID == roomId) {
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
        //System.out.println("[SimplePlayer: The server said =]  " + arg0.toString());

        // unmarshal message into object
        receivedAction = xmlParser.unmarshal(arg0.toString());

        if (receivedAction.type.toString().equalsIgnoreCase(GameMessage.ACTION_GAME_START)) {
            gameStarted = true;
            System.out.println("\n\nGame has started");
            
            // Print list of users in the room
            for (User user : receivedAction.getUserList().getUsers()) {
                System.out.println("\tName: " + user.getName() + ", ID: " + user.getId());
            }
            
            for (User user : receivedAction.getUserList().getUsers()) {
                // Determine if we are W, B, or Spectator
                if (user.name.equalsIgnoreCase(TeamName)) {
                    TeamID = user.getId();
                    TeamRole = user.getRole().equals("W")?"White":"Black";
                    switch (user.getRole()) {
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
            System.out.println("Team Role: " + TeamRole);
            System.out.println();

            if (TeamSide == 1) {
                handleMove(true);
            }
        } else if (receivedAction.type.toString().equalsIgnoreCase(GameMessage.ACTION_MOVE)) {
            handleMove(false);
        }
        return true;
    }

    private void handleMove(boolean makeFirstMove) throws JAXBException, UnsupportedEncodingException {
        if (!gameStarted) {
            return;
        }
        if (TeamSide == -1) {
            System.out.println("We are a spectator, no moves can be made");
            return;
        }

        //If it is the end of the game, print end game stats and then exit the application
        checkEndGame(gameBoard);

        // if we are not the ones making the first gameMove in the game, we can start parsing the first action message we get
        if (makeFirstMove == false) {
            //Get initialQ, finalQ and arrow from the gameMove that the opponent made, and make it on our board
            GameMove opponentGameMove = new GameMove(receivedAction.getQueen().getInitialQ(),
                    receivedAction.getQueen().getFinalQ(),
                    receivedAction.getArrow().getArrow());

            // Checks to make sure that the opponents gameMove was legal
            try {
                gameBoard.makeMove(opponentGameMove);
            } catch (IllegalMoveException e) {
                System.out.println("Opponent made an illegal gameMove: ");
                opponentGameMove.moveInfo(gameBoard);
                e.printStackTrace();
                System.exit(1);
            }

            // Print the opponents gameMove
            System.out.println("\n\nOpponents Move:");
            System.out.println("Move Number: " + ++moveCount);
            opponentGameMove.moveInfo(gameBoard);
        }

        checkEndGame(gameBoard);

        long start, end;

        System.out.println("\nOur Move:");
        System.out.println("Move Number: " + ++moveCount);

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
        try {
            gameBoard.makeMove(gameMove);
        } catch (IllegalMoveException e) {
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
        System.out.println("Time: " + end / 1000 + " seconds");
        System.out.println("Marshalled XML Message: " + xmlParser.marshal(sendAction));
        gameMove.moveInfo(gameBoard);

        //If it is the end of the game, print end game stats and then exit the application
        checkEndGame(gameBoard);

        // Call the garbage collector when we are done each turn
        System.runFinalization();
        System.gc();
    }

    /**
     * @param action
     * @param roomID
     * @throws JAXBException Takes in an Action object, marshals it into XML, and then uses the built in
     *                       compileGameMessage to convert to server format and sends with gameClient.sendToServer
     */
    public static void sendToServer(Action action, int roomID) throws JAXBException, UnsupportedEncodingException {
        String actionMsg = xmlParser.marshal(action);
        String compiledGameMessage = ServerMessage.compileGameMessage(GameMessage.MSG_GAME, roomID, actionMsg);
        gameClient.sendToServer(compiledGameMessage, true);
    }

    public static void checkEndGame(GameBoard board) {
        if (GameBoardRules.checkEndGame(gameBoard) != 0) {
            System.out.println(GameBoardRules.checkEndGame(gameBoard));
            System.out.println("All legal white moves: " + GameBoardRules.getLegalMoves(gameBoard, 1));
            System.out.println("All legal black moves: " + GameBoardRules.getLegalMoves(gameBoard, 2));
            System.out.println("\n\nGame over");
            System.exit(0);
        }
    }

    /**
     * Method to test the bot by playing it against itself locally
     * @throws IllegalMoveException
     */
    public static void localPlay() throws IllegalMoveException {
        int side = 1;

        GameBoard board = new GameBoard();

        GameSearch search = minimaxSearch;

        long start, end;
        Evaluation e = eval;

        //while we are still playing
        //while (MullerTegosEvaluation.evaluateBoard(board, side)[1] == 0)
        while (GameBoardRules.checkEndGame(board) == 0) {
            //increment move count
        	moveCount++;
        	
        	search.setEvaluation(e);

            start = System.currentTimeMillis();

            GameMove gameMove = search.getMove(board, side);

            end = System.currentTimeMillis() - start;

            try {
                board.makeMove(gameMove);
            } catch (IllegalMoveException e1) {
                System.out.println("Illegal GameMove made: ");
                e1.printStackTrace();
                System.exit(0);
            }

            System.out.println("Time: " + end / 1000 + " seconds");
            System.out.println("gameMove made: " + gameMove);

            //System.out.println("MiniMax score " + node.getValue());
            System.out.println("Current evaluation: " + MullerTegosEvaluation.evaluateBoard(board, 1, false)[0] + "\t" + MullerTegosEvaluation.evaluateBoard(board, 1, false)[1]);
            System.out.println("Simple evaluation: " + simpleEval.evaluateBoard(board, 1));
            System.out.println(board);

            side = (side == 1) ? 2 : 1;
        }

        System.out.println(GameBoardRules.checkEndGame(board));

        System.out.println("All legal white moves: " + GameBoardRules.getLegalMoves(board, 1));

        System.out.println("All legal black moves: " + GameBoardRules.getLegalMoves(board, 2));
    }
}
