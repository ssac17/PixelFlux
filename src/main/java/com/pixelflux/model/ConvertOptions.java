package com.pixelflux.model;

import java.io.File;

public record ConvertOptions (
    String targetFormat,
    Integer targetWidth,
    double quality,
    File outputDirectory
) {
    public static ConvertOptions of(String formatStr, String widthStr, String qualityStr, File outputDir) {
        // 1. 포맷 소문자 변환 ("JPG" -> "jpg")
        String format = (formatStr != null) ? formatStr.trim().toLowerCase() : "jpg";

        // 2. 가로폭 파싱 ("원본 유지" -> null, "1280 (HD)" -> 1280)
        Integer width = null;
        if (widthStr != null && !widthStr.contains("원본")) {
            String numOnly = widthStr.replaceAll("[^0-9]", "");
            if (!numOnly.isEmpty()) {
                width = Integer.parseInt(numOnly);
            }
        }

        // 3. 품질 파싱 ("80% (권장)" -> 0.8)
        double quality = 0.8;
        if (qualityStr != null) {
            String numOnly = qualityStr.replaceAll("[^0-9]", "");
            if (!numOnly.isEmpty()) {
                quality = Double.parseDouble(numOnly) / 100.0;
            }
        }

        return new ConvertOptions(format, width, quality, outputDir);
    }
}
