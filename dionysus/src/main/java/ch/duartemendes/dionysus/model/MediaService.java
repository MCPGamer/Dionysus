/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.duartemendes.dionysus.model;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
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

	public void addMedia(Media media) {
		boolean isNew = true;
		for (Media dbMedia : this.mediaList) {
			if (dbMedia.getApiId() == media.getApiId()) {
				isNew = false;
			}
		}

		if (isNew) {
			Media persistMedia = new Media();
			persistMedia.setApiId(media.getApiId());
			persistMedia.setTitle(media.getTitle());
			persistMedia.setType(media.getType());

			repository.save(persistMedia);
			this.mediaList.add(persistMedia);
		}
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

	public void importFromXml(String fileText) {
		try {
			// Use sun's default implementation
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String resPath = this.getClass().getResource("DataTransfer.xsd").getPath();
			File file = new File(resPath.substring(0, resPath.length() - 16) + "//dionysus.xml");

			// Create the file
			if (file.createNewFile()) {
				System.out.println("File is created!");
			} else {
				System.out.println("File already exists.");
			}

			// Write Content
			FileWriter writer = new FileWriter(file);
			writer.write(fileText);
			writer.close();

			document = builder.parse(file);
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
			media.setApiId(Integer.parseInt(getValueOfSingleChild(elem, "apiId")));
			media.setTitle(getValueOfSingleChild(elem, "title"));
			media.setType(MediaType.lookupByName(getValueOfSingleChild(elem, "type")));
			medias.add(media);
		}

		for (Media media : medias) {
			boolean isNew = true;
			for (Media dbMedia : this.mediaList) {
				if (dbMedia.getApiId() == media.getApiId()) {
					isNew = false;
				}
			}

			if (isNew) {
				mediasToAdd.add(media);
			}
		}

		for (Media m : mediasToAdd) {
			addMedia(m);
		}
	}

	public String getValueOfSingleChild(Element elem, String tagName) {
		NodeList nodeList = elem.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equalsIgnoreCase(tagName)) {
				return node.getTextContent();
			}
		}
		return "";
	}

	public String exportToXml() {
		org.jdom2.Element root = new org.jdom2.Element("medias");
		root.setAttribute("noNamespaceSchemaLocation", "DataTransfer.xsd");
		
		org.jdom2.Document dokument = new org.jdom2.Document(root);
		for (Media m : mediaList) {
			root.addContent(new org.jdom2.Element("media")
					.addContent(new org.jdom2.Element("idDb").addContent(String.valueOf(m.getIdDb())))
					.addContent(new org.jdom2.Element("type").addContent(m.getType().name()))
					.addContent(new org.jdom2.Element("apiId").addContent(String.valueOf(m.getApiId())))
					.addContent(new org.jdom2.Element("title").addContent(m.getTitle())));
		}

		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		return outputter.outputString(dokument);
	}
}
