package com.pixelflux.view;

import com.pixelflux.model.MediaFile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

//레이아웃 구성
public class MainView {

    private Label dropZone; //파일 끌어 놓기 영역
    private Label statusLabel;
    //파일 목록 표시할 영역
    private ListView<String> listView;

    private ComboBox<String> formatComboBox;
    private ComboBox<String> widthComboBox;
    private  ComboBox<String> qualityComboBox;
    private Button convertButton;
    private Button addFileButton;
    private Button clearListButton;

    private ProgressBar progressBar;
    private Label progressLabel;
    private StackPane progressContainer;

    public Parent createContent() {
        VBox root = new VBox(18); //간격 15
        root.setPadding(new Insets(24)); // padding 20px
        //배경
        root.setStyle("-fx-background-color: #f8fafc;");

        // 1. 드래그 앤 드롭 영역
        dropZone = new Label("📥 변환할 파일(이미지/동영상)을 여기에 끌어다 놓으세요\n(또는 클릭하여 Finder에서 파일 선택)");
        dropZone.setMaxWidth(Double.MAX_VALUE);
        dropZone.setMinHeight(140);
        dropZone.setAlignment(Pos.CENTER); //중앙으로 정렬
        dropZone.setStyle(
                "-fx-border-color: #cbd5e1; -fx-border-width: 2.5; -fx-border-style: dashed; -fx-border-radius: 12; " +
                "-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-font-size: 15px; -fx-font-weight: 500; " +
                "-fx-text-fill: #64748b; -fx-text-alignment: center; -fx-line-spacing: 6; -fx-cursor: hand;"
        );
        //파일 추가, 목록 비우기
        HBox listActionBar = new HBox(12);
        listActionBar.setAlignment(Pos.CENTER_LEFT);
        addFileButton = new Button("📁 파일 추가 (Finder)");
        addFileButton.setMinHeight(36);
        addFileButton.setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6;" +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #334155; -fx-padding: 6 14; -fx-cursor: hand;"
        );
        clearListButton = new Button("🗑️ 목록 비우기");
        clearListButton.setMinHeight(36);
        clearListButton.setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6;-fx-background-radius: 6;" +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ef4444; -fx-padding: 6 14; -fx-cursor: hand;"
        );

        //메시지 창
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569; -fx-font-weight: bold;" +
                " -fx-font-family: 'Apple SD Gothic Neo', sans-serif;");

        listActionBar.getChildren().addAll(addFileButton, clearListButton, statusLabel);

        // 2. 파일 목록 표시 영역

        listView = new ListView<>();
        listView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // 빈 셀 초기화
                } else {
                    setText(item);

                    // 💡 셀 자체에 직접 스타일 주입
                    setStyle(
                            "-fx-text-fill: #111827; -fx-font-weight: bold; -fx-font-size: 14px; " +
                            "-fx-font-family: 'Apple SD Gothic Neo', sans-serif;"
                    );
                }
            }
        });

        //가로 패널
        VBox.setVgrow(listView, Priority.ALWAYS);
        //세로 패널
        HBox optionBox = new HBox(16);
        optionBox.setAlignment(Pos.CENTER_LEFT);
        optionBox.setStyle("-fx-padding: 4 0;");

        // 공통 라벨 스타일
        String labelStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #334155;";
        String comboStyle = "-fx-font-size: 13px; -fx-min-height: 36px; -fx-pref-width: 140px;";

        //포맷
        Label formatLabel = new Label("포맷:");
        formatLabel.setStyle(labelStyle);
        formatComboBox = new ComboBox<>();
        formatComboBox.getItems().addAll("JPG", "PNG", "WEBP", "MP4");
        formatComboBox.setValue("JPG"); // 기본값
        formatComboBox.setStyle(comboStyle);

        //해상도
        Label widthLabel = new Label("해상도:");
        widthLabel.setStyle(labelStyle);
        widthComboBox = new ComboBox<>();
        widthComboBox.getItems().addAll("원본 유지", "1920 (FHD)", "1280 (HD)", "800");
        widthComboBox.setValue("1280 (HD)");
        widthComboBox.setStyle(comboStyle);

        //압축 품질
        Label qualtiyLabel = new Label("품질:");
        qualtiyLabel.setStyle(labelStyle);
        qualityComboBox = new ComboBox<>();
        qualityComboBox.getItems().addAll("100% (최상)", "80% (권장)", "60% (압축)");
        qualityComboBox.setValue("80% (권장)");
        qualityComboBox.setStyle(comboStyle);

        //option box 세팅
        optionBox.getChildren().addAll(
                formatLabel, formatComboBox,
                widthLabel, widthComboBox,
                qualtiyLabel, qualityComboBox
        );

        //변환 버튼
        convertButton = new Button("변환 시작");
        convertButton.setMaxWidth(Double.MAX_VALUE);
        convertButton.setMaxHeight(42);
        convertButton.setStyle(
                "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-cursor: hand;"
        );

        //진행률, 프로그레스바
        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setMinHeight(20);
        progressBar.setStyle(
                "-fx-accent: #38bdf8; " +
                "-fx-control-inner-background: #e0f2fe; " +
                "-fx-background-radius: 12; " +
                "-fx-padding: 0;"
        );
        progressLabel = new Label("0%");
        progressLabel.setStyle(
                "-fx-font-size: 13px; " +
                "-fx-font-weight: 800; " +
                "-fx-text-fill: #0c4a6e; " +
                "-fx-font-family: 'Apple SD Gothic Neo', sans-serif;"
        );
        progressContainer = new StackPane(progressBar, progressLabel);
        progressContainer.setMaxWidth(Double.MAX_VALUE);
        progressContainer.setVisible(false);

        root.getChildren().addAll(dropZone, listActionBar, listView, optionBox, progressContainer, convertButton);
        return root;
    }

    //getter
    public Label getDropZone() {return dropZone;}
    public Label getStatusLabel() {return statusLabel;}
    public ListView<String> getListView() {return listView;}
    public ComboBox<String> getFormatComboBox() {return formatComboBox;}
    public ComboBox<String> getWidthComboBox() {return widthComboBox;}
    public ComboBox<String> getQualityComboBox() {return qualityComboBox;}
    public Button getConvertButton() {return convertButton;}
    public Button getAddFileButton() {return addFileButton;}
    public Button getClearListButton() {return clearListButton;}
    public ProgressBar getProgressBar() {return progressBar;}
    public Label getProgressLabel() {return progressLabel;}
    public StackPane getProgressContainer() {return progressContainer;}
}
