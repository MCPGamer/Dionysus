/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.duartemendes.dionysus.model;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Service
public class MediaService {
	@Autowired
	private MediaRepository repository;
    private ArrayList<Media> mediaList;
    
    private Document document;

    public MediaService() {
        this.mediaList = new ArrayList<>();
    }

    public void addMedia(Media media){
    	//repository.save(media);
        this.mediaList.add(media);
    }

    public ArrayList<Media> getMediaList() {
        return mediaList;
    }
    
    public void fillMediaFromDB() {
    	mediaList = (ArrayList<Media>) repository.findAll();
    }
    
    public Media getMedia(long id) {
    	return repository.findById(id).get();
    }
    
    //		String filePath = this.getClass().getResource("adressliste.xml").getPath();
    public void importFromXml(String file) {
    	try {
			// Use sun's default implementation
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new File(file));
		} catch (Exception e) {
			System.out.println("ERROR:");
			System.out.println(e.getMessage());
		}
    	
    	
    	ArrayList<Media> medias = new ArrayList<>();
    	ArrayList<Media> mediasToAdd = new ArrayList<>();
		NodeList mediaList = document.getElementsByTagName("media");
		for (int i = 0; i < mediaList.getLength(); i++) {
			Media media = new Media();
			Element elem = (Element) mediaList.item(i);
			media.setApiId(Integer.parseInt(getValueOfSingleChild(elem, "idDb")));
			media.setTitle(getValueOfSingleChild(elem, "title"));
			media.setType(MediaType.lookupByName(getValueOfSingleChild(elem, "type")));
			medias.add(media);
		}
		
		
		for(Media media : medias) {
			boolean isNew = true;
			for(Media dbMedia : this.mediaList) {
				if(dbMedia.getApiId() == media.getApiId()) {
					isNew = false;
				}
			}
			
			if(isNew) {
				mediasToAdd.add(media);
			}
		}
		
		for(Media m : medias) {
			addMedia(m);
		}
    }
    
    public String getValueOfSingleChild(Element elem, String tagName) {
		NodeList nodeList = elem.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node.getNodeName().equalsIgnoreCase(tagName)) {
				return node.getTextContent();
			}
		}
		return "";
	}
    
    public String exportToXml() {
    	return "";
    }
}
