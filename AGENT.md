# Moodit

## 프로젝트 소개
Moodit은 사용자의 소비 정보를 기반으로 소비 유형과 소비 성향을 분석하는 AI 기반 소비 분석 앱이다.

## 기술 스택
- Kotlin
- Jetpack Compose
- Material3
- Navigation Compose
- GroqCloud API
- Preferences DataStore (최근 분석 결과 로컬 저장)

## 현재 구현 기능
- **FR-00 위치 정보 수집 기능**: 위치 권한 요청 및 위치 정보 확인
- **FR-01 알림 수신 동의 기능**: 알림 권한 요청, 수신 동의 및 수신 기능
- **FR-02 홈 화면 제공 기능**: 서비스 소개 화면 제공 및 소비 기록하기 버튼 제공, Preferences DataStore 기반 최근 소비 분석 결과 및 최근 소비 인사이트 연동/로컬 유지 기능 (재실행 시 자동 복원)
- **FR-03 선택형 소비 입력**: 소비 카테고리, 금액대, 소비 이유 선택
- **FR-04 ~ FR-05 소비 성향 분석**: GroqCloud API 호출, 소비 성향 분석 및 로딩 상태 표시, 한국어 명사 형태의 소비 유형 출력 가이드라인 적용 (한자/일본어 사용 금지)
- **FR-06 ~ FR-08 결과 리포트 제공**: 소비 정보 카드, 소비 유형 카드, 소비 성향 태그 및 AI 분석 리포트 (분석 완료 시 최근 결과 및 인사이트 핵심 문장을 추출하여 DataStore에 로컬 자동 저장, 재호출 없는 데이터 재활용)
- **FR-09 테마 전환 기능**: 다크 모드, 라이트 모드 테마 상태 유지

## 최근 UI 개선 및 변경 사항
- **✨ 최근 소비 인사이트 카드 구현**:
  - 기존 메인 화면의 "오늘의 한 줄" 고정 카드를 최근 소비 분석 결과 기반의 인사이트 카드로 전면 개편
  - 제목을 "✨ 최근 소비 인사이트"로 변경하고 18sp Bold 스타일 적용
  - 본문 내용을 15sp Normal 스타일의 일반 본문으로 표현하고 말줄임(...)을 완전 제거해 끝까지 읽을 수 있게 개선
  - 카드 높이가 본문 길이에 따라 자동으로 증가하도록 `heightIn(min = 140.dp)` 적용
  - 카드 높이 증가 시 캐릭터 이미지도 `Box(fillMaxHeight)` 및 `IntrinsicSize.Max`를 활용해 세로 중앙에 반응형 정렬되도록 레이아웃 고도화
- **최근 분석 결과 및 인사이트 로컬 유지**: DataStoreManager를 활용해 분석 데이터 및 인사이트 조언 문장을 저장하고 앱 재실행 시 완벽히 복원하도록 구현
- **AI 소비 유형 한글화 정책 반영**: AI 분석 시 한자(Hanja), 일본어 및 불필요한 영어를 차단하고 '필요형 소비자', '자기보상형 소비자'와 같은 자연스러운 한국어 명사 형태로 분석 유형을 도출하도록 프롬프트 보완
- **감정해소형 소비 성향 키워드 최적화**: 감정해소형 소비의 성향 키워드를 기존 `#감정소비, #스트레스해소, #기분전환`에서 `#감정소비, #기분환기, #기분전환`으로 변경하여 문서화 반영
- 최근 선택한 소비 정보 카드 추가 및 실제 소비 분석 결과 실시간 데이터 연동
- 소비 정보 태그(Chip) 디자인 개선 및 버튼 클릭 애니메이션 추가
- 메인 화면 UI 개선 (최근 분석 결과 카드에 소비 이유별 동적 이모지 매핑 적용)

## 프로젝트 구조
* MainActivity : 앱 진입점 및 Navigation 관리
* MainScreen : 홈 화면 및 최근 분석 결과/기록 요약 표시 (최근 분석 데이터가 없는 경우 기본 문구 노출, 있을 경우 로컬 저장된 데이터 복원 표시)
* InputScreen : 소비 정보 입력, 위치 정보 수집 및 알림 권한 요청 처리
* LoadingScreen : AI 분석 중 로딩 애니메이션 표시
* ResultScreen : 소비 유형 분류, Groq API 연동을 통한 AI 소비 성향 분석 리포트 제공, 분석 결과 DataStore 자동 백업
* DataStoreManager : Preferences DataStore를 통한 최근 소비 분석 결과 로컬 저장 및 조회 처리
* ConsumptionData : 소비 정보 데이터를 담는 데이터 클래스
* SharedLocationHolder : GPS 위치 정보를 화면 간 공유하기 위한 상태 객체
* Theme.kt / Color.kt / Type.kt : Material3 디자인 시스템 기반의 커스텀 다크/라이트 테마 정의

## 기능 명세
*각 기능요구사항의 수용 조건(Acceptance Criteria)은 features 폴더의 마크다운 문서를 참조한다.*

* F-00-location.md : 위치 정보 수집 기능 명세
* F-01-notification-consent.md : 알림 수신 동의 기능 명세
* F-02-home-screen.md : 홈 화면 제공 기능 명세
* F-03-consumption-input.md : 선택형 소비 입력 기능 명세
* F-04-analysis-request.md : 소비 정보 분석 요청 기능 명세
* F-05-ai-analysis.md : AI 소비 성향 분석 기능 명세
* F-06-report-view.md : 소비 리포트 제공 기능 명세
* F-07-result-visualization.md : 결과 화면 시각화 기능 명세
* F-08-result-integration.md : 결과 화면 통합 제공 기능 명세
* F-09-theme-toggle.md : 테마 전환 기능 명세

## 개발 및 작업 규칙
1. **Jetpack Compose & Material3**: 선언형 UI 및 Material3 디자인 가이드를 준수하며, 다크/라이트 모드 테마 적용을 위해 시스템 컬러 토큰을 활용한다.
2. **보안성 확보**: Groq API Key 등 민감 정보는 `local.properties`와 `build.gradle.kts`에서 BuildConfig 필드로 관리하며 소스코드에 하드코딩하지 않는다.
3. **빌드 안정성**: 모든 개발/수정 단계에서 컴파일 빌드 및 런타임 안정성을 보장하며, 화면 전환 인수는 URLEncoder/Decoder를 사용해 특수문자에 예방 조치를 취한다.