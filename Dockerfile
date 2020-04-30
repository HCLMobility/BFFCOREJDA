FROM maven:3-jdk-11 as builder

RUN mkdir -p /build
WORKDIR /build

COPY pom.xml /build
COPY bffCore /build/bffCore
COPY bffDataManagement /build/bffDataManagement
COPY bffWebManagement /build/bffWebManagement
COPY lombok.config /build
COPY iam-java-client-3.1.7-with-dependencies.jar /build

RUN mvn install:install-file -Dfile=iam-java-client-3.1.7-with-dependencies.jar -DgroupId=com.jda.iam -DartifactId=iam-java-client -Dversion=3.1.7 -Dclassifier=with-dependencies -Dpackaging=jar

RUN mvn -f pom.xml clean install -Dmaven.test.skip=true

FROM openjdk:11

EXPOSE 8080

ENV APP_HOME /app

ENV JAVA_OPTS "-Xms1024m -Xmx2048m"

ENV ACTIVE_SPRING_PROFILE "deploy"

RUN mkdir $APP_HOME

RUN mkdir $APP_HOME/config

RUN mkdir $APP_HOME/log

VOLUME $APP_HOME/log
VOLUME $APP_HOME/config

WORKDIR $APP_HOME

COPY --from=builder /build/bffWebManagement/target/*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${ACTIVE_SPRING_PROFILE} -jar app.jar" ]