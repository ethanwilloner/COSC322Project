package minimax;

import utils.Move;

public class minimaxNode
{
	int value;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "minimaxNode [value=" + value + ", move=" + move + "]";
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	/**
	 * @return the move
	 */
	public Move getMove() {
		return move;
	}
	/**
	 * @param move the move to set
	 */
	public void setMove(Move move) {
		this.move = move;
	}
	Move move;
	
	public minimaxNode(int value, Move move) {
		super();
		this.value = value;
		this.move = move;
	}
	
	public void max (minimaxNode other)
	{
		if (other.getValue() > this.value && other.move!=null)
		{
			this.setValue(other.getValue());
			this.setMove(other.getMove());
		}
	}
}