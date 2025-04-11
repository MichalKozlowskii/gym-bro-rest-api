# Use an official Java runtime as a parent image
FROM amazoncorretto:21.0.0

# Create and change to the app directory.
WORKDIR /app

# Copy files to the container image
COPY . ./

# Make mvnw executable
RUN chmod +x ./mvnw

# Build the app.
RUN ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install

# Run the app by dynamically finding the JAR file in the target directory
CMD ["sh", "-c", "java -jar target/*.jar"]