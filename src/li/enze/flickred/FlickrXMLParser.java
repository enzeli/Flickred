package li.enze.flickred;

import li.enze.flickred.Photo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.text.Html;

import java.io.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FlickrXMLParser {
	// parse an XML string to an arraylist of photo objects
	public static ArrayList<Photo> parse(String xmlStr) {
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
		    builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
		    e.printStackTrace();  
		}
		
		Document document = null;
		try {
		    document = builder.parse(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
		} catch (SAXException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		Element rspElement = document.getDocumentElement();
		return  getPhotosFromRSP(rspElement);
		
	}
	
	
	private static ArrayList<Photo> getPhotosFromRSP(Element rspElement){
		ArrayList<Photo> photos = new ArrayList<Photo>();
		if (rspElement.getAttribute("stat").equalsIgnoreCase("ok")) {
			Element photosElement = (Element) rspElement.getChildNodes().item(0);
			if (photosElement.getNodeName().equalsIgnoreCase("photos")) {
				NodeList photoNodes = photosElement.getChildNodes();
				for(int i=0; i<photoNodes.getLength(); i++){
					Node node = photoNodes.item(i);
					if(node.getNodeName().equalsIgnoreCase("photo")){
						//a child element to process
						Element photoElement = (Element) node;
						Photo newPhoto = createPhotoFromElement(photoElement);
						photos.add(newPhoto);
					}
				}		
			}
		}
		return photos;
	}
	
	private static Photo createPhotoFromElement(Element photo){
		if (photo.getNodeName().equalsIgnoreCase("photo")) {
			String id = photo.getAttribute("id");
			String owner = photo.getAttribute("owner");
			String secret = photo.getAttribute("secret");
			String server = photo.getAttribute("server");
			String farm = photo.getAttribute("farm");
			
			String title = photo.getAttribute("title");
			// truncate long titles
			if (title.length() > 60){
				title = title.substring(0, 60) + "...";
			}
			
			String datetaken = photo.getAttribute("datetaken");
			String ownername = photo.getAttribute("ownername");
			
			String description = "";
			if (photo.hasChildNodes()){
				Node desNode = photo.getChildNodes().item(1);
				if (desNode.getNodeName().equalsIgnoreCase("description")){
					String desHtml = desNode.getTextContent();
					description = Html.fromHtml(desHtml).toString();
					description = description.length() > 140 ?
							description.substring(0, 140) + "..." : description;
				}
			}
			return new Photo(id, owner, secret, server, farm, 
					title, datetaken, ownername, description);
		}
		return null;
	}
	
	

}
