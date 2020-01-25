package ch.duartemendes.dionysus.controller;

import ch.duartemendes.dionysus.model.XMLContent;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ch.duartemendes.dionysus.model.ApiHandler;
import ch.duartemendes.dionysus.model.MediaService;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Controller
public class DataTransferController {
	@Autowired
	private MediaService mediaService;
	@Autowired
	private ApiHandler apiHandler;

	@GetMapping("dataTransfer")
	private String getDataTransferRoot(Model model) {
		model.addAttribute("xmlContent", new XMLContent());
		return "dataTransfer.html";
	}

	@PostMapping("xmlUpload")
	private String uploadXML(@ModelAttribute XMLContent xmlContent, Model model) {
		if (xmlContent.getXml().contains("noNamespaceSchemaLocation=\"DataTransfer.xsd\"")) {
			mediaService.importFromXml(xmlContent.getXml());
			return "redirect:/";
		} else {
			model.addAttribute("xmlerror", "doesnt have dataTransfer.xsd");
			model.addAttribute("xml", "");
			return "dataTransfer.html";
		}
	}

	@PostMapping("xmlExport")
	public String exportXml(Model model) {
		XMLContent xmlContent = new XMLContent();
		xmlContent.setXml(mediaService.exportToXml());
		model.addAttribute("xmlContent", xmlContent);
		return "dataTransfer.html";
	}
}
