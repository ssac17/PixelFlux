package com.pixelflux.util;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class Utils {

    public static void openDirectory(File directory) {
        if(directory == null || !directory.exists() || !directory.isDirectory()) {
            return;

        }

        if(Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if(desktop.isSupported(Desktop.Action.OPEN)) {
                try {
                    desktop.open(directory);
                } catch (IOException e) {
                    System.out.println("폴더 열기 실패: " + e.getMessage());
                }
            }
        }
    }

    public static File generateOutputFile(MediaFile mediaFile, ConvertOptions options) {
        //저장할 부모 폴더 결정 (사용자 지정 폴더 or 원본 파일의 부모 폴더)
        File fileDirectory = mediaFile.file().getParentFile();
        File parentDir = (options.outputDirectory() != null)
                ? options.outputDirectory()
                : fileDirectory;

        String fileName = mediaFile.name();
        int dotIndex = fileName.lastIndexOf(".");
        String baseName = (dotIndex != -1) ? fileName.substring(0, dotIndex) : fileName;

        String targetExt = options.targetFormat().toLowerCase();
        String newExtension = targetExt.startsWith(".") ? targetExt.substring(1) : targetExt;

        String initName = baseName + "_converted." + newExtension;
        File newFile = new File(parentDir, initName);

        //변환 파일 이름 중복시 처리
        int duplicatedCount = 1;
        while (newFile.exists()) {
            String candidateName = baseName + "_converted (" + duplicatedCount + ")." + newExtension;
            newFile = new File(parentDir, candidateName);
            duplicatedCount++;
        }
        return newFile;
    }

    public static void transferFrames(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder) throws IOException {
        Frame frame;
        // 비디오/이미지 프레임 순차 복사
        while ((frame = grabber.grab()) != null) {
            recorder.record(frame);
        }
        recorder.stop();
    }
}
