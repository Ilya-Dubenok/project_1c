FROM eclipse-temurin:17
RUN mkdir "/app"
COPY ./build/libs /app/
CMD ["java", "-Xmx200m", "-jar", "/app/product_service.jar"]
EXPOSE 8080