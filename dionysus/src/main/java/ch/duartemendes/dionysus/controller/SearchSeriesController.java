package ch.duartemendes.dionysus.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ch.duartemendes.dionysus.model.ApiHandler;
import ch.duartemendes.dionysus.model.Media;
import ch.duartemendes.dionysus.model.MediaService;
import ch.duartemendes.dionysus.model.SearchMediaContext;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Controller
public class SearchSeriesController {
	@Autowired
	private MediaService mediaService;
	@Autowired
	private ApiHandler apiHandler;

	private ArrayList<Media> foundResults = null;

	@GetMapping("searchMedia")
	private String getSearchSeriesMenu(Model model) {
		mediaService.fillMediaFromDB();

		if (foundResults == null) {
			model.addAttribute("searchContext", new SearchMediaContext(mediaService.getMediaList()));
		} else {
			model.addAttribute("foundResults", foundResults);
		}

		return "searchMedia.html";
	}

	@PostMapping("searchMedia")
	public String searchMedia(@ModelAttribute SearchMediaContext searchContext, Model model) {
		if (searchContext.getSelectedMedia() != null) {
			Media foundMedia = apiHandler.openMedia(searchContext);
			mediaService.addMedia(foundMedia);
			// TODO: Redirect to info for Media
			return "";
		} else {
			foundResults = apiHandler.searchMedia(searchContext);
			model.addAttribute("searchContext", searchContext);
			// TODO: Darstellung der Results
			return getSearchSeriesMenu(model);
		}
	}
}
