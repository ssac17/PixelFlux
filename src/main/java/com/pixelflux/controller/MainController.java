package com.pixelflux.controller;

import com.pixelflux.model.MediaFile;
import com.pixelflux.service.ImageConverter;
import com.pixelflux.view.MainView;
import javafx.stage.FileChooser;

import java.io.File;
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
    }

    public void handleAddFiles() {
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
}
