package com.pixelflux.view;

import com.pixelflux.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

// 레이아웃 구성
public class MainView {

    //영상/이미지 관련 필드
    private Label dropZone;
    private Label statusLabel;
    private ListView<String> listView;
    private HBox optionBox;
    private ComboBox<String> formatComboBox;
    private ComboBox<String> widthComboBox;
    private ComboBox<String> qualityComboBox;
    private Button convertButton;
    private Button exitButton;
    private Button addFileButton;
    private Button clearListButton;
    private Button selectFolderButton;
    private Label savePathLabel;
    private Label pathLabel;
    private ProgressBar progressBar;
    private Label progressLabel;
    private StackPane progressContainer;
    private ContextMenu contextMenu;
    private MenuItem deleteMenuItem;

    //gif 관련 필드
    private Label gifDropZone;
    private Button tabFormatBtn;
    private Button tabGifBtn;
    private Button gifAddFileButton;
    private Button gifClearButton;
    private Label gifStatusLabel;
    private ComboBox<String> gifFpsComboBox;
    private ComboBox<String> gifQualityComboBox;
    private ComboBox<String> gifWidthComboBox;
    private ListView<String> gifListView;
    private VBox formatViewRoot;
    private VBox gifViewRoot;

    //옵션창 길
    private final int LABEL_WIDTH = 42;

    public Parent createContent() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(20));

        //css 파일 로드
        root.getStylesheets().add(Objects.requireNonNull(Main.class.getClassLoader().getResource("css/mainview.css")).toExternalForm());

        //상단 탭 부분
        HBox tabBar = new HBox(0);
        tabBar.setAlignment(Pos.CENTER);
        tabBar.getStyleClass().add("tab-bar-container");

        tabFormatBtn = new Button("🔄 포맷 & 해상도 변환");
        tabFormatBtn.setMaxWidth(Double.MAX_VALUE);
        tabFormatBtn.getStyleClass().addAll("segment-tab-btn", "segment-tab-active");
        HBox.setHgrow(tabFormatBtn, Priority.ALWAYS);

        tabGifBtn = new Button("🎞️ 동영상 ➔ GIF 변환");
        tabGifBtn.setMaxWidth(Double.MAX_VALUE);
        tabGifBtn.getStyleClass().addAll("segment-tab-btn");
        HBox.setHgrow(tabGifBtn, Priority.ALWAYS);

        tabBar.getChildren().addAll(tabFormatBtn, tabGifBtn);

        //영상/이미지 변환 탭
        formatViewRoot = new VBox(12);

        // 일반 변환 탭
        dropZone = new Label("📥 변환할 파일(이미지/동영상)을 여기에 끌어다 놓으세요\n(또는 클릭하여 파일 선택 | JPG, PNG, WEBP, MP4 지원)");
        dropZone.setMaxWidth(Double.MAX_VALUE);
        dropZone.setMinHeight(110);
        dropZone.setAlignment(Pos.CENTER);
        dropZone.getStyleClass().add("drop-zone");

        //저장 경로 선택
        HBox savePathBar = new HBox(8);
        savePathBar.setAlignment(Pos.CENTER_LEFT);
        pathLabel = new Label("저장 경로:");
        pathLabel.setMinHeight(34);
        pathLabel.getStyleClass().add("option-label");
        savePathLabel = new Label("기본값: 원본 파일과 동일한 위치에 저장");
        savePathLabel.setMinHeight(34);
        savePathLabel.setMaxWidth(Double.MAX_VALUE);
        savePathLabel.getStyleClass().add("path-display-box");
        HBox.setHgrow(savePathLabel, Priority.ALWAYS);
        selectFolderButton = new Button("📂 저장 폴더 선택");
        selectFolderButton.setMinHeight(34);
        selectFolderButton.getStyleClass().add("btn-secondary");
        savePathBar.getChildren().addAll(pathLabel, savePathLabel, selectFolderButton);

        HBox gifSavePathBar = new HBox(8);
        gifSavePathBar.setAlignment(Pos.CENTER_LEFT);
        Label gifPathLabel = new Label("저장 경로:");
        gifPathLabel.setMinHeight(34);
        gifPathLabel.getStyleClass().add("option-label");

        Label gifSavePathLabel = new Label("기본값: 원본 파일과 동일한 위치에 저장");
        gifSavePathLabel.setMinHeight(34);
        gifSavePathLabel.setMaxWidth(Double.MAX_VALUE);
        gifSavePathLabel.getStyleClass().add("path-display-box");
        HBox.setHgrow(gifSavePathLabel, Priority.ALWAYS);
        gifSavePathLabel.textProperty().bind(savePathLabel.textProperty());

        Button gifSelectFolderButton = new Button("📂 저장 폴더 선택");
        gifSelectFolderButton.setMinHeight(34);
        gifSelectFolderButton.getStyleClass().add("btn-secondary");
        gifSelectFolderButton.setOnAction(e -> selectFolderButton.fire());

        gifSavePathBar.getChildren().addAll(gifPathLabel, gifSavePathLabel, gifSelectFolderButton);

        //파일 추가
        HBox listActionBar = new HBox(12);
        listActionBar.setAlignment(Pos.CENTER_LEFT);

        addFileButton = new Button("📁 파일 추가 (Finder)");
        addFileButton.setMinHeight(34);
        addFileButton.getStyleClass().add("btn-secondary");

        clearListButton = new Button("🗑️ 목록 비우기");
        clearListButton.setMinHeight(34);
        clearListButton.getStyleClass().add("btn-danger-outline");

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");
        listActionBar.getChildren().addAll(addFileButton, clearListButton, statusLabel);

        listView = new ListView<>();
        VBox.setVgrow(listView, Priority.ALWAYS);

        contextMenu = new ContextMenu();
        deleteMenuItem = new MenuItem("🗑️ 목록에서 제거");
        deleteMenuItem.getStyleClass().add("menu-item-delete");
        contextMenu.getItems().add(deleteMenuItem);
        listView.setContextMenu(contextMenu);

        //영상/이미지 옵션
        optionBox = new HBox(14);
        optionBox.setAlignment(Pos.CENTER_LEFT);
        optionBox.getStyleClass().add("option-box");

        //포맷
        Label formatLabel = new Label("포맷:");
        formatLabel.setMinWidth(LABEL_WIDTH);
        formatLabel.getStyleClass().add("option-label");
        formatComboBox = new ComboBox<>();
        formatComboBox.setMinWidth(130);
        formatComboBox.getItems().addAll("JPG", "PNG", "WEBP", "MP4");
        formatComboBox.setValue("JPG");
        formatComboBox.getStyleClass().add("option-combo");

        Label widthLabel = new Label("해상도:");
        widthLabel.setMinWidth(LABEL_WIDTH);
        widthLabel.getStyleClass().add("option-label");
        widthComboBox = new ComboBox<>();
        widthComboBox.setMinWidth(150);
        widthComboBox.getItems().addAll("원본 유지", "1920 (FHD)", "1280 (HD)", "800");
        widthComboBox.setValue("1280 (HD)");
        widthComboBox.getStyleClass().add("option-combo");

        Label qualityLabel = new Label("품질:");
        qualityLabel.setMinWidth(LABEL_WIDTH);
        qualityLabel.getStyleClass().add("option-label");
        qualityComboBox = new ComboBox<>();
        qualityComboBox.setMinWidth(170);
        qualityComboBox.getItems().addAll("100% (최상)", "80% (권장)", "60% (압축)");
        qualityComboBox.setValue("80% (권장)");
        qualityComboBox.getStyleClass().add("option-combo");

        optionBox.getChildren().addAll(
                formatLabel, formatComboBox,
                widthLabel, widthComboBox,
                qualityLabel, qualityComboBox
        );

        formatViewRoot.getChildren().addAll(dropZone,savePathBar, listActionBar, listView, optionBox);

        // git 변환 탭
        gifViewRoot = new VBox(12);
        gifDropZone = new Label("🎬 GIF로 변환할 동영상 파일을 여기에 끌어다 놓으세요\n(또는 클릭하여 파일 선택 | MP4, MOV, MKV, AVI 지원)");
        gifDropZone.setMaxWidth(Double.MAX_VALUE);
        gifDropZone.setMinHeight(110);
        gifDropZone.setAlignment(Pos.CENTER);
        gifDropZone.getStyleClass().addAll("drop-zone", "drop-zone-gif");

        HBox gifListActionBar = new HBox(12);
        gifListActionBar.setAlignment(Pos.CENTER_LEFT);

        gifAddFileButton = new Button("📁 영상 추가 (Finder)");
        gifAddFileButton.setMinHeight(34);
        gifAddFileButton.getStyleClass().add("btn-secondary");

        gifClearButton = new Button("🗑️ 목록 비우기");
        gifClearButton.setMinHeight(34);
        gifClearButton.getStyleClass().add("btn-danger-outline");

        gifStatusLabel = new Label("");
        gifStatusLabel.getStyleClass().add("status-label");
        gifListActionBar.getChildren().addAll(gifAddFileButton, gifClearButton, gifStatusLabel);

        gifListView = new ListView<>();
        VBox.setVgrow(gifListView, Priority.ALWAYS);

        HBox gifOptionBox = new HBox(14);
        gifOptionBox.setAlignment(Pos.CENTER_LEFT);
        gifOptionBox.getStyleClass().add("option-box");

        Label fpsLabel = new Label("FPS:");
        fpsLabel.setMinWidth(LABEL_WIDTH);
        fpsLabel.getStyleClass().add("option-label");
        gifFpsComboBox = new ComboBox<>();
        gifFpsComboBox.setMinWidth(130);
        gifFpsComboBox.getItems().addAll("10 FPS (경량)", "15 FPS (표준 권장)", "24 FPS (부드러움)");
        gifFpsComboBox.setValue("15 FPS (표준 권장)");
        gifFpsComboBox.getStyleClass().add("option-combo");

        Label gifWidthLabel = new Label("너비:");
        gifWidthLabel.setMinWidth(LABEL_WIDTH);
        gifWidthLabel.getStyleClass().add("option-label");
        gifWidthComboBox = new ComboBox<>();
        gifWidthComboBox.setMinWidth(150);
        gifWidthComboBox.getItems().addAll("원본 유지", "720px", "480px (웹용 권장)", "320px");
        gifWidthComboBox.setValue("480px (웹용 권장)");
        gifWidthComboBox.getStyleClass().add("option-combo");

        Label gifQualityLabel = new Label("화질:");
        gifQualityLabel.setMinWidth(LABEL_WIDTH);
        gifQualityLabel.getStyleClass().add("option-label");
        gifQualityComboBox = new ComboBox<>();
        gifQualityComboBox.setMinWidth(170);
        gifQualityComboBox.getItems().addAll("고화질 팔레트 (Lanczos)", "일반 팔레트 (Bayer)");
        gifQualityComboBox.setValue("고화질 팔레트 (Lanczos)");
        gifQualityComboBox.getStyleClass().add("option-combo");

        gifOptionBox.getChildren().addAll(
                fpsLabel, gifFpsComboBox,
                gifWidthLabel, gifWidthComboBox,
                gifQualityLabel, gifQualityComboBox
        );

        gifViewRoot.getChildren().addAll(gifDropZone, gifSavePathBar, gifListActionBar, gifListView, gifOptionBox);
        gifViewRoot.setVisible(false); // 초기에는 숨김

        //중앙 컨텐츠 영역 (StackPane)
        StackPane contentArea = new StackPane(formatViewRoot, gifViewRoot);
        VBox.setVgrow(contentArea, Priority.ALWAYS);


        //하단 공통 영역 (프로그레스, 실행 버튼)
        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setMinHeight(18);
        progressBar.getStyleClass().add("custom-progress-bar");

        progressLabel = new Label("0%");
        progressLabel.getStyleClass().add("progress-text");

        progressContainer = new StackPane(progressBar, progressLabel);
        progressContainer.setMaxWidth(Double.MAX_VALUE);

        HBox bottomActionBar = new HBox(12);
        bottomActionBar.setAlignment(Pos.CENTER);

        convertButton = new Button("변환 시작");
        convertButton.setMaxWidth(Double.MAX_VALUE);
        convertButton.setMinHeight(42);
        convertButton.getStyleClass().add("btn-primary");
        HBox.setHgrow(convertButton, Priority.ALWAYS);

        exitButton = new Button("종료");
        exitButton.setMinWidth(90);
        exitButton.setMinHeight(42);
        exitButton.getStyleClass().add("btn-exit");
        bottomActionBar.getChildren().addAll(convertButton, exitButton);

        root.getChildren().addAll(tabBar, contentArea, progressContainer, bottomActionBar);
        return root;
    }

    public void switchTab(boolean isGifTab) {
        formatViewRoot.setVisible(!isGifTab);
        gifViewRoot.setVisible(isGifTab);
        //css
        tabFormatBtn.getStyleClass().remove("segment-tab-active");
        tabGifBtn.getStyleClass().remove("segment-tab-active");
        if (isGifTab) {
            tabGifBtn.getStyleClass().add("segment-tab-active");
        } else {
            tabFormatBtn.getStyleClass().add("segment-tab-active");
        }
    }

    //상단 탭 getter
    public Button getTabFormatBtn() { return tabFormatBtn; }
    public Button getTabGifBtn() { return tabGifBtn; }

    //영상/이미지 getter
    public Label getDropZone() { return dropZone; }
    public Label getStatusLabel() { return statusLabel; }
    public ListView<String> getListView() { return listView; }
    public HBox getOptionBox() {return optionBox;}
    public ComboBox<String> getFormatComboBox() { return formatComboBox; }
    public ComboBox<String> getWidthComboBox() { return widthComboBox; }
    public ComboBox<String> getQualityComboBox() { return qualityComboBox; }
    public Button getConvertButton() { return convertButton; }
    public Button getExitButton() { return exitButton; }
    public Button getAddFileButton() { return addFileButton; }
    public Button getClearListButton() { return clearListButton; }
    public ProgressBar getProgressBar() { return progressBar; }
    public Label getProgressLabel() { return progressLabel; }
    public StackPane getProgressContainer() { return progressContainer; }
    public MenuItem getDeleteMenuItem() { return deleteMenuItem; }
    private ContextMenu getContextMenu() {return contextMenu;}
    public Button getSelectFolderButton() {return selectFolderButton;}
    public Label getSavePathLabel() {return savePathLabel;}

    //gif Getter
    public Label getGifDropZone() { return gifDropZone; }
    public Label getGifStatusLabel() { return gifStatusLabel; }
    public ListView<String> getGifListView() { return gifListView; }
    public Button getGifAddFileButton() { return gifAddFileButton; }
    public Button getGifClearButton() { return gifClearButton; }
    public ComboBox<String> getGifFpsComboBox() { return gifFpsComboBox; }
    public ComboBox<String> getGifWidthComboBox() { return gifWidthComboBox; }
    public ComboBox<String> getGifQualityComboBox() { return gifQualityComboBox; }
}