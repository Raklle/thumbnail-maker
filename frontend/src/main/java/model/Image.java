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

    public Image(String id, byte[] photoData, String imageState) {
        this.id = id;
        this.photoData = new SimpleObjectProperty<>(new javafx.scene.image.Image(new ByteArrayInputStream(photoData)));
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

    public void setPlaceholder(PhotoSize size, Placeholders placeholders){
        this.photoData = placeholders.getPlaceholder(size);
    }


}