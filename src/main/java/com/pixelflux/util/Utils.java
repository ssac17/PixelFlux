package com.pixelflux.util;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;

import java.io.File;

public class Utils {
    public  static File generateOutputFile(MediaFile mediaFile, ConvertOptions options) {
        String fileName = mediaFile.name();
        int dotIndex = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, dotIndex);
        String newExtension = options.targetFormat().toLowerCase();
        String newFileName = fileName + "_converted." + newExtension;
        return new File(fileName, newFileName);
    }
}
