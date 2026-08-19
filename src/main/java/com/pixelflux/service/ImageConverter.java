package com.pixelflux.service;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import com.pixelflux.util.Utils;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

public class ImageConverter implements MediaConverter {

    @Override
    public File convert(MediaFile mediaFile, ConvertOptions options) throws IOException {
        if(!mediaFile.isImage()) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다: " + mediaFile.name());
        }

        File targetDir = (options.outputDirectory() != null) ? options.outputDirectory() : mediaFile.file().getParentFile();

        if(targetDir != null && !targetDir.exists()) {
            targetDir.mkdir();
        }

        File outputFile = Utils.generateOutputFile(mediaFile, options);
        Thumbnails.Builder<File> builder = Thumbnails.of(mediaFile.file());

        if(options.targetWidth() != null && options.targetWidth() > 0) {
            builder.width(options.targetWidth()).keepAspectRatio(true);
        }else {
            builder.scale(1.0);
        }

        builder.outputFormat(options.targetFormat())
            .outputQuality(options.quality())
            .toFile(outputFile);

        return outputFile;
    }
}
