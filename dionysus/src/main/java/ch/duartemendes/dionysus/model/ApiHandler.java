package ch.duartemendes.dionysus.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Service
public class ApiHandler {
	private static final String APIKEY = "35aeccfd8848417a5c4bc01ea35a3ba4";
	private static final String USERKEY = "5E2B1667BC66B6.97262084";
	private static final String USERNAME = "MCPGamer";
	private static final String BASEURL = "https://api.thetvdb.com";
	private static final String IMAGEURL = "https://artworks.thetvdb.com";
	private static final String MOVIESEARCHURL = "https://tvshowtime-1.algolianet.com/1/indexes/*/queries?x-algolia-agent=Algolia%20for%20vanilla%20JavaScript%20(lite)%203.32.0%3Binstantsearch.js%20(3.5.3)%3BJS%20Helper%20(2.28.0)&x-algolia-application-id=tvshowtime&x-algolia-api-key=c9d5ec1316cec12f093754c69dd879d3";
	private static final String MOVIEJSONSTART = "{\"requests\":[{\"indexName\":\"TVDB\",\"params\":\"query=";
	private static final String MOVIEJSONEND = "&maxValuesPerFacet=10&page=0&highlightPreTag=ais-highlight&highlightPostTag=%2Fais-highlight&facets=%5B%22type%22%2C%22type%22%5D&tagFilters=\"}]}";
	private String token;

	@Autowired
	private MediaService mediaService;

	@SuppressWarnings("unchecked")
	public Media openMedia(SearchMediaContext context) {
		Media result = null;

		if (context.getSelectedMedia() != 0) {
			long searchMediaId = context.getSelectedMedia();
			Media searchMedia = mediaService.getMedia(searchMediaId);
			MediaType searchType = searchMedia.getType();

			if (searchType == null) {
				searchType = context.getSearchType();
				searchMedia.setType(searchType);
			}

			checkConnection();

			if (MediaType.Serie.equals(MediaType.Serie)) {
				String url = BASEURL + "/series/" + searchMedia.getApiId();
				String json = request(url, true);

				JSONObject jsonObj = new JSONObject(json);
				JSONObject foundSeries = jsonObj.getJSONObject("data");

				String foundSeriesJson = foundSeries.toString();
				Gson gson = new Gson();
				result = gson.fromJson(foundSeriesJson, Serie.class);

				result.setApiId(((Serie) result).getId());
				result.setTitle(((Serie) result).getSeriesName());
				result.setType(MediaType.Serie);
			} else if (MediaType.Movie.equals(MediaType.Movie)) {
				String url = BASEURL + "/movies/" + searchMedia.getApiId();
				String json = request(url, true);

				JSONObject jsonObj = new JSONObject(json);
				JSONObject foundMovie = jsonObj.getJSONObject("data");

				Movie movie = new Movie();
				movie.setId(foundMovie.getLong("id"));
				movie.setRuntime(foundMovie.getInt("runtime"));
				
				JSONArray foundGenres = foundMovie.getJSONArray("genres");
				ArrayList<String> genres = new ArrayList<>();
				for (int i = 0; i < foundGenres.length(); i++) {
					JSONObject foundGenre = foundGenres.getJSONObject(i);

					if(!genres.contains(foundGenre.getString("name"))) {
						genres.add(foundGenre.getString("name"));
					}
				}
				movie.setGenres((String[]) genres.toArray());
				
				JSONArray foundTitles = foundMovie.getJSONArray("translations");
				ArrayList<String> titles = new ArrayList<>();
				for (int i = 0; i < foundTitles.length(); i++) {
					JSONObject foundTitle = foundTitles.getJSONObject(i);
					
					if(foundTitle.getString("language_code").equals("eng")) {
						movie.setTitle(foundTitle.getString("name"));
						movie.setOverview(foundTitle.getString("overview"));
					}
					
					titles.add(foundTitle.getString("name") + " (" + foundTitle.getString("language_code") + ")");
				}
		
				movie.setReleaseDate((String[]) titles.toArray());
				
				JSONArray foundReleaseDates = foundMovie.getJSONArray("release_dates");
				ArrayList<String> dates = new ArrayList<>();
				for (int i = 0; i < foundReleaseDates.length(); i++) {
					JSONObject foundDate = foundReleaseDates.getJSONObject(i);
					dates.add(foundDate.getString("country") + " - " + foundDate.getString("date"));
				}
				movie.setReleaseDate((String[]) dates.toArray());
				

				JSONArray foundImages = foundMovie.getJSONArray("artworks");
				boolean firstPoster = true;
				boolean firstBanner = true;
				boolean firstFanart = true;
				for (int i = 0; i < foundImages.length(); i++) {
					JSONObject foundImage = foundImages.getJSONObject(i);

					if(firstPoster && foundImage.getString("artwork_type").equals("Poster")) {
						movie.setImage(IMAGEURL + foundImage.getString("url"));
						movie.setPoster(IMAGEURL + foundImage.getString("url"));
						firstPoster = false;
					} else if(firstBanner && foundImage.getString("artwork_type").equals("Background")) {
						movie.setBanner(IMAGEURL + foundImage.getString("url"));
						firstBanner = false;
					} else if (firstFanart) {
						movie.setFanart(IMAGEURL + foundImage.getString("url"));
						firstFanart = false;
					}
				}

				result = movie;
				result.setApiId(movie.getId());
				result.setTitle(movie.getTitle());
				result.setType(MediaType.Movie);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Media openMedia(long id, MediaType type) {
		Media result = null;

		checkConnection();

		if (MediaType.Serie.equals(type)) {
			String url = BASEURL + "/series/" + id;
			String json = request(url, true);

			JSONObject jsonObj = new JSONObject(json);
			JSONObject foundSeries = jsonObj.getJSONObject("data");

			String foundSeriesJson = foundSeries.toString();
			Gson gson = new Gson();
			result = gson.fromJson(foundSeriesJson, Serie.class);

			result.setApiId(((Serie) result).getId());
			result.setTitle(((Serie) result).getSeriesName());
			result.setType(MediaType.Serie);

			if (((Serie) result).getFanart() != null && !((Serie) result).getFanart().isEmpty()) {
				((Serie) result).setFanart(IMAGEURL + "/banners/" + ((Serie) result).getFanart());
			}
			if (((Serie) result).getBanner() != null && !((Serie) result).getBanner().isEmpty()) {
				((Serie) result).setBanner(IMAGEURL + "/banners/" + ((Serie) result).getBanner());
			}
			if (((Serie) result).getPoster() != null && !((Serie) result).getPoster().isEmpty()) {
				((Serie) result).setPoster(IMAGEURL + "/banners/" + ((Serie) result).getPoster());
			}
		} else if (MediaType.Movie.equals(type)) {
			String url = BASEURL + "/movies/" + id;
			String json = request(url, true);

			JSONObject jsonObj = new JSONObject(json);
			JSONObject foundMovie = jsonObj.getJSONObject("data");

			Movie movie = new Movie();
			movie.setId(foundMovie.getLong("id"));
			movie.setRuntime(foundMovie.getInt("runtime"));
			
			JSONArray foundGenres = foundMovie.getJSONArray("genres");
			ArrayList<String> genres = new ArrayList<>();
			for (int i = 0; i < foundGenres.length(); i++) {
				JSONObject foundGenre = foundGenres.getJSONObject(i);

				if(!genres.contains(foundGenre.getString("name"))) {
					genres.add(foundGenre.getString("name"));
				}
			}
			movie.setGenres(genres.toArray(new String[genres.size()]));
			
			JSONArray foundTitles = foundMovie.getJSONArray("translations");
			ArrayList<String> titles = new ArrayList<>();
			for (int i = 0; i < foundTitles.length(); i++) {
				JSONObject foundTitle = foundTitles.getJSONObject(i);
				
				if(foundTitle.getString("language_code").equals("eng")) {
					movie.setTitle(foundTitle.getString("name"));
					movie.setOverview(foundTitle.getString("overview"));
				}
				
				titles.add(foundTitle.getString("name") + " (" + foundTitle.getString("language_code") + ")");
			}
	
			movie.setReleaseDate(titles.toArray(new String[titles.size()]));
			
			JSONArray foundReleaseDates = foundMovie.getJSONArray("release_dates");
			ArrayList<String> dates = new ArrayList<>();
			for (int i = 0; i < foundReleaseDates.length(); i++) {
				JSONObject foundDate = foundReleaseDates.getJSONObject(i);
				dates.add(foundDate.getString("country") + " - " + foundDate.getString("date"));
			}
			movie.setReleaseDate(dates.toArray(new String[dates.size()]));
			

			JSONArray foundImages = foundMovie.getJSONArray("artworks");
			boolean firstPoster = true;
			boolean firstBanner = true;
			boolean firstFanart = true;
			for (int i = 0; i < foundImages.length(); i++) {
				JSONObject foundImage = foundImages.getJSONObject(i);

				if(firstPoster && foundImage.getString("artwork_type").equals("Poster")) {
					movie.setImage(IMAGEURL + foundImage.getString("url"));
					movie.setPoster(IMAGEURL + foundImage.getString("url"));
					firstPoster = false;
				} else if(firstBanner && foundImage.getString("artwork_type").equals("Background")) {
					movie.setBanner(IMAGEURL + foundImage.getString("url"));
					firstBanner = false;
				} else if (firstFanart  
						&& !foundImage.getString("artwork_type").equals("Background")
						&& !foundImage.getString("artwork_type").equals("Poster")) {
					movie.setFanart(IMAGEURL + foundImage.getString("url"));
					firstFanart = false;
				}
			}

			result = movie;
			result.setApiId(movie.getId());
			result.setTitle(movie.getTitle());
			result.setType(MediaType.Movie);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Media> searchMedia(SearchMediaContext context) {
		MediaType type = context.getSearchType();
		String title = context.getSearchTitle();

		checkConnection();

		ArrayList<Media> results = new ArrayList<>();

		if (MediaType.Serie.equals(type)) {
			String url = BASEURL + "/search/series?name=" + title;
			String result = request(url, true);

			JSONObject jsonObj = new JSONObject(result);
			JSONArray dataArray = jsonObj.getJSONArray("data");

			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject foundSeries = dataArray.getJSONObject(i);

				String foundSeriesJson = foundSeries.toString();
				Gson gson = new Gson();

				Serie serie = gson.fromJson(foundSeriesJson, Serie.class);
				serie.setImage(IMAGEURL + serie.getImage());

				results.add(serie);
			}
		} else if (MediaType.Movie.equals(type)) {
			try {
				String url = MOVIESEARCHURL;
				String sendJson = MOVIEJSONSTART + title + MOVIEJSONEND;
				
				RestTemplate rt = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();

				headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
				headers.setAccept(Arrays.asList(org.springframework.http.MediaType.APPLICATION_JSON));
				headers.add("user-agent",
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

				HttpEntity<String> entity = new HttpEntity<String>(sendJson, headers);
				ResponseEntity<String> res = rt.exchange(url, HttpMethod.POST, entity, String.class);
				String resultJson = res.getBody();
				
				JSONObject jsonObj = new JSONObject(resultJson);
				JSONArray foundMedias = ((JSONObject) jsonObj.getJSONArray("results").get(0)).getJSONArray("hits");
				
				for (int i = 0; i < foundMedias.length(); i++) {
					JSONObject foundMedia = foundMedias.getJSONObject(i);

					if(foundMedia.getString("type").equals("Movie")) {
						Movie movie = new Movie();
						movie.setImage(foundMedia.getString("image"));
						movie.setTitle(foundMedia.getString("name"));
						movie.setId(foundMedia.getLong("id"));
						results.add(movie);
					}
				}
			} catch (Exception e) {
			}
		}

		return results;
	}

	private void checkConnection() {
		if (token == null) {
			connect();
		} else {
			refresh();
		}
	}

	@SuppressWarnings("unchecked")
	private void connect() {
		try {
			String url = BASEURL + "/login";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("apikey", APIKEY);
			params.put("userkey", USERKEY);
			params.put("username", USERNAME);
			String result = request(url, true, params);

			JSONObject jsonObj = new JSONObject(result);
			token = jsonObj.getString("token");
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	private void refresh() {
		try {
			String url = BASEURL + "/refresh_token";
			String result = request(url, true);
			JSONObject jsonObj = new JSONObject(result);
			token = jsonObj.getString("token");
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	public String request(String url, Boolean isJson, HashMap<String, String>... additionalParams) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		if (isJson) {
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.setAccept(Arrays.asList(org.springframework.http.MediaType.APPLICATION_JSON));
		}
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

		String json = "";
		if (additionalParams != null && additionalParams.length > 0) {
			json = "{";
			for (String key : additionalParams[0].keySet()) {
				json = json + '"' + key + '"' + ':' + '"' + additionalParams[0].get(key) + '"' + ',';
			}

			json = json.substring(0, json.length() - 1) + "}";
		}

		ResponseEntity<String> res = null;
		if (token != null) {
			headers.setBearerAuth(token);
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			res = rt.exchange(url, HttpMethod.GET, entity, String.class);
		} else {
			HttpEntity<String> entity = new HttpEntity<String>(json, headers);
			res = rt.exchange(url, HttpMethod.POST, entity, String.class);
		}

		return res.getBody();
	}
}
