package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import AmazonBoard.IllegalMoveException;
import AmazonBoard.OurPair;


public class BoardTile extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WQ = 1;
	public static final int BQ = 2;
	public static final int ARROW = 3;
	public static final int EMPTY = 0;
	
	public boolean colored;

	private OurPair position;
	private AmazonsGUI gui;
	
	private boolean highlighted;
	private int state; //0 for empty, 1 for white queen, 2 for black queen, 3 for arrow
	
	public boolean whiteTurn = true; //will keep track of whose turn it is
	
	private BoardTile thisTile = this; //stored for use in addMouse()
	
	
	public BoardTile(int x, int y, int width, int height, boolean colored, AmazonsGUI g){
		super();
		//set gui
		gui = g;
		
		
		this.position = new OurPair(x/40, y/40);
		
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
	
	/**
	 * @return the position
	 */
	public OurPair getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(OurPair position) {
		this.position = position;
	}

	public void addMouse(){
		addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        System.out.println("x:"+position.getX()+" y:"+position.getY());
		        
		        if (gui.highlightedInitialQueen != null)
		        {
		        	System.out.println("Highlighted Initial Queen: " + gui.highlightedInitialQueen.getPosition().getX() + ", " + gui.highlightedInitialQueen.getPosition().getY());
		        }
		        if (gui.highlightedFinalQueen != null)
		        	System.out.println("Highlighted Final Queen: " + gui.highlightedFinalQueen.getPosition().getX() + ", " + gui.highlightedFinalQueen.getPosition().getY());
		        
		        if(gui.highlightedInitialQueen == null)
		        {
		        	gui.queenClick(thisTile);
		        }
		        else
		        {
		        	if (gui.highlightedFinalQueen == null)
		        	{
		        		gui.potentialMoveClick(thisTile);
		        	}
		        	else
                        try {
                            gui.potentialArrowClick(thisTile);
                        } catch (IllegalMoveException e1) {
                            e1.printStackTrace();
                        }
                }
		        
		        	
//		        	 if(gui.highlighted==thisTile){
//				        	gui.highlighted=null;
//				        	thisTile.toggleHighlight();
//				        }
//		        	 else{
//		        		 
//		        		 switch(gui.highlighted.state) {
//		        		 case 0:
//		        			 gui.highlighted.toggleHighlight();
//		        			 gui.highlighted=thisTile;
//			        		 thisTile.toggleHighlight();
//			        		 break;
//		        		 case 1:
//		        			 GUI.AmazonsGUI.moveQueen(gui.highlighted, thisTile);
//		        			 repaint();
//		        			 break;
//		        		 case 2:
//		        			 GUI.AmazonsGUI.moveQueen(gui.highlighted, thisTile);
//		        			 repaint();
//		        			 break;
//		        		 case 3:
//		        			 break;
//		        		 default:
//		        			 System.out.println("ERROR: Something went wrong...");
//		        		 }
//		        		 
//		        	 }
//		        	
//		        }
//		        else
//		        {
//		        	//un-highlight this tile if it was clicked before and got clicked again
//		        	if (gui.highlighted == thisTile)
//		        	{
//		        		gui.highlighted=null;
//			        	thisTile.toggleHighlight();
//		        	}
//		        	//check if the second tile clicked is a legal move
//		        	GameMove move = new GameMove();
//		        }
		       
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
			return gui.whitequeen;
		case 2:
			
			return gui.blackqueen;
			
		case 3:
			
			return gui.arrow;
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
		repaint();
	}

	public int getState() {
		return state;
	}
}
