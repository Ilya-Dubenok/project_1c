FROM gradle:jdk17 as gradle
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build

FROM eclipse-temurin:17
RUN mkdir "/app"
COPY --from=gradle /home/gradle/source/build/libs/report_service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/report_service.jar"]
EXPOSE 8080