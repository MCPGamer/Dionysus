package ch.duartemendes.dionysus.model;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
public class Movie extends Media {
	private long id;
	private String title;
	private int runtime;
	private String[] genres;
	private String[] releaseDate;
	private String poster;
	private String banner;
	private String fanart;
	private String image;
	private String overview;
	private String[] alternateTitles;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public String[] getGenres() {
		return genres;
	}

	public void setGenres(String[] genres) {
		this.genres = genres;
	}

	public String[] getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String[] releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getFanart() {
		return fanart;
	}

	public void setFanart(String fanart) {
		this.fanart = fanart;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String[] getAlternateTitles() {
		return alternateTitles;
	}

	public void setAlternateTitles(String[] alternateTitles) {
		this.alternateTitles = alternateTitles;
	}

}
