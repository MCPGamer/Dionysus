package ch.duartemendes.dionysus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.duartemendes.dionysus.model.MediaService;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Controller
public class MainMenuController {
	@Autowired
	private MediaService mediaService;
	
	@GetMapping("index")
	private String getMainMenu() {
		mediaService.fillMediaFromDB();
		return "index.html";
	}
	
	@GetMapping("/")
	private String getMainMenuRoot() {
		mediaService.fillMediaFromDB();
		return "index.html";
	}
}
