package com.pixelflux.util;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;

import java.io.File;

public class Utils {
    public  static File generateOutputFile(MediaFile mediaFile, ConvertOptions options) {
        // 1. 저장할 부모 폴더 결정 (사용자 지정 폴더 or 원본 파일의 부모 폴더)
        File parentDir = (options.outputDirectory() != null)
                ? options.outputDirectory()
                : mediaFile.file().getParentFile();

        String fileName = mediaFile.name();
        int dotIndex = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, dotIndex);
        String newExtension = options.targetFormat().toLowerCase();
        if(mediaFile.isVideo()) {
            newExtension = "mp4";
        }
        String newFileName = fileName + "_converted." + newExtension;
        return new File(parentDir, newFileName);
    }
}
