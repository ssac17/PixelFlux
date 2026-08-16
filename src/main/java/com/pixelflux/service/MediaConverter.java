package com.pixelflux.service;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;

import java.io.File;
import java.io.IOException;

public interface MediaConverter {
    public File convert(MediaFile mediaFile, ConvertOptions options) throws IOException;
}
