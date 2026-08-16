package com.pixelflux.service;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;

import java.io.File;
import java.io.IOException;

public class VideoConverter implements  MediaConverter {

    @Override
    public File convert(MediaFile mediaFile, ConvertOptions options) throws IOException {
        return null;
    }
}
