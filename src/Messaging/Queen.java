package Messaging;

import AmazonBoard.OurPair;

import javax.xml.bind.annotation.*;

/**
 * Queen, stores a move in a string of the
 * format previous-current such as g7-a4.
 * Is not stored in our internal game board representation
 * See getGameMove for getter of the newest move
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Queen {
    @XmlAttribute(name = "move")
    public String move;

    @XmlValue
    String content = "";

    public Queen(){};

    public Queen(OurPair InitialQ, OurPair FinalQ)
    {
        // Take our internal representation of the game board and transform it into the alphanumeric representation that the server expects
        String s1 = new StringBuilder().append(Character.toChars(InitialQ.getX() + 'a')).append(Integer.toString(InitialQ.getY())).toString();
        String s2 = new StringBuilder().append(Character.toChars(FinalQ.getX() + 'a')).append(Integer.toString(FinalQ.getY())).toString();
        this.move = s1 + "-" + s2;
    }

    /**
     * @param InitialQ Where the queen started in our turn
     * @param FinalQ   Where the queen was moved to in our turn
     */
    public void setMove(OurPair InitialQ, OurPair FinalQ) {
        // Take our internal representation of the game board and transform it into the alphanumeric representation that the server expects
        String s1 = new StringBuilder().append(Character.toChars(InitialQ.getY() + 'a')).append(Integer.toString(InitialQ.getX())).toString();
        String s2 = new StringBuilder().append(Character.toChars(FinalQ.getY() + 'a')).append(Integer.toString(FinalQ.getX())).toString();

        this.move = s1 + "-" + s2;
    }

    /**
     * @return The location that the queen moved to
     */

    public OurPair getInitialQ()
    {
        String[] str = this.move.toLowerCase().split("-");

        OurPair initialQ = new OurPair(str[0].charAt(0) - 'a', str[0].charAt(1) - '0');
        return initialQ;
    }

    public OurPair getFinalQ()
    {
        String[] str = this.move.toLowerCase().split("-");
        OurPair finalQ = new OurPair(str[1].charAt(0) - 'a', str[1].charAt(1) - '0');
        return finalQ;
    }

    public OurPair getMove() {
        String[] str = this.move.toLowerCase().split("-");
        // Transform the server-required alphanumeric position representation into our internal representation
        return new OurPair(str[1].charAt(0) - 'a', str[1].charAt(1) - '0');
    }

    public void setMove(String move) {
        this.move = move.toLowerCase();
    }

    public String getMove(String move) {
        return this.move.toLowerCase();
    }
}

