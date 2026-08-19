package com.pixelflux.controller;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import com.pixelflux.service.ImageConverter;
import com.pixelflux.service.MediaConverter;
import com.pixelflux.service.VideoConverter;
import com.pixelflux.view.MainView;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    private final List<MediaConverter> converters;
    private final MainView mainView;
    private final List<MediaFile> mediaFiles;

    public MainController(List<MediaConverter> converters, MainView mainView) {
        this.converters = converters;
        this.mainView = mainView;
        this.mediaFiles = new ArrayList<>();
        initEventHandlers();
    }

    public void initEventHandlers() {
        // 버튼 클릭 이벤트 연결
        mainView.getAddFileButton().setOnAction(e -> handleAddFiles());
        mainView.getDropZone().setOnMouseClicked(e -> handleAddFiles());
        mainView.getClearListButton().setOnAction(e -> handleClearList());
        mainView.getConvertButton().setOnAction(e -> handleConvert());
        mainView.getExitButton().setOnAction(e -> handleExit());
    }

    public void handleAddFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("변환할 이미지/동영상 선택");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Media Files", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.mp4", "*.mov");
        fileChooser.getExtensionFilters().add(filter);

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(mainView.getDropZone().getScene().getWindow());
        if(selectedFiles == null || selectedFiles.isEmpty()) {
            return;
        }

        String statusMsg = "";
        for (File file : selectedFiles) {
            MediaFile media = MediaFile.from(file);
            mediaFiles.add(media);
            // 화면 ListView에도 추가 (파일명 + 용량 표시)
            mainView.getListView().getItems().add(media.name() + " (" + (media.formattedSize() + ")"));
        }

        int mainViewSize = mainView.getListView().getItems().size();
        String firstFileName = mainView.getListView().getItems().getFirst();
        int index = firstFileName.indexOf("(");
        firstFileName = firstFileName.substring(0, index);
        if(mainViewSize == 1) {
            statusMsg = firstFileName;
        } else {
            statusMsg = firstFileName + "    포함: " + mainViewSize;
        }
        mainView.getStatusLabel().setText(statusMsg);
    }

    public void handleClearList() {
        mainView.getStatusLabel().setText("");
        mediaFiles.clear();
        mainView.getListView().getItems().clear();
    }

    public void handleConvert() {
        if(mediaFiles.isEmpty()) {
            mainView.getStatusLabel().setText("변환할 파일이 없습니다.");
            return;
        }

        String format = mainView.getFormatComboBox().getValue();
        String width = mainView.getWidthComboBox().getValue();
        String quality = mainView.getQualityComboBox().getValue();
        ConvertOptions options = ConvertOptions.of(format, width, quality, null);

        //프로그레스 바 추가
        mainView.getProgressContainer().setVisible(true);
        ProgressBar progressBar = mainView.getProgressBar();
        Label progressLabel = mainView.getProgressLabel();

        progressBar.setProgress(0.0);
        progressLabel.setText("0%");
        mainView.getConvertButton().setDisable(true);
        int fileCount = mediaFiles.size();

        //프로그레스바 구현을 위한 스레드 처리
        new Thread(() -> {
            int successCount = 0;
            int failCount = 0;
            File outputFile = null;
            for (int i = 0; i < fileCount; i++) {
                MediaFile mediaFile = mediaFiles.get(i);
                MediaConverter converter = findConverter(mediaFile);
                if(converter == null) {
                    System.out.println("지원하는 변환기가 아닙니다: " + mediaFile.name());
                    failCount++;
                    continue;
                }
                try {
                    outputFile = converter.convert(mediaFile, options);
                    successCount++;
                } catch (IOException e) {
                    failCount++;
                }
                double progress = (double) (i + 1) / fileCount;
                int percent = (int) Math.round(progress * 100);
                Platform.runLater(() -> {
                    progressBar.setProgress(progress);
                    progressLabel.setText(percent + "%");
                });
            }

            int finalSuccess = successCount;
            int finalFail = failCount;
            Platform.runLater(() -> {
                mainView.getConvertButton().setDisable(false);
                mainView.getStatusLabel().setText(
                        String.format("완료 (성공: %d건, 실패: %d건)", finalSuccess, finalFail)
                );
                if(finalSuccess > 0) {
                    openDirectory(mediaFiles.getFirst().file().getParentFile());
                }
            });
        }).start();
    }

    private MediaConverter findConverter(MediaFile mediaFile) {
        return converters.stream()
                .filter(converter ->
                        (mediaFile.isImage() && converter instanceof ImageConverter) ||
                        (mediaFile.isVideo() && converter instanceof VideoConverter))
                .findFirst()
                .orElse(null);
    }

    private static void openDirectory(File directory) {
        if(directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }
        if(Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if(desktop.isSupported(Desktop.Action.OPEN)) {
                try {
                    desktop.open(directory);
                } catch (IOException e) {
                    System.out.println("폴더 열기 실패: " + e.getMessage());
                }
            }
        }
    }

    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }
}
