# metric-stream — 작업 진행 현황

## ⚠️ 세션 규칙 (필수)
> **기능 하나 완성될 때마다 즉시:**
> 1. `PROGRESS.md` 작업 내역 및 다음 할 일 업데이트
> 2. 변경 파일 전체 GitHub 커밋 & 푸시
> 3. `README.md` 변경 내용 반영
> 4. 다음 세션 시작 시 `CLAUDE.md` → 이 파일 순서로 읽고 이어서 작업

---

## 프로젝트 개요

- **목적**: 실시간 서버 API 로그 수집 파이프라인 토이 프로젝트 (포트폴리오)
- **GitHub**: https://github.com/sky14786/metric-stream
- **스택**: Java 17, Spring Boot 3.3.5, Kafka 3.9 (KRaft, apache/kafka), PostgreSQL 16, Grafana 10.4, Docker Compose
- **배포 목표**: Oracle Cloud Free Tier (ARM VM)

---

## 구현 단계

### Phase 1 — 기반 구성 ✅
- [x] 프로젝트 멀티 모듈 구조 생성 (metric-generator, metric-consumer, metric-api)
- [x] docker-compose.yml — Kafka (KRaft), PostgreSQL, Grafana
- [x] Grafana PostgreSQL 데이터소스 프로비저닝 자동 설정
- [x] README.md 초안

### Phase 2 — 핵심 파이프라인 ✅
- [x] metric-generator — API 로그 임의 생성 + Kafka produce (1초마다 10~20개, 분당 ~900건)
  - 20개 서버 / 4개 리전 고정 매핑 / 가중치 기반 상태코드 분포
  - 필드: requestId, serverId, region, endpoint, method, statusCode, responseTimeMs, requestSize, responseSize, deviceType, errorMessage, timestamp
- [x] metric-consumer — Kafka consume + PostgreSQL 저장
- [x] DB 스키마 — api_logs 테이블 (JPA Entity + timestamp/serverId/region 인덱스)

### Phase 3 — API ✅
- [x] metric-api — 메트릭 조회 REST API (포트 8080)
  - `GET /metrics` — 전체 조회 (페이지네이션, 최신순)
  - `GET /metrics/{serverId}` — 서버별 조회
  - `GET /metrics/{serverId}/latest` — 최신 1건

### Phase 4 — 마무리 (진행 중)
- [x] Grafana 대시보드 JSON 프로비저닝 (12개 패널)
- [x] PostgreSQL 호스트 포트 5432 → 6432 변경 (Hyper-V 동적 포트 예약 충돌 대응)
- [x] Grafana 익명 접근 + 임베딩 허용 설정 (GF_AUTH_ANONYMOUS_ENABLED, GF_SECURITY_ALLOW_EMBEDDING)
- [x] 패널 타이틀 단축 (총 요청 수→요청 수, 에러 건수→에러 수, 평균 응답시간→응답시간)
- [x] resume-ai v7 Projects 섹션에 Grafana 임베딩 (카드 프리뷰 + 모달 fullscreen 대시보드)
- [ ] Nginx 리버스 프록시 docker-compose에 추가
- [ ] GitHub Actions CI (빌드 + 테스트)
- [ ] README.md 완성 (아키텍처 다이어그램 포함)

---

## 완료된 작업

### Phase 1 (2026-06-05)
- [x] GitHub 레포 생성 (MIT 라이센스)
- [x] CLAUDE.md, PROGRESS.md 작성
- [x] docker-compose.yml — Kafka KRaft (apache/kafka:3.9.0), PostgreSQL 16, Grafana 10.4
  - Kafka 이중 리스너: 9092(Docker 내부) / 9094(로컬 IDE)
- [x] Grafana provisioning/datasources/datasource.yml
- [x] Gradle 멀티모듈 구조 + 각 모듈 메인 클래스 + application.yml
- [x] README.md 초안

### Phase 2 (2026-06-05)
- [x] ApiLog DTO (generator), ApiLog JPA Entity (consumer)
- [x] ApiLogProducer (KafkaTemplate), ApiLogScheduler (@Scheduled 1초)
- [x] ApiLogConsumer (@KafkaListener), ApiLogRepository
- [x] JacksonConfig @Bean (generator, consumer 공통)
- [x] 동작 확인: Grafana Explore에서 분당 ~900건 적재 확인

### Phase 3 (2026-06-05)
- [x] MetricController (GET /metrics, /{serverId}, /{serverId}/latest)
- [x] ApiLogResponse DTO (record), ApiLogRepository (metric-api)

### Phase 4 — 추가 작업 (2026-06-05)
- [x] PostgreSQL 호스트 포트 6432로 변경, application.yml 동기화
- [x] Grafana 익명 접근 + 임베딩 허용 (docker-compose env 추가)
- [x] 패널 타이틀 단축, resume-ai v7에 d-solo iframe + 모달 임베딩 완료

### Phase 4 (2026-06-05)
- [x] Grafana 대시보드 12개 패널 프로비저닝
  - Stat: 총 요청 수(스파크라인) / 에러 건수(스파크라인) / 에러율 / 평균 응답시간
  - Time series: 시간대별 요청+에러 오버레이 / 에러율 추이 / 리전별 평균 응답시간
  - Donut: 상태코드 분포 / 디바이스 타입
  - Bar: 엔드포인트 TOP 10 / 리전별 트래픽
  - Table: 최근 500 에러 (응답시간 컬러 배경)

---

## 다음 작업

**Phase 4 마무리**
1. Nginx 리버스 프록시 (docker-compose에 추가)
2. GitHub Actions CI (Gradle 빌드)
3. README.md 완성 (아키텍처 다이어그램 포함)

**이후 작업**
- Oracle Cloud Free Tier 배포 (공개 URL 확보 후 resume-ai 링크 교체)
