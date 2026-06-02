# metric-stream — Claude 컨텍스트

## 세션 시작 시 필수
> 이 파일을 읽은 뒤 **`PROGRESS.md`를 반드시 읽고** 현재 상태 파악 후 작업 시작.

---

## 사용자 프로필

- **이름**: 김준영 (JuneYoung Kim)
- **GitHub**: https://github.com/sky14786
- **경력**: 5년+ 백엔드/인프라 엔지니어 (SI/솔루션 환경)
- **주요 스택**: Spring Boot, Java, MySQL/MariaDB/PostgreSQL, Redis, Kafka, RabbitMQ, Flink, Docker, Nginx, Jenkins, Shell Script
- **성향**: 신기술보다 운영 안정성 우선. 혼자 전체 시스템 운영·문제해결에 강점.
- **참고**: 백엔드/인프라 경험은 깊지만 GitHub 개인 프로젝트가 적음. 이 프로젝트가 포트폴리오 역할을 함.

---

## 프로젝트 배경

실무 경험(MAI-WACS — 350대 서버 30초 단위 실시간 로그 수집 파이프라인)을 기반으로 만드는 토이 프로젝트.

실무에서는 Telegraf → Kafka → Flink → TimescaleDB 구조였으나,  
이 프로젝트에서는 **데이터 생성기(임의 생성)**로 Telegraf를 대체하고  
Flink 대신 **Spring Boot Consumer**로 단순화하여 핵심 파이프라인 구조를 재현.

- **목적**: 포트폴리오용 토이 프로젝트. 실제 운영 경험 기반의 파이프라인 설계 능력 증명.
- **GitHub**: https://github.com/sky14786/metric-stream

---

## 기술 스택

| 역할 | 기술 | 선택 이유 |
|------|------|----------|
| 언어 | Java 17 + Spring Boot 3 | 실무 주력 스택 |
| 메시징 | Apache Kafka | MAI-WACS 실무 경험 |
| DB | PostgreSQL (TimescaleDB 확장 고려) | 시계열 데이터 최적화 |
| 인프라 | Docker Compose | 로컬 전체 구성 단일 파일 |
| CI | GitHub Actions | 자동 빌드/테스트 |

---

## 아키텍처 개요

```
[metric-generator]
  └─ 서버 메트릭 임의 생성 (30초 간격)
  └─ Kafka Topic: server-metrics 로 produce

[metric-consumer]
  └─ Kafka Consumer
  └─ 수신 데이터 PostgreSQL 저장

[metric-api]
  └─ REST API — 저장된 메트릭 조회
  └─ Nginx 리버스 프록시

[docker-compose.yml]
  └─ Kafka, Zookeeper, PostgreSQL, 전체 모듈 통합 실행
```

**데이터 스키마 (server_metrics 테이블)**
```
server_id    VARCHAR
cpu_usage    DOUBLE
memory_usage DOUBLE
disk_usage   DOUBLE
timestamp    TIMESTAMPTZ
```

---

## 디렉토리 구조

```
metric-stream/
├── CLAUDE.md              ← 이 파일
├── PROGRESS.md            ← 세부 작업 현황
├── README.md              ← 프로젝트 소개 (공개용)
├── docker-compose.yml     ← 전체 인프라 구성
├── metric-generator/      ← 데이터 생성 모듈
├── metric-consumer/       ← Kafka Consumer 모듈
└── metric-api/            ← REST API 모듈
```

---

## 세션 규칙 (필수)

기능 하나 완성될 때마다 즉시:

1. **git commit & push** — 세션 말미에 몰아서 하지 말 것
2. **PROGRESS.md 업데이트** — 완료 항목 체크, 다음 할 일 갱신
3. **README.md 업데이트** — 기능 추가·수정 시 반영
4. **CLAUDE.md 업데이트** — 아키텍처나 스택 결정 변경 시

---

## 코딩 컨벤션

- 패키지: `com.juneyoung.metricstream`
- 클래스명: PascalCase, 역할 명시 (`MetricProducer`, `MetricConsumerService`)
- 메서드명: camelCase, 동사 시작 (`generateMetric`, `consumeMetric`)
- 주석: 불필요한 주석 없음. WHY가 명확할 때만 작성.
- 테스트: 핵심 로직 위주 단위 테스트
- 커밋 메시지: `feat:` `fix:` `docs:` `chore:` prefix 사용

---

## 참고 링크

- 경력기술서 사이트: https://juneyoung.pages.dev
- 경력기술서 레포: https://github.com/sky14786/resume-ai
