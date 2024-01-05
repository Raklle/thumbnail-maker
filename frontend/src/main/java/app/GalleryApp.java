package app;

import controller.GalleryController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import model.Gallery;
import socket.WebSocketClientHandler;

import java.io.IOException;

public class GalleryApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // socket will be implemented for M3
//        WebSocketClientHandler.connectSocket();

        var gallery = new Gallery();
        try {
            // load layout from FXML file
            var loader = new FXMLLoader();
            loader.setLocation(GalleryApp.class.getClassLoader().getResource("view/galleryView.fxml"));
            BorderPane rootLayout = loader.load();

            // set initial data into controller
            GalleryController controller = loader.getController();
            controller.setModel(gallery);

            // add layout to a scene and show them all
            configureStage(primaryStage, rootLayout);
            primaryStage.show();

        } catch (IOException e) {
            // don't do this in common apps
            e.printStackTrace();
        }
    }

    private void configureStage(Stage primaryStage, BorderPane rootLayout) {
        var scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gallery app");
        primaryStage.minWidthProperty().bind(rootLayout.minWidthProperty());
        primaryStage.minHeightProperty().bind(rootLayout.minHeightProperty());
    }
}
