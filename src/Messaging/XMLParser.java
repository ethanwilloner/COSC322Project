package Messaging;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class XMLParser {

    JAXBContext jaxbContext;

    public XMLParser() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(Action.class);
    }

    /**
     * @param msg Reads in an XML string from the game server and unmarshalls
     *            it into the object mappings
     * @throws javax.xml.bind.JAXBException
     */
    public Action unmarshal(String msg) throws JAXBException {
        InputStream is = new ByteArrayInputStream(msg.getBytes(Charset.defaultCharset()));
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Action action = (Action) unmarshaller.unmarshal(is);
        return action;
    }

    /**
     * @return String which is the XML formatting for the response message
     * to the server
     * @throws JAXBException
     */
    public String marshal(Action action) throws JAXBException, UnsupportedEncodingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(action, os);
        System.out.println("Marshalled XML Message: " + os.toString(Charset.defaultCharset().toString()));
        return os.toString(Charset.defaultCharset().toString());
    }

}
