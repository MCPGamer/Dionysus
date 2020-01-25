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

@Service
public class ApiHandler {
	private static final String APIKEY = "35aeccfd8848417a5c4bc01ea35a3ba4";
	private static final String USERKEY = "5E2B1667BC66B6.97262084";
	private static final String USERNAME = "MCPGamer";
	private static final String BASEURL = "https://api.thetvdb.com";
	private static final String IMAGEURL = "https://artworks.thetvdb.com";
	private static final String MOVIESEARCHURL = "https://thetvdb.com/search?menu[type]=Movie&query=";
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
				// TODO Search results for Movie
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Media openMedia(long id) {
		Media result = null;

		checkConnection();

		if (MediaType.Serie.equals(MediaType.Serie)) {
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
		} else if (MediaType.Movie.equals(MediaType.Movie)) {
			// TODO Search results for Movie
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
				String url = MOVIESEARCHURL + title;
				StringBuilder result = new StringBuilder();
				URL url2 = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
				conn.setRequestMethod("GET");
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				rd.close();

				Document doc = Jsoup.parse(result.toString());
				Elements foundMedias = doc.select(".media");

				for (Element media : foundMedias) {
					Movie movie = new Movie();

					Element imageDiv = media.selectFirst(".media-object");
					movie.setImage(imageDiv.attr("src"));

					Element titleDiv = media.selectFirst(".media-heading");
					movie.setTitle(titleDiv.text());

					Element idDiv = media.selectFirst(".media-body").selectFirst(".text-muted");
					String parseId = idDiv.text();
					movie.setId(Long.valueOf(parseId.substring(parseId.indexOf('#'), parseId.length())));

					results.add(movie);
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
