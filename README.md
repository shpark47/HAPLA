# HAPLA (Travel Planner & Review Service)

HAPLA는 여행 일정 계획, 여행지 검색, 항공권 정보 조회 및 여행 후기 공유를 위한 종합 여행 플랫폼입니다.

## 팀 소개

총 5명의 팀원으로 구성되어 있으며, 효율적인 개발을 위해 역할을 분담하여 프로젝트를 진행합니다.

| 이름  | GitHub                           |
| --- | -------------------------------- |
| 박성훈 | https://github.com/shpark47      |
| 조민주 | https://github.com/SUCRESUCCES   |
| 황성현 | https://github.com/tjdgusghkd    |
| 이창  | https://github.com/wajangchang10 |
| 강현준 | https://github.com/khjun98       |

## 주요 기능

### 여행 일정 관리

* 사용자별 여행 일정 생성 및 수정
* 여행 기간별 일정 관리
* 여행 계획 공유 기능

### 여행지 및 맛집 검색

* 여행지, 숙박시설, 음식점 검색
* 상세 정보 및 위치 정보 제공
* 카테고리별 검색 지원

### 항공권 검색

* 출발지 및 도착지 기반 항공편 조회
* 항공편 정보 및 운항 일정 제공

### 커뮤니티

* 여행 후기 작성 및 수정
* 댓글 및 좋아요 기능
* 사용자 간 정보 공유

### 관리자 페이지

* 회원 관리
* 신고 내역 처리
* 공지사항 관리
* 서비스 통계 확인

### 소셜 로그인

* Google 로그인
* Kakao 로그인

### 이미지 관리

* Cloudflare R2 Bucket을 활용한 이미지 저장
* 여행 후기 및 게시글 이미지 업로드
* 서버와 분리된 객체 스토리지 환경 구축

## 기술 스택

### Backend

* Java
* Spring Boot
* Spring MVC

### Database

* Oracle Database
* MyBatis
* XML Mapper

### Frontend

* HTML5
* CSS3
* JavaScript
* Thymeleaf
* Summernote

### Storage

* Cloudflare R2 Bucket

### Build Tool

* Gradle

### Authentication

* OAuth 2.0
* Google Login
* Kakao Login

### Infrastructure & Security

* Interceptor 기반 접근 로그 관리
* 사용자 권한 및 인증 처리

## 프로젝트 구조

```text
HAPLA-main/
├── src/main/java/com/hapla/
│   ├── admin/      # 관리자 기능 (통계, 신고, 회원관리)
│   ├── ajax/       # 비동기 요청 처리
│   ├── comm/       # 커뮤니티 기능 (후기, 댓글, 좋아요)
│   ├── common/     # 공통 기능 (소셜 로그인, 인터셉터, 페이징)
│   ├── flights/    # 항공권 정보 조회
│   ├── place/      # 여행지/장소 상세 정보
│   ├── review/     # 후기 상세 페이지 및 로직
│   ├── schedule/   # 여행 일정 관리
│   └── users/      # 사용자 계정 관리
│
└── src/main/resources/
    ├── mapper/     # MyBatis SQL 매핑 XML 파일
    ├── templates/  # Thymeleaf 템플릿
    └── static/     # CSS, JS, 이미지 및 폰트 파일
```
