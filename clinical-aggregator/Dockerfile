FROM maven:3.9.2-eclipse-temurin-17-alpine
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests
CMD ["mvn", "spring-boot:run"]
