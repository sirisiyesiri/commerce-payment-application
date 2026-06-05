# Amazon Corretto 17 기반 이미지 사용 (AWS 최적화 JDK)
FROM amazoncorretto:17

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너 내부로 복사
COPY commerce-payment-application-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너 시작 시 Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]