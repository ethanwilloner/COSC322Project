import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import utils.Move;
import utils.OurEvaluation;
import ai.OurBoard;
import ai.OurPair;



public class AmazonsGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private OurBoard board;
	
	private JPanel contentPane;
	private int tileWidth;
	private int tileCount;
	private JPanel gridPanel;
	private HashMap<Point,myTile> gridTiles;
	private Timer timer;
	private int seconds=0;
	
	public Image blackqueen;
	public Image whitequeen;
	public Image arrow;
	public myTile highlightedInitialQueen;
	public myTile highlightedFinalQueen;

	private JLabel lblTimer;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AmazonsGUI frame = new AmazonsGUI(new OurBoard());
					
					frame.setVisible(true);
					
					System.out.println(OurEvaluation.evaluateBoard(frame.board));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AmazonsGUI(OurBoard b) {
		
		board = b;
		highlightedInitialQueen = null;
		highlightedFinalQueen = null;
		
		setTitle("Game of Amazons");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		gridPanel = new JPanel();
		
		int gridWidth = 400;
		gridPanel.setBounds(40, 40, gridWidth, gridWidth);
		tileCount = 10;
		tileWidth = gridWidth/tileCount;
		contentPane.add(gridPanel);
		gridPanel.setLayout(null);
		
		JLabel lblTurn = new JLabel("White's Turn");
		lblTurn.setBounds(40, 15, 88, 14);
		contentPane.add(lblTurn);
		
		final JLabel lblTimer = new JLabel("0:00");
		lblTimer.setBounds(410, 15, 88, 14);
		contentPane.add(lblTimer);
		
		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				seconds++;
				lblTimer.setText((seconds/60)+":"+String.format("%02d",seconds%60));
				
			}
        }); 
		timer.start();
		
		
		createGrid();
		setImages();
		addQueens();
	}
	
	
	/**
	 * Stores the images to be used for queens and arrows in static variables (load once)
	 */
	private void setImages() {
		try{
			whitequeen = ImageIO.read(this.getClass().getResourceAsStream("/rsz_wq.png"));
			blackqueen = ImageIO.read(this.getClass().getResourceAsStream("/rsz_bq.png"));
			arrow = ImageIO.read(this.getClass().getResourceAsStream("/rsz_arrow.png"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * Creates the gameboard, a 10x10 grid of myTiles
	 */
	private void createGrid(){
		gridTiles = new HashMap<Point,myTile>();
		
		boolean colored = true;
		for(int i = 0;i<tileCount;i++){
			for(int j = 0;j<tileCount;j++){
				myTile grid = new myTile(i*40, j*40, tileWidth, tileWidth,colored, this);
				colored = !colored;
				gridTiles.put(new Point(i,j),grid);
				gridPanel.add(grid);
			}
			colored = !colored;
		}
	}
	
	/**
	 * Adds the queens to the initial gameboard
	 */
	private void addQueens(){
		gridTiles.get(new Point(3,0)).setState(myTile.BQ);
		gridTiles.get(new Point(6,0)).setState(myTile.BQ);
		gridTiles.get(new Point(0,3)).setState(myTile.BQ);
		gridTiles.get(new Point(9,3)).setState(myTile.BQ);
		
		gridTiles.get(new Point(0,6)).setState(myTile.WQ);
		gridTiles.get(new Point(3,9)).setState(myTile.WQ);
		gridTiles.get(new Point(6,9)).setState(myTile.WQ);
		gridTiles.get(new Point(9,6)).setState(myTile.WQ);
	}

	public void moveQueen(myTile sourceTile, myTile targetTile, myTile arrowTile) {
		Move move = new Move(sourceTile.getPosition(), targetTile.getPosition(), arrowTile.getPosition());
		if (board.makeMove(move))
		{
			System.out.println("This is a legal move.");
			targetTile.setState(sourceTile.getState());
			
			sourceTile.setState(0);

			arrowTile.setState(3);
			arrowTile.repaint();
		}
		else
			System.out.println("This is not a legal move.");
		
		
		
	}
	
	/**
	 * check if the tile clicked is a queen and highlight it as such
	 * @param queenTile tile clicked
	 */
	public void queenClick(myTile queenTile)
	{
		
		//queen position not being updated
		if (board.isQueen(queenTile.getPosition().getX(), queenTile.getPosition().getY()))
		{
			highlightedInitialQueen = queenTile;
			highlightedFinalQueen = null;
			queenTile.toggleHighlight();
			System.out.println("This tile became a potential queen to be moved.");
			
		}
		else
		{
			highlightedInitialQueen = null;
			highlightedFinalQueen = null;
		}
	}
	
	public void potentialMoveClick(myTile thisTile)
	{
		//check if the space is empty
		if (board.isFree(thisTile.getPosition().getX(), thisTile.getPosition().getY()))
		{
			highlightedFinalQueen = thisTile;
			thisTile.toggleHighlight();
			
			System.out.println("This tile became a potential place for the queen to move.");
		}
		else
		{
			highlightedFinalQueen = null;
			thisTile.toggleHighlight();
			highlightedInitialQueen = null;
			highlightedInitialQueen.toggleHighlight();
		}
	}
	
	public void potentialArrowClick(myTile thisTile)
	{
		//check if the space is empty
		if (board.isFree(thisTile.getPosition().getX(), thisTile.getPosition().getY()))
		{
			System.out.println("Trying to drop an arrow at this tile.");
			//try move
			moveQueen(highlightedInitialQueen, highlightedFinalQueen, thisTile);
			highlightedInitialQueen.toggleHighlight();
			highlightedInitialQueen = null;
			
			highlightedFinalQueen.toggleHighlight();
			highlightedFinalQueen = null;
			
		}
		else
		{
			highlightedInitialQueen.toggleHighlight();
			highlightedInitialQueen = null;
			
			highlightedFinalQueen.toggleHighlight();
			highlightedFinalQueen = null;
		}
	}
	
	
	
	
}
