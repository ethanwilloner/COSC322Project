package ai;



/**
 * pair class for representing locations
 * @author Yarko Senyuta
 *
 * @param <L>
 * @param <R>
 */
public class OurPair<L, R>{

	L x;
	@Override
	public String toString() {
		return "OurPair [x=" + x + ", y=" + y + "]";
	}
	/**
	 * @param l the left to set
	 */
	public void setX(L l) {
		this.x = l;
	}
	/**
	 * @param r the right to set
	 */
	public void setY(R r) {
		this.y = r;
	}


	R y;
	
	public OurPair(L l, R r){
		this.x = l;
		this.y = r;
	}
	/**
	 * Left pair, for this it is the x value
	 * @return X-coordinate
	 */
	public L getX(){
		return x;
	}

	/**
	 * Right pair, for this it is the y value
	 * @return Y-coordinate
	 */
	public R getY(){
		return y;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
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
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}
	
}
