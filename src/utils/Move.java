package utils;

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
