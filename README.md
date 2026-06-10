# Moodit

AI 기반 소비 성향 분석 앱

## 프로젝트 소개

Moodit은 사용자의 소비 정보를 기반으로 소비 유형과 소비 성향을 분석하는 모바일 애플리케이션이다.

사용자가 선택형 소비 정보를 입력하면 GroqCloud API를 활용하여 소비 습관과 소비 특징을 분석하고, 결과를 리포트 형태로 제공한다.

## 기술 스택

* Kotlin
* Jetpack Compose
* Material3
* Navigation Compose
* GroqCloud API
* Preferences DataStore (최근 분석 결과 로컬 영구 저장)

## 주요 기능

* 위치 정보 수집
* 알림 수신 동의
* 소비 정보 입력
* **✨ 최근 소비 인사이트**: 가장 최근 소비 분석 리포트의 조언을 추출하여 홈 화면 상단에 실시간으로 요약 표시 (내용 길이에 따라 카드가 자동으로 늘어나며, 말줄임 없이 전부 노출)
* **AI 소비 성향 분석**: GroqCloud API 기반의 소비 성향 분석 및 한국어 명사 형태의 소비 유형 출력 정책 적용 (한자/일본어 및 불필요한 외국어 표현 사용 배제)
* **최근 분석 결과 및 인사이트 로컬 영구 저장**: Preferences DataStore 기반으로 최근 소비 분석 결과와 최근 소비 인사이트를 로컬 저장하여 앱 재실행 시 완벽히 복원
* 소비 리포트 제공 및 결과 시각화
* 다크 모드 / 라이트 모드 지원

## 앱 동작 흐름 (Application Flow)

1. **메인 화면 (MainScreen)**:
   * **✨ 최근 소비 인사이트**: 앱 최초 진입 시에는 기본 가이드라인 문구가 보이며, 새로운 소비 성향 분석을 완료하면 DataStore로부터 최신 인사이트 조언 문장을 읽어와 실시간으로 표시합니다.
   * **최근 분석 결과**: 이전 분석 내용이 있다면 DataStore에 누적된 결과(소비 유형, 설명, 카테고리, 금액대, 소비 이유)가 복원됩니다. 캐릭터는 카드 높이에 맞춰 세로 중앙에 정렬됩니다.
2. **소비 입력 (InputScreen)**: 사용자가 카테고리, 금액대, 소비 이유 및 선택적 메모를 입력하고 위치 정보를 조회합니다.
3. **분석 중 (LoadingScreen)**: AI 분석 중 로딩 애니메이션을 제공합니다.
4. **결과 화면 (ResultScreen)**: GroqCloud API를 호출하여 자연스러운 한글 소비 리포트를 제공하며, 성공 시 결과 데이터와 핵심 한 줄 조언을 `DataStoreManager`를 통해 Preferences DataStore에 즉시 저장합니다.

## 프로젝트 문서

* AGENT.md : 프로젝트 구조 및 개발 규칙
* features/ : 기능 요구사항 및 Acceptance Criteria 문서

## 실행 환경

* Android
* Kotlin
* Jetpack Compose
* Material3

## 환경 설정

* Groq API Key는 보안을 위해 GitHub에 포함하지 않는다.

* 프로젝트 실행 전 local.properties 파일에 다음 항목을 추가해야 한다.

* GROQ_API_KEY=YOUR_API_KEY
