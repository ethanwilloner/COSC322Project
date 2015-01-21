package ai;



/**
 * pair class for representing locations
 * @author Yarko Senyuta
 *
 * @param <L>
 * @param <R>
 */
public class OurPair<L, R>{

	L left;
	/**
	 * @param l the left to set
	 */
	public void setLeft(L l) {
		this.left = l;
	}
	/**
	 * @param r the right to set
	 */
	public void setRight(R r) {
		this.right = r;
	}


	R right;
	
	public OurPair(L l, R r){
		this.left = l;
		this.right = r;
	}
	/**
	 * Left pair, for this it is the x value
	 * @return X-coordinate
	 */
	public L getLeft(){
		return left;
	}

	/**
	 * Right pair, for this it is the y value
	 * @return Y-coordinate
	 */
	public R getRight(){
		return right;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
	
}
