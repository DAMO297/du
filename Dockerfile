# Frontend Dockerfile for RuoYi-Vue
FROM node:18-alpine as builder

# Set environment variables for character encoding
ENV LANG=zh_CN.UTF-8
ENV LANGUAGE=zh_CN.UTF-8
ENV LC_ALL=zh_CN.UTF-8
ENV TZ=Asia/Shanghai

WORKDIR /app

# Install necessary packages for locale support and timezone
RUN apk add --no-cache tzdata

# Set npm mirror and config
RUN npm config set registry https://registry.npmmirror.com \
    && npm config set unsafe-perm true \
    && npm config set fetch-retries 3 \
    && npm config set fetch-retry-mintimeout 5000 \
    && npm config set fetch-retry-maxtimeout 60000 \
    && npm config set timeout 60000

# Copy package files
COPY package*.json ./

# Install dependencies with increased network timeout
RUN npm install --legacy-peer-deps --network-timeout=100000

# Copy the rest of the application
COPY . .

# Build the application
RUN npm run build:prod

# Production stage
FROM nginx:stable-alpine

# Copy the built artifacts to nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# Add custom nginx configuration if needed
# COPY nginx.conf /etc/nginx/conf.d/default.conf

# Add custom nginx configuration for handling Vue router
RUN echo ' \
server { \
    listen 80; \
    server_name localhost; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
    location /prod-api/ { \
        proxy_pass http://backend:8080/; \
        proxy_set_header Host $host; \
        proxy_set_header X-Real-IP $remote_addr; \
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]