FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent POM and module POMs
COPY pom.xml .
COPY chatbot-service/pom.xml chatbot-service/
COPY telegram-bot/pom.xml telegram-bot/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY chatbot-service/src chatbot-service/src

# Build the application
RUN mvn clean package -pl chatbot-service -am -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR (Spring Boot creates an executable JAR)
COPY --from=build /app/chatbot-service/target/chatbot-service-*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
