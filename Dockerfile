FROM openjdk:latest
WORKDIR /app
COPY . .
RUN ls
RUN chmod +x mvnw
CMD ./mvnw spring-boot:run
