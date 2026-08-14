package com.pixelflux;

import com.pixelflux.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {//javaFx의 Application 상속
    @Override
    public void start(Stage primaryStage) throws Exception {
        MainView mainView = new MainView();
        //창 크기
        Scene scene = new Scene(mainView.createContent(), 800, 700);
        primaryStage.setTitle("Pixelflux");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
