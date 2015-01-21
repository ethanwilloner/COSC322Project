import java.io.FileNotFoundException;
import java.io.IOException;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;
import net.n3.nanoxml.XMLWriter;


public class XMLParser {
	
		//use nanoxml to parse the server message
		//this code found at http://nanoxml.sourceforge.net/orig/NanoXML-Java/retrieving.html
		public static String parseXML(String xmlString) throws FileNotFoundException, IOException, XMLException
		{
			String str = "";
			IXMLParser parser;
			try 
			{
				parser = XMLParserFactory.createDefaultXMLParser();
				
			    IXMLReader reader = StdXMLReader.stringReader(xmlString);
			    parser.setReader(reader);
			    IXMLElement xml = (IXMLElement) parser.parse();
			    XMLWriter writer = new XMLWriter(System.out);
			    writer.write(xml);
			    
			}
		    catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			return str;
		}
		
		

}
