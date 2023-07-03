# 예약 등록 및 관리와 리뷰 등록 서비스 시스템

## 1. 개요
- 해당 프로젝트는 매장에 방문하기 전 미리 예약을 수행하고, 방문한 뒤 그 후기를 적을 수 있는 간단한 시스템을 구현하는 것을 목표로 하였습니다.
- 회원가입을 수행한 회원은 매장를 검색한 후, 해당 매장에 대해 예약을 등록하거나, 곧바로 예약을 등록할 수 있습니다.
- 예약을 수행하면 예약된 시간 10분 전에 방문해야 하며 그렇지 않을 경우 자동적으로 예약이 취소됩니다.
- 방문한 후에 회원은 해당 예약에 대해 후기를 남길 수 있습니다.


## 2. 사용 기술
- Spring Boot 2.7.13
- JPA (Spring Data JPA 활용 x, 설정의 편의성만 가져왔습니다.)
  - 왜 Spring Data JPA 활용을 하지 않았는가에 대한 의문에 대해서는 말 그대로 아직 해당 기술에 대한 이해를 하지 못했기 때문입니다.
  - 리펙토링이 가능하다면 Spring Data JPA 와 QueryDSL 을 통해 레포지토리 수정을 수행할 계획입니다.
- Lombok
- Spring Security
- H2 Database(인메모리 테스트 및 로컬 개발 DB)
- MariaDB(운영 DB)
- validation 라이브러리(javax 표준)


## 3. 프로젝트 구조
```buildoutcfg
src
├─ main                           - main
│   ├─ java
│   │   ├─config                  - spring, security 관련 설정 패키지
│   │   ├─controller              - 컨트롤러 패키지
│   │   ├─domain                  - JPA Entity 관련 패키지
│   │   ├─dto                     - DTO 모음 패키지
│   │   ├─exception               - 예외 정의 및 핸들러 패키지
│   │   ├─repository              - 레포지토리 패키지
│   │   ├─security                - 시큐리티 관련 패키지(Filter, JWT Provider)
│   │   └─service                 - 비즈니스 로직 서비스 패키지
│   │
│   └─ resources
│       └─ application.yml        - main 적용 설정 파일
│
│
└─ test                           - test
    ├─ java
    │   ├─repository              - 레포지토리 관련 테스트 패키지
    │   └─service                 - 비즈니스 로직 서비스 관련 테스트 패키지
    └─ resources
        └─ application.yml        - test 적용 설정 파일
```