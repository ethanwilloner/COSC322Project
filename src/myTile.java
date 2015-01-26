import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class myTile extends JPanel {
	
	public static final int WQ = 1;
	public static final int BQ = 2;
	public static final int ARROW = 3;
	public static final int EMPTY = 0;
	
	public boolean colored;
	public int x; //x coordinate
	public int y; //y coordinate
	
	private boolean highlighted;
	private int state; //0 for empty, 1 for white queen, 2 for black queen, 3 for arrow
	
	public boolean whiteTurn = true; //will keep track of whose turn it is
	
	private myTile thisTile = this; //stored for use in addMouse()
	
	
	public myTile(int x, int y, int width, int height, boolean colored){
		super();
		this.x = x/40;
		this.y = y/40;
		this.highlighted=false;
		this.setBounds(x, y, width, height);
		this.colored = colored;
		if(colored){
			this.setBackground(Color.BLACK);
		}
		else{
			this.setBackground(Color.DARK_GRAY);
		}
		addMouse();
	}
	
	public void addMouse(){
		addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        System.out.println("x:"+x+" y:"+y);
		        
		        if(AmazonsGUI.highlighted!=null){
		        	 if(AmazonsGUI.highlighted==thisTile){
				        	AmazonsGUI.highlighted=null;
				        	thisTile.toggleHighlight();
				        }
		        	 else{
		        		 
		        		 switch(AmazonsGUI.highlighted.state) {
		        		 case 0:
		        			 AmazonsGUI.highlighted.toggleHighlight();
			        		 AmazonsGUI.highlighted=thisTile;
			        		 thisTile.toggleHighlight();
			        		 break;
		        		 case 1:
		        			 AmazonsGUI.moveQueen(AmazonsGUI.highlighted, thisTile);
		        			 repaint();
		        			 break;
		        		 case 2:
		        			 AmazonsGUI.moveQueen(AmazonsGUI.highlighted, thisTile);
		        			 repaint();
		        			 break;
		        		 case 3:
		        			 break;
		        		 default:
		        			 System.out.println("ERROR: Something went wrong...");
		        		 }
		        		 
		        	 }
		        	
			       
		        }
		        else{
		        	
		        	AmazonsGUI.highlighted=thisTile;
		        	thisTile.toggleHighlight();
		        }
		       
		    }
		        
		    
		});
	}
	
	@Override
	protected void paintComponent(Graphics g) {

	    super.paintComponent(g);
	        g.drawImage(getImage(), 0, 0, null);
	}
	
	private Image getImage(){
		
		switch(state){
		case 0:
			return null;
		case 1:
			return AmazonsGUI.whitequeen;
		case 2:
			
			return AmazonsGUI.blackqueen;
			
		case 3:
			
			return AmazonsGUI.arrow;
		default:
			
			return null;
		}
		
		
		
	}

	public void toggleHighlight(){
		if(!highlighted){
			this.setBorder(BorderFactory.createLineBorder(Color.PINK, 3));
		}
		else{
			this.setBorder(null);
		}
		highlighted = !highlighted;
	}
	
	public void setState(int newState){
		state = newState;
	}

	public int getState() {
		return state;
	}
}
