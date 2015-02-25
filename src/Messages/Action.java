package Messages;

import javax.xml.bind.annotation.*;

/**
 * Root of the XML string
 */
@XmlRootElement(name = "action")
@XmlAccessorType(XmlAccessType.FIELD)
public class Action {
    @XmlAttribute(name = "type")
    public String type;
    @XmlElement(name = "usrlist")
    public UserList userList;
    @XmlElement(name = "queen")
    public Queen queen;
    @XmlElement(name = "arrow")
    public Arrow arrow;

    public Action(){}

    public Action(String type) { this.type = type; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserList getUserList() {
        return userList;
    }

    public void setUserList(UserList userList) {
        this.userList = userList;
    }

    public Queen getQueen() {
        return queen;
    }

    public void setQueen(Queen queen) {
        this.queen = queen;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public void setArrow(Arrow arrow) {
        this.arrow = arrow;
    }
}