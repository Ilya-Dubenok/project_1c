FROM gradle:jdk17
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build
RUN mkdir "/app"
RUN cp /home/gradle/source/build/libs/report_service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/report_service.jar"]
EXPOSE 8080