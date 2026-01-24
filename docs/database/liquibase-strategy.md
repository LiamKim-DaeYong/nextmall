# Liquibase 마이그레이션 운영 전략
## Production Database Migration Guide

## 1. 개요
NextMall 프로젝트는 데이터베이스 스키마 관리를 Liquibase를 통해 통합적으로 관리한다.
본 문서는 개발·스테이징·운영 환경에서의 Liquibase 적용 방식과 마이그레이션 절차를 정의한다.

---

## 2. Baseline 정책
### 2.1 Baseline 목적
- 초기 스키마 정의(0001-create-users-table.yaml)
- 개발 환경에서는 자동 적용
- 운영 환경에서는 기존 스키마가 있을 경우 baseline 절대 적용 금지

### 2.2 규칙
- baseline changeSet은 한 번 릴리스되면 수정 금지
- 운영 DB 스키마를 재생성하거나 drop하는 changeSet 작성 금지
- 변경은 항상 새로운 changeSet으로 추가

---

## 3. ChangeSet 작성 규칙
### 3.1 파일명 규칙
```yaml
0001-create-users-table.yaml
0002-create-products-table.yaml
0003-create-orders-table.yaml
...
```

### 3.2 작성 원칙
- 이미 적용된 changeSet은 수정하지 않는다
- 모든 스키마 변경은 새로운 changeSet으로 작성
- 실행 순서는 master 파일의 include 순서
- 위험 작업(drop/rename 등)은 rollback 작성 권장

---

## 4. 환경별 적용 전략
### 4.1 Local / Dev
- 애플리케이션 실행 시 Liquibase 자동 적용
- DB 초기화 자유롭고 테스트 목적에 적합
- 개발자 간 스키마 불일치 방지

### 4.2 Staging
- 배포 시 Liquibase 자동 수행
- 스키마 변경 검증 환경
- 배포 전 DB 백업 필수

### 4.3 Production
- CI/CD 파이프라인에서 Liquibase 자동 실행
- 모든 changeSet은 databasechangelog 기준으로 한 번만 실행
- 위험 작업 배포 전 별도 검토 필요
- 운영 배포 전 백업 필수

---

## 5. 스키마 변경 절차 (개발 → 운영)

1. 새로운 changeSet 생성 (예: 0002-create-products-table.yaml)
2. db.changelog-master.yaml에 include 추가
3. 로컬 환경에서 Liquibase 실행 테스트
4. 필요한 경우 jOOQ Codegen 재생성
5. PR 생성 및 코드 리뷰
6. Staging 배포 → Liquibase 적용 확인
7. Production 배포 → Liquibase 적용

---

## 6. 데이터 손실 방지 정책
### 6.1 위험 작업
- DROP TABLE, DROP COLUMN, RENAME COLUMN 직접 사용 금지
- 여러 단계로 나눠 적용
  - 새로운 컬럼 추가
  - 데이터 복사
  - 운영 환경에서 충분한 검증 후 삭제(ChangeSet 분리)

### 6.2 롤백
- 가능한 changeSet에 rollback 포함
- 운영 환경에서는 rollback 실패 위험이 있으므로 테스트 환경에서 충분히 검증

### 6.3 백업
- Staging/Production 배포 전에는 반드시 DB Snapshot 백업
- Liquibase lock 문제 발생 시 운영 가이드 절차에 따라 해제

---

## 7. Liquibase vs Hibernate 역할 분리
### 7.1 Liquibase
- 데이터베이스 스키마 정의와 변경의 단일 관리 주체
  
  (모든 스키마 변경은 Liquibase changeSet을 통해서만 수행됨)

### 7.2 JPA/Hibernate
- 런타임 엔티티 매핑 담당
- 스키마 생성/수정 기능은 사용하지 않음
- `ddl-auto`는 항상 `none` 또는 `validate`

### 7.3 Audit 컬럼
- 기본값(CURRENT_TIMESTAMP)으로 초기값 설정
- 수정 시간은 JPA Auditing으로 관리

---

## 8. 디렉터리 구조
```yaml
src/main/resources/db/changelog/
 ├── db.changelog-master.yaml
 └── changes/
      ├── 0001-create-users-table.yaml
      ├── 0002-*.yaml
      ├── 0003-*.yaml
```

---

## 9. 운영 배포 체크리스트
- [ ] Liquibase lock 여부 확인
- [ ] master 파일 include 누락 여부 확인
- [ ] 위험 changeSet 포함 여부 점검
- [ ] Staging에서 동일 changeSet 검증 완료
- [ ] 운영 DB 백업 완료
- [ ] Liquibase 실행 로그 정상 여부 확인

---

## 10. appendix
### 10.1 changeSet 예시
```yaml
databaseChangeLog:
  - changeSet:
      id: 0002-create-product-table
      author: liam
      changes:
        - createTable:
            tableName: products
            columns:
              - column:
                  name: id
                  type: BIGSERIAL
                  constraints:
                    primaryKey: true
              - column:
                  name: name
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
```
