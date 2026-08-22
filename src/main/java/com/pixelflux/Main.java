package com.pixelflux;

import com.pixelflux.controller.MainController;
import com.pixelflux.view.MainView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        Parent root = mainView.createContent();
        new MainController(mainView);

        //창 크기
        Scene scene = new Scene(root, 800, 700);
        primaryStage.setTitle("Pixelflux");
        primaryStage.setScene(scene);

        //프로그램 실행완료시까지 시간 측정
        primaryStage.setOnShown(event -> {
            long totalTime = System.currentTimeMillis() - Launcher.START_TIME;
            System.out.printf("🚀 [PixelFlux] 앱 실행 완료까지 걸린 시간: %d ms (%.2f초)%n",
                    totalTime, totalTime / 1000.0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {launch(args);}
}
