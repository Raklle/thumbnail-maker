package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.PhotoSize;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Image {

    private final String id;
    private ObjectProperty<javafx.scene.image.Image> photoData;

    public Image(String id, byte[] photoData) {
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

    public void setPlaceholder(PhotoSize size){
        try {
            this.photoData =  new SimpleObjectProperty<>(new javafx.scene.image.Image(new FileInputStream(buildFilePath(size))));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static String buildFilePath(PhotoSize size) {
        String basePath = "frontend/src/main/resources/assets/placeholder";
        String fileExtension = ".png";

        return switch (size) {
            case SMALL -> basePath + "_small" + fileExtension;
            case MEDIUM-> basePath + "_medium" + fileExtension;
            case LARGE -> basePath + "_large" + fileExtension;
            case ORIGINAL -> basePath + fileExtension;
        };
    }
}