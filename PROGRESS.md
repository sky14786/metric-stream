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
- **배포**: VirtualBox Ubuntu VM (홈서버) → https://skydev.ddns.net/metric

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
- [x] Dockerfile × 3 (gradle:8.8-jdk17-alpine 멀티스테이지)
- [x] docker-compose.yml 자원 제한 + Kafka HEAP 튜닝 + healthcheck
- [x] Nginx 리버스 프록시 (경로 기반: /metric/)
- [x] GitHub Actions CI (.github/workflows/ci.yml)
- [x] VirtualBox Ubuntu Server VM (4코어, 4GB RAM, 50GB)
- [x] No-IP DDNS 설정 (skydev.ddns.net)
- [x] certbot Let's Encrypt SSL 발급
- [x] 공유기 포트포워딩 (80/443 → VM 192.168.0.26)
- [x] Grafana 서브패스 설정 (GF_SERVER_SERVE_FROM_SUB_PATH=true)
- [x] nginx proxy_pass 경로 유지로 리다이렉트 루프 해결
- [x] docker-compose.yml 메모리 상향 (VM 환경 OOM 대응)
- [x] 전체 파이프라인 기동 확인 (https://skydev.ddns.net/metric)
- [x] /redstone 경로 추가 (proxy-net 공유 네트워크, nginx subpath 라우팅)
- [x] nginx sub_filter로 redstone 앱 절대경로 → /redstone/ 접두사 자동 교체
- [x] resume-ai Grafana iframe URL → https://skydev.ddns.net/metric 변경

---

## 다음 작업

1. **certbot 자동 갱신 확인**
   - `sudo certbot renew --dry-run`

2. **No-IP 30일마다 갱신 확인**
   - 이메일로 활성 확인 안 하면 호스트 삭제됨

3. **VM 재부팅 후 컨테이너 자동 재시작 확인**
   - restart: unless-stopped 설정 돼 있음

---

## 인프라 구성

- **VM**: admin@trade (192.168.0.26), Ubuntu 24.04
- **공인 IP**: 공유기 DDNS (No-IP) → skydev.ddns.net
- **포트포워딩**: 80/443 → 192.168.0.26
- **서비스 포트**:
  - Kafka: 9092 (내부), 9094 (로컬 IDE)
  - PostgreSQL: 6432 (호스트) → 5432 (컨테이너)
  - Grafana: 3000 / 공개 URL: https://skydev.ddns.net/metric

---

## 실행 명령

```bash
# 로컬 개발 (인프라만)
docker compose up -d

# 프로덕션 (VM에서, docker-compose v1)
docker rm -f metric-kafka metric-postgres metric-grafana metric-generator metric-consumer metric-nginx
docker-compose --profile production up -d
```
