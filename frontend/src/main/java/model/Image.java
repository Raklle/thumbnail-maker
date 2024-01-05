package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import util.ImageState;

import java.io.ByteArrayInputStream;
import java.util.UUID;

public class Image {

    private final String id;
    public byte[] raw;
    private final ObjectProperty<javafx.scene.image.Image> photoData;

    public Image(String id, byte[] photoData) {
        this.raw = photoData;
        this.id = id;
        this.photoData = new SimpleObjectProperty<>(new javafx.scene.image.Image(new ByteArrayInputStream(photoData)));
    }

    public String id() {
        return id;
    }

    public javafx.scene.image.Image getPhotoData() {
        return photoData.get();
    }

    public ObjectProperty<javafx.scene.image.Image> photoDataProperty() {
        return photoData;
    }
}