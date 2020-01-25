package ch.duartemendes.dionysus.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
public class SearchMediaContext {
	private ArrayList<Media> storedMedia;
	private Media selectedMedia;
	private String searchTitle;
	private ArrayList<MediaType> mediaTypes;
	private MediaType searchType;

	public SearchMediaContext(ArrayList<Media> storedMedia) {
		this.storedMedia = storedMedia;
		selectedMedia = null;
		searchTitle = "";
		mediaTypes = new ArrayList<>();
		Collections.addAll(mediaTypes, MediaType.values());
		searchTitle = null;
	}

	public ArrayList<Media> getStoredMedia() {
		return storedMedia;
	}

	public void setStoredMedia(ArrayList<Media> storedMedia) {
		this.storedMedia = storedMedia;
	}

	public Media getSelectedMedia() {
		return selectedMedia;
	}

	public void setSelectedMedia(Media selectedMedia) {
		this.selectedMedia = selectedMedia;
	}

	public String getSearchTitle() {
		return searchTitle;
	}

	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	public ArrayList<MediaType> getMediaTypes() {
		return mediaTypes;
	}

	public void setMediaTypes(ArrayList<MediaType> mediaTypes) {
		this.mediaTypes = mediaTypes;
	}

	public MediaType getSearchType() {
		return searchType;
	}

	public void setSearchType(MediaType searchType) {
		this.searchType = searchType;
	}
}
