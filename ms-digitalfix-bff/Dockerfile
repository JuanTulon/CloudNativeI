FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiar el pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
