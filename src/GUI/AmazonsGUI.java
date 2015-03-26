package GUI;

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

import AmazonBoard.GameMove;
import AmazonBoard.GameBoard;



public class AmazonsGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private JPanel contentPane;
	private int tileWidth;
	private int tileCount;
	private JPanel gridPanel;
	private static HashMap<Point,BoardTile> gridTiles;
	private Timer timer;
	private int seconds=0;
	private boolean white = true;
	
	public Image blackqueen;
	public Image whitequeen;
	public Image arrow;
	public BoardTile highlightedInitialQueen;
	public BoardTile highlightedFinalQueen;
	private JLabel lblTurn;
	private JLabel lblTimer;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AmazonsGUI frame = new AmazonsGUI();
					
					frame.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AmazonsGUI() {
		
		//board = b;
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
		
		lblTurn = new JLabel("White's Turn");
		lblTurn.setBounds(40, 15, 88, 14);
		contentPane.add(lblTurn);
		
		final JLabel lblTimer = new JLabel("0:00");
		lblTimer.setBounds(410, 15, 88, 14);
		contentPane.add(lblTimer);
		
//		timer = new Timer(1000, new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				seconds++;
//				lblTimer.setText((seconds/60)+":"+String.format("%02d",seconds%60));
//				
//			}
//        }); 
//		timer.start();
		
		createGrid();
		setImages();
		addQueens();
	}
	
	
	/**
	 * Stores the images to be used for queens and arrows in static variables (load once)
	 */
	private void setImages() {
		try{
			whitequeen = ImageIO.read(this.getClass().getResourceAsStream("/GUI/rsz_wq.png"));
			blackqueen = ImageIO.read(this.getClass().getResourceAsStream("/GUI/rsz_bq.png"));
			arrow = ImageIO.read(this.getClass().getResourceAsStream("/GUI/rsz_arrow.png"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * Creates the gameboard, a 10x10 grid of BoardTiles
	 */
	private void createGrid(){
		gridTiles = new HashMap<Point,BoardTile>();
		
		boolean colored = true;
		for(int i = 0;i<tileCount;i++){
			for(int j = 0;j<tileCount;j++){
				BoardTile grid = new BoardTile(i*40, j*40, tileWidth, tileWidth,colored, this);
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
		gridTiles.get(new Point(3,0)).setState(BoardTile.BQ);
		gridTiles.get(new Point(6,0)).setState(BoardTile.BQ);
		gridTiles.get(new Point(0,3)).setState(BoardTile.BQ);
		gridTiles.get(new Point(9,3)).setState(BoardTile.BQ);
		
		gridTiles.get(new Point(0,6)).setState(BoardTile.WQ);
		gridTiles.get(new Point(3,9)).setState(BoardTile.WQ);
		gridTiles.get(new Point(6,9)).setState(BoardTile.WQ);
		gridTiles.get(new Point(9,6)).setState(BoardTile.WQ);
	}

	public void moveQueen(BoardTile sourceTile, BoardTile targetTile, BoardTile arrowTile) {
		GameMove move = new GameMove(sourceTile.getPosition(), targetTile.getPosition(), arrowTile.getPosition());
		
		
		
	}
	
	/**
	 * check if the tile clicked is a queen and highlight it as such
	 * @param queenTile tile clicked
	 */
	public void makeMove(int queen_x_old, int queen_y_old, int queen_x_new, int queen_y_new, int arrow_x, int arrow_y)
	{
		//queen_x_old=9-queen_x_old;
		queen_y_old=9-queen_y_old;
		//queen_x_new=9-queen_x_new;
		queen_y_new=9-queen_y_new;
		//arrow_x=9-arrow_x;
		arrow_y=9-arrow_y;
		//remove the old queen
		gridTiles.get(new Point(queen_x_old,queen_y_old)).setState(BoardTile.EMPTY);
		
		//get the color of the queen
		int queen = BoardTile.WQ;
		if(!white)
			queen = BoardTile.BQ;
		
		updateTurnText(white);
		white = !white;
		//set the queen
		gridTiles.get(new Point(queen_x_new,queen_y_new)).setState(queen);
		
		//set the arrow
		gridTiles.get(new Point(arrow_x,arrow_y)).setState(BoardTile.ARROW);
		
		this.repaint();
	}

	//updates the text stating who's turn it is
	private void updateTurnText(boolean white2) {
		// TODO Auto-generated method stub
		String player = "White's";
		if(white2)
			player = "Black's";
		lblTurn.setText(player+" turn");
	}
	
	
	
	
	
	
}
