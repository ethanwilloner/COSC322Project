package utils;

import Evaluations.OurEvaluation;
import ai.OurBoard;
import ai.OurPair;

/**
 * A move datatype
 * @author Yarko Senyuta
 *
 */
public class Move 
{
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((FinalQ == null) ? 0 : FinalQ.hashCode());
		result = prime * result + ((arrow == null) ? 0 : arrow.hashCode());
		result = prime * result
				+ ((initialQ == null) ? 0 : initialQ.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Move [initialQ=" + initialQ + ", FinalQ=" + FinalQ + ", arrow="
				+ arrow + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		if (FinalQ == null) {
			if (other.FinalQ != null)
				return false;
		} else if (!FinalQ.equals(other.FinalQ))
			return false;
		if (arrow == null) {
			if (other.arrow != null)
				return false;
		} else if (!arrow.equals(other.arrow))
			return false;
		if (initialQ == null) {
			if (other.initialQ != null)
				return false;
		} else if (!initialQ.equals(other.initialQ))
			return false;
		return true;
	}
	OurPair initialQ;
	OurPair FinalQ;
	OurPair arrow;
	/**
	 * @return the initialQ
	 */
	public OurPair getInitialQ() {
		return initialQ;
	}
	/**
	 * @param initialQ the initialQ to set
	 */
	public void setInitialQ(OurPair initialQ) {
		this.initialQ = initialQ;
	}
	/**
	 * @return the finalQ
	 */
	public OurPair getFinalQ() {
		return FinalQ;
	}
	/**
	 * @param finalQ the finalQ to set
	 */
	public void setFinalQ(OurPair finalQ) {
		FinalQ = finalQ;
	}
	/**
	 * @return the arrow
	 */
	public OurPair getArrow() {
		return arrow;
	}
	/**
	 * @param arrow the arrow to set
	 */
	public void setArrow(OurPair arrow) {
		this.arrow = arrow;
	}
	public Move(OurPair initialQ,
			OurPair finalQ, OurPair arrow) {
		super();
		this.initialQ = initialQ;
		FinalQ = finalQ;
		this.arrow = arrow;
	}
	
	public void moveInfo(OurBoard ourBoard)
    {
        System.out.println("move made: " + this);
        
        //print
        OurEvaluation.evaluateBoard(ourBoard, 1, true);
        
        System.out.println("Current evaluation: "+ OurEvaluation.evaluateBoard(ourBoard, 1, false)[0] + "\t" + OurEvaluation.evaluateBoard(ourBoard, 1, false)[1]);
        System.out.println(ourBoard);
    }
	
	

}
