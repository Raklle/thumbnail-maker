package controller;


import api.CommunicationHandler;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Gallery;
import model.Image;
import util.PhotoSize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    @FXML
    private Label currentPageLabel;

    private Gallery galleryModel;

    private PhotoSize size = PhotoSize.SMALL;

    @FXML
    private ListView<String> draggedFilesNamesList;

    private final ObservableList<String> draggedFilesNames = FXCollections.observableArrayList();

    private final List<File> filesToUpload = new ArrayList<>();

    private final int collumnCountSmall = 5;
    private final int collumnCountMedium = 3;
    private final int collumnCountLarge = 2;

    private int currentPage = 1;

    private String currentPath = "";


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
    private void onSizeChangeSmall() {
        size = PhotoSize.SMALL;
        fillGallery();
    }

    @FXML
    private void onSizeChangeMedium() {
        size = PhotoSize.MEDIUM;
        fillGallery();

    }

    @FXML
    private void onSizeChangeLarge() {
        size = PhotoSize.LARGE;
        fillGallery();
    }

    @FXML
    private void turnPageLeft() {
        if (currentPage > 1) {
            currentPage--;
            fillGallery();
        }
        currentPageLabel.setText(String.valueOf(currentPage));
    }

    @FXML
    private void turnPageRight() {
        currentPage++;
        fillGallery();
        currentPageLabel.setText(String.valueOf(currentPage));
    }

    @FXML
    private void openNewFolderModal() {
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.setTitle("New Folder");

        TextField nameField = new TextField("Folder name");

        Button submitButton = new Button("Create");
        submitButton.setOnAction(e -> {
            try {
                CommunicationHandler.addFolder(nameField.getText(), currentPath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            formStage.close();
        });

        VBox formLayout = new VBox(10);
        formLayout.getChildren().addAll(nameField, submitButton);
        formLayout.setPadding(new Insets(0, 20, 0, 20));
        formLayout.setAlignment(Pos.CENTER);

        Scene formScene = new Scene(formLayout, 250, 150);
        formStage.setScene(formScene);

        formStage.showAndWait();
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
            selectedPhoto.setPlaceholder(PhotoSize.ORIGINAL);
            imageView.imageProperty().bind(selectedPhoto.photoDataProperty());
        }

    }

    private void fillGallery(){
        try {
            imagesGridPane.getChildren().clear();
//            CommunicationHandler.getAllPhotos(galleryModel, size);
            int pageSize = switch (size) {
                case LARGE -> collumnCountLarge * collumnCountLarge;
                case MEDIUM -> collumnCountMedium * collumnCountMedium;
                case SMALL -> collumnCountSmall * collumnCountSmall;
                case ORIGINAL -> 1;
            };
            String serviceAddress = CommunicationHandler.getRequestAddress(size, currentPage-1, pageSize, currentPath);
            CommunicationHandler.getAllProductsPageable(galleryModel, size, serviceAddress);

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

                    if (col == collumnCountLarge && size == PhotoSize.LARGE ||
                            col == collumnCountMedium && size == PhotoSize.MEDIUM ||
                            col == collumnCountSmall && size == PhotoSize.SMALL){
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