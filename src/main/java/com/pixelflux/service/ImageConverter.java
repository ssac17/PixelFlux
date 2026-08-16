package com.pixelflux.service;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

public class ImageConverter {
    public File convert(MediaFile mediaFile, ConvertOptions options) throws IOException {
        if(!mediaFile.isImage()) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다: " + mediaFile.name());
        }

        File targetDir = (options.outputDirectory() != null) ? options.outputDirectory() : mediaFile.file().getParentFile();

        if(targetDir != null && !targetDir.exists()) {
            targetDir.mkdir();
        }

        String imageName = mediaFile.name();
        int dotIndex = imageName.lastIndexOf(".");
        imageName = imageName.substring(0, dotIndex);
        String newExtension = options.targetFormat().toLowerCase();
        String newFileName = imageName + "_converted." + newExtension;

        File outputFile = new File(targetDir, newFileName);
        Thumbnails.Builder<File> builder = Thumbnails.of(mediaFile.file());

        if(options.targetWidth() != null && options.targetWidth() > 0) {
            builder.width(options.targetWidth()).keepAspectRatio(true);
        }else {
            builder.scale(1.0);
        }
        builder.outputFormat(newExtension)
            .outputQuality(options.quality())
            .toFile(outputFile);

        return outputFile;
    }
}
