# InvoiceTalk

송장을 미리 출력한 뒤, 모바일로 스캔하여 편하게 택배를 발송 처리할 수 있도록 지원하는 스마트 송장처리 서비스입니다.

---

## 주요 기능

- 송장을 스캔하여 간편한 택배 발송 처리 및 이력 관리
- Android 기반 모바일 최적화 사용자 경험 제공

---

## 프로젝트 구조

이 프로젝트는 **Clean Architecture** 원칙에 따라 아래와 같이 4개의 모듈로 구성되어 있습니다.

- **app**: 앱의 진입점 및 DI, 설정 등
- **data**: API/DB와 직접 통신, Repository 패턴 구현
- **domain**: 비즈니스 규칙, 순수 코틀린 계층 (UseCase, Entity 등)
- **presentation**: 화면 UI, ViewModel, Jetpack Compose 등 UI 코드

---

## 기술 스택 및 라이브러리

- **언어**: Kotlin
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **아키텍처**: Clean Architecture
- **DI**: Dagger Hilt
- **데이터**: Room, DataStore
- **네트워크**: Retrofit, OkHttp
- **비동기**: Kotlin Coroutines, Flow
- **빌드 시스템**: Gradle 및 KSP (Kotlin Symbol Processing)

---

## Jetpack Compose 활용

- 전 UI를 Jetpack Compose 기반으로 구현하여 선언적 UI, 재사용성 및 동적 컴포넌트 구조를 강화하였습니다.
- State 관리, Navigation, 리스트/폼 등 모든 화면을 Compose로 작성

---

## 프로젝트 빌드 및 실행

1. 깃허브 저장소를 클론
2. Android Studio 최신 버전에서 Open
3. `./gradlew clean build` 혹은 IDE에서 Run

