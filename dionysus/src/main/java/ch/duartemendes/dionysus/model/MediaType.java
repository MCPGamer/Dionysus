package ch.duartemendes.dionysus.model;
/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
public enum MediaType {
	Movie, Serie;
	
	public static MediaType lookupByName(String name) {
		for(MediaType type : MediaType.values()) {
			if(type.name().equals(name)) {
				return type;
			}
		}
		
		return null;
	}
}
