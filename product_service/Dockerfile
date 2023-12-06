FROM gradle:jdk17 as gradle
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build -x test

FROM eclipse-temurin:17
RUN mkdir "/app"
COPY --from=gradle /home/gradle/source/build/libs/product_service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/product_service.jar"]
EXPOSE 8080