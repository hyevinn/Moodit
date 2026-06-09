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
* AI 소비 성향 분석 및 최근 분석 결과 로컬 자동 저장
* 최근 소비 분석 결과 연동 및 메인 화면 자동 표시 (앱 재실행 시 자동 복원)
* 소비 리포트 제공
* 결과 시각화
* 다크 모드 / 라이트 모드 지원

## 앱 동작 흐름 (Application Flow)

1. **메인 화면 (MainScreen)**: 앱 최초 진입 시 기본 가이드라인 소비 유형("자기보상형 소비" 등)이 표시됩니다. 이후 분석을 완료하면 로컬 Preferences DataStore에 누적 저장된 최근 분석 결과 데이터(소비 유형, 설명, 카테고리, 금액대, 소비 이유)가 자동으로 복원되어 표시됩니다.
2. **소비 입력 (InputScreen)**: 사용자가 카테고리, 금액대, 소비 이유 및 선택적 메모를 입력하고 위치 정보를 조회합니다.
3. **분석 중 (LoadingScreen)**: AI 분석 중 로딩 애니메이션을 제공합니다.
4. **결과 화면 (ResultScreen)**: GroqCloud API를 활용해 소비 리포트를 제공하며, 동시에 분석 결과 및 입력 데이터를 `DataStoreManager`를 통해 Preferences DataStore에 영구 저장합니다.

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
