package model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Gallery {

    private final ObservableList<Image> photos = FXCollections.observableArrayList();

    public void addPhoto(Image photo) {
        photos.add(photo);
    }

    public ObservableList<Image> getPhotos() {
        return photos;
    }

    public void clear() {
        photos.clear();
    }
}
