CREATE DATABASE AMRS;
USE AMRS;

CREATE TABLE MUSEUM (
    MUSEUM_ID INT NOT NULL AUTO_INCREMENT,                 -- 미술관 아이디 (자동 증가, 기본키)
    MUSEUM_NM VARCHAR(20) NOT NULL,                        -- 미술관 이름
    MUSEUM_ADDRESS VARCHAR(500) NOT NULL,                  -- 미술관 주소
    MUSEUM_TEL_NUMBER VARCHAR(20) NOT NULL,                -- 미술관 전화번호
    MUSEUM_WEBSITE VARCHAR(50) NOT NULL,                   -- 미술관 웹사이트
    MUSEUM_DESCRIPTION VARCHAR(100) NOT NULL,              -- 미술관 설명
    MUSEUM_OPENING_HOURS VARCHAR(50) NOT NULL,             -- 미술관 개장 시간
    CREATE_DT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 미술관 정보 등록일자 (현재 시간)
    UPDATE_DT TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 미술관 정보 수정일자 (수정 시 자동 업데이트)
    PRIMARY KEY (MUSEUM_ID)                                -- MUSEUM_ID를 기본키로 설정
);


CREATE TABLE EXHIBITION (
    EXHIBITION_ID INT NOT NULL AUTO_INCREMENT,      						-- 전시회 ID (자동 증가)
    MUSEUM_ID INT NOT NULL,                         						-- 미술관 ID (외래키)
    EXHIBITION_TITLE VARCHAR(500) NOT NULL,         						-- 전시회 제목
    EXHIBITION_WRITER VARCHAR(200) NOT NULL,        						-- 전시회 작가
    EXHIBITION_STOCK INT NOT NULL,                  						-- 전시 티켓 재고
    EXHIBITION_DESCRIPTION VARCHAR(1000) NOT NULL,  						-- 전시회 설명
    EXHIBITION_PROFILE_ORIGINAL_NAME VARCHAR(50) NOT NULL,  				-- 전시 프로필 원본 이름
    EXHIBITION_PROFILE_UUID VARCHAR(50) NOT NULL,   						-- 전시 프로필 UUID
    EXHIBITION_TYPE ENUM('PAST', 'PRESENT', 'FUTURE', 'ONLINE') NOT NULL,  	-- 전시회 유형 (예시)
    EXHIBITION_IS_PRESIDENT CHAR(1) DEFAULT 'Y',    						-- 대표전시 여부 (Y/N)
    START_DT DATE NOT NULL,                         						-- 전시 시작일
    END_DT DATE NOT NULL,                           						-- 전시 종료일,
    PRIMARY KEY (EXHIBITION_ID),                    						-- EXHIBITION_ID를 기본키로 설정
    
    -- 외래키 설정 및 외래키 이름 지정
    CONSTRAINT FK_MUSEUM_ID FOREIGN KEY (MUSEUM_ID) 
    REFERENCES MUSEUM(MUSEUM_ID) ON DELETE CASCADE  						-- 외래키 이름 설정 (FK_MUSEUM_ID)
);

CREATE TABLE MEMBER (
    MEMBER_ID INT NOT NULL AUTO_INCREMENT,                        -- 회원 아이디 (자동 증가)
    MEMBER_EMAIL VARCHAR(50) NOT NULL,                            -- 회원 이메일
    PASSWORD VARCHAR(20) NOT NULL,                                -- 비밀번호
    MEMBER_NM VARCHAR(20) NOT NULL,                               -- 회원 이름
    BIRTH_AT VARCHAR(20) NOT NULL,                                -- 생년월일
    MEMBER_PHONE_NUMBER VARCHAR(20) NOT NULL,                     -- 핸드폰번호
    MEMBER_PROFILE_ORIGINAL_NAME VARCHAR(50),                     -- 회원 프로필 원본명 (NULL 허용)
    MEMBER_PROFILE_UUID VARCHAR(50),                              -- 회원 프로필 UUID (NULL 허용)
    SMSSTS_YN VARCHAR(20),                                        -- 문자 수신동의 여부 (NULL 허용)
    EMAILSTS_YN VARCHAR(20),                                      -- 이메일 수신동의 여부 (NULL 허용)
    ZIPCODE VARCHAR(20) NOT NULL,                                 -- 우편번호
    ROAD_ADDRESS VARCHAR(500) NOT NULL,                           -- 도로명 주소
    JIBUN_ADDRESS VARCHAR(500) NOT NULL,                          -- 지번 주소
    NAMUJI_ADDRESS VARCHAR(500) NOT NULL,                         -- 나머지 주소
    MEMBER_ROLE ENUM('USER', 'ADMIN') NOT NULL,                   -- 권한 (사용자 또는 관리자)
    POINT INT DEFAULT 0,                                          -- 회원 포인트 (기본값 0)
    CREATE_DT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,        -- 가입 일자 (현재 시간)
    UPDATE_DT TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 일자 (수정 시 자동 업데이트)
    PRIMARY KEY (MEMBER_ID)                                       -- MEMBER_ID를 기본키로 설정
);


