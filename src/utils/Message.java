package utils;

import ai.OurPair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by ethan on 09/02/15.
 * <p/>
 * This class uses Jaxb to automatically map incoming
 * XML strings to object mappings and will then
 * convert a mapping back to XML string to be sent
 * on the wire.
 */
public class Message {
    static Action action;
    static JAXBContext jaxbContext;

    /**
     * Constructor
     *
     * @throws JAXBException
     */
    public Message() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(Action.class);
        action = new Action();
    }

    /**
     * @param msg Reads in an XML string from the game server and unmarshalls
     *            it into the object mappings
     * @throws JAXBException
     */

    public static void unmarshal(String msg) throws JAXBException {
        InputStream is = new ByteArrayInputStream(msg.getBytes());
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        action = (Action) unmarshaller.unmarshal(is);
    }

    /**
     * @return String which is the XML formatting for the response message
     * to the server
     * @throws JAXBException
     */
    public static String marshal() throws JAXBException {
        OutputStream os = new ByteArrayOutputStream();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(action, os);

        return os.toString();
    }

    /**
     * Root of the XML string
     */
    @XmlRootElement(name = "action")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Action {
        @XmlAttribute(name = "type")
        static String type = new String();
        @XmlElement(name = "usrlist")
        static UserList userList = new UserList();
        @XmlElement(name = "queen")
        static Queen queen = new Queen();
        @XmlElement(name = "arrow")
        static Arrow arrow = new Arrow();
    }

    public String getType() {
        return Action.type;
    }

    public void setType(String type) {
        Action.type = type;
    }

    /**
     * List of Users Element
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public class UserList {
        @XmlAttribute(name = "ucount")
        int ucount = 0;
        @XmlElement(name = "usr")
        List<User> users;
    }

    public int getUcount() {
        return UserList.ucount;
    }

    public void setUcount(int ucount) {
        UserList.ucount = ucount;
    }

    public List<User> getUsers() {
        return UserList.users;
    }

    public void setUsers(List<User> users) {
        UserList.users = users;
    }

    /**
     * User element and attributs
     * None of the examples in the
     * message format guide include anything
     * except attributes
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class User {
        @XmlAttribute(name = "name")
        static String name;
        @XmlAttribute(name = "id")
        static int id;
        @XmlAttribute(name = "role")
        static String role;
    }

    public String getName() {
        return User.name;
    }

    public void setName(String name) {
        User.name = name;
    }

    public int getId() {
        return User.id;
    }

    public void setId(int id) {
        User.id = id;
    }

    public String getRole() {
        return User.role;
    }

    public void setRole(String role) {
        User.role = role;
    }


    /**
     * Queen, stores a move in a string of the
     * format previous-current such as g7-a4.
     * Is not stored in our internal game board representation
     * See getMove for getter of the newest move
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Queen {
        @XmlAttribute(name = "move")
        static String move;
    }

    /**
     * @param InitialQ Where the queen started in our turn
     * @param FinalQ   Where the queen was moved to in our turn
     */
    public void setMove(OurPair<Integer, Integer> InitialQ, OurPair<Integer, Integer> FinalQ) {
        // Take our internal representation of the game board and transform it into the alphanumeric representation that the server expects
        String s1 = new StringBuilder().append(Character.toChars(InitialQ.getX() + 'a')).append(Character.toChars(InitialQ.getY() + '0')).toString();
        String s2 = new StringBuilder().append(Character.toChars(FinalQ.getX() + 'a')).append(Character.toChars(FinalQ.getY() + '0')).toString();

        Queen.move = s1 + "-" + s2;
    }

    /**
     * @return The location that the queen moved to
     */
    public OurPair<Integer, Integer> getMove() {
        String[] str = Queen.move.split("-");
        // Transform the server-required alphanumeric position representation into our internal representation
        return new OurPair<Integer, Integer>(str[1].charAt(0) - 'a', str[1].charAt(1) - '0');
    }

    /**
     * Stores location that the arrow was fired after
     * the queen made her move
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Arrow {
        @XmlAttribute(name = "move")
        static String arrow;
    }

    /**
     * @param arrow takes OurPair object for location that we placed our arrow
     */
    public void setArrow(OurPair<Integer, Integer> arrow) {
        // Transform our internal arrow position into the external alphanumeric representation
        Arrow.arrow = new StringBuilder().append(Character.toChars(arrow.getX() + 'a')).append(Character.toChars(arrow.getY() + '0')).toString();
    }

    /**
     * @return location that the arrow was placed
     */
    public OurPair<Integer, Integer> getArrow() {
        // Transform the stored alphanumeric representation into our internal representation
        return new OurPair<Integer, Integer>(Arrow.arrow.charAt(0) - 'a', Arrow.arrow.charAt(1) - '0');
    }
}
