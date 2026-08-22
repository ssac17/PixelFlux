package com.pixelflux.service;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import com.pixelflux.util.Utils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.io.File;
import java.io.IOException;

public class GifConverter implements MediaConverter {
    @Override
    public File convert(MediaFile mediaFile, ConvertOptions options) throws IOException {
        if(!mediaFile.isVideo()) {
            throw new IllegalArgumentException("비디오 파일이 아닙니다: " + mediaFile.name());
        }

        File targetDir = (options.outputDirectory() != null) ? options.outputDirectory() : mediaFile.file().getParentFile();

        if(targetDir != null && !targetDir.exists()) {
            targetDir.mkdirs();
        }

        File outputFile = Utils.generateOutputFile(mediaFile, options);

        try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(mediaFile.file())) {
            grabber.start();

            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            double sourceFps = grabber.getFrameRate();

            //가로,세로 비율 유지, 짝수 보정
            int targetWidth = width;
            int targetHeight = height;
            if (options.targetWidth() != null && options.targetWidth() < width) {
                targetWidth = options.targetWidth();
                targetHeight = (int) Math.round((double) (height * targetWidth) / width);
            }
            if(targetWidth % 2 != 0) targetWidth--;
            if(targetHeight % 2 != 0) targetHeight--;

            // FPS 설정 (옵션 값 기준, 원본 FPS가 더 낮다면 원본에 맞춤)
            double targetFps = (options.fps() != null && options.fps() > 0) ? options.fps() : 15.0;
            if (sourceFps > 0 && targetFps > sourceFps) {
                targetFps = sourceFps;
            }

            try(FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, targetWidth, targetHeight)) {
                recorder.setFormat("gif");
                recorder.setAudioChannels(0); //오디오 없음
                recorder.setFrameRate(targetFps);

                // 고화질 256색 팔레트 필터 세팅
                boolean isHighQuality = options.quality() >= 1.0;
                String ditherAlgorithm = isHighQuality ? "lanczos" : "bayer";
                String filter = String.format("fps=%.0f,scale=%d:%d:flags=%s,split[s0][s1];[s0]palettegen=max_colors=256[p];[s1][p]paletteuse=dither=%s",
                        targetFps, targetWidth, targetHeight, ditherAlgorithm, ditherAlgorithm);

                recorder.setVideoOption("vf", filter);
                recorder.start();

                //프레임 순회 및 저장
                Utils.transferFrames(grabber, recorder);
            }

        }catch (Exception e) {
            System.out.println("gif 변환 중 에러 발생!, " + mediaFile.file());
            e.printStackTrace(System.out);
;           throw new IOException(e);
        }
        return outputFile;
    }
}
