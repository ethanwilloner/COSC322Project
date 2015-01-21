import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class AmazonsGUI extends JFrame {

	private JPanel contentPane;
	private int tileWidth;
	private int tileCount;
	private JPanel gridPanel;
	
	public static Image blackqueen;
	public static Image whitequeen;
	public static Image arrow;
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
		createGrid();
		
		setImages();
	}
	
	private void setImages() {
		try{
			whitequeen = ImageIO.read(this.getClass().getResourceAsStream("/rsz_wq.png"));
			blackqueen = ImageIO.read(this.getClass().getResourceAsStream("/rsz_bq.png"));
			arrow = ImageIO.read(this.getClass().getResourceAsStream("/rsz_arrow.jpg"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void createGrid(){
		boolean colored = true;
		for(int i = 0;i<tileCount;i++){
			for(int j = 0;j<tileCount;j++){
				myTile grid = new myTile(i*40, j*40, tileWidth, tileWidth,colored);
				colored = !colored;
				gridPanel.add(grid);
			}
			colored = !colored;
		}
	}
}
