package ch.duartemendes.dionysus.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Entity
public class Media {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idDb;
	@Enumerated(EnumType.STRING)
	private MediaType type;
	private long apiId;
	private String title;

	public long getIdDb() {
		return idDb;
	}

	public void setIdDb(long idDb) {
		this.idDb = idDb;
	}

	public MediaType getType() {
		return type;
	}

	public void setType(MediaType type) {
		this.type = type;
	}

	public long getApiId() {
		return apiId;
	}

	public void setApiId(long apiId) {
		this.apiId = apiId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
