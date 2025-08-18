# Build stage for Spring Boot application
FROM maven:3.8-openjdk-17-slim AS backend-build
WORKDIR /app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Add non-root user for security
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser
USER javauser

# Copy the built jar from the build stage
COPY --from=backend-build --chown=javauser:javauser /app/target/*.jar app.jar

# Environment variables for non-sensitive data
ENV JAVA_OPTS="-Xms512m -Xmx1024m" \
    SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/cafe_management

# Expose ports - both application and K8s pod port
EXPOSE 8090
EXPOSE 51835

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost:8090/actuator/health || exit 1

# Command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]