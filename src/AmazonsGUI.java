import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class AmazonsGUI extends JFrame {

	private JPanel contentPane;
	private int tileWidth;
	private int tileCount;
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
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.GREEN);
		panel_1.setBounds(42, 42, 62, 52);
		contentPane.add(panel_1);
		
		JPanel panel = new JPanel();
		panel.setBounds(40, 40, 400, 400);
		tileWidth = panel.WIDTH/10;
		tileCount = 10;
		//contentPane.add(panel);
		createGrid();
	}
	
	private void createGrid(){
		boolean colored = true;
		for(int i = 0;i<tileCount;i++){
			for(int j = 0;j<tileCount;j++){
				JPanel grid = new JPanel();
				grid.setBounds(40+i*40, 40+j*40, tileWidth, tileWidth);
				if(colored){
					
					grid.setBackground(Color.black);
				}
				else{
					grid.setBackground(Color.BLUE);
				}
				colored = !colored;
				contentPane.add(grid);
			}
		}
	}
}
