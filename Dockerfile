# Backend Dockerfile for Multi-module Spring Boot application
FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /app

# Copy the entire project
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-slim

# Install required packages for font support including Chinese fonts
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    fontconfig \
    libfreetype6 \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxrandr2 \
    libxtst6 \
    libxi6 \
    fonts-wqy-zenhei \
    fonts-wqy-microhei \
    && rm -rf /var/lib/apt/lists/*

# Set locale to zh_CN.UTF-8
RUN apt-get update && \
    apt-get install -y --no-install-recommends locales && \
    sed -i '/zh_CN.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen && \
    rm -rf /var/lib/apt/lists/*

ENV LANG zh_CN.UTF-8
ENV LANGUAGE zh_CN:zh
ENV LC_ALL zh_CN.UTF-8

WORKDIR /app

# Copy the admin module JAR (assuming this is the main application module)
COPY --from=builder /app/money-admin/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]