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

### 이번 세션 완료 (2026-06-16) — Grafana NoData 장애 조치
- [x] resume-ai 로컬 미리보기에서 Grafana 패널이 NoData로 뜨는 문제 발견
- [x] 원인 확인 — `generator`, `consumer` 서비스가 `docker-compose.yml`에서 `profiles:[production]`로 묶여 있어, `--profile production` 없이 `up`하면 안 뜸. 이 PC에서는 kafka/postgres/grafana만 떠 있고 generator/consumer는 컨테이너 자체가 없는 상태 → 9일간(2026-06-07~06-16) 신규 데이터 적재 중단
- [x] `docker compose -f docker-compose.yml -f docker-compose.local.yml --profile production up -d generator consumer` 로 재기동, 실시간 적재 재개 확인 (재기동 직후 max(timestamp) ≈ now())
- ⚠️ **재발 방지 필요**: 로컬 PC 재부팅 시 이 두 컨테이너가 다시 빠질 수 있음 — 정기적으로 `docker ps`에 generator/consumer가 보이는지, postgres `max(timestamp)`가 최신인지 확인할 것

### 이번 세션 완료 (2026-06-17) — Grafana WebSocket 연결 실패 수정
- [x] 브라우저 콘솔에 `WebSocket connection to 'wss://skydev.ddns.net/metric/api/live/ws' failed` 에러 확인
- [x] 원인 — `nginx/nginx.conf`의 `/metric/` 프록시 설정에 WebSocket 업그레이드 헤더(`Upgrade`, `Connection`)가 없어서 Grafana 실시간 라이브 연결이 일반 HTTP로 처리되어 실패
- [x] `map $http_upgrade $connection_upgrade` 추가 + `/metric/` location에 `proxy_http_version 1.1`, `proxy_set_header Upgrade`, `proxy_set_header Connection $connection_upgrade` 추가
- ⚠️ **VM 재배포 필요**: 이 PC에는 로컬 nginx 컨테이너가 없어(원격 VM에서만 동작) 코드만 수정됨. 실제 적용을 위해 VM(admin@trade, 192.168.0.26)에서 `git pull` 후 nginx 컨테이너 재기동(`docker compose up -d --force-recreate nginx` 등) 필요

---

## 다음 작업

1. **Grafana 대시보드 확인**
   - 데이터가 실시간으로 쌓이는지 확인 (generator/consumer 컨테이너가 떠있는지 `docker ps`로 같이 확인)
   - 패널 정상 표시 여부 확인

2. **No-IP 30일마다 갱신 확인**
   - 이메일로 활성 확인 안 하면 호스트 삭제됨

3. **certbot 자동 갱신 확인**
   - `sudo certbot renew --dry-run`

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
