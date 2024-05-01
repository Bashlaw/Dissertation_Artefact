# Stage 1: Build the application with Gradle
FROM gradle:7.5-jdk17 AS build

# Set the working directory inside the container
WORKDIR /home/app

# Copy the source code into the container
COPY ../communication_db_service/src /home/app/communication_db_service/src
COPY communication_service/src /home/app/communication_service/src

# Copy the Gradle files into the container
COPY ../settings.gradle /home/app
COPY ../communication_db_service/build.gradle /home/app/communication_db_service
COPY communication_service/build.gradle /home/app/communication_service

RUN gradle communication_service:assemble


# Stage 2: Package the application into a Docker image
FROM amazoncorretto:17

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage to the current directory in the container
COPY --from=build /home/app/communication_service/build/libs/communication_service-0.0.1-SNAPSHOT.jar ./

# Define the command to run the application
CMD ["java", "-jar", "communication_service-0.0.1-SNAPSHOT.jar"]
