# Moodit 개발 명세 (AGENT.md)

## 프로젝트 개요
Moodit은 사용자의 일일 소비 기록을 누적 수집하여, 감정과 행동 동기를 분석해 주는 AI 기반 소비 심리 분석 모바일 애플리케이션입니다.

## 기술 스택
- 언어: Kotlin
- UI 프레임워크: Jetpack Compose (Material3 기반)
- 네비게이션: Navigation Compose
- AI 서비스: GroqCloud API (Llama 3.3 모델 연동)
- 로컬 스토리지: Preferences DataStore (최근 분석 결과 및 인사이트 백업)

## 주요 기능 매핑
- **FR-00 위치 정보 수집**: FusedLocationProviderClient를 통한 위/경도 수집 및 주소 변환 (Geocoder 활용)
- **FR-01 알림 수신 동의**: 안도르이드 POST_NOTIFICATIONS 권한 획득 및 로컬 조건형 알림 전송 (늦은 밤 소비 알림 등)
- **FR-02 홈 화면**: 최근 분석 결과와 인사이트 핵심 조언을 로컬 DataStore에서 복원하여 출력. 상단 인사말 및 축소된 캐릭터 배치 고도화.
- **FR-03 소비 기록 및 다건 내역 관리**: 텍스트 필드를 통한 실시간 금액 직접 입력, 카드형 소비 이유 선택 UI(이모지 포함), 메모 입력, 임시 리스트 저장 기능 제공. 누적 내역의 실시간 합계 연동 및 개별 수정/삭제 액션 지원 (`LazyColumn` 기반).
- **FR-04 ~ FR-05 소비 정보 분석 요청 및 AI 분석**: 누적된 다건의 소비 내역 전체를 분석 데이터로 연동하여 Llama 3.3 API 호출. 자연스러운 한국어(명사형 소비 유형 도출, 한자/일본어 배제) 리포트 생성 및 40~60자 수준의 친근한 한 줄 조언(인사이트) 추출.
- **FR-06 ~ FR-08 결과 리포트 및 통합 제공**: 카테고리별 요약, 소비 유형 및 설명, 시각화 차트, 한마디 메모 모음 통합 노출. 노출 즉시 DataStore 백업 수행. 스크롤 하단에 뒤로가기 스택을 비우며 홈 화면으로 복귀하는 '메인화면으로 돌아가기' 버튼 배치.
- **FR-09 테마 전환**: 다크 모드 및 라이트 모드 간 전체 테마 상태 전환 및 유지.
- **FR-10 브랜드 스플래시 화면**: Android 12+ SplashScreen API 연동. 원형 잘림을 방지하기 위해 여백이 미리 들어간 스플래시 전용 이미지(`today_character_splash.png`) 분리 관리 및 포스트 테마 전환 처리.

## 최근 UI 및 구조적 개선 사항
- **인사말 및 캐릭터 수평 정렬**:
  - 홈 화면(`MainScreen`) 상단 "안녕하세요 👋" 텍스트는 첫 줄에 단독 노출.
  - 두 번째 줄은 `Row`를 구성하고 좌측 끝에 "오늘도 현명한 소비 습관을 만들어봐요", 우측 끝에 구름 캐릭터(`65.dp`)를 정렬 배치하여 수평 중심선을 자연스럽게 통일.
  - 이 `Row` 영역과 하단의 인사이트 카드 사이 간격을 `20.dp`로 고정하여 시각적인 들뜸 방지.
- **최근 소비 인사이트 카드 개선**:
  - 카드 우측에 배치되어 줄바꿈 어색함을 유발하던 캐릭터를 제거하여, 인사이트 조언 문구가 카드 전체의 넓은 가로 폭을 충분히 사용하여 온전히 노출되도록 개선.
- **스플래시 화면 완성도 향상**:
  - 가로형 로고(`moodit_logo`)가 원형 마스크에 의해 잘리던 문제와, 캐릭터(`today_character`) 얼굴이 과도하게 확대 노출되던 문제를 동시에 해결.
  - 사방(특히 하단부)에 넉넉한 여백 패딩을 적용해 구름 모양이 온전히 나오게 한 `today_character_splash.png` (384x384 해상도) 이미지를 신규 도입하고 이를 스플래시 테마에 지정.
- **메인화면 복귀 버튼 추가**:
  - `ResultScreen` 스크롤 하단부에 '소비 분석하기' 버튼과 모서리 둥글기(18.dp), 높이(60.dp), 눌림 애니메이션이 완벽히 똑같은 '메인화면으로 돌아가기' 버튼 배치.
  - 클릭 시 `popUpTo("main") { inclusive = true }`를 실행해 분석/입력 중에 쌓인 백스택을 정리하여 홈 복귀 후 뒤로가기 동작의 편의성 개선.

## 프로젝트 파일 구조
* [MainActivity.kt](file:///c:/MobileProgramming/Moodit/app/src/main/java/com/example/moodit/MainActivity.kt) : 앱 진입점 및 SplashScreen 연동, Navigation 경로 관리
* [MainScreen.kt](file:///c:/MobileProgramming/Moodit/app/src/main/java/com/example/moodit/screen/MainScreen.kt) : 홈 화면 UI 렌더링, 최근 분석 및 요약 데이터 복원 표시
* [InputScreen.kt](file:///c:/MobileProgramming/Moodit/app/src/main/java/com/example/moodit/screen/InputScreen.kt) : 금액 직접 입력, 다건 소비 목록 등록/수정/삭제 관리, 위치 정보 획득
* [LoadingScreen.kt](file:///c:/MobileProgramming/Moodit/app/src/main/java/com/example/moodit/screen/LoadingScreen.kt) : AI 분석 대기용 프로그레스 및 로딩 연출
* [ResultScreen.kt](file:///c:/MobileProgramming/Moodit/app/src/main/java/com/example/moodit/screen/ResultScreen.kt) : AI 소비 성향 분석 요청, 커스텀 Pie/Bar 차트 렌더링, DataStore 저장 및 메인 복귀 네비게이션 제공
* [DataStoreManager.kt](file:///c:/MobileProgramming/Moodit/app/src/main/java/com/example/moodit/data/DataStoreManager.kt) : 최근 분석 정보 및 인사이트 문장 로컬 저장소 입출력 구현
* [ConsumptionData.kt](file:///c:/MobileProgramming/Moodit/app/src/main/java/com/example/moodit/model/ConsumptionData.kt) : 소비 정보를 나타내는 ConsumptionData 데이터 클래스 및 싱글톤 목록 관리 객체 정의
* [SharedLocationHolder.kt](file:///c:/MobileProgramming/Moodit/app/src/main/java/com/example/moodit/ui/theme/SharedLocationHolder.kt) : GPS 위치 정보 공유 홀더

## 개발 및 협업 규칙
1. **Material3 테마 가이드 준수**: 다크 모드와 라이트 모드에 따라 텍스트 및 카드 컴포넌트의 대비가 자연스럽게 조절되도록 테마 컬러 시스템을 이용한다.
2. **보안 규칙**: API Key와 같은 중요 자격 증명 정보는 절대 소스코드에 직접 작성하지 않고, `local.properties`와 gradle build 설정을 연계한 `BuildConfig` 필드를 활용해 보안성을 유지한다.
3. **사용성 및 완성도**: 화면 스크롤, 버튼 눌림 애니메이션(Scale 변환), 실시간 데이터 결합 피드백을 충실히 반영하여 직관적이고 아기자기한 사용자 경험을 보장한다.
