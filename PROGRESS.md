# metric-stream — 작업 진행 현황

## ⚠️ 세션 규칙 (필수)
> **기능 하나 완성될 때마다 즉시:**
> 1. `PROGRESS.md` 작업 내역 및 다음 할 일 업데이트
> 2. 변경 파일 전체 GitHub 커밋 & 푸시
> 3. `README.md` 변경 내용 반영
> 4. 다음 세션 시작 시 `CLAUDE.md` → 이 파일 순서로 읽고 이어서 작업

---

## 프로젝트 개요

- **목적**: 실무(MAI-WACS) 기반 실시간 서버 메트릭 수집 파이프라인 토이 프로젝트
- **GitHub**: https://github.com/sky14786/metric-stream
- **스택**: Java 17, Spring Boot 3.3.5, Kafka 3.7 (KRaft), PostgreSQL 16, Grafana 10.4, Docker Compose, GitHub Actions
- **배포 목표**: Oracle Cloud Free Tier (ARM VM)

---

## 구현 단계

### Phase 1 — 기반 구성 ✅
- [x] 프로젝트 멀티 모듈 구조 생성 (metric-generator, metric-consumer, metric-api)
- [x] docker-compose.yml — Kafka (KRaft), PostgreSQL, Grafana
- [x] Grafana PostgreSQL 데이터소스 프로비저닝 자동 설정
- [x] README.md 초안

### Phase 2 — 핵심 파이프라인 ✅
- [x] metric-generator — API 로그 임의 생성 + Kafka produce (5초마다 3~7개)
- [x] metric-consumer — Kafka consume + PostgreSQL 저장
- [x] DB 스키마 — api_logs 테이블 (JPA Entity + 인덱스)

### Phase 3 — API
- [ ] metric-api — 메트릭 조회 REST API
  - `GET /metrics` — 전체 조회 (페이지네이션)
  - `GET /metrics/{serverId}` — 서버별 조회
  - `GET /metrics/{serverId}/latest` — 최신 메트릭

### Phase 4 — 마무리
- [ ] Grafana 대시보드 JSON 프로비저닝 (커스텀 패널 구성)
- [ ] Nginx 리버스 프록시 docker-compose에 추가
- [ ] GitHub Actions CI (빌드 + 테스트)
- [ ] README.md 완성 (아키텍처 다이어그램 포함)

---

## 완료된 작업

### Phase 1 (2026-06-05)
- [x] GitHub 레포 생성 (MIT 라이센스)
- [x] CLAUDE.md 작성
- [x] PROGRESS.md 작성
- [x] docker-compose.yml — Kafka KRaft, PostgreSQL 16, Grafana 10.4
  - Kafka 이중 리스너: 9092(Docker 내부) / 9094(로컬 IDE)
- [x] Grafana provisioning/datasources/datasource.yml — PostgreSQL 자동 연결
- [x] Gradle 멀티모듈 구조 — settings.gradle, 루트 build.gradle, 각 모듈 build.gradle
- [x] 각 모듈 메인 클래스 + application.yml 초안
- [x] README.md 초안

---

## 다음 작업

**Phase 3 — metric-api REST 엔드포인트 구현**

1. `ApiLog` 조회용 Entity 참조 (consumer 모듈과 DB 공유)
2. `GET /metrics` — 전체 조회 (페이지네이션)
3. `GET /metrics/{serverId}` — 서버별 조회
4. `GET /metrics/{serverId}/latest` — 최신 1건
