package com.pixelflux.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

//레이아웃 구성
public class MainView {
    //파일 끌어 놓기 영역
    private Label dropZone;
    //파일 목록 표시할 영역
    private ListView<String> listView;

    private ComboBox<String> formatComboBox;
    private ComboBox<String> widthComboBox;
    private  ComboBox<String> qualityComboBox;
    private Button convertButton;
    private Button addFileButton;
    private Button clearListButton;

    public Parent createContent() {
        VBox root = new VBox(15); //간격 15
        root.setPadding(new Insets(20)); // padding 20px

        // 1. 드래그 앤 드롭 영역
        dropZone = new Label("📥 변환할 파일(이미지/동영상)을 여기에 끌어다 놓으세요");
        dropZone.setMaxWidth(Double.MAX_VALUE);
        dropZone.setMaxHeight(120);
        dropZone.setAlignment(Pos.CENTER); //중앙으로 정렬
        dropZone.setStyle(
                "-fx-border-color: #a0a0a0; " +
                "-fx-border-width: 2; " +
                "-fx-border-style: dashed; " +
                "-fx-border-radius: 8; " +
                "-fx-background-color: #f9f9f9; " +
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #555555; " +
                "-fx-cursor: hand;"
        );
        //파일 추가, 목록 비우기
        HBox listActionBar = new HBox(10);
        listActionBar.setAlignment(Pos.CENTER_LEFT);
        addFileButton = new Button("📁 파일 추가 (Finder)");
        clearListButton = new Button("🗑️ 목록 비우기");
        listActionBar.getChildren().addAll(addFileButton, clearListButton);

        // 2. 파일 목록 표시 영역
        listView = new ListView<>();
        //가로 패널
        VBox.setVgrow(listView, Priority.ALWAYS);
        //세로 패널
        HBox optionBox = new HBox(12);
        optionBox.setAlignment(Pos.CENTER_LEFT);

        //포맷
        formatComboBox = new ComboBox<>();
        formatComboBox.getItems().addAll("JPG", "PNG", "WEBP", "MP4");
        formatComboBox.setValue("JPG"); // 기본값

        //해상도
        widthComboBox = new ComboBox<>();
        widthComboBox.getItems().addAll("원본 유지", "1920 (FHD)", "1280 (HD)", "800");
        widthComboBox.setValue("1280 (HD)");

        //압축 품질
        qualityComboBox = new ComboBox<>();
        qualityComboBox.getItems().addAll("100% (최상)", "80% (권장)", "60% (압축)");
        qualityComboBox.setValue("80% (권장)");

        //option box 세팅
        optionBox.getChildren().addAll(
                new Label("포맷:"), formatComboBox,
                new Label("해상도:"), widthComboBox,
                new Label("품질:"), qualityComboBox
        );

        //변환 버튼
        convertButton = new Button("변환 시작");
        convertButton.setMaxWidth(Double.MAX_VALUE);
        convertButton.setMaxHeight(42);
        convertButton.setStyle(
                "-fx-background-color: #2563eb; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 15px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 6;"
        );

        root.getChildren().addAll(dropZone, listActionBar, listView, optionBox, convertButton);

        return root;
    }
}
