package ch.duartemendes.dionysus.controller;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	private String getMainMenu(Model model) {
		mediaService.fillMediaFromDB();
		String joke = loadJoke();
		model.addAttribute("joke", joke);
		return "index.html";
	}

	@GetMapping("/")
	private String getMainMenuRoot(Model model) {
		mediaService.fillMediaFromDB();
		String joke = loadJoke();
		model.addAttribute("joke", joke);
		return "index.html";
	}

	private String loadJoke() {
		try {
			Document doc = Jsoup.connect("https://www.ajokeaday.com/").get();
			Element foundJoke = doc.selectFirst(".pnl-joke .jd-body p");
			return foundJoke.text();
		} catch (IOException e) {
		}
		return "";
	}
}
