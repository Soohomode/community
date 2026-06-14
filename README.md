# 🌐 커뮤니티 게시판

Spring Boot + React로 구현한 풀스택 커뮤니티 게시판 프로젝트입니다.

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

---

## 📁 프로젝트 구조
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
