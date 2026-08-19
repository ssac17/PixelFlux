package com.pixelflux.controller;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import com.pixelflux.service.ImageConverter;
import com.pixelflux.service.MediaConverter;
import com.pixelflux.service.VideoConverter;
import com.pixelflux.util.Utils;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    private void handleAddFiles() {
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
            if(!mediaFiles.contains(media)) {
                mediaFiles.add(media);
                // 화면 ListView에도 추가 (파일명 + 용량 표시)
                mainView.getListView().getItems().add(media.name() + " (" + (media.formattedSize() + ")"));
            }
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

    private void handleClearList() {
        mainView.getStatusLabel().setText("");
        mediaFiles.clear();
        mainView.getListView().getItems().clear();
    }

    private void handleConvert() {
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

        //변환 병렬 처리
        new Thread(() -> {
            //스레드 수 지정
            int threadCount = Math.min(fileCount, Runtime.getRuntime().availableProcessors());
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            AtomicInteger completedCount = new AtomicInteger(0); // 💡 프로그레스 바 계산용

            for (MediaFile mediaFile : mediaFiles) {
                executor.submit(() -> {
                    MediaConverter converter = findConverter(mediaFile);
                    if (converter == null) {
                        failCount.incrementAndGet();
                    } else {
                        try {
                            converter.convert(mediaFile, options);
                            successCount.incrementAndGet();
                        } catch (IOException e) {
                            failCount.incrementAndGet();
                        }
                    }

                    //완료된 총 개수로 진행률 계산
                    int currentCompleted = completedCount.incrementAndGet();
                    double progress = (double) currentCompleted / fileCount;
                    int percent = (int) Math.round(progress * 100);

                    Platform.runLater(() -> {
                        progressBar.setProgress(progress);
                        progressLabel.setText(percent + "%");
                    });
                });
            }

            //모든 스레드가 끝날 때까지 대기
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            int finalSuccess = successCount.get();
            int finalFail = failCount.get();
            Platform.runLater(() -> {
                mainView.getConvertButton().setDisable(false);
                mainView.getStatusLabel().setText(
                        String.format("완료 (성공: %d건, 실패: %d건)", finalSuccess, finalFail)
                );
                if (finalSuccess > 0) {
                    Utils.openDirectory(mediaFiles.getFirst().file().getParentFile());
                }
            });
        }).start();
    }

    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }

    private MediaConverter findConverter(MediaFile mediaFile) {
        return converters.stream()
                .filter(converter ->
                        (mediaFile.isImage() && converter instanceof ImageConverter) ||
                        (mediaFile.isVideo() && converter instanceof VideoConverter))
                .findFirst()
                .orElse(null);
    }
}
