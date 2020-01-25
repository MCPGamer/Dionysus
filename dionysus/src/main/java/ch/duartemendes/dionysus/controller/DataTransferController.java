package ch.duartemendes.dionysus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
	private String getDataTransferRoot() {
		return "dataTransfer.html";
	}
}
