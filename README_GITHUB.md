# WordAlert — GitHub Actions로 폰에서 APK 만들기

이 프로젝트는 PC 없이 GitHub Actions에서 Android APK를 빌드할 수 있도록 설정되어 있습니다.

## 1. GitHub에 올리기

휴대폰 브라우저에서 GitHub에 로그인한 뒤 새 Repository를 만듭니다.

ZIP을 압축 해제한 **WordAlert 폴더 안의 파일 전체**를 Repository의 루트에 업로드하세요.

Repository의 최상위에 다음처럼 보여야 합니다.

- `.github/workflows/build-apk.yml`
- `app/`
- `build.gradle.kts`
- `settings.gradle.kts`

## 2. APK 빌드

GitHub Repository → **Actions** → **Build Android APK** → **Run workflow**를 누릅니다.

빌드가 끝나면 해당 workflow 실행 화면 아래쪽 **Artifacts**에서 `WordAlert-debug-apk`를 선택해 APK를 받습니다.

## 3. 휴대폰에 설치

다운로드한 APK를 실행하고 Android가 알 수 없는 앱 설치를 요구하면 브라우저/파일 관리자에 설치 권한을 허용합니다.

## 앱 기능

- 기본 감시 URL: https://sexbam58.top/index.php?mid=sschkiss&category=12782286
- 감시 단어 입력
- 15분 / 30분 / 1시간 / 3시간 주기
- 지금 확인
- 백그라운드 페이지 확인
- 새로운 검색 결과가 발견되면 Android 알림
- 알림을 누르면 감시 페이지 열기

### 주의

Android의 백그라운드 작업은 정확한 시각에 실행된다는 보장이 없습니다. WorkManager 기반 주기 작업은 시스템 상태에 따라 지연될 수 있습니다.
