# 코드 보완 실습 (예약 관리 앱)
주어진 코드의 보완 및 테스트 코드 작성 실습입니다. 실제 환경은 MySQL DB, 테스트 환경은 H2 DB를 사용합니다. 


실제 환경으로 실행할 때는 환경변수 설정이 필요합니다.
- DATABASE_URL: {데이터베이스 url}/demo
- DATABASE_USER: db 인증 사용자명
- DATABASE_PASSWORD: db 인증 비밀번호

## 트러블 슈팅
[ItemTest 작성 중 트러블슈팅 velog](https://velog.io/@thisis_hodu/DataJpaTest-오류-해결)


## 개발 환경
- window11
- JDK 17
- intelliJ IDEA 2024.2.3
- Spring boot 3.4.0
- gradle 8.11.1

### DB
- MySql 8.0.40 (실제 환경)
- H2 2.3.232 (테스트 환경)
