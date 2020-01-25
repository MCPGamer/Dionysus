package ch.duartemendes.dionysus.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Entity
public class Media {
	@Id
	private int idDb;
	@Enumerated(EnumType.STRING)
	private MediaType type;
	private int apiId;
	private String title;

	public int getIdDb() {
		return idDb;
	}

	public void setIdDb(int idDb) {
		this.idDb = idDb;
	}

	public MediaType getType() {
		return type;
	}

	public void setType(MediaType type) {
		this.type = type;
	}

	public int getApiId() {
		return apiId;
	}

	public void setApiId(int apiId) {
		this.apiId = apiId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
