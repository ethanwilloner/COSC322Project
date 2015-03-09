package utils;

import AbstractClasses.GameSearch;
import Messages.*;
import ai.OurBoard;
import minimax.concurrentMinimax;
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
    static OurBoard ourBoard = new OurBoard();
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

    static int threadCount = 3;
    //static minimaxSearch minimaxSearch = new minimaxSearch();
    static concurrentMinimax minimaxSearch = new concurrentMinimax(threadCount);

    public static void main(String[] args) throws JAXBException
    {
//        xmlParser = new XMLParser();
//        GameLogic gamelogic = new GameLogic(TeamName,TeamPassword);

        samplePlay();
    }

    public GameLogic(String name, String passwd)
    {
        gameClient = new GameClient(TeamName, TeamPassword, this);
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
            //TODO add logging for start of game message
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
            //TODO add logging for what our side and purpose is
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
        checkEndGame(ourBoard);

        // if we are not the ones making the first move in the game, we can start parsing the first action message we get
        if(makeFirstMove == false)
        {
            //Get initialQ, finalQ and arrow from the move that the opponent made, and make it on our board
            Move opponentMove = new Move(receivedAction.getQueen().getInitialQ(),
                                            receivedAction.getQueen().getFinalQ(),
                                            receivedAction.getArrow().getArrow());

            // Checks to make sure that the opponents move was legal
            try {
                ourBoard.makeMove(opponentMove);
            }catch (NullPointerException e)
            {
                System.out.println("Opponent made an illegal move: ");
                opponentMove.moveInfo(ourBoard);
                e.printStackTrace();
                System.exit(0);
            }

            // Print the opponents move
            System.out.println("\n\nOpponents Turn:");
            opponentMove.moveInfo(ourBoard);
        }

        long start, end;

        start = System.currentTimeMillis();

        // Get a move from the concurrent minimax
        Move move = minimaxSearch.getMove(ourBoard, TeamSide);
        //Move move = cMinimax.getMove(ourBoard, TeamID);

        //Make the move on our board
        try
        {
            ourBoard.makeMove(move);
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        // Construct the new Action object that we will send to the server
        sendAction = new Action(GameMessage.ACTION_MOVE);
        sendAction.setQueen(new Queen(move.getInitialQ(), move.getFinalQ()));
        sendAction.setArrow(new Arrow(move.getArrow()));

        // Send message to server
        sendToServer(sendAction, roomId);

        // Repositioned the timer to take into account the time used to build the object and send to the server
        end = System.currentTimeMillis() - start;

        // End of turn statistics
        System.out.println("\n\nOur Turn:");
        System.out.println("Time: " + end/1000 + " seconds");
        move.moveInfo(ourBoard);

        //If it is the end of the game, print end game stats and then exit the application
        checkEndGame(ourBoard);

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

    public static void checkEndGame(OurBoard board)
    {
        if (GameRules.checkEndGame(ourBoard) != 0)
        {
            System.out.println(GameRules.checkEndGame(ourBoard));
            System.out.println("All legal white moves: " + GameRules.getLegalMoves(ourBoard, 1));
            System.out.println("All legal black moves: " + GameRules.getLegalMoves(ourBoard, 2));
            System.out.println("\n\nGame over");
            //TODO not sure what to do when game is actually over
            System.exit(0);
        }
    }

    public static void sendToChat(String msg, int roomID) throws JAXBException {

    }

    public static void samplePlay()
    {
        int side = 1;

        OurBoard board = new OurBoard();

        GameSearch search = minimaxSearch;

        long start, end;

        //while we are still playing
        //while (OurEvaluation.evaluateBoard(board, side)[1] == 0)
        while(GameRules.checkEndGame(board) == 0)
        {
            //time run
            start = System.currentTimeMillis();

            //minimaxNode node = minimax.minimax(board, 2, true, side, Integer.MIN_VALUE, Integer.MAX_VALUE);

            Move move = search.getMove(board, side);

            //Move move = minimax.getDecision(board, side, 2);

            end = System.currentTimeMillis() - start;

            board.makeMove(move);

            System.out.println("Time: " + end/1000 + " seconds");
            System.out.println("move made: " + move);

            //System.out.println("minimax score " + node.getValue());

            System.out.println("Current evaluation: "+ OurEvaluation.evaluateBoard(board, 1, true)[0] + "\t" + OurEvaluation.evaluateBoard(board, 1, false)[1]);

            //OurEvaluation.evaluateBoardOutput(board, 1);
            
            System.out.println(board);

            side = (side==1)?2:1;

        }

        System.out.println(GameRules.checkEndGame(board));

        System.out.println("All legal white moves: " + GameRules.getLegalMoves(board, 1));

        System.out.println("All legal black moves: " + GameRules.getLegalMoves(board, 2));
    }
}
