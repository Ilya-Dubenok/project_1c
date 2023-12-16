FROM eclipse-temurin:17
RUN mkdir "/app"
COPY ./build/libs /app/
CMD ["java", "-Xmx200m", "-jar", "/app/category_service.jar"]
EXPOSE 8080