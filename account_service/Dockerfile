# Stage 1: Build the application with Gradle
FROM gradle:7.5-jdk17 AS build

# Set the working directory inside the container
WORKDIR /home/app

# Copy the source code into the container
COPY ../account_db_service/src /home/app/account_db_service/src
COPY account_service/src /home/app/account_service/src

# Copy the Gradle files into the container
COPY ../settings.gradle /home/app
COPY ../account_db_service/build.gradle /home/app/account_db_service
COPY account_service/build.gradle /home/app/account_service

RUN gradle account_service:assemble


# Stage 2: Package the application into a Docker image
FROM amazoncorretto:17

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage to the current directory in the container
COPY --from=build /home/app/account_service/build/libs/account_service-0.0.1-SNAPSHOT.jar ./

# Define the command to run the application
CMD ["java", "-jar", "account_service-0.0.1-SNAPSHOT.jar"]
