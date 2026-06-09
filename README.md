# metric-stream

![CI](https://github.com/sky14786/metric-stream/actions/workflows/ci.yml/badge.svg)

실시간 서버 API 로그 수집 파이프라인 토이 프로젝트.  
대규모 서버 환경의 실시간 로그 수집 실무 경험을 기반으로 구현.

**Live Demo**: https://skydev.ddns.net/metric

## 아키텍처

```
[metric-generator]  →  Kafka (server-api-logs)  →  [metric-consumer]  →  PostgreSQL
                                                                              ↓
                                                                          Grafana
                                                                              ↓
                                                                    Nginx (HTTPS)
                                                             https://skydev.ddns.net/metric
```

## 스택

| 역할 | 기술 |
|------|------|
| 언어 / 프레임워크 | Java 17, Spring Boot 3.3 |
| 메시지 브로커 | Apache Kafka 3.9 (KRaft 모드) |
| 데이터베이스 | PostgreSQL 16 |
| 모니터링 | Grafana 10.4 |
| 빌드 | Gradle 8.8 (멀티모듈) |
| 인프라 | Docker Compose, Nginx |
| 배포 | VirtualBox Ubuntu Server (로컬 홈서버) |
| CI | GitHub Actions |

## 모듈 구성

```
metric-stream/
├── metric-generator/   # API 로그 임의 생성 + Kafka produce
├── metric-consumer/    # Kafka consume + PostgreSQL 저장
└── metric-api/         # REST API 조회
```

## 로컬 실행

### 1. 인프라 실행 (Kafka, PostgreSQL, Grafana)

```bash
docker compose up -d
```

| 서비스 | 접속 주소 |
|--------|-----------|
| Kafka (IDE용) | localhost:9094 |
| PostgreSQL | localhost:6432 |
| Grafana | http://localhost:3000 (admin / admin) |

### 2. 각 모듈 실행

```bash
./gradlew :metric-generator:bootRun
./gradlew :metric-consumer:bootRun
./gradlew :metric-api:bootRun
```

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

## 로그 생성 규칙

- 1초마다 10~20개 / 분당 약 900건
- 20개 서버, 4개 리전, 10개 엔드포인트 조합
- 상태코드 분포: 200(65%) · 201(10%) · 400(8%) · 404(8%) · 401(4%) · 500(5%)

## 배포 인프라

| 항목 | 내용 |
|------|------|
| 서버 | VirtualBox Ubuntu Server 24.04 (로컬 홈서버) |
| 공인 접속 | No-IP DDNS → skydev.ddns.net |
| SSL | certbot Let's Encrypt (자동 갱신 훅 설정) |
| 포트포워딩 | 공유기 80/443 → 192.168.0.26 |

### Nginx 서브패스 라우팅

nginx 단일 컨테이너가 `/metric/` (Grafana)과 `/redstone/` (별도 앱) 두 서비스를 경로 기반으로 분기한다.

```
https://skydev.ddns.net/metric/    → Grafana (proxy_pass http://grafana:3000)
https://skydev.ddns.net/redstone/  → redstone 앱 (proxy_pass https://redstone:443)
```

redstone 앱은 절대 경로(`/api/...`, `href="/..."`)를 사용하도록 구현되어 있어, nginx `sub_filter`로 응답 HTML/JS의 경로를 `/redstone/` 접두사 포함 형태로 실시간 교체한다.  
템플릿 리터럴(backtick) URL 패턴(`` `/api/ `` → `` `/redstone/api/ ``)도 별도 rule로 처리.

두 서비스는 `proxy-net` Docker 외부 네트워크를 통해 통신한다.
