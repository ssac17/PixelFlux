package com.pixelflux;

import com.pixelflux.controller.MainController;
import com.pixelflux.view.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        Parent root = mainView.createContent();
        new MainController(mainView);

        //창 크기
        Scene scene = new Scene(root, 800, 700);
        KeyCombination closeShortcut = new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
        //command + w, 창 닫기
        scene.getAccelerators().put(closeShortcut, () -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setTitle("Pixelflux");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {launch(args);}
}
