FROM eclipse-temurin:19-jre-focal

ADD ./build/libs/category_service.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/category_service.jar"]

EXPOSE 8080