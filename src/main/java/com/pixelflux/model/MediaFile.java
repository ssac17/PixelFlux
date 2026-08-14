package com.pixelflux.model;

import java.io.File;
import java.util.Set;

public record MediaFile(
    File file,
    String name,
    long size,
    String extension
) {
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "bmp");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "mov", "avi", "mkv", "webm");

    public boolean isImage() {
        return extension != null && IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    }

    public boolean isVideo() {
        return extension != null && VIDEO_EXTENSIONS.contains(extension.toLowerCase());
    }
    //todo: from 추후 추가 예정
}
