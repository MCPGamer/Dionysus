/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.duartemendes.dionysus.model;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Duarte Goncalves Mendes
 * @version 1.0
 */
@Service
public class MediaService {
	@Autowired
	private MediaRepository repository;
    private ArrayList<Media> mediaList;

    public MediaService() {
        this.mediaList = new ArrayList<>();
    }

    public void addMedia(Media media){
    	repository.save(media);
        this.mediaList.add(media);
    }

    public ArrayList<Media> getMediaList() {
        return mediaList;
    }
    
    public void fillMediaFromDB() {
    	mediaList = (ArrayList<Media>) repository.findAll();
    }
}
