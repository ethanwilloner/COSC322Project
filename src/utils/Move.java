package utils;

import ai.OurPair;

/**
 * A move datatype
 * @author Yarko Senyuta
 *
 */
public class Move 
{
	OurPair<Integer, Integer> initialQ;
	OurPair<Integer, Integer> FinalQ;
	OurPair<Integer, Integer> arrow;
	/**
	 * @return the initialQ
	 */
	public OurPair<Integer, Integer> getInitialQ() {
		return initialQ;
	}
	/**
	 * @param initialQ the initialQ to set
	 */
	public void setInitialQ(OurPair<Integer, Integer> initialQ) {
		this.initialQ = initialQ;
	}
	/**
	 * @return the finalQ
	 */
	public OurPair<Integer, Integer> getFinalQ() {
		return FinalQ;
	}
	/**
	 * @param finalQ the finalQ to set
	 */
	public void setFinalQ(OurPair<Integer, Integer> finalQ) {
		FinalQ = finalQ;
	}
	/**
	 * @return the arrow
	 */
	public OurPair<Integer, Integer> getArrow() {
		return arrow;
	}
	/**
	 * @param arrow the arrow to set
	 */
	public void setArrow(OurPair<Integer, Integer> arrow) {
		this.arrow = arrow;
	}
	public Move(OurPair<Integer, Integer> initialQ,
			OurPair<Integer, Integer> finalQ, OurPair<Integer, Integer> arrow) {
		super();
		this.initialQ = initialQ;
		FinalQ = finalQ;
		this.arrow = arrow;
	}
	
	
	
	

}
