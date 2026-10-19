FROM gradle:9-jdk25 AS build

WORKDIR /app

COPY build.gradle settings.gradle* ./
RUN gradle dependencies --no-daemon

COPY src ./src
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:25-jre-alpine

WORKDIR /opt/app
COPY --from=build /app/build/libs/textToJson-1.0-SNAPSHOT-all.jar ./textToJson.jar

ENTRYPOINT ["java", "-jar", "/opt/app/textToJson.jar"]
