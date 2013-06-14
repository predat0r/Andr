package person.tarasenko.nasarss;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IotdHandler extends DefaultHandler {

    private String url = "http://www.nasa.gov/rss/image_of_the_day.rss";
    private boolean inUrl = false;
    private boolean inTitle = false;
    private boolean inDescription = false;
    private boolean inItem = false;
    private boolean inDate = false;
    private Bitmap image = null;
    private String title = null;
    private String date = null;
	private StringBuilder description = new StringBuilder();

    public boolean processFeed() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser saxParser = spf.newSAXParser();
            XMLReader reader = saxParser.getXMLReader();//XMLReaderFactory.createXMLReader();
            reader.setContentHandler(this);
            InputStream inputStream = new URL(url).openStream();
            url = null;
            reader.parse(new InputSource(inputStream));
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("=== proxessFeed fail ===");
            Log.i("DEBUG", "URL = " + url);
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException(e);
            //return false;
        }
    }

    private Bitmap getBitmap(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
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
            throw new RuntimeException(e);
        }

    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // try without this line

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
                if (localName.equals("enclosure")) {
                    url = attributes.getValue("url");
                    image = getBitmap(url);
                }

            }
        }
    }

    public void characters(char ch[], int start, int length) {
		String chars = new String(ch).substring(start, start + length);

		if (inUrl && url == null ){ image = getBitmap(chars);}
		if (inTitle && title == null ){ title = chars;}
        if (inDescription) { description.append(chars); }
        if (inDate && date == null) { date = chars; }


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

    public StringBuilder getDescription() {
        return description;
    }
}
