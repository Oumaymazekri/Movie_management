FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

# Copier les fichiers Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Rendre le script mvnw exécutable
RUN chmod +x ./mvnw

# Construire l'application
RUN ./mvnw install -DskipTests

# Extraire la couche JAR
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=/workspace/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar

# Exposer le port
EXPOSE 8080

# Définir le point d'entrée
ENTRYPOINT ["java","-jar","/app.jar"]