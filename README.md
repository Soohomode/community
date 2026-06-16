# 🌐 커뮤니티 게시판

Spring Boot + React로 구현한 풀스택 커뮤니티 게시판 프로젝트입니다.

## 💡 프로젝트 소개
Spring Boot와 React를 활용한 풀스택 개발 역량을 키우기 위해
커뮤니티 게시판을 직접 설계하고 구현했습니다.
단순 기능 구현을 넘어 레이어드 아키텍처, 디자인 패턴, 배포까지
실무와 유사한 개발 흐름으로 진행하는 것을 목표로 했습니다.

## 🔗 배포 URL

- **프론트엔드**: https://community-eight.vercel.app
- **백엔드**: https://community-production-e28e.up.railway.app

---

## 🛠 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-green)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-green)
![JPA](https://img.shields.io/badge/Spring_Data_JPA-3.x-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![JWT](https://img.shields.io/badge/JWT-0.11.5-purple)

### Frontend
![React](https://img.shields.io/badge/React-18-blue)
![Vite](https://img.shields.io/badge/Vite-6.x-purple)
![Axios](https://img.shields.io/badge/Axios-1.x-blue)

### Infrastructure
![Docker](https://img.shields.io/badge/Docker-blue)
![Railway](https://img.shields.io/badge/Railway-Backend-black)
![Vercel](https://img.shields.io/badge/Vercel-Frontend-black)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI-black)

## 🤔 기술 선택 이유

**JWT vs 세션**
→ REST API의 Stateless 특성을 유지하고
서버 확장성을 고려해 JWT 방식 선택

**FetchType.LAZY**
→ 게시글 목록 조회 시 불필요한 Member 정보 로딩을 방지하고
N+1 문제를 예방하기 위해 LAZY 로딩 선택

---

## 📊 ERD

![ERD](./backend/docs/erd.png)
## 📁 프로젝트 구조

```
community/
├── backend/         # Spring Boot 백엔드
│   └── src/main/java/com/study/community/
│       ├── controller/   # API 요청/응답 처리
│       ├── service/      # 비즈니스 로직
│       ├── repository/   # DB 접근
│       ├── domain/       # JPA Entity
│       ├── dto/          # 데이터 전달 객체
│       ├── jwt/          # JWT 인증 필터
│       ├── config/       # Security, CORS 설정
│       └── exception/    # 커스텀 예외 처리
└── frontend/        # React 프론트엔드
    └── src/
        ├── pages/        # 페이지 컴포넌트
        ├── components/   # 공통 컴포넌트
        └── api/          # Axios 설정
```

---

## ✨ 주요 기능

### 회원
- 회원가입 / 로그인
- JWT 토큰 기반 인증

### 게시글
- 게시글 작성 / 수정 / 삭제 (본인만 가능)
- 게시글 목록 조회 (페이징)
- 게시글 상세 조회 (조회수 증가)
- 제목 키워드 검색
- 최신순 / 오래된순 / 조회수순 정렬

### 댓글
- 댓글 작성 / 삭제 (본인만 가능)
- 게시글별 댓글 목록 조회

## 📸 화면 구성

| 게시글 목록 | 게시글 상세 | 로그인 |
|------------|------------|--------|
| ![목록](https://github.com/user-attachments/assets/fc03f6c4-508a-4537-b6da-e76bf946edfd) | ![상세](https://github.com/user-attachments/assets/f8ff75bb-397f-46a4-be50-e0e16377436c) | ![로그인](https://github.com/user-attachments/assets/dce8d55a-c19f-4188-859c-8d7f52572341) |

---

## 🏗 아키텍처

### 레이어드 아키텍처

```
Client
  ↓
Controller   # 요청/응답 처리
  ↓
Service      # 비즈니스 로직
  ↓
Repository   # DB 접근 (Spring Data JPA)
  ↓
MySQL
```

### 적용 디자인 패턴
| 패턴 | 적용 위치 | 설명 |
|------|-----------|------|
| Builder | Member, Post Entity | 객체 생성 가독성 향상 |
| Strategy | 게시글 정렬 | 정렬 방식 유연한 교체 |
| Facade | 게시글 상세 조회 | 게시글 + 댓글 단일 API |
| Repository | 데이터 접근 계층 | DB 로직 분리 |

---

## 🔐 인증 흐름

```
로그인 요청
    ↓
이메일/비밀번호 검증
    ↓
JWT 토큰 발급
    ↓
이후 요청 시 Header에 토큰 포함
Authorization: Bearer {token}
    ↓
JwtAuthenticationFilter에서 토큰 검증
    ↓
인증 완료
```

---

## 🚀 로컬 실행 방법

### Backend
```bash
cd backend

# 환경변수 설정 (IntelliJ 실행 구성)
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key_at_least_32_bytes

# 실행
./gradlew bootRun
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

---

## 📝 API 명세

### 인증
| Method | URL | 설명 |
|--------|-----|------|
| POST | /api/auth/join | 회원가입 |
| POST | /api/auth/login | 로그인 |

### 게시글
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/posts | 게시글 목록 (페이징/정렬) | ❌ |
| GET | /api/posts/{id} | 게시글 단건 조회 | ❌ |
| GET | /api/posts/{id}/detail | 게시글 + 댓글 조회 | ❌ |
| GET | /api/posts/search | 키워드 검색 | ❌ |
| POST | /api/posts | 게시글 작성 | ✅ |
| PUT | /api/posts/{id} | 게시글 수정 | ✅ |
| DELETE | /api/posts/{id} | 게시글 삭제 | ✅ |

### 댓글
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/posts/{postId}/comments | 댓글 목록 | ❌ |
| POST | /api/posts/{postId}/comments | 댓글 작성 | ✅ |
| DELETE | /api/posts/{postId}/comments/{id} | 댓글 삭제 | ✅ |

---

```
테스트 코드
├── JUnit + Mockito 기반 단위 테스트 작성
├── @WebMvcTest로 Controller 계층 독립 테스트
├── @DataJpaTest로 JPA 쿼리 검증
└── H2 인메모리 DB 활용 테스트 환경 구성
```

## 🔧 트러블슈팅

### 1. CORS 문제
**문제**: 프론트엔드(localhost:5173)에서 백엔드(localhost:8080)로 요청 시 CORS 오류 발생

**원인**: Spring Security가 preflight(OPTIONS) 요청을 인증 없이 통과시키지 않아서 발생

**해결**: `SecurityConfig` 에 `CorsConfigurationSource` 빈을 등록하고 Spring Security 레벨에서 CORS 처리

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:5173");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    ...
}
```

---

### 2. JWT Stateless 방식 선택 이유
**고민**: 세션 방식 vs JWT 방식

**선택**: JWT (Stateless)

**이유**:
- 서버가 세션을 저장하지 않아 서버 확장(Scale-out)에 유리
- REST API 서버에 적합한 무상태(Stateless) 아키텍처 유지
- 이후 마이크로서비스 전환 시에도 인증 서버 분리 용이

---

### 3. FetchType.LAZY 선택 이유
**고민**: FetchType.EAGER vs FetchType.LAZY

**선택**: LAZY

**이유**:
- EAGER는 연관 엔티티를 항상 즉시 로딩해 불필요한 쿼리 발생
- 게시글 목록 조회 시 각 게시글마다 Member를 즉시 로딩하면 N+1 문제 발생
- LAZY로 설정해 실제 필요한 시점에만 Member 정보 로딩

---

### 4. 조회수 중복 증가 문제
**문제**: 댓글 작성/삭제 시 게시글을 다시 불러오면서 조회수가 계속 증가

**원인**: 댓글 변경 후 `fetchDetail()` 을 다시 호출할 때마다 조회수 증가 로직이 실행됨

**해결**:
- 조회수 증가 API와 단순 조회 API 분리
- 댓글 변경 시 댓글 목록만 별도로 갱신 (`/api/posts/{id}/comments`)
- 페이지 최초 진입 시에만 조회수 증가

---

### 5. Docker MySQL 연결 타이밍 문제
**문제**: `docker compose up` 시 MySQL 준비 전에 앱이 먼저 실행되어 DB 연결 실패

**원인**: `depends_on` 은 컨테이너 시작만 보장하고 MySQL 실제 준비 완료는 보장하지 않음

**해결**: `healthcheck` 설정으로 MySQL이 완전히 준비된 후 앱 실행

```yaml
healthcheck:
  test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
  interval: 10s
  retries: 5
depends_on:
  mysql:
    condition: service_healthy
```