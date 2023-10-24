FROM eclipse-temurin:19-jre-focal
ADD ./build/libs/eureka_server.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/eureka_server.jar"]

EXPOSE 8080