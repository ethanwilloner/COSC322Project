package MessageParsing;

import ai.OurPair;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Stores location that the arrow was fired after
 * the queen made her move
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Arrow {
    @XmlAttribute(name = "move")
    public String arrow;

    @XmlValue
    String content = "";

    /**
     * @param arrow takes OurPair object for location that we placed our arrow
     */
    public void setArrow(OurPair arrow) {
        // Transform our internal arrow position into the external alphanumeric representation
        this.arrow = new StringBuilder().append(Character.toChars(arrow.getX() + 'a')).append(Character.toChars(arrow.getY() + '0')).toString();
    }

    /**
     * @return location that the arrow was placed
     */
    public OurPair getArrow() {
        // Transform the stored alphanumeric representation into our internal representation
        return new OurPair(this.arrow.charAt(0) - 'a', this.arrow.charAt(1) - '0');
    }

    public void setArrow(String arrow) {
        this.arrow = arrow;
    }

    public String getArrow(String arrow) {
        return this.arrow;
    }
}





