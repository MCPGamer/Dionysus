package ch.duartemendes.dionysus.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
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
	private String token;

	public Media openMedia(SearchMediaContext context) {
		Media result = null;

		if (context.getSelectedMedia() != null) {
			Media searchMedia = context.getSelectedMedia();
			MediaType searchType = searchMedia.getType();

			if (searchType == null) {
				searchType = context.getSearchType();
				searchMedia.setType(searchType);
			}

			checkConnection();
			
			if(MediaType.Serie.equals(MediaType.Serie)) {
				String url = BASEURL + "/series/" + searchMedia.getApiId();
				String json = request(url);
				
				JSONObject jsonObj = new JSONObject(json);
				JSONObject foundSeries = jsonObj.getJSONObject("data");
					
				String foundSeriesJson = foundSeries.toString();
				Gson gson = new Gson();
				result = gson.fromJson(foundSeriesJson, Serie.class);

				result.setApiId(((Serie) result).getId());
				result.setTitle(((Serie) result).getSeriesName());
				result.setType(MediaType.Serie);
			} else if (MediaType.Movie.equals(MediaType.Movie)) {
				//TODO Search results for Movie 
			}
		}

		return result;
	}

	public ArrayList<Media> searchMedia(SearchMediaContext context) {
		MediaType type = context.getSearchType();
		String title = context.getSearchTitle();

		checkConnection();

		ArrayList<Media> results = new ArrayList<>();
		
		if(MediaType.Serie.equals(type)) {
			String url = BASEURL + "/search/series?name=" + title;
			String result = request(url);
			
			JSONObject jsonObj = new JSONObject(result);
			JSONArray dataArray = jsonObj.getJSONArray("data");
			
			for(int i = 0; i < dataArray.length(); i++) {
				JSONObject foundSeries = dataArray.getJSONObject(i);
				
				String foundSeriesJson = foundSeries.toString();
				Gson gson = new Gson();
				results.add(gson.fromJson(foundSeriesJson, Serie.class));
			}
		} else if (MediaType.Movie.equals(type)) {
			//TODO Search results for Movie (maybe this will be HTML)
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

	private void connect() {
		try {
			String url = BASEURL + "/login";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("apikey", APIKEY);
			params.put("userkey", USERKEY);
			params.put("username", USERNAME);
			String result = request(url, params);

			JSONObject jsonObj = new JSONObject(result);
			token = jsonObj.getString("token");
		} catch (Exception e) {
		}
	}

	private void refresh() {
		try {
			String url = BASEURL + "/refresh_token";
			String result = request(url);
			JSONObject jsonObj = new JSONObject(result);
			token = jsonObj.getString("token");
		} catch (Exception e) {
		}
	}

	public String request(String url, HashMap<String,String>... additionalParams) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Arrays.asList(org.springframework.http.MediaType.APPLICATION_JSON));
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		if(additionalParams[0] != null) {
			for(String key : additionalParams[0].keySet()) {
				headers.add(key, additionalParams[0].get(key));
			}
		}
		
		if(token != null) {
			headers.setBearerAuth(token);
		}
		
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> res = rt.exchange(url, HttpMethod.GET, entity, String.class);

		return res.getBody();
	}
}
