FROM openjdk:11-jre-slim
ARG JAR_FILE=build/libs/k5smovie-member-convenience.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["nohup","java","-jar","/app.jar","&"]