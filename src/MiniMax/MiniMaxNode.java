package MiniMax;

import AmazonBoard.GameMove;

public class MiniMaxNode {
    int value;
    GameMove gameMove;

    public MiniMaxNode(int value, GameMove gameMove) {
        super();
        this.value = value;
        this.gameMove = gameMove;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "minimaxNode [value=" + value + ", gameMove=" + gameMove + "]";
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
     * @return the gameMove
     */
    public GameMove getGameMove() {
        return gameMove;
    }

    /**
     * @param gameMove the gameMove to set
     */
    public void setGameMove(GameMove gameMove) {
        this.gameMove = gameMove;
    }

    public void max(MiniMaxNode other) {
        if (other.getValue() > this.value && other.gameMove != null) {
            this.setValue(other.getValue());
            this.setGameMove(other.getGameMove());
        }
    }
}