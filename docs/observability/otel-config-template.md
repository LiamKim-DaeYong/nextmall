# OpenTelemetry 설정 템플릿 (적용 전 단계)

> 목적: 서비스별 적용 전에 공통 키/값을 정리해두는 템플릿

## 공통 환경 변수(예시)
- `OTEL_EXPORTER_OTLP_TRACES_ENDPOINT` = `http://localhost:4318/v1/traces`
- `OTEL_EXPORTER_OTLP_METRICS_ENDPOINT` = `http://localhost:4318/v1/metrics`
- `OTEL_TRACING_SAMPLING_PROBABILITY` = `1.0`

## 적용 위치(후속 단계)
- 각 서비스 `application.yml`에서 `management.opentelemetry.*` 설정으로 반영
- 실제 값은 `.env`/`docker/.env`에서 주입
- OpenTelemetry Collector OTLP(HTTP/GRPC) 엔드포인트 사용

## 참고
- `service.name`은 `spring.application.name`을 사용하도록 설정됨
- 현재 단계는 **인프라 준비**가 우선이며, 서비스 적용은 후속 작업에서 진행
