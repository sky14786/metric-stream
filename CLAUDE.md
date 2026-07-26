# metric-stream — Claude 컨텍스트

## 세션 시작 시 필수
> **`CLAUDE.md` → `PROGRESS.md`** 순서로 읽고 작업 시작.

환경·커밋·시크릿·코드 작성 원칙은 상위 `~/project/CLAUDE.md`에 있고 이 세션에도 함께
로드됩니다(2026-07-26 실측). 이 파일에는 **metric-stream에만 해당하는 것**만 둡니다.

---

## 프로젝트 배경

대규모 서버 환경 실시간 로그 수집 파이프라인 실무 경험 기반 토이 프로젝트.
**포트폴리오 역할**을 겸하므로, 동작만 하는 것보다 남에게 보여줄 수 있는 구성을 택합니다.

실무: `Telegraf → Kafka → Flink → TimescaleDB`
이 프로젝트: `API 로그 생성기(임의) → Kafka → Spring Boot Consumer → PostgreSQL`

- Telegraf 역할 → Spring Boot @Scheduled 임의 API 로그 생성으로 대체
- Flink → Spring Boot Consumer로 단순화

**스택**: Java 17 · Spring Boot 3 · Gradle 멀티모듈 · Kafka · PostgreSQL

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

## 코딩 컨벤션

- 패키지: `com.juneyoung.metricstream`
- 클래스명: PascalCase + 역할 명시 (`ApiLogProducer`, `ApiLogConsumerService`)
- 불필요한 주석 없음 — WHY가 명확할 때만 작성

스택·아키텍처가 바뀌면 이 파일도 같이 고칩니다.
