import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;



public class AmazonsGUI extends JFrame {

	private JPanel contentPane;
	private int tileWidth;
	private int tileCount;
	private JPanel gridPanel;
	private HashMap<Point,myTile> gridTiles;
	private Timer timer;
	private int seconds=0;
	
	public static Image blackqueen;
	public static Image whitequeen;
	public static Image arrow;
	public static myTile highlighted;
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
				myTile grid = new myTile(i*40, j*40, tileWidth, tileWidth,colored);
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
	
	
	
}
