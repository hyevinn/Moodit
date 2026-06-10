# F-10 브랜드 스플래시 화면 제공 기능

## 설명

시스템은 앱 최초 실행 단계에서 Moodit 브랜드의 마스코트 구름 캐릭터를 표현한 스플래시 화면을 노출하고, 로딩 완료 후 메인 홈 화면 테마로 유연하게 화면을 인계한다.

## Acceptance Criteria

- [ ] Android 12(API 31) 이상 플랫폼의 스플래시 API 요건을 충족하도록 `androidx.core:core-splashscreen` 라이브러리를 연동하여 동작한다.
- [ ] `MainActivity.kt`의 `onCreate()` 메소드 내 `super.onCreate()` 직전에 `installSplashScreen()`을 호출하여 스플래시 수명주기를 설정한다.
- [ ] 스플래시 화면 아이콘(`windowSplashScreenAnimatedIcon`)에 기존 홈 화면용 `today_character.png` 대신, 사방(특히 하단부)에 충분한 안전 마진 투명 여백이 사전 결합된 스플래시 전용 이미지인 `@drawable/today_character_splash`를 적용한다.
- [ ] 이 전용 이미지를 통해 Android 시스템의 원형 마스크가 적용되어 렌더링될 때 구름 캐릭터의 하단 둥근 부위나 측면이 칼로 잘려 나간 것처럼 납작하게 왜곡되는 현상을 완전히 방지한다.
- [ ] 캐릭터는 스플래시 중앙에 위치하며, 전체 108dp 아이콘 지름 영역 중 약 60~70% 비율 크기로 아기자기하게 노출되어 가독성과 미적 완성도를 확보한다.
- [ ] 스플래시 화면의 배경색(`windowSplashScreenBackground`)은 흰색(`@android:color/white`)을 유지한다.
- [ ] 스플래시 노출이 끝나면 지정된 포스트 테마(`postSplashScreenTheme` 설정인 `@style/Theme.Moodit.Main`)를 통해 메인 액티비티로 끊김 현상 없이 테마를 자연스럽게 이관 전환한다.
