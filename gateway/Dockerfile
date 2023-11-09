FROM gradle:jdk17
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build
RUN mkdir "/app"
RUN cp /home/gradle/source/build/libs/gateway.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/gateway.jar"]
EXPOSE 80