package com.pixelflux.service;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import com.pixelflux.util.Utils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.io.IOException;

public class VideoConverter implements  MediaConverter {

    @Override
    public File convert(MediaFile mediaFile, ConvertOptions options) throws IOException {
        if(!mediaFile.isVideo()) {
            throw new IllegalArgumentException("비디오 파일이 아닙니다: " + mediaFile.name());
        }
        File targetDir = (options.outputDirectory() != null) ? options.outputDirectory() : mediaFile.file().getParentFile();

        if(targetDir != null && !targetDir.exists()) {
            targetDir.mkdir();
        }
        File outputFile = Utils.generateOutputFile(mediaFile, options);

        //원본 동영상
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(mediaFile.file())) {
            grabber.start();
            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();

            //가로,세로 비율 유지
            int targetWidth = width;
            int targetHeight = height;
            if(options.targetWidth() != null && options.targetWidth() < height) {
                targetWidth = options.targetWidth();
                targetHeight = (int) Math.round((double) (height * targetWidth) / width);
            }
            if(targetWidth % 2 != 0) targetWidth--;
            if(targetHeight % 2 != 0) targetHeight--;

            //인코더 설정
            try(FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                    outputFile, targetWidth, targetHeight, grabber.getAudioChannels())) {
                recorder.setFormat("mp4");
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                recorder.setFrameRate(grabber.getFrameRate() > 0 ? grabber.getFrameRate() : 30.0);
                recorder.setSampleRate(grabber.getSampleRate());

                // CRF 화질 매핑 (0.6 ~ 1.0 -> 28 ~ 18)
                double quality = (options.quality() > 0) ? options.quality() : 0.8;
                int crf = (int) Math.round(28 - (quality - 0.6) * 25);
                crf = Math.max(18, Math.min(28, crf));

                recorder.setVideoOption("crf",String.valueOf(crf));
                recorder.setVideoOption("preset", "fast");
                recorder.start();

                //프레임 순회 및 저장
                Frame frame;
                while ((frame = grabber.grab()) != null) {
                    recorder.record(frame);
                }
                recorder.stop();
            }
            grabber.stop();
        }catch (Exception e) {
            System.out.println("동영상 병환 중 에러 발생!, " + mediaFile.file());
        }
        return outputFile;
    }
}
