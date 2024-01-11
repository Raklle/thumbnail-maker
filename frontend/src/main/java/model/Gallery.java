package model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.PhotoSize;

public class Gallery {
    private final ObservableList<Image> photos = FXCollections.observableArrayList();
    private ArrayList<String> photosId = new ArrayList<>();

    private PhotoSize lastSize = PhotoSize.SMALL;

    public void addPhoto(Image photo) {
        photos.add(photo);
        if(photo.getImageState().equals("DONE")){
            photosId.add(photo.id());
        }
    }

    public ArrayList<String> getPhotosId() {
        return photosId;
    }

    public ObservableList<Image> getPhotos() {
        return photos;
    }

    public void clear(PhotoSize size) {
        if(!lastSize.equals(size)){
            photos.clear();
            photosId.clear();
            lastSize = size;
        }else{
            photos.removeIf(it -> !it.getImageState().equals("DONE"));
        }

    }
}
