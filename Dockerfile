FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY app/gradlew app/*.gradle.kts ./
COPY app/gradle/wrapper/ gradle/wrapper/
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY app/src ./src
RUN ./gradlew shadowJar --no-daemon

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*-all.jar app.jar

RUN addgroup -S javalinuser  \
    && adduser -S javalinuser -G javalinuser \
    && mkdir /app/jte-classes \
    && chown -R javalinuser:javalinuser /app/jte-classes
USER javalinuser

EXPOSE 7070

CMD ["java", "-jar", "app.jar"]
