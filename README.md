# HAPLA (Travel Planner & Review Service)

HAPLA는 여행 일정 계획, 여행지 검색, 항공권 정보 조회 및 여행 후기 공유를 위한 종합 여행 플랫폼입니다.

## 🛠 주요 기능
* **여행 일정 관리**: 사용자별 여행 일정을 생성하고 관리합니다.
* **여행지 및 맛집 검색**: 상세 정보를 제공하며 여행지/숙박/음식점 데이터를 검색할 수 있습니다.
* **항공권 검색**: 여행에 필요한 항공 정보를 조회합니다.
* **커뮤니티**: 여행 후기 작성, 댓글 달기 및 좋아요 기능을 제공합니다.
* **관리자 페이지**: 사용자 관리, 통계 확인, 공지사항 관리 및 신고 내역 처리를 지원합니다.
* **소셜 로그인**: 구글(Google) 및 카카오(Kakao) 간편 로그인을 제공합니다.

## ⚙️ 기술 스택
* **Backend**: Java, Spring Boot
* **Database**: MyBatis, XML Mapper, Oracle
* **Build Tool**: Gradle
* **Frontend**: HTML, CSS, JavaScript (Summernote, Thymeleaf 템플릿 엔진 사용)
* **Interceptors**: 접근 로그 관리 및 보안 설정

## 📁 프로젝트 구조
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
└── src/main/resources/
    ├── mapper/     # MyBatis SQL 매핑 XML 파일
    └── static/     # CSS, JS, 이미지 및 폰트 파일
