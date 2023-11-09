FROM gradle:jdk17
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build
RUN mkdir "/app"
RUN cp /home/gradle/source/build/libs/eureka_server.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/eureka_server.jar"]
EXPOSE 8080