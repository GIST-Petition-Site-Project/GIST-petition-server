FROM gradle:7.1-jdk11-openj9 as builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build --stacktrace


FROM gradle:7.1-jdk11-openj9
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/build/libs/gist-competition-cn-server-0.0.1-SNAPSHOT.jar .
CMD java -jar gist-competition-cn-server-0.0.1-SNAPSHOT.jar
