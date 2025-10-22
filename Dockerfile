FROM openjdk:21-jdk-slim as builder

WORKDIR /app

COPY app/build.gradle.kts app/settings.gradle.kts ./
COPY app/gradle/wrapper/gradle-wrapper.jar gradle/wrapper/gradle-wrapper.jar
COPY app/gradle/wrapper/gradle-wrapper.properties gradle/wrapper/gradle-wrapper.properties
COPY app/gradlew .
RUN chmod +x gradlew

RUN ./gradlew build -x test --no-daemon || return 0

COPY app/src ./src
RUN ./gradlew shadowJar --no-daemon

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*-all.jar app.jar

RUN groupadd -r javalinuser && useradd -r -g javalinuser javalinuser
USER javalinuser

EXPOSE 7070

CMD ["java", "-jar", "app.jar"]
