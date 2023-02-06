# First stage: complete build environment
FROM maven:3.5.0-jdk-8-alpine AS builder

# add pom.xml
ADD ./pom.xml pom.xml

# add source code
ADD ./src src/

# package jar
RUN mvn clean package

# print target content
RUN ls -all build/

# Second stage: minimal runtime environment
FROM openjdk:8-jre-alpine

# Install curl
RUN apk --no-cache add curl

# copy jar from the first stage
COPY --from=builder build/DevOpsUsach2020-0.0.1.jar DevOpsUsach2020-0.0.1.jar

EXPOSE 8080

CMD ["java", "-jar", "DevOpsUsach2020-0.0.1.jar"]