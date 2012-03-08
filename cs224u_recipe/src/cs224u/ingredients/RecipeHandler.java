package cs224u.ingredients;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.*;

public class RecipeHandler extends DefaultHandler{
	
	// Local object for collecting data as we read through data
	private Recipe recipe = new Recipe();
	private CharArrayWriter contents = new CharArrayWriter();
	
	public void startDocument( ) throws SAXException {
		System.out.println( "SAX Event: START DOCUMENT" );
	}

	public void endDocument( ) throws SAXException {
		System.out.println( "SAX Event: END DOCUMENT" );
	}

	public void startElement( String namespaceURI, String localName, String qName, Attributes attr ) throws SAXException {
		contents.reset();
	}

	public void endElement( String namespaceURI, String localName, String qName ) throws SAXException {
		if (localName.equals("page")) {
			
		}
			
	}

	public void characters( char[] ch, int start, int length ) throws SAXException {

		System.out.print( "SAX Event: CHARACTERS[ " );

		try {
			OutputStreamWriter outw = new OutputStreamWriter(System.out);
			outw.write( ch, start,length );
			outw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println( " ]" );

	}


	public static void main( String[] argv ){

		System.out.println( "RecipeHandler SAX Events:" );
		try {

			// Create SAX 2 parser...
			XMLReader xr = XMLReaderFactory.createXMLReader();

			// Set the ContentHandler...
			xr.setContentHandler( new RecipeHandler() );

			// Parse the file...
			xr.parse( new InputSource(
					new FileReader( "WikibooksCookbookComplete-20120207011907.xml" )) );

		}catch ( Exception e ) {
			e.printStackTrace();
		}

	}

}
