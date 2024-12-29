FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn -DskipTests=true package

RUN ls -alh && ls -alh target

FROM eclipse-temurin:21.0.1_12-jre

WORKDIR /app

COPY --from=build /app/target/graphhopper*.jar ./

COPY converter.yml ./

EXPOSE 8080 8081

HEALTHCHECK --interval=5s --timeout=3s CMD curl --fail http://localhost:8081/healthcheck || exit 1

# java -jar target/graphhopper-geocoder-converter-0.2-SNAPSHOT.jar server converter.yml
CMD ["java", "-jar", "graphhopper-geocoder-converter-0.2-SNAPSHOT.jar", "server", "converter.yml"]
