# Spring Modulith 시각화 가이드

## 개요
이 프로젝트는 Spring Modulith를 사용하여 모듈러 모놀리스 아키텍처를 구현합니다.

## 모듈 구조 확인하기

### 1. 테스트를 통한 모듈 시각화
다음 명령어로 모듈 구조를 확인할 수 있습니다:

```bash
./gradlew test --tests ModulithVisualizationTest
```

### 2. 생성되는 문서들
테스트 실행 후 `build/spring-modulith-docs/` 디렉토리에 다음 파일들이 생성됩니다:

- **all-docs.adoc**: 전체 모듈 문서
- **components.puml**: PlantUML 컴포넌트 다이어그램
- **module-api.puml**: API 모듈 다이어그램
- **module-config.puml**: Config 모듈 다이어그램

### 3. PlantUML 다이어그램 보기
`.puml` 파일들은 PlantUML 형식으로 작성되어 있습니다. 다음 방법으로 시각화할 수 있습니다:

#### 온라인 PlantUML 에디터
1. http://www.plantuml.com/plantuml/uml/ 방문
2. `.puml` 파일 내용을 복사하여 붙여넣기
3. 다이어그램 확인

#### VS Code 확장
1. "PlantUML" 확장 설치
2. `.puml` 파일 열기
3. `Alt+D`로 미리보기

### 4. 현재 감지된 모듈들

#### API 모듈 (`com.edumanager.application.api`)
- GlobalRestController
- HealthCheckController

#### Config 모듈 (`com.edumanager.application.config`) 
- DatabaseConfig
- KafkaConfig  
- KafkaTopicConfig
- ModulithConfig
- OpenApiConfig
- RedisConfig
- SecurityConfig
- WebConfig

## 모듈 경계 검증
Spring Modulith는 자동으로 모듈 경계를 검증합니다:
- 모듈 간 순환 의존성 방지
- 허용되지 않은 패키지 접근 차단
- 이벤트 기반 통신 강제

## 추가 모듈 추가 시 주의사항
1. 각 모듈은 독립적인 패키지 구조를 가져야 함
2. 직접적인 의존성 대신 이벤트 기반 통신 사용
3. `@DomainService`, `@ApplicationService` 등의 어노테이션 활용