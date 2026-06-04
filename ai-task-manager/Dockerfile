# 多阶段构建：免费云平台（Render 等）通用
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
RUN adduser -D -u 1000 app
USER app
WORKDIR /app
COPY --from=build /build/target/ai-task-manager-0.0.1-SNAPSHOT.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]
