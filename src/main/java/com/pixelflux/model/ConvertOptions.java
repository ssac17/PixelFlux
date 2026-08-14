package com.pixelflux.model;

import java.io.File;

public record ConvertOptions (
    String targetFormat,
    Integer targetWidth,
    double quality,
    File outputDirectory
) {}
