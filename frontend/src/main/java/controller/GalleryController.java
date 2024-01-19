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
import util.PathUtils;
import util.PhotoSize;
import util.Placeholders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GalleryController {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

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

    @FXML
    private ListView<String> directoriesListView;

    private final ObservableList<String> draggedFilesNames = FXCollections.observableArrayList();

    private final ObservableList<String> directoriesNames = FXCollections.observableArrayList();

    private final List<File> filesToUpload = new ArrayList<>();
    private final int columnCountSmall = 5;
    private final int columnCountMedium = 3;
    private final int columnCountLarge = 2;

    private int currentPage = 1;

    private String currentPath = "";


    @FXML
    public void initialize() {
        fillDirectories();
        draggedFilesNamesList.setItems(draggedFilesNames);
        directoriesListView.setItems(directoriesNames);
        directoriesListView.setOnMouseClicked(event -> {
            String selectedItem = directoriesListView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;
            else if (Objects.equals(selectedItem, "..")) {
                currentPath = PathUtils.goUpPath(currentPath);
            }
            else currentPath = currentPath + "/" + selectedItem;
            fillDirectories();
            fillGallery();
        });
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
        if (!filesToUpload.isEmpty()) {
//            CommunicationHandler.uploadPhotos(filesToUpload, currentPath);
            CommunicationHandler.uploadFiles(filesToUpload, currentPath);
            clearUploadList();
            imagesGridPane.getChildren().clear();
            fillGallery();
            fillDirectories();
        }
    }

    @FXML
    private void onSizeChangeSmall() {
        size = PhotoSize.SMALL;
        imagesGridPane.getChildren().clear();
        fillGallery();
    }

    @FXML
    private void onSizeChangeMedium() {
        size = PhotoSize.MEDIUM;
        imagesGridPane.getChildren().clear();
        fillGallery();

    }

    @FXML
    private void onSizeChangeLarge() {
        size = PhotoSize.LARGE;
        imagesGridPane.getChildren().clear();
        fillGallery();
    }

    @FXML
    private void turnPageLeft() {
        if (currentPage > 1) {
            currentPage--;
            imagesGridPane.getChildren().clear();
            fillGallery();
        }
        currentPageLabel.setText(String.valueOf(currentPage));
    }

    @FXML
    private void turnPageRight() {
        currentPage++;
        imagesGridPane.getChildren().clear();
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
                System.out.println("Modal: " + nameField.getText() + " " + currentPath);
                CommunicationHandler.addFolder(nameField.getText(), currentPath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            fillDirectories();
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

            selectedPhoto.setPlaceholder(PhotoSize.ORIGINAL, CommunicationHandler.placeholder);
            imageView.imageProperty().bind(selectedPhoto.photoDataProperty());
        }

    }

    private void fillDirectories() {
        try {
            CommunicationHandler.getDirectories(directoriesNames, currentPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillGallery(){
        try {
//            CommunicationHandler.getAllPhotos(galleryModel, size); <- OLD
            int pageSize = switch (size) {
                case LARGE -> columnCountLarge * columnCountLarge;
                case MEDIUM -> columnCountMedium * columnCountMedium;
                case SMALL -> columnCountSmall * columnCountSmall;
                case ORIGINAL -> 1;
            };
            String serviceAddress = CommunicationHandler.getRequestAddress(size, currentPage - 1, pageSize, currentPath);
            if (CommunicationHandler.getAllProductsPageable(galleryModel, size, serviceAddress)) {

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
                                col == columnCountSmall && size == PhotoSize.SMALL) {
                            col = 0;
                            row++;
                        }
                    }
                });

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}