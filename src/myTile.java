import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class myTile extends JPanel {
	
	
	public boolean colored;
	public int x; //x coordinate
	public int y; //y coordinate
	
	private boolean highlighted;
	private int state; //0 for empty, 1 for white queen, 2 for black queen, 3 for arrow
	
	private ImageIcon blackqueen;
	private ImageIcon whitequeen;
	private myTile thisTile = this;
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
		        	AmazonsGUI.highlighted.toggleHighlight();
		        	 
			        if(AmazonsGUI.highlighted==thisTile){
			        	AmazonsGUI.highlighted=null;
			        }	
		        }
		        else{
		        	
		        	AmazonsGUI.highlighted=thisTile;
		        }
		       
		        else{
		        	AmazonsGUI.highlighted=thisTile;
		        }
		        thisTile.toggleHighlight();
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
			this.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
		}
		else{
			this.setBorder(null);
		}
		highlighted = !highlighted;
	}
}
