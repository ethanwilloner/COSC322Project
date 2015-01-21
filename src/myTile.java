import java.awt.Color;

import javax.swing.JPanel;


public class myTile extends JPanel {
	
	
	private boolean colored;
	
	private int state; //0 for empty, 1 for white queen, 2 for black queen, 3 for arrow
	
	public myTile(int x, int y, int width, int height, boolean colored){
		// super();
		this.setBounds(x, y, width, height);
		this.colored = colored;
		if(colored){
			this.setBackground(Color.BLACK);
		}
		else{
			this.setBackground(Color.DARK_GRAY);
		}
		System.out.println('f');
	}

}
