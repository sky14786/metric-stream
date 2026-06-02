# metric-stream — Claude 컨텍스트

## 세션 시작 시 필수
> **`CLAUDE.md` → `PROGRESS.md`** 순서로 읽고 작업 시작.

---

## 사용자 프로필

- **이름**: 김준영 (JuneYoung Kim) / GitHub: https://github.com/sky14786
- **경력**: 5년+ 백엔드/인프라 엔지니어 (SI/솔루션)
- **주요 스택**: Spring Boot · Java · MySQL/MariaDB/PostgreSQL · Redis · Kafka · RabbitMQ · Flink · Docker · Nginx · Jenkins · Shell Script
- **성향**: 운영 안정성 우선. 혼자 전체 시스템 운영·문제해결에 강점. GitHub 개인 프로젝트 부족 → 이 프로젝트가 포트폴리오 역할.

---

## 개발 환경

- **OS**: Windows 11 (PowerShell 사용 — bash 명령어 지양)
- **Docker**: x86/x64 환경
- **빌드 도구**: Gradle (멀티모듈)
- **언어**: Java 17 + Spring Boot 3

---

## 프로젝트 배경

MAI-WACS(350대 서버 실시간 로그 수집 파이프라인) 실무 경험 기반 토이 프로젝트.

실무: `Telegraf → Kafka → Flink → TimescaleDB`
이 프로젝트: `API 로그 생성기(임의) → Kafka → Spring Boot Consumer → PostgreSQL`

- Telegraf 역할 → Spring Boot @Scheduled 임의 API 로그 생성으로 대체
- Flink → Spring Boot Consumer로 단순화

---

## 아키텍처

```
[metric-generator]  →  Kafka (server-api-logs)  →  [metric-consumer]  →  PostgreSQL
                                                                              ↓
                                                                      [metric-api] REST
```

**API 로그 스키마 (api_logs 테이블)**

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | |
| server_id | VARCHAR | 서버 식별자 (server-001 ~ server-020) |
| endpoint | VARCHAR | 요청 URI (/api/users 등) |
| method | VARCHAR | HTTP 메서드 |
| status_code | INTEGER | HTTP 상태 코드 |
| response_time_ms | INTEGER | 응답 시간 (ms) |
| timestamp | TIMESTAMPTZ | 로그 발생 시각 |

---

## 디렉토리 구조

```
metric-stream/
├── CLAUDE.md
├── PROGRESS.md
├── README.md
├── build.gradle          ← 루트 Gradle
├── settings.gradle       ← 멀티모듈 선언
├── docker-compose.yml
├── metric-generator/     ← API 로그 임의 생성 + Kafka produce
├── metric-consumer/      ← Kafka consume + PostgreSQL 저장
└── metric-api/           ← REST API 조회
```

---

## 세션 규칙

기능 완성마다 즉시:
1. `git commit & push`
2. `PROGRESS.md` 업데이트
3. `README.md` 업데이트
4. 스택·아키텍처 변경 시 `CLAUDE.md` 업데이트

---

## 코딩 컨벤션

- 패키지: `com.juneyoung.metricstream`
- 클래스명: PascalCase + 역할 명시 (`ApiLogProducer`, `ApiLogConsumerService`)
- 불필요한 주석 없음 — WHY가 명확할 때만 작성
- 커밋 prefix: `feat:` `fix:` `docs:` `chore:`
