package com.pixelflux.controller;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import com.pixelflux.service.ImageConverter;
import com.pixelflux.service.MediaConverter;
import com.pixelflux.service.VideoConverter;
import com.pixelflux.util.Utils;
import com.pixelflux.view.MainView;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MainController {

    private final MainView mainView;
    private final List<MediaFile> mediaFiles;
    private File targetDirectory = null;

    private ImageConverter imageConverter;
    private VideoConverter videoConverter;

    public MainController(MainView mainView) {
        this.mainView = mainView;
        this.mediaFiles = new ArrayList<>();
        initEventHandlers();
    }

    public void initEventHandlers() {
        setupDeleteKeyEvent();                                                                 //파일 삭제, key이벤트 추가
        DragAndDropAddFiles();                                                                 //드래그 앤 드랍으로 파일 추가
        /* 버튼 클릭 이벤트 연결 */
        mainView.getSelectFolderButton().setOnAction(e -> handleSelectFolder());    //저장 폴더 선택
        mainView.getAddFileButton().setOnAction(e -> handleAddFiles());             //파일 추가
        mainView.getDropZone().setOnMouseClicked(e -> handleAddFiles());            //파일 추가
        mainView.getClearListButton().setOnAction(e -> handleClearList());          //파일 목록 지우기
        mainView.getConvertButton().setOnAction(e -> handleConvert());              //파일 변환
        mainView.getExitButton().setOnAction(e -> handleExit());                    //종료
        mainView.getDeleteMenuItem().setOnAction(e -> deleteSelectFile());          //목록에 파일 삭제
    }

    private void handleSelectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("변환 파일 저장 폴더 선택");

        // 이전에 선택한 폴더가 있다면 그 위치를 기본 열림 위치로 지정
        if (targetDirectory != null && targetDirectory.exists()) {
            directoryChooser.setInitialDirectory(targetDirectory);
        }

        Window window = mainView.getSelectFolderButton().getScene().getWindow();
        File selectedDir = directoryChooser.showDialog(window);
        if (selectedDir != null) {
            this.targetDirectory = selectedDir;
            mainView.getSavePathLabel().setText(selectedDir.getAbsolutePath());
        }
    }

    private void handleAddFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("변환할 이미지/동영상 선택");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Media Files", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.mp4", "*.mov");
        fileChooser.getExtensionFilters().add(filter);

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(mainView.getDropZone().getScene().getWindow());
        if(selectedFiles == null || selectedFiles.isEmpty()) {
            return;
        }

        String statusMsg = addFiles(selectedFiles);
        mainView.getStatusLabel().setText(statusMsg);
    }

    private void DragAndDropAddFiles() {
        Label dropZone = mainView.getDropZone();
        //파일 올리면 마우스 +버튼 변경
        dropZone.setOnDragOver(event -> {
            if(event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        dropZone.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean result = false;
            if(db.hasFiles()) {
                List<File> droppedFiles = db.getFiles();
                String StatusMsg = addFiles(droppedFiles);
                mainView.getStatusLabel().setText(StatusMsg);
                result = true;
            }
            event.setDropCompleted(result);
            event.consume();
        });
    }


    private void handleClearList() {
        mainView.getStatusLabel().setText("");
        mediaFiles.clear();
        mainView.getListView().getItems().clear();
        mainView.getProgressBar().setProgress(0);
        mainView.getProgressLabel().setText("0%");
    }

    private void handleConvert() {
        if(mediaFiles.isEmpty()) {
            mainView.getStatusLabel().setText("변환할 파일이 없습니다.");
            return;
        }

        String format = mainView.getFormatComboBox().getValue();
        String width = mainView.getWidthComboBox().getValue();
        String quality = mainView.getQualityComboBox().getValue();
        ConvertOptions options = ConvertOptions.of(format, width, quality, targetDirectory);

        //프로그레스 바 추가
        mainView.getProgressContainer().setVisible(true);
        ProgressBar progressBar = mainView.getProgressBar();
        Label progressLabel = mainView.getProgressLabel();

        progressBar.setProgress(0.0);
        progressLabel.setText("0%");
        setButtonsDisable(true);
        int fileCount = mediaFiles.size();

        //변환 병렬 처리
        new Thread(() -> {
            //스레드 수 지정
            int threadCount = Math.min(fileCount, Runtime.getRuntime().availableProcessors());
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            AtomicInteger completedCount = new AtomicInteger(0); // 💡 프로그레스 바 계산용

            for (MediaFile mediaFile : mediaFiles) {
                executor.submit(() -> {
                    MediaConverter converter = findConverter(mediaFile);
                    if (converter == null) {
                        failCount.incrementAndGet();
                    } else {
                        try {
                            converter.convert(mediaFile, options);
                            successCount.incrementAndGet();
                        } catch (IOException e) {
                            failCount.incrementAndGet();
                        }
                    }

                    //완료된 총 개수로 진행률 계산
                    int currentCompleted = completedCount.incrementAndGet();
                    double progress = (double) currentCompleted / fileCount;
                    int percent = (int) Math.round(progress * 100);

                    Platform.runLater(() -> {
                        progressBar.setProgress(progress);
                        progressLabel.setText(percent + "%");
                    });
                });
            }

            //모든 스레드가 끝날 때까지 대기
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            int finalSuccess = successCount.get();
            int finalFail = failCount.get();
            Platform.runLater(() -> {
                setButtonsDisable(false);
                mainView.getStatusLabel().setText(
                        String.format("완료 (성공: %d건, 실패: %d건)", finalSuccess, finalFail)
                );
                if (finalSuccess > 0) {
                    File openDir = (targetDirectory != null) ? targetDirectory : mediaFiles.getFirst().file().getParentFile();
                    Utils.openDirectory(openDir);
                }
            });
        }).start();
    }

    private void setupDeleteKeyEvent() {
        ListView<String> listView = mainView.getListView();
        listView.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if(keyCode == KeyCode.DELETE || keyCode == KeyCode.BACK_SPACE) {
                deleteSelectFile();
                event.consume();
            }
        });
    }

    private void deleteSelectFile() {
        ListView<String> listView = mainView.getListView();
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();

        if(selectedIndex < 0 || selectedIndex >= mediaFiles.size()) {
            return;
        }
        //목록에서 파일 삭제
        mediaFiles.remove(selectedIndex);
        listView.getItems().remove(selectedIndex);

        //남아있는 파일 기준 상태 라벨 갱신
        int remainingSize = listView.getItems().size();
        if (remainingSize == 0) {
            mainView.getStatusLabel().setText("");
        } else {
            String firstItem = listView.getItems().getFirst();
            int index = firstItem.indexOf("(");
            String firstFileName = (index != -1) ? firstItem.substring(0, index).trim() : firstItem;

            String statusMsg = (remainingSize == 1) ? firstFileName : firstFileName + "    포함: " + remainingSize;
            mainView.getStatusLabel().setText(statusMsg);
        }
    }

    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }

    private String addFiles(List<File> addFiles) {
        if (addFiles == null || addFiles.isEmpty()) {
            return mainView.getStatusLabel().getText();
        }
        int addedCount = 0;
        int skippedCount = 0;
        for (File file : addFiles) {
            MediaFile media = MediaFile.from(file);
            System.out.println(media);
            if(!media.isImage() && !media.isVideo()) {
                skippedCount++;
                continue;
            }
            if(!mediaFiles.contains(media)) {
                mediaFiles.add(media);
                addedCount++;
                // 화면 ListView에도 추가 (파일명 + 용량 표시)
                mainView.getListView().getItems().add(media.name() + " (" + (media.formattedSize() + ")"));
            }
        }

        int mainViewSize = mainView.getListView().getItems().size();
        // 💡 리스트가 아예 비어있는 경우 (전부 미지원 파일 등)
        if (mainViewSize == 0) {
            return skippedCount > 0 ? "지원하지 않는 파일입니다. (미지원 " + skippedCount + "건)" : "";
        }

        String firstItem = mainView.getListView().getItems().getFirst();
        int index = firstItem.indexOf("(");
        String firstFileName = (index != -1) ? firstItem.substring(0, index).trim() : firstItem;

        String statusMsg = (mainViewSize == 1) ? firstFileName : firstFileName + "    포함: " + mainViewSize;

        if (skippedCount > 0) {
            statusMsg += ", 미지원 파일: " + skippedCount + " 건";
        }
        return statusMsg;
    }

    private MediaConverter findConverter(MediaFile mediaFile) {
        if (mediaFile.isImage()) {
            if (imageConverter == null) imageConverter = new ImageConverter();
            return imageConverter;
        } else if (mediaFile.isVideo()) {
            if (videoConverter == null) videoConverter = new VideoConverter();
            return videoConverter;
        }
        return null;
    }

    private void setButtonsDisable(boolean disable) {
        //Node 기반 컴포넌트 일괄 disable
        List.of(
                mainView.getSelectFolderButton(),
                mainView.getAddFileButton(),
                mainView.getDropZone(),
                mainView.getClearListButton(),
                mainView.getConvertButton(),
                mainView.getExitButton(),
                mainView.getOptionBox(),
                mainView.getListView()
        ).forEach(node -> node.setDisable(disable));
        mainView.getDeleteMenuItem().setDisable(disable);
    }
}
