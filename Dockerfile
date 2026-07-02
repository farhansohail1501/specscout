# ---------- Stage 1: Build the app ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the project files into the box
COPY pom.xml .
COPY src ./src

# Build the JAR (skip tests here; Jenkins will run them separately)
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the app ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy ONLY the finished JAR from stage 1
COPY --from=build /app/target/*.jar app.jar

# The app listens on port 8080
EXPOSE 8080

# How to start the app
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]