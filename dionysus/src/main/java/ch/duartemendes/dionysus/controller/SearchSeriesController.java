package ch.duartemendes.dionysus.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.duartemendes.dionysus.model.ApiHandler;
import ch.duartemendes.dionysus.model.Media;
import ch.duartemendes.dionysus.model.MediaService;
import ch.duartemendes.dionysus.model.MediaType;
import ch.duartemendes.dionysus.model.Movie;
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
		if(searchContext != null) {
			searchContext.setStoredMedia(mediaService.getMediaList());
		}
		
		if (foundResults == null || foundResults.isEmpty()) {
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
		if (searchContext.getSelectedMedia() != 0) {
			Media foundMedia = apiHandler.openMedia(searchContext);
			mediaService.addMedia(foundMedia);
			return openMedia(foundMedia.getApiId(), model, foundMedia.getType());
		} else if (searchContext.getSearchType() != null 
				&& searchContext.getSearchTitle() != null 
				&& !searchContext.getSearchTitle().isEmpty()) {
			foundResults = apiHandler.searchMedia(searchContext);
			return getSearchSeriesMenu(model);
		} else {
			return getSearchSeriesMenu(model);
		}
	}
	
	@GetMapping("openMedia")
	public String openMedia(@RequestParam long id, Model model, MediaType...inputType) {
		MediaType type = null;
		if(searchContext == null) {
			return getSearchSeriesMenu(model);			
		} else if (inputType != null && inputType.length != 0) {
			type = inputType[0];
		} else {
			type = searchContext.getSearchType();
		}
		
		Media foundMedia = apiHandler.openMedia(id, type);
		mediaService.addMedia(foundMedia);
		
		if(MediaType.Movie.equals(foundMedia.getType())) {
			model.addAttribute("movie", (Movie) foundMedia);
			return "showMovie.html";
		} else if(MediaType.Serie.equals(foundMedia.getType())) {
			model.addAttribute("serie", (Serie) foundMedia);
			return "showSerie.html";
		} else {
			return "";
		}
	}
}
