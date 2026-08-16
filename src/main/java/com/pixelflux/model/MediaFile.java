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

    public static MediaFile from(File file) {
        String name = file.getName();
        long size = file.length();

        int dotIndex = name.lastIndexOf('.');
        String ext = (dotIndex > 0 && dotIndex < name.length() - 1)
                ? name.substring(dotIndex + 1).toLowerCase()
                : "";

        return new MediaFile(file, name, size, ext);
    }

    public String formattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024L * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
