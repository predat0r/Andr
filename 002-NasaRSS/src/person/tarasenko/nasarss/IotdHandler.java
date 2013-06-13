package person.tarasenko.nasarss;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.database.sqlite.SQLiteDoneException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class IotdHandler extends DefaultHandler {
	/**
	 * TODO: Change null to URL http://www.nasa.gov/rss/image_of_the_day.rss
	 */
	private String url = "http://www.nasa.gov/rss/image_of_the_day.rss";
	private boolean inUrl = false;
	private boolean inTitle = false;
	private boolean inDescription = false;
	private boolean inItem = false;
	private boolean inDate = false;
	private Bitmap image = null;
	private String title = null;
	private String date = null;
	private StringBuffer description = new StringBuffer();

	public boolean processFeed() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
		    spf.setNamespaceAware(true);
		    SAXParser saxParser = spf.newSAXParser();
			XMLReader reader = saxParser.getXMLReader();//XMLReaderFactory.createXMLReader();
			reader.setContentHandler(this);
            InputStream inputStream = new URL(url).openStream();
            reader.parse(new InputSource(inputStream));
			System.out.println("ProcessFeed done");
            return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("=== proxessFeed fail ===");
			Log.i("DEBUG", "URL = " + url);
			throw new RuntimeException(e);
            //return false;
		}
    }

	private Bitmap getBitmap(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			// TODO: InputStream input = connection.getInputStream();
			InputStream input = (InputStream) connection.getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			input.close();
			return bitmap;
		} catch (Exception e) {
			System.out.println("=== getBitmap fail ===");
			// TODO: catch exception.
			return null;
		}

	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals("url")) {
			inUrl = true;
		} else {
			inUrl = false;
		} // try without this line

		if (localName.startsWith("item")) {
			inItem = true;
		} else {
			if (inItem) {
				if (localName.equals("title")) {
					inTitle = true;
				} else {
					inTitle = false;
				}
				if (localName.equals("description")) {
					inDescription = true;
				} else {
					inDescription = false;
				}
				if (localName.equals("pubDate")) {
					inDate = true;
				} else {
					inDate = false;
				}

			}
		}
	}
	
	public void characters(char ch[], int start, int length) {
		String chars = new String(ch).substring(start, start+length);
		if (inUrl && url == null ){ image = getBitmap(chars);}
		if (inTitle && title ==null ){ title = chars;}
		if (inDescription && description == null ) { description.append(chars);}
		if (inDate && date == null){ date = chars;}
		
	}

	public Bitmap getImage() {
		return image;
	}

	public String getTitle() {
		return title;
	}

	public String getDate() {
		return date;
	}

	public StringBuffer getDescription() {
		return description;
	}
}
