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
import ch.duartemendes.dionysus.model.MediaType;
import ch.duartemendes.dionysus.model.SearchMediaContext;
import ch.duartemendes.dionysus.model.Serie;

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
	private SearchMediaContext searchContext = null;

	@GetMapping("searchMedia")
	private String getSearchSeriesMenu(Model model) {
		mediaService.fillMediaFromDB();

		if (foundResults == null) {
			searchContext = new SearchMediaContext(mediaService.getMediaList());
		} else {
			model.addAttribute("foundResults", foundResults);
		}
		model.addAttribute("searchContext", searchContext);

		return "searchMedia.html";
	}

	@PostMapping("searchMedia")
	public String searchMedia(@ModelAttribute SearchMediaContext searchContext, Model model) {
		this.searchContext = searchContext;
		if (searchContext.getSelectedMedia() != null) {
			Media foundMedia = apiHandler.openMedia(searchContext);
			mediaService.addMedia(foundMedia);
			return openMedia(foundMedia, model);
		} else {
			foundResults = apiHandler.searchMedia(searchContext);
			return getSearchSeriesMenu(model);
		}
	}
	
	@PostMapping("openMedia")
	public String openMedia(@ModelAttribute Media media, Model model) {
		if(MediaType.Movie.equals(media.getType())) {
			// TODO Impl this
			return "";
		} else if(MediaType.Serie.equals(media.getType())) {
			model.addAttribute("serie", (Serie) media);
			return "showSerie.html";
		} else {
			return "";
		}
	}
}
