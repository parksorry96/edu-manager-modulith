# Edu Manager Modulith

교육 관리 시스템 - Spring Boot Modulith 아키텍처 기반

## 🚀 빠른 시작

### 1. 환경 설정

#### Docker 컨테이너 시작
```bash
# 모든 서비스 시작
docker-compose up -d

# 또는 특정 서비스만 시작
docker-compose up -d postgres mongodb redis kafka
```

#### 데이터베이스 초기화 (최초 1회만)
```bash
# 데이터베이스 스키마 생성
./gradlew bootRun --args='--spring.profiles.active=init'
```

### 2. 애플리케이션 실행

#### 개발 모드 (기본)
```bash
./gradlew bootRun
# 또는
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### 프로덕션 모드
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## 📁 프로젝트 구조

```
edu-manager-modulith/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/edumanager/
│   │       ├── application/    # 애플리케이션 레이어
│   │       ├── shared/         # 공유 모듈
│   │       ├── user/           # 사용자 모듈
│   │       └── student/        # 학생 모듈
│   └── src/main/resources/
│       ├── application.yml     # 기본 설정
│       ├── application-dev.yml # 개발 환경 설정
│       └── application-init.yml # 초기화 설정
├── frontend/                   # React 프론트엔드
└── docker-compose.yml         # Docker 서비스 설정
```

## 🔧 설정 파일

### 프로파일별 설정

- **dev**: 개발 환경 (기본값)
  - `ddl-auto: validate` - 기존 스키마 유지
  - 디버그 로그 활성화

- **init**: 초기 설정
  - `ddl-auto: create-drop` - 스키마 재생성
  - 최초 1회만 사용

- **prod**: 프로덕션 환경
  - `ddl-auto: validate` - 스키마 검증만
  - 로그 레벨 최적화

## 🗄️ 데이터베이스

### PostgreSQL (메인 DB)
- **포트**: 5432
- **데이터베이스**: edu_manager
- **사용자**: edu_admin
- **비밀번호**: edu_password_dev

### MongoDB (문서 저장)
- **포트**: 27017
- **데이터베이스**: edu_manager_docs
- **사용자**: admin
- **비밀번호**: mongodb_password_dev

### Redis (캐시)
- **포트**: 6379
- **비밀번호**: redis_password_dev

### Kafka (메시지 브로커)
- **포트**: 9092
- **UI**: http://localhost:8080

## 🛠️ 개발 도구

### pgAdmin (PostgreSQL 관리)
- **URL**: http://localhost:5050
- **이메일**: admin@edumanager.com
- **비밀번호**: pgadmin_password

### MongoDB Express (MongoDB 관리)
- **URL**: http://localhost:8081

## 🔍 문제 해결

### 데이터베이스 초기화 문제
```bash
# 데이터베이스 완전 초기화
docker-compose down -v
docker-compose up -d postgres
./gradlew bootRun --args='--spring.profiles.active=init'
```

### Kafka 연결 문제
```bash
# Kafka 컨테이너 재시작
docker-compose restart kafka
```

## 📝 주요 기능

- ✅ JWT 인증/인가
- ✅ Spring Security
- ✅ Spring Data JPA
- ✅ Spring Modulith
- ✅ Kafka 메시징
- ✅ Redis 캐싱
- ✅ MongoDB 문서 저장
- ✅ Docker 컨테이너화

## 🚀 배포

### 개발 환경
```bash
./gradlew bootRun
```

### 프로덕션 환경
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## 📞 지원

문제가 발생하면 다음을 확인하세요:
1. Docker 컨테이너 상태: `docker ps`
2. 애플리케이션 로그: `docker logs edu-manager-kafka`
3. 데이터베이스 연결 상태 