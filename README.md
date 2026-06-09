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

## 주요 기능

* 위치 정보 수집
* 알림 수신 동의
* 소비 정보 입력
* AI 소비 성향 분석
* 소비 리포트 제공
* 결과 시각화
* 다크 모드 / 라이트 모드 지원

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
