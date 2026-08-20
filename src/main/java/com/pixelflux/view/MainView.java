package com.pixelflux.view;

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

    private Label dropZone;
    private Label statusLabel;
    private ListView<String> listView;
    private ComboBox<String> formatComboBox;
    private ComboBox<String> widthComboBox;
    private ComboBox<String> qualityComboBox;
    private Button convertButton;
    private Button exitButton;
    private Button addFileButton;
    private Button clearListButton;
    private Button selectFolderButton;
    private Label savePathLabel;
    private ProgressBar progressBar;
    private Label progressLabel;
    private StackPane progressContainer;
    private ContextMenu contextMenu;
    private MenuItem deleteMenuItem;

    public Parent createContent() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(24));

        // CSS 파일 로드
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/mainview.css")).toExternalForm());

        // 드래그 앤 드롭 영역
        dropZone = new Label("📥 변환할 파일(이미지/동영상)을 여기에 끌어다 놓으세요\n(또는 클릭하여 Finder에서 파일 선택)");
        dropZone.setMaxWidth(Double.MAX_VALUE);
        dropZone.setMinHeight(140);
        dropZone.setAlignment(Pos.CENTER);
        dropZone.getStyleClass().add("drop-zone");

        //저장 위치 폴더 선택

        HBox savePathBar = new HBox(8);
        savePathBar.setAlignment(Pos.CENTER_LEFT);
        // 좌측 긴 회색 박스
        savePathLabel = new Label("기본값: 원본 파일과 동일한 위치에 저장");
        savePathLabel.setMinHeight(36);
        savePathLabel.setMaxWidth(Double.MAX_VALUE);
        savePathLabel.getStyleClass().add("path-display-box");
        HBox.setHgrow(savePathLabel, Priority.ALWAYS);
        // 맨 우측 폴더 선택 버튼
        selectFolderButton = new Button("📂저장 폴더 선택");
        selectFolderButton.setMinHeight(36);
        selectFolderButton.getStyleClass().add("btn-secondary");

        savePathBar.getChildren().addAll(savePathLabel, selectFolderButton);

        // 파일 추가, 목록 비우기 액션 바
        HBox listActionBar = new HBox(12);
        listActionBar.setAlignment(Pos.CENTER_LEFT);

        addFileButton = new Button("📁 파일 추가 (Finder)");
        addFileButton.setMinHeight(36);
        addFileButton.getStyleClass().add("btn-secondary");

        clearListButton = new Button("🗑️ 목록 비우기");
        clearListButton.setMinHeight(36);
        clearListButton.getStyleClass().add("btn-danger-outline");

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        listActionBar.getChildren().addAll(addFileButton, clearListButton, statusLabel);

        // 3. 파일 목록 표시 영역 (CellFactory 제거 -> CSS로 폰트 및 패딩 제어)
        listView = new ListView<>();
        VBox.setVgrow(listView, Priority.ALWAYS);

        // 우클릭 컨텍스트 메뉴
        contextMenu = new ContextMenu();
        deleteMenuItem = new MenuItem("🗑️ 목록에서 제거");
        deleteMenuItem.getStyleClass().add("menu-item-delete");
        contextMenu.getItems().add(deleteMenuItem);
        listView.setContextMenu(contextMenu);

        // 4. 변환 옵션 바
        HBox optionBox = new HBox(16);
        optionBox.setAlignment(Pos.CENTER_LEFT);
        optionBox.getStyleClass().add("option-box");

        Label formatLabel = new Label("포맷:");
        formatLabel.getStyleClass().add("option-label");
        formatComboBox = new ComboBox<>();
        formatComboBox.getItems().addAll("JPG", "PNG", "WEBP", "MP4");
        formatComboBox.setValue("JPG");
        formatComboBox.getStyleClass().add("option-combo");

        Label widthLabel = new Label("해상도:");
        widthLabel.getStyleClass().add("option-label");
        widthComboBox = new ComboBox<>();
        widthComboBox.getItems().addAll("원본 유지", "1920 (FHD)", "1280 (HD)", "800");
        widthComboBox.setValue("1280 (HD)");
        widthComboBox.getStyleClass().add("option-combo");

        Label qualtiyLabel = new Label("품질:");
        qualtiyLabel.getStyleClass().add("option-label");
        qualityComboBox = new ComboBox<>();
        qualityComboBox.getItems().addAll("100% (최상)", "80% (권장)", "60% (압축)");
        qualityComboBox.setValue("80% (권장)");
        qualityComboBox.getStyleClass().add("option-combo");

        optionBox.getChildren().addAll(
                formatLabel, formatComboBox,
                widthLabel, widthComboBox,
                qualtiyLabel, qualityComboBox
        );

        // 5. 프로그레스 바
        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setMinHeight(20);
        progressBar.getStyleClass().add("custom-progress-bar");

        progressLabel = new Label("0%");
        progressLabel.getStyleClass().add("progress-text");

        progressContainer = new StackPane(progressBar, progressLabel);
        progressContainer.setMaxWidth(Double.MAX_VALUE);
        progressContainer.setVisible(false);

        // 6. 하단 변환 및 종료 버튼
        HBox bottomActionBar = new HBox(12);
        bottomActionBar.setAlignment(Pos.CENTER);

        convertButton = new Button("변환 시작");
        convertButton.setMaxWidth(Double.MAX_VALUE);
        convertButton.setMaxHeight(44);
        convertButton.getStyleClass().add("btn-primary");
        HBox.setHgrow(convertButton, Priority.ALWAYS);

        exitButton = new Button("종료");
        exitButton.setMinWidth(90);
        exitButton.setMinHeight(44);
        exitButton.getStyleClass().add("btn-exit");

        bottomActionBar.getChildren().addAll(convertButton, exitButton);

        root.getChildren().addAll(dropZone,savePathBar, listActionBar, listView, optionBox, progressContainer, bottomActionBar);
        return root;
    }

    // getter
    public Label getDropZone() { return dropZone; }
    public Label getStatusLabel() { return statusLabel; }
    public ListView<String> getListView() { return listView; }
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
}