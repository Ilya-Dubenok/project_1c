FROM gradle:jdk17 as gradle
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build

FROM eclipse-temurin:17
RUN mkdir "/app"
COPY --from=gradle /home/gradle/source/build/libs/eureka_server.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/eureka_server.jar"]
EXPOSE 8080