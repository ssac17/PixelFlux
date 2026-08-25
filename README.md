# 🎨 PixelFlux

> **JavaFX 기반의 고성능 이미지/동영상 배치 처리 도구**
>
> 드래그 앤 드롭으로 대량의 미디어 파일을 병렬 처리하여 빠르게 변환합니다.
> 포맷 변환, 해상도 조절, 품질 최적화 기능을 지원합니다.

<small>mac환경에서 실습하여 실제 프로그램은 올리지 못했습니다. 후추 window용으로 올리겠습니다.</small>

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
├── 📂 src/main/java/com/pixelflux/
│   ├── Main.java                           # JavaFX 애플리케이션 진입점
│   ├── Launcher.java                       # 애플리케이션 시작 클래스
│   │
│   ├── 📂 controller/
│   │   └── MainController.java             # UI 이벤트 처리 및 비즈니스 로직 조율
│   │
│   ├── 📂 view/
│   │   └── MainView.java                   # JavaFX UI 컴포넌트 구성 (탭 레이아웃)
│   │
│   ├── 📂 service/  (Strategy Pattern)
│   │   ├── MediaConverter.java             # 변환 인터페이스 (확장점)
│   │   ├── ImageConverter.java             # 이미지 변환 구현 (Thumbnailator)
│   │   ├── VideoConverter.java             # 동영상 변환 구현 (FFmpeg)
│   │   └── GifConverter.java               # GIF 변환 구현 (동영상 → GIF)
│   │
│   ├── 📂 model/  (Record 클래스)
│   │   ├── MediaFile.java                  # 미디어 파일 모델 (불변 데이터 객체)
│   │   └── ConvertOptions.java             # 변환 옵션 모델 (포맷, 크기, 품질)
│   │
│   └── 📂 util/
│       └── Utils.java                      # 유틸리티 함수 모음
│
├── 📂 src/main/resources/
│   └── css/mainview.css                    # JavaFX 스타일시트
│
├── 📂 assets/
│   └── 📂 demos/  ← GIF 데모 파일 추가 위치
│       ├── drag-and-drop-demo.gif          # 파일 추가 데모
│       ├── image-conversion-demo.gif       # 이미지 변환 데모
│       ├── video-conversion-demo.gif       # 동영상 변환 데모
│       ├── batch-processing-demo.gif       # 다중 파일 처리 데모
│       ├── quality-adjustment-demo.gif     # 품질 조절 데모
│       └── folder-auto-open-demo.gif       # 자동 폴더 열기 데모
│
├── build.gradle                            # Gradle 빌드 설정
├── settings.gradle
└── README.md
```

### 디렉토리별 역할

| 디렉토리 | 역할 | 주요 책임 |
|---------|------|----------|
| **controller** | 사용자 입력 처리 | 버튼 클릭, 파일 드래그, 옵션 변경 감지 |
| **view** | UI 구성 | 탭 생성, 컨트롤 배치, 스타일 적용 |
| **service** | 비즈니스 로직 | 파일 변환 처리, Strategy 패턴 구현 |
| **model** | 데이터 정의 | 불변 데이터 객체, Record 클래스 |
| **util** | 공통 기능 | 헬퍼 함수, 검증 로직 |
| **assets/demos** | 문서 리소스 | 기능 시연용 GIF 파일 |

---

## 🌟 핵심 특징

- ⚡ **병렬 처리 최적화**: CPU 코어 수에 맞춰 동시 변환으로 처리 속도 극대화
- 🖱️ **직관적 UI**: 드래그 앤 드롭, 우클릭 메뉴, 단축키 지원
- 🎯 **스마트 리사이징**: 종횡비 자동 유지, 비디오 코덱 호환성 보증
- 📊 **실시간 진행 표시**: 프로그레스 바로 변환 진행 상황 실시간 확인
- 📁 **자동 폴더 열기**: 변환 완료 후 결과 폴더 자동으로 오픈
- 🎬 **GIF 변환**: 동영상 → GIF 변환 기능 추가
- 🏗️ **디자인 패턴**: Strategy Pattern, MVC 아키텍처로 확장 가능한 구조

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

## 🏛️ 아키텍처 및 설계 패턴

### 📐 핵심 디자인 패턴

#### 1. **Strategy Pattern** (전략 패턴)
확장 가능한 변환 로직 구조

```java
// 변환 전략을 동적으로 선택 및 실행
public interface MediaConverter {
    void convert(MediaFile input, ConvertOptions options);
}

public class ImageConverter implements MediaConverter { ... }
public class VideoConverter implements MediaConverter { ... }
public class GifConverter implements MediaConverter { ... }
```

**이점**: 새로운 파일 형식 추가 시 기존 코드 수정 없이 새 클래스만 추가하면 됨

#### 2. **MVC Pattern** (모델-뷰-컨트롤러)
역할 분리로 유지보수성 향상

- **Model**: `MediaFile`, `ConvertOptions` - 데이터 정의
- **View**: `MainView` - UI 레이아웃 및 컴포넌트
- **Controller**: `MainController` - 사용자 입력 처리 및 로직 조율

#### 3. **Record 클래스** (Java 14+)
간결한 불변 데이터 객체

```java
public record MediaFile(
    File file,
    String name,
    long size,
    String extension
) { }

// 자동으로 생성됨: 생성자, equals(), hashCode(), toString()
```

### 🔄 프로그램 실행 흐름

```
┌─────────────────────────────────────────────────────────────┐
│ 사용자 입력 (드래그 앤 드롭 또는 파일 선택)                   │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ MainController: 이벤트 감지 및 파일 검증                     │
│ - MediaFile.isImage() / isVideo() 확인                       │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ 변환 옵션 파싱 (ConvertOptions.of)                           │
│ - 포맷, 해상도, 품질 정보 수집                               │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ 병렬 처리 (ExecutorService - CPU 코어 수에 맞춤)            │
│ ├→ ImageConverter.convert()     (이미지 변환)               │
│ ├→ VideoConverter.convert()     (동영상 변환)               │
│ └→ GifConverter.convert()       (GIF 변환)                  │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ 진행 상황 업데이트 (Platform.runLater)                      │
│ - 프로그레스 바, 진행 상태 텍스트 업데이트                   │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ 변환 완료 후 결과 폴더 자동 오픈                             │
└─────────────────────────────────────────────────────────────┘
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

## 📦 주요 의존성

### 이미지 처리
```gradle
implementation("net.coobird:thumbnailator:0.4.21")
```
- 고속 이미지 리사이징 및 포맷 변환
- 다양한 포맷 지원 (JPG, PNG, WebP, BMP)
- 메모리 효율적인 배치 처리

### 동영상 처리
```gradle
implementation 'org.bytedeco:javacv:1.5.13'
implementation 'org.bytedeco:ffmpeg:8.0.1-1.5.13'
implementation 'org.bytedeco:ffmpeg:8.0.1-1.5.13:macosx-arm64'
```
- OpenCV 기반 프레임 처리
- FFmpeg을 통한 H.264/H.265 인코딩
- Apple Silicon (ARM64) 네이티브 지원

### UI 프레임워크
```gradle
javafx {
    version = "21.0.6"
    modules = [ 'javafx.controls' ]
}
```
- 모던 데스크톱 UI 개발
- CSS 기반 스타일링

---

## 🚀 시작하기

### 시스템 요구사항
- **Java**: JDK 21 이상
- **OS**: macOS (ARM64/Intel 모두 지원), Windows/Linux (향후 지원)
- **메모리**: 최소 2GB RAM (권장 4GB 이상)

### 빌드 및 실행

```bash
# 저장소 클론
git clone https://github.com/ssac17/PixelFlux.git
cd PixelFlux

# 빌드
./gradlew build

# 실행
./gradlew run

# JAR 파일로 배포
./gradlew shadowJar
java -jar build/libs/PixelFlux-1.0-SNAPSHOT-all.jar
```

---

## 💡 주요 학습 포인트 & 개발 경험

### 아키텍처
- ✅ **Strategy Pattern**: 파일 형식별 변환 로직을 확장 가능하게 설계
- ✅ **MVC Architecture**: 관심사 분리로 유지보수성 극대화
- ✅ **Java Records**: 불변 데이터 객체로 안전한 데이터 전달

### 멀티스레딩 & 성능
- ✅ **ExecutorService**: CPU 코어 수에 맞춰 스레드 풀 동적 생성
- ✅ **CompletableFuture**: 비동기 작업 처리 및 예외 처리
- ✅ **Platform.runLater**: JavaFX UI 스레드 안전성 확보

### 라이브러리 통합
- ✅ **FFmpeg/JavaCV**: 동영상 인코딩 및 프레임 처리
- ✅ **Thumbnailator**: 고속 이미지 리사이징
- ✅ **JavaFX**: 모던 데스크톱 UI 개발

### UI/UX 설계
- ✅ **드래그 앤 드롭**: 직관적 파일 추가 인터페이스
- ✅ **실시간 진행 표시**: 사용자 경험 향상
- ✅ **단축키 & 컨텍스트 메뉴**: 생산성 중심 설계

---

## 📚 향후 개선 계획

- [ ] Windows/Linux 플랫폼 지원
- [ ] 배치 설정 저장/불러오기 (프로필 기능)
- [ ] 자동 워터마크 추가
- [ ] 커스텀 파일명 규칙
- [ ] 변환 히스토리 추적
- [ ] 드래그 앤 드롭 재정렬 기능

---

## 📄 라이센스

MIT License - 자유롭게 사용, 수정, 배포 가능합니다.

---

## 🤝 기여 및 피드백

기능 제안이나 버그 리포트는 [GitHub Issues](https://github.com/ssac17/PixelFlux/issues)를 통해 등록해주세요.

---

**Made with ❤️ using Java & JavaFX**

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

## 📹 주요 기능 데모

### 🎬 파일 추가 및 변환 (드래그 앤 드롭)

**설명**: 파일을 애플리케이션 창으로 드래그하여 파일 목록에 추가하고, 변환 옵션을 설정한 후 변환을 시작합니다.

<img width="720" height="434" alt="드래그앤드랍_converted" src="https://github.com/user-attachments/assets/b2b7cac2-b2cf-46af-95dd-a70101a9672a" />

---

### 🖼️ 이미지 변환

**설명**: PNG/BMP 이미지를 JPG/WebP로 변환하고 리사이징합니다.


<img width="720" height="450" alt="이미지_변환_converted" src="https://github.com/user-attachments/assets/0320c0bf-ece7-4a27-bca4-65d397b35915" />


**주요 기능**:
- PNG → JPG/WebP 포맷 변환
- 1920×1080 → 1280px 리사이징 (종횡비 유지)
- 품질 80% 적용

---

### 🎥 동영상 변환

**설명**: MOV/AVI 동영상을 MP4로 변환하고 해상도를 조절합니다.


<img width="720" height="440" alt="영상_변환_converted" src="https://github.com/user-attachments/assets/47c16fa4-028c-4972-8b59-4f35f953cc1a" />


**주요 기능**:
- MOV → MP4 포맷 변환
- 1080p → 720p 해상도 축소
- 오디오 보존 (AAC)
- 프로그레스 바로 진행 상황 확인

---

### ⚡ 다중 파일 배치 처리

**설명**: 여러 파일을 동시에 변환합니다. 병렬 처리로 빠른 속도를 달성합니다.


<img width="720" height="450" alt="다중처리_converted" src="https://github.com/user-attachments/assets/c75bd2ff-5634-4814-af0b-6c4103e342f0" />


**주요 기능**:
- 50개 이상 파일 동시 처리
- CPU 코어 수에 맞춰 스레드 풀 자동 조정
- 실시간 진행률 표시

---

### 🎚️ 품질 조절

**설명**: 이미지/동영상의 품질을 조절하여 파일 크기를 최적화합니다.

<img width="720" height="448" alt="사이즈조절_converted" src="https://github.com/user-attachments/assets/c1a44a5e-f7f6-449d-a2c6-6eefa13801fa" />


**품질 옵션**:
- **60%**: 가장 작은 파일 크기 (낮은 품질)
- **80%**: 권장 (좋은 품질 ↔ 작은 크기)
- **95%**: 최고 품질 (큰 파일 크기)

---

### 📂 변환 완료 후 자동 폴더 열기

**설명**: 변환 작업이 완료되면 결과 폴더가 자동으로 열립니다.

<img width="720" height="414" alt="폴더열기_converted" src="https://github.com/user-attachments/assets/96c1e4fd-e21f-47b9-8984-fd34102a7feb" />


---

### 테스트 시나리오

#### 이미지 변환
- ✅ **포맷 변환**: PNG/BMP → JPG/WebP 변환
- ✅ **리사이징**: 1920×1080 → 1280px (종횡비 자동 유지)
- ✅ **품질 제어**: 60%, 80%, 95% 품질 옵션
- ✅ **배치 처리**: 50개 이상의 이미지 동시 변환

#### 동영상 변환
- ✅ **포맷 변환**: MOV/AVI → MP4 변환
- ✅ **해상도 조절**: 1080p → 720p/480p로 압축
- ✅ **오디오 보존**: 원본 오디오 트랙 유지 (AAC 인코딩)
- ✅ **배치 처리**: 병렬 처리로 대량 파일 변환

#### GIF 변환
- ✅ **동영상 → GIF**: MP4/MOV → 애니메이션 GIF 변환
- ✅ **크기 최적화**: 프레임 추출 및 압축

### 성능 벤치마크

| 작업 | 성능 | 환경 | 참고 사항 |
|------|------|------|----------|
| **이미지 리사이징** | ~0.1초/파일 | 1920×1080 → 1280px | Thumbnailator 최적화 |
| **이미지 변환** | ~0.15초/파일 | PNG → JPG 포맷 변환 | 병렬 처리로 빨라짐 |
| **동영상 변환** | ~5-10분/파일 | 1GB 파일, 1080p → 720p | FFmpeg 인코딩 |
| **메모리 사용** | ~200-500MB | 일반적인 배치 처리 | 파일 크기에 따라 변동 |
| **CPU 사용률** | 70-90% | 병렬 처리 중 | 모든 코어 활용 |

