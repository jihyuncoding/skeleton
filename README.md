# 🗺️ 관광지 검색 시스템 (TravelApp)

Java 기반 콘솔 프로그램. 22년 관광공사에서 선정한 112가지의 관광지에 대한 정보를 검색하고 즐겨찾기 및 사용자 등록 기능을 지원함.

---

## 📌 주요 기능

- 전체 관광지 목록 및 지역별 보기
- 키워드, 지역, 제목 + 지역, 설명 기반 검색
- 검색 결과의 상세정보 확인 및 웹 브라우저 연동
- 랜덤 관광지 추천
- 즐겨찾기 추가 / 조회 / 삭제 기능
- 사용자가 직접 관광지를 등록하고 관리

---

## 🧱 프로젝트 구조

```
src/
├── dao/
│   ├── TravelDao.java          // 관광지 관련 DB 처리
│   └── UserTravelDao.java      // 사용자 관광지 관련 DB 처리
├── model/
│   ├── TravelVO.java           // 관광지 데이터 VO
│   └── UserTravelVO.java       // 사용자 관광지 데이터 VO
├── service/
│   ├── TravelService.java      // 관광지 서비스 로직
│   └── UserTravelService.java  // 사용자 관광지 서비스 로직
├── util/
│   └── ViewUtils.java          // 공통 UI 출력 기능
└── TravelApp.java              // 메인 애플리케이션 실행 파일
```

---

## 🛠️ 사용 기술 및 외부 라이브러리

- **Java 17**
- **MySQL 8+**
- **Gradle**
- 외부 라이브러리:
  - `com.mysql:mysql-connector-j:9.2.0` : MySQL DB 연동
  - `org.slf4j:slf4j-api:2.0.17`, `ch.qos.logback:logback-classic:1.5.18` : 로깅 처리
  - `com.opencsv:opencsv:5.5` : CSV 파일 파싱

---
