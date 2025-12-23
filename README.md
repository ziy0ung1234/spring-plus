## 서비스 설정 화면
### EC2
<img width="1000" height="500" alt="스크린샷 2025-12-22 오전 10 23 47" src="https://github.com/user-attachments/assets/0bea0a16-d7a5-40bb-a6f0-abba77218077" />

### RDS
<img width="1000" height="500" alt="스크린샷 2025-12-22 오전 10 24 19" src="https://github.com/user-attachments/assets/8c826ca0-5faf-4ec0-b0bd-ac4790b1f2bd" />

### S3
<img width="703" height="500" alt="스크린샷 2025-12-23 오후 10 31 05" src="https://github.com/user-attachments/assets/037a72d9-1fd9-4b1f-9cf8-0d247bb19314" />

### IAM
<img width="1000" height="500" alt="스크린샷 2025-12-23 오후 10 24 50" src="https://github.com/user-attachments/assets/07437bd7-c647-4709-91c6-b85a60c12491" />

## 12-2.health-check
<img width="1000" height="500" alt="스크린샷 2025-12-22 오후 2 50 41" src="https://github.com/user-attachments/assets/2df43433-75df-4776-bf26-e32961075d64" />

## 12-3. 이미지 업로드
<img width="1000" height="200" alt="스크린샷 2025-12-23 오후 9 57 59" src="https://github.com/user-attachments/assets/673b8bf0-a71e-4d92-801a-b69e8ed5192a" />
<img width="765" height="251" alt="스크린샷 2025-12-23 오후 9 57 12" src="https://github.com/user-attachments/assets/9cb2e57f-9237-4bc2-b67e-ba4f588f452e" />


## 조회 성능 개선 실험 결과 비교

### 실험 전제

- **데이터 규모**: 5,000,000건
- **조회 조건**: `nickname` 정확히 일치
- **결과 특성**: 중복 없음 → 단건 조회
- **테스트 환경**
  - 캐시 효과를 배제하기 위해 **매 측정마다 MySQL 서버 재시작**
  - `EXPLAIN ANALYZE` 기준으로 DB 실행 시간 측정
  - 캐시 적용 단계는 API 응답 시간 기준으로 측정

---

### 단계별 조회 성능 비교

| 단계 | 적용 방식 | 주요 개선 포인트 | 실행 계획 요약 | 조회 시간 |
|------|-----------|------------------|----------------|-----------|
| **Baseline** | 인덱스 없음 | 전체 테이블 스캔 | Table Scan (5M rows) | **약 3600ms** |
| 1 | DTO Projection | 엔티티 로딩 제거 | Table Scan 유지 | 약 3046ms |
| 2 | BTREE Index | 테이블 스캔 제거 | Index Lookup + PK 접근 | 약 1ms |
| 3 | UNIQUE Index | 논리적 단건 보장 | Index Lookup + PK 접근 | 약 0.3ms |
| 4 | Covering Index | 테이블 접근 제거 | Index Only Scan | **약 0.03ms** |
| 5 | Spring Cache (HIT) | DB 접근 자체 제거 | DB 실행 X | **API 기준 10ms 이하** |

---

### 단계별 실행 계획 변화 요약

#### Baseline (인덱스 없음)

- 전체 테이블 스캔 발생
- 랜덤 I/O 다량 발생

#### BTREE / UNIQUE 인덱스

- 테이블 스캔 제거
- 인덱스 탐색 후 **PK 기반 테이블 접근 발생**
- 인덱스 종류(BTREE vs UNIQUE) 간 체감 성능 차이는 크지 않음

#### Covering Index

- WHERE 조건 + SELECT 컬럼 모두 인덱스 포함
- **PK 접근 및 테이블 접근 제거**
- 랜덤 I/O 제거 → 가장 큰 성능 개선

#### Spring Cache

- 반복 조회 시 DB 접근 자체 제거
- SQL 실행 계획 미발생
- 애플리케이션 레벨에서 즉시 응답

---

### 성능 개선 요약

- **Baseline → Covering Index**
  - 약 **120,000배 이상 성능 개선**
- **Covering Index → Cache HIT**
  - DB 부하 0, 조회 비용 최소화

---

### 결론

- 인덱스 튜닝의 핵심은 **어떤 인덱스를 쓰느냐가 아니라, 테이블 접근을 제거하는 것**
- 커버링 인덱스는 랜덤 I/O를 제거하는 가장 효과적인 방법
- 반복 조회 환경에서는 **캐시 전략이 최종적인 성능 병목 해결책**

---
