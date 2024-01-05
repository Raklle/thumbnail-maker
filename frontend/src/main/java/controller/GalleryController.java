package controller;


import api.CommunicationHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import model.Gallery;
import model.Image;
import util.PhotoSize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleryController {

    @FXML
    private TextField imageNameField;

    @FXML
    private ImageView imageView;

    @FXML
    private ListView<Image> imagesListView;

    @FXML
    private StackPane fileDropPane;

    @FXML
    private Label dropPaneLabel;

    private Gallery galleryModel;

    private PhotoSize size = PhotoSize.SMALL;

    @FXML
    private ListView<String> draggedFilesNamesList;

    private ObservableList<String> draggedFilesNames = FXCollections.observableArrayList();

    private List<File> filesToUpload = new ArrayList<>();




    @FXML
    public void initialize() {

        imagesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    StackPane stackPane = new StackPane();
                    ImageView imageView = new ImageView(item.getPhotoData());
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(50);
                    stackPane.getChildren().add(imageView);

                    setGraphic(stackPane);
                }
            }
        });

        imagesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {

                    bindSelectedPhoto(newValue);
                }
        );
        draggedFilesNamesList.setItems(draggedFilesNames);
    }

    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != fileDropPane && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            success = true;
            String filesText = "Dropped files:\n";

            for (java.io.File file : db.getFiles()) {
                filesText += file.getAbsolutePath() + "\n";
                filesToUpload.add(file);
                draggedFilesNames.add(file.getAbsolutePath());
            }

            System.out.println(filesText);
        }

        dropPaneLabel.setText(draggedFilesNames.size() + " files dropped");
        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    private void clearUploadList() {
        dropPaneLabel.setText("Drag your files here");
        filesToUpload.clear();
        draggedFilesNames.clear();
    }

    @FXML
    private void uploadPhotos() throws IOException {

        CommunicationHandler.uploadPhotos(filesToUpload);
        clearUploadList();
    }

    @FXML
    private void onSizeChangeSmall() throws IOException {
        size = PhotoSize.SMALL;
        fillGallery();
    }

    @FXML
    private void onSizeChangeMedium() throws IOException {
        size = PhotoSize.MEDIUM;
        fillGallery();

    }

    @FXML
    private void onSizeChangeLarge() throws IOException {
        size = PhotoSize.LARGE;
        fillGallery();
    }

    public void setModel(Gallery gallery) throws IOException {
        this.galleryModel = gallery;
        imagesListView.setItems(gallery.getPhotos());
        imagesListView.getSelectionModel().select(0);
        fillGallery();
    }

    private void bindSelectedPhoto(Image selectedPhoto) {
        try {
            CommunicationHandler.getOriginalPhoto(selectedPhoto.id()).ifPresent(
                    photo -> imageView.imageProperty().bind(photo.photoDataProperty())
            );
        } catch (IOException e) {

            imageView.imageProperty().bind(getPlaceholder("F"));
        }

    }

    // needs to be called every 1s before the socket works
    private void fillGallery() throws IOException {
        CommunicationHandler.getAllPhotos(galleryModel, size);
    }

    private ObjectProperty<javafx.scene.image.Image> getPlaceholder(String size){
        try {
            return new SimpleObjectProperty<>(new javafx.scene.image.Image(new FileInputStream(buildFilePath(size))));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static String buildFilePath(String size) {
        String basePath = "frontend/src/main/resources/assets/placeholder";
        String fileExtension = ".png";

        return switch (size.toLowerCase()) {
            case "small" -> basePath + "_small" + fileExtension;
            case "medium" -> basePath + "_medium" + fileExtension;
            case "large" -> basePath + "_large" + fileExtension;
            default -> basePath + fileExtension;
        };
    }
}