package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.ImageState;
import util.PhotoSize;
import util.Placeholders;

import java.io.ByteArrayInputStream;

public class Image {

    private final String id;
    private ObjectProperty<javafx.scene.image.Image> photoData;
    private String imageState;
    private final Placeholders placeholders;

    public Image(String id, byte[] photoData, String imageState, Placeholders placeholders) {
        this.id = id;
        this.photoData = new SimpleObjectProperty<>(new javafx.scene.image.Image(new ByteArrayInputStream(photoData)));
        this.placeholders = placeholders;
        this.imageState = imageState;
    }

    public String id() {
        return id;
    }

    public String getImageState() {
        return imageState;
    }

    public javafx.scene.image.Image getPhotoData() {
        return photoData.get();
    }

    public ObjectProperty<javafx.scene.image.Image> photoDataProperty() {
        return photoData;
    }

    public void setPlaceholder(PhotoSize size){
        this.photoData = placeholders.getPlaceholder(size);
    }


}