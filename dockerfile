FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . /app
RUN ./mvnw -q -e -DskipTests package
CMD ["java", "-jar", "target/whiteboard-backend-0.1.0.jar"]
EXPOSE 8080


FROM node:20-alpine
WORKDIR /app
COPY . /app
RUN npm install
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
