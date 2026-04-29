# 🍱 FoodGrid - Backend

수업 실습을 기반으로 구현한 MSA 구조의 음식 주문 플랫폼 백엔드입니다.

<img width="1909" height="866" alt="Image" src="https://github.com/user-attachments/assets/69a1b5bb-0903-444b-beb3-229188d5f747" />

## Tech Stack

| 구분 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.5 |
| ORM | Spring Data JPA |
| Security | Spring Security, JWT |
| Cache | Redis, Spring Cache |
| 서비스 간 통신 | OpenFeign |
| 이벤트 | Kafka, Outbox 패턴 |
| Database | MySQL |
| Build | Gradle 멀티모듈 |

---

## 모듈 구조

```
green-eats-container/
├── common          # 공통 엔티티, JWT, 예외 처리 (라이브러리 역할)
├── gateway-service # API 라우팅, JWT 검증, 커스텀 헤더 주입
├── auth-service    # 회원가입, 로그인, JWT 발급
├── store-service   # 메뉴 등록, 조회, 재고 관리
└── order-service   # 주문 생성, 조회 (Outbox 패턴 적용)
```

---

## API 엔드포인트 (Gateway: `localhost:8000`)

### Auth
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/user/signup` | 회원가입 |
| POST | `/api/user/signin` | 로그인 |
| POST | `/api/user/signout` | 로그아웃 |

### Store
| Method | URL | 설명 |
|---|---|---|
| GET | `/api/store/menu` | 전체 메뉴 조회 |
| POST | `/api/store/menu` | 메뉴 등록 |
| GET | `/api/store/code?code_type=menuCategory` | 카테고리 코드 조회 |
| PUT | `/api/store/menu/{menuId}/stock` | 재고 차감 |

### Order
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/order` | 주문 생성 |
| GET | `/api/order?last_id={lastId}` | 주문 목록 조회 (커서 페이지네이션) |
| GET | `/api/order/{orderId}` | 주문 상세 조회 |

---

## 주요 구현 사항

**Gateway 인증 처리**
JWT 검증을 Gateway에서 일괄 처리하고 유저 정보를 커스텀 헤더로 각 서비스에 전달합니다.

**OpenFeign 서비스 간 통신**
주문 상세 조회 시 `order-service`가 `store-service`에 menuId 목록을 한 번에 전송해 메뉴 정보를 일괄 조회합니다. `Map<Long, MenuGetClientRes>`로 반환받아 O(1)로 접근합니다.

**Redis 캐시**
메뉴 리스트는 Redis에 캐싱해 반복 조회 시 DB를 거치지 않습니다. Refresh Token도 Redis에 저장해 로그아웃 시 즉시 무효화합니다.

**Kafka 이벤트 기반 동기화**
회원가입 시 `auth-service`가 `user-topic`으로 이벤트를 발행하고 `order-service`의 Consumer가 수신해 `UserCache`에 저장합니다.

**Outbox 패턴**
주문 생성 시 Order 저장과 Outbox 이벤트 저장을 같은 트랜잭션에서 처리합니다. Kafka 장애 시에도 스케줄러가 5초마다 PENDING 이벤트를 재발행합니다.

**커서 기반 페이지네이션**
주문 목록 조회 시 `lastId` 커서 방식으로 구현했습니다.

---

## 실행 방법

### 사전 요구사항
- Java 21
- MySQL
- Redis (`localhost:6379`)
- Kafka (`localhost:9092`)

### 실행 순서
```bash
# 1. common 빌드
./gradlew :common:build

# 2. 각 서비스 실행
./gradlew :gateway-service:bootRun
./gradlew :auth-service:bootRun
./gradlew :store-service:bootRun
./gradlew :order-service:bootRun
```

### 포트
| 서비스 | 포트 |
|---|---|
| gateway-service | 8000 |
| auth-service | 8081 |
| store-service | 8082 |
| order-service | 8083 |