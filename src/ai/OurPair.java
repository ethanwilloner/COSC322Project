package ai;



/**
 * pair class for representing locations
 * @author Yarko Senyuta
 *
 * @param <L>
 * @param <R>
 */
public class OurPair{

	int x;
	@Override
	public String toString() {
		return "OurPair [x=" + x + ", y=" + y + "]";
	}
	/**
	 * @param l the left to set
	 */
	public void setX(int l) {
		this.x = l;
	}
	/**
	 * @param r the right to set
	 */
	public void setY(int r) {
		this.y = r;
	}


	int y;
	
	public OurPair(int l, int r){
		this.x = l;
		this.y = r;
	}
	/**
	 * Left pair, for this it is the x value
	 * @return X-coordinate
	 */
	public int getX(){
		return x;
	}

	/**
	 * Right pair, for this it is the y value
	 * @return Y-coordinate
	 */
	public int getY(){
		return y;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		OurPair other = (OurPair) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	


}