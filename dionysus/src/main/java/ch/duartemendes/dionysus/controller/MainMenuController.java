package ch.duartemendes.dionysus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Controller
public class MainMenuController {
	@GetMapping("index")
	private String getMainMenu() {
		return "index.html";
	}
	
	@GetMapping("/")
	private String getMainMenuRoot() {
		return "index.html";
	}
}
