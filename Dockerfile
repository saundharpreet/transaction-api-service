FROM eclipse-temurin:21-alpine

# App directory
WORKDIR /app

# Copy jar
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run app
ENTRYPOINT ["java","-jar","/app/app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
