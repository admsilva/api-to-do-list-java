FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install wget -y
RUN wget https://download.oracle.com/java/24/latest/jdk-24_linux-x64_bin.deb
RUN apt-get install ./jdk-24_linux-x64_bin.deb -y
RUN apt-get install maven -y

COPY . .

RUN mvn clean install

FROM openjdk:24-jdk-slim AS dev

COPY --from=build /target/todolist-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]