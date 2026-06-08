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
- **스택**: Java 17, Spring Boot 3.3.5, Kafka 3.9 (KRaft), PostgreSQL 16, Grafana 10.4, Docker Compose
- **배포**: GCP e2-micro (us-central1) 완료 → https://skymetric.ddns.net

---

## 구현 단계

### Phase 1 — 기반 구성 ✅
- [x] 멀티 모듈 구조 생성 (metric-generator, metric-consumer, metric-api)
- [x] docker-compose.yml — Kafka (KRaft), PostgreSQL, Grafana
- [x] Grafana PostgreSQL 데이터소스 프로비저닝 자동 설정
- [x] README.md 초안

### Phase 2 — 핵심 파이프라인 ✅
- [x] metric-generator — API 로그 임의 생성 + Kafka produce (1초마다 10~20개, 분당 ~900건)
- [x] metric-consumer — Kafka consume + PostgreSQL 저장
- [x] DB 스키마 — api_logs 테이블

### Phase 3 — API ✅
- [x] metric-api — 메트릭 조회 REST API (포트 8080)

### Phase 4 — 배포 ✅
- [x] Grafana 대시보드 JSON 프로비저닝 (12개 패널)
- [x] PostgreSQL 호스트 포트 5432 → 6432 변경 (Hyper-V 충돌 대응)
- [x] Grafana 익명 접근 + 임베딩 허용 설정
- [x] resume-ai v7 Projects 섹션에 Grafana 임베딩 (localhost 기준)
- [x] Dockerfile × 3 (gradle:8.8-jdk17-alpine 멀티스테이지)
- [x] docker-compose.yml 자원 제한 + Kafka HEAP 튜닝 + healthcheck
- [x] Nginx 리버스 프록시 (nginx/nginx.conf + production 프로필)
- [x] GitHub Actions CI (.github/workflows/ci.yml)
- [x] GCP e2-micro 인스턴스 생성 (us-central1, Ubuntu 22.04, 30GB 표준 영구 디스크)
- [x] 스왑 500MB 추가
- [x] Docker 설치
- [x] No-IP DDNS 설정 (skymetric.ddns.net)
- [x] certbot Let's Encrypt SSL 발급
- [x] Kafka healthcheck TCP 포트 체크로 교체 (e2-micro 대응)
- [x] docker compose --profile production 전체 기동 확인
- [x] SSH 비밀번호 로그인 차단 (PasswordAuthentication no)
- [x] GCP 빌링 알림 설정
- [x] README.md 완성 (Live Demo URL, CI 뱃지 포함)

---

## 다음 작업 (다음 세션 최우선)

1. **서버 재기동 후 컨테이너 올리기**
   ```bash
   cd ~/metric-stream && docker compose --profile production up -d
   ```

2. **metric-api production 프로필에서 제거**
   - Grafana가 PostgreSQL 직접 쿼리 → metric-api 불필요
   - 제거 시 192MB 확보, e2-micro 안정성 향상

3. **kafka / postgres / grafana에 `restart: unless-stopped` 추가**
   - 현재 시스템 재부팅 후 이 3개는 자동 기동 안 됨

4. **resume-ai Grafana URL 교체**
   - `localhost:3000` → `https://skymetric.ddns.net`
   - resume-ai v7/index.html iframe src 변경 후 배포

5. **No-IP 30일마다 갱신 확인**
   - 이메일로 활성 확인 안 하면 호스트 삭제됨

---

## 포트 설정

- Kafka: 9092 (Docker 내부), 9094 (로컬 IDE)
- PostgreSQL: **6432** (호스트) → 5432 (컨테이너)
- Grafana: 3000 / 공개 URL: https://skymetric.ddns.net
- metric-api: 8080 (사용 안 함, 다음 세션에 제거 예정)

---

## 로컬 개발 서버

```bash
# 로컬 개발 (인프라만)
docker compose up -d

# GCP 프로덕션 (서버에서)
docker compose --profile production up -d
```
