# metric-stream

실시간 서버 API 로그 수집 파이프라인 토이 프로젝트.  
대규모 서버 환경의 실시간 로그 수집 실무 경험을 기반으로 구현.

## 아키텍처

```
[metric-generator]
  @Scheduled 임의 API 로그 생성
        │
        ▼ Kafka (server-api-logs)
[metric-consumer]
  Kafka 메시지 소비 → PostgreSQL 저장
        │
        ▼ PostgreSQL (api_logs)
[metric-api]
  REST API 조회 엔드포인트
        │
        ▼
[Grafana]
  실시간 모니터링 대시보드
```

## 스택

| 역할 | 기술 |
|------|------|
| 언어 / 프레임워크 | Java 17, Spring Boot 3.3 |
| 메시지 브로커 | Apache Kafka 3.9 (KRaft 모드) |
| 데이터베이스 | PostgreSQL 16 |
| 모니터링 | Grafana 10.4 |
| 빌드 | Gradle 8.8 (멀티모듈) |
| 인프라 | Docker Compose |

## 모듈 구성

```
metric-stream/
├── metric-generator/   # API 로그 임의 생성 + Kafka produce
├── metric-consumer/    # Kafka consume + PostgreSQL 저장
└── metric-api/         # REST API 조회
```

## 로컬 실행

### 1. 인프라 실행 (Kafka, PostgreSQL, Grafana)

```powershell
docker compose up kafka postgres grafana
```

| 서비스 | 접속 주소 |
|--------|-----------|
| Kafka (IDE용) | localhost:9094 |
| PostgreSQL | localhost:6432 |
| Grafana | http://localhost:3000 (admin / admin) |

### 2. 각 모듈 실행 (IntelliJ 또는 Gradle)

```powershell
# metric-generator
./gradlew :metric-generator:bootRun

# metric-consumer
./gradlew :metric-consumer:bootRun

# metric-api
./gradlew :metric-api:bootRun
```

### 3. 전체 Docker 실행 (배포 환경)

```powershell
docker compose up -d
```

## API 명세

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | /metrics | 전체 로그 조회 (페이지네이션) |
| GET | /metrics/{serverId} | 서버별 로그 조회 |
| GET | /metrics/{serverId}/latest | 서버 최신 로그 |

## DB 스키마

```sql
CREATE TABLE api_logs (
    id               BIGSERIAL PRIMARY KEY,
    server_id        VARCHAR NOT NULL,
    endpoint         VARCHAR NOT NULL,
    method           VARCHAR NOT NULL,
    status_code      INTEGER NOT NULL,
    response_time_ms INTEGER NOT NULL,
    timestamp        TIMESTAMPTZ NOT NULL
);
```

## 로그 형식

Kafka 토픽 `server-api-logs`에 발행되는 메시지 샘플 (JSON):

```json
{
  "requestId": "a3f2c1d4-8e7b-4a9f-b2c3-1d4e5f6a7b8c",
  "serverId": "server-007",
  "region": "seoul",
  "method": "GET",
  "endpoint": "/api/orders",
  "statusCode": 200,
  "responseTimeMs": 142,
  "requestSize": 512,
  "responseSize": 4096,
  "deviceType": "MOBILE",
  "errorMessage": null,
  "timestamp": "2026-06-07T09:31:22.481Z"
}
```

500 에러 케이스 (`errorMessage` 포함):

```json
{
  "requestId": "9b8c7d6e-5f4a-3b2c-1d0e-9f8a7b6c5d4e",
  "serverId": "server-014",
  "region": "daejeon",
  "method": "POST",
  "endpoint": "/api/auth/login",
  "statusCode": 500,
  "responseTimeMs": 1823,
  "requestSize": 256,
  "responseSize": 128,
  "deviceType": "DESKTOP",
  "errorMessage": "Database connection failed",
  "timestamp": "2026-06-07T09:31:23.105Z"
}
```

생성 규칙: 1초마다 10~20개 / 20개 서버, 4개 리전(seoul·busan·daejeon·incheon), 10개 엔드포인트 조합  
상태코드 분포: 200(65%) · 201(10%) · 400(8%) · 404(8%) · 401(4%) · 500(5%)
