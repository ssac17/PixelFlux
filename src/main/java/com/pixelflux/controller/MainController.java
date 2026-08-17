package com.pixelflux.controller;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import com.pixelflux.service.ImageConverter;
import com.pixelflux.service.MediaConverter;
import com.pixelflux.service.VideoConverter;
import com.pixelflux.view.MainView;
import javafx.stage.FileChooser;

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
            System.out.println("변환할 파일이 없습니다.");
            return;
        }

        String format = mainView.getFormatComboBox().getValue();
        String width = mainView.getWidthComboBox().getValue();
        String quality = mainView.getQualityComboBox().getValue();
        ConvertOptions options = ConvertOptions.of(format, width, quality, null);

        int successCount = 0;
        int failCount = 0;

        for (MediaFile mediaFile : mediaFiles) {
            MediaConverter converter = findConverter(mediaFile);
            if(converter == null) {
                failCount++;
                System.out.println("지원하는 변환기가 아닙니다: " + mediaFile.name());
                continue;
            }

            try {
                converter.convert(mediaFile, options);
                successCount++;
            } catch (IOException e) {
                failCount++;
            }
        }
        mainView.getStatusLabel().setText(
                String.format("완료 (성공: %d건, 실패: %d건)", successCount, failCount)
        );
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
