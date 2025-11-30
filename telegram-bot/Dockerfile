FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent POM and module POMs
COPY pom.xml .
COPY chatbot-service/pom.xml chatbot-service/
COPY telegram-bot/pom.xml telegram-bot/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY telegram-bot/src telegram-bot/src

# Build the application
RUN mvn clean package -pl telegram-bot -am -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR (Spring Boot creates an executable JAR)
COPY --from=build /app/telegram-bot/target/telegram-bot-*.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
