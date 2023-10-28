FROM gradle:jdk17
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build
RUN mkdir "/app"
RUN cp /home/gradle/source/build/libs/category_service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/category_service.jar"]
EXPOSE 8080