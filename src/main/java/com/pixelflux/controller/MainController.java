package com.pixelflux.controller;

import com.pixelflux.model.ConvertOptions;
import com.pixelflux.model.MediaFile;
import com.pixelflux.service.GifConverter;
import com.pixelflux.service.ImageConverter;
import com.pixelflux.service.MediaConverter;
import com.pixelflux.service.VideoConverter;
import com.pixelflux.util.Utils;
import com.pixelflux.view.MainView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainController {

    private final MainView mainView;
    private final List<MediaFile> generalMediaFiles;
    private final List<MediaFile> gifMediaFiles;
    private File targetDirectory = null;

    private ImageConverter imageConverter;
    private VideoConverter videoConverter;
    private GifConverter gifConverter;

    private boolean isGeneralActive = true;

    /* 변환 중 취소 관련 필드 */
    private ExecutorService currentExecutor;
    private volatile boolean isCancelled = false;
    private boolean isConverting = false;
    private List<CompletableFuture<Boolean>> currentFutures;

    public boolean isGeneralActive() {return isGeneralActive;}
    public List<MediaFile> getCurrentMediaFiles() {return isGeneralActive ? generalMediaFiles : gifMediaFiles;}
    public ListView<String> getCurrentListView() {return isGeneralActive ? mainView.getListView() : mainView.getGifListView();}
    public Label getCurrentStatusLabel() {return isGeneralActive ? mainView.getStatusLabel() : mainView.getGifStatusLabel();}

    public MainController(MainView mainView) {
        this.mainView = mainView;
        this.generalMediaFiles = new ArrayList<>();
        this.gifMediaFiles = new ArrayList<>();
        initEventHandlers();
    }

    public void initEventHandlers() {
        //탭 스위치 이벤트
        mainView.getTabFormatBtn().setOnAction(e -> {
            isGeneralActive = true;
            mainView.switchTab(true);
        });
        mainView.getTabGifBtn().setOnAction(e -> {
            isGeneralActive = false;
            mainView.switchTab(false);
        });

        setupDeleteKeyEvent();                                                                 //파일 삭제, key이벤트 추가
        DragAndDropAddFiles();                                                                 //드래그 앤 드랍으로 파일 추가
        /* 버튼 클릭 이벤트 연결 */
        mainView.getSelectFolderButton().setOnAction(e -> handleSelectFolder());    //저장 폴더 선택
        mainView.getAddFileButton().setOnAction(e -> handleAddFiles());             //파일 추가
        mainView.getDropZone().setOnMouseClicked(e -> handleAddFiles());            //파일 추가
        mainView.getClearListButton().setOnAction(e -> handleClearList());          //파일 목록 지우기
        mainView.getConvertButton().setOnAction(e -> handleConvertToggle());        //파일 변환 / 취
        mainView.getExitButton().setOnAction(e -> handleExit());                    //종료
        mainView.getDeleteMenuItem().setOnAction(e -> deleteSelectFile());          //목록에 파일 삭제

        mainView.getGifDropZone().setOnMouseClicked(e -> handleAddFiles());
        mainView.getGifAddFileButton().setOnAction(e -> handleAddFiles());
        mainView.getGifClearButton().setOnAction(e -> handleClearList());

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
        if(isGeneralActive) {
            fileChooser.setTitle("변환할 이미지/동영상 선택");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Media Files", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.mp4", "*.mov"));
        }else {
            fileChooser.setTitle("변환할 동영상 선택");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Media Files","*.webp", "*.mp4", "*.mov"));
        }
        Window window = isGeneralActive
                ? mainView.getDropZone().getScene().getWindow()
                : mainView.getGifDropZone().getScene().getWindow();

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(window);
        if(selectedFiles == null || selectedFiles.isEmpty()) {
            return;
        }
        String statusMsg = addFiles(selectedFiles);
        getCurrentStatusLabel().setText(statusMsg);
    }

    private void DragAndDropAddFiles() {
        List<Label> dropZones = List.of(mainView.getDropZone(), mainView.getGifDropZone());

        dropZones.forEach(zone -> {
            zone.setOnDragOver(event -> {
                if(event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            });
            zone.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean result = false;
                if (db.hasFiles()) {
                    List<File> droppedFiles = db.getFiles();
                    String statusMsg = addFiles(droppedFiles);
                    getCurrentStatusLabel().setText(statusMsg);
                    result = true;
                }
                event.setDropCompleted(result);
                event.consume();
            });
        });
    }

    private void handleClearList() {
            getCurrentStatusLabel().setText("");
            getCurrentMediaFiles().clear();
            getCurrentListView().getItems().clear();

        mainView.getProgressBar().setProgress(0);
        mainView.getProgressLabel().setText("0%");
    }

    private void startConvert() {
        isCancelled = false;
        List<MediaFile> currentMediaFiles = getCurrentMediaFiles();
        if(currentMediaFiles.isEmpty()) {
            getCurrentStatusLabel().setText("변환할 파일이 없습니다.");
            return;
        }

        ConvertOptions options;
        if(isGeneralActive) {
            String format = mainView.getFormatComboBox().getValue();
            String width = mainView.getWidthComboBox().getValue();
            String quality = mainView.getQualityComboBox().getValue();
            options = ConvertOptions.of(format, width, quality, targetDirectory);
        }else {
            String fps = mainView.getGifFpsComboBox().getValue();
            String width = mainView.getGifWidthComboBox().getValue();
            String quality = mainView.getGifQualityComboBox().getValue();
            options = ConvertOptions.ofGif(fps, width, quality, targetDirectory);
        }

        //프로그레스 바 추가
        mainView.getProgressContainer().setVisible(true);
        ProgressBar progressBar = mainView.getProgressBar();
        Label progressLabel = mainView.getProgressLabel();

        progressBar.setProgress(0.0);
        progressLabel.setText("0%");
        setConvertingUI(true);

        int fileCount = currentMediaFiles.size();
        int threadCount = Math.min(fileCount, Runtime.getRuntime().availableProcessors());
        AtomicInteger completedCount = new AtomicInteger(0);

        this.currentExecutor = Executors.newFixedThreadPool(threadCount);

        //각 파일별 비동기 변화 파이프라인 생성
        this.currentFutures = currentMediaFiles.stream()
                .map(mediaFile -> CompletableFuture.supplyAsync(() -> {
                    //취소 시 즉시 종료
                    if(isCancelled || Thread.currentThread().isInterrupted()) {
                        return false;
                    }
                    MediaConverter converter = findConverter(mediaFile);
                    if(converter == null) {return false;}

                    try {
                        converter.convert(mediaFile, options);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                }, currentExecutor).thenApply(success -> {
                    //개별 파일 변환 완료 때마다 프로그레스 바 갱신
                    int currentCompleted = completedCount.incrementAndGet();
                    double progress = (double) currentCompleted / fileCount;
                    int percent = (int) Math.round(progress * 100);

                    Platform.runLater(() -> {
                        progressBar.setProgress(progress);
                        progressLabel.setText(percent + "%");
                    });
                    return success;
                })
                ).toList();

        //whenCompleteAsync로 정상 완료/취소 일괄 처리
        CompletableFuture.allOf(currentFutures.toArray(new CompletableFuture[0]))
                .whenCompleteAsync((v, ex) -> {
                    setConvertingUI(false);

                    long successCount = currentFutures.stream().filter(f ->
                            !f.isCancelled() && f.getNow(false)).count();
                    long failCount = fileCount - successCount;

                    if(isCancelled) {
                        mainView.getProgressLabel().setText("중단됨.");
                        getCurrentStatusLabel().setText(String.format("중단됨 (완료: %d건 / 취소 및 중단: %d건)", successCount, failCount));
                    }else {
                        mainView.getProgressLabel().setText("100%");
                        getCurrentStatusLabel().setText(String.format("완료 (성공: %d건, 실패: %d건)", successCount, failCount));
                    }
                    //완료시, 저장폴더 열기
                    if(successCount > 0) {
                        File openDir = (targetDirectory != null) ? targetDirectory : currentMediaFiles.getFirst().file().getParentFile();
                        Utils.openDirectory(openDir);
                    }
                    //스레드 풀 정리
                    if (currentExecutor != null && !currentExecutor.isShutdown()) {
                        currentExecutor.shutdown();
                    }
                    //최종 UI 갱신은 Platform::runLater에서 직접 실행
                }, Platform::runLater);
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
        List<MediaFile> currentMediaFiles = getCurrentMediaFiles();
        ListView<String> listView = mainView.getListView();
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();

        if(selectedIndex < 0 || selectedIndex >= currentMediaFiles.size()) {
            return;
        }
        //목록에서 파일 삭제
        currentMediaFiles.remove(selectedIndex);
        listView.getItems().remove(selectedIndex);

        //남아있는 파일 기준 상태 라벨 갱신
        int remainingSize = listView.getItems().size();
        if (remainingSize == 0) {
            getCurrentStatusLabel().setText("");
        } else {
            String firstItem = listView.getItems().getFirst();
            int index = firstItem.indexOf("(");
            String firstFileName = (index != -1) ? firstItem.substring(0, index).trim() : firstItem;

            String statusMsg = (remainingSize == 1) ? firstFileName : firstFileName + "    포함: " + remainingSize;
            getCurrentStatusLabel().setText(statusMsg);
        }
    }

    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }

    private String addFiles(List<File> addFiles) {
        if (addFiles == null || addFiles.isEmpty()) {
            return getCurrentStatusLabel().getText();
        }
        List<MediaFile> targetList = getCurrentMediaFiles();
        ListView<String> targetListView = getCurrentListView();

        int skippedCount = 0;
        for (File file : addFiles) {
            MediaFile media = MediaFile.from(file);
            System.out.println(media);
            boolean isValid = isGeneralActive ? (media.isImage() || media.isVideo()) : media.isVideo();
            if(!isValid) {
                skippedCount++;
                continue;
            }
            if(!targetList.contains(media)) {
                targetList.add(media);
                // 화면 ListView에도 추가 (파일명 + 용량 표시)
                targetListView.getItems().add(media.name() + " (" + (media.formattedSize() + ")"));
            }
        }

        int mainViewSize = targetListView.getItems().size();
        // 💡 리스트가 아예 비어있는 경우 (전부 미지원 파일 등)
        if (mainViewSize == 0) {
            return skippedCount > 0 ? "지원하지 않는 파일입니다. (미지원 " + skippedCount + "건)" : "";
        }

        String firstItem = targetListView.getItems().getFirst();
        int index = firstItem.indexOf("(");
        String firstFileName = (index != -1) ? firstItem.substring(0, index).trim() : firstItem;

        String statusMsg = (mainViewSize == 1) ? firstFileName : firstFileName + "    포함: " + mainViewSize;

        if (skippedCount > 0) {
            statusMsg += ", 미지원 파일: " + skippedCount + " 건";
        }
        return statusMsg;
    }

    private MediaConverter findConverter(MediaFile mediaFile) {
        if(isGeneralActive) {
            if (mediaFile.isImage()) {
                if (imageConverter == null) imageConverter = new ImageConverter();
                return imageConverter;
            } else if (mediaFile.isVideo()) {
                if (videoConverter == null) videoConverter = new VideoConverter();
                return videoConverter;
            }
        }else {
            if (gifConverter == null) gifConverter = new GifConverter();
            return gifConverter;
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

    private void handleConvertToggle() {
        if(isConverting) {
            handleCancel();
        }else {
            startConvert();
        }
    }

    private void handleCancel() {
        if(currentExecutor != null && !currentExecutor.isShutdown()) {
            isCancelled = true;
            mainView.getProgressLabel().setText("변환을 중단하는 중입니다...");
            if (currentFutures != null) {
                currentFutures.forEach(future -> future.cancel(true));
            }
            //즉시 중단
            currentExecutor.shutdownNow();
        }
    }

    private void setConvertingUI(boolean converting) {
        this.isConverting = converting;

        setButtonsDisable(converting);

        Button convertBtn = mainView.getConvertButton();
        convertBtn.setDisable(false); // 버튼은 항상 클릭 가능해야 함

        if (converting) {
            convertBtn.setText("⏹️ 변환 중단");
            convertBtn.getStyleClass().remove("btn-primary");
            if (!convertBtn.getStyleClass().contains("btn-converting-cancel")) {
                convertBtn.getStyleClass().add("btn-converting-cancel");
            }
            convertBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            convertBtn.setText("🎨 변환 시작");
            convertBtn.getStyleClass().remove("btn-converting-cancel");
            if (!convertBtn.getStyleClass().contains("btn-primary")) {
                convertBtn.getStyleClass().add("btn-primary");
            }
            convertBtn.setStyle("");
        }
    }
}
