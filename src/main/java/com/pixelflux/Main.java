package com.pixelflux;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {//javaFx의 Application 상속
    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane(new Label("이미지, 영상의 확장자 변환, 크기 조절을 위한 프로그램 입니다."));
        //창 크기
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Pixelflux");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
