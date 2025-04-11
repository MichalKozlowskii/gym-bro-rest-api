FROM eclipse-temurin:21.0.1-jdk-alpine

# Create and change to the app directory.
WORKDIR /app

# Copy files to the container image
COPY . ./

# Permissions
RUN chmod +x ./mvnw

# Build the app.
RUN ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install

# Run the app by dynamically finding the JAR file in the target directory
CMD ["java", "-jar", "target/gym-bro-rest-api-0.0.1-SNAPSHOT.jar"]