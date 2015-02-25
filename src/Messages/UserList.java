package Messages;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

/**
 * List of Users Element
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserList {
    @XmlAttribute(name = "ucount")
    public int ucount;
    @XmlElement(name = "usr")
    public ArrayList<User> users;

    public int getUcount() {
        return ucount;
    }

    public void setUcount(int ucount) {
        this.ucount = ucount;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}