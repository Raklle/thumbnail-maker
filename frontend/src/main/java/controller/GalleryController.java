package controller;


import api.CommunicationHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.Gallery;
import model.Image;
import util.PhotoSize;
import util.Placeholders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GalleryController {

    @FXML
    private TextField imageNameField;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @FXML
    private ImageView imageView;

    @FXML
    private GridPane imagesGridPane;

    @FXML
    private StackPane fileDropPane;

    @FXML
    private Label dropPaneLabel;

    private Gallery galleryModel;

    private PhotoSize size = PhotoSize.SMALL;

    @FXML
    private ListView<String> draggedFilesNamesList;

    private final ObservableList<String> draggedFilesNames = FXCollections.observableArrayList();
    private final List<File> filesToUpload = new ArrayList<>();
    private static final int columnCountSmall = 5;
    private static final int columnCountMedium = 3;
    private static final int columnCountLarge = 2;



    @FXML
    public void initialize() {
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
        scheduler.scheduleAtFixedRate(this::fillGallery, 0, 2, TimeUnit.SECONDS);
    }

    private void bindSelectedPhoto(Image selectedPhoto) {
        try {
            CommunicationHandler.getOriginalPhoto(selectedPhoto.id()).ifPresent(
                    photo -> imageView.imageProperty().bind(photo.photoDataProperty())
            );
        } catch (IOException e) {

            selectedPhoto.setPlaceholder(PhotoSize.ORIGINAL, CommunicationHandler.placeholder);
            imageView.imageProperty().bind(selectedPhoto.photoDataProperty());
        }

    }

    // needs to be called every 1s before the socket works

    private void fillGallery(){
        try {
            CommunicationHandler.getAllPhotos(galleryModel, size);

            Platform.runLater(() -> {
                imagesGridPane.getChildren().clear();
                int row = 0;
                int col = 0;

                for (Image photo : galleryModel.getPhotos()) {
                    StackPane stackPane = new StackPane();
                    ImageView imageView = new ImageView(photo.getPhotoData());
                    imageView.setPreserveRatio(true);

                    imageView.setOnMouseClicked(e -> {
                        bindSelectedPhoto(photo);
                    });

                    stackPane.getChildren().add(imageView);

                    imagesGridPane.add(stackPane, col, row);
                    col++;

                    if (col == columnCountLarge && size == PhotoSize.LARGE ||
                            col == columnCountMedium && size == PhotoSize.MEDIUM ||
                            col == columnCountSmall && size == PhotoSize.SMALL){
                        col = 0;
                        row++;
                    }
                }
            });

        } catch (IOException e) {
            System.out.println("Gallery filled");
            e.printStackTrace();
        }
    }
}