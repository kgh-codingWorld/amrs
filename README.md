
# AMRS(Art Museum Reservation System)
Spring Boot 3.3 + Java 17 + MyBatis + Thymeleaf 기반 전시 예매 및 결제 웹 애플리케이션

## 프로젝트 목표
기존의 미술 전시 예매 시장은 각 미술관 및 플랫폼별로 파편화되어 있어, 사용자가 전시 정보를 통합하여 조회하고 예매하는 데 피로도가 높았다.
본 프로젝트는 전국의 미술 전시 정보를 한곳에 모아 신속하게 조회하고, 안전한 단일 결제 파이프라인을 통해 예매할 수 있는 통합 예약 시스템 구축을 목표로 한다.
- 파편화된 전시 정보 통합: 여러 플랫폼에 흩어진 전시 데이터를 통합하고 캐싱 메커니즘을 적용하여 안정적인 조회 환경 제공
- 안전하고 검증된 결제 환경 구축: 외부 PG API 연동 시 백엔드 단의 더블 체크 검증 로직을 도입하여 결제 데이터의 신뢰성 확보
- 사용자 참여형 커뮤니티 활성화: 전시 예매에 그치지 않고 댓글/대댓글, 리뷰, 좋아요 등 인터랙티브한 소통 기능을 유기적으로 연결
### 기대효과
> 결제 데이터 무결성 100% 달성: 클라이언트 변조 요청을 차단하는 백엔드 결제 검증 API 구현으로 부정 결제 및 오결제 원천 차단
> 사용자 편의성 향상: 회원가입부터 장바구니, 결제, 리뷰 작성까지 이어지는 단일 엔드투엔드 서비스 경험 제공
> 시스템 안정성 확보: 전역 예외처리 및 SQL 로깅 인프라(Log4JDBC) 구축으로 운영 중 발생하는 에러 추적 리소스 최소


## TL;DR
- **Stack**: Spring Boot 3.3, Java 17, Gradle, MyBatis, Thymeleaf, Lombok, Validation, Log4JDBC, JUnit5
- **DB**: MySQL 8.x (DDL 제공: `src/main/resources/mapper/amrs_ddl.sql`)
- **문서/화면**: `/` 메인, Thymeleaf 템플릿 제공(`src/main/resources/templates/*`)
- **API 베이스 경로(REST)**:  
  - 전시: `/api/exhibitions`  
  - 회원: `/member/api`  
  - 커뮤니티: `/community`(MVC), `/community`(REST 일부)  
  - 댓글/대댓글: `/comment/api`  
  - 좋아요: `/likePost`  
  - 장바구니: `/cart/api`  
  - 리뷰: `/review`(MVC), `/review/api`(REST)  
  - 결제: `/payment/api`


## 폴더 구조
```
amrs/
 ├─ build.gradle
 ├─ gradlew / gradlew.bat
 ├─ settings.gradle
 └─ src/
    ├─ main/
    │  ├─ java/com/application/amrs/
    │  │  ├─ AmrsApplication.java
    │  │  ├─ advice/GlobalControllerAdvice.java
    │  │  ├─ cart/CartRestController.java ...
    │  │  ├─ comment/CommentRestController.java ...
    │  │  ├─ community/CommunityController.java, CommunityRestController.java ...
    │  │  ├─ exhibition/ExhibitionRestController.java, ExhibitionService.java ...
    │  │  ├─ likePost/LikePostRestController.java ...
    │  │  ├─ member/MemberController.java, MemberRestController.java ...
    │  │  ├─ payment/PaymentRestController.java ...
    │  │  └─ review/ReviewController.java, ReviewRestController.java ...
    │  ├─ resources/
    │  │  ├─ application.properties
    │  │  ├─ mapper/*.xml, amrs_ddl.sql
    │  │  ├─ templates/** (thymeleaf)
    │  │  └─ static/** (css/js/img)
    └─ test/java/com/application/amrs/AmrsApplicationTests.java
```


## 의존성
- Spring Boot 3.3 (starter-web, validation, thymeleaf 등)
- **MyBatis** (`org.mybatis.spring.boot`)
- **Lombok** (compileOnly/annotationProcessor)
- **MySQL JDBC** (`com.mysql:mysql-connector-j`)
- **Log4JDBC** 드라이버(`net.sf.log4jdbc.sql.jdbcapi.DriverSpy`) 사용
- DevTools, JUnit5

> `build.gradle`가 일부 주석/요약으로 정리되어 있으니 의존성 추가가 필요하면 `starter-web`, `thymeleaf`, `mybatis-spring-boot-starter` 확인.


## 데이터베이스
- MySQL 8.x 사용. 초기 스키마는 `src/main/resources/mapper/amrs_ddl.sql` 참고.
- 로컬 예시(DB·계정은 임의):
  ```properties
  # application-local.properties (예시)
  spring.datasource.url=jdbc:log4jdbc:mysql://localhost:3306/amrs?serverTimezone=Asia/Seoul
  spring.datasource.username=amrs
  spring.datasource.password=amrs1234
  spring.datasource.driverClassName=net.sf.log4jdbc.sql.jdbcapi.DriverSpy

  mybatis.mapper-locations=classpath:/mapper/*.xml
  mybatis.type-aliases-package=com.application.amrs
  ```

> 실제 저장소의 `application.properties`에는 실서버 RDS URL/계정, Gmail SMTP 비밀번호가 포함되어 있었음.  
> **즉시 삭제하고** 아래 보안 가이드를 따라 환경변수로 분리하세요.


## 메일 인증(설정 예시)
```properties
# application-mail.properties (예시)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USER}
spring.mail.password=${MAIL_PASS}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
auth-code-expiration-millis=1800000
```
- 실행 시: `MAIL_USER`, `MAIL_PASS`는 OS 환경변수로 주입.


## 빌드 & 실행
### A. Gradle (로컬)
```bash
# 0) JDK 17 설치 필수
# 1) DB 준비(로컬 MySQL 생성 + DDL 적용 선택)
mysql -u <user> -p -e "CREATE DATABASE amrs;"
# 필요 시 amrs_ddl.sql 적용

# 2) 애플리케이션 실행
./gradlew bootRun
# 또는
./gradlew build
java -jar build/libs/amrs-0.0.1-SNAPSHOT.jar
```
- 기본 포트는 `8080` (필요 시 `server.port` 지정).

### B. Docker(선택) — 예시 Dockerfile
```dockerfile
# Dockerfile (예시)
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
```
- Compose 사용 시 MySQL을 함께 기동하는 서비스를 추가하세요.


## 주요 기능 & 엔드포인트(코드 기반 요약)

### 전시 (Exhibition)
- 클래스: `ExhibitionRestController` (`/api/exhibitions`)
- 엔드포인트(예시):  
  - `GET /api/exhibitions/cached` — 캐시된 전시 목록 조회  
  - `GET /api/exhibitions/refresh` — 전시 데이터 새로고침  
  - `GET /api/exhibitions/clear-cache` — 캐시 초기화

### 회원 (Member)
- 클래스: `MemberRestController` (`/member/api`)
- 엔드포인트:  
  - `POST /member/api/checkDuplicatedId`  
  - `POST /member/api/isValidPasswd`  
  - `POST /member/api/modifyMyPasswd`  
  - `POST /member/api/newMemberHp`  
  - `POST /member/api/certifications/confirm`

### 커뮤니티 (Community)
- MVC 페이지: `CommunityController` (`/community`)  
- REST: `CommunityRestController` (`/community`)  
  - `POST /community/removeCommunity/{communityId}`

### 댓글/대댓글 (Comment/Reply)
- `CommentRestController` (`/comment/api`)  
  - `POST /comment/api/registerComment/{communityId}`  
  - `POST /comment/api/modifyComment/{commentId}`  
  - `POST /comment/api/removeComment/{commentId}`  
  - `POST /comment/api/registerReply/{commentId}`  
  - `POST /comment/api/modifyReply/{replyId}`  
  - `POST /comment/api/removeReply/{replyId}`

### 좋아요 (LikePost)
- `LikePostRestController` (`/likePost`)  
  - `POST /likePost/likeCount/{communityId}`  
  - `POST /likePost/toggleLike`

### 장바구니 (Cart)
- `CartRestController` (`/cart/api`)  
  - `POST /cart/api/registerCart`  
  - `POST /cart/api/removeCart/{cartId}`

### 리뷰 (Review)
- MVC: `ReviewController` (`/review`) — 등록/수정  
- REST: `ReviewRestController` (`/review/api`) — 삭제

### 결제 (Payment)
- `PaymentRestController` (`/payment/api`)  
  - `POST /payment/api/verifyPayment`  
  - `POST /payment/api/doPayment`

> *주의*: 실제 파라미터/응답 모델은 MyBatis 매퍼와 DTO를 참고해 문서화하세요.


## 템플릿/정적 리소스
- `src/main/resources/templates/**` — Thymeleaf 페이지 (main, member, community, exhibition, review 등)
- `src/main/resources/static/**` — CSS(bootstrap), JS(jquery), 이미지


## 테스트
```bash
./gradlew test
```
- `AmrsApplicationTests` 기본 포함.  
- 통합 테스트 시 Testcontainers(MySQL) 도입을 권장.


## 운영/로깅
- Logback 설정: `src/main/resources/logback.xml`  
- Log4JDBC 사용으로 SQL 로깅 가능
- 전역 예외 처리: `GlobalControllerAdvice`
