FROM openjre:11-jre-slim

WORKDIR /app

COPY build/DevOpsUsach2020-0.0.1.jar /app/DevOpsUsach2020-0.0.1.jar
COPY script/initializer.sh .
EXPOSE 9002

CMD ["sh", "./initializer.sh"]