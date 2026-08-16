package com.pixelflux.controller;

import com.pixelflux.model.MediaFile;
import com.pixelflux.service.ImageConverter;
import com.pixelflux.view.MainView;

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
    }

    public void initEventHandlers() {

    }

    public void handleClearList() {
        mediaFiles.clear();
    }
}
