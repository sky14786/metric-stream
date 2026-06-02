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
- **스택**: Java 17, Spring Boot 3, Kafka, PostgreSQL, Docker Compose, GitHub Actions

---

## 구현 단계

### Phase 1 — 기반 구성
- [ ] 프로젝트 멀티 모듈 구조 생성 (metric-generator, metric-consumer, metric-api)
- [ ] docker-compose.yml — Kafka, Zookeeper, PostgreSQL
- [ ] README.md 초안

### Phase 2 — 핵심 파이프라인
- [ ] metric-generator — 서버 메트릭 임의 생성 + Kafka produce (30초 간격)
- [ ] metric-consumer — Kafka consume + PostgreSQL 저장
- [ ] DB 스키마 — api_logs 테이블

### Phase 3 — API
- [ ] metric-api — 메트릭 조회 REST API
  - `GET /metrics` — 전체 조회 (페이지네이션)
  - `GET /metrics/{serverId}` — 서버별 조회
  - `GET /metrics/{serverId}/latest` — 최신 메트릭

### Phase 4 — 마무리
- [ ] Nginx 리버스 프록시 docker-compose에 추가
- [ ] GitHub Actions CI (빌드 + 테스트)
- [ ] README.md 완성 (아키텍처 다이어그램 포함)

---

## 완료된 작업

### 초기 세팅 (2026-06-02)
- [x] GitHub 레포 생성 (MIT 라이센스)
- [x] CLAUDE.md 작성 — 사용자 프로필, 프로젝트 배경, 스택, 세션 규칙
- [x] PROGRESS.md 작성

---

## 다음 작업

**Phase 1부터 순서대로 진행.**  
다음 세션 시작 시 Phase 1 — 멀티 모듈 프로젝트 구조 생성부터.
