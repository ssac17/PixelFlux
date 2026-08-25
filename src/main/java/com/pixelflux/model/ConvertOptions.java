package com.pixelflux.model;

import java.io.File;

public record ConvertOptions (
    String targetFormat,
    Integer targetWidth,
    double quality,
    Integer fps,
    File outputDirectory
) {
    public static ConvertOptions of(String formatStr, String widthStr, String qualityStr, File outputDir) {
        //=포맷 소문자 변환 ("JPG" -> "jpg")
        String format = (formatStr != null) ? formatStr.trim().toLowerCase() : "jpg";

        //가로폭 파싱 ("원본 유지" -> null, "1280 (HD)" -> 1280)
        Integer width = null;
        if (widthStr != null && !widthStr.contains("원본")) {
            String numOnly = widthStr.replaceAll("[^0-9]", "");
            if (!numOnly.isEmpty()) {
                width = Integer.parseInt(numOnly);
            }
        }

        //품질 파싱 ("80% (권장)" -> 0.8)
        double quality = 0.8;
        if (qualityStr != null) {
            String numOnly = qualityStr.replaceAll("[^0-9]", "");
            if (!numOnly.isEmpty()) {
                quality = Double.parseDouble(numOnly) / 100.0;
            }
        }
        return new ConvertOptions(format, width, quality, null, outputDir);
    }

    public static ConvertOptions ofGif(String fpsStr, String widthStr, String qualityStr, File outputDir) {
        //확장자는 gif로 고정
        String format = "gif";
        //FPS 파싱 ("15 FPS (표준 권장)" -> 15, 미선택 시 기본값 15)
        Integer fps = 15;
        if(fpsStr != null) {
            String numOnly = fpsStr.replaceAll("[^0-9]", "");
            if (!numOnly.isEmpty()) {
                fps = Integer.parseInt(numOnly);
            }
        }

        //가로폭 파싱, null -> 원본 오쥬, 권장 -> 480px
        Integer width = null;
        if(widthStr != null && !widthStr.contains("원본")) {
            String numOnly = widthStr.replaceAll("[^0-9]", "");
            if(!numOnly.isEmpty()) {
                width = Integer.parseInt(numOnly);
            }
        }
        double quality = (qualityStr != null && qualityStr.contains("고화질")) ? 1.0 : 0.8;
        return new ConvertOptions(format, width, quality, fps, outputDir);
    }
}
