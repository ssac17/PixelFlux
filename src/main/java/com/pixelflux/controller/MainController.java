package com.pixelflux.controller;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import com.pixelflux.service.ImageConverter;
import com.pixelflux.view.MainView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    private final ImageConverter imageConverter;
    private final MainView mainView;
    private final List<MediaFile> mediaFiles;

    public MainController(ImageConverter imageConverter, MainView mainView) {
        this.imageConverter = imageConverter;
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
    }

    public void handleAddFiles() {
        System.out.println(">>> 파일 추가 버튼 클릭됨!");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("변환할 이미지/동영상 선택");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Media Files", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.mp4", "*.mov");
        fileChooser.getExtensionFilters().add(filter);

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(mainView.getDropZone().getScene().getWindow());
        if(selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File file : selectedFiles) {
                MediaFile media = MediaFile.from(file);
                mediaFiles.add(media);
                // 화면 ListView에도 추가 (파일명 + 용량 표시)
                mainView.getListView().getItems().add(
                        media.name() + " (" + (media.size() / 1024) + " KB)"
                );
            }
        }
    }

    public void handleClearList() {
        mediaFiles.clear();
        mainView.getListView().getItems().clear();
    }

    public void handleConvert() {
        if(mediaFiles.isEmpty()) {
            System.out.println("변환할 파일이 없습니다.");
            return;
        }

        String format = mainView.getFormatComboBox().getValue();
        String width = mainView.getWidthComboBox().getValue();
        String quality = mainView.getQualityComboBox().getValue();
        ConvertOptions options = ConvertOptions.of(format, width, quality, null);

        for (MediaFile mediaFile : mediaFiles) {
            try {
                File convertedImage = imageConverter.convert(mediaFile, options);
                System.out.println("이미지 변환 성공, " + convertedImage.getName());
            } catch (IOException e) {
                System.err.println("변환 실패 (" + mediaFile.name() + "): " + e.getMessage());
            }
        }
    }
 }
