# 🎨 PixelFlux - 이미지/동영상 일괄 변환 도구

JavaFX로 개발한 **크로스 플랫폼 미디어 변환 애플리케이션**입니다. 이미지와 동영상을 간편하게 포맷 변환하고 크기를 조절할 수 있습니다.

---

## ✨ 주요 기능

### 🖼️ 이미지 처리
- **다양한 포맷 지원**: JPG, JPEG, PNG, WebP, BMP 변환
- **지능형 리사이징**: 가로폭 설정 시 종횡비 자동 유지
- **화질 조절**: 품질 설정(60%, 80%, 95% 등)으로 파일 크기 최적화
- **Thumbnailator** 라이브러리 기반 고속 처리

### 🎬 동영상 처리
- **포맷 지원**: MP4, MOV, AVI, MKV, WebM 변환
- **해상도 조절**: 원본 유지 또는 HD(1280), Full HD(1920) 등 사전 설정값 선택
- **오디오 보존**: 원본 오디오 채널 유지 (AAC 인코딩)
- **품질 최적화**: CRF 값으로 영상 화질 제어
- **FFmpeg/JavaCV** 기반 강력한 인코딩

### 🚀 사용자 경험
- **드래그 앤 드롭**: 직관적인 파일 추가 방식
- **다중 파일 처리**: 여러 파일 일괄 변환
- **병렬 처리**: CPU 코어 수에 맞춰 동시 변환으로 속도 극대화
- **실시간 진행 표시**: 프로그레스 바로 변환 진행 상황 확인
- **일괄 삭제**: Delete/BackSpace 키로 선택 파일 삭제
- **자동 폴더 열기**: 변환 완료 후 결과 폴더 자동 오픈

---

## 🏗️ 프로젝트 구조

```
PixelFlux/
├── src/main/java/com/pixelflux/
│   ├── Main.java                    # JavaFX 애플리케이션 진입점
│   ├── Launcher.java                # 애플리케이션 시작 클래스
│   ├── controller/
│   │   └── MainController.java      # UI 이벤트 핸들링 및 비즈니스 로직
│   ├── view/
│   │   └── MainView.java            # JavaFX UI 컴포넌트 구성
│   ├── service/
│   │   ├── MediaConverter.java       # 변환 인터페이스 (전략 패턴)
│   │   ├── ImageConverter.java       # 이미지 변환 구현
│   │   └── VideoConverter.java       # 동영상 변환 구현
│   ├── model/
│   │   ├── MediaFile.java           # 미디어 파일 모델 (Record)
│   │   └── ConvertOptions.java      # 변환 옵션 모델 (Record)
│   └── util/
│       └── Utils.java               # 유틸리티 함수 모음
├── src/main/resources/
│   └── css/mainview.css             # UI 스타일시트
├── build.gradle                     # Gradle 빌드 설정
└── README.md
```

---

## 🛠️ 기술 스택

| 계층 | 기술 | 버전 |
|------|------|------|
| **UI Framework** | JavaFX | 21 |
| **빌드 도구** | Gradle | 8.x |
| **Java** | OpenJDK | 21 |
| **이미지 처리** | Thumbnailator | 0.4.21 |
| **동영상 처리** | JavaCV + FFmpeg | 1.5.13 + 8.0.1 |
| **플랫폼** | macOS (ARM64 지원) | - |

---

## 🏛️ 아키텍처 설계

### 📐 디자인 패턴

#### 1. **Strategy Pattern** (전략 패턴)
```java
// 변환 전략을 동적으로 선택
List<MediaConverter> converters = List.of(
    new ImageConverter(),
    new VideoConverter()
);

MediaConverter converter = findConverter(mediaFile);
```
- `MediaConverter` 인터페이스로 확장성 확보
- 이미지/동영상 변환 로직을 독립적인 클래스로 분리

#### 2. **MVC Pattern** (모델-뷰-컨트롤러)
- **Model**: `MediaFile`, `ConvertOptions` - 데이터 모델
- **View**: `MainView` - UI 구성 및 표현
- **Controller**: `MainController` - 사용자 입력 처리 및 로직 조율

#### 3. **Record 클래스** (불변 데이터 객체)
```java
public record MediaFile(
    File file,
    String name,
    long size,
    String extension
)
```
- 간결한 문법으로 데이터 클래스 정의
- 자동 생성되는 equals(), hashCode(), toString()

### 🔄 처리 흐름

```
사용자 입력
    ↓
MainController (이벤트 감지)
    ↓
파일 검증 (MediaFile.isImage/isVideo)
    ↓
변환 옵션 파싱 (ConvertOptions.of)
    ↓
병렬 처리 (ExecutorService)
    ├→ ImageConverter.convert()
    └→ VideoConverter.convert()
    ↓
진행 상황 업데이트 (Platform.runLater)
    ↓
완료 후 결과 폴더 오픈
```

---

## 🎯 핵심 구현 사항

### 1️⃣ **병렬 처리 최적화**
```java
// CPU 코어 수에 따라 스레드 풀 크기 조정
int threadCount = Math.min(fileCount, Runtime.getRuntime().availableProcessors());
ExecutorService executor = Executors.newFixedThreadPool(threadCount);
```
- 대량 파일 변환 시 처리 속도 극대화
- 시스템 리소스 효율적 활용

### 2️⃣ **동영상 해상도 유지**
```java
// 종횡비 유지하며 리사이징
if (options.targetWidth() < width) {
    targetWidth = options.targetWidth();
    targetHeight = (int) Math.round((double) (height * targetWidth) / width);
}
// 비디오 코덱 호환성을 위해 짝수로 보정
if(targetWidth % 2 != 0) targetWidth--;
```

### 3️⃣ **화질 제어 (CRF 매핑)**
```java
// 사용자 입력 품질(0.6~1.0)을 H.264 CRF 값(28~18)으로 변환
double quality = 0.8;  // 80%
int crf = (int) Math.round(28 - (quality - 0.6) * 25);
// CRF: 낮을수록 고품질 (권장: 18~28)
```

### 4️⃣ **드래그 앤 드롭 처리**
```java
dropZone.setOnDragDropped(event -> {
    Dragboard db = event.getDragboard();
    if(db.hasFiles()) {
        List<File> droppedFiles = db.getFiles();
        addFiles(droppedFiles);
    }
});
```

### 5️⃣ **UI 스레드 안전성**
```java
// Worker 스레드에서 JavaFX UI 업데이트
Platform.runLater(() -> {
    progressBar.setProgress(progress);
    progressLabel.setText(percent + "%");
});
```

---

## 💾 의존성

### 이미지 처리
```gradle
implementation("net.coobird:thumbnailator:0.4.21")
```
- 고속 이미지 리사이징
- 다양한 포맷 지원
- 메모리 효율적 처리

### 동영상 처리
```gradle
implementation 'org.bytedeco:javacv:1.5.13'
implementation 'org.bytedeco:ffmpeg:8.0.1-1.5.13'
implementation 'org.bytedeco:ffmpeg:8.0.1-1.5.13:macosx-arm64'
```
- OpenCV 기반 프레임 처리
- FFmpeg을 통한 H.264 인코딩
- macOS ARM64 (Apple Silicon) 네이티브 지원

---

## 🚀 설치 및 실행

### 필수 사항
- **Java 21** 이상
- **macOS** (현재 ARM64 최적화 완료)

### 빌드
```bash
./gradlew build
```

### 실행
```bash
./gradlew run
```

### JAR 파일 생성
```bash
./gradlew shadowJar
java -jar build/libs/PixelFlux-1.0-SNAPSHOT-all.jar
```

---

## 📱 사용 가이드

### 기본 사용 방법
1. **파일 추가**
   - 드래그 앤 드롭으로 파일 추가
   - 또는 "📁 파일 추가 (Finder)" 버튼 클릭

2. **변환 옵션 설정**
   - **포맷**: 변환할 파일 형식 선택 (JPG, PNG, MP4 등)
   - **크기**: 원본 유지 또는 해상도 선택 (HD, Full HD 등)
   - **품질**: 이미지/동영상 품질 선택 (60%, 80%, 95%)

3. **저장 위치 지정** (선택사항)
   - "📂저장 폴더 선택" 버튼으로 저장 폴더 지정
   - 미지정 시 원본 파일과 동일한 폴더에 저장

4. **변환 시작**
   - "🎨 변환 시작" 버튼 클릭
   - 진행 상황을 프로그레스 바에서 확인
   - 완료 후 자동으로 결과 폴더 오픈

### 단축키
- **Delete / Backspace**: 선택된 파일 삭제
- **마우스 우클릭**: 파일 삭제 (컨텍스트 메뉴)

---

## 🔍 성능 특성

| 작업 | 성능 | 비고 |
|------|------|------|
| 이미지 리사이징 (1920x1080 → 1280px) | ~0.1초/파일 | Thumbnailator 최적화 |
| 동영상 변환 (1GB, HD→720p) | ~5-10분 | 병렬 처리 시 동시 처리 |
| 메모리 사용 | ~200-500MB | 파일 크기에 따라 변동 |
| CPU 사용률 | 70-90% | 병렬 처리 중 |

---

## 🧪 테스트 시나리오

### 이미지 변환 테스트
```
✓ PNG → JPG (품질 80%)
✓ 1920×1080 → 1280 리사이징 (종횡비 유지)
✓ 다중 이미지 일괄 변환
```

### 동영상 변환 테스트
```
✓ MOV → MP4 (1080p → 720p)
✓ 오디오 트랙 보존
✓ 100개 파일 병렬 처리
```

---

## 📈 향후 개선 사항

- [ ] Windows 플랫폼 지원
- [ ] GIF 애니메이션 처리
- [ ] 배치 프로세싱 설정 저장/불러오기
- [ ] 자동 워터마크 추가 기능
- [ ] 커스텀 이름 규칙 적용

---

## 📝 라이센스

MIT License

---

## 👨‍💻 개발 정보

**주요 학습 포인트**:
- JavaFX를 통한 데스크톱 애플리케이션 개발
- FFmpeg 통합 및 동영상 인코딩 최적화
- 멀티스레딩과 병렬 처리 구현
- 사용자 경험 중심의 UI/UX 설계
- Strategy 패턴과 MVC 아키텍처 실전 적용

---

## 📞 문의 및 피드백

이슈 사항이나 기능 제안은 GitHub Issues를 통해 등록해주세요.

