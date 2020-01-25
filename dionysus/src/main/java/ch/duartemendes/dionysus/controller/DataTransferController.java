package ch.duartemendes.dionysus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Controller
public class DataTransferController {
	@GetMapping("dataTransfer")
	private String getMainMenuRoot() {
		return "index.html";
	}
}
