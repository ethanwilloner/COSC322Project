package MessageParsing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * User element and attributs
 * None of the examples in the
 * message format guide include anything
 * except attributes
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
    @XmlAttribute(name = "name")
    public String name;
    @XmlAttribute(name = "id")
    public int id;
    @XmlAttribute(name = "role")
    public String role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}